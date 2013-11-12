package com.ll.download.util;

import android.os.Handler;
import android.os.Message;

import com.ll.download.DownloadApp;
import com.ll.download.bean.DownloadInfo;
import com.ll.download.bean.DownloadInfo.OprationStatus;
import com.ll.download.util.DBProvider.TDownloadInfo;
import com.ll.download.util.DownloadThreadRunner.RunnerOpration;
import com.ll.download.util.DownloadThreadRunner.RunnerState;

public class DownloadTask{
    
    public interface ICallback{
        void onObtainDownloadInfo(DownloadInfo downloadInfo,int statusCode);
        void onStartDownload();
        void onDownloadFailed(int errCode);
        void onDownloadComplete();
        void onProgress(float progress);
        void onSpeed(float speed);
    }
    
    static final int MSG_OBTIN_DOWNLOAD_INFO_SUCCESS = 1;
    static final int MSG_UPDATE_PROGRESS = 2;
    static final int MSG_ON_DOWNLOAD_COMPLETE = 3;
    static final int MSG_ON_DOWNLOAD_FAILED = 4;
    static final int MSG_UPDATE_SPEED = 5;
    
    private DownloadThreadRunner mRunner;
    
    private int startPos;
    private int endPos;
    
    private long mReadPos;
    
    private RunnerState mState = RunnerState.IDEL;
    
    private ICallback mCallback;
    
    private DownloadInfo mDownloadInfo;
    
    public DownloadTask(String uri, String downloadDir,ICallback callback) {
        super();
        mCallback = callback;
        mDownloadInfo = new DownloadInfo(uri, downloadDir,mReadPos);
    }
    
    public synchronized void download(){
        if(mState == RunnerState.RUNNING){
            return;
        }
        mRunner = new DownloadThreadRunner(mDownloadInfo, mHandler);
        mState = RunnerState.RUNNING;
        updateOprationState(OprationStatus.STARTED);
        mRunner.execute(RunnerOpration.START);
    }

    public synchronized void pause(){
        if(mState == RunnerState.PAUSED){
            return;
        }
        mState = RunnerState.PAUSED;
        mRunner.execute(RunnerOpration.PAUSE);
        mReadPos = mRunner.getReadLen();
        updateOprationState(OprationStatus.PAUSED);
        mRunner = null;
    }

    private void updateOprationState(OprationStatus status){
        mDownloadInfo.setOprationType(status);
        DownloadApp
        .getAppContext()
        .getContentResolver()
        .update(DBProvider.TDownloadInfo.CONTENT_URI,
                mDownloadInfo.toContentValues(),
                TDownloadInfo.URL + "='" + mDownloadInfo.getUrl() + "'", null);
    }
    
    public synchronized void obtainDownloadInfo(){
        if(DBUtil.isBaseInfoObtained(mDownloadInfo.getUrl())){
            mDownloadInfo = DBUtil.getDownloadInfoByUrl(mDownloadInfo.getUrl());
            sendMsg(mDownloadInfo, MSG_OBTIN_DOWNLOAD_INFO_SUCCESS);
        } else {
            updateOprationState(OprationStatus.STARTED);
            mRunner = new DownloadThreadRunner(mDownloadInfo, mHandler);
            mRunner.updateRunnerState(RunnerState.RUNNING);
            mRunner.execute(RunnerOpration.OBTIN_DOWNLOADINFO);
        }
    }
    
    private void sendMsg(Object obj,int what){
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        msg.sendToTarget();
    }
    
    private  Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(mCallback == null){
                return;
            }
            switch (msg.what) {
                case MSG_OBTIN_DOWNLOAD_INFO_SUCCESS:
                    if (msg.obj instanceof DownloadInfo) {
                        mDownloadInfo = (DownloadInfo) msg.obj;
                        mDownloadInfo.setBaseInfoObtained(true);
                        String url = mDownloadInfo.getUrl();
                        if (DBUtil.isDownloadExist(url)) {
                            DownloadApp
                                    .getAppContext()
                                    .getContentResolver()
                                    .update(DBProvider.TDownloadInfo.CONTENT_URI,
                                            mDownloadInfo.toContentValues(),
                                            TDownloadInfo.URL + "='" + url + "'", null);
                        } else {
                            DownloadApp
                                    .getAppContext()
                                    .getContentResolver()
                                    .insert(DBProvider.TDownloadInfo.CONTENT_URI,
                                            mDownloadInfo.toContentValues());
                        }
                        mDownloadInfo = DBUtil.getDownloadInfoByUrl(url);
                        mCallback.onObtainDownloadInfo(mDownloadInfo, 0);
                    }
                    break;
                case MSG_UPDATE_PROGRESS:
                    if(msg.obj instanceof Float){
                        mCallback.onProgress((Float) msg.obj);
                    }
                    break;
                case MSG_ON_DOWNLOAD_COMPLETE:
                    mCallback.onDownloadComplete();
                    MultiTaskDownloader.getInstance().removeTask(mDownloadInfo.getUrl());
                    break;
                case MSG_ON_DOWNLOAD_FAILED:
                    mCallback.onDownloadFailed(-1);
                    break;
                case MSG_UPDATE_SPEED:
                    mCallback.onSpeed((Float) msg.obj);
                    break;

                default:
                    break;
            }
        }
    };
}
