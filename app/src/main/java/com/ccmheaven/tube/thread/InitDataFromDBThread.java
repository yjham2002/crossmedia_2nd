package com.ccmheaven.tube.thread;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.ccmheaven.tube.db.MyMusicDB;
import com.ccmheaven.tube.db.MyMusicDownDB;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.ListInfo;

import java.util.List;

public class InitDataFromDBThread extends Thread {
	public static final int FROM_DOWNDB = 1;
	public static final int FROM_PAGEDB = 0;
	/***************************************************/
	private Handler handler;
	private static InitDataFromDBThread intance;
	private List<ListInfo> list;
	private MyMusicDB myMusicDB;
	private MyMusicDownDB myMusicDownDB;
	private int select;
	private String name;

	private InitDataFromDBThread(Context context, Handler handler,
			List<ListInfo> list, int sel) {
		this.handler = handler;
		this.list = list;
		select = sel;
		name = null;
		myMusicDB = new MyMusicDB(context);
		myMusicDownDB = new MyMusicDownDB(context);
	}

	private InitDataFromDBThread(Context context, Handler handler,
			List<ListInfo> list, String name) {
		this.handler = handler;
		this.list = list;
		this.name = name;
		myMusicDownDB = new MyMusicDownDB(context);
	}

	public static void startInitDataFromDBThread(Context context,
			Handler handler, List<ListInfo> list, int sel) {
		if (intance == null) {
			intance = new InitDataFromDBThread(context, handler, list, sel);
			intance.start();
		}
	}

	public static void startInitDataFromDBThread(Context context,
			Handler handler, List<ListInfo> list, String name) {
		if (intance == null) {
			intance = new InitDataFromDBThread(context, handler, list, name);
			intance.start();
		}
	}

	public void run() {
		try {
			list.clear();
			List<ListInfo> contacts;
			if (name == null) {
				if (select == 0) {
					contacts = myMusicDB.getAllContacts();
				} else {
					contacts = myMusicDownDB.getAllContacts();
				}
			} else {
				contacts = myMusicDownDB.getCotacts(name);
			}
			list.addAll(contacts);
			handler.sendEmptyMessage(Constants.INITDATA);
		} catch (Exception e) {
			Log.d("dev", Log.getStackTraceString(e));
			handler.sendEmptyMessage(Constants.INITDATA);
		}
		intance = null;
	}
}
