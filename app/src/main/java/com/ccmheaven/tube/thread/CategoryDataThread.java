package com.ccmheaven.tube.thread;

import android.os.Handler;
import android.os.Message;

import com.ccmheaven.tube.pub.CategoryListInfo;
import com.ccmheaven.tube.pub.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CategoryDataThread extends Thread {
	
	private Handler handler;
	private List<CategoryListInfo> list;
	private static CategoryDataThread intance;

	private CategoryDataThread(Handler handler, List<CategoryListInfo> list) {
		this.handler = handler;
		this.list = list;
		intance = this;
	}

	public static void startCategoryDataThread(Handler handler,
			List<CategoryListInfo> list) {
		if (intance == null) {
			intance = new CategoryDataThread(handler, list);
			intance.start();
		}
	}

	public void run() {
		URL url;
		InputStream inStream = null;
		ByteArrayOutputStream data = null;
		String datastr = null;
		try {
			url = new URL(Constants.API_CATEGORY_2013);
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
			JSONArray jsonarray = jsonobject.getJSONArray("result");
			list.clear();
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject json = jsonarray.getJSONObject(i);
				CategoryListInfo listinfo = new CategoryListInfo();
				listinfo.setName(json.getString("cg_name"));
				listinfo.setCgid(json.getString("cg_id"));
				listinfo.setImageUrl(json.getString("cg_image_url"));
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
