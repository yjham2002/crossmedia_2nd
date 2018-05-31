package com.RKclassichaeven.tube;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.pub.Constants;

public class LogoActivity extends Activity {
	public static LogoActivity intance = null;
	public static final String CONFIG_NAME = "gospel_config";
	private static final int CONFIG_OK = 1;
	private static final int CONFIG_NO = 0;
	public static boolean isStart = false;

	/*****************************************************/
	private String download;
	private String audio;
	private String ad;
	private String ad_interstital;
	private String ad_fix;
	private String ad_type;

	private String adfit_banner;
	private String adfit_interstital;

	private int version;

	private void goTo() {
		Intent intent = new Intent(intance, CategoryActivity.class);
		startActivity(intent);
		isStart = true;
		intance.finish();
	}

	protected void onCreate(Bundle savedInstanceState) {
		new ConfigThread().start();
		Log.d("dev", "LogoActivity.onCreate() start");
		super.onCreate(savedInstanceState);
		if (!isStart) {
			intance = this;
			setContentView(R.layout.activity_logo);
		} else {
			finish();
		}
	}

	protected void onDestroy() {
		intance = null;
		super.onDestroy();
	}

	public int getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			Log.d("dev", Log.getStackTraceString(e));
			return 0;
		}
	}

	private class ConfigThread extends Thread {
		public void run() {
			ad = "N";
			ad_interstital = "N";
			audio = "Y";
			download = "Y";
			ad_fix = "N";
			URL url;
			InputStream inStream = null;
			ByteArrayOutputStream data = null;
			String datastr = null;
			HttpURLConnection connection = null;
			try {
				url = new URL(Constants.API_APP_CONFIG);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(3000);
				connection.setReadTimeout(3000);
				inStream = connection.getInputStream();
				data = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inStream.read(buffer)) != -1) {
					data.write(buffer, 0, len);
				}
				datastr = new String(data.toByteArray(), "utf-8");
				JSONObject object = new JSONObject(datastr).getJSONObject("result");
				download = object.getString("btn_download");
				audio = object.getString("btn_audio");
				ad = object.getString("btn_ad");
				ad_interstital = object.getString("ad_interstital");
				ad_type = object.getString("adtype");
				version = Integer.valueOf(object.getString("app_ver"));
				ad_fix = object.getString("fix_video_ad");

				adfit_banner = object.getString("adfit_banner");
				adfit_interstital = object.getString("adfit_interstital");

				Log.d("dev", "LogoActivity - adtype: " + ad_type + ", adfit_banner: " + adfit_banner + ", adfit_interstital: " + adfit_interstital);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				
				SharedPreferences edit = getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
				download = "N";
				ad_fix = "N";
				edit.edit().putString("download", download).commit();
				edit.edit().putString("audio", audio).commit();
				edit.edit().putString("ad", ad).commit();
				edit.edit().putString("ad_interstital", ad_interstital).commit();
				edit.edit().putString("ad_fix", ad_fix).commit();
				
				edit.edit().putString("ad_type", ad_type).commit();
				edit.edit().putString("adfit_banner", adfit_banner).commit();
				edit.edit().putString("adfit_interstital", adfit_interstital).commit();

				if (intance != null) {
					if (getVersion() < version) {

						new AlertDialog.Builder(intance)
						.setTitle("Remind")
						.setMessage("Find new update，update now?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Uri uri = Uri.parse("https://play.google.com/store");
								Intent it = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(it);
								finish();
							}
						})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								goTo();
							}
						}).show();
					} else {
						goTo();
					}
				}
				
				if (connection != null) {
					connection.disconnect();
				}
				try {
					if (inStream != null) { inStream.close(); }
					if (data != null) { data.close(); }
				} catch (IOException e) { }
			}
		}
	}
}
