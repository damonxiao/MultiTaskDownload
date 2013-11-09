
package com.ll.download.util;

public class DownloadInfo {
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
    
    

}
