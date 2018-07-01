package com.RKclassichaeven.tube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
//import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.models.SyncInfo;
import com.ccmheaven.tube.adapter.CommonListviewAdapter;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.ListInfo;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlaylistEventListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gw.swipeback.SwipeBackLayout;

import net.khirr.library.foreground.Foreground;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class YoutubePlayerActivity extends YouTubeFailureRecoveryActivity {

	//private String path = Environment.getExternalStorageDirectory().getPath();
	private YouTubePlayerView youTubeView;
	private YouTubePlayer player;

	public static int FromActivity;
//	private BottomView bottomview;
	private TopMenuView topMenuView;
	TextView tvTitle;
	View topView;
	public static int index;

	private ImageView left_back, toggler;

	private ImageView btn_prev, btn_next;

	private SwipeBackLayout mSwipeBackLayout;
	private ListView listview;
	private CommonListviewAdapter listviewadapter;
	private MyPlaylistEventListener playlistEventListener;
	private MyPlayerStateChangeListener playerStateChangeListener;
	private MyPlaybackEventListener playbackEventListener;
	private List<Integer> playedList;
	//private ListviewLoadView listviewLoadView;

	private ImageView chkShuffleMode;
	private PLAY_MODE doShuffle = PLAY_MODE.PLAY_ALL_REPEAT;

	private enum PLAY_MODE{
		SHUFFLE_NORMAL{
			@Override
			PLAY_MODE next() {
				return PLAY_ALL;
			}
		},
		PLAY_ALL{
			@Override
			PLAY_MODE next() {
				return PLAY_ALL_REPEAT;
			}
		},
		PLAY_ONE_REPEAT{
			@Override
			PLAY_MODE next() {
				return SHUFFLE_NORMAL;
			}
		},
		PLAY_ALL_REPEAT{
			@Override
			PLAY_MODE next() {
				return PLAY_ONE_REPEAT;
			}
		};
		abstract PLAY_MODE next();
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getExtras().getString("second", "");
			Log.e("TimerRecv", action);
			switch (action){
				case "stopYT":{
					if(player != null) player.pause();
					break;
				}
				case "playYT":{
					if(player != null) player.play();
					break;
				}
				case "nextYT":{
					if(player != null) {
						startPlay();
					}
					break;
				}
				case "prevYT":{
					if(player != null) {
						prevSong();
					}
					break;
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		isPausedByContext = false;
		registerReceiver(broadcastReceiver, new IntentFilter(bases.Constants.ACTIVITY_INTENT_FILTER));
		if(isInitialized){
			syncPlay();
		}
	}

	private boolean isPausedByContext = false;

	@Override
	protected void onPause() {
		super.onPause();
		isPausedByContext = true;
		Log.e("onPause", Foreground.Companion.isBackground() + " / F : " + Foreground.Companion.isForeground());
		unregisterReceiver(broadcastReceiver);
	}

	LinearLayout llAdview;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtubeplayer);
		index = 0;

		mSwipeBackLayout = new SwipeBackLayout(this);
		mSwipeBackLayout.setDirectionMode(SwipeBackLayout.FROM_TOP);
		mSwipeBackLayout.attachToActivity(this);

		btn_next = findViewById(R.id.btn_next);
		btn_prev = findViewById(R.id.btn_prev);

		btn_next.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				startPlay();
			}
		});

		btn_prev.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				prevSong();
			}
		});

		left_back = findViewById(R.id.left_back);
		llAdview = findViewById(R.id.llAdview);
		AdHelper adHelper = new AdHelper(this);
		if(adHelper != null && llAdview != null){
			Log.d("dev", "BottomView.addAdView() call adAdView()");
			adHelper.addAdView(llAdview);
		}

		/**
		 * PickleCode 2018-06-01
		 * get Actual Screen size and revise the position of ad view.
		 */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		Point size2 = new Point();
		display.getSize(size);
		display.getRealSize(size2);
		int width = size.x;
		int height = size.y;
		int width2 = size2.x;
		int height2 = size2.y;

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)llAdview.getLayoutParams();

		params.setMargins(0, 0, 0, height2 - height);
		if(height2 - height > 0) llAdview.setLayoutParams(params);

		Log.e("screenInfo", width + ":" + height + " // " + width2 + ":" + height2);

		left_back.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				finish();
				overridePendingTransition( R.anim.slide_up_rev, R.anim.slide_down_rev );
			}
		});

		Bundle extras = getIntent().getExtras();

		if(extras == null){
			doShuffle = PLAY_MODE.PLAY_ALL_REPEAT;
		}else {
			if (extras.containsKey("mode")) {
				int modeNum = extras.getInt("mode");
				if (modeNum == 0) {
					doShuffle = PLAY_MODE.PLAY_ALL_REPEAT;
				} else {
					doShuffle = PLAY_MODE.SHUFFLE_NORMAL;
				}
			}
		}

		Gson gson = new Gson();
		String strJson = (String) extras.get("playlist");
