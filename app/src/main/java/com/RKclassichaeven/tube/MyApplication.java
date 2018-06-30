package com.RKclassichaeven.tube;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;

import com.RKclassichaeven.tube.services.MediaService;

import net.khirr.library.foreground.Foreground;

import bases.BaseApp;
import bases.Constants;

public class MyApplication extends MultiDexApplication {
	
//	public static GoogleAnalytics analytics;
//	public static Tracker tracker;

	/*
	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(context);
		MultiDex.install(this);
	}
*/
	private static MediaService mediaService;

	public void onCreate() {
		super.onCreate();

		Foreground.Companion.init(this);

		BaseApp.context = this;
		final Intent backgroundIntentCall = new Intent(getBaseContext(), MediaService.class);
		bindService(backgroundIntentCall, mConnection, BIND_AUTO_CREATE);
		// Parse?
//		Parse.initialize(
//			this,
//			"vVWdbanhhk3rNAGrfYey5mY4uTw97vGIw0OB30MM",
//			"cx0uIBrS5orgdThPVocRmmCUAfl5JUhsyq1726xR"
//		);
//		PushService.setDefaultPushCallback(this, LogoActivity.class);
//		ParseInstallation.getCurrentInstallation().saveInBackground();
		
		// Google Analytics
//		analytics = GoogleAnalytics.getInstance(this);
//	    analytics.setLocalDispatchPeriod(1800);
//	    tracker = analytics.newTracker(getString(R.string.ga_trackingId)); // Replace with actual tracker/property Id
//	    tracker.enableExceptionReporting(true);
//	    tracker.enableAdvertisingIdCollection(true);
//	    tracker.enableAutoActivityTracking(true);
	}

	private boolean mBounded = false;

	public static MediaService getMediaService(){
		return mediaService;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mediaService = null;
			mBounded = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBounded = true;
			MediaService.LocalBinder mLocalBinder = (MediaService.LocalBinder)service;
			mediaService = mLocalBinder.getServiceInstance();
			final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
			activityIntent1.putExtra("action", "refresh");
			sendBroadcast(activityIntent1);
		}
	};

	@Override
	public void onTerminate(){
//        unbindService(mConnection);
		super.onTerminate();
	}

}
