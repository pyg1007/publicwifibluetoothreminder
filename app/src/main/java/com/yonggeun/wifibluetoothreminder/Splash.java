package com.yonggeun.wifibluetoothreminder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

public class Splash extends AppCompatActivity {

    private static final int REQUEST_ACCESS_LOCATION = 1000;

    private String Market_Version;

    boolean isPermissionCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getStoreVersion();
        VersionCheck();
    }

    public void checkpermmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ACCESS_FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED || ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOCATION);
            else if (ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED && ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED) {
                isPermissionCheck = true;
                IgnoreDose();
            }
        }
    }

    public void IgnoreDose() {
        boolean isWhiteListing = false;
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivityForResult(intent, 1);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new SplashHandler(), 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isPermissionCheck = false;
                    break;
                } else {
                    isPermissionCheck = true;
                }
            }
            if (isPermissionCheck) {
                IgnoreDose();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("권한").setMessage("해당 어플리케이션을 사용하기 위해서 필요합니다. 정말 거부하시겠습니까?");
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
                        AlertDialog.Builder no = new AlertDialog.Builder(Splash.this);
                        no.setMessage("해당 어플리케이션을 사용하기 위해서는 권한이 필요합니다. 해당기능을 사용하기 위해서는 설정 -> App 및 알림 -> App 선택 -> 권한 -> 허용을 해주세요");
                        no.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Splash.this.finish();
                            }
                        });
                        no.show();
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Handler handler = new Handler();
                handler.postDelayed(new SplashHandler(), 2000);
            } else if (resultCode == RESULT_CANCELED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("권한").setMessage("이 설정을 허용하지 않으면 푸시알림이 지속적으로 오게됩니다. 정말 허용하지 않겠습니까?");
                builder.setPositiveButton("무시", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Handler handler = new Handler();
                        handler.postDelayed(new SplashHandler(), 2000);
                    }
                });
                builder.setNegativeButton("허용", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        IgnoreDose();
                    }
                });
                builder.show();
            }
        }
    }

    private class SplashHandler implements Runnable {
        @Override
        public void run() {
            startActivity(new Intent(Splash.this, MainActivity.class));
            Splash.this.finish();
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void VersionCheck(){
        String version_name = BuildConfig.VERSION_NAME;
        Log.e("Version_Name : ", version_name);
        Log.e("MarKet_Version : ", Market_Version);

        try {
            if (!Market_Version.equals(version_name)){
                String[] Gradle_Version = version_name.split("\\.");
                String[] Market = Market_Version.split("\\.");

                if (!Gradle_Version[1].equals(Market[1])){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
                    builder.setMessage("업데이트가 필요합니다. 확인을 누르면 플레이스토어로 이동합니다.").setCancelable(false);
                    builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String appPackageName = getPackageName();
                            try{
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                finish();
                            }catch (Exception e){
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                finish();
                            }
                        }
                    });
                    builder.show();
                }else{
                    checkpermmission();
                }
            }else{
                checkpermmission();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void getStoreVersion(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=com.yonggeun.wifibluetoothreminder").get();

                    Elements version = document.select(".htlgb");

                    for (int i = 0 ; i<30; i++){
                        Market_Version = version.get(i).text();
                        if (Pattern.matches("^[0-9]+.[0-9]+.[0-9]+$", Market_Version)){
                            break;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try{
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
