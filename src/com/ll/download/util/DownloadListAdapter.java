package com.ll.download.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ll.download.R;
import com.ll.download.bean.DownloadInfo;
import com.ll.download.widget.DownloadItemView;

import java.util.List;

public class DownloadListAdapter extends BaseAdapter {

    private List<DownloadInfo> mDatas;
    
    private LayoutInflater mInflater;
    
    public DownloadListAdapter(Context context,List<DownloadInfo> datas){
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDatas = datas;
    }
    
    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public void updateDataItem(int index,DownloadInfo data){
        mDatas.add(index, data);
        notifyDataSetChanged();
    }
    
    public void refreshData(List<DownloadInfo> datas){
        mDatas = datas;
        notifyDataSetChanged();
    }
    
    @Override
    public Object getItem(int position) {
        return mDatas == null || mDatas.size() < position + 1 ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.download_item, null);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DownloadInfo data = mDatas.get(position);
        DownloadItemView downloadItemView = (DownloadItemView) convertView;
        downloadItemView.bindData(data);
        convertView.setTag(holder);
        return convertView;
    }

    static class ViewHolder{
        TextView fileName;
        TextView fileSize;
        TextView progressTxt;
        ProgressBar progressBar;
        View oprationView;
    }
    

}
