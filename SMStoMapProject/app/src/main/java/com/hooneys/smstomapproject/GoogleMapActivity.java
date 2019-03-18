package com.hooneys.smstomapproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooneys.smstomapproject.MyApplication.MyApp;
import com.hooneys.smstomapproject.MyPermissionPack.MyPermission;

public class GoogleMapActivity extends AppCompatActivity {
    private final String TAG = GoogleMapActivity.class.getSimpleName();
    private final int INIT_ZOOM = 14;

    private LatLng latLng;
    private String nowAddress, nowDate;
    private FloatingActionButton floatingActionButton;
    private SupportMapFragment maps;
    private OnMapReadyCallback readyCallback;
    private boolean isSend;
    private MyPermission permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        setAppBar(getSupportActionBar());
        init();
        setEvent();
    }

    private void setAppBar(ActionBar bar){
        bar.setTitle(getResources().getString(R.string.app_name));
        bar.setSubtitle("personal project");
    }

    @Override
    protected void onStart() {
        super.onStart();

        getLatLng();
        getLocationAndDate();
        maps.getMapAsync(readyCallback);
    }

    private void init(){
        isSend = false;
        permission = new MyPermission(GoogleMapActivity.this);

        maps = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.send_sms);
        readyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);

                if(latLng.latitude == 0.0f && latLng.longitude == 0.0f){
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(37.450626, 127.128847))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                            .snippet("[default]")
                            .title("가천대학교")
                            .zIndex((float) 1));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.450626, 127.128847), INIT_ZOOM));
                }else{
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            .snippet("[" + nowDate + "]")
                            .title(nowAddress)
                            .zIndex((float) 1));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INIT_ZOOM));
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

    private void getLocationAndDate(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        nowAddress = pref.getString("before_location", null);
        nowDate = pref.getString("before_date", null);
    }

    private void getLatLng(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        latLng = new LatLng(pref.getFloat("lat", 0.0f), pref.getFloat("lon", 0.0f));
    }

    private void sendSMS(){
        String msg = "";
        try{
            for(String num : MyApp.sendSMSNumber){
                SmsManager.getDefault().sendTextMessage(num,
                        null,
                        "$$ [ " + this.nowDate + " ]\n위치 : " + nowAddress,
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
}
