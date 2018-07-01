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
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.RKclassichaeven.tube.FloatingMovieActivity;
import com.RKclassichaeven.tube.MyApplication;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.models.SyncInfo;
import com.ccmheaven.tube.pub.ListInfo;
import com.ccmheaven.tube.view.PlayerYouTubeFrag;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import net.khirr.library.foreground.Foreground;

import java.util.List;
import java.util.Vector;

import bases.Constants;
import bases.imageTransform.RoundedTransform;
import bases.utils.AlarmUtils;
import utils.PreferenceUtil;

/**
 * Created by HP on 2018-03-16.
 */
public class MediaService extends Service implements View.OnClickListener{

    private NotificationManager mNotificationManager;
    private static final int notiId = 20180319;
    private View mView, botView, botArea;
    private View wrap;
    private List<ListInfo> tracks = new Vector<>();
    private WebView webView;
    private WindowManager mManager;
    private YouTubePlayerView player;
    private YouTubePlayer actualPlayer;
    private IBinder mBinder = new LocalBinder();
    private WindowManager.LayoutParams mParamsBot;
    private WindowManager.LayoutParams mParams;

    private float dX = 0;
    private float dY = 0;

    Foreground.Listener foregroundListener;

    public List<ListInfo> getTracks() {
        return tracks;
    }

    public void setTracks(List<ListInfo> tracks) {
        this.tracks = tracks;
    }

    private BroadcastReceiver notificationListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getExtras().getString("action", "");
            Log.e("notiListener", action);
            switch (action) {
                case Constants.INTENT_NOTIFICATION.ACTION_PLAY:{
                    final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
                    activityIntent1.putExtra("action", "refresh");
                    activityIntent1.putExtra("second", "playYT");
                    context.sendBroadcast(activityIntent1);

                    if(tracks.size() > 0) {
                        syncInfo.setPlayState();
                        refreshPlayer();
                    }
                    break;
                }
                case Constants.INTENT_NOTIFICATION.ACTION_STOP:{
                    final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
                    activityIntent1.putExtra("action", "refresh");
                    activityIntent1.putExtra("second", "stopYT");
                    context.sendBroadcast(activityIntent1);
                    syncInfo.setPauseState();
                    refreshPlayer();
                    break;
                }
                case Constants.INTENT_NOTIFICATION.ACTION_NEXT:{
                    nextSong();
                    final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
                    activityIntent1.putExtra("action", "refresh");
                    activityIntent1.putExtra("second", "nextYT");
                    context.sendBroadcast(activityIntent1);
                    break;
                }
                case Constants.INTENT_NOTIFICATION.ACTION_PREV:{
                    prevSong();
                    final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
                    activityIntent1.putExtra("action", "refresh");
                    activityIntent1.putExtra("second", "prevYT");
                    context.sendBroadcast(activityIntent1);
                    break;
                }
                case Constants.INTENT_NOTIFICATION.ACTION_CLOSE:{
                    exitApp();
                    break;
                }
            }
        }
    };

    private void exitApp(){
        mNotificationManager.cancel(notiId);
        AlarmUtils.getInstance().cancelAll(MediaService.this);
        PreferenceUtil.setBoolean(Constants.PREFERENCE.IS_ALARM_SET, false);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

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

    private SyncInfo syncInfo = new SyncInfo();

    public SyncInfo getSyncInfo() {
        return syncInfo;
    }

    private void nextSong(){
        final int nextIndex = syncInfo.getCurrentIndex() + 1 > tracks.size() - 1 ?  0 : syncInfo.getCurrentIndex() + 1;
        syncInfo.setCurrentIndex(nextIndex);
        syncInfo.setBySong(tracks.get(nextIndex));
        refreshPlayer();
    }

    private void prevSong(){
        final int nextIndex = syncInfo.getCurrentIndex() - 1 < 0 ?  tracks.size() - 1 : syncInfo.getCurrentIndex() - 1;
        syncInfo.setCurrentIndex(nextIndex);
        syncInfo.setBySong(tracks.get(nextIndex));
        refreshPlayer();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mView = mInflater.inflate(R.layout.always_on_display_layout, null);
        botView = mInflater.inflate(R.layout.always_on_display_layout_bottom, null);
        botArea = botView.findViewById(R.id.area);
        player = mView.findViewById(R.id.player);
        wrap = mView.findViewById(R.id.wrapper);

        int versionDependedType = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            versionDependedType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                versionDependedType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mParamsBot = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                versionDependedType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mParamsBot.gravity = Gravity.BOTTOM;

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        wrap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final WindowManager.LayoutParams p = (WindowManager.LayoutParams)mView.getLayoutParams();

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = event.getRawX();
                        dY = event.getRawY();
                        mManager.addView(botView, mParamsBot);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(botArea.getY() < dY){
                            botArea.setBackgroundColor(getResources().getColor(R.color.transparent_active));
                        }else{
                            botArea.setBackgroundColor(getResources().getColor(R.color.transparent_inactive));
                        }
                        p.x = (int)(p.x - (dX - event.getRawX()));
                        p.y = (int)(p.y - (dY - event.getRawY()));
                        mManager.updateViewLayout(mView, p);
                        dX = event.getRawX();
                        dY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        if(botArea.getY() < dY){
                            exitApp();
                        }else{
                            // Do Nothing
                        }
                        mManager.removeView(botView);
                        break;

                    default:
                        return false;
                }
                return true;
            }

        });

        player.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        actualPlayer = initializedYouTubePlayer;
                        actualPlayer.addListener(new YouTubePlayerListener() {
                            @Override
                            public void onReady() {

                            }

                            @Override
                            public void onStateChange(int state) {
                                if(tracks.size() == 0) {
                                    syncInfo.release();
                                    refreshPlayer();
                                    return;
                                }
                                if(state == PlayerConstants.PlayerState.ENDED){
                                    Log.e("MediaService", "ENDED");
                                    if(syncInfo.getState() == SyncInfo.STATE_PLAY){
                                        Log.e("MediaService", "ENDED - STATE_PLAY");
                                        nextSong();
                                    }
                                }
                            }

                            @Override
                            public void onPlaybackQualityChange(@NonNull String playbackQuality) {

                            }

                            @Override
                            public void onPlaybackRateChange(@NonNull String playbackRate) {

                            }

                            @Override
                            public void onError(int error) {

                            }

                            @Override
                            public void onApiChange() {

                            }

                            @Override
                            public void onCurrentSecond(float second) {
                                syncInfo.setCurrentTime(second);
                            }

                            @Override
                            public void onVideoDuration(float duration) {
                                Log.e("YTime", "onVideoDuration : " + duration);
                            }

                            @Override
                            public void onVideoLoadedFraction(float loadedFraction) {

                            }

                            @Override
                            public void onVideoId(@NonNull String videoId) {

                            }
                        });
                    }

                    @Override
                    public void onStateChange(int state) {
                        super.onStateChange(state);
                    }
                });
            }
        }, true);

        syncInfo.release();
