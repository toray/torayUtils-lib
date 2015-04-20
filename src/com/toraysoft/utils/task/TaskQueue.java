package com.toraysoft.utils.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TaskQueue {

	public enum RunningMode {
		SERIAL, PARALLEL
	}

	List<MultiTask> tasks = new ArrayList<MultiTask>();
	Map<Integer, MultiTask> runMap = new HashMap<Integer, MultiTask>();
	Map<Integer, Boolean> resultMap = new HashMap<Integer, Boolean>();
	BlockingQueue<MultiTask> runningQueue;

	boolean isRunning = false;

	RunningMode runningMode = RunningMode.PARALLEL;

	OnFinishListener onFinishListener;

	private TaskQueue(MultiTask... tasks) {
		for (MultiTask t : tasks) {
			this.tasks.add(t);
		}
	}

	public static TaskQueue create() {
		return new TaskQueue();
	}

	public static TaskQueue create(MultiTask... tasks) {
		return new TaskQueue(tasks);
	}

	synchronized public void exe(final Object... params) {
		isRunning = true;
		if (runningMode == RunningMode.PARALLEL) {
			for (MultiTask task : tasks) {
				final int hash = task.hashCode();
				runMap.put(hash, task);
				task.setOnTaskFinishListener(new OnTaskFinishListener() {

					@Override
					public void onFinish(boolean result) {
						runMap.remove(hash);
						resultMap.put(hash, result);
						if (isALlDone()) {
							isRunning = false;
						}
					}
				});
				task.execute(params);
			}
		} else if (runningMode == RunningMode.SERIAL) {
			runningQueue = new ArrayBlockingQueue<MultiTask>(tasks.size());
			runningQueue.addAll(tasks);
			try {
				MultiTask task = runningQueue.poll(1, TimeUnit.SECONDS);
				runParallelTask(task, params);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	void runParallelTask(MultiTask task, final Object... params) {
		if (task != null) {
			final int hash = task.hashCode();
			runMap.put(hash, task);
			task.setOnTaskFinishListener(new OnTaskFinishListener() {

				@Override
				public void onFinish(boolean result) {
					runMap.remove(hash);
					resultMap.put(hash, result);
					if (!isALlDone()) {
						try {
							runParallelTask(
									runningQueue.poll(1, TimeUnit.SECONDS),
									params);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}else{
						if(onFinishListener != null)
							onFinishListener.onFinish();
					}
				}
			});
			task.execute(params);
		}
	}

	public void addTask(MultiTask task) {
		if (isRunning) {
			throw new AlreadyRunningException();
		}
		this.tasks.add(task);
	}

	public static interface OnFinishListener {
		public void onFinish();
	}

	public static interface OnTaskFinishListener {
		public void onFinish(boolean result);
	}

	public static class AlreadyRunningException extends RuntimeException {
		private static final long serialVersionUID = 4667739804998726246L;

		public AlreadyRunningException() {
			super(
					"All of tasks already finished, please create another TaskQueue !!!");
		}

		public AlreadyRunningException(String string) {
			super(string);
		}
	}

	public RunningMode getRunningMode() {
		return runningMode;
	}

	public void setRunningMode(RunningMode runningMode) {
		this.runningMode = runningMode;
	}

	public OnFinishListener getOnFinishListener() {
		return onFinishListener;
	}

	public void setOnFinishListener(OnFinishListener onFinishListener) {
		this.onFinishListener = onFinishListener;
	}

	public boolean getResult(MultiTask task) {
		return resultMap.get(task.hashCode());
	}

	public boolean isALlDone() {
		return resultMap.size() == tasks.size() && runMap.size() == 0;
	}

	public boolean isOK() {
		if (!isALlDone())
			return false;

		Iterator<Integer> keys = resultMap.keySet().iterator();
		while (keys.hasNext()) {
			Integer key = keys.next();
			if (!resultMap.get(key))
				return false;
		}
		return true;
	}
}
