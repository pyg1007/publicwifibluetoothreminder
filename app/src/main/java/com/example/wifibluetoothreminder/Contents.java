package com.example.wifibluetoothreminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.wifibluetoothreminder.CustomDialog.ContentDialog;
import com.example.wifibluetoothreminder.Adapter.ContentsModelAdapter;
import com.example.wifibluetoothreminder.Room.ContentList;
import com.example.wifibluetoothreminder.RunningCheck.ServiceRunningCheck;
import com.example.wifibluetoothreminder.Service.BluetoothWifiService;
import com.example.wifibluetoothreminder.ViewModel.ContentListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Contents extends AppCompatActivity implements ContentsModelAdapter.OnListItemClickInterface, ContentsModelAdapter.OnListItemLongClickInterface{

    private String Mac, SSID, NickName;
    private List<ContentList> items;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ContentsModelAdapter contentsModelAdapter;
    private int Clickcount = 0;

    private ServiceRunningCheck serviceRunningCheck;
    private ContentListViewModel contentListViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        UI();

        contentListViewModel = new ViewModelProvider(this).get(ContentListViewModel.class);
        contentListViewModel.getAll().observe(this, new Observer<List<ContentList>>() {
            @Override
            public void onChanged(List<ContentList> contentLists) {
                items.clear();
                if (contentLists.size() != 0) {
                    for (ContentList contentList : contentLists)
                        if (contentList.getMac().equals(Mac))
                            items.add(contentList);
                }
                contentsModelAdapter.notifyDataSetChanged();
            }
        });
    }

    public void UI() { // UI여기서 작업
        getData();

        items = new ArrayList<>();

        serviceRunningCheck = new ServiceRunningCheck(this);
        recyclerView = findViewById(R.id.ContentList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        contentsModelAdapter = new ContentsModelAdapter(items, Contents.this, Contents.this);
        recyclerView.setAdapter(contentsModelAdapter);

        toolbar = findViewById(R.id.ContentsToolbar);
        toolbar.setTitle(NickName + "의 일정");
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(HomeKeyPress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(HomeKeyPress, intentFilter);
    }

    private BroadcastReceiver HomeKeyPress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())){
                if ("homekey".equals(intent.getStringExtra("reason"))){
                    if (serviceRunningCheck.RunningCheck("com.example.wifibluetoothreminder.Service.BluetoothWifiService"))
                        stopService(new Intent(Contents.this, BluetoothWifiService.class));
                    finish();
                }else if ("recentapps".equals(intent.getStringExtra("reason"))){
                    Log.e("TAG : ", "homekeyLongClick");
                }
            }
        }
    };

    public void getData() {
        Intent intent = getIntent();
        Mac = intent.getStringExtra("Mac");
        SSID = intent.getStringExtra("SSID");
        NickName = intent.getStringExtra("NickName");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Mac", Mac);
        intent.putExtra("SIZE", items.size());
        setResult(200, intent);
        super.onBackPressed();
    }

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
                                contentListViewModel.Update(items.get(position).ID, Contents);
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
                        contentListViewModel.Delete(items.get(position).ID, items.get(position).getContent());
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
        ContentsModelAdapter.CustomViewHolder customViewHoler = (ContentsModelAdapter.CustomViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contenttoolbar_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Add:
                ContentDialog dialog = new ContentDialog(Contents.this);
                dialog.setDialogListener(new ContentDialog.CustomDialogListener() {
                    @Override
                    public void PositiveClick(String Contents) {
                        contentListViewModel.Insert(new ContentList(Mac, SSID, Contents));
                        contentsModelAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void NegativeClick() {

                    }
                });
                dialog.show();
                return true;
            case R.id.Select:
                Clickcount++;
                if (Clickcount % 2 == 1){
                    item.setIcon(R.drawable.ic_delete_24dp);
                    contentsModelAdapter.setCount(Clickcount);
                    contentsModelAdapter.notifyDataSetChanged();
                }else{
                    if (contentsModelAdapter.getCheckedlist().size() != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(contentsModelAdapter.getCheckedlist().size() + "개의 일정을 삭제 하시겠습니까?");
                        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contentsModelAdapter.setCount(Clickcount);
                                List<ContentList> list = contentsModelAdapter.getCheckedlist();
                                for (int j = 0; j < list.size(); j++) {
                                    contentListViewModel.Delete(list.get(j).ID, list.get(j).getContent());
                                }
                                item.setIcon(R.drawable.ic_check_24dp);
                                contentsModelAdapter.notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contentsModelAdapter.setCount(Clickcount);
                                item.setIcon(R.drawable.ic_check_24dp);
                                contentsModelAdapter.notifyDataSetChanged();
                            }
                        });
                        builder.show();
                    }else{
                        contentsModelAdapter.setCount(Clickcount);
                        item.setIcon(R.drawable.ic_check_24dp);
                        contentsModelAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("Mac", Mac);
                intent.putExtra("SIZE", items.size());
                setResult(200, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