//		Log.e("devY", strJson);
		if(strJson != null) {
			Type listType = new TypeToken<List<ListInfo>>() {
			}.getType();
			List<ListInfo> templist = (List<ListInfo>) gson.fromJson(strJson, listType);
			if(MyApplication.getMediaService() != null){
				MyApplication.getMediaService().setTracks(templist);
				MyApplication.getMediaService().getSyncInfo().release();
			}
		}

		InitConfig();

		playedList = new ArrayList<Integer>();
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
//		bottomview = (BottomView) findViewById(R.id.bottomView1);
//		topMenuView = findViewById(R.id.topMenuView);
//		bottomview.setActivity(this);
//		bottomview.addAdView();

		topView = findViewById(R.id.topView1);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setSelected(true);

		listview = (ListView) findViewById(R.id.video_listView1);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setItemsCanFocus(false);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Message msg = Message.obtain();
				msg.what = Constants.VIDEOPLAY;
				msg.obj = arg2;
				handler.sendMessage(msg);
			}
		});
		//listview.setOnTouchListener(new ListviewOnTouch()); --> do nothing
		//listview.setOnScrollListener(new ScrollListener()); --> do nothing

		listviewadapter = new CommonListviewAdapter(this, MyApplication.getMediaService().getTracks(), listview, handler);
		listview.setAdapter(listviewadapter);

		youTubeView.initialize(Constants.YOUTUBE_DEV_KEY, this);
		playlistEventListener = new MyPlaylistEventListener();
		playerStateChangeListener = new MyPlayerStateChangeListener();
		playbackEventListener = new MyPlaybackEventListener();
