package com.example.wifibluetoothreminder.Service;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;



public class BluetoothService extends AppCompatActivity {

    //BluetoothAdapter
    BluetoothAdapter mBluetoothAdapter;

    //블루투스 요청 액티비티 코드
    final static int BLUETOOTH_REQUEST_CODE = 100;

    int selectDevice;

    public BluetoothService(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //선택한 디바이스 없음
        selectDevice = -1;

        //블루투스 지원 유무 확인
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //블루투스를 지원하지 않으면 null을 리턴한다
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 단말기 입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //블루투스 브로드캐스트 리시버 등록
        //리시버
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        registerReceiver(mBluetoothStateReceiver, stateFilter);
    }

    //여기서 장치 이름얻음(예를 들어 갤럭시~)
    public String getLocalBluetoothName() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        String name = mBluetoothAdapter.getName();
        if (name == null) {
            System.out.println("Name is null!");
            name = mBluetoothAdapter.getAddress();
        }
        return name;
    }


    //블루투스 상태변화 BroadcastReceiver
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //BluetoothAdapter.EXTRA_STATE : 블루투스의 현재상태 변화
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            String action = intent.getAction();
            //연결된 장치를 intent를 통하여 가져온다.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //장치가 연결이 되었으면
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d("TEST", device.getName().toString() + " Device Is Connected!");
            }
            //장치의 연결이 끊기면
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.d("TEST", device.getName().toString() + " Device Is DISConnected!");
            }
        }
    };
}

