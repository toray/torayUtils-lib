package com.toraysoft.utils.download;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DLTask extends Thread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7879712306224184813L;

//	private URL url;
	private List<URL> urls;
	private Map<String, String> fileNames;
//	private File file;
	private File fileSaveDir;
	private int threadQut; // 下载线程数量，用户可定制
	private long contentLen; // 下载文件长度
	private long completedTot; // 当前下载完成总数
	private String curPercent; // 下载百分比

	private DLThread[] dlThreads;
	private DLEngine engine;
	private boolean flag =true;
	
	static int TIMEOUT = 30 * 1000;

	public DLTask(DLEngine engine, int threadQut, 
			List<String> strUrls,Map<String, String> fileNames, File fileSaveDir) {
		this.engine = engine;
		this.threadQut = threadQut;
		this.fileSaveDir = fileSaveDir;
		this.dlThreads = new DLThread[strUrls.size()*threadQut];
		this.fileNames = fileNames;
		try {
			urls = new ArrayList<URL>();
			for (String string : strUrls) {
				urls.add(new URL(string)); 
			}
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void run() {
		newTask();
	}

	/**
	 * 新建任务时被调用，通过连接资源获取资源相关信息，并根据具体长度创建线程块， 线程创建完毕后，即刻通过线程池进行调度
	 * 
	 * @throws RuntimeException
	 */
	private void newTask() throws RuntimeException {
		try {
			boolean isFinish = true;
			File file = null;
			for(int k=0;k<urls.size();k++){
				if (!flag)
					break;
				URL url = urls.get(k);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				long contentLen = con.getContentLength();
				con.setConnectTimeout(TIMEOUT);
				if (!flag)
					break;
				this.contentLen += contentLen;
				if (contentLen <= 0) {
					// System.out.println("无法获取资源长度，中断下载进程");
					if(engine==null||engine.getListener()==null){
						return;
					}
					this.engine.getListener().onDownloadError(engine.get_id(),-1);
					return;
				}
				this.engine.saveContentLen(contentLen);
				if (!fileSaveDir.exists())
					fileSaveDir.mkdirs();
				if(fileNames!=null && fileNames.containsKey(url.toString())
						&& fileNames.get(url.toString())!=null){
					file = new File(fileSaveDir, fileNames.get(url.toString()));
				}else{
					file = new File(fileSaveDir, url.toString().hashCode() + "");
				}
				// file = new File(fileSaveDir,isEncrypt?
				// MD5Util.encode(this.url.toString()):this.url.toString());
				// int fileCnt = 1;
				// while (file.exists()) {
				// file = new File(filename += (fileCnt + FILE_POSTFIX));
				// fileCnt++;
				// }
				// long freespace = file.getFreeSpace();
				// if (contentLen < freespace) {
				// System.out.println("磁盘空间不够。");
				// return;
				// }
				Map<Integer, Long> cache = this.engine
						.getCache(url.toString());
				if (cache!=null && !cache.isEmpty() && file.exists()) {
					for (Map.Entry<Integer, Long> entry : cache.entrySet()) {
						// this.engine.getData().put(entry.getKey(),
						// entry.getValue());//把各条线程已经下载的数据长度放入data中
						this.completedTot += entry.getValue();
					}
					if(file.length()==0 || file.length()<this.completedTot){
						cache = new HashMap<Integer, Long>();
						cache.clear();
					}
				}
				// else if(file.length()==contentLen){
				// this.engine.getListener().onDownloadFinish();
				// return;
				// }
				else {
					cache = new HashMap<Integer, Long>();
					cache.clear();
				}
				if (file.length() == contentLen) {
//					this.engine.getListener().onDownloadFinish();
					continue;
				}
				isFinish = false;
				this.engine.getData().clear();
				long subLen = (contentLen % threadQut) == 0 ? (contentLen / threadQut)
						: (contentLen / threadQut) + 1;
				for (int i = k*threadQut; i < (k+1)*threadQut; i++) {
					if (!flag)
						break;
					int m = i-k*threadQut;
					long start = cache.containsKey(i + 1) ? subLen * m
							+ cache.get(i + 1) : subLen * m;
					long end = subLen * (m + 1) - 1;
					if (cache.containsKey(i + 1))
						this.engine.getData().put(i + 1, cache.get(i + 1));
					else
						this.engine.getData().put(i + 1, 0L);
					long readyByte = this.engine.getData().get(i + 1);
					end = end > contentLen ? contentLen : end;
					DLThread thread = new DLThread(this, url,file, i + 1, start, end,
							readyByte);
					dlThreads[i] = thread;
					DLEngine.pool.execute(dlThreads[i]);
				}
//				this.engine.delete(engine.get_id());
				this.engine.save();
				if (!flag)
					break;
			}
			if(isFinish){
				this.engine.getListener().onDownloadFinish(engine.get_id());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		
	}

//	public void keepOn() {
//		for (int i = 0; i < threadQut; i++) {
//			if (dlThreads[i] != null && dlThreads[i].isFail()) {
//				Map<Integer, Long> cache = this.engine.getCache(this.url
//						.toString());
//				long subLen = (contentLen % threadQut) == 0 ? (contentLen / threadQut)
//						: (contentLen / threadQut) + 1;
//				long start = cache.containsKey(i + 1) ? subLen * i
//						+ cache.get(i + 1) : subLen * i;
//				long end = subLen * (i + 1) - 1;
//				if (cache.containsKey(i + 1))
//					this.engine.getData().put(i + 1, cache.get(i + 1));
//				else
//					this.engine.getData().put(i + 1, 0L);
//				long readyByte = this.engine.getData().get(i + 1);
//				end = end > contentLen ? contentLen : end;
//				DLThread thread = new DLThread(this, i + 1, start, end,
//						readyByte);
//				dlThreads[i] = thread;
//				DLEngine.pool.execute(dlThreads[i]);
//			}
//		}
//	}

	public void onDLPercent() {
		if(this.engine.getListener()!=null)
			this.engine.getListener().onDownloadPercent(engine.get_id(),this.getCurPercent()
					,this.completedTot);
	}

	public void onDLThreadFinish() {
		if (isComplete()) {
			this.engine.delete(this.engine.get_id());
			if(this.engine.getListener()!=null)
				this.engine.getListener().onDownloadFinish(engine.get_id());
		}
	}
	public void onDLThreadCancel() {
		if (isComplete()) {
			if(this.engine.getListener()!=null)
				this.engine.getListener().onDownloadCancel(engine.get_id());
		}
	}

	public void onDLError(int state) {
		if(this.engine.getListener()!=null)
			this.engine.getListener().onDownloadError(engine.get_id(),state);
	}

	/**
	 * 计算当前已经完成的长度并返回下载百分比的字符串表示，目前百分比均为整数
	 * 
	 * @return
	 */
	public String getCurPercent() {
		this.completeTot();
		curPercent = new BigDecimal(completedTot)
				.divide(new BigDecimal(this.contentLen), 2,
						BigDecimal.ROUND_HALF_EVEN)
				.divide(new BigDecimal(0.01), 0, BigDecimal.ROUND_HALF_EVEN)
				.toString();
		return curPercent;
	}

	private void completeTot() {
		completedTot = 0;
		for (DLThread t : dlThreads) {
			if(t!=null)
				completedTot += t.getReadByte();
		}
	}

	/**
	 * 判断全部线程是否已经下载完成，如果完成则返回true，相反则返回false
	 * 
	 * @return
	 */
	public boolean isComplete() {
		boolean completed = true;
		for (DLThread t : dlThreads) {
			if (t != null){
				completed = t.isFinished();
			}else{
				completed = false;
			}
			if (!completed) {
				break;
			}
		}
		return completed;
	}

	public void stopThread() {
		this.flag = false;
		for (DLThread t : dlThreads) {
			if (t != null)
				t.setFlag(false);
		}
	}

//	public URL getUrl() {
//		return url;
//	}
//
//	public void setUrl(URL url) {
//		this.url = url;
//	}

//	public File getFile() {
//		return file;
//	}
//
//	public void setFile(File file) {
//		this.file = file;
//	}

	// public String getFilename() {
	// return filename;
	// }
	//
	// public void setFilename(String filename) {
	// this.filename = filename;
	// }

	public DLEngine getEngine() {
		return engine;
	}

	public void setEngine(DLEngine engine) {
		this.engine = engine;
	}

	public long getCompletedTot() {
		return completedTot;
	}

	public void setCompletedTot(long completedTot) {
		this.completedTot = completedTot;
	}

	public long getContentLen() {
		return contentLen;
	}

	public void setContentLen(long contentLen) {
		this.contentLen = contentLen;
	}

}
