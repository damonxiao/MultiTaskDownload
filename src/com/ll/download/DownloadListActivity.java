
package com.ll.download;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.ll.download.bean.DownloadInfo;
import com.ll.download.util.DBProvider.TDownloadInfo;
import com.ll.download.util.DBUtil;
import com.ll.download.util.DownloadListAdapter;
import com.ll.download.util.DownloadTask.ICallback;
import com.ll.download.util.MultiTaskDownloader;
import com.ll.download.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class DownloadListActivity extends Activity {
    private static final String TAG = DownloadListActivity.class.getSimpleName();
    private String url = "http://dl.google.com/android/ADT-22.3.0.zip";
//    private String url = "http://distribution.hexxeh.net/archive/vanilla/4028.0.2013_04_20_1810-r706c4144/ChromeOS-Vanilla-4028.0.2013_04_20_1810-r706c4144-VirtualBox.zip";
    
    private ListView mDownloadListView;
    
    private DownloadListAdapter mAdapter;
    
    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            Logger.d(TAG, "onChange()[selfChange="+selfChange+"]");
            refreshData();
        };
        
        public void onChange(boolean selfChange, android.net.Uri uri) {
            Logger.d(TAG, "onChange()[selfChange="+selfChange+",uri="+uri.toString()+"]");
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_list);
        mDownloadListView = (ListView) findViewById(R.id.download_list);
        
        mAdapter = new DownloadListAdapter(this, getAllDownloadInfos());
        mDownloadListView.setAdapter(mAdapter);
        getContentResolver().registerContentObserver(TDownloadInfo.CONTENT_URI, true, mObserver);
        if(!DBUtil.isDownloadExist(url)){
            newDownloadTask(url);
        }
    }
    
    private void newDownloadTask(String url){
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setUrl(url);
        getContentResolver().insert(TDownloadInfo.CONTENT_URI, downloadInfo.toContentValues());
    }
    
    private void refreshData(){
        mAdapter.refreshData(getAllDownloadInfos());
    }
    
    private List<DownloadInfo> getAllDownloadInfos(){
        List<DownloadInfo> downloadInfos = new ArrayList<DownloadInfo>();
        Cursor cursor = getContentResolver().query(TDownloadInfo.CONTENT_URI, null, null, null, null);
        while(cursor != null && cursor.getCount() > 0 && cursor.moveToNext()){
            downloadInfos.add(new DownloadInfo(cursor));
        }
        return downloadInfos;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
    }
    

}
