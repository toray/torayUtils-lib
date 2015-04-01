package com.toraysoft.manager;

import org.json.JSONObject;

import android.text.TextUtils;

import com.toraysoft.db.UserDatabaseHelper;
import com.toraysoft.utils.encrypt.MD5;

public class UserManager {
	static UserManager mUserManager;

	public static UserManager get() {
		if (mUserManager == null) {
			mUserManager = new UserManager();
		}
		return mUserManager;
	}

	public void setUser(String user) {
		if (!TextUtils.isEmpty(user)) {
			String val = MD5.md5(user);
			SharedPreManager.get().setUser(val);
			UserDatabaseHelper.get().init(ConfigManager.get().getContext(),
					val, ConfigManager.get().getDbName(),
					ConfigManager.get().getDbVersion());
		} else {
			SharedPreManager.get().removeUser();
		}
	}
	
	public boolean isLogin() {
		return !TextUtils.isEmpty(SharedPreManager.get().getUser());
	}
	
	public String getUser() {
		return SharedPreManager.get().getUser();
	}
	
	public void setUserInfo(String key, String val){
		UserDatabaseHelper.get().setUserInfo(key, val);
	}
	
	public JSONObject getUserInfo() {
		return UserDatabaseHelper.get().getUserInfo();
	}
	
	public void logout(){
		setUser("");
	}
}
