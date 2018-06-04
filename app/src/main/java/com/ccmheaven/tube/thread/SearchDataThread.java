package com.ccmheaven.tube.thread;

import android.os.Handler;
import android.os.Message;

import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.ListInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchDataThread extends Thread {
	private Handler handler;
	private List<ListInfo> list;
	private String title;
	private static SearchDataThread intance;

	private SearchDataThread(Handler handler, List<ListInfo> list, String title) {
		this.handler = handler;
		this.list = list;
		this.title = title;
		intance = this;
	}

	public static void startSearchDataThread(Handler handler,
			List<ListInfo> list, String title) {
		if (intance == null) {
			intance = new SearchDataThread(handler, list, title);
			intance.start();
		}
	}

	public void run() {
		URL url;
		InputStream inStream = null;
		ByteArrayOutputStream data = null;
		String datastr = null;
		try {
			title = URLEncoder.encode( title , "UTF-8" );
			url = new URL(Constants.API_VIDEO_LIST + "&sh_fld=title&sh_txt=" + title);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(6000);
			inStream = connection.getInputStream();
			data = new ByteArrayOutputStream();// 劤쉔寧俚쌘鑒莉渴놔직
			byte[] buffer = new byte[1024];// 瞳코닸櫓역근寧뙈뻠녑혐，쌈肝貢쭹渴흙직
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				data.write(buffer, 0, len); // 뻠녑혐찮죄裂빈쉥뻠녑혐돨코휭畇돕渴놔직
			}
			datastr = new String(data.toByteArray(), "utf-8");
			/* json */

			Message msg = Message.obtain();
			msg.obj = datastr;
			msg.what = Constants.INITDATA;
			handler.sendMessage(msg);
			InitDataThread.isConnect = true;
		} catch (Exception e) {
			Message msg = Message.obtain();
			msg.what = Constants.INITDATA_LOSE;
			handler.sendMessage(msg);
			InitDataThread.isConnect = false;
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (data != null) {
					data.close();
				}
			} catch (IOException e) {
			}
		}
		intance = null;
	}
}
