package com.hooneys.smstomapproject.MyApplication;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.hooneys.smstomapproject.MyRooms.Do.Catch;
import com.hooneys.smstomapproject.MyRooms.ViewModels.CatchViewModel;
import com.hooneys.smstomapproject.MyRooms.ViewModels.SendViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {
    public static ArrayList<String> catchSMSNumber;
    public static ArrayList<String> sendSMSNumber;

    public static FragmentActivity instatnceActivity;
    public static CatchViewModel catchViewModel;
    public static SendViewModel sendViewModel;
    public static List<Catch> catches;

    public static void initCatchViewModel(FragmentActivity instance){
        catchViewModel = ViewModelProviders.of(instance).get(CatchViewModel.class);
    }

    public static void initSendViewModel(FragmentActivity instance){
        sendViewModel = ViewModelProviders.of(instance).get(SendViewModel.class);
    }


    public static void initCatchSMSNumber(Context context){
        catchSMSNumber = new ArrayList<>();
        loadCatchSMSNumber(context);
    }

    public static void reloadCatchSMSNumber(Context context){
        catchSMSNumber.clear();
        loadCatchSMSNumber(context);
    }

    private static void loadCatchSMSNumber(Context context){
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        String num = pref.getString("catch_num", null);
        if(num != null){
            String[] numbers = num.split(",");
            for(String n : numbers){
                catchSMSNumber.add(n);
            }
        }
    }

    public static void initSendSMSNumber(Context context){
        sendSMSNumber = new ArrayList<>();
        loadSendSMSNumber(context);
    }

    public static void reloadSendSMSNumber(Context context){
        sendSMSNumber.clear();
        loadSendSMSNumber(context);
    }

    private static void loadSendSMSNumber(Context context){
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        String num = pref.getString("send_num", null);
        if(num != null){
            String[] numbers = num.split(",");
            for(String n : numbers){
                sendSMSNumber.add(n);
            }
        }
    }
}
