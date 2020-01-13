package com.example.wifibluetoothreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.wifibluetoothreminder.CheckingService.RunningService;
import com.example.wifibluetoothreminder.RecyclerView.MainListModel;
import com.example.wifibluetoothreminder.RecyclerView.MainRecyclerViewAdapter;
import com.example.wifibluetoothreminder.SQLite.DbOpenHelper;
import com.example.wifibluetoothreminder.Service.BluetoothWifiService;
import com.example.wifibluetoothreminder.Service.UnCatchTaskService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdapter.OnListItemLongClickInterface, MainRecyclerViewAdapter.OnListItemClickInterface{

    RunningService runningService;

    // 변경되지 않을 상수
    private static final int REQUEST_ACCESS_LOCATION = 1000;

    private ArrayList<MainListModel> list;
    private RecyclerView recyclerView;
    private MainRecyclerViewAdapter mainRecyclerViewAdapter;

    private Handler mHandler; // MainThread 아닌곳에서 UI 작업할 수 없기 때문에 핸들러 제작

    private SQLiteDatabase sqLiteDatabase;


    private DbOpenHelper mDbOpenHelper;



    @Override
    protected void onStart() {
        stopService(new Intent(MainActivity.this, BluetoothWifiService.class));
        /*
        TODO : 비정상종료(최근실행 앱에서 종료시 발생)
         */
        runningService = new RunningService(this);
        //TODO : java-lang-illegalstateexception-not-allowed-to-start-service-inten 에러 가끔 발생 이유 모름.

        if (!runningService.isRunning("com.example.wifibluetoothreminder.Service.UnCatchTaskService")) {
            startService(new Intent(this, UnCatchTaskService.class));
        }

        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbOpenHelper = new DbOpenHelper(this);

        mDbOpenHelper.open();

        mDbOpenHelper.create();
        UI();
        checkpermmission();
    }

    public void UI(){ //UI들 여기 작성

        list = new ArrayList<>();
        //TODO : recyclerView Init
        recyclerView = findViewById(R.id.WIFINameList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(list, MainActivity.this, MainActivity.this);
        recyclerView.setAdapter(mainRecyclerViewAdapter);

        mHandler = new Handler();
    }

    public void checkpermmission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int ACCESS_FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if ( ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED || ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_ACCESS_LOCATION);
            else if (ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED && ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED) {
                WifiStatus();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
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
        }else{
            WifiStatus();
        }
    }

    public void WifiStatus(){
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        CM.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                SearchSSID_createDialog();
            }
        });

    }

    public void SearchSSID_createDialog(){ // 발견시 추가다이얼로그?

        WifiManager WM = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = WM.getConnectionInfo();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("새로운 기기 감지").setMessage(wifiInfo.getSSID());
        builder.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO : 리사이클러뷰 추가
                list.add(new MainListModel(wifiInfo.getSSID(), "0"));
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(wifiInfo.getSSID(), wifiInfo.getSSID());
                Cursor cursor = mDbOpenHelper.selectColumns();
                while(cursor.moveToNext()){
                    String Nick = cursor.getString(1);
                    StartLog("DB : ", Nick);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(1000);

                    }
                }).start();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    public void AutoService(){
        runningService = new RunningService(this);
        if(!runningService.isRunning("com.example.wifibluetoothreminder.Service.BluetoothWifiService")) { // 중복실행 막기위해서 사용
            Intent intent = new Intent(MainActivity.this, BluetoothWifiService.class);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }
            else {
                startService(intent);
            }
        }
    }

    public void StartLog(String TAG, String msg){ // 빨간색로그
        Log.e(TAG, msg);
    }

    public void StartToast(String msg){ // 토스트
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        AutoService();
        super.onDestroy();
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, v , Gravity.RIGHT);
        getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());
        MainRecyclerViewAdapter.CoustomViewHolder viewHolder = (MainRecyclerViewAdapter.CoustomViewHolder)recyclerView.findViewHolderForAdapterPosition(position); // 선택아이템 포지션 알아옴
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.edit: // 편집
                        //TODO : 업데이트문 실행
                        /**
                         * viewHolder.NickName.getText().toString() 하면 NickName값 알아옴 이걸 이용
                         */
                        MainListModel mainListModel = new MainListModel("",""); // 이부분에서 바꿀내용 지정
                        list.set(position, mainListModel);
                        mainRecyclerViewAdapter.notifyItemChanged(position);
                        break;
                    case R.id.del: //삭제
                        //TODO : 딜리트문 실행
                        list.remove(position);
                        mainRecyclerViewAdapter.notifyItemRemoved(position);
                        mainRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
        StartToast(viewHolder.NickName.getText().toString());
    }

    @Override
    public void onItemClick(View v, int position) {
        MainRecyclerViewAdapter.CoustomViewHolder viewHolder = (MainRecyclerViewAdapter.CoustomViewHolder)recyclerView.findViewHolderForAdapterPosition(position);
        StartToast(viewHolder.NickName.getText().toString());
    }

    public class Handler extends android.os.Handler{ // TODO : 여기서 UI 작업
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1000){
                mainRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
