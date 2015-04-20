package com.toraysoft.utils.download;

import java.util.Map;

/**
 * 
 */
public class FileService {
	
	private IDLEnv env;
	
	
	public FileService(IDLEnv env){
		this.env = env;
	}

	/**
	 * 获取每条线程已经下载的文件长度
	 * 
	 * @param key
	 * @return
	 */
	public synchronized Map<Integer, Long> getData(String _id) {
		if(env!=null)
			return env.getDLData(_id);
		return null;
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 * 
	 * @param key,link
	 * @param map
	 */
	public synchronized void save(String _id, Map<Integer, Long> map) {
		if(env!=null)
			env.saveDLData(_id, map);
	}

	/**
	 * 实时更新每条线程已经下载的文件长度
	 * 
	 * @param key
	 * @param map
	 */
	public synchronized void update(String _id,  Map<Integer, Long> map) {
		if(env!=null)
			env.updateDLData(_id, map);
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * 
	 * @param key
	 */
	public synchronized void delete(String _id) {
		if(env!=null)
			env.deleteDLData(_id);
	}
	
	/**
	 * 查看文件是否存在记录 说明未下载完整
	 * 
	 * @param key
	 * @return
	 */
	
	public synchronized void saveContentLen(String _id, long contentLen){
		if(env!=null)
			env.saveContentLen(_id, contentLen);
	}
	
}
