package com.ings.gogo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private List<String> allGoodsBgUrl;

	public ImageAdapter(Context context, List<String> allGoodsBgUrl) {
		super();
		this.context = context;
		this.allGoodsBgUrl = allGoodsBgUrl;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		LogUtils.e("ImageAdapter-->>?????", allGoodsBgUrl.size() + "");
		return allGoodsBgUrl.size();
	}

	@Override
	public Object getItem(int position) {
		return allGoodsBgUrl.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_detaildescbypicitem, null);
			viewHolder = new ViewHolder();
			viewHolder.mDescribByIm = (ImageView) convertView
					.findViewById(R.id.descriGoodsByIvAndWordIv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		for (int i = 0; i < allGoodsBgUrl.size(); i++) {
			LogUtils.e(" mmmmmmm", allGoodsBgUrl.get(i));
			ImageLoader.getInstance().displayImage(allGoodsBgUrl.get(i),
					viewHolder.mDescribByIm, BaseActivity.options);
		}

		return convertView;
	}

	class ViewHolder {
		ImageView mDescribByIm;

	}
}
