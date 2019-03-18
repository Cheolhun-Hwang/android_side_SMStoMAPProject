package com.hooneys.smstomapproject.MyBroadcast;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.hooneys.smstomapproject.GoogleMapActivity;
import com.hooneys.smstomapproject.MainActivity;
import com.hooneys.smstomapproject.MyApplication.MyApp;
import com.hooneys.smstomapproject.MyGEO.GEO;
import com.hooneys.smstomapproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyReceiver extends BroadcastReceiver {
    private final String TAG = MyReceiver.class.getSimpleName();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy년 MM월 HH시 mm분 ss초 ", Locale.KOREA);

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages = getSmsMessages(bundle);
            //수신 받은 시간
            Date date = new Date(smsMessages[0].getTimestampMillis());
            String currentDate = mDateFormat.format(date);
            Log.i(TAG, "문자 수신 시간 : " + currentDate.toString());
            //SMS 발신 번호
            String receivedNum = smsMessages[0].getOriginatingAddress();
            Log.i(TAG, "발신 번호 : " + receivedNum);
            //문자 내용
            String msg = smsMessages[0].getMessageBody();
            Log.i(TAG, "발신 내용 : " + msg);

            if(isCatches(receivedNum)){
                String[] parse_one = msg.split("!");
                String now = parse_one[parse_one.length-1];
                if (parse_one.length < 2 || now.contains("$$")){
                   return;
                }
                String before = getBeforeLocation(context);
                Log.d(TAG, "Before : " + before);
                Log.d(TAG, "now : " + now);

                if(before == null){
                    Log.d(TAG, "First Recognition!!");
                    actToCatchEvent(context, now, currentDate);
                }else{
                    if(!before.equals(now)){
                        Log.d(TAG, "New Spot!!");
                        actToCatchEvent(context, now, currentDate);
                    }
                }
            }
        }
    }

    private void actToCatchEvent(Context context, String now, String currentDate){
        saveLocation(context, now, currentDate);
        LatLng getNowLocation = new GEO().getNameToLatLng(context,now);
        if(getNowLocation != null){
            saveLatLng(context, getNowLocation);
        }
        makePushAlarm(context, now, currentDate);
    }

    private boolean isCatches(String sender){
        for(String num : MyApp.catchSMSNumber){
            if(num.toLowerCase().equals(sender)){
                return true;
            }
        }
        return false;
    }

    private SmsMessage[] getSmsMessages(Bundle bundle){
        //문자메시지
        Object[] Msg = (Object[])bundle.get("pdus");
        SmsMessage[] smsMessages = new SmsMessage[Msg.length];
        for(int index = 0; index<Msg.length; index++){
            smsMessages[index] = SmsMessage.createFromPdu((byte[])Msg[index]);
        }
        return smsMessages;
    }

    private String getBeforeLocation(Context context){
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString("before_location", null);
    }

    private void saveLocation(Context context, String name, String currentDate){
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("before_location", name);
        editor.putString("before_date", currentDate);
        editor.commit();
    }

    private void saveLatLng(Context context, LatLng latLng){
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("lat", (float) latLng.latitude);
        editor.putFloat("lon", (float) latLng.longitude);
        editor.commit();
    }

    private void makePushAlarm(Context context, String where, String when){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Android 26 이상
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String id = "all_user_channel";
            // 사용자에게 보이는 채널의 이름
            CharSequence name = context.getString(R.string.channel_name); //"ALL"
            // 사용자에게 보이는 채널의 설명
            String description = context.getString(R.string.channel_description); //"모든 이용자에게"
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
            builder = new Notification.Builder(context, mChannel.getId());
        }else{
            builder = new Notification.Builder(context);
        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_map_black_24dp));
        builder.setSmallIcon(R.drawable.ic_map_black_24dp);
        builder.setContentTitle(where);
        builder.setContentText("[ " + when + " ]");

        Intent intent = new Intent(context, GoogleMapActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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
}
