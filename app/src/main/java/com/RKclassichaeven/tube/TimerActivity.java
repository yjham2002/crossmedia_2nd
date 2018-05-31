package com.RKclassichaeven.tube;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.RKclassichaeven.tube.models.AdapterCall;
import com.RKclassichaeven.tube.models.TimerItem;
import com.ccmheaven.tube.view.TopView;
import com.google.android.gms.ads.AdView;

import bases.BaseActivity;
import bases.Constants;
import bases.utils.AlarmUtils;
import utils.PreferenceUtil;

public class TimerActivity extends BaseActivity {

    private Activity mContext;

    private RecyclerView mRecyclerView;
    private TimerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private View bufferProgress;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getExtras().getString("action", "");
            final int state = intent.getExtras().getInt("state", -1);
            final boolean flag = intent.getExtras().getBoolean("flag", false);
            Log.e("TimerRecv", action);
            switch (action){
                case "buffering":{
                    if(flag) {
                        bufferProgress.setVisibility(View.VISIBLE);
                    }
                    else {
                        bufferProgress.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
                case "finish":{
                    Log.e("finishState", "invoked");
                    TimerActivity.this.finish();
                    break;
                }
                case "refresh":{
                    loadList();
                    timeIndHandler.post(timeIndicator);
                    refreshTimerText();
                    break;
                }
                case "state":{

                    break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTIVITY_INTENT_FILTER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_timer);

        initView();
    }

    private Runnable timeIndicator = new Runnable() {
        @Override
        public void run() {
            final long remainings = (PreferenceUtil.getLong(Constants.PREFERENCE.ALARM_TIME, 0) - System.currentTimeMillis()) / 1000;
            final long hour = remainings / 60 / 60;
            final long min = (remainings - (hour * 60 * 60)) / 60;
            final long sec = (remainings - (hour * 60 * 60)) - (min * 60);
            if(remainings <= 0){
                ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.sleep));
            }else{
                ((TopView) findViewById(R.id.topView1)).setTitleName(String.format("%02d:%02d:%02d", hour, min, sec));
                refreshTimerText();
            }
        }
    };

    private Handler timeIndHandler = new Handler();

    private void refreshTimerText(){
        if(AlarmUtils.getInstance().getCurrentSetAlarm(mContext) == null){
            timeIndHandler.removeCallbacks(timeIndicator);
            ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.sleep));
        }else{
            timeIndHandler.postDelayed(timeIndicator, 1000);
        }
    }

    private void initView(){
        this.mContext = this;

        bufferProgress = findViewById(R.id.bufferProgress);

        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new TimerAdapter(this, R.layout.layout_timer, new AdapterCall<TimerItem>() {
            @Override
            public void onCall(TimerItem article) {
                if(!article.isCancel()) {
                    final int inHour = article.getTimeInMins() / 60;
                    final int leftMin = article.getTimeInMins() - (inHour * 60);
                    final String timeString = String.format("%02d:%02d", inHour, leftMin);
                    showToast(timeString + " 후에 음악방송 재생이 중지됩니다.");
                }else{
                    showToast("슬립타이머 설정이 취소되었습니다.");
                }
            }
        });

        ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.sleep));
        ((TopView) findViewById(R.id.topView1)).setViewVisibility(false, true);

        mRecyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadList();

        timeIndHandler.post(timeIndicator);
        refreshTimerText();
    }

    private void loadList(){
        mAdapter.mListData.clear();
        mAdapter.mListData.add(new TimerItem(1, "1 Mins", false));
        mAdapter.mListData.add(new TimerItem(15, "15 Mins", false));
        mAdapter.mListData.add(new TimerItem(30, "30 Mins", false));
        mAdapter.mListData.add(new TimerItem(45, "45 Mins", false));
        mAdapter.mListData.add(new TimerItem(60 * 1, "1 Hour", false));
        mAdapter.mListData.add(new TimerItem(60 * 2, "2 Hours", false));
        mAdapter.mListData.add(new TimerItem(60 * 4, "4 Hours", false));
        mAdapter.mListData.add(new TimerItem(60 * 12, "12 Hours", false));
        if(PreferenceUtil.getBoolean(Constants.PREFERENCE.IS_ALARM_SET, false)) {
            mAdapter.mListData.add(new TimerItem(0, "Cancel", true));
        }

        mAdapter.notifyDataSetChanged();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
