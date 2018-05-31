package com.ccmheaven.tube.thread;

import android.os.Handler;
import android.os.Message;

import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.RecommendInfo;
import com.ccmheaven.tube.pub.SettingInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingDataThread extends Thread {

    private Handler handler;
    private SettingInfo info;
    private static SettingDataThread intance;

    private SettingDataThread(Handler handler,
                              SettingInfo info) {
        this.handler = handler;
        this.info = info;
        intance = this;
    }

    public static void startSettingDataThread(Handler handler,
                                              SettingInfo info) {
        if (intance == null) {
            intance = new SettingDataThread(handler, info);
            intance.start();
        }
    }

    public void run() {
        URL url;
        InputStream inStream = null;
        ByteArrayOutputStream data = null;
        String datastr = null;
        try {
            url = new URL(Constants.API_APP_CONFIG);
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
            JSONObject json = jsonobject.getJSONObject("result");
            info.setAppName(json.getString("app_name"));
            info.setAppVer(json.getString("app_ver"));
            info.setAdType(json.getString("adtype"));
            info.setBtnDownload(json.getString("btn_download"));
            info.setBtnYoutube(json.getString("btn_youtube"));
            info.setBtnAudio(json.getString("btn_audio"));
            info.setAdInterstital(json.getString("ad_interstital"));
            info.setFixVideoAd(json.getString("fix_video_ad"));
            info.setBtnAd(json.getString("btn_ad"));
            info.setHitKeyword(json.getString("hit_keyword"));
            info.setUpdateTime(json.getString("update_time"));
            info.setLatestUpdate(json.getString("latest_update"));
            info.setEmail(json.getString("email"));
            //info.setPlaystoreUrl(json.getString("playstore_url"));

            JSONArray jsonarray = json.getJSONArray("recommend");

            for (int i = 0; i < jsonarray.length(); i++) {
                json = jsonarray.getJSONObject(i);
                RecommendInfo recommendinfo = new RecommendInfo();
                recommendinfo.setSubject(json.getString("subject"));
                recommendinfo.setDescription(json.getString("description"));
                recommendinfo.setImg(json.getString("img"));
                recommendinfo.setLink(json.getString("link"));
                info.getRecommend().add(recommendinfo);
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
