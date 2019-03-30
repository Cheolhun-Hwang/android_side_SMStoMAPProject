package com.hooneys.smstomapproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hooneys.smstomapproject.MyApplication.MyApp;
import com.hooneys.smstomapproject.MyPermissionPack.MyPermission;

public class LoadingActivity extends AppCompatActivity {
    private final int SIG_PERMISSION = 901;

    private Thread loadingThread;
    private MyPermission permission;

    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 101:
                    startActivity(new Intent(getApplicationContext(), GoogleMapActivity.class));
                    finish();
                    break;
                case 102:
                    Toast.makeText(getApplicationContext(), "Loading Error!!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        init();
    }

    private void init(){
        permission = new MyPermission(LoadingActivity.this);
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(permission.checkPermission()){
            //All permission
            startThread();
        }else{
            //No ALL permission
            permission.commitPermission(SIG_PERMISSION);
        }
    }

    @Override
    protected void onStop(){
        clearLoadingThread();
        super.onStop();
    }

    private void startThread(){
        loadingThread = initLoadingThread();
        loadingThread.start();
    }

    private Thread initLoadingThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = myHandler.obtainMessage();
                try {
                    MyApp.initCatchSMSNumber(getApplicationContext());
                    MyApp.initSendSMSNumber(getApplicationContext());
                    MyApp.loadSaveMsg(getApplicationContext());

                    Thread.sleep(2000);
                    msg.what = 101;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    msg.what = 102;
                }
                myHandler.sendMessage(msg);
            }
        });
    }

    private void clearLoadingThread(){
        if(loadingThread != null){
            if(loadingThread.isAlive()){
                loadingThread.interrupt();
            }
            loadingThread = null;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == SIG_PERMISSION){
            boolean isALL = true;
            for(int grant : grantResults){
                if(grant == PackageManager.PERMISSION_DENIED){
                    isALL = false;
                    break;
                }
            }

            if(isALL){
                //All Permission
                startThread();
            }else{
                //No All permission
                Toast.makeText(getApplicationContext(), "모든 권한이 필요합니다.",
                        Toast.LENGTH_SHORT).show();
                permission.commitPermission(SIG_PERMISSION);
            }

        }
    }
}
