package com.ll.download;

import android.os.Handler;

import com.ll.download.DownloadThreadRunner.RunnerOpration;
import com.ll.download.DownloadThreadRunner.RunnerState;

public class DownloadTask{
    
    public interface ICallback{
        void onObtainDownloadInfo(DownloadInfo downloadInfo,int statusCode);
        void onStartDownload();
        void onDownloadFailed(int errCode);
        void onDownloadComplete();
        void onProgress(float progress);
    }
    
    static final int MSG_OBTIN_DOWNLOAD_INFO_SUCCESS = 1;
    static final int MSG_UPDATE_PROGRESS = 2;
    static final int MSG_ON_DOWNLOAD_COMPLETE = 3;
    static final int MSG_ON_DOWNLOAD_FAILED = 4;
    
    private DownloadThreadRunner mRunner;
    
    private int startPos;
    private int endPos;
    
    private String mUri;
    
    private String mDownloadDir;
    
    private long mReadPos;
    
    private RunnerState mState = RunnerState.IDEL;
    
    private ICallback mCallback;
    
    public DownloadTask(String uri, String downloadDir,ICallback callback) {
        super();
        this.mUri = uri;
        this.mDownloadDir = downloadDir;
        mCallback = callback;
    }
    
    public synchronized void download(){
        if(mState == RunnerState.RUNNING){
            return;
        }
        mRunner = new DownloadThreadRunner(mUri, mDownloadDir, mReadPos, mHandler);
        mState = RunnerState.RUNNING;
        mRunner.execute(RunnerOpration.START);
    }

    public synchronized void pause(){
        if(mState == RunnerState.PAUSED){
            return;
        }
        mState = RunnerState.PAUSED;
        mRunner.execute(RunnerOpration.PAUSE);
        mReadPos = mRunner.getReadLen();
        mRunner = null;
    }

    public synchronized void obtainDownloadInfo(){
        mRunner = new DownloadThreadRunner(mUri, mDownloadDir, mReadPos, mHandler);
        mRunner.updateRunnerState(RunnerState.RUNNING);
        mRunner.execute(RunnerOpration.OBTIN_DOWNLOADINFO);
    }
    
    private  Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(mCallback == null){
                return;
            }
            switch (msg.what) {
                case MSG_OBTIN_DOWNLOAD_INFO_SUCCESS:
                    if(msg.obj instanceof DownloadInfo){
                        mCallback.onObtainDownloadInfo((DownloadInfo) msg.obj, 0);
                    }
                    break;
                case MSG_UPDATE_PROGRESS:
                    if(msg.obj instanceof Float){
                        mCallback.onProgress((Float) msg.obj);
                    }
                    break;
                case MSG_ON_DOWNLOAD_COMPLETE:
                    mCallback.onDownloadComplete();
                    MultiTaskDownloader.getInstance().removeTask(mUri);
                    break;
                case MSG_ON_DOWNLOAD_FAILED:
                    mCallback.onDownloadFailed(-1);
                    break;

                default:
                    break;
            }
        }
    };
}
