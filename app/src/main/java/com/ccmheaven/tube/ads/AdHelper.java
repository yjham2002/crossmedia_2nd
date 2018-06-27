package com.ccmheaven.tube.ads;

import com.RKclassichaeven.tube.LogoActivity;
import com.google.android.gms.ads.AdSize;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.LinearLayout;

import bases.SimpleCallback;

/**
 * @author Barnabas
 *
 */
public class AdHelper {
	
	private String ad;
	private String ad_type;
	private String ad_interstital;
	private Activity activity;
	
	/**
	 * @param activity
	 */
	public AdHelper(Activity activity){
		this.activity = activity;
        SharedPreferences edit = activity.getSharedPreferences(LogoActivity.CONFIG_NAME, Context.MODE_PRIVATE);
        ad = edit.getString("ad", "Y");
        ad_type = edit.getString("ad_type", "adfit"); // 'admob' or 'adfit'
        ad_interstital = edit.getString("ad_interstital", "Y");
        
        Log.d("dev", "AdHelper.AdHelper() ad: " + ad + ", ad_type: " + ad_type + ", ad_interstital: " + ad_interstital);
	}

	public void addAdViewWithSize(LinearLayout layout, AdSize adSize){
		if ("Y".equals(ad)) {
			if("admob".equals(ad_type)){
				Log.d("dev", "AdHelper.addAdView() admob");
				new AdmobHelper(activity).addAdView(layout, adSize);
			}else if("adfit".equals(ad_type)){
				Log.d("dev", "AdHelper.addAdView() adfit");
				new AdfitHelper(activity).addAdView(layout);
			}
		}
	}

	/**
	 * @param layout
	 */
	public void addAdView(LinearLayout layout){
		if ("Y".equals(ad)) {
        	if("admob".equals(ad_type)){
        		Log.d("dev", "AdHelper.addAdView() admob");
        		new AdmobHelper(activity).addAdView(layout);
        	}else if("adfit".equals(ad_type)){
        		Log.d("dev", "AdHelper.addAdView() adfit");
        		new AdfitHelper(activity).addAdView(layout);
        	}
        }
	}
	
	/**
	 * 
	 */
	public void loadInterstitialAd(){
		if ("Y".equals(ad_interstital)) {
        	if("admob".equals(ad_type)){
        		new AdmobHelper(activity).loadInterstitialAd();
        	}else if("adfit".equals(ad_type)){
        		new AdfitHelper(activity).loadInterstitialAd();
        	}
        }
//		activity.finish();
	}

	public void loadInterstitialAd(SimpleCallback callback){
		if ("Y".equals(ad_interstital)) {
			if("admob".equals(ad_type)){
				new AdmobHelper(activity).loadInterstitialAd(callback);
			}else if("adfit".equals(ad_type)){
				new AdfitHelper(activity).loadInterstitialAd();
			}
		}
//		activity.finish();
	}
}
