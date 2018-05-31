package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.ccmheaven.tube.ads.AdHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;


public class ExitDialog extends Dialog implements View.OnClickListener {

    private static String LOG_TAG = "EXAMPLE";

    private AdView mAdView;
//    VideoController mVideoController;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exit_confirm:
                m_listener.onConfirm();
                break;

            case R.id.tv_exit_cancel:
                m_listener.onCancel();
                break;
        }
    }

    public interface ExitDialogListener {
        void onConfirm();

        void onCancel();
    }

    private ExitDialogListener m_listener;

    public ExitDialog(Context context, ExitDialogListener listener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_exit);

        m_listener = listener;

        findViewById(R.id.tv_exit_confirm).setOnClickListener(this);
        findViewById(R.id.tv_exit_cancel).setOnClickListener(this);

        // native banner
//        mAdView = findViewById(R.id.adView);
        LinearLayout llAdview = findViewById(R.id.llAdview);
        AdHelper adHelper = new AdHelper((Activity)context);
        if(adHelper != null && llAdview != null){
            Log.d("dev", "BottomView.addAdView() call adAdView()");
            adHelper.addAdViewWithSize(llAdview, AdSize.MEDIUM_RECTANGLE);
        }

//        mAdView.setVideoOptions(new VideoOptions.Builder()
//                .setStartMuted(true)
//                .build());

//        mVideoController = mAdView.getVideoController();
//        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//            @Override
//            public void onVideoEnd() {
//                Log.d(LOG_TAG, "Video playback is finished.");
//                super.onVideoEnd();
//            }
//        });
//
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                if (mVideoController.hasVideoContent()) {
//                    Log.d(LOG_TAG, "Received an ad that contains a video asset.");
//                } else {
//                    Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
//                }
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                Log.d("onAdFailedToLoad", String.format("%d",  i));
//            }
//
//        });
//
//        mAdView.loadAd(new AdRequest.Builder().build());

    }
}
