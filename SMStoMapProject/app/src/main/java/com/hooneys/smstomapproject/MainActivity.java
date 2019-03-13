package com.hooneys.smstomapproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int INT_ZOOM = 14;
    private LatLng latLng;
    private String locationName, beforeDate;

    private SupportMapFragment maps;
    private OnMapReadyCallback mapCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getLatLng();
        getLocationName();
        maps.getMapAsync(mapCallback);
    }

    private void init(){
        maps = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.main_map);

        mapCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);

                if(latLng.latitude == 0.0f && latLng.longitude == 0.0f){
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(37.450626, 127.128847))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            .snippet("[default]")
                            .title("가천대학교")
                            .zIndex((float) 1));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.450626, 127.128847), INT_ZOOM));
                }else{
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            .snippet("[" + beforeDate + "]")
                            .title(locationName)
                            .zIndex((float) 1));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INT_ZOOM));
                }
            }
        };
    }

    private void getLatLng(){
        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        latLng =  new LatLng(pref.getFloat("lat",0.0f),pref.getFloat("lon", 0.0f));
    }

    private void getLocationName(){
        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        locationName = pref.getString("before_location", null);
        beforeDate = pref.getString("before_date", null);
    }
}
