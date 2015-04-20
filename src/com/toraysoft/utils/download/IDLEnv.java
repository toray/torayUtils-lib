package com.toraysoft.utils.download;

import java.util.Map;

public interface IDLEnv {
	public Map<Integer, Long> getDLData(String _id);

	public void saveDLData(String _id, Map<Integer, Long> map);

	public void updateDLData(String _id, Map<Integer, Long> map);

	public void deleteDLData(String _id);

	public void saveContentLen(String _id, long contentLen);
	
}
