
package com.ll.download.util;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ll.download.bean.DownloadInfo;
import com.ll.download.bean.DownloadInfo.OprationStatus;

public class DownloadThreadRunner implements Runnable {
    
    public enum RunnerOpration{
        OBTIN_DOWNLOADINFO,START,PAUSE
    }
    
    private static final String TAG = DownloadThreadRunner.class.getSimpleName();
    private static final String HTTP_CONTENT_DISPOSITION = "Content-disposition";
    private static final String HTTP_HEADER_FILE_NAME = "filename";
    private static final int BUF_SIZE = 1024 * 5;

    private ProgressHandler progressHandler;
    
    private Handler mHandler;
    
    public enum RunnerState {
        IDEL, RUNNING, PAUSED
    }
    
    private RunnerState runnerState = RunnerState.IDEL;
    
    private RunnerOpration mOpration;
    private DownloadInfo mDownloadInfo;
    
    public DownloadThreadRunner(DownloadInfo downloadInfo,Handler handler) {
        super();
        progressHandler = new ProgressHandler();
        mHandler = handler;
        mDownloadInfo = downloadInfo;
    }
    
    public void execute(RunnerOpration runnerOpration){
        mOpration = runnerOpration;
        switch (mOpration) {
            case OBTIN_DOWNLOADINFO:
                new Thread(this).start();
                break;
            case START:
                if(runnerState != RunnerState.RUNNING){
                    runnerState = RunnerState.RUNNING;
                    new Thread(this).start();
                }
                break;
            case PAUSE:
                if(runnerState != RunnerState.PAUSED ){
                    runnerState = RunnerState.PAUSED;
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void run() {
        if(mDownloadInfo == null){
            //TODO error message
            return;
        }
        URL url = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        try {
            url = new URL(mDownloadInfo.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","NetFox"); //must set 
            conn.addRequestProperty("RANGE", "bytes="+mDownloadInfo.getReadLen()+"-");
            conn.connect();
            Map<String, List<String>> header = conn.getHeaderFields();
            dumpHeader(header);
            if(mOpration == RunnerOpration.OBTIN_DOWNLOADINFO){
                String fileName = null;
                if (header != null && header.containsKey(HTTP_CONTENT_DISPOSITION)) {
                    fileName = decodeFileNameFromHeader(header);
                } else {
                    fileName = decodeFileNameFromUri(mDownloadInfo.getUrl());
                }
                mDownloadInfo.setFileName(fileName);
                mDownloadInfo.setFileSize(conn.getContentLength());
                mDownloadInfo.setOprationType(OprationStatus.STARTED);
                if(mHandler != null){
                    sendMsg(mDownloadInfo, DownloadTask.MSG_OBTIN_DOWNLOAD_INFO_SUCCESS);
                }
            } else if(mOpration == RunnerOpration.START){
                if (conn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED
                        || conn.getResponseCode() == HttpURLConnection.HTTP_OK
                        || conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    int totalLen = (int) (conn.getContentLength()+mDownloadInfo.getReadLen());
                    Logger.v(TAG, "totalLen="+totalLen+"]");
                    
                    byte buf[] = new byte[BUF_SIZE];
                    is = conn.getInputStream();
                    float progress = 0f;
                    File file = new File(mDownloadInfo.getSavedPath() + File.separator
                            + mDownloadInfo.getFileName());
                    if(!file.exists()){
                    	file.createNewFile();
                    }
                    Logger.v(TAG, "filePath="+file.getAbsolutePath()+"]");
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(mDownloadInfo.getReadLen());
                    progressHandler.onProgress(progress);
                    int tmpLen = -1;
                    while ((tmpLen = is.read(buf)) != -1 && runnerState != RunnerState.PAUSED) {
                        raf.write(buf, 0, tmpLen);
                        mDownloadInfo.setReadLen(mDownloadInfo.getReadLen() + tmpLen);
                        //calculate progress and send message
                        progress = (float) ((float) mDownloadInfo.getReadLen() / (float) totalLen) * 100;
                        BigDecimal bigDecimal = new BigDecimal(progress);
                        progress = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        progressHandler.onProgress(progress);
                        
                        //calculate download speed and send message
                        sendMsg((float)tmpLen / 1024f, DownloadTask.MSG_UPDATE_SPEED);
                    }
                    Logger.v(TAG, "run()[totalLen="+totalLen+",mDownloadInfo.getReadLen()="+mDownloadInfo.getReadLen()+"]");
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(raf != null){
                    raf.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void dumpHeader(Map<String, List<String>> header) {
        Logger.v(TAG, "dumpHeader()");
        if (header == null) {
            return;
        }
        Set<String> keys = header.keySet();
        for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
            String key = iter.next();
            Logger.v(TAG, "dumpHeader()[" + key + "=" + header.get(key) + "]");

        }
    }

    private String decodeFileNameFromHeader(Map<String, List<String>> header) {
        String fileName = null;
        List<String> contentDisposition = header.get(HTTP_CONTENT_DISPOSITION);
        if (contentDisposition != null && !contentDisposition.isEmpty()
                && contentDisposition.get(0) != null) {
            String[] tmpStr = contentDisposition.get(0).split(";");
            if (tmpStr != null && tmpStr.length > 1) {
                fileName = tmpStr[1].replace(HTTP_HEADER_FILE_NAME, "").replace("=", "")
                        .replace("\"", "");
                Logger.v(TAG, "run()[fileName=" + fileName + "]");
            }
        }
        return fileName;
    }

    private String decodeFileNameFromUri(final String uri) {
        String fileName = null;
        String uriPostfix = uri.substring(uri.lastIndexOf('/') + 1, uri.length());
        if (uriPostfix.indexOf("?") > 0) {
            fileName = uriPostfix.substring(0, uriPostfix.indexOf("?"));
        } else {
            fileName = uriPostfix;
        }
        Logger.v(TAG, "fileName=" + fileName);
        return fileName;
    }

    
    private class ProgressHandler{
        private float previousProgress = -1f;
        public void onProgress(float progress){
            if(progress == previousProgress){
                return;
            }
            Logger.v(TAG, "ProgressHandler.onProgress()[progress="+progress+"]");
            if(mHandler != null){
                sendMsg(progress, DownloadTask.MSG_UPDATE_PROGRESS);
                if((int)progress == 100){
                    sendMsg(null, DownloadTask.MSG_ON_DOWNLOAD_COMPLETE);
                }
            }
        }
    }
    
    private void sendMsg(Object obj,int what){
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        msg.sendToTarget();
    }
    
    public void updateRunnerState(RunnerState state){
        runnerState = state;
    }
    
    public long getReadLen(){
        return mDownloadInfo.getReadLen();
    }
}
