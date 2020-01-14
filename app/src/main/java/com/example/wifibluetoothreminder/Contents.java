package com.example.wifibluetoothreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wifibluetoothreminder.RecyclerView.ContentsModel;
import com.example.wifibluetoothreminder.RecyclerView.ContentsModelAdapter;
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

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        item = new ArrayList<>();

        recyclerView = findViewById(R.id.ContentList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        Cursor cursor = mDbOpenHelper.selectColumns2();
        while(cursor.moveToNext()){
            item.add(new ContentsModel(cursor.getString(1))); //임시지정
        }

        item.add(new ContentsModel("TEST"));

        contentsModelAdapter = new ContentsModelAdapter(item,Contents.this,Contents.this);
        recyclerView.setAdapter(contentsModelAdapter);

        floatingActionButton = findViewById(R.id.Add);
        floatingActionButton.setOnClickListener(this);
    }

    public void getData(){
        Intent intent = getIntent();
        SSID = intent.getStringExtra("SSID");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Contents.this, MainActivity.class));
        super.onBackPressed();
    }

    // 여기서
    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onItemClick(View v, int position) {

    }
    // 여기까지 리스트 클릭이벤트

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Add:

                break;
        }
    }
}
