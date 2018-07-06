package com.ccmheaven.tube.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.RKclassichaeven.tube.CategoryActivity;
import com.RKclassichaeven.tube.MyApplication;
import com.RKclassichaeven.tube.MyPageActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.SearchActivity;
import com.RKclassichaeven.tube.YoutubePlayerActivity;
import com.RKclassichaeven.tube.models.SyncInfo;
import com.RKclassichaeven.tube.services.MediaService;
import com.ccmheaven.tube.ads.AdHelper;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

import bases.Constants;
import bases.SimpleCallback;
import comm.SimpleCall;

public class BottomView extends LinearLayout {

	private View view, hider, wrap;
    private LinearLayout llAdview;
    private LinearLayout llyLank, llyCategory, llySearch;
//    private LinearLayout llyLank, llyCategory, llySearch, llyMyList;
//    private ImageView rank, category, search, myList;
//    private ImageView rank, category, search;
    private TextView tvRank, tvCategory, tvSearch, tvMyList;
	private Activity activity;
	private YouTubePlayerView youTubePlayerView;
	private YouTubePlayer actualPlayer;
	AdHelper adHelper;

	private TextView title, sub;
	private ImageView next, prev, play, pause;

	/**
	 * @param activity
	 */
	public void setActivity(Activity activity){
		this.activity = activity;
		if(this.activity != null){
			adHelper = new AdHelper(this.activity);
		}
	}

	public static int getScreenWidth() {
		return Resources.getSystem().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight() {
		return Resources.getSystem().getDisplayMetrics().heightPixels;
	}

	public static boolean isVisible(final View view) {
		if (view == null) {
			return false;
		}
		if (!view.isShown()) {
			return false;
		}
		final Rect actualPosition = new Rect();
		view.getGlobalVisibleRect(actualPosition);
		final Rect screen = new Rect(0, 0, getScreenWidth(), getScreenHeight());
		return actualPosition.intersect(screen);
	}

	private void refreshPlayer(){
		SyncInfo syncInfo = MyApplication.getMediaService().getSyncInfo();
		displayPlaybar();
		if(actualPlayer != null){
			if(syncInfo.getState() != SyncInfo.STATE_RELEASE){
				hider.setBackgroundColor(getResources().getColor(R.color.transparent));
				if(syncInfo.getState() == SyncInfo.STATE_PLAY) {
					if(syncInfo.getCurrentScene() == SyncInfo.SCENE_BOTTOM) actualPlayer.loadVideo(syncInfo.getVideoId(), syncInfo.getCurrentTime());
				} else {
					if(syncInfo.getCurrentScene() == SyncInfo.SCENE_BOTTOM) actualPlayer.cueVideo(syncInfo.getVideoId(), syncInfo.getCurrentTime());
				}
			}else{
				hider.setBackgroundColor(getResources().getColor(R.color.jet));
			}

		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);

		Log.e("BottomView", "onWindowFocusChanged : " + hasWindowFocus);

		if (hasWindowFocus) {
			//onresume() called
		} else {
			// onPause() called
		}
	}

	public void onActivityResume(){
		MyApplication.getMediaService().getSyncInfo().setCurrentScene(SyncInfo.SCENE_BOTTOM);
		refreshPlayer();
		MyApplication.getMediaService().setSimpleCallback(new SimpleCallback() {
			@Override
			public void callback() {
				refreshPlayer();
			}
		});

		MyApplication.getMediaService().setAffinityCall(new SimpleCallback() {
			@Override
			public void callback() {
				Log.e("Affinity", "Pre-Called");
				if(BottomView.this.activity != null) {
					ActivityCompat.finishAffinity(BottomView.this.activity);
					Log.e("Affinity", "Called");
				}
			}
		});
	}

	public void onActivityPause(){
		if(actualPlayer != null) actualPlayer.pause();
	}

	private void displayPlaybar(){
		SyncInfo s = MyApplication.getMediaService().getSyncInfo();
		title.setText(s.getTitle());
		sub.setText(s.getAuthor());
		if(s.getState() == SyncInfo.STATE_PLAY){
			play.setVisibility(View.INVISIBLE);
			pause.setVisibility(View.VISIBLE);
		}else{
			play.setVisibility(View.VISIBLE);
			pause.setVisibility(View.INVISIBLE);
		}
	}

	private View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			MediaService mediaService = MyApplication.getMediaService();
			SyncInfo syncInfo = mediaService.getSyncInfo();
			final Intent intent = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
			switch (view.getId()){
				case R.id.bot_next:
					intent.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_NEXT);
					getContext().sendBroadcast(intent);
					if(mediaService.getTracks().size() > 0){
//						nextSong();
					}else{
//						Toast.makeText(getContext(), "재생 곡을 추가해주세요", Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.bot_prev:
					intent.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_PREV);
					getContext().sendBroadcast(intent);
					if(mediaService.getTracks().size() > 0){
//						prevSong();
					}else{
//						Toast.makeText(getContext(), "재생 곡을 추가해주세요", Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.bot_play:
					if(newListCall != null){
						newListCall.callback();
					}
					intent.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_PLAY);
					getContext().sendBroadcast(intent);
					if(mediaService.getTracks().size() > 0){
//						syncInfo.setPlayState();
//						refreshPlayer();
					}else{
//						Toast.makeText(getContext(), "재생 곡을 추가해주세요", Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.bot_pause:
					intent.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_STOP);
					getContext().sendBroadcast(intent);
					if(mediaService.getTracks().size() > 0){
//						syncInfo.setPauseState();
//						refreshPlayer();
					}else{
//						Toast.makeText(getContext(), "재생 곡을 추가해주세요", Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.mainWrap:
					Intent intentYT = new Intent(getContext(), YoutubePlayerActivity.class);
					intentYT.putExtra("direct", true);
					getContext().startActivity(intentYT);
					((Activity)getContext()).overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
					break;
				default: break;
			}
		}
	};

	private SimpleCallback newListCall;

	public void setNewListCall(SimpleCallback newListCall) {
		this.newListCall = newListCall;
	}

	private void setOnClickListener(View.OnClickListener onClickListener, View... views){
		for(View view : views) {
			if(view != null) view.setOnClickListener(onClickListener);
		}
	}

	private void nextSong(){
		final Intent intent = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
		intent.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_NEXT);
		getContext().sendBroadcast(intent);
	}

	private void prevSong(){
		final Intent intent = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
		intent.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_PREV);
		getContext().sendBroadcast(intent);
	}

	private boolean mStatusBarShown;

	@Override
	protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		Log.e("BottomView", "onVisibilityChanged : " + (visibility == View.VISIBLE));
		if (visibility == View.VISIBLE) {
			//onResume called
		}
    	else {
			// onPause() called
		}
	}

    /**
     * @param context
     * @param attrs
     */
    public BottomView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_bottom, this);
        hider = view.findViewById(R.id.hider);

        youTubePlayerView = view.findViewById(R.id.iv_photo);

		title = view.findViewById(R.id.tv_name);
		sub = view.findViewById(R.id.tv_category);
		play = view.findViewById(R.id.bot_play);
		next = view.findViewById(R.id.bot_next);
		prev = view.findViewById(R.id.bot_prev);
		pause = view.findViewById(R.id.bot_pause);
		wrap = view.findViewById(R.id.mainWrap);

		title.setSelected(true);
		sub.setSelected(true);

