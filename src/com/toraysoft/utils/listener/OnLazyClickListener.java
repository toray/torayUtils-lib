package com.toraysoft.utils.listener;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnLazyClickListener implements OnClickListener{

	long timestamp = 0L;
	
	@Override
	public void onClick(View v) {
		long time = System.currentTimeMillis();
		if(time-timestamp<500L){
			return;
		}
		timestamp = time;
		onClickView(v);
	}

	public abstract void onClickView(View v);
}
