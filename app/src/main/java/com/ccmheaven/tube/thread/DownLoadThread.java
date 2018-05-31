package com.ccmheaven.tube.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.RKclassichaeven.tube.DownLoadActivity;
import com.ccmheaven.tube.db.MyMusicDownDB;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.ListInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadThread extends Thread {
	private String path = Environment.getExternalStorageDirectory().getPath();
	private ListInfo listinfo;
	private Handler handler;
	private MyMusicDownDB mymusicdowndb;

	public DownLoadThread(Context context, Handler handler, ListInfo listinfo) {
		this.listinfo = listinfo;
		this.handler = handler;
		mymusicdowndb = new MyMusicDownDB(context);
	}

	/**
	 * public void stopThread() { isStop = true; }
	 */
	public void run() {
		try {
			sleep(1000);
		} catch (InterruptedException e2) {
		}
		String str2 = null;
		File file = null;
		InputStream inputStream = null;
		OutputStream out = null;
		URL url = null;
		URLConnection connect = null;
		System.setProperty("http.keepAlive", "false");
		try {
			HttpURLConnection localHttpURLConnection = (HttpURLConnection) new URL(
					"http://m.youtube.com/watch?v=" + listinfo.getVideoCode() + "&xl=xl_blazer&ajax=1&tsp=1&tspv=v2&xl=xl_blazer")
					.openConnection();

			// Set the timeout 10 seconds
			localHttpURLConnection.setConnectTimeout(8000);
			localHttpURLConnection.setReadTimeout(10000);
			localHttpURLConnection.setRequestMethod("GET");
			InputStreamReader localInputStreamReader = new InputStreamReader(
					localHttpURLConnection.getInputStream());
			StringBuilder localStringBuilder = new StringBuilder();
			int i3 = localInputStreamReader.read();
			while (!DownLoadActivity.isStop) {
				if (i3 == -1) {
					str2 = localStringBuilder.toString();
					break;
				}
				char c1 = (char) i3;
				i3 = localInputStreamReader.read();
				localStringBuilder.append(c1);
			}
			localHttpURLConnection.disconnect();
			if (!DownLoadActivity.isStop && str2 != null) {
				if (str2.startsWith(")]}'")) {
					str2 = str2.substring(4);
				}
				JSONArray localJSONArray = new JSONObject(str2)
						.getJSONObject("content").getJSONObject("player_data")
						.getJSONArray("fmt_stream_map");
				JSONObject object = localJSONArray.getJSONObject(0);
				String urlstr = object.getString("url");
				File filepath = new File(path + "/gospel/down/");
				if (!filepath.exists()) {
					filepath.mkdirs();
				}
				file = new File(path + "/gospel/down/",
						listinfo.getArtistName() + "_"
								+ listinfo.getVideoName() + ".dat");
				file.createNewFile();
				url = new URL(urlstr);
				connect = url.openConnection();
				connect.setConnectTimeout(8000);
				connect.setReadTimeout(10000);
				connect.connect();
				inputStream = connect.getInputStream();
				out = new FileOutputStream(file);
				Message message = Message.obtain();
				message.what = DownLoadActivity.InitName;
				message.obj = listinfo.getVideoName();
				DownLoadActivity.prohandler.sendMessage(message);
				byte buf[] = new byte[16384];
				int downcomple = 0, downloadedint = 0, tdDownlength = connect
						.getContentLength();
				while (!DownLoadActivity.isStop) {
					int numread = 0;
					numread = inputStream.read(buf);
					if (numread <= 0)
						break;
					out.write(buf, 0, numread);
					downcomple = downcomple + numread;
					downloadedint = ((int) (((float) downcomple / tdDownlength * 100)));
					Message msg = Message.obtain();
					msg.what = DownLoadActivity.Progressbarsh;
					msg.obj = downloadedint;
					DownLoadActivity.prohandler.sendMessage(msg);
				}
				out.flush();
				if (!DownLoadActivity.isStop) {
					if (file.isFile()) {
						file.renameTo(new File(path + "/gospel/down/"
								+ listinfo.getArtistName() + "_"
								+ listinfo.getVideoName() + ".mp4"));
					}
					mymusicdowndb.addContact(listinfo);
				} else {
					if (file != null && file.exists()) {
						file.delete();
					}
					handler.sendEmptyMessage(Constants.DOWNLOADNO);
				}
			} else {
				handler.sendEmptyMessage(Constants.DOWNLOADNO);
			}
		} catch (Exception e) {
			if (file != null && file.exists()) {
				file.delete();
			}
			handler.sendEmptyMessage(Constants.DOWNLOADNO);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e1) {
			}
		}
		handler.sendEmptyMessage(Constants.INITDATA);
		DownLoadActivity.prohandler.sendEmptyMessage(DownLoadActivity.End);
		if (!DownLoadActivity.isStop) {
			/* 苟潼暠튬 */
			loadImageFromUrl(listinfo.getImageUrl());
		}
	}

	private void loadImageFromUrl(String url) {
		if (url == null) {
			return;
		}
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			Bitmap d = BitmapFactory.decodeStream(i);
			storeInSD(d);
		} catch (MalformedURLException e1) {
		} catch (Exception e) {
		}
	}

	private void storeInSD(Bitmap bitmap) {
		File file = new File(path + "/gospel/down/imgdata/");
		if (!file.exists()) {
			file.mkdirs();
		}
		File imageFile = new File(file, listinfo.getArtistName() + "_"
				+ listinfo.getVideoName() + ".jpg");
		FileOutputStream fos = null;
		try {
			imageFile.createNewFile();
			fos = new FileOutputStream(imageFile);
			bitmap.compress(CompressFormat.JPEG, 50, fos);
			fos.flush();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
