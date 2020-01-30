package com.example.wifibluetoothreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.wifibluetoothreminder.CustomDialog.ContentDialog;
import com.example.wifibluetoothreminder.Adapter.ContentsModel;
import com.example.wifibluetoothreminder.Adapter.ContentsModelAdapter;
import com.example.wifibluetoothreminder.SQLite.DbOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Contents extends AppCompatActivity implements ContentsModelAdapter.OnListItemClickInterface, ContentsModelAdapter.OnListItemLongClickInterface, View.OnClickListener{

    private String SSID;
    ArrayList<ContentsModel> item;
    private RecyclerView recyclerView;
    private ContentsModelAdapter contentsModelAdapter;
    private DbOpenHelper mDbOpenHelper;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        UI();
    }

    public void UI(){ // UI여기서 작업
        getData();

        item = new ArrayList<>();

        Select_SSID_Contents();
        recyclerView = findViewById(R.id.ContentList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        contentsModelAdapter = new ContentsModelAdapter(item,Contents.this,Contents.this);
        recyclerView.setAdapter(contentsModelAdapter);

        floatingActionButton = findViewById(R.id.Add);
        floatingActionButton.setOnClickListener(this);
    }

    public void Select_SSID_Contents(){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        item.clear();
        Cursor cursor = mDbOpenHelper.selectColumns2();
        while(cursor.moveToNext()){
            item.add(new ContentsModel(cursor.getString(0),cursor.getString(1), cursor.getString(2))); //임시지정
        }
        cursor.close();
        mDbOpenHelper.close();
    }

    public void InsertContent(String ssid, String content){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        mDbOpenHelper.insertColumn2(ssid, content);
        mDbOpenHelper.close();
    }

    public void DeleteContent(String ssid, String content){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        mDbOpenHelper.deletePlanTableCloumn(ssid, content);
        mDbOpenHelper.close();
    }

    public void UpdateContent(String _id, String content){
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        mDbOpenHelper.updatePlanTableColumn(_id, content);
        mDbOpenHelper.close();
    }

    public void getData(){
        Intent intent = getIntent();
        SSID = intent.getStringExtra("SSID");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent("Activity-Activity");
        LocalBroadcastManager.getInstance(Contents.this).sendBroadcast(intent);
        super.onBackPressed();
    }

    // 여기서
    @Override
    public void onItemLongClick(View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.RIGHT);
        getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.edit: // 편집
                        ContentDialog dialog = new ContentDialog(Contents.this);
                        dialog.setDialogListener(new ContentDialog.CustomDialogListener() {
                            @Override
                            public void PositiveClick(String Contents) {
                                UpdateContent(item.get(position).get_ID(), Contents);
                                Select_SSID_Contents();
                                contentsModelAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void NegativeClick() {

                            }
                        });
                        dialog.show();
                        break;
                    case R.id.del: //삭제
                        //TODO : 딜리트문 실행
                        DeleteContent(item.get(position).getSSID(), item.get(position).getContents());
                        Select_SSID_Contents();
                        contentsModelAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onItemClick(View v, int position) {
        ContentsModelAdapter.CustomViewHoler customViewHoler = (ContentsModelAdapter.CustomViewHoler)recyclerView.findViewHolderForAdapterPosition(position);
    }
    // 여기까지 리스트 클릭이벤트

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Add:
                ContentDialog dialog = new ContentDialog(Contents.this);
                dialog.setDialogListener(new ContentDialog.CustomDialogListener() {
                    @Override
                    public void PositiveClick(String Contents) {
                        InsertContent(SSID, Contents);
                        Select_SSID_Contents();
                        contentsModelAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void NegativeClick() {

                    }
                });
                dialog.show();
                break;
        }
    }
}
