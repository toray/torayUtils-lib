package com.toraysoft.manager;

import com.toraysoft.utils.encrypt.MD5;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreManager {

	static SharedPreManager mSharedPreManager;

	Context mContext;
	boolean isInitialize;

	static final String SP_ONLINE_PARAMS = "com_toraysoft_online_params";
	static final String SP_CONFIG = "com_toraysoft_config";
	static final String SP_SYS = "com_toraysoft_sys";
	static final int FLAG_SP_ONLINE_PARAMS = 1;
	static final int FLAG_SP_CONFIG = 2;
	static final int FLAG_SP_SYS = 3;

	SharedPreferences mOnlineParamsSharedPreferences;
	SharedPreferences mConfigParamsSharedPreferences;
	SharedPreferences mSysSharedPreferences;

	static final String APP_LAST_VERSIONCODE = "app_last_versioncode";
	static final String APP_SHOW_GUIDE = "app_show_guide";
	final static String CONFIG_USER_FLAG = MD5.md5("user");

	private SharedPreManager() {

	}

	public static SharedPreManager get() {
		if (mSharedPreManager == null)
			mSharedPreManager = new SharedPreManager();
		return mSharedPreManager;
	}

	public void init(Context context) {
		this.mContext = context;
		isInitialize = true;
	}

	protected SharedPreferences getSharedPreferences(int flag) {
		if (!isInitialize)
			throw new RuntimeException(
					"Hey man, you did not forget init me(SharedPreManager) !!!");

		switch (flag) {
		case FLAG_SP_ONLINE_PARAMS:
			if (mOnlineParamsSharedPreferences == null) {
				mOnlineParamsSharedPreferences = mContext.getSharedPreferences(
						SP_ONLINE_PARAMS, Activity.MODE_PRIVATE);
			}
			return mOnlineParamsSharedPreferences;
		case FLAG_SP_CONFIG:
			if (mConfigParamsSharedPreferences == null) {
				mConfigParamsSharedPreferences = mContext.getSharedPreferences(
						SP_CONFIG, Activity.MODE_PRIVATE);
			}
			return mConfigParamsSharedPreferences;
		case FLAG_SP_SYS:
			if (mSysSharedPreferences == null) {
				mSysSharedPreferences = mContext.getSharedPreferences(SP_SYS,
						Activity.MODE_PRIVATE);
			}
			return mSysSharedPreferences;
		default:
			break;
		}
		return null;
	}

	public int getAppLastVersionCode() {
		if (getSharedPreferences(FLAG_SP_CONFIG) != null) {
			int versionCode = getSharedPreferences(FLAG_SP_CONFIG).getInt(
					APP_LAST_VERSIONCODE, 0);
			return versionCode;
		}
		return 0;
	}

	public void setAppLastVersionCode(int versionCode) {
		if (getSharedPreferences(FLAG_SP_CONFIG) != null) {
			getSharedPreferences(FLAG_SP_CONFIG).edit()
					.putInt(APP_LAST_VERSIONCODE, versionCode).commit();
		}
	}
	
	public void setUser(String val){
		getSharedPreferences(FLAG_SP_CONFIG).edit()
			.putString(CONFIG_USER_FLAG, val).commit();
	}
	
	public String getUser(){
		return getSharedPreferences(FLAG_SP_CONFIG).getString(CONFIG_USER_FLAG, "");
	}
	
	public void removeUser(){
		getSharedPreferences(FLAG_SP_CONFIG).edit().remove(CONFIG_USER_FLAG).commit();
	}
	
	public long getOpTimes(String key){
		return getSharedPreferences(FLAG_SP_ONLINE_PARAMS).getLong(key, 0L);
	}
	
	public void setOptimes(String key,long time){
		getSharedPreferences(FLAG_SP_ONLINE_PARAMS).edit().putLong(key, time).commit();
	}
	
	public String getOpVal(String key){
		return getSharedPreferences(FLAG_SP_ONLINE_PARAMS).getString(key,"");
	}
	
	public void setOpVal(String key,String value){
		getSharedPreferences(FLAG_SP_ONLINE_PARAMS).edit().putString(key, value).commit();
	}
	
	public boolean isShowGuide(){
		return getSharedPreferences(FLAG_SP_SYS).getBoolean(APP_SHOW_GUIDE, false);
	}
	
	public void setShowGuide(){
		getSharedPreferences(FLAG_SP_SYS).edit().putBoolean(APP_SHOW_GUIDE, true).commit();
	}

}
