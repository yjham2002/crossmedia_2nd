package com.ccmheaven.tube.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ccmheaven.tube.pub.ListInfo;

import java.util.ArrayList;
import java.util.List;

public class MyMusicDB extends SQLiteOpenHelper {
	private final String TableName = "MyMusic";
	private final String VideoId = "VideoId";
	private final String VideoCode = "VideoCode";
	private final String VideoUrl = "VideoUrl";
	private final String Music = "Music";
	private final String Artist = "Artist";
	private final String ImageUrl = "ImageUrl";
	private final String Runtime = "Runtime";
    private final String Views = "Views";

	public MyMusicDB(Context context) {
		super(context, "MyMusicDB", null, 1);
	}

	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TableName
				+ "(id INTEGER PRIMARY KEY," + VideoId + " TEXT," + Music
				+ " TEXT," + Artist + " TEXT," + ImageUrl + " TEXT,"
				+ VideoCode + " TEXT," + VideoUrl +" TEXT," + Runtime + " TEXT," + Views + " TEXT)");
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
			values.put(Runtime, listinfo.getRuntime());
            values.put(Views, listinfo.getViews());
			localSQLiteDatabase.insert(TableName, null, values);
			localSQLiteDatabase.close();
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
					listinfo.setRuntime(cursor.getString(cursor
							.getColumnIndex(Runtime)));
                    listinfo.setViews(cursor.getString(cursor
                            .getColumnIndex(Views)));
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