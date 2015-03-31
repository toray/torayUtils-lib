package com.toraysoft.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSQLite {

	SQLiteDatabase db;
	ExSQLiteOpenHelper helper;
	Context mContext;
	String userName;
	String dbName;
	int dbVersion;

	public UserSQLite(Context context, String userName, String dbName, int dbVersion) {
		this.mContext = context;
		this.userName = userName;
		this.dbVersion = dbVersion;
		this.dbName = dbName;
	}

	public SQLiteDatabase get() {
		if (db == null) {
			File file = mContext.getDatabasePath(userName);
			if (!file.exists())
				file.mkdirs();
			String path = file.getPath() + "/" + dbName;
			for (int i = 0; i < 3; i++) {
				helper = new ExSQLiteOpenHelper(mContext, path, dbVersion);
				if (helper != null) {
					db = helper.getWritableDatabase();
					break;
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		}
		return db;
	}

	private static class ExSQLiteOpenHelper extends SQLiteOpenHelper {
		ExSQLiteOpenHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE userinfo ('key' VARCHAR, 'value' VARCHAR, PRIMARY KEY ('key'))";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	public synchronized void run(String sql) {

//		Debug.i("Run SQL", sql);

//		if (!get().inTransaction()) {
//			get().beginTransaction();
//			get().execSQL(sql);
//			get().setTransactionSuccessful();
//			get().endTransaction();
//		} else {
			get().execSQL(sql);
//		}
	}

	public synchronized void run(String sql, Object[] args) {

//		Debug.i("Run SQL", sql);
		if(args != null){
//			if (!get().inTransaction()) {
//				get().beginTransaction();
//				get().execSQL(sql, args);
//				get().setTransactionSuccessful();
//				get().endTransaction();
//			} else {
				get().execSQL(sql, args);
//			}	
		} else {
			run(sql);
		}
	}

	public boolean queryExist(String sql, String[] keys) {
		Cursor cursor = get().rawQuery(sql, keys);
		return cursor.getCount() > 0;
	}

	public Cursor query(String sql) {
		return query(sql, null);
	}

	public Cursor query(String sql, String[] keys) {
		return get().rawQuery(sql, keys);
	}

}