//		topMenuView.buttonimg(FromActivity);

		// CheckBox: Shuffle
		chkShuffleMode = findViewById(R.id.chkShuffle);
		setTogglerImage(doShuffle);
		chkShuffleMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doShuffle = doShuffle.next();
				setTogglerImage(doShuffle);
				//Log.d("dev", "chkShuffle.OnClick. isChecked: " + doShuffle);
			}
		});

		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			nowConfig = Configuration.ORIENTATION_LANDSCAPE;
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			nowConfig = Configuration.ORIENTATION_PORTRAIT;
		}
		newConfig = -1;
	}

	private String ad_fix = "N";

	private void prevSong(){
		if(MyApplication.getMediaService().getTracks().size() == 1){
			startPlay();
			return;
		}

		Log.e("controllerButton", "PREV - PRE : " + index);
		int next_num = index - 2;
		if(next_num < 0) {
			Log.e("controllerButton", "Broken");
			next_num = MyApplication.getMediaService().getTracks().size() - 1;
		}
		index = next_num;
		startPlayWithoutRounding();
		index++;
		Log.e("controllerButton", "PREV - POST : " + index);
	}

	private void InitConfig() {
		SharedPreferences share = getSharedPreferences(LogoActivity.CONFIG_NAME, Context.MODE_PRIVATE);
		ad_fix = share.getString("ad_fix", "N");
	}

	private int nowConfig;
	private int newConfig;

	private void setTogglerImage(PLAY_MODE play_mode){
		switch (doShuffle){
			case PLAY_ALL:
				chkShuffleMode.setImageResource(R.drawable.toggle_shuffle_once);
				break;
			case PLAY_ALL_REPEAT:
				chkShuffleMode.setImageResource(R.drawable.toggle_shuffle_all);
				break;
			case PLAY_ONE_REPEAT:
				chkShuffleMode.setImageResource(R.drawable.toggle_shuffle_one);
				break;
			case SHUFFLE_NORMAL:
				chkShuffleMode.setImageResource(R.drawable.toggle_shuffle_normal);
				break;
		}
	}

	private Handler tracker = new Handler();
	private int checkInterval = 500;
	private Runnable trackingRunnable = new Runnable() {
		@Override
		public void run() {
			float second = ((float)player.getCurrentTimeMillis() / 1000.0f);
			Log.e("PlaybackTracker", player.getCurrentTimeMillis() + " / " + second);
			MyApplication.getMediaService().getSyncInfo().setCurrentTime(second);
			tracker.removeCallbacks(trackingRunnable);
			tracker.postDelayed(this, checkInterval);
		}
	};

	private void checkDuration(boolean flag){
		if(flag){
			tracker.post(trackingRunnable);
		}else{
			tracker.removeCallbacks(trackingRunnable);
		}
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.VIDEOPLAY) {
				if (player != null) {
					player.pause();
					int num = Integer.parseInt(msg.obj.toString());
					startPlay(num);
				} else {
					finish();
				}
			}

			if (msg.what == Constants.REMOVE_ITEM) {
				int num = Integer.parseInt(msg.obj.toString());

				//Log.v("handler_check","here : "+msg.obj);
				//Toast.makeText(YoutubePlayerActivity.this, "제거! : "+num, Toast.LENGTH_SHORT).show();

				//item 제거
				int pos = num;
				if (pos != listview.INVALID_POSITION) {
					MyApplication.getMediaService().getTracks().remove(pos);
					listview.clearChoices();
					listviewadapter.notifyDataSetChanged();
				}

				//제거한 다음 플레이리스트를 재생하게 함. 끝이거나 없을 경우 처리를 잘 해줘야 에러가 안나겠다.
				int next_num = num;
				Log.d("dev", "remove_check_next : " + next_num);//앞으로 플레이할 것
				Log.d("dev", "remove_check_current : " + (index - 1));//앞으로 플레이할 것

				if (MyApplication.getMediaService().getTracks().size() == 0) {
					finish();
				} else if (next_num > MyApplication.getMediaService().getTracks().size() - 1) {
					Log.d("dev", "remove_check 1 : 마지막을 넘어가므로 0으로 넘김");
					next_num = 0;
					startPlay(next_num);
				} else {
					if (next_num < (index - 1)) {
						//계속 재생
						Log.d("dev", "remove_check 1 : 이전 것을 지웠으므로 index 에서 하나를 뺌. 재생은 그대로 함");
						index = index - 1;
					} else if (num == (index - 1)) {
						Log.d("dev", "remove_check 1 : 재생중인 것을 지웠으므로 해당 다시 재생시킴");
						startPlay(next_num);
					} else {
						//계속 재생
						Log.d("dev", "remove_check 1 : 큰 것이므로 그냥 둠");
					}
				}

				//새로운 index로 다시 조정함
				listviewadapter.notifyDataSetChanged();
			}
		}
	};

	int[] randIndex = null;
	int suffle_pos = 0;
	int currentPos = 0;

	int random() {
		if (randIndex == null || randIndex.length != MyApplication.getMediaService().getTracks().size()) {
			randIndex = new int[MyApplication.getMediaService().getTracks().size()];
			for (int i = 0; i < MyApplication.getMediaService().getTracks().size(); i++) {
				randIndex[i] = i;
			}
			suffle_pos = MyApplication.getMediaService().getTracks().size();
		}

		// 한번 전체가 재생되었으면, 다시 처음부터.
		if (suffle_pos < 1) {
			suffle_pos = MyApplication.getMediaService().getTracks().size();
		}

		// 랜덤하게 하나씩 뽑는다.
		int randValue = ((int) (Math.random() * 100000)) % suffle_pos;
		currentPos = randIndex[randValue];
		randIndex[randValue] = randIndex[suffle_pos - 1];
		randIndex[suffle_pos - 1] = currentPos;

		suffle_pos--;
		return currentPos;
	}

	private void syncPlay(){
		SyncInfo syncInfo = MyApplication.getMediaService().getSyncInfo();

		if (!MyApplication.getMediaService().getTracks().isEmpty()) {
			this.index = syncInfo.getCurrentIndex();

			if(syncInfo.getState() == SyncInfo.STATE_RELEASE) {
				syncInfo.setBySong(MyApplication.getMediaService().getTracks().get(index));
				syncInfo.setPlayState();
			}

			Log.e("PlaybacksyncPlay", syncInfo.toString());

			MyApplication.getMediaService().refreshPlayer();

			if (ad_fix.equals("Y")) {

				if (playedList.isEmpty()) {
					tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
					player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode(), (int)(syncInfo.getCurrentTime() * 1000));
					playedList.add(index);

				} else {

					boolean isFirst = true;
					for (int i = 0; i < playedList.size(); i++) {
						if (playedList.get(i) == index) {
							isFirst = false;
							break;
						}
					}
					if (isFirst) {
						Log.e("testif", "" + 0);
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode(), (int)(syncInfo.getCurrentTime() * 1000));
						playedList.add(index);
					} else {
						Log.e("testif", "" + 1);
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						if(syncInfo.getState() == SyncInfo.STATE_PLAY){
							Log.e("testif", "" + 2);
							player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode(), (int)(syncInfo.getCurrentTime() * 1000));
						}else{
							Log.e("testif", "" + 3);
							player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode(), (int)(syncInfo.getCurrentTime() * 1000));
						}
					}

				}
			} else {
				if(index >= MyApplication.getMediaService().getTracks().size()){
					Log.e("testif", "" + 4);
					index = 0;
				}
				tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
				if(syncInfo.getState() == SyncInfo.STATE_PLAY){
					Log.e("testif", "" + 5);
					flagByLoad = true;
					player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode(), (int)(syncInfo.getCurrentTime() * 1000));
				}else{
					Log.e("testif", "" + 6);
					flagByLoad = false;
					player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode(), (int)(syncInfo.getCurrentTime() * 1000));
				}
			}

			// select & scroll the item of listview
			listview.clearChoices();
			listview.setSelection(index);
			listview.setItemChecked(index, true);
			listview.smoothScrollToPosition(index);
		}

		if (++index >= MyApplication.getMediaService().getTracks().size()) {
			index = 0;
			if(doShuffle.equals(PLAY_MODE.PLAY_ALL)){
				if(player != null) player.pause();
			}
		}
		listviewadapter.notifyDataSetChanged();
	}

	private boolean flagByLoad = true;

	private void startPlay() {
		if (!MyApplication.getMediaService().getTracks().isEmpty()) {
			SyncInfo syncInfo = MyApplication.getMediaService().getSyncInfo();
			syncInfo.setBySong(MyApplication.getMediaService().getTracks().get(index));
			syncInfo.setCurrentIndex(index);
			syncInfo.setPlayState();
			MyApplication.getMediaService().refreshPlayer();
			if (ad_fix.equals("Y")) {

				if (playedList.isEmpty()) {
					tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
					player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
					playedList.add(index);

				} else {

					boolean isFirst = true;
					for (int i = 0; i < playedList.size(); i++) {
						if (playedList.get(i) == index) {
							isFirst = false;
							break;
						}
					}
					if (isFirst) {
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
						playedList.add(index);
					} else {
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
					}

				}
			} else {
				if(index >= MyApplication.getMediaService().getTracks().size()){
					index = 0;
				}
				tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
				player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
			}

			// select & scroll the item of listview
			listview.clearChoices();
			listview.setSelection(index);
			listview.setItemChecked(index, true);
			listview.smoothScrollToPosition(index);
		}

		if (++index >= MyApplication.getMediaService().getTracks().size()) {
			index = 0;
			if(doShuffle.equals(PLAY_MODE.PLAY_ALL)){
				if(player != null) player.pause();
			}
		}
		listviewadapter.notifyDataSetChanged();
	}

	private void startPlayWithoutRounding() {
		if (!MyApplication.getMediaService().getTracks().isEmpty()) {
			if (ad_fix.equals("Y")) {

				if (playedList.isEmpty()) {
					tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
					player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
					playedList.add(index);
				} else {

					boolean isFirst = true;
					for (int i = 0; i < playedList.size(); i++) {
						if (playedList.get(i) == index) {
							isFirst = false;
							break;
						}
					}
					if (isFirst) {
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
						playedList.add(index);
					} else {
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
					}

				}
			} else {
				tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
				player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
			}

			// select & scroll the item of listview
			listview.clearChoices();
			listview.setSelection(index);
			listview.setItemChecked(index, true);
			listview.smoothScrollToPosition(index);
		}

		listviewadapter.notifyDataSetChanged();
	}

	private void startPlay(int num) {
		index = num;
		if (!MyApplication.getMediaService().getTracks().isEmpty()) {
			SyncInfo syncInfo = MyApplication.getMediaService().getSyncInfo();
			syncInfo.setBySong(MyApplication.getMediaService().getTracks().get(index));
			syncInfo.setPlayState();
			MyApplication.getMediaService().refreshPlayer();
			if (ad_fix.equals("Y")) {
				if (playedList.isEmpty()) {
					tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
					player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
					playedList.add(index);
				} else {
					boolean isFirst = true;
					for (int i = 0; i < playedList.size(); i++) {
						if (playedList.get(i) == index) {
							isFirst = false;
							break;
						}
					}
					if (isFirst) {
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						player.cueVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
						playedList.add(index);
					} else {
						tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
						player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
					}
				}
			} else {
				tvTitle.setText(MyApplication.getMediaService().getTracks().get(index).getVideoName());
				player.loadVideo(MyApplication.getMediaService().getTracks().get(index).getVideoCode());
			}
			// listview.clearChoices();
			// listview.setItemChecked(index, true);
		}
		if (++index >= MyApplication.getMediaService().getTracks().size()) {
			index = 0;
		}
		listviewadapter.notifyDataSetChanged();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (player != null)
			player.release();
		if (playedList != null) {
			playedList.clear();
		}
		index = 0;
	}

	private boolean isFullscreen = false;
	private boolean isInitialized = false;

	// 놓迦뺏돨쀼딧변鑒
	public void onInitializationSuccess(YouTubePlayer.Provider Provider,
										YouTubePlayer player, boolean wasRestored) {
		Log.d("dev", "InitializationSuccess");
		this.isInitialized = true;
		this.player = player;
		this.player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
			@Override
			public void onFullscreen(boolean b) {
				isFullscreen = b;
				Log.e("isFull", "state : " + isFullscreen);
			}
		});

		player.setPlaylistEventListener(playlistEventListener);
		player.setPlayerStateChangeListener(playerStateChangeListener);
		player.setPlaybackEventListener(playbackEventListener);

		if (!wasRestored && !MyApplication.getMediaService().getTracks().isEmpty()) {
			listview.setItemChecked(index, true);
			listviewadapter.notifyDataSetChanged();
//			startPlay();
			// BLOCK
			syncPlay();
		}

	}

	protected Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	protected final class MyPlaylistEventListener implements
			PlaylistEventListener {
		public void onNext() {
			// log("NEXT VIDEO");
			Log.d("dev", "NEXT VIDEO");
		}

		public void onPrevious() {
			// log("PREVIOUS VIDEO");
			Log.d("dev", "PREVIOUS VIDEO");
		}

		public void onPlaylistEnded() {
			// log("PLAYLIST ENDED");
			Log.d("dev", "PLAYLIST VIDEO");
		}
	}

	protected final class MyPlaybackEventListener implements
			PlaybackEventListener {
		String playbackState = "NOT_PLAYING";
		String bufferingState = "";

		public void onPlaying() {
			Log.e("PlayBack", "onPlaying" + YoutubePlayerActivity.this.player.getCurrentTimeMillis());
			MyApplication.getMediaService().getSyncInfo().setPlayState();
			MyApplication.getMediaService().refreshPlayer();
			checkDuration(true);
			newConfig = -1;
			playbackState = "PLAYING";
			Log.d("dev", playbackState);
		}

		public void onBuffering(boolean isBuffering) {
			bufferingState = isBuffering ? "(BUFFERING)" : "buffering false";
			Log.d("dev", bufferingState);
			if (newConfig != -1) {
				nowConfig = newConfig;
			}
		}

		public void onStopped() {
			MyApplication.getMediaService().getSyncInfo().setPauseState();
			MyApplication.getMediaService().refreshPlayer();
			checkDuration(false);
			playbackState = "STOPPED";
			Log.d("dev", playbackState);
			if (nowConfig == newConfig) {
				// player.get
				player.play();
				// player.seekToMillis(seekto);
				newConfig = -1;
			}
		}

		@Override
		public void onPaused() {
			Log.e("onPaused", Foreground.Companion.isBackground() + " / F : " + Foreground.Companion.isForeground());
			checkDuration(false);
			if(!isPausedByContext) {
				MyApplication.getMediaService().getSyncInfo().setPauseState();
				MyApplication.getMediaService().refreshPlayer();
			}
			newConfig = -1;
			playbackState = "PAUSED";
			Log.d("dev", playbackState);
		}

		public void onSeekTo(int endPositionMillis) {
			Log.d("dev", "endPositionMillis:" + endPositionMillis);
		}
	}

	//private int seekto;

	public void onConfigurationChanged(Configuration newConfiguration) {
		//seekto = player.getCurrentTimeMillis();
		if (newConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			topView.setVisibility(View.VISIBLE);
//			bottomview.setVisibility(View.VISIBLE);
			listview.setVisibility(View.VISIBLE);
			newConfig = Configuration.ORIENTATION_PORTRAIT;
		} else if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			topView.setVisibility(View.GONE);
//			bottomview.setVisibility(View.GONE);
			listview.setVisibility(View.GONE);
			newConfig = Configuration.ORIENTATION_LANDSCAPE;
		}
		// nowConfig = newConfig;
		super.onConfigurationChanged(newConfiguration);
	}

