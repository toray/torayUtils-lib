package com.toraysoft.utils.cache;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class CacheUtil {

	ACache mACache;
	private static int DEFAULT_SAVETIME = 60 * 60 * 2;
	String typePrefix = "Cache_type_";

	public CacheUtil(File cacheDir) {
		mACache = ACache.get(cacheDir);
	}

	// save string cache
	public void putStringCache(String key, String value) {
		if (mACache != null) {
			key = key.hashCode() + "";
			mACache.put(key, value, DEFAULT_SAVETIME);
		}
	}

	// get string cache
	public String getStringCache(String key) {
		if (mACache != null) {
			key = key.hashCode() + "";
			return mACache.getAsString(key);
		}
		return null;
	}

	// save jsonObject cache
	public void putJSONObjectCache(String key, JSONObject jo) {
		if (mACache != null) {
			key = key.hashCode() + "";
			mACache.put(key, jo, DEFAULT_SAVETIME);
		}
	}

	// get jsonObject cache
	public JSONObject getJSONObjectCache(String key) {
		if (mACache != null) {
			key = key.hashCode() + "";
			return mACache.getAsJSONObject(key);
		}
		return null;
	}

	// save jsonArray cache
	public void putJSONArrayCache(String key, JSONArray ja) {
		if (mACache != null) {
			key = key.hashCode() + "";
			mACache.put(key, ja, DEFAULT_SAVETIME);
		}
	}

	// get jsonArray cache
	public JSONArray getJSONArrayCache(String key) {
		if (mACache != null) {
			key = key.hashCode() + "";
			return mACache.getAsJSONArray(key);
		}
		return null;
	}

	// save bitmap cache
	public void putBitmapCache(String key, Bitmap bm) {
		if (mACache != null) {
			key = key.hashCode() + "";
			mACache.put(key, bm, DEFAULT_SAVETIME);
		}
	}

	// get bitmap cache
	public Bitmap getBitmapCache(String key) {
		if (mACache != null) {
			key = key.hashCode() + "";
			return mACache.getAsBitmap(key);
		}
		return null;
	}

	// save json cache
	public void putJSONCache(String key, Object val) {
		key = key.hashCode() + "";
		if (val instanceof JSONArray) {
			putJSONArrayCache(key, (JSONArray) val);
			putStringCache(typePrefix + key, "0");
		} else {
			putJSONObjectCache(key, (JSONObject) val);
			putStringCache(typePrefix + key, "1");
		}
	}

	// get jsonArray cache
	public Object getJSONCache(String key) {
		if (mACache != null) {
			key = key.hashCode() + "";
			if ("0".equals(getStringCache(typePrefix + key))) {
				return getJSONArrayCache(key);
			} else {
				return getJSONObjectCache(key);
			}
		}
		return null;
	}

}
