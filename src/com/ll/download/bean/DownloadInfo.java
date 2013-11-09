
package com.ll.download.bean;

import com.ll.download.util.DBProvider.TDownloadInfo;

import android.database.Cursor;

public class DownloadInfo {
	private long id;
    private String fileName;
    private String url;
    private String savedPath;
    private long fileSize;

    private float progress;
    
    private long readLen;
    
    public DownloadInfo(String url, String savedPath, long readLen) {
        super();
        this.url = url;
        this.savedPath = savedPath;
        this.readLen = readLen;
    }

    public DownloadInfo(Cursor c){
    	if(c != null){
    		id = c.getLong(c.getColumnIndex(TDownloadInfo.ID));
    		fileName = c.getString(c.getColumnIndex(TDownloadInfo.FILE_NAME));
    		url = c.getString(c.getColumnIndex(TDownloadInfo.URL));
    		savedPath = c.getString(c.getColumnIndex(TDownloadInfo.SAVED_PATH));
    		fileSize = c.getLong(c.getColumnIndex(TDownloadInfo.FILE_SIZE));
    	}
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavedPath() {
        return savedPath;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public long getReadLen() {
        return readLen;
    }
    
    public void setReadLen(long readLen) {
        this.readLen = readLen;
    }

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "DownloadInfo [id=" + id + ", fileName=" + fileName + ", url="
				+ url + ", savedPath=" + savedPath + ", fileSize=" + fileSize
				+ ", progress=" + progress + ", readLen=" + readLen + "]";
	}

}
