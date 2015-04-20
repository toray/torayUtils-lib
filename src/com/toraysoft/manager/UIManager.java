package com.toraysoft.manager;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

public class UIManager {
	static UIManager mUIManager;
	Context mContext;
	int screenWidth, screenHeight = 0;

	int screenWidthDefault = 750;

	private UIManager() {

	}
	
	public void setScreenWidthDefault(int screenWidthDefault){
		this.screenWidthDefault = screenWidthDefault;
	}

	public int getScreenWidthDefault() {
		return screenWidthDefault;
	}

	public static UIManager get() {
		if (mUIManager == null)
			mUIManager = new UIManager();
		return mUIManager;
	}

	public void init(Context context) {
		mContext = context;
	}

	public int getScreenWidth() {
		if (screenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			dm = mContext.getResources().getDisplayMetrics();
			screenWidth = dm.widthPixels;
		}
		return screenWidth;
	}

	public int getScreenHeight() {
		if (screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			dm = mContext.getResources().getDisplayMetrics();
			screenHeight = dm.heightPixels;
		}
		return screenHeight;
	}

	public int getCutImageWidth() {
		return getScreenWidth();
	}

	public int getScaleLength(int length) {
		return length * getScreenWidth() / screenWidthDefault;
	}

	public void setViewScaleLength(View view, int width, int height) {
		setViewScaleLength(view, width, height, screenWidthDefault);
	}

	public void setViewScaleLength(View view, int width, int height,
			int screenWidth) {
		LayoutParams params = view.getLayoutParams();
		if (width == LayoutParams.MATCH_PARENT
				|| width == LayoutParams.WRAP_CONTENT)
			params.width = width;
		else
			params.width = width * getScreenWidth() / screenWidth;
		if (height == LayoutParams.MATCH_PARENT
				|| height == LayoutParams.WRAP_CONTENT)
			params.height = height;
		else
			params.height = height * getScreenWidth() / screenWidth;
		view.setLayoutParams(params);
	}

	public void setViewSquareScaleLength(View view, int width) {
		setViewSquareScaleLength(view, width, screenWidthDefault);
	}

	public void setViewSquareScaleLength(View view, int width, int screenWidth) {
		setViewScaleLength(view, width, width, screenWidthDefault);
	}

	public void setViewSquareLength(View view, int width) {
		setViewLength(view, width, width);
	}

	public void setViewLength(View view, int width, int height) {
		LayoutParams params = view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	public void setViewMargin(View view, int left, int top) {
		LayoutParams params = view.getLayoutParams();
		if (params instanceof MarginLayoutParams) {
			((MarginLayoutParams) params).topMargin = top;
			((MarginLayoutParams) params).leftMargin = left;
		}
	}

	public void setViewMargin(View view, int left, int top, int right,
			int bottom) {
		LayoutParams params = view.getLayoutParams();
		if (params instanceof MarginLayoutParams) {
			((MarginLayoutParams) params).topMargin = top;
			((MarginLayoutParams) params).leftMargin = left;
			((MarginLayoutParams) params).rightMargin = right;
			((MarginLayoutParams) params).bottomMargin = bottom;
		}
	}

	public void setViewScaleMargin(View view, int left, int top) {
		LayoutParams params = view.getLayoutParams();
		if (params instanceof MarginLayoutParams) {
			((MarginLayoutParams) params).topMargin = getScaleLength(top);
			((MarginLayoutParams) params).leftMargin = getScaleLength(left);
		}
	}

	public void setViewScaleMargin(View view, int left, int top, int right,
			int bottom) {
		LayoutParams params = view.getLayoutParams();
		if (params instanceof MarginLayoutParams) {
			((MarginLayoutParams) params).topMargin = getScaleLength(top);
			((MarginLayoutParams) params).leftMargin = getScaleLength(left);
			((MarginLayoutParams) params).rightMargin = getScaleLength(right);
			((MarginLayoutParams) params).bottomMargin = getScaleLength(bottom);
		}
	}

	public void setViewPadding(View view, int padding) {
		setViewPadding(view, padding, padding, padding, padding);
	}

	public void setViewPadding(View view, int left, int top, int right,
			int bottom) {
		view.setPadding(left, top, right, bottom);
	}

	public void setViewScalePadding(View view, int padding) {
		setViewScalePadding(view, padding, padding, padding, padding);
	}

	public void setViewScalePadding(View view, int left, int top, int right,
			int bottom) {
		view.setPadding(getScaleLength(left), getScaleLength(top),
				getScaleLength(right), getScaleLength(bottom));
	}

	public int getHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return (view.getMeasuredHeight());
	}

	public int dip2px(float dipValue) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
	
	/**
	 * 获取标题栏高度
	 * 
	 * @return
	 */
	public int getActionBarHeight() {
		TypedValue tv = new TypedValue();
		if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
				true)) {
			return TypedValue.complexToDimensionPixelSize(tv.data,
					mContext.getResources().getDisplayMetrics());
		}
		return 0;
	}
}
