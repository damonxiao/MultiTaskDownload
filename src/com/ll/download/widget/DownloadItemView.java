
package com.ll.download.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ll.download.R;
import com.ll.download.bean.DownloadInfo;
import com.ll.download.bean.DownloadInfo.OprationStatus;
import com.ll.download.util.MultiTaskDownloader;
import com.ll.download.util.DownloadTask.ICallback;

public class DownloadItemView extends LinearLayout implements OnClickListener,ICallback{

    private TextView mFileName;
    private TextView mFileSize;
    private TextView mProgressTxt;
    private TextView mSpeed;
    private ProgressBar mProgressBar;
    private ImageView mOprationView;;

    private DownloadInfo mDownloadInfo;

    private boolean mInflateFinish;

    public DownloadItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFileName = (TextView) findViewById(R.id.file_name);
        mFileSize = (TextView) findViewById(R.id.file_size);
        mProgressTxt = (TextView) findViewById(R.id.progress_txt);
        mSpeed = (TextView) findViewById(R.id.speed);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_view);
        mOprationView = (ImageView) findViewById(R.id.opration_btn);
        mOprationView.setOnClickListener(this);
        mInflateFinish = true;
        bindData(mDownloadInfo);
    }

    public void bindData(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
        if (mDownloadInfo != null && mInflateFinish) {
            mFileName.setText(mDownloadInfo.getFileName());
            mFileSize.setText(getFileSizeStr(mDownloadInfo.getFileSize()));
            mProgressTxt.setText(mDownloadInfo.getProgress() + "%");
            mProgressBar.setProgress((int) mDownloadInfo.getProgress());
            switch (mDownloadInfo.getOprationType()) {
                case PAUSED:
                    mOprationView.setImageResource(R.drawable.start_btn_selector);
                    break;
                case STARTED:
                    mOprationView.setImageResource(R.drawable.pause_btn_selector);
                    break;
                case DONE:
                    break;
                default:
                    break;
            }
        }
    }
    
    private String getFileSizeStr(long fileLength){
        return String.valueOf((float)fileLength / (float)(1024*1024))+"MB";
    }

    @Override
    public void onStartDownload() {
        
    }
    
    @Override
    public void onProgress(float progress) {
         mProgressBar.setProgress((int)progress);
         mProgressTxt.setText(progress+"%");
    }
    
    @Override
    public void onDownloadFailed(int errCode) {
        
    }
    
    @Override
    public void onDownloadComplete() {
        
    }

    @Override
    public void onObtainDownloadInfo(DownloadInfo downloadInfo, int statusCode) {
        mDownloadInfo = downloadInfo;
        MultiTaskDownloader.getInstance().start(mDownloadInfo.getUrl());
        bindData(mDownloadInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.opration_btn:
                String url = mDownloadInfo != null ? mDownloadInfo.getUrl() : null;
                if(url == null){
                    break;
                }
                if(mDownloadInfo.getOprationType() == OprationStatus.PAUSED){
                    mOprationView.setImageResource(R.drawable.pause_btn_selector);
                    if(!MultiTaskDownloader.getInstance().isTaskObtianed(url)){
                        MultiTaskDownloader.getInstance().obtainDownloadInfo(url, this);
                    } else {
                        MultiTaskDownloader.getInstance().start(url);
                    }
                } else if(mDownloadInfo.getOprationType() == OprationStatus.STARTED){
                    mOprationView.setImageResource(R.drawable.start_btn_selector);
                    MultiTaskDownloader.getInstance().pause(url);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onSpeed(float speed) {
        mSpeed.setText(speed+"KB/S");
    }

}
