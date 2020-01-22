package com.example.wifibluetoothreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.wifibluetoothreminder.CustomDialog.NickNameDialog;
import com.example.wifibluetoothreminder.RecyclerView.MainListModel;
import com.example.wifibluetoothreminder.RecyclerView.MainRecyclerViewAdapter;
import com.example.wifibluetoothreminder.SQLite.DbOpenHelper;
import com.example.wifibluetoothreminder.Service.BluetoothWifiService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdapter.OnListItemLongClickInterface, MainRecyclerViewAdapter.OnListItemClickInterface {


    // 변경되지 않을 상수
    private static final int REQUEST_ACCESS_LOCATION = 1000;

    private ArrayList<MainListModel> list;
    private ArrayList<String> itemCount;
    private RecyclerView recyclerView;
    private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private Intent foregroundIntent = null;

    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkpermmission();
        AutoService();
        UI();
        notiDataCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
       LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Service-Activity"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Activity-Activity"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Receive : ", "Active");
            if ("Service-Activity".equals(intent.getAction())) {
                String Detect_Type = intent.getStringExtra("DeviceType");
                String SSID = intent.getStringExtra("SSID");
                if (Detect_Type != null && SSID != null)
                    setFirstDetectDialog(Detect_Type, SSID);
            }else if ("Activity-Activity".equals(intent.getAction())){
                SelectAllDB();
            }
        }
    };

    public void UI() { //UI들 여기 작성

        list = new ArrayList<>();
        itemCount = new ArrayList<>();
        //TODO : recyclerView Init
        recyclerView = findViewById(R.id.WIFINameList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        SelectAllDB();

        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(list, MainActivity.this, MainActivity.this);
        recyclerView.setAdapter(mainRecyclerViewAdapter);

    }

    public void notiDataCheck() { // 노티로 접근시 작동
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            StartLog("A", "ACTION");
            String Detect_Type = extra.getString("DetectType");
            String SSID = extra.getString("SSID");
            if (Detect_Type != null && SSID != null)
                setFirstDetectDialog(Detect_Type, SSID);
        }
    }

    public void setFirstDetectDialog(final String Detect_Type, final String ssid) {
        NickNameDialog nickNameDialog = new NickNameDialog(this);
        nickNameDialog.setCancelable(false); // 버튼이외로 취소불가능
        nickNameDialog.setDialogListener(new NickNameDialog.CustomDialogListener() {
            @Override
            public void PositiveClick(String NickName) {
                if (NickName.length() > 0) {
                    InsertDB(Detect_Type,ssid,NickName);
                    SelectAllDB();
                    mainRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    InsertDB(Detect_Type,ssid,ssid);
                    SelectAllDB();
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

    public void SelectAllDB() {
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        list.clear();
        Cursor cursor = mDbOpenHelper.selectColumns();
        while (cursor.moveToNext()) {
            list.add(new MainListModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), String.valueOf(SelectContentsCount(cursor.getString(2)))));
        }
        cursor.close();
        mDbOpenHelper.close();
    }

    public void InsertDB(String device_type, String ssid, String nickname){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        mDbOpenHelper.insertColumn(device_type,ssid,nickname);
        mDbOpenHelper.close();
    }

    public int SelectContentsCount(String ssid){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        itemCount.clear();
        Cursor cursor = mDbOpenHelper.selectSSIDColumns(ssid);
        while(cursor.moveToNext()){
            itemCount.add(cursor.getString(1));
        }
        mDbOpenHelper.close();
        return itemCount.size();
    }

    public void DeleteDB(String ssid){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        mDbOpenHelper.deleteUserTableColumn(ssid);
        mDbOpenHelper.close();
    }

    public void UpdateUserTable(String ssid, String nickname){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        mDbOpenHelper.updateUserTableColumn(ssid,nickname);
        mDbOpenHelper.close();
    }

    public void checkpermmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ACCESS_FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED || ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOCATION);
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

        if (BluetoothWifiService.serviceIntent == null) {
            foregroundIntent = new Intent(this, BluetoothWifiService.class);
            startService(foregroundIntent);
            StartToast("service_start");
        } else {
            foregroundIntent = BluetoothWifiService.serviceIntent;
            StartToast("service_already");
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

        if (foregroundIntent != null) {
            stopService(foregroundIntent);
            foregroundIntent = null;
        }
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
                                UpdateUserTable(list.get(position).getSSID(), NickName);
                                SelectAllDB();
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
                        StartToast(list.get(position).getSSID());
                        DeleteDB(list.get(position).getSSID());
                        SelectAllDB();
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
        MainRecyclerViewAdapter.CoustomViewHolder viewHolder = (MainRecyclerViewAdapter.CoustomViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        Intent intent = new Intent(MainActivity.this, Contents.class);
        intent.putExtra("SSID", list.get(position).getSSID());
        startActivity(intent);
    }

    public class Handler extends android.os.Handler { // TODO : 여기서 UI 작업
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1000) {
                mainRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
