package com.hooneys.smstomapproject.MyPermissionPack;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class MyPermissionPack {
    private String[] permissions = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private Activity activity;

    public MyPermissionPack(Activity a){
        activity = a;
    }

    public boolean checkPermission(){
        boolean isAll = true;
        int permissionCheck = PackageManager.PERMISSION_GRANTED;

        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(activity, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                isAll = false;
                break;
            }
        }
        return isAll;
    }

    public boolean checkPermissionSP(String target){
        if(ContextCompat.checkSelfPermission(activity, target) == PackageManager.PERMISSION_DENIED){
            return false;
        }
        return true;
    }

    public void commitPermission(int SIG_PERMISSION){
        ActivityCompat.requestPermissions(activity, permissions, SIG_PERMISSION);
    }


}
