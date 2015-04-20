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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Base64;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.toraysoft.utils.cache.CacheUtil;

public class ImageUtil {
	static ImageUtil mImageManager;
	static ImageLoader mImageLoader;
	Map<String, ImageListener> tasks = new HashMap<String, ImageListener>();
	static boolean isLock;
	static BitmapLruCache mBitmapLruCache;

	Context mContext;
	static CacheUtil cacheUtil;

	Animation animation;

	private ImageUtil(Context context) {
		this.mContext = context;
	}

	public static ImageUtil get(Context context) {
		if (mImageManager == null) {
			mImageManager = new ImageUtil(context);
			cacheUtil = new CacheUtil(context.getApplicationContext()
					.getExternalCacheDir());
			mBitmapLruCache = new BitmapLruCache();
		}
		return mImageManager;
	}

	public ImageLoader getImageLoader() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(Volley.newRequestQueue(mContext),
					mBitmapLruCache);
		}
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
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri());
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
	 * 
	 * @param fromFile
	 * @return
	 */
	public static Intent getCutImageIntent(Uri uri, Uri out, int w, int h) {
		return getCutImageIntent(uri, out, w, h, 1, 1);
	}

	/**
	 * 裁剪图片
	 * 
	 * @param fromFile
	 * @return
	 */
	public static Intent getCutImageIntent(Uri uri, Uri out, int w, int h,
			int aspectX, int aspectY) {
		// try {
		// ContentResolver resolver = context.getContentResolver();
		// InputStream inStream = resolver.openInputStream(uri);
		// BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		// Bitmap bm = BitmapFactory.decodeStream(inStream, null,
		// bitmapOptions);
		//
		// return cutImage(bm, w, h);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", w);
		intent.putExtra("outputY", h);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
		return intent;
		// bitmapOptions.inJustDecodeBounds = true;
		// BitmapFactory.decodeStream(inStream,null,bitmapOptions);
		// BitmapFactory.Options bitmapOptions2 = new BitmapFactory.Options();
		// bitmapOptions2.inSampleSize=bitmapOptions.outHeight/Env.get().getScreenWidth();
		// bitmapOptions2.inJustDecodeBounds = false;
		// inStream = resolver.openInputStream(uri);
		// Bitmap bm2 = BitmapFactory.decodeStream(inStream, null,
		// bitmapOptions2);
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @param l
	 */
	public void getImageBitmap(String url, ImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		getImageLoader().get(url, l);
	}

	public void getImageSmallBitmap(String url, final ImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		final String u = (!url.contains("img.diange.fm") || url.contains("!") || url
				.contains("?")) ? url : url + "!m";
		getImageLoader().get(u, l);
	}

	public void getImageMiniBitmap(String url, final ImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		final String u = (!url.contains("img.diange.fm") || url.contains("!") || url
				.contains("?")) ? url : url + "!s";
		getImageLoader().get(u, l);
	}

	public void getImageLargeBitmap(String url, final ImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		final String u = (!url.contains("img.diange.fm") || url.contains("!") || url
				.contains("?")) ? url : url + "!l";
		getImageLoader().get(u, l);
	}

	public void getImageBitmap(String url, ImageListener l, int max_width,
			int max_height) {
		if (TextUtils.isEmpty(url))
			return;
		getImageLoader().get(url, l, max_width, max_height);
	}

	public void getRoundImageMiniBitmap(final String url,
			final CustomImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		final String u = (!url.contains("img.diange.fm") || url.contains("!") || url
				.contains("?")) ? url : url + "!s";
		getRoundImageBitmap(u, l);
	}

	public void getRoundImageBitmap(final String url,
			final CustomImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		getImageLoader().get(url, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (l != null)
					l.onErrorResponse(error);
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response != null && response.getBitmap() != null) {
					if (l != null) {
						Bitmap bitmap = cutRoundBitmap(response.getBitmap());
						l.onResponse(bitmap);
					}
				}
			}
		});
	}

	public void getRoundImageBitmap(final Uri uri, final CustomImageListener l) {
		try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					mContext.getContentResolver(), uri);
			if (bitmap != null) {
				Bitmap b = cutRoundBitmap(bitmap);
				l.onResponse(b);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getCornersImageMiniBitmap(final String url,
			final float corners, final CustomImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		final String u = (!url.contains("img.diange.fm") || url.contains("!") || url
				.contains("?")) ? url : url + "!s";
		getCornersImageBitmap(u, corners, l);
	}

	public void getCornersImageBitmap(final String url, final float corners,
			final CustomImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		getImageLoader().get(url, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (l != null)
					l.onErrorResponse(error);
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response != null && response.getBitmap() != null) {
					if (l != null) {
						Bitmap bitmap = cutCornersBitmap(response.getBitmap(),
								corners);
						l.onResponse(bitmap);
					}
				}
			}
		});
	}

	public void getBlurImageBitmap(String url, final ImageListener l) {
		if (TextUtils.isEmpty(url))
			return;
		if (url.contains("?")) {
			url = url.substring(0, url.indexOf("?"));
		}
		final String u = url + "?imageMogr2/blur/50x80/thumbnail/180x";
		getImageLoader().get(u, l);
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap cutRoundBitmap(Bitmap bitmap) {
		if (bitmap == null)
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
		try {
			Bitmap output = Bitmap
					.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final Paint paint = new Paint();
			final Rect src = new Rect((int) left, (int) top, (int) right,
					(int) bottom);
			final Rect dst = new Rect((int) dst_left, (int) dst_top,
					(int) dst_right, (int) dst_bottom);
			final RectF rectF = new RectF(dst);

			paint.setAntiAlias(true);// 设置画笔无锯齿

			canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

			// 以下有两种方法画圆,drawRounRect和drawCircle
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
			// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
			canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

			return output;
		} catch (OutOfMemoryError error) {

		}
		return null;
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap cutCornersBitmap(Bitmap bitmap, float corners) {
		if (bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx = corners;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			// roundPx = width / 2;

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
			// roundPx = height / 2;

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
		try {
			Bitmap output = Bitmap
					.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final Paint paint = new Paint();
			final Rect src = new Rect((int) left, (int) top, (int) right,
					(int) bottom);
			final Rect dst = new Rect((int) dst_left, (int) dst_top,
					(int) dst_right, (int) dst_bottom);
			final RectF rectF = new RectF(dst);

			paint.setAntiAlias(true);// 设置画笔无锯齿

			canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

			// 以下有两种方法画圆,drawRounRect和drawCircle
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
			// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
			canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
			return output;
		} catch (OutOfMemoryError error) {

		}
		return null;
	}

	/**
	 * 保存图片
	 * 
	 * @param url
	 * @param l
	 */
	public static Uri saveImageBitmap(Bitmap bitmap, File dir) {
		File file = new File(dir, System.currentTimeMillis() + "");
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			if (bitmap != null) {
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
					// fileOutputStream.close();
					return Uri.fromFile(file);
				}
			}
		} catch (FileNotFoundException e) {
			file.delete();
			e.printStackTrace();
		} catch (IOException e) {
			file.delete();
			e.printStackTrace();
		} finally {
			try {
				if(fileOutputStream != null){
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String saveImageBitmap(Bitmap bitmap, String path) {
		File file = new File(path);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			if (bitmap != null) {
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
					// fileOutputStream.close();
					return path;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			file.delete();
			e.printStackTrace();
		} finally {
			try {
				if(fileOutputStream != null){
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	public static String bitmap2Base64(Bitmap bitmap) {
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);// 参数100表示不压缩
			byte[] bytes = bos.toByteArray();
			return Base64.encodeToString(bytes, Base64.DEFAULT);
		}catch(Throwable t){
			t.printStackTrace();
		}
		return null;
	}

	public void setAnimation(Animation anim) {
		this.animation = anim;
	}

	public void putTask(String image, final ImageListener l) {
		if (getImageLoader().isCached(image, 0, 0)) {
			if (l != null) {
				getImageLoader().get(image, l);
			}
		} else {
			tasks.put(image, l);
		}
	}

	public void doTask() {
		for (String image : tasks.keySet()) {
			getImageLoader().get(image, tasks.get(image));
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

	public interface CustomImageListener {
		public void onErrorResponse(VolleyError error);

		public void onResponse(Bitmap bitmap);
	}

	public static Bitmap getBitmapFromFileScale(String filepath) {
		if (TextUtils.isEmpty(filepath))
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
		if (TextUtils.isEmpty(filepath))
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

	public static Bitmap resizeBitmap(Bitmap bitmap, float percent) {
		Matrix matrix = new Matrix();
		matrix.postScale(percent, percent); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	public final Uri getImageUriDegree(Uri uri, File file, int sw, int sh) {
		String path = uri.getPath();
		int degrees = readPictureDegree(path);
		if (degrees > 0) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int width = opts.outWidth;
			// int height = opts.outHeight;
			// if(sw>width){
			opts.inSampleSize = 4;
			// }
			opts.inJustDecodeBounds = false;
			Bitmap bitmap = rotate(BitmapFactory.decodeFile(path, opts),
					degrees);
			if (bitmap != null) {
				Uri tmp = saveImageBitmap(bitmap, file);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		return uri;
	}

	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	private static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees == 0) {
			return b;
		}
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth(), (float) b.getHeight());
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle(); // Android开发网再次提示Bitmap操作完应该显示的释放
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
			}
		}
		return b;
	}
}
