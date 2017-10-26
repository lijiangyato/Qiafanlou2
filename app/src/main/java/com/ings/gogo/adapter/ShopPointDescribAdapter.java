package com.ings.gogo.adapter;

import java.util.List;
import com.ings.gogo.R;
import com.ings.gogo.activity.MainPageActivity;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.entity.CleanFoodsEntity;
import com.ings.gogo.entity.CleanFoodsEntity.CleanFoodsDatas;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity.FastFoodsDatas;
import com.ings.gogo.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ShopPointDescribAdapter extends BaseAdapter {
	private Context sContext;
	private List<String> dataList;

	public ShopPointDescribAdapter(Context context, List<String> strings) {
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
					R.layout.layout_shoppointitem, null);
			viewHolder = new ViewHolder();
			viewHolder.mShopPointTextView = (TextView) convertView
					.findViewById(R.id.shopPoint_describ);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mShopPointTextView.setText(dataList.get(position));
		return convertView;
	}

	static class ViewHolder {
		TextView mShopPointTextView;

	}
}
