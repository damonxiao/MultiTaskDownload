package com.ll.download.util;

import com.ll.download.util.DownloadTask.ICallback;
import com.ll.download.util.DownloadThreadRunner.RunnerState;

import java.util.HashMap;


public class MultiTaskDownloader {
    
    
    private static Object mObjLock = new Object();
    
    private static MultiTaskDownloader mInstance;
    
    private HashMap<String, DownloadTask> mTasks;
    
    private MultiTaskDownloader(){
        mTasks = new HashMap<String, DownloadTask>();
    }
    
    public static MultiTaskDownloader getInstance(){
        synchronized (mObjLock) {
            if(mInstance == null ){
                mInstance = new MultiTaskDownloader();
            }
        }
        return mInstance;
    }
    
    public void start(String url) {
        if (mTasks.containsKey(url)) {
            mTasks.get(url).download();
        }
    }
    
    public void pause(String url){
        if(mTasks.containsKey(url)){
            mTasks.get(url).pause();
        }
    }
    
    public void resume(String url){
        if(mTasks.containsKey(url)){
            mTasks.get(url).download();
        }
    }
    
    public void obtainDownloadInfo(String url, ICallback callback) {
        synchronized (mTasks) {
            mTasks.put(url, new DownloadTask(url, Const.DOWNLOAD_DIR, callback));
            mTasks.get(url).obtainDownloadInfo();
        }
    }
    
    public boolean isTaskObtained(String url){
        return mTasks.containsKey(url);
    }

    public void removeTask(String url){
        if(mTasks.containsKey(url)){
            mTasks.remove(url);
        }
    }
}
