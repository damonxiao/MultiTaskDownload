package com.ll.download.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBProvider extends ContentProvider{
	
	private static final String TAG = DBProvider.class.getSimpleName();
	
	private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private DBHelper mDBHelper;
	
	private static final String AUTHORITY = "com.ll.download";
	
	private static final int ALL_DOWNLOAD_IFNO = 1;
	
	static{
		sUriMatcher.addURI(AUTHORITY, TDownloadInfo.TABLE_NAME, ALL_DOWNLOAD_IFNO);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = sUriMatcher.match(uri);
		Logger.d(TAG, "insert()[uri="+uri+",match="+match+"]");
		SQLiteDatabase sqlDB = mDBHelper.getWritableDatabase();
		int deleteRows = -1;
		switch (match) {
		case ALL_DOWNLOAD_IFNO:
			deleteRows = sqlDB.delete(TDownloadInfo.TABLE_NAME, selection, selectionArgs);
			break;
		default:
			break;
		}
		if(deleteRows > 0){
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return deleteRows;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = sUriMatcher.match(uri);
		Logger.d(TAG, "insert()[uri="+uri+",match="+match+"]");
		SQLiteDatabase sqlDB = mDBHelper.getWritableDatabase();
		long effectId = -1;
		switch (match) {
		case ALL_DOWNLOAD_IFNO:
			effectId = sqlDB.insert(TDownloadInfo.TABLE_NAME, null, values);
			break;
		default:
			break;
		}
		if(effectId > 0){
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return uri;
	}

	@Override
	public boolean onCreate() {
		mDBHelper = new DBHelper(this.getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = sUriMatcher.match(uri);
		Logger.d(TAG, "query()[uri="+uri+",match="+match+"]");
		Cursor cursor = null;
		switch (match) {
		case ALL_DOWNLOAD_IFNO:
			cursor = mDBHelper.getWritableDatabase().query(
					TDownloadInfo.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		default:
			break;
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
	
	public static class TDownloadInfo implements BaseColumns{
		public static final String TABLE_NAME = "TDownloadInfo";
		public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
		public static final String ID = _ID;
		public static final String FILE_NAME = "filename";
		public static final String URL = "url";
		public static final String SAVED_PATH = "savedPath";
		public static final String FILE_SIZE = "fileSize";
		
		private static void createSQL(SQLiteDatabase db){
			StringBuilder builder = new StringBuilder("create table "+TABLE_NAME+"(");
			builder.append(ID+" integer primary key,");
			builder.append(FILE_NAME+" char,");
			builder.append(URL+" char,");
			builder.append(SAVED_PATH+" char,");
			builder.append(FILE_SIZE+" integer ");
			builder.append(");");
			db.execSQL(builder.toString());
		}
	}
	
	private class DBHelper extends SQLiteOpenHelper{
		private static final String DB_NAME = "downloadDB";
		private static final int DB_VERSION = 1;
		public DBHelper(Context context) {
			this(context, DB_NAME, null, DB_VERSION);
		}
		
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}



		@Override
		public void onCreate(SQLiteDatabase db) {
			TDownloadInfo.createSQL(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
	}
	
}
