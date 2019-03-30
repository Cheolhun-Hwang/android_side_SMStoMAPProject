package com.hooneys.smstomapproject.MyApplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.hooneys.smstomapproject.MyMonitoring.MMSDO;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MyApp extends Application {
    public static ArrayList<String> catchSMSNumber;
    public static ArrayList<String> sendSMSNumber;
    public static JSONArray saveMsg;
    public static ArrayList<MMSDO> mmsMsg;

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

    public static void loadSaveMsg(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        String res = pref.getString("save_msg", null);
        if(res != null){
            try {
                saveMsg = new JSONArray(res);
            } catch (JSONException e) {
                e.printStackTrace();
                saveMsg = new JSONArray();
            }
        }else{
            saveMsg = new JSONArray();
        }
    }


//    public static void reloadCatchSMSNumber(Context context){
//        catchSMSNumber.clear();
//        loadCatchSMSNumber(context);
//    }
//
//    private static void loadCatchSMSNumber(Context context){
//        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
//        String numbers = pref.getString("catch_num", "");
//        if (!numbers.equals("")){
//            String[] sp_num = numbers.split(",");
//            for (String num : sp_num){
//                catchSMSNumber.add(num);
//            }
//        }
//    }
//
//    private static void loadSendSMSNumber(Context context){
//        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
//        String numbers = pref.getString("send_num", "");
//        if (!numbers.equals("")){
//            String[] sp_num = numbers.split(",");
//            for (String num : sp_num){
//                sendSMSNumber.add(num);
//            }
//        }
//    }
//
//    public static void initSendSMSNumber(Context context){
//        sendSMSNumber = new ArrayList<>();
//        loadSendSMSNumber(context);
//    }
//
//    public static void reloadSendSMSNumber(Context context){
//        sendSMSNumber.clear();
//        loadSendSMSNumber(context);
//    }
}