//        syncInfo.setPlayState();
//        syncInfo.setTitle("X Song");
//        syncInfo.setAuthor("볼빨간 사춘기");
//        syncInfo.setCurrentTime(10);
//        syncInfo.setVideoId("ZD9jqLNN_V4");

        foregroundListener = new Foreground.Listener() {
            @Override
            public void foreground() {
                Log.e("Foreground", "Go to foreground");
                activate(false);
            }
            @Override
            public void background() {
                Log.e("Foreground", "Go to background");
                refreshPlayer();
            }
        };

        Foreground.Companion.addListener(foregroundListener);

        Log.e("MediaService", "onCreate : webView initialized.");

//        mManager.addView(mView, mParams);

    }

    public void refreshPlayer(){
        showPlayerNotification();
        if(actualPlayer != null && Foreground.Companion.isBackground()) {
            if (syncInfo.getState() != SyncInfo.STATE_RELEASE) {
                activate(true);
                if (syncInfo.getState() == SyncInfo.STATE_PLAY) {
                    actualPlayer.loadVideo(syncInfo.getVideoId(), syncInfo.getCurrentTime());
                } else {
                    actualPlayer.cueVideo(syncInfo.getVideoId(), syncInfo.getCurrentTime());
                }
            } else {
                activate(false);
            }
        }
    }

    private void activate(boolean active){
        if(active && Foreground.Companion.isBackground()){
            player.setVisibility(View.VISIBLE);
            try {
                mManager.addView(mView, mParams);
            }catch (Exception e){
                Log.e("MediaService", "view already attached");
            }
        }else{
            player.setVisibility(View.INVISIBLE);
            try{
                mManager.removeView(mView);
            }catch(IllegalArgumentException e){
                Log.e("MediaService", "view not found");
            }
        }
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
        PendingIntent pendingIntent_play = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_PLAY, intent_play, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bot_play, pendingIntent_play);

        final Intent intent_stop = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        intent_stop.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_STOP);
//        intent_stop.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent_stop = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_STOP, intent_stop, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bot_pause, pendingIntent_stop);

        final Intent intent_next = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        intent_next.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_NEXT);
//        intent_stop.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent_next = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_NEXT, intent_next, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bot_next, pendingIntent_next);

        final Intent intent_prev = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        intent_prev.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_PREV);
//        intent_stop.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent_prev = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_PREV, intent_prev, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bot_prev, pendingIntent_prev);

        final Intent intent_close = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        intent_close.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_CLOSE);
//        intent_close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent_close = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_CLOSE, intent_close, PendingIntent.FLAG_UPDATE_CURRENT);
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

        remoteViews.setTextViewText(R.id.noti_title, syncInfo.getTitle());
        remoteViews.setTextViewText(R.id.noti_sub, syncInfo.getAuthor());
        remoteViews.setImageViewResource(R.id.noti_img, R.color.jet);

        if(syncInfo.getState() == SyncInfo.STATE_RELEASE) {

        }else{
            if(syncInfo.getThumbnail() != null && !syncInfo.getThumbnail().trim().equals("")) {
                try {
                    Picasso
                            .get()
                            .load(syncInfo.getThumbnail())
                            .centerCrop().resize(60, 50).into(remoteViews, R.id.noti_img, notiId, notification);
                }catch (Exception e){
                    e.printStackTrace();
                    remoteViews.setImageViewResource(R.id.noti_img, R.drawable.icon_hour_glass);
                }
            }
        }

        setNotificationPlaying(remoteViews, R.id.bot_play, R.id.bot_pause, syncInfo.getState() == SyncInfo.STATE_PLAY);

        startForeground(notiId, notification);
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
    }

}
