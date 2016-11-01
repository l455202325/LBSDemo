package com.eddyedu.appinfo.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eddyedu.appinfo.R;

import java.util.List;

/**
 * 类作用：
 * Created by liubp on 16/11/1.
 */
public class AppInfoAdapter extends BaseAdapter {
    private List<AppInfo> data;
    private Context context;

    public AppInfoAdapter(List<AppInfo> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_appinfo,null);
            holder.icon = (ImageView) convertView.findViewById(R.id.app_icom);
            holder.pkgNameTV = (TextView) convertView.findViewById(R.id.item_pkgname);
            holder.nameTV = (TextView) convertView.findViewById(R.id.item_name);
            holder.MD5TV = (TextView) convertView.findViewById(R.id.item_md5);
            holder.SHA1TV = (TextView) convertView.findViewById(R.id.item_sha1);
            holder.versionTV = (TextView) convertView.findViewById(R.id.item_version);
            holder.versionNameTV = (TextView) convertView.findViewById(R.id.item_version_name);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo info = data.get(position);
        holder.icon.setImageDrawable(info.icon);
        holder.nameTV.setText("应用名称：" + info.getName());
        holder.pkgNameTV.setText("应用包名：" + info.getPackageName());
        holder.MD5TV.setText("MD5：" + info.getMd5());
        holder.SHA1TV.setText("SHA1：" + info.getSha1());
        holder.versionNameTV.setText("版本：" + info.getVersionName());
        holder.versionTV.setText("版本号：" + info.getVersion());

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView nameTV;
        TextView pkgNameTV;
        TextView MD5TV;
        TextView SHA1TV;
        TextView versionTV;
        TextView versionNameTV;
    }
}
