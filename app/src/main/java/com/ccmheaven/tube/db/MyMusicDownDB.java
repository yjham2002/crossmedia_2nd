package com.ccmheaven.tube.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.ccmheaven.tube.pub.ListInfo;

public class MyMusicDownDB extends SQLiteOpenHelper {
	private String path = Environment.getExternalStorageDirectory().getPath();
	private final String TableName = "MyMusic";
	private final String VideoId = "VideoId";
	private final String VideoCode = "VideoCode";
	private final String VideoUrl = "VideoUrl";
	private final String Music = "Music";
	private final String Artist = "Artist";
	private final String ImageUrl = "ImageUrl";

	public MyMusicDownDB(Context context) {
		super(context, "MyMusicDownDB", null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TableName
				+ "(id INTEGER PRIMARY KEY," + VideoId + " TEXT," + Music
				+ " TEXT," + Artist + " TEXT," + ImageUrl + " TEXT,"
				+ VideoCode + " TEXT," + VideoUrl + " TEXT)");
		List<ContentValues> values = initData();
		for (int i = 0; i < values.size(); i++) {
			db.insert(TableName, null, values.get(i));
		}
	}

	private List<ContentValues> initData() {
		List<ContentValues> list = new ArrayList<ContentValues>();
		File file = new File(path + "/gospel/down/");
		if (file.exists()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					String name = files[i].getName();
					int index = name.indexOf(".mp4");
					if (index != -1) {
						name = name.substring(0, index);
						String[] names = name.split("_");
						if (names.length < 2) {
							continue;
						} else {
							if (names.length > 2) {
								names[1] = name.substring(
										name.indexOf("_") + 1, name.length());
							}
							ContentValues values = new ContentValues();
							values.put(Music, names[1]);
							values.put(Artist, names[0]);
							list.add(values);
						}
					}
				}
			}
		}
		return list;
	}

	public int getVideoId(String paramString) {
		SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
		String str = "SELECT " + VideoId + " FROM " + TableName + " Where "
				+ VideoId + "='" + paramString + "'";
		Cursor localCursor = localSQLiteDatabase.rawQuery(str, null);
		int i = localCursor.getCount();
		localCursor.close();
		localSQLiteDatabase.close();
		return i;
	}

	public int getContactsCount() {
		Cursor localCursor = getReadableDatabase().rawQuery(
				"SELECT  * FROM " + TableName, null);
		int i = localCursor.getCount();
		localCursor.close();
		return i;
	}

	public void addContact(ListInfo listinfo) {
		String str1 = listinfo.getVideoId();
		SQLiteDatabase localSQLiteDatabase;
		if (getVideoId(str1) == 0) {
			localSQLiteDatabase = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(VideoId, listinfo.getVideoId());
			values.put(Music, listinfo.getVideoName());
			values.put(Artist, listinfo.getArtistName());
			values.put(VideoCode, listinfo.getVideoCode());
			values.put(VideoUrl, listinfo.getVideoUrl());
			values.put(ImageUrl, listinfo.getImageUrl());
			localSQLiteDatabase.insert(TableName, null, values);
			localSQLiteDatabase.close();
		}
	}

	public List<ListInfo> getCotacts(String name) {
		ArrayList<ListInfo> list = new ArrayList<ListInfo>();
		String str1 = null;
		str1 = "SELECT  * FROM " + TableName + " where " + Music + " like '%"
				+ name + "%' order by id asc";
		while (true) {
			SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
			Cursor cursor = localSQLiteDatabase.rawQuery(str1, null);
			if (cursor.moveToFirst())
				do {
					ListInfo listinfo = new ListInfo();
					listinfo.setVideoName(cursor.getString(cursor
							.getColumnIndex(Music)));
					listinfo.setArtistName(cursor.getString(cursor
							.getColumnIndex(Artist)));
					listinfo.setImageUrl(cursor.getString(cursor
							.getColumnIndex(ImageUrl)));
					listinfo.setVideoCode(cursor.getString(cursor
							.getColumnIndex(VideoCode)));
					listinfo.setVideoId(cursor.getString(cursor
							.getColumnIndex(VideoId)));
					listinfo.setVideoUrl(cursor.getString(cursor
							.getColumnIndex(VideoUrl)));
					list.add(listinfo);
				} while (cursor.moveToNext());
			cursor.close();
			localSQLiteDatabase.close();
			return list;
		}
	}

	public List<ListInfo> getAllContacts() {
		ArrayList<ListInfo> list = new ArrayList<ListInfo>();
		String str1 = null;
		str1 = "SELECT  * FROM " + TableName + " order by id asc";
		while (true) {
			SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
			Cursor cursor = localSQLiteDatabase.rawQuery(str1, null);
			if (cursor.moveToFirst())
				do {
					ListInfo listinfo = new ListInfo();
					listinfo.setVideoName(cursor.getString(cursor
							.getColumnIndex(Music)));
					listinfo.setArtistName(cursor.getString(cursor
							.getColumnIndex(Artist)));
					listinfo.setImageUrl(cursor.getString(cursor
							.getColumnIndex(ImageUrl)));
					listinfo.setVideoCode(cursor.getString(cursor
							.getColumnIndex(VideoCode)));
					listinfo.setVideoId(cursor.getString(cursor
							.getColumnIndex(VideoId)));
					listinfo.setVideoUrl(cursor.getString(cursor
							.getColumnIndex(VideoUrl)));
					list.add(listinfo);
				} while (cursor.moveToNext());
			cursor.close();
			localSQLiteDatabase.close();
			return list;
		}
	}

	public void deleteContact(ListInfo listinfo) {
		SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
		localSQLiteDatabase.delete(TableName, Music + " = ?",
				new String[] { listinfo.getVideoName() });
		localSQLiteDatabase.close();
	}

	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}
}
