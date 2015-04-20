package com.toraysoft.manager;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class AppManager {
	static AppManager mAppManager;
	
	public static final String CHANNEL_OFFICIAL = "official";

	private AppManager() {

	}

	public static AppManager get() {
		if (mAppManager == null) {
			mAppManager = new AppManager();
		}
		return mAppManager;
	}
	
	public int getAppVersionCode(Context context, String packageName) {
		try {
			PackageInfo mPackageInfo = context.getPackageManager()
					.getPackageInfo(packageName, 0);
			return mPackageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getVersionCode(Context context) {
		return AppManager.get().getAppVersionCode(context,
				context.getPackageName());
	}
	
	public void setAppLastVersionCode(Context context){
		SharedPreManager.get().setAppLastVersionCode(getVersionCode(context));
	}
	
	public boolean isUpgurad(Context context){
		return getVersionCode(context) != SharedPreManager.get().getAppLastVersionCode();
	}
	
	public String getAppVersionName(Context context){
		try {
			PackageInfo mPackageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return mPackageInfo.versionName;
		} catch (NameNotFoundException e) {
		}
		return "";
	}
	
	public String getApplicationMetadata(Context context, String packageName,
			String key){
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			return appInfo.metaData.getString(key);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
	
	public String getChannel(Context context){
		String channel = "";
		try {
			channel = getApplicationMetadata(context, context.getPackageName(), "UMENG_CHANNEL");
			return channel;
		} catch (Exception e) {
		}
		return CHANNEL_OFFICIAL;
	}
	
	public String getMetadata(Context context, String key){
		try {
			return getApplicationMetadata(context, context.getPackageName(), key);
		} catch (Exception e) {
		}
		return "";
	}
	
	public boolean isAppExists(Context context, String packageName) {
		try {
			PackageInfo mPackageInfo = context.getPackageManager()
					.getPackageInfo(packageName, 0);
			if (mPackageInfo != null) {
				return true;
			}
		} catch (NameNotFoundException e) {
//			e.printStackTrace();
		}
		return false;
	}
	
	public void openApp(Context context,String packageName){
		if(!isAppExists(context, packageName)) return;
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		context.startActivity(intent);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void copyText(Context context,String content)  
	{  
		if (Build.VERSION.SDK_INT < 11) {
			ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
			clipboardManager.setText(content);  
			if (clipboardManager.hasText()){  
			    clipboardManager.getText();  
			}  
		}else{
			android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
			ClipData clip = ClipData.newPlainText(content,content);
			clipboardManager.setPrimaryClip(clip);
		}
	}
}
