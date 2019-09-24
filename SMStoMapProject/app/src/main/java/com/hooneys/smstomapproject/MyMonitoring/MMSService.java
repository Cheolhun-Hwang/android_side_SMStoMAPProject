package com.hooneys.smstomapproject.MyMonitoring;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.hooneys.smstomapproject.GoogleMapActivity;
import com.hooneys.smstomapproject.MyApplication.MyApp;
import com.hooneys.smstomapproject.MyGEO.GEO;
import com.hooneys.smstomapproject.MyRooms.Do.Catch;
import com.hooneys.smstomapproject.R;

import java.util.ArrayList;

public class MMSService extends Service {
    private final String TAG = MMSService.class.getSimpleName();
    private final int TIME_CHECK = 60 * 1000 * 1 ; //ms
    private final String COMPANY_NAME = "가천대학교산학협력관";
    private final String COMPANY_PHONE = "031-750-4615";
    private final String SEND_NAME = "대표자";
    private boolean isRun;
    private int beforeLength;
    private Thread mms_monitoring;

    public MMSService() {
        beforeLength = -1;
        isRun = true;

        Log.d(TAG, "MMS Service Start...");
        mms_monitoring = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   if(isRun){
                       Log.d(TAG, "Monitoring start...");
                       startMonitoring();
                       Thread.sleep(TIME_CHECK);
                   }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mms_monitoring.start();

    }

    private void startMonitoring() {
        final String[] projection = new String[] {"_id", "ct_t", "date"};
        Uri uri = Uri.parse("content://mms/inbox");
        Cursor query = getContentResolver().query(uri, projection, null, null, "date DESC");

        if ( query.moveToFirst() ) {
            //do{...}while(query.moveToNext());
            String mmsId = query.getString(query.getColumnIndex("_id"));
            String date = query.getString(query.getColumnIndex("date"));
            String mmsType = query.getString(query.getColumnIndex("ct_t"));
            String messageBody = "";
            if ( mmsType != null && mmsType.startsWith("application/vnd.wap.multipart")) {
                // MMS인 것을 한번 더 확인
                String selectionPart = "mid=" + mmsId;
                Uri uriPart = Uri.parse("content://mms/part");
                Cursor cur = getContentResolver().query(uriPart, null, selectionPart, null, null);
                if (cur.moveToFirst()) {
                    do {
                        String partId = cur.getString(cur.getColumnIndex("_id"));
                        String type = cur.getString(cur.getColumnIndex("ct"));
                        if ("text/plain".equals(type)) {
                            String data = cur.getString(cur.getColumnIndex("_data"));
                            if (data != null) {
                                messageBody = "None";
                            }else {
                                messageBody = cur.getString(cur.getColumnIndex("text"));
                            }
                        }
                    } while( cur.moveToNext() );
                    cur.close();
                }
            }
//            Log.d(TAG, "MMS List : " + mmsId + " / " + date + " / " + mmsType + " / " + messageBody);
            if(messageBody.contains("[발신기지국]") && messageBody.contains("[위치자료]")){
                String[] sp_msg = messageBody.split("\n");
                Log.d(TAG, sp_msg[6]);  //위치정보
                if(MyApp.catches.size() < 1){
                    //추가
                    Catch cat = new Catch();
                    cat.setCompany(COMPANY_NAME);
                    cat.setDate(date);
                    LatLng latLng = new GEO().getNameToLatLng(MyApp.instatnceActivity, sp_msg[6]);
                    cat.setLocation(sp_msg[6]);
                    cat.setLat(latLng.latitude);
                    cat.setLon(latLng.longitude);
                    cat.setPhone(COMPANY_PHONE);
                    cat.setName(SEND_NAME);
                    MyApp.catchViewModel.insert(cat);
                    makePushAlarm(sp_msg[6], date);
                    return;
                }else{
                    for(Catch cat : MyApp.catches){
                        if(cat.getName().equals(SEND_NAME) &&
                            cat.getCompany().equals(COMPANY_NAME) &&
                            cat.getPhone().equals(COMPANY_PHONE)) {
                            if(!cat.getLocation().equals(sp_msg[6])){
                                //수정
                                LatLng latLng = new GEO().getNameToLatLng(MyApp.instatnceActivity, sp_msg[6]);
                                cat.setLocation(sp_msg[6]);
                                cat.setLat(latLng.latitude);
                                cat.setLon(latLng.longitude);
                                MyApp.catchViewModel.update(cat);
                                makePushAlarm(sp_msg[6], date);
                            }
                            return;
                        }
                    }
                    //추가
                    Catch cat = new Catch();
                    cat.setCompany(COMPANY_NAME);
                    cat.setDate(date);
                    LatLng latLng = new GEO().getNameToLatLng(MyApp.instatnceActivity, sp_msg[6]);
                    cat.setLocation(sp_msg[6]);
                    cat.setLat(latLng.latitude);
                    cat.setLon(latLng.longitude);
                    cat.setPhone(COMPANY_PHONE);
                    cat.setName(SEND_NAME);
                    MyApp.catchViewModel.insert(cat);
                    makePushAlarm(sp_msg[6], date);
                    return;
                }
            }
        }
        query.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    private void makePushAlarm(String where, String when){
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //Android 26 이상
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String id = "all_user_channel";
            // 사용자에게 보이는 채널의 이름
            CharSequence name = getString(R.string.channel_name); //"ALL"
            // 사용자에게 보이는 채널의 설명
            String description = getString(R.string.channel_description); //"모든 이용자에게"
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 알림 채널 설정
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // 기기가 이 기능을 지원한다면, 이 채널에 게시되는 알림에 대한 알림 불빛 색상을 설정
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(mChannel);
            builder = new Notification.Builder(MMSService.this, mChannel.getId());
        }else{
            builder = new Notification.Builder(MMSService.this);
        }
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_black_24dp));
        builder.setSmallIcon(R.drawable.ic_map_black_24dp);
        builder.setContentTitle(where);
        builder.setContentText("[ " + when + " ]");

        Intent intent = new Intent(MMSService.this, GoogleMapActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MMSService.this);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        manager.notify(1, builder.build());
    }

    @Override
    public void onDestroy() {
        synchronized (MMSService.this){
            this.isRun = false;
        }

        super.onDestroy();
    }
}
