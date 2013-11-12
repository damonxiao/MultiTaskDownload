package com.ll.download.util;

import android.database.Cursor;

import com.ll.download.DownloadApp;
import com.ll.download.bean.DownloadInfo;
import com.ll.download.util.DBProvider.TDownloadInfo;

public class DBUtil {
    public static boolean isDownloadExist(String url) {
        if (url == null) {
            return false;
        }
        Cursor cursor = DownloadApp.getAppContext().getContentResolver()
                .query(TDownloadInfo.CONTENT_URI, new String[] {
                        TDownloadInfo.ID
                }, TDownloadInfo.URL + "='" + url + "'", null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }
    
    public static DownloadInfo getDownloadInfoByUrl(String url) {
        if (url == null) {
            return null;
        }
        DownloadInfo downloadInfo = null;
        Cursor cursor = DownloadApp
                .getAppContext()
                .getContentResolver()
                .query(TDownloadInfo.CONTENT_URI, null,
                        TDownloadInfo.URL + "='" + url + "'", null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToNext()) {
            downloadInfo = new DownloadInfo(cursor);
            cursor.close();
        }
        return downloadInfo;
    }
    
    public static boolean isBaseInfoObtained(String url) {
        if (url == null) {
            return false;
        }
        boolean obeained = false;
        Cursor c = null;
        try {
            c = DownloadApp
                    .getAppContext()
                    .getContentResolver()
                    .query(TDownloadInfo.CONTENT_URI, new String[] {
                            TDownloadInfo.ID
                    }, TDownloadInfo.URL + "=? and " + TDownloadInfo.BASEINFO_OBTAINED + "=?",
                            new String[] {
                                    url, String.valueOf(1)
                            }, null);
            obeained = c != null && c.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return obeained;
    }
}
