package com.hooneys.smstomapproject.MyApplication;

import android.app.Application;

import java.util.ArrayList;

public class MyApp extends Application {
    public static ArrayList<String> catchSMSNumber;
    public static ArrayList<String> sendSMSNumber;

    public static void initCatchSMSNumber(){
        catchSMSNumber = new ArrayList<>();
        catchSMSNumber.add("01048260178");
        catchSMSNumber.add("01011111111");
        catchSMSNumber.add("01012345678");
    }

    public static void initSendSMSNumber(){
        sendSMSNumber = new ArrayList<>();
        sendSMSNumber.add("01048260178");
    }
}
