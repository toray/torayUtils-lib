package com.toraysoft.utils.download;

public interface DLListener {
	public void onDownloadPercent(String _id,String percent,long completedTot);
	
	public void onDownloadFinish(String _id);
	
	public void onDownloadCancel(String _id);
	
	public void onDownloadError(String _id,int state);
	
}
