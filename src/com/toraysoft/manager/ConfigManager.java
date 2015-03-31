package com.toraysoft.manager;

import android.content.Context;

public class ConfigManager {

	boolean isInit;

	static ConfigManager mConfigManager;
	Context mContext;
	String dbName;
	int dbVerion;

	boolean isDebug;

	private ConfigManager() {

	}

	public static ConfigManager get() {
		if (mConfigManager == null) {
			mConfigManager = new ConfigManager();
		}
		return mConfigManager;
	}

	public void init(Context context, String dbName, int dbVerion) {
		this.mContext = context;
		this.dbName = dbName;
		this.dbVerion = dbVerion;
		SharedPreManager.get().init(mContext);
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = false;
	}

	public Context getContext() {
		if (!isInit) {
			throw new IllegalArgumentException("didn't init config");
		}
		return this.mContext;
	}

	public String getDbName() {
		if (!isInit) {
			throw new IllegalArgumentException("didn't init config");
		}
		return this.dbName;
	}

	public int getDbVersion() {
		if (!isInit) {
			throw new IllegalArgumentException("didn't init config");
		}
		return this.dbVerion;
	}

}
