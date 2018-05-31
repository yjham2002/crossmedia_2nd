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

public class ArtistCategoryChildDataThread extends Thread {
	private Handler handler;
	private List<ListInfo> list;
	private String cgid;
	public static ArtistCategoryChildDataThread intance;
	private int page;

	private ArtistCategoryChildDataThread(Handler handler, List<ListInfo> list,
			String cgid, int page) {
		this.handler = handler;
		this.list = list;
		this.cgid = cgid;
		this.page = page;
	}

	public static void startArtistCategoryChildDataThread(Handler handler,
			List<ListInfo> list, String cgid, int page) {
		if (intance == null) {
			intance = new ArtistCategoryChildDataThread(handler, list, cgid,
					page);
			intance.start();
		}
	}

	public void run() {
		URL url;
		InputStream inStream = null;
		ByteArrayOutputStream data = null;
		String datastr = null;
		try {
			url = new URL(Constants.API_VIDEO_LIST + "&cg_id=" + cgid + "&page=" + page);
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
			if (page == 1)
				list.clear();
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
				list.add(listinfo);
			}
			Message msg = Message.obtain();
			msg.what = Constants.INITDATA;
			msg.obj = datastr;
			handler.sendMessage(msg);
			InitDataThread.isConnect = true;
		} catch (Exception e) {
			Message msg = Message.obtain();
			msg.what = Constants.INITDATA_LOSE;
			msg.obj = datastr;
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
