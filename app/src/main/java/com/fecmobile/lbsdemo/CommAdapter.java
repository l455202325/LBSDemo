package com.fecmobile.lbsdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.fecmobile.lbsdemo.holder.CommViewHolder;

import java.util.List;
import java.util.Map;


public abstract class CommAdapter<T> extends BaseAdapter {

	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<CloudPoiInfo> mData = null;
	protected final int mItemLayoutId;

	/**
	 * @param mContext 上下文对象
	 * @param mData 数据源
	 * @param itemLayoutId listview对应的Item
	 */
	public CommAdapter(Context mContext, List<CloudPoiInfo> mData, int itemLayoutId) {
		super();
		mInflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
		this.mData = mData;
		this.mItemLayoutId = itemLayoutId;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public CloudPoiInfo getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CommViewHolder viewHolder = CommViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);

		convert(viewHolder, getItem(position));

		return viewHolder.getConvertView();
	}

	public abstract void convert(CommViewHolder holder, CloudPoiInfo item);

}
