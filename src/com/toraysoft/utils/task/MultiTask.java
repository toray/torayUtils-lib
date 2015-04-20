package com.toraysoft.utils.task;

import android.os.AsyncTask;

import com.toraysoft.utils.task.TaskQueue.OnTaskFinishListener;

public abstract class MultiTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	int DEFAULT_TIMEOUT = 30;

	int timeout = DEFAULT_TIMEOUT;

	OnTaskFinishListener mOnTaskFinishListener;

	boolean isAutoFinish = true;
	boolean isStart = false;
	boolean isRunning = false;
	boolean isFinish = true;
	long startTime = 0;

	// TODO timeout feature
	// Timer timer;
	// TimerTask timeoutTask = new TimerTask() {
	//
	// @Override
	// public void run() {
	// cancel();
	// }
	// };

	@Override
	final protected Result doInBackground(Params... params) {
		startTime = System.currentTimeMillis();
		isStart = true;
		isRunning = true;
		isFinish = false;
		// timer = new Timer();
		// timer.schedule(timeoutTask, timeout * 1000);
		try {
			Result ret = runInBackground(params);
			isRunning = false;
			isFinish = true;
			// timer.cancel();
			if (isAutoFinish() && mOnTaskFinishListener != null)
				mOnTaskFinishListener.onFinish(true);
			return ret;
		} catch (RuntimeException e) {
			isRunning = false;
			isFinish = true;
			// timer.cancel();
			if (mOnTaskFinishListener != null)
				mOnTaskFinishListener.onFinish(false);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onCancelled(Result result) {
		super.onCancelled(result);
		finish(false);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		finish(false);
	}

	protected abstract Result runInBackground(Params... params);

	public void setOnTaskFinishListener(
			OnTaskFinishListener mOnTaskFinishListener) {
		this.mOnTaskFinishListener = mOnTaskFinishListener;
	}

	public boolean isAutoFinish() {
		return isAutoFinish;
	}

	public void setAutoFinish(boolean isAutoFinish) {
		this.isAutoFinish = isAutoFinish;
	}

	protected void finish(boolean result) {
		isFinish = true;
		if (mOnTaskFinishListener != null)
			mOnTaskFinishListener.onFinish(result);
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
