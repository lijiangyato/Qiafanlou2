package com.ings.gogo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.ings.gogo.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationTipsAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Tip> arrayList;

	public LocationTipsAdapter(Context context, List<Tip> tipList) {
		super();
		this.context = context;
		this.arrayList = (ArrayList<Tip>) tipList;
	}

	@Override
	public int getCount() {

		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {

		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.gettipsitem, null);
			holder.mGetTipsIvBg = (ImageView) convertView
					.findViewById(R.id.getTipsBg);
			holder.mTipsCompanyTv = (TextView) convertView
					.findViewById(R.id.TipsCompanyNameTv);
			holder.mTipsLocationTv = (TextView) convertView
					.findViewById(R.id.TipsPlaceNameTv);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mGetTipsIvBg.setBackgroundResource(R.drawable.dinweibg);
		holder.mTipsCompanyTv.setText(String.valueOf(arrayList.get(position)
				.getName()));
		holder.mTipsLocationTv.setText(String.valueOf(arrayList.get(position)
				.getDistrict()));
		return convertView;
	}

	static class ViewHolder {

		ImageView mGetTipsIvBg;
		TextView mTipsCompanyTv;
		TextView mTipsLocationTv;
		// ImageView mGetTipsSelectedIv;

	}

}
