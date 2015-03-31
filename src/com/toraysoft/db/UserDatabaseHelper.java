package com.toraysoft.db;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

public class UserDatabaseHelper {

	private UserSQLite db;
	static UserDatabaseHelper mUserDatabaseHelper;

	private UserDatabaseHelper() {
	}

	public static UserDatabaseHelper get() {
		if (mUserDatabaseHelper == null) {
			mUserDatabaseHelper = new UserDatabaseHelper();
		}
		return mUserDatabaseHelper;
	}

	public void init(Context context, String name, String dbName, int dbVersion) {
		db = new UserSQLite(context, name, dbName, dbVersion);
	}

	public void setUserInfo(String key, String value) {
		try {
			String sql = "replace into userinfo (key,value) values (?,?)";
			db.run(sql, new String[] { key, value });
		} catch (Exception e) {

		}
	}

	public JSONObject getUserInfo() {
		try {
			String sql = "select key, value from userinfo";
			Cursor cursor = db.query(sql);
			JSONObject info = null;
			if (cursor.getCount() > 0) {
				info = new JSONObject();
				while (cursor.moveToNext()) {
					String key = cursor.getString(0);
					String value = cursor.getString(1);
					try {
						info.put(key, value);
					} catch (JSONException e) {
					}
				}
			}
			cursor.close();
			return info;
		} catch (Exception e) {
		}
		return null;
	}
}
