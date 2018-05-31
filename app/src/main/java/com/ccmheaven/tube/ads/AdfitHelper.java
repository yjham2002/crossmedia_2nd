/**
 * 
 */
package com.ccmheaven.tube.ads;

import com.RKclassichaeven.tube.LogoActivity;
import com.ccmheaven.tube.pub.Constants;

import net.daum.adam.publisher.AdInterstitial;
import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;
import net.daum.adam.publisher.AdView.OnAdClickedListener;
import net.daum.adam.publisher.AdView.OnAdClosedListener;
import net.daum.adam.publisher.AdView.OnAdFailedListener;
import net.daum.adam.publisher.AdView.OnAdLoadedListener;
import net.daum.adam.publisher.AdView.OnAdWillLoadListener;
import net.daum.adam.publisher.impl.AdError;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author Barnabas
 *
 */
public class AdfitHelper {

	public Activity activity;
	public final static String LOGTAG = "dev";

	String ADFIT_BANNER_KEY = "";
	String ADFIT_INTERSTITAL_KEY = "";
	
	AdInterstitial mAdInterstitial = null;
	
	/**
	 * @param activity
	 */
	public AdfitHelper(Activity activity){
		this.activity = activity;
		// load info from SharedPreferences
		SharedPreferences edit = activity.getSharedPreferences(LogoActivity.CONFIG_NAME, Context.MODE_PRIVATE);
        ADFIT_BANNER_KEY = edit.getString("adfit_banner", Constants.ADFIT_DEFAULT_KEY_BANNER);
        ADFIT_INTERSTITAL_KEY = edit.getString("adfit_interstital", Constants.ADFIT_DEFAULT_KEY_INTERSTITIAL);
	}
	
	/**
	 * @param layout
	 */
	public void addAdView(LinearLayout layout){

		if(layout == null){ Log.d(LOGTAG, "AdfitHelper.addAdView() LinearLayout is null");}
		
		AdView adView = new AdView(activity);
        adView.setOnAdClickedListener(new OnAdClickedListener() {
            public void OnAdClicked() {
                Log.i(LOGTAG, "Adfit.OnAdClicked");
            }
        });
        adView.setOnAdFailedListener(new OnAdFailedListener() {
            public void OnAdFailed(AdError arg0, String arg1) {
                Log.w(LOGTAG, "Adfit.OnAdFailed: " + arg1);
            }
        });
        adView.setOnAdLoadedListener(new OnAdLoadedListener() {
            public void OnAdLoaded() {
                Log.i(LOGTAG, "Adfit.OnAdLoaded");
            }
        });
        adView.setOnAdWillLoadListener(new OnAdWillLoadListener() {
            public void OnAdWillLoad(String arg1) {
                Log.i(LOGTAG, "Adfit.OnAdWillLoad: " + arg1);
            }
        });
        adView.setOnAdClosedListener(new OnAdClosedListener() {
            public void OnAdClosed() {
                Log.i(LOGTAG, "Adfit.OnAdClosed");
            }
        });
        
        adView.setClientId(ADFIT_BANNER_KEY);
        adView.setRequestInterval(12);
        adView.setAnimationType(AnimationType.FLIP_HORIZONTAL);
        adView.setVisibility(View.VISIBLE);
        
        // add adView into the layout
		layout.addView(adView);
	}
	
	public void loadInterstitialAd(){
		
		Log.d(LOGTAG, "Adfit.loadInterstitialAd. start");
        mAdInterstitial = new AdInterstitial(activity);
        mAdInterstitial.setClientId(ADFIT_INTERSTITAL_KEY);
        mAdInterstitial.setOnAdLoadedListener(new OnAdLoadedListener() {
            public void OnAdLoaded() { 
            	Log.d(LOGTAG, "AdInterstitial.OnAdLoaded");
            }
        });
        mAdInterstitial.setOnAdFailedListener(new OnAdFailedListener() {
            public void OnAdFailed(AdError error, String errorMessage) { 
            	Log.d(LOGTAG, "AdInterstitial.OnAdFailed");
            }
        });
        mAdInterstitial.loadAd();
	}
	
}