//	private String formatTime(int millis) {
//		int seconds = millis / 1000;
//		int minutes = seconds / 60;
//		int hours = minutes / 60;
//		return (hours == 0 ? "" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
//	}

	protected final class MyPlayerStateChangeListener implements
			PlayerStateChangeListener {
		String playerState = "UNINITIALIZED";

		@Override
		public void onLoading() {
			playerState = "LOADING";
			Log.d("dev", playerState);
			new Thread() {
				public void run() {
					if (playerState.equals("LOADING")) {
						Log.d("zpf", "thread play");
						try {
							//sleep(6000);
							if(player != null)
							{
								player.pause();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}.start();
			// player.
		}

		@Override
		public void onLoaded(String videoId) {
			playerState = String.format("LOADED %s", videoId);
			if(flagByLoad) player.play();
			Log.d("dev", playerState);
		}

		@Override
		public void onAdStarted() {
			playerState = "AD_STARTED";
			Log.d("dev", playerState);
		}

		@Override
		public void onVideoStarted() {
			playerState = "VIDEO_STARTED";
			Log.d("dev", playerState);
			//seekto = 0;
			//isVideoStart = true;
		}

		@Override
		public void onVideoEnded() {
			playerState = "VIDEO_ENDED";
			boolean once_flag = false;
			switch (doShuffle){
				case PLAY_ALL:
					once_flag = true;
					// Processing on startPlay
					break;
				case PLAY_ALL_REPEAT:
					// Do nothing
					break;
				case PLAY_ONE_REPEAT:
					index = index - 1;
					break;
				case SHUFFLE_NORMAL:
					index = random();
					break;
			}
			//isVideoStart = false;
			if(!once_flag) startPlay();
			Log.d("dev", playerState);
		}

		@Override
		public void onError(ErrorReason reason) {
			playerState = "ERROR (" + reason + ")";
			Log.d("dev", playerState);
			if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
				// When this error occurs the player is released and can no longer be used.
				player = null;
				finish();
				// setControlsEnabled(false);
			} else {
				if (reason.toString().equals("NOT_PLAYABLE")) {
					Log.d("dev", reason.toString() + " index:" + index);
					if (MyApplication.getMediaService().getTracks().size() == 1) {
						finish();
					} else {
						switch (doShuffle){
							case PLAY_ALL:
								// Processing on startPlay
								break;
							case PLAY_ALL_REPEAT:
								// Do nothing
								break;
							case PLAY_ONE_REPEAT:
								index = index - 1;
								break;
							case SHUFFLE_NORMAL:
								index = random();
								break;
						}
//						if(doShuffle.equals(PLAY_MODE.PLAY_ALL_REPEAT)){
//							index = random();
//						}
						startPlay();
					}
				}
			}
		}
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
            	Log.e("keyback", "full : " + isFullscreen);
				if(isFullscreen){
					player.setFullscreen(false);
				}else{
					finish();
					overridePendingTransition( R.anim.slide_up_rev, R.anim.slide_down_rev );
				}
				return true;
			}
        }
        return false;
    }

}
