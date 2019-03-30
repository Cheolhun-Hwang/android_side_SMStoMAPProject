package com.hooneys.smstomapproject.MyBroadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.hooneys.smstomapproject.GoogleMapActivity;
import com.hooneys.smstomapproject.MyApplication.MyApp;
import com.hooneys.smstomapproject.MyGEO.GEO;
import com.hooneys.smstomapproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyReceiver extends BroadcastReceiver {
    private final String TAG = MyReceiver.class.getSimpleName();
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy년 MM월 HH시 mm분 ss초 ", Locale.KOREA);

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        Log.d(TAG, "BroadCast : " + action + " / " + type);

        if (intent.getAction().equals(ACTION_SMS_RECEIVED)){
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
                if (parse_one.length < 5){
                    return;
                }
                try {
                    matching(context, parse_one[0], parse_one[1], parse_one[2], now, currentDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void matching2(Context context, String msg) {
        String[] sp_msg = msg.split("\n");
        Log.d(TAG, sp_msg.toString());
    }

    private void matching(Context context, String name, String comp, String num, String loc, String date) throws JSONException {
        if(MyApp.saveMsg.length() < 1){
            JSONObject object = new JSONObject();
            object.put("name", name);
            object.put("company", comp);
            object.put("send_num", num);
            object.put("location", loc);
            object.put("date", date);
            MyApp.saveMsg.put(object);
            actToCatchEvent(context, loc, date);
        }else{
            for(int index = 0; index < MyApp.saveMsg.length() ; index++){
                JSONObject object = MyApp.saveMsg.getJSONObject(index);
                Log.d(TAG, name+"/"+comp+"/"+num);
                Log.d(TAG, object.getString("name")+"/"+
                        object.getString("company")+"/"+
                        object.getString("send_num"));
                Log.d(TAG, "TF : " + (object.getString("name").equals(name) &&
                        object.getString("company").equals(comp) &&
                        object.getString("send_num").equals(num)));
                if(object.getString("name").equals(name) &&
                    object.getString("company").equals(comp) &&
                    object.getString("send_num").equals(num)){
                    //같은 물품
                    Log.d(TAG, loc);
                    Log.d(TAG, object.getString("location"));
                    Log.d(TAG, "TF : " + !(object.getString("location").equals(loc)));
                    if(!object.getString("location").equals(loc)){
                        //다른 위치!!
                        MyApp.saveMsg.remove(index);
                        object.put("location", loc);
                        MyApp.saveMsg.put(object);
                        actToCatchEvent(context, loc, date);
                        return;
                    }else{
                        //같은 위치!! 였을때
                        return;
                    }
                }
            }

            //여기서 실행되는건?
            //다른 물품!!
            JSONObject object = new JSONObject();
            object.put("name", name);
            object.put("company", comp);
            object.put("send_num", num);
            object.put("location", loc);
            object.put("date", date);
            MyApp.saveMsg.put(object);
            actToCatchEvent(context, loc, date);
        }
    }

    private void actToCatchEvent(Context context, String now, String currentDate){
        saveJson(context);
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

    private void saveJson(Context context){
        /*
        [
            {
                name : '이**',
                company : 'LGT',
                send_num : '01960',
                location : '서울시 송파구 문정동 678',
                date : '2019-02-11 22:02:11'
            },....
        ]
        * */
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("save_msg", MyApp.saveMsg.toString());
        editor.commit();
    }
}
