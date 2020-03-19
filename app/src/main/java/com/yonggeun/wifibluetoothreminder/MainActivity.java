package com.yonggeun.wifibluetoothreminder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.yonggeun.wifibluetoothreminder.Adapter.MainRecyclerViewAdapter;
import com.yonggeun.wifibluetoothreminder.CustomDialog.Main_Content_Enrollment_Dialog;
import com.yonggeun.wifibluetoothreminder.CustomDialog.NickNameDialog;
import com.yonggeun.wifibluetoothreminder.CustomDialog.NickNameEditDialog;
import com.yonggeun.wifibluetoothreminder.Room.ContentList;
import com.yonggeun.wifibluetoothreminder.Room.ContentListDao;
import com.yonggeun.wifibluetoothreminder.Room.ListDatabase;
import com.yonggeun.wifibluetoothreminder.Room.WifiBluetoothList;
import com.yonggeun.wifibluetoothreminder.Room.WifiBluetoothListDao;
import com.yonggeun.wifibluetoothreminder.Service.BluetoothWifiService;
import com.yonggeun.wifibluetoothreminder.ViewModel.ContentListViewModel;
import com.yonggeun.wifibluetoothreminder.ViewModel.WifiBluetoothListViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdapter.OnListItemLongClickInterface, MainRecyclerViewAdapter.OnListItemClickInterface {

    // google Admob관련
    private AdView adView;

    public List<WifiBluetoothList> list;
    public RecyclerView recyclerView;
    public MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private TextView MainText;

    private WifiBluetoothListViewModel wifiBluetoothListViewModel;
    private ContentListViewModel contentListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UI();

        wifiBluetoothListViewModel = new ViewModelProvider(this).get(WifiBluetoothListViewModel.class);
        contentListViewModel = new ViewModelProvider(this).get(ContentListViewModel.class);
        wifiBluetoothListViewModel.getAllData().observe(this, new Observer<List<WifiBluetoothList>>() {
            @Override
            public void onChanged(List<WifiBluetoothList> wifiBluetoothLists) {
                list.clear();
                if (wifiBluetoothLists.size() != 0) {
                    MainText.setVisibility(View.GONE);
                    list.addAll(wifiBluetoothLists);
                } else
                    MainText.setVisibility(View.VISIBLE);
                mainRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        RecyclerViewlist_init();
    }

    private BroadcastReceiver HomeKeyPress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                if ("homekey".equals(intent.getStringExtra("reason"))) {
                    if (BluetoothWifiService.ServiceIntent != null)
                        stopService(new Intent(MainActivity.this, BluetoothWifiService.class));
                    finish();
                } else if ("recentapps".equals(intent.getStringExtra("reason"))) {

                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        unregisterReceiver(HomeKeyPress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AutoService();
        IntentFilter intentFilter = new IntentFilter("Service_to_Activity");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(HomeKeyPress, filter);
    }


    public void UI() { //UI들 여기 작성

        list = new ArrayList<>();

        recyclerView = findViewById(R.id.WIFINameList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(list, MainActivity.this, MainActivity.this);
        recyclerView.setAdapter(mainRecyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DecorationItem(4));

        Toolbar toolbar = findViewById(R.id.MainToolbar);
        toolbar.setTitle(R.string.MainToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        MainText = findViewById(R.id.Center_Text);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.Main_AdMob);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        AdmobTest();
    }

    public void AdmobTest() {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e("onAdClosed", "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e("onAdFailedToLoad", "onAdFailedToLoad" + i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.e("onAdLeftApplication", "onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e("onAdOpened", "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e("onAdLoaded", "onAdLoaded");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e("onAdClicked", "onAdClicked");
            }
        });
    }

    public void RecyclerViewlist_init() {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                ListDatabase listDatabase = ListDatabase.getDatabase(MainActivity.this);
                ContentListDao dao = listDatabase.contentListDao();
                WifiBluetoothListDao wifiBluetoothListDao = listDatabase.wifiBluetoothListDao();
                List<WifiBluetoothList> lists = wifiBluetoothListDao.getAll_Service(); // Content에서 뒤로가기 시에는 list에 값이 존재하지만, 최초실행시는 존재하지 않기 때문에 값을 가져옴.
                for (int i = 0; i < lists.size(); i++) {
                    List<ContentList> item = dao.getItem(lists.get(i).getMac());
                    if (item.size() != 0)
                        wifiBluetoothListViewModel.updateCount(lists.get(i).getMac(), item.size());
                    else
                        wifiBluetoothListViewModel.updateCount(lists.get(i).getMac(), 0);
                }
                handler.sendEmptyMessage(100);
            }
        };
        Thread thread = new Thread(update);
        thread.start();
    }

    public void setFirstDetectDialog(final String Detect_Type, final String mac, final String ssid) {
        NickNameDialog nickNameDialog = new NickNameDialog(this);
        nickNameDialog.setCancelable(false); // 버튼이외로 취소불가능
        nickNameDialog.setDialogListener(new NickNameDialog.CustomDialogListener() {
            @Override
            public void PositiveClick(String NickName) {
                if (NickName.length() > 0) {
                    wifiBluetoothListViewModel.insert(new WifiBluetoothList(Detect_Type, mac, ssid, NickName, 0));
                    mainRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    wifiBluetoothListViewModel.insert(new WifiBluetoothList(Detect_Type, mac, ssid, ssid, 0));
                    mainRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void NegativeClick() {

            }
        });
        nickNameDialog.show();
        CustomDialog_Resize(nickNameDialog, 0.9f, 0.3f);
    }

    public void AutoService() {
        if (BluetoothWifiService.ServiceIntent == null) {
            Intent intent = new Intent(MainActivity.this, BluetoothWifiService.class);
            startService(intent);
        } else {
            Intent intent = new Intent("Activity_to_Service");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("Service_to_Activity".equals(intent.getAction())) {
                try {
                    String DeviceType = intent.getStringExtra("DeviceType");
                    String Mac = intent.getStringExtra("Mac");
                    String SSID = intent.getStringExtra("SSID");
                    if (DeviceType != null && Mac != null && SSID != null)
                        setFirstDetectDialog(DeviceType, Mac, SSID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BluetoothWifiService.ServiceIntent != null)
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
                        NickNameEditDialog nickNameEditDialog = new NickNameEditDialog(MainActivity.this);
                        nickNameEditDialog.setCancelable(false);
                        nickNameEditDialog.setDialogListener(new NickNameEditDialog.CustomDialogListener() {
                            @Override
                            public void PositiveClick(String NickName) {
                                if (NickName.length() > 0) {
                                    wifiBluetoothListViewModel.update(list.get(position).getMac(), NickName);
                                    mainRecyclerViewAdapter.notifyDataSetChanged();
                                } else {
                                    wifiBluetoothListViewModel.update(list.get(position).getMac(), list.get(position).getSSID());
                                    mainRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void NegativeClick() {

                            }
                        });
                        nickNameEditDialog.show();
                        nickNameEditDialog.setCancelable(false);
                        CustomDialog_Resize(nickNameEditDialog, 0.9f, 0.3f);
                        break;
                    case R.id.del: //삭제
                        //TODO : 딜리트문 실행
                        wifiBluetoothListViewModel.delete(list.get(position).getMac());
                        contentListViewModel.DeleteAll(list.get(position).getMac());
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
        intent.putExtra("Mac", list.get(position).getMac());
        intent.putExtra("NickName", list.get(position).getNickName());
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 200) {
                try {
                    wifiBluetoothListViewModel.updateCount(data.getStringExtra("Mac"), data.getIntExtra("SIZE", 0));
                    mainRecyclerViewAdapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                mainRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maintoobar_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Add) {
            ArrayList<String> ssid_List = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                ssid_List.add(list.get(i).getNickName());
            }
            if (ssid_List.size() != 0) {
                Main_Content_Enrollment_Dialog main_content_enrollment_dialog = new Main_Content_Enrollment_Dialog(MainActivity.this, ssid_List);
                main_content_enrollment_dialog.setCancelable(false);
                main_content_enrollment_dialog.setDialogListener(new Main_Content_Enrollment_Dialog.CustomDialogListener() {
                    @Override
                    public void PositiveClick(String Content, int position) {
                        contentListViewModel.Insert(new ContentList(list.get(position).getMac(), list.get(position).getSSID(), Content));
                        wifiBluetoothListViewModel.updateCount(list.get(position).getMac(), list.get(position).getCount() + 1);
                        mainRecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void NegativeClick() {

                    }
                });
                main_content_enrollment_dialog.show();
                CustomDialog_Resize(main_content_enrollment_dialog, 0.9f, 0.3f);
            } else {
                Intent intent = new Intent("Activity_to_Service");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void CustomDialog_Resize(Dialog dialog, float horizontal, float vertical) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = dialog.getWindow();

        int x = (int) (size.x * horizontal);
        int y = (int) (size.y * vertical);

        window.setLayout(x, y);
    }
}