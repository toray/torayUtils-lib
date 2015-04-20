package com.toraysoft.utils.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DLEngine {

	public static ExecutorService pool = Executors.newCachedThreadPool();

	private FileService fileService;
	private DLTask mDLtask;
	private String _id;
//	private String url;
	private List<String> urls;
	private Map<String, String> fileNames;
	private File fileSaveDir;
	private int threadQut;
	private DLListener listener;
	private boolean isKeep = true;
	// private boolean isEncrypt;

	/* 缓存各线程下载的长度 */
	private Map<Integer, Long> data = new ConcurrentHashMap<Integer, Long>();
	
	public DLEngine(String _id,List<String> urls,Map<String,String> fileNames,File fileSaveDir, int threadQut
						,IDLEnv env){
		this.fileService = new FileService(env);
		this.urls = urls;
		this.fileNames = fileNames;
		this._id = _id;
		this.fileSaveDir = fileSaveDir;
		this.threadQut = threadQut;
	}

	public DLEngine(String _id, String url, File fileSaveDir,
			String fileName, int threadQut, IDLEnv env) {
		// new DLEngine(context, url, fileSaveDir, threadQut, true);
		this.fileService = new FileService(env);
//		this.url = url;
		this.urls = new ArrayList<String>();
		urls.add(url);
		this._id = _id;
		this.fileNames = new HashMap<String, String>();
		fileNames.put(url, fileName);
		this.fileSaveDir = fileSaveDir;
		this.threadQut = threadQut;
		// this.isEncrypt = true;
	}

	// public DLEngine(String url, File fileSaveDir, int threadQut,boolean
	// isEncrypt) {
	// this.fileService = new FileService(env);
	// this.url = url;
	// this.fileSaveDir = fileSaveDir;
	// this.threadQut = threadQut;
	// this.isEncrypt = isEncrypt;
	// }

	public int download(DLListener listener) {
		this.listener = listener;
		this.createDLTask(threadQut, urls, fileNames, fileSaveDir);
		return 0;
	}
	
	public void download() {
		this.createDLTask(threadQut, urls, fileNames, fileSaveDir);
	}
	
	private void createDLTask(int threadQut, List<String> urls,Map<String, String> fileNames, File fileSaveDir) {
		isKeep = true;
		this.mDLtask = new DLTask(DLEngine.this, threadQut, urls,fileNames, fileSaveDir);
		pool.execute(mDLtask);
	}
	
//	private void createDLTask(int threadQut, String url, File fileSaveDir) {
//
//		this.mDLtask = new DLTask(DLEngine.this, threadQut, url, fileSaveDir);
//		pool.execute(mDLtask);
//	}

	/**
	 * 更新指定线程最后下载的位置
	 * 
	 * @param threadId
	 *            线程id
	 * @param pos
	 *            最后下载的位置
	 */
	protected synchronized void update(int threadId, long pos) {
		this.data.put(threadId, pos);
		this.fileService.update(this._id,
				this.data);
	}

	protected synchronized void save() {
		this.fileService.save(this._id, this.data);
	}

	protected synchronized Map<Integer, Long> getCache(String url) {
		return this.fileService.getData(this._id);
	}

	protected synchronized void delete(String url) {
//		this.fileService.delete(this._id);
	}

	protected synchronized void saveContentLen(long contentLen) {
		this.fileService.saveContentLen(_id, contentLen);
	}
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Map<Integer, Long> getData() {
		return data;
	}

	public void setData(Map<Integer, Long> data) {
		this.data = data;
	}

	public DLListener getListener() {
		return listener;
	}

	public void setListener(DLListener listener) {
		this.listener = listener;
	}

	public DLTask getmDLtask() {
		return mDLtask;
	}

	public void setmDLtask(DLTask mDLtask) {
		this.mDLtask = mDLtask;
	}

	public boolean isKeep() {
		return isKeep;
	}

//	public void setKeep(boolean isKeep) {
//		this.isKeep = isKeep;
//	}
	
	public void stop(){
		this.isKeep = false;
		if(this.mDLtask!=null){
			mDLtask.stopThread();
		}
	}
	
	
}
