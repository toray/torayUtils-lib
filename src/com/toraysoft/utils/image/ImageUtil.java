package com.toraysoft.utils.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
	static ImageLoader mImageLoader;
	Map<String, NetworkImageView> tasks = new HashMap<String, NetworkImageView>();
	static boolean isLock;
	static BitmapLruCache mBitmapLruCache;

	Context context;
	static CacheUtil cacheUtil;

	private ImageUtil() {
	}

	public static ImageUtil get(Context context) {
		if (mImageManager == null) {
			mImageManager = new ImageUtil();
			cacheUtil = new CacheUtil(context.getApplicationContext()
					.getExternalCacheDir());
			mBitmapLruCache = new BitmapLruCache();
			mImageLoader = new ImageLoader(Volley.newRequestQueue(context
					.getApplicationContext()), mBitmapLruCache);
		}
		return mImageManager;
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
	
	/**
	 * 图库工具选择界面
	 * 
	 * @return Intent
	 */
	public static Intent getImageChooserIntent(String title) {
		Intent intent = Intent.createChooser(getMediaImageIntent(), title);
		return intent;
	}

	/**
	 * 开启本地图片库
	 * 
	 * @return Intent
	 */
	private static Intent getMediaImageIntent() {
		Intent intent = new Intent("android.intent.action.PICK");
		Uri uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
		intent.setDataAndType(uri, "image/*");
		return intent;
	}
	
	/**
	 * 开启照相机
	 * 
	 * @return Intent
	 */
	public static Intent getTakePhotoIntent() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri());
		return intent;
	}
	
	/**
	 * 拍照工具选择界面
	 * 
	 * @return Intent
	 */
	public static Intent getTaskPhotoIntent(String title) {
		Intent intent = Intent.createChooser(getTakePhotoIntent(), title);
		return intent;
	}
	
	/**
	 * 裁剪图片
	 * @param fromFile
	 * @return
	 */
	public static Intent getCutImageIntent(Uri uri, int w, int h) {
//		try {
//			ContentResolver resolver = context.getContentResolver(); 
//			InputStream inStream = resolver.openInputStream(uri);
//			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//			Bitmap bm = BitmapFactory.decodeStream(inStream, null, bitmapOptions); 
//			
//			return cutImage(bm, w, h);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", w);
		intent.putExtra("outputY", h);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		return intent;
//		bitmapOptions.inJustDecodeBounds = true; 
//		BitmapFactory.decodeStream(inStream,null,bitmapOptions);
//		BitmapFactory.Options bitmapOptions2 = new BitmapFactory.Options();
//		bitmapOptions2.inSampleSize=bitmapOptions.outHeight/Env.get().getScreenWidth();
//		bitmapOptions2.inJustDecodeBounds = false;
//		inStream = resolver.openInputStream(uri);
//		Bitmap bm2 = BitmapFactory.decodeStream(inStream, null, bitmapOptions2);
	}
	
	/**
	 * 获取网络图片
	 * @param url
	 * @param l
	 */
	public void getImageBitmap(String url, ImageListener l) {
		mImageLoader.get(url, l);
	}
	
	/**
	 * 保存图片
	 * @param url
	 * @param l
	 */
	public static Uri saveImageBitmap(Bitmap bitmap, File dir) {
		File file = new File(dir , System.currentTimeMillis() + ".jpg");
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			if (bitmap != null) {
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
					fileOutputStream.flush();
//					fileOutputStream.close();
					return Uri.fromFile(file);
				}
			}
		} catch (FileNotFoundException e) {
			file.delete();
			e.printStackTrace();
		} catch (IOException e) {
			file.delete();
			e.printStackTrace();
		} finally{
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
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
