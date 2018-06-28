package com.RKclassichaeven.tube.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.RKclassichaeven.tube.FloatingMovieActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.ccmheaven.tube.view.PlayerYouTubeFrag;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import bases.Constants;
import bases.SimpleCallback;
import bases.SimpleStringCallback;
import bases.imageTransform.RoundedTransform;
import bases.utils.AlarmUtils;
import comm.SimpleCall;
import utils.PreferenceUtil;

/**
 * Created by HP on 2018-03-16.
 */
public class MediaService extends Service implements View.OnClickListener{

    private boolean isPlaying = false;

    private NotificationManager mNotificationManager;
    private static final int notiId = 20180319;
    private View mView;
    private WebView webView;
    private WindowManager mManager;
    private IBinder mBinder = new LocalBinder();

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getExtras().getString("action", "");
            Log.e("notiListener", action);
            switch (action) {
                case Constants.INTENT_NOTIFICATION.ACTION_PLAY:{
                    break;
                }
                case Constants.INTENT_NOTIFICATION.ACTION_STOP:{
                    break;
                }
                case Constants.INTENT_NOTIFICATION.ACTION_CLOSE:{
                    mNotificationManager.cancel(notiId);
                    AlarmUtils.getInstance().cancelAll(MediaService.this);
                    PreferenceUtil.setBoolean(Constants.PREFERENCE.IS_ALARM_SET, false);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                    break;
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getExtras().getString("action", "");
            final int state = intent.getExtras().getInt("state", -1);
            Log.e("MediaReceiver", action + " : FROM " + context);
            switch (action){
                case "refresh":{
                    showPlayerNotification();
                    break;
                }
                case "state":{
                    if(state == 0){
                    }
                    break;
                }
            }
        }
    };

    public interface VideoCallBack{
        void onCall();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTIVITY_INTENT_FILTER));
        registerReceiver(notificationListener, new IntentFilter(Constants.INTENT_NOTIFICATION.REP_FILTER));
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MediaService getServiceInstance() {
            return MediaService.this;
        }
    }

    private boolean repeatFlag = false;
    private Handler intervalStateCheckHandler = new Handler();
    private Runnable stateCheck = new Runnable() {
        @Override
        public void run() {
            checkState();
        }
    };

    private void checkState(){
        if(repeatFlag) webView.loadUrl("javascript:currentStatus();");
        intervalStateCheckHandler.postDelayed(stateCheck, 1000);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mView = mInflater.inflate(R.layout.empty_layout, null);

        int versionDependedType = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            versionDependedType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        PlayerYouTubeFrag myFragment =  PlayerYouTubeFrag.newInstance("");
        myFragment.init();

//        ((LinearLayout)mView).addView(myFragment);
//
//
//        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                versionDependedType,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
////                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//
//        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//
//        Log.e("MediaService", "onCreate : webView initialized.");
//
//        mManager.addView(mView, mParams);

    }

    protected void showPlayerNotification(){

        Notification.Builder mBuilder = createNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(notificationChannel);
            mBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
        }

        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.player_notification_layout);

        /**
         * Setting Listeners start
         */
        final Intent intent_play = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        intent_play.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_PLAY);
//        intent_play.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent_play = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_PLAY, intent_play, 0);
//        remoteViews.setOnClickPendingIntent(R.id.noti_play, pendingIntent_play);

        final Intent intent_stop = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        intent_stop.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_STOP);
//        intent_stop.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent_stop = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_STOP, intent_stop, 0);
//        remoteViews.setOnClickPendingIntent(R.id.noti_pause, pendingIntent_stop);

        final Intent intent_close = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        intent_close.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_CLOSE);
//        intent_close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent_close = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_CLOSE, intent_close, 0);
        remoteViews.setOnClickPendingIntent(R.id.noti_close, pendingIntent_close);

        PendingIntent noti_intent = createPendingIntent();
        remoteViews.setOnClickPendingIntent(R.id.mainLayout, createPendingIntent());
        /**
         * Setting Listeners end
         */

        mBuilder.setContent(remoteViews);
//        mBuilder.setContentIntent(createPendingIntent());
        mBuilder.setOngoing(true);

        final Notification notification = mBuilder.build();



        if(1 == 1) {
            remoteViews.setImageViewResource(R.id.noti_img, R.drawable.icon_hour_glass);
            remoteViews.setTextViewText(R.id.noti_title, "재생중인 채널이 없습니다.");
            remoteViews.setTextViewText(R.id.noti_sub, "");
        }else{
            remoteViews.setImageViewResource(R.id.noti_img, R.drawable.icon_hour_glass);
            if(1 != 1) {
                try {
                    Picasso
                            .get()
                            .load("")
                            .centerCrop()
                            .resize(50, 50)
                            .transform(new RoundedTransform(5, 0)).into(remoteViews, R.id.noti_img, notiId, notification);
                }catch (Exception e){
                    e.printStackTrace();
                    remoteViews.setImageViewResource(R.id.noti_img, R.drawable.icon_hour_glass);
                }
            }
            remoteViews.setTextViewText(R.id.noti_title, "zz");
            remoteViews.setTextViewText(R.id.noti_sub, "dfdf");
        }
//        setNotificationPlaying(remoteViews, R.id.noti_play, R.id.noti_pause, isPlaying);

        Log.e("MediaService", "Notification showed");

        if(true) startForeground(notiId, notification);
    }

    private void setNotificationPlaying(RemoteViews remoteViews, int playId, int stopId, boolean isPlaying){
        if(isPlaying){
            remoteViews.setViewVisibility(playId, View.INVISIBLE);
            remoteViews.setViewVisibility(stopId, View.VISIBLE);
        }else{
            remoteViews.setViewVisibility(playId, View.VISIBLE);
            remoteViews.setViewVisibility(stopId, View.INVISIBLE);
        }
    }

    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, RankActivity.class);
        resultIntent.putExtra("action", "open");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(RankActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(
                95,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private Notification.Builder createNotification(){
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Notification.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder = new Notification.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);
        }else{
            builder = new Notification.Builder(this);
        }

        builder
                .setSmallIcon(R.drawable.temp_ico_small)
                .setLargeIcon(icon)
                .setContentTitle("StatusBar Title")
                .setContentText("StatusBar subTitle")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
//                .setDefaults(Notification.);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            builder.setCategory(Notification.CATEGORY_MESSAGE)
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .setVisibility(Notification.VISIBILITY_PUBLIC);
//        }
        return builder;
    }

    public void sendRefreshingBroadcast(){
        final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
        activityIntent1.putExtra("action", "refresh");
        this.sendBroadcast(activityIntent1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//        }
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("MediaService", "onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(notificationListener);
        isPlaying = false;
    }

}
