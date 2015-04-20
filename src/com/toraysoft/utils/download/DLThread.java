package com.toraysoft.utils.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

public class DLThread extends Thread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2544570689022922170L;

	private static int BUFFER_SIZE = 8096;
	transient private DLTask dlTask;
	private int id;
	private URL url;
	private long startPos;
	private long endPos;
	private long readByte;
	transient private File file;
	private boolean finished;
	private boolean flag = true;
	private boolean fail;
	
	static int TIMEOUT = 30 * 1000;

	public DLThread(DLTask dlTask,URL url,File file, int id, long startPos, long endPos,
			long readByte) {
		this.dlTask = dlTask;
		this.id = id;
		this.url = url;
		this.startPos = startPos;
		this.endPos = endPos;
		this.file = file;
		this.finished = false;
		this.fail = false;
		this.readByte = readByte;
	}

	@Override
	public void run() {
//		System.out.println("线程" + id + "启动......");
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		byte[] buf = new byte[BUFFER_SIZE];
		URLConnection con = null;
		try {
			con = url.openConnection();
			con.setConnectTimeout(TIMEOUT);
			con.setReadTimeout(TIMEOUT);
			con.setAllowUserInteraction(true);
			con.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
			fos = new RandomAccessFile(file, "rw");
			fos.seek(startPos);
			bis = new BufferedInputStream(con.getInputStream());
			
			while (startPos < endPos) {                                                                                                              
				if (!flag)
					break;
				int len = bis.read(buf, 0, BUFFER_SIZE);
				if (len == -1) {
					break;
				}
				fos.write(buf, 0, len);
				startPos = startPos + len;
				if (startPos > endPos) {
					readByte += len - (startPos - endPos) + 1; // 获取正确读取的字节数
				} else {
					readByte += len;
				}
				if (!flag)
					break;
				this.dlTask.onDLPercent();
				this.dlTask.getEngine().update(this.id, readByte);
			}
//			System.out.println("线程" + id + "已经下载完毕。");
			this.finished = true;
			if (flag) {
				this.dlTask.onDLThreadFinish();
			}else{
				this.dlTask.onDLThreadCancel();
			}
			bis.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			// throw new RuntimeException(ex);
			this.fail = true;
//			this.dlTask.onDLError(Constant.DLERROR_IO);
		} finally {
			this.finished = true;
		}
	}

	public long getReadByte() {
		return readByte;
	}

	public void setReadByte(long readByte) {
		this.readByte = readByte;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isFail() {
		return fail;
	}

	public void setFail(boolean fail) {
		this.fail = fail;
	}
}
