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
import java.util.List;

public class InitDataThread extends Thread {
	public static boolean isConnect;
	private Handler handler;
	private List<ListInfo> list;
	public static InitDataThread intance;
	private int page;

	private InitDataThread(Handler handler, List<ListInfo> list, int page) {
		this.handler = handler;
		this.page = page;
		this.list = list;
		intance = this;
	}

	public static void startInitDataThread(Handler handler,
			List<ListInfo> list, int page) {
		if (intance == null) {
			intance = new InitDataThread(handler, list, page);
			intance.start();
		}
	}

	public void run() {
		URL url;
		InputStream inStream = null;
		ByteArrayOutputStream data = null;
		String datastr = null;
		try {
			url = new URL(Constants.API_VIDEO_LIST + "&page=" + page);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(6000);
			inStream = connection.getInputStream();
			data = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				data.write(buffer, 0, len);
			}
			datastr = new String(data.toByteArray(), "utf-8");
			/* json */
			JSONObject jsonobject = new JSONObject(datastr);
			JSONArray jsonarray = jsonobject.getJSONObject("result")
					.getJSONArray("list");
			if (page == 1) list.clear();
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject json = jsonarray.getJSONObject(i);
				ListInfo listinfo = new ListInfo();
				listinfo.setVideoName(json.getString("vd_title").replace("/",
						""));
				listinfo.setVideoCode(json.getString("vd_code"));
				listinfo.setImageUrl(json.getString("vd_thum_url"));
				listinfo.setVideoId(json.getString("vd_id"));
				listinfo.setArtistName(json.getString("vd_name"));
				listinfo.setVideoUrl(json.getString("vd_url"));
				listinfo.setRuntime(json.getString("vd_runtime"));
                listinfo.setViews(json.getString("views"));
				list.add(listinfo);
			}
			Message msg = Message.obtain();
			msg.what = Constants.INITDATA;
			msg.obj = datastr;
			handler.sendMessage(msg);
			isConnect = true;
		} catch (Exception e) {
			Message msg = Message.obtain();
			msg.what = Constants.INITDATA_LOSE;
			msg.obj = datastr;
			handler.sendMessage(msg);
			isConnect = false;
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