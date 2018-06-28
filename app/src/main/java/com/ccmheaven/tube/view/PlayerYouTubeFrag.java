package com.ccmheaven.tube.view;

import android.os.Bundle;

import com.ccmheaven.tube.pub.Constants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * Created by HP on 2018-06-28.
 */

public class PlayerYouTubeFrag extends YouTubePlayerSupportFragment {
    private String currentVideoID = "video_id";
    private YouTubePlayer activePlayer;

    public static PlayerYouTubeFrag newInstance(String url) {
        PlayerYouTubeFrag playerYouTubeFrag = new PlayerYouTubeFrag();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);

        return playerYouTubeFrag;
    }

    public void init() {
        initialize(Constants.YOUTUBE_DEV_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                if (!wasRestored) {
                    activePlayer.loadVideo(getArguments().getString("url"), 0);

                }
            }
        });
    }

}