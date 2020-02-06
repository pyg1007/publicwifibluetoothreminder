package com.example.wifibluetoothreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.wifibluetoothreminder.CustomDialog.ContentDialog;
import com.example.wifibluetoothreminder.Adapter.ContentsModelAdapter;
import com.example.wifibluetoothreminder.Room.ContentList;
import com.example.wifibluetoothreminder.ViewModel.ContentListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Contents extends AppCompatActivity implements ContentsModelAdapter.OnListItemClickInterface, ContentsModelAdapter.OnListItemLongClickInterface, View.OnClickListener{

    private String SSID;
    private List<ContentList> item;
    private RecyclerView recyclerView;
    private ContentsModelAdapter contentsModelAdapter;

    private ContentListViewModel contentListViewModel;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        UI();

        contentListViewModel = new ViewModelProvider(this).get(ContentListViewModel.class);
        contentListViewModel.getAll().observe(this, new Observer<List<ContentList>>() {
            @Override
            public void onChanged(List<ContentList> contentLists) {
                item.clear();
                if (contentLists.size() != 0){
                    for(ContentList contentList : contentLists)
                        item.add(contentList);
                }
                for (int i = 0; i<item.size(); i++) {
                    Log.e("TAG :", String.valueOf(item.get(i).ID) + " : " + item.get(i).Content + " : " + item.get(i).Content_SSID);
                }
                contentsModelAdapter.notifyDataSetChanged();
            }
        });
    }

    public void UI(){ // UI여기서 작업
        getData();

        item = new ArrayList<>();

        recyclerView = findViewById(R.id.ContentList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

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
                                contentListViewModel.Update(item.get(position).ID, Contents);
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
                        contentListViewModel.Delete(item.get(position).ID, item.get(position).getContent());
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
                        contentListViewModel.Insert(new ContentList(SSID, Contents));
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
