package com.toraysoft.utils.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class DLManager {

	static DLManager mDLManager;

	Map<String, DLEngine> queue;

	private DLManager() {
		queue = new HashMap<String, DLEngine>();
	}

	public static DLManager get() {
		if (mDLManager == null)
			mDLManager = new DLManager();
		return mDLManager;
	}

	public synchronized void download(Context mContext, final String id,
			final String url, File fileSaveDir, String fileName,
			final DLListener l) {
		download(id, url, fileSaveDir, fileName, l, new DefaultDLEnv(mContext));
	}

	public synchronized void download(final String id, final String url,
			File fileSaveDir, String fileName, final DLListener l,
			final IDLEnv env) {
		DLListener listener = new DLListener() {

			@Override
			public void onDownloadPercent(String _id, String percent,
					long completedTot) {
				if (l != null) {
					l.onDownloadPercent(_id, percent, completedTot);
				}

			}

			@Override
			public void onDownloadFinish(String _id) {
				queue.remove(url);
				if (l != null) {
					l.onDownloadFinish(_id);
				}
			}

			@Override
			public void onDownloadError(String _id, int state) {
				queue.remove(url);
				if (l != null) {
					l.onDownloadError(_id, state);
				}
			}

			@Override
			public void onDownloadCancel(String _id) {
				queue.remove(url);
				if (l != null) {
					l.onDownloadCancel(_id);
				}
			}
		};
		if (queue.containsKey(url) && queue.get(url) != null) {
			queue.get(url).setListener(listener);
			return;
		}
		DLEngine mDLEngine = new DLEngine(id, url, fileSaveDir, fileName, 1,
				env);
		queue.put(url, mDLEngine);
		mDLEngine.setListener(listener);
		mDLEngine.download();
	}

	public void cancel(String url) {
		if (queue.containsKey(url)) {
			DLEngine mDLEngine = queue.get(url);
			if (mDLEngine != null)
				mDLEngine.stop();
			queue.remove(url);
		}
	}

	public static class DefaultDLEnv implements IDLEnv {

		Context mContext;
		public static String KEY_SHARED_DOWNLOAD_MANAGER = "download_manager_config";

		public DefaultDLEnv(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public Map<Integer, Long> getDLData(String _id) {
			Map<Integer, Long> data = new HashMap<Integer, Long>();
			data.put(1, getOffset());
			return data;
		}

		@Override
		public void saveDLData(String _id, Map<Integer, Long> map) {

		}

		@Override
		public void updateDLData(String _id, Map<Integer, Long> map) {
			setOffset(map.get(1));
		}

		@Override
		public void deleteDLData(String _id) {

		}

		@Override
		public void saveContentLen(String _id, long contentLen) {
			setLength(contentLen);
		}

		void setLength(long length) {
			getSP().edit().putLong("length", length).commit();
		}

		void setOffset(long offset) {
			getSP().edit().putLong("offset", offset).commit();
		}

		long getOffset() {
			return getSP().getLong("offset", 0);
		}

		SharedPreferences getSP() {
			return mContext.getSharedPreferences(KEY_SHARED_DOWNLOAD_MANAGER,
					Activity.MODE_PRIVATE);
		}
	}

}
