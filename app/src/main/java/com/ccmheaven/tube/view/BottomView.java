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
import com.RKclassichaeven.tube.MyPageActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.SearchActivity;
import com.RKclassichaeven.tube.YoutubePlayerActivity;
import com.ccmheaven.tube.ads.AdHelper;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
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
	AdHelper adHelper;
	
	/**
	 * @param activity
	 */
	public void setActivity(Activity activity){
		this.activity = activity;
		if(this.activity != null){
			adHelper = new AdHelper(this.activity);
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
        youTubePlayerView.setEnabled(false);
		youTubePlayerView.initialize(new YouTubePlayerInitListener() {
			@Override
			public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
				initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
					@Override
					public void onReady() {
						String videoId = "xRbPAVnqtcs";
						initializedYouTubePlayer.loadVideo(videoId, 0);
					}

					@Override
					public void onStateChange(int state) {
						if(state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED){
							hider.setBackgroundColor(getResources().getColor(R.color.transparent));
						}else{
							hider.setBackgroundColor(getResources().getColor(R.color.jet));
						}
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
