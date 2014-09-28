package com.toraysoft.utils.image;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.toraysoft.utils.cache.CacheUtil;

public class ImageUtil {
	static ImageUtil mImageManager;
	ImageLoader mImageLoader;
	Map<String, NetworkImageView> tasks = new HashMap<String, NetworkImageView>();
	static boolean isLock;
	BitmapLruCache mBitmapLruCache;

	Context context;
	static CacheUtil cacheUtil;

	public ImageUtil(Context context) {
		cacheUtil = new CacheUtil(context.getExternalCacheDir());
		mBitmapLruCache = new BitmapLruCache();
		mImageLoader = new ImageLoader(Volley.newRequestQueue(context),
				mBitmapLruCache);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	static class BitmapLruCache extends LruCache<String, Bitmap> implements
			ImageCache {
		public static int getDefaultLruCacheSize() {
			final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			final int cacheSize = maxMemory / 8;
			return cacheSize;
		}

		public BitmapLruCache() {
			this(getDefaultLruCacheSize());
		}

		public BitmapLruCache(int sizeInKiloBytes) {
			super(sizeInKiloBytes);
		}

		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight() / 1024;
		}

		@Override
		public Bitmap getBitmap(String url) {
			Bitmap bitmap = get(url);
			if (bitmap != null && !bitmap.isRecycled()) {
				return bitmap;
			} else {
				// get bitmap from local cache
				if (cacheUtil != null) {
					Bitmap bm = cacheUtil.getBitmapCache(url);
					if (bm != null && !bm.isRecycled()) {
						put(url, bm);
						return bm;
					}
				}
				return null;
			}
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			put(url, bitmap);
			// save bitmap to local cache
			if (cacheUtil != null) {
				cacheUtil.putBitmapCache(url, bitmap);
			}
		}

	}

	public void getImageBitmap(String url, ImageListener l) {
		mImageLoader.get(url, l);
	}

	public void putTask(NetworkImageView iv, String image) {
		Bitmap bitmap = mBitmapLruCache.get(image);
		if (isLock && (bitmap == null || bitmap.isRecycled())) {
			tasks.put(image, iv);
		} else {
			iv.setImageUrl(image, getImageLoader());
		}
	}

	public void doTask() {
		for (String image : tasks.keySet()) {
			tasks.get(image).setImageUrl(image, getImageLoader());
		}
		tasks.clear();
	}

	public static class OnLockLoadImageScrollListener implements
			OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				isLock = true;
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				isLock = true;
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				isLock = false;
				break;
			default:
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

	}
}
