package com.ccmheaven.tube.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import com.ccmheaven.tube.ads.AdHelper;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

public class BottomView extends LinearLayout {

	private View view, hider;
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

	private void refreshPlayer(){
		SyncInfo syncInfo = MyApplication.getMediaService().getSyncInfo();
		displayPlaybar();
		if(actualPlayer != null){
			if(syncInfo.getState() != SyncInfo.STATE_RELEASE){
				hider.setBackgroundColor(getResources().getColor(R.color.transparent));
				if(syncInfo.getState() == SyncInfo.STATE_PLAY) {
					actualPlayer.loadVideo(syncInfo.getVideoId(), syncInfo.getCurrentTime());
				} else {
					actualPlayer.cueVideo(syncInfo.getVideoId(), syncInfo.getCurrentTime());
				}
			}else{
				hider.setBackgroundColor(getResources().getColor(R.color.jet));
			}

		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			//onresume() called
			refreshPlayer();
		} else {
			// onPause() called
		}
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
			switch (view.getId()){
				case R.id.bot_next:
					break;
				case R.id.bot_prev:
					break;
				case R.id.bot_play:
					break;
				case R.id.bot_pause:
					break;
				default: break;
			}
		}
	};

	private void setOnClickListener(View.OnClickListener onClickListener, View... views){
		for(View view : views) {
			if(view != null) view.setOnClickListener(onClickListener);
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

		setOnClickListener(onClickListener, play, next, prev, pause);

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
										final int nextIndex = MyApplication.getMediaService().getSyncInfo().getCurrentIndex() + 1 >= MyApplication.getMediaService().getTracks().size() - 1
												?  0 : MyApplication.getMediaService().getSyncInfo().getCurrentIndex() + 1;
										MyApplication.getMediaService().getSyncInfo().setCurrentIndex(nextIndex);
										MyApplication.getMediaService().getSyncInfo().setBySong(MyApplication.getMediaService().getTracks().get(nextIndex));
										refreshPlayer();
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
