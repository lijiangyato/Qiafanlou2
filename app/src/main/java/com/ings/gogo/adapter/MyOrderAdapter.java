package com.ings.gogo.adapter;

import java.util.HashMap;
import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.entity.DetailFoodsEntity;
import com.ings.gogo.entity.DetailFoodsEntity.DetailFoosdPic;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity.FastFoodsDatas;
import com.ings.gogo.entity.SelectedGoodsEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyOrderAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<SelectedGoodsEntity> entitys;

	// private HashMap<String, SelectedGoodsEntity> entitys;

	public MyOrderAdapter(Context context, List<SelectedGoodsEntity> entitys) {
		super();
		this.context = context;
		this.entitys = entitys;
		this.layoutInflater = LayoutInflater.from(context);
	}

	// public MyOrderAdapter(Context context,
	// HashMap<String, SelectedGoodsEntity> entitys) {
	// super();
	// this.context = context;
	// this.entitys = entitys;
	// this.layoutInflater = LayoutInflater.from(context);
	// }

	@Override
	public int getCount() {

		return entitys.size();
	}

	@Override
	public Object getItem(int position) {
		return entitys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.layout_myorderlistitem, null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(SelectedGoodsEntity entity, ViewHolder holder) {
		holder.mOrderGoodsName.setText(entity.getGoodsName());
		holder.mOrderGoodsNum.setText("x" + entity.getGoodsNum());
		Double total = Integer.parseInt(entity.getGoodsNum())
				* Double.parseDouble(entity.getGoodsPrice());
		holder.mOrderGoodsPrice.setText("¥ " + entity.getGoodsPrice());

	}

	public class ViewHolder {
		private final TextView mOrderGoodsName;
		private final TextView mOrderGoodsNum;
		private final TextView mOrderGoodsPrice;
		public final View root;

		// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

		public ViewHolder(View root) {
			mOrderGoodsName = (TextView) root
					.findViewById(R.id.order_GoodsName);
			mOrderGoodsNum = (TextView) root.findViewById(R.id.order_GoodsNum);
			mOrderGoodsPrice = (TextView) root
					.findViewById(R.id.order_GoodsPrice);
			this.root = root;
		}
	}
}
