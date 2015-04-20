package com.toraysoft.common;

import com.toraysoft.manager.ConfigManager;

import android.util.Log;

public class Debug {
	static final String TAG = "Debug";

	public Debug() {
		Log.v("", "");
	}

	public static void d(String msg) {
		d(TAG, msg);
	}

	public static void d(String tag, String msg) {
		if (ConfigManager.get().isDebug()) {
			Log.d(tag, msg);
		}
	}
	
	public static void d(String tag, String msg, Throwable t) {
		if (ConfigManager.get().isDebug()) {
			Log.d(tag, msg,t);
		}
	}

	public static void v(String msg) {
		v(TAG, msg);
	}

	public static void v(String tag, String msg) {
		if (ConfigManager.get().isDebug()) {
			Log.v(tag, msg);
		}
	}
	
	public static void v(String tag, String msg, Throwable t) {
		if (ConfigManager.get().isDebug()) {
			Log.v(tag, msg,t);
		}
	}

	public static void e(String msg) {
		e(TAG, msg);
	}

	public static void e(String tag, String msg) {
		if (ConfigManager.get().isDebug()) {
			Log.e(tag, msg);
		}
	}
	
	public static void e(String tag, String msg, Throwable t) {
		if (ConfigManager.get().isDebug()) {
			Log.e(tag, msg,t);
		}
	}

	public static void i(String msg) {
		i(TAG, msg);
	}

	public static void i(String tag, String msg) {
		if (ConfigManager.get().isDebug()) {
			Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable t) {
		if (ConfigManager.get().isDebug()) {
			Log.i(tag, msg,t);
		}
	}
	
	public static void w(String msg) {
		w(TAG, msg);
	}

	public static void w(String tag, String msg) {
		if (ConfigManager.get().isDebug()) {
			Log.w(tag, msg);
		}
	}
	
	public static void w(String tag, String msg, Throwable t) {
		if (ConfigManager.get().isDebug()) {
			Log.w(tag, msg,t);
		}
	}
}
