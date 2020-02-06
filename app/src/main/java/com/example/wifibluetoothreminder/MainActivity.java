package com.example.wifibluetoothreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.wifibluetoothreminder.CustomDialog.NickNameDialog;
import com.example.wifibluetoothreminder.Adapter.MainRecyclerViewAdapter;
import com.example.wifibluetoothreminder.Room.ContentList;
import com.example.wifibluetoothreminder.Room.ContentListDao;
import com.example.wifibluetoothreminder.Room.ListDatabase;
import com.example.wifibluetoothreminder.Room.WifiBluetoothList;
import com.example.wifibluetoothreminder.Room.WifiBluetoothListDao;
import com.example.wifibluetoothreminder.RunningCheck.ServiceRunningCheck;
import com.example.wifibluetoothreminder.Service.BluetoothWifiService;
import com.example.wifibluetoothreminder.ViewModel.ContentListViewModel;
import com.example.wifibluetoothreminder.ViewModel.WifiBluetoothListViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdapter.OnListItemLongClickInterface, MainRecyclerViewAdapter.OnListItemClickInterface {

    // 변경되지 않을 상수
    private static final int REQUEST_ACCESS_LOCATION = 1000;

    public List<WifiBluetoothList> list;
    public RecyclerView recyclerView;
    public MainRecyclerViewAdapter mainRecyclerViewAdapter;

    private WifiBluetoothListViewModel wifiBluetoothListViewModel;

    ServiceRunningCheck serviceRunningCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UI();
        checkpermmission();

        wifiBluetoothListViewModel = new ViewModelProvider(this).get(WifiBluetoothListViewModel.class);
        wifiBluetoothListViewModel.getAllData().observe(this, new Observer<List<WifiBluetoothList>>() {
            @Override
            public void onChanged(List<WifiBluetoothList> wifiBluetoothLists) {
                list.clear();
                if (wifiBluetoothLists.size() != 0){
                    for(WifiBluetoothList wifiBluetoothList : wifiBluetoothLists)
                        list.add(wifiBluetoothList);
                }
                mainRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StartLog("onResume", "onResume");
        Runnable update = new Runnable() {
            @Override
            public void run() {
                NonLeakHandler nonLeakHandler = new NonLeakHandler(MainActivity.this, mainRecyclerViewAdapter);
                ListDatabase listDatabase = ListDatabase.getDatabase(MainActivity.this);
                ContentListDao dao = listDatabase.contentListDao();
                WifiBluetoothListDao wifiBluetoothListDao = listDatabase.wifiBluetoothListDao();
                List<WifiBluetoothList> lists = wifiBluetoothListDao.getAll_Service(); // Content에서 뒤로가기 시에는 list에 값이 존재하지만, 최초실행시는 존재하지 않기 때문에 값을 가져옴.
                for (int i = 0; i<lists.size(); i++) {
                    List<ContentList> item = dao.getItem(lists.get(i).getSSID());
                    wifiBluetoothListViewModel.updateCount(lists.get(i).getSSID(), item.size());
                }
                nonLeakHandler.sendEmptyMessage(100);
            }
        };
        Thread thread = new Thread(update);
        thread.start();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Service-Activity"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("Service-Activity".equals(intent.getAction())) {
                String Detect_Type = intent.getStringExtra("DeviceType");
                String SSID = intent.getStringExtra("SSID");
                if (Detect_Type != null && SSID != null)
                    setFirstDetectDialog(Detect_Type, SSID);
            }
        }
    };

    public void UI() { //UI들 여기 작성

        list = new ArrayList<>();
        serviceRunningCheck = new ServiceRunningCheck(this);
        //TODO : recyclerView Init
        recyclerView = findViewById(R.id.WIFINameList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(list, MainActivity.this, MainActivity.this);
        recyclerView.setAdapter(mainRecyclerViewAdapter);

    }

    public void setFirstDetectDialog(final String Detect_Type, final String ssid) {
        NickNameDialog nickNameDialog = new NickNameDialog(this);
        nickNameDialog.setCancelable(false); // 버튼이외로 취소불가능
        nickNameDialog.setDialogListener(new NickNameDialog.CustomDialogListener() {
            @Override
            public void PositiveClick(String NickName) {
                if (NickName.length() > 0) {
                    wifiBluetoothListViewModel.insert(new WifiBluetoothList(Detect_Type, ssid, NickName, 0));
                    mainRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    wifiBluetoothListViewModel.insert(new WifiBluetoothList(Detect_Type, ssid, ssid, 0));
                    mainRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void NegativeClick() {

            }
        });
        if(!nickNameDialog.isShowing())
            nickNameDialog.show();
    }

    public void checkpermmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ACCESS_FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED || ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOCATION);
            else if(ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED && ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED)
                AutoService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("권한").setMessage("WIFI의 정보를 받아오기 위해서 필요합니다. 정말 거부하시겠습니까?");
            builder.setPositiveButton("승인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    checkpermmission();
                }
            });
            builder.setNegativeButton("거부", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    AlertDialog.Builder no = new AlertDialog.Builder(MainActivity.this);
                    no.setMessage("해당기능을 사용하기 위해서는 설정 -> App 및 알림 -> App 선택 -> 권한 -> 허용을 해주세요").show();
                }
            });
            builder.show();
        }
    }

    public void AutoService() {
        // TODO : 출처 : https://forest71.tistory.com/185
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        if (!serviceRunningCheck.RunningCheck("com.example.wifibluetoothreminder.Service.BluetoothWifiService")){
            startService(new Intent(MainActivity.this, BluetoothWifiService.class));
            StartLog("MainActivity : ", "A");
        }else{
            stopService(new Intent(MainActivity.this, BluetoothWifiService.class));
            startService(new Intent(MainActivity.this, BluetoothWifiService.class));
        }


    }


    public void StartLog(String TAG, String msg) { // 빨간색로그
        Log.e(TAG, msg);
    }

    public void StartToast(String msg) { // 토스트
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(MainActivity.this, BluetoothWifiService.class));
    }


    // TODO : 리사이클러뷰 클릭 이벤트
    @Override
    public void onItemLongClick(View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.RIGHT);
        getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.edit: // 편집
                        NickNameDialog mainListDialog = new NickNameDialog(MainActivity.this);
                        mainListDialog.setCancelable(false);
                        mainListDialog.setDialogListener(new NickNameDialog.CustomDialogListener() {
                            @Override
                            public void PositiveClick(String NickName) {
                                //TODO : 업데이트문 실행
                                wifiBluetoothListViewModel.update(list.get(position).getSSID(), NickName);
                                mainRecyclerViewAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void NegativeClick() {

                            }
                        });
                        mainListDialog.show();
                        break;
                    case R.id.del: //삭제
                        //TODO : 딜리트문 실행
                        wifiBluetoothListViewModel.delete(list.get(position).getSSID());
                        Runnable del = new Runnable() {
                            @Override
                            public void run() {
                                ListDatabase listDatabase = ListDatabase.getDatabase(MainActivity.this);
                                ContentListDao contentDao = listDatabase.contentListDao();
                                contentDao.Delete_All(list.get(position).getSSID());
                            }
                        };
                        Thread thread = new Thread(del);
                        thread.start();
                        mainRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(MainActivity.this, Contents.class);
        intent.putExtra("SSID", list.get(position).getSSID());
        startActivity(intent);
    }

    private static final class NonLeakHandler extends Handler{

        private final WeakReference<MainActivity> ref;
        private MainRecyclerViewAdapter mainRecyclerViewAdapter;

        public NonLeakHandler(MainActivity act, MainRecyclerViewAdapter adapter){
            ref = new WeakReference<>(act);
            mainRecyclerViewAdapter = adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity act = ref.get();
            if (act != null){
                switch (msg.what){
                    case 100:
                        mainRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
