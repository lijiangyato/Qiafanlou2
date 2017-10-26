package com.ings.gogo.adapter;

import java.util.List;
import com.ings.gogo.R;
import com.ings.gogo.activity.OtherMainPageActivity;
import com.ings.gogo.base.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewDescribByImageAdapter extends BaseAdapter {
	private Context sContext;
	private List<String> dataList;

	public NewDescribByImageAdapter(Context context, List<String> strings) {
		this.sContext = context;
		this.dataList = strings;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(sContext).inflate(
					R.layout.layout_detaildescbypicitem, null);
			viewHolder = new ViewHolder();
			viewHolder.mDescribByImageIV = (ImageView) convertView
					.findViewById(R.id.descriGoodsByIvAndWordIv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		android.view.ViewGroup.LayoutParams params = viewHolder.mDescribByImageIV
				.getLayoutParams();

		params.height = (int) (OtherMainPageActivity.mWidth);
		params.width = (int) (OtherMainPageActivity.mWidth);
		ImageLoader.getInstance().displayImage("",
				viewHolder.mDescribByImageIV, BaseActivity.options);
		return convertView;
	}

	static class ViewHolder {
		ImageView mDescribByImageIV;

	}
}
