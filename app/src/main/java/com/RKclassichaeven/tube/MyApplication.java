package com.RKclassichaeven.tube;

import com.RKclassichaeven.tube.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends Application {
	
	public static GoogleAnalytics analytics;
	public static Tracker tracker;

	/*
	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(context);
		MultiDex.install(this);
	}
*/
	public void onCreate() {
		super.onCreate();

		// Parse?
//		Parse.initialize(
//			this,
//			"vVWdbanhhk3rNAGrfYey5mY4uTw97vGIw0OB30MM",
//			"cx0uIBrS5orgdThPVocRmmCUAfl5JUhsyq1726xR"
//		);
//		PushService.setDefaultPushCallback(this, LogoActivity.class);
//		ParseInstallation.getCurrentInstallation().saveInBackground();
		
		// Google Analytics
		analytics = GoogleAnalytics.getInstance(this);
	    analytics.setLocalDispatchPeriod(1800);
	    tracker = analytics.newTracker(getString(R.string.ga_trackingId)); // Replace with actual tracker/property Id
	    tracker.enableExceptionReporting(true);
	    tracker.enableAdvertisingIdCollection(true);
	    tracker.enableAutoActivityTracking(true);
	}

}
