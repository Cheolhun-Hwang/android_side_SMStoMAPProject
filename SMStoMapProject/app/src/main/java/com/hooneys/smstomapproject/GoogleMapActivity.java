package com.hooneys.smstomapproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.hooneys.smstomapproject.MyGEO.GEO;
import com.hooneys.smstomapproject.MyMonitoring.MMSDO;
import com.hooneys.smstomapproject.MyMonitoring.MMSService;
import com.hooneys.smstomapproject.MyPermissionPack.MyPermission;

import org.json.JSONException;
import org.json.JSONObject;

public class GoogleMapActivity extends AppCompatActivity {
    private final String TAG = GoogleMapActivity.class.getSimpleName();
    private final int INIT_ZOOM = 14;

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

        maps.getMapAsync(readyCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(getApplicationContext(), MMSService.class)); // 서비스 시작

        super.onDestroy();
    }

    private void init(){
        isSend = false;
        permission = new MyPermission(GoogleMapActivity.this);

        startService(new Intent(getApplicationContext(), MMSService.class)); // 서비스 시작

        maps = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.send_sms);
        readyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);

                if(MyApp.saveMsg.length() < 1){
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(37.450626, 127.128847))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                            .snippet("[default]")
                            .title("가천대학교")
                            .zIndex((float) 1));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.450626, 127.128847), INIT_ZOOM));
                }else{
                    LatLng lastLatLng = null;
                    for(int i = 0;i<MyApp.saveMsg.length(); i++){
                        try {
                            JSONObject object = MyApp.saveMsg.getJSONObject(i);

                            LatLng latLng = new GEO().getNameToLatLng(getApplicationContext(), object.getString("location"));
                            if(i == MyApp.saveMsg.length()-1){
                                lastLatLng = latLng;
                            }

                            googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                    .snippet("[" + object.getString("date") + "]")
                                    .title(object.getString("location"))
                                    .zIndex((float) 1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    for(int i = 0;i<MyApp.mmsMsg.size(); i++){
                        googleMap.addMarker(new MarkerOptions()
                                .position(MyApp.mmsMsg.get(i).getLatLng())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                                .snippet("[" + MyApp.mmsMsg.get(i).getDepart() + "]")
                                .title(MyApp.mmsMsg.get(i).getLocation())
                                .zIndex((float) 1));
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, INIT_ZOOM));
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
            JSONObject object = MyApp.saveMsg.getJSONObject(MyApp.saveMsg.length()-1);
            for(String num : MyApp.sendSMSNumber){
                SmsManager.getDefault().sendTextMessage(num,
                        null,
                        object.getString("name")+ "!"+object.getString("company")+"!"+
                                object.getString("send_num")+"!!!" + object.getString("date") +
                                "!!!" + object.getString("location"),
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
