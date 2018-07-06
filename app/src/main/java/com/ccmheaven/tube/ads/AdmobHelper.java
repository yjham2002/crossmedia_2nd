package com.ccmheaven.tube.ads;

import com.RKclassichaeven.tube.LogoActivity;
import com.ccmheaven.tube.pub.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import bases.SimpleCallback;
import comm.SimpleCall;

public class AdmobHelper {
	
	public InterstitialAd interstitial;
	public Context context;

	public AdmobHelper(Context context){
		this.context = context;
	}
	
	public void addAdView(LinearLayout layout){

    	final AdView adview = new AdView(context);
    	adview.setAdSize(AdSize.SMART_BANNER);
    	adview.setAdUnitId(Constants.ADMOB_KEY_BANNER);
		adview.setVisibility(View.GONE);
		adview.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				adview.setVisibility(View.VISIBLE);
			}
		});
        AdRequest adRequest = new AdRequest.Builder()
	 		//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	 		//.addTestDevice("1BC5DF7052976BDA26A695A6EA80308F")
        	.build();
		adview.loadAd(adRequest);
		
		// add it to the linearlayout
		layout.addView(adview);
	}

	public void addAdView(LinearLayout layout, AdSize adSize){

		final AdView adview = new AdView(context);
		adview.setAdSize(adSize);
		adview.setAdUnitId(Constants.ADMOB_KEY_BANNER);
		adview.setVisibility(View.GONE);
		adview.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				adview.setVisibility(View.VISIBLE);
			}
		});
		AdRequest adRequest = new AdRequest.Builder()
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				//.addTestDevice("1BC5DF7052976BDA26A695A6EA80308F")
				.build();
		adview.loadAd(adRequest);

		// add it to the linearlayout
		layout.addView(adview);
	}

	public void loadInterstitialAd(){

    	// Create the interstitial AD
        interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId(Constants.ADMOB_KEY_INTERSTITIAL);

        // Set the AdListener.
        interstitial.setAdListener(new AdListener() {
        	public void onAdLoaded() {
				// Call displayInterstitial() function
        		displayInterstitial();
			}
			@Override
			public void onAdClosed() {
		        LogoActivity.isStart = false;
				super.onAdClosed();
			}
		});

        // Request the AD
        AdRequest adRequest = new AdRequest.Builder()
            //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	 		//.addTestDevice("1BC5DF7052976BDA26A695A6EA80308F")
            .build();
        interstitial.loadAd(adRequest);

	}

	public void loadInterstitialAd(final SimpleCallback callback){

		// Create the interstitial AD
		interstitial = new InterstitialAd(context);
		interstitial.setAdUnitId(Constants.ADMOB_KEY_INTERSTITIAL);

		// Set the AdListener.
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				// Call displayInterstitial() function
				displayInterstitial();
			}
			@Override
			public void onAdClosed() {
				LogoActivity.isStart = false;
				super.onAdClosed();
				if(callback != null) callback.callback();
			}
		});

		// Request the AD
		AdRequest adRequest = new AdRequest.Builder()
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				//.addTestDevice("1BC5DF7052976BDA26A695A6EA80308F")
				.build();
		interstitial.loadAd(adRequest);

	}

	public void loadInterstitialAdWithOnLoad(final SimpleCallback callback, final SimpleCallback onLoad){

		// Create the interstitial AD
		interstitial = new InterstitialAd(context);
		interstitial.setAdUnitId(Constants.ADMOB_KEY_INTERSTITIAL);

		// Set the AdListener.
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				// Call displayInterstitial() function
				if(onLoad != null) onLoad.callback();
				displayInterstitial();
			}
			@Override
			public void onAdClosed() {
				LogoActivity.isStart = false;
				super.onAdClosed();
				if(callback != null) callback.callback();
			}
		});

		// Request the AD
		AdRequest adRequest = new AdRequest.Builder()
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				//.addTestDevice("1BC5DF7052976BDA26A695A6EA80308F")
				.build();
		interstitial.loadAd(adRequest);

	}

	public void displayInterstitial(){
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial != null && interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	
	
}
