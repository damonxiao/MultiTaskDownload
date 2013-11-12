
package com.ll.download.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.ll.download.util.DBProvider.TDownloadInfo;

public class DownloadInfo {
    public enum OprationStatus {
        PAUSED(1), STARTED(2), DONE(3);
        private int value;

        private OprationStatus(int value) {
            this.value = value;
        }

        public static OprationStatus getOprationType(int value) {
            switch (value) {
                case 1:
                default:
                    return PAUSED;
                case 2:
                    return STARTED;
                case 3:
                    return DONE;
            }
        }

        public int getValue() {
            return value;
        }
    }
    
	private long id;
    private String fileName;
    private String url;
    private String savedPath;
    private long fileSize;

    private float progress;
    
    private long readLen;
    
    private OprationStatus oprationType = OprationStatus.PAUSED;
    
    private boolean baseInfoObtained;
    
    public DownloadInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public DownloadInfo(String url, String savedPath, long readLen) {
        super();
        this.url = url;
        this.savedPath = savedPath;
        this.readLen = readLen;
    }

    public DownloadInfo(Cursor c){
    	updateInfo(c);
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
	
	public OprationStatus getOprationType() {
        return oprationType;
    }

    public void setOprationType(OprationStatus oprationType) {
        this.oprationType = oprationType;
    }

    
    
    public boolean isBaseInfoObtained() {
        return baseInfoObtained;
    }

    public void setBaseInfoObtained(boolean baseInfoObtained) {
        this.baseInfoObtained = baseInfoObtained;
    }

    public ContentValues toContentValues(){
	    ContentValues values = new ContentValues();
	    values.put(TDownloadInfo.FILE_NAME, fileName);
	    values.put(TDownloadInfo.URL, url);
	    values.put(TDownloadInfo.SAVED_PATH, savedPath);
	    values.put(TDownloadInfo.FILE_SIZE, fileSize);
	    values.put(TDownloadInfo.OPRATION_TYPE, oprationType.getValue());
        values.put(TDownloadInfo.BASEINFO_OBTAINED, baseInfoObtained ? 1 : 0);
	    return values;
	}

    public void updateInfo(Cursor c) {
        if (c != null) {
            id = c.getLong(c.getColumnIndex(TDownloadInfo.ID));
            fileName = c.getString(c.getColumnIndex(TDownloadInfo.FILE_NAME));
            url = c.getString(c.getColumnIndex(TDownloadInfo.URL));
            savedPath = c.getString(c.getColumnIndex(TDownloadInfo.SAVED_PATH));
            fileSize = c.getLong(c.getColumnIndex(TDownloadInfo.FILE_SIZE));
            int oprationTypeValue = c.getInt(c.getColumnIndex(TDownloadInfo.OPRATION_TYPE));
            oprationType = OprationStatus.getOprationType(oprationTypeValue);
            baseInfoObtained = c.getInt(c.getColumnIndex(TDownloadInfo.BASEINFO_OBTAINED)) == 1;
        }
    }

    @Override
    public String toString() {
        return "DownloadInfo [id=" + id + ", fileName=" + fileName + ", url=" + url
                + ", savedPath=" + savedPath + ", fileSize=" + fileSize + ", progress=" + progress
                + ", readLen=" + readLen + ", oprationType=" + oprationType + ", baseInfoObtained="
                + baseInfoObtained + "]";
    }
	
}