//		View decorView = ((Activity)getContext()).getWindow().getDecorView();
//		decorView.setOnSystemUiVisibilityChangeListener
//				(new View.OnSystemUiVisibilityChangeListener() {
//					@Override
//					public void onSystemUiVisibilityChange(int visibility) {
//						// Note that system bars will only be "visible" if none of the
//						// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
//						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//							// TODO: The system bars are visible. Make any desired
//							// adjustments to your UI, such as showing the action bar or
//							// other navigational controls.
//							mStatusBarShown = true;
//							Log.e("BottomView", "onSystemUiVisibilityChange : " + mStatusBarShown);
//
//						} else {
//							// TODO: The system bars are NOT visible. Make any desired
//							// adjustments to your UI, such as hiding the action bar or
//							// other navigational controls.
//							mStatusBarShown = false;
//							Log.e("BottomView", "onSystemUiVisibilityChange : " + mStatusBarShown);
//						}
//					}
//				});

		setOnClickListener(onClickListener, play, next, prev, pause, wrap);

        youTubePlayerView.setEnabled(false);
		youTubePlayerView.initialize(new YouTubePlayerInitListener() {
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
								if(MyApplication.getMediaService().getTracks().size() == 0) {
									MyApplication.getMediaService().getSyncInfo().release();
									refreshPlayer();
									return;
								}
								if(state == PlayerConstants.PlayerState.ENDED){
									if(MyApplication.getMediaService().getSyncInfo().getState() == SyncInfo.STATE_PLAY){
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
								nextSong();
							}

							@Override
							public void onApiChange() {

							}

							@Override
							public void onCurrentSecond(float second) {
								MyApplication.getMediaService().getSyncInfo().setCurrentTime(second);
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
						refreshPlayer();
					}

					@Override
					public void onStateChange(int state) {
						super.onStateChange(state);
					}
				});
			}
		}, true);
        // ad
        llAdview = (LinearLayout) view.findViewById(R.id.llAdview);
    }

    public void addAdView(){
    	Log.d("dev", "BottomView.addAdView() entered");
    	if(adHelper != null && llAdview != null){
    		Log.d("dev", "BottomView.addAdView() call adAdView()");
    		adHelper.addAdView(llAdview);
    	}
    }

}
