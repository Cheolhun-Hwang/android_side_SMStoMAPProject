package com.hooneys.smstomapproject;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooneys.smstomapproject.MyApplication.MyApp;
import com.hooneys.smstomapproject.MyMonitoring.MMSMonitoringService;
import com.hooneys.smstomapproject.MyMonitoring.MMSService;
import com.hooneys.smstomapproject.MyPermissionPack.MyPermission;
import com.hooneys.smstomapproject.MyRooms.Do.Catch;

import java.util.List;

public class GoogleMapActivity extends AppCompatActivity {
    private final String TAG = GoogleMapActivity.class.getSimpleName();
    private final int INIT_ZOOM = 14;
    private final int JOB_ID = 1;

    private FloatingActionButton floatingActionButton;
    private SupportMapFragment maps;
    private GoogleMap mapHandler;
    private OnMapReadyCallback readyCallback;
    private boolean isSend;
    private MyPermission permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        setAppBar(getSupportActionBar());

        applicationInit();
        init();
        initService();

        setEvent();
    }

    private void initService() {
//        startService(new Intent(getApplicationContext(), MMSService.class)); // 서비스 시작
        JobScheduler scheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(this, MMSMonitoringService.class))
                    .setMinimumLatency(DateUtils.MINUTE_IN_MILLIS)
                    .setPersisted(true) //재부팅해도 지속유지
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(this, MMSMonitoringService.class))
                    .setPeriodic(DateUtils.MINUTE_IN_MILLIS)
                    .setPersisted(true) //재부팅해도 지속유지
                    .build();
        }
        int result_code = scheduler.schedule(jobInfo);
        if(result_code == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "MMS Job Success");
        }else{
            Log.d(TAG, "MMS Job Fail...");
        }

//        Job 스캐줄 확인
//        boolean hasBeenScheduled = false;
//        for (JobInfo jobInf : scheduler.getAllPendingJobs()) {
//            if (jobInf.getId() == JOB_ID) {
//                hasBeenScheduled  = true;
//            }
//        }
    }

    private void addingMarkers(List<Catch> catches) {
        mapHandler.clear();

        if(catches.size() < 1){
            mapHandler.addMarker(new MarkerOptions()
                    .position(new LatLng(37.450626, 127.128847))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .snippet("[default]")
                    .title("가천대학교")
                    .zIndex((float) 1));
            mapHandler.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(37.450626, 127.128847), INIT_ZOOM));
            return;
        }

        LatLng point = null;
        for(Catch cat : catches){
            point = new LatLng(cat.getLat(), cat.getLon());
            mapHandler.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .snippet(cat.getCompany())
                    .title(cat.getLocation()));
        }
        mapHandler.moveCamera(CameraUpdateFactory.newLatLngZoom(point, INIT_ZOOM));
    }

    private void applicationInit() {
        MyApp.instatnceActivity = GoogleMapActivity.this;
        MyApp.initSendViewModel(GoogleMapActivity.this);
        MyApp.initCatchViewModel(GoogleMapActivity.this);

        MyApp.catchViewModel.getAllCatehs().observe(MyApp.instatnceActivity,
                new Observer<List<Catch>>() {
                    @Override
                    public void onChanged(@Nullable List<Catch> catches) {
                        MyApp.catches = catches;
                        if(mapHandler != null && catches != null){
                            addingMarkers(catches);
                        }
                    }
                });
    }

    private void setAppBar(ActionBar bar){
        bar.setTitle(getResources().getString(R.string.app_name));
        bar.setSubtitle("personal project");
    }

    @Override
    protected void onStart() {
        super.onStart();

        maps.getMapAsync(readyCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(getApplicationContext(), MMSService.class));

        super.onDestroy();
    }

    private void init(){
        isSend = false;
        permission = new MyPermission(GoogleMapActivity.this);

        maps = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.send_sms);
        readyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapHandler = googleMap;

                mapHandler.getUiSettings().setScrollGesturesEnabled(true);
                mapHandler.getUiSettings().setZoomGesturesEnabled(true);
                mapHandler.getUiSettings().setZoomControlsEnabled(true);
                mapHandler.getUiSettings().setCompassEnabled(true);

                // 20190924 추가수정
                if(MyApp.catches != null){
                    addingMarkers(MyApp.catches);
                }
            }
        };
    }

    private void setEvent(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSend){
                    isSend = true;
                    sendSMS();
                }
            }
        });
    }

    private void sendSMS(){
        String msg = "";
        try{
            Catch item = MyApp.catches.get(MyApp.catches.size()-1);
            for(String num : MyApp.sendSMSNumber){
                SmsManager.getDefault().sendTextMessage(num,
                        null,
                        item.getName()+ "!"+item.getCompany()+"!"+
                                item.getSendNum()+"!!!" + item.getDate() +
                                "!!!" + item.getLocation(),
                        null,
                        null);
            }
            msg = "정상적으로 전송하였습니다.";
        }catch (Exception e){
            e.printStackTrace();
            msg = "오류가 나타났습니다. 로그를 확인해주세요.";
        }
        alertShow(msg);
        isSend = false;
    }

    private void alertShow(String msg){
        AlertDialog.Builder alert = new AlertDialog.Builder(GoogleMapActivity.this);
        alert.setTitle(R.string.app_name);
        alert.setMessage(msg);
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_catch_num:
                intentTypeToAdd(1);
                break;
            case R.id.add_send_num:
                intentTypeToAdd(2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void intentTypeToAdd(int type){
        Intent intent = new Intent(getApplicationContext(), AddNumberActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
