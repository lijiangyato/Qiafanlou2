package com.ings.gogo.adapter;

import java.util.ArrayList;

import com.amap.api.services.core.PoiItem;
import com.ings.gogo.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<PoiItem> arrayList;

	public LocationAdapter(Context context, ArrayList<PoiItem> arrayList) {
		this.context = context;
		this.arrayList = arrayList;
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
			convertView = View.inflate(context, R.layout.map_listviewitem, null);
			holder.mLocationIvBg = (ImageView) convertView
					.findViewById(R.id.dinWeiBg);
			holder.mCompanyNameTv = (TextView) convertView
					.findViewById(R.id.companyNameTv);
			holder.mCompanyLocationTv = (TextView) convertView
					.findViewById(R.id.detailPlaceNameTv);
			holder.mDistanceTv = (TextView) convertView
					.findViewById(R.id.distanceTv);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mLocationIvBg.setBackgroundResource(R.drawable.location);
		holder.mDistanceTv.setText(String.valueOf(arrayList.get(position)
				.getDistance()) + "รื");
		holder.mCompanyNameTv.setText(String.valueOf(arrayList.get(position)));
		holder.mCompanyLocationTv.setText(String.valueOf(arrayList
				.get(position).getSnippet()));

		return convertView;
	}

	static class ViewHolder {

		ImageView mLocationIvBg;
		TextView mCompanyNameTv;
		TextView mCompanyLocationTv;
		TextView mDistanceTv;

	}

}
