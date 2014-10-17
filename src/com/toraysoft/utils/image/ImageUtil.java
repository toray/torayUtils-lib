package com.toraysoft.utils.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
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
	
	public void getRoundImageBitmap(final String url,final CustomImageListener l){
		mImageLoader.get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				if(l!=null)
					l.onErrorResponse(error);
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if(response!=null && response.getBitmap()!=null){
					if(l!=null){
						Bitmap bitmap = cutRoundBitmap(response.getBitmap());
						l.onResponse(bitmap);
					}
				}
			}
		});
	}
	
	public void getCornersImageBitmap(final String url,final float corners,final CustomImageListener l){
		mImageLoader.get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				if(l!=null)
					l.onErrorResponse(error);
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if(response!=null && response.getBitmap()!=null){
					if(l!=null){
						Bitmap bitmap = cutCornersBitmap(response.getBitmap(),corners);
						l.onResponse(bitmap);
					}
				}
			}
		});
	}
	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap cutRoundBitmap(Bitmap bitmap) {
		if(bitmap==null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}
	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap cutCornersBitmap(Bitmap bitmap,float corners) {
		if(bitmap==null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx = corners;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
//			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
//			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
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
				get(view.getContext()).doTask();
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
	
	public interface CustomImageListener{
		public void onErrorResponse(VolleyError error);
		
		public void onResponse(Bitmap bitmap);
	}
	
	public static Bitmap getBitmapFromFileScale(String filepath) {
		if(TextUtils.isEmpty(filepath))
			return null;
		File file = new File(filepath);
		if (!file.exists()) {
			return null;
		}
		Bitmap bitmap = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(filepath, options);
		options.inJustDecodeBounds = false;
		int w = options.outWidth;
		int h = options.outHeight;
		float hh = 1280f;
		float ww = 720f;
		int be = 1;
		if (w > ww || h > hh) {
			if (w > h && w > ww) {
				be = (int) (options.outWidth / ww);
			} else if (w < h && h > hh) {
				be = (int) (options.outHeight / hh);
			}
		}
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		try {
			bitmap = BitmapFactory.decodeFile(filepath, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap getBitmapFromFileScale2(String filepath) {
		if(TextUtils.isEmpty(filepath))
			return null;
		File file = new File(filepath);
		if (!file.exists()) {
			return null;
		}
		Bitmap bitmap = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(filepath, options);
		options.inJustDecodeBounds = false;
		int w = options.outWidth;
		int h = options.outHeight;
		float hh = 240f;
		float ww = 240f;
		int be = 1;
		if (w > ww || h > hh) {
			if (w > h && w > ww) {
				be = (int) (options.outWidth / ww);
			} else if (w < h && h > hh) {
				be = (int) (options.outHeight / hh);
			}
		}
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		try {
			bitmap = BitmapFactory.decodeFile(filepath, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}
	
}
