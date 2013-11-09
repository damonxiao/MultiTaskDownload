
package com.ll.download;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ll.download.bean.DownloadInfo;
import com.ll.download.util.DBProvider;
import com.ll.download.util.DBProvider.TDownloadInfo;
import com.ll.download.util.DownloadTask.ICallback;
import com.ll.download.util.Logger;
import com.ll.download.util.MultiTaskDownloader;

public class DownloadListActivity extends Activity {
    private static final String TAG = DownloadListActivity.class.getSimpleName();
    private String url = "http://dl.google.com/android/ADT-22.3.0.zip";
//    private String url = "http://distribution.hexxeh.net/archive/vanilla/4028.0.2013_04_20_1810-r706c4144/ChromeOS-Vanilla-4028.0.2013_04_20_1810-r706c4144-VirtualBox.zip";
    
    private ProgressBar mProgressBar;
    private TextView mProgressView;
    private TextView mDownloadFilename;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        setContentView(R.layout.download_item);
//        ContentValues values = new ContentValues();
//        values.put(TDownloadInfo.FILE_NAME, "file name test");
//        values.put(TDownloadInfo.SAVED_PATH, "/mnt/sdcard");
//        getContentResolver().insert(DBProvider.TDownloadInfo.CONTENT_URI, values);
//        Cursor cursor = null;
//		try {
//			cursor = getContentResolver().query(TDownloadInfo.CONTENT_URI,
//					null, null, null, null);
//			while (cursor != null && cursor.getCount() > 0 && cursor.moveToNext()) {
//				DownloadInfo downloadInfo = new DownloadInfo(cursor);
//				Logger.d(TAG, "downloadinfo="+downloadInfo);
//			}
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//        setContentView(R.layout.);
//        mProgressBar = (ProgressBar) findViewById(R.id.download_progress);
//        mProgressBar.setMax(100);
//        mProgressView = (TextView) findViewById(R.id.progress_view);
//        mDownloadFilename = (TextView) findViewById(R.id.download_filename);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void handleOnClick(View v){
        Log.d(TAG, "handelOnClick");
        switch (v.getId()) {
            case R.id.start_download:
                if(!MultiTaskDownloader.getInstance().isTaskObtained(url)){
                    MultiTaskDownloader.getInstance().obtainDownloadInfo(url, mDownloadCallback);
                } else {
                    MultiTaskDownloader.getInstance().start(url);
                }
                break;
            case R.id.pause_download:
                MultiTaskDownloader.getInstance().pause(url);
                break;
            default:
                break;
        }
    }
    
    private ICallback mDownloadCallback = new ICallback() {
        
        @Override
        public void onStartDownload() {
            
        }
        
        @Override
        public void onProgress(float progress) {
            mProgressBar.setProgress((int)progress);
            mProgressView.setText(progress+"%");
        }
        
        @Override
        public void onDownloadFailed(int errCode) {
            
        }
        
        @Override
        public void onDownloadComplete() {
            
        }

        @Override
        public void onObtainDownloadInfo(DownloadInfo downloadInfo, int statusCode) {
            MultiTaskDownloader.getInstance().start(url);
            mDownloadFilename.setText(downloadInfo.getFileName()+downloadInfo.getSavedPath());
        }
    };

}
