package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.entity.GoodsEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGildAdapter extends BaseAdapter {

	private List<GoodsEntity> entitys;
	private Context context;
	private LayoutInflater layoutInflater;

	public MyGildAdapter(List<GoodsEntity> entitys, Context context) {
		super();
		this.entitys = entitys;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}

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
			convertView = layoutInflater.inflate(R.layout.layout_goodsitem,
					null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}
		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(final GoodsEntity entitys,
			final ViewHolder holder) {

		// holder.PendingItemTag.setText("文件名称:" + entitys.getFileName() + "\n"
		// + "文件大小:" + trueFileSize + "KB" + "\n" + "文件状态:" + tem2);
		holder.mGoodsImage.setBackgroundResource(R.drawable.icon);
		holder.mGoodsDescrib.setText(entitys.getGoodsDescrib());
		holder.mGoodsPrice.setText(entitys.getGoodsPrice());
	}

	public class ViewHolder {
		public final TextView mGoodsDescrib;
		public final TextView mGoodsPrice;
		public final ImageView mGoodsImage;
		public final View root;

		public ViewHolder(View root) {
			mGoodsImage = (ImageView) root.findViewById(R.id.foodBgIV);
			mGoodsDescrib = (TextView) root.findViewById(R.id.foodDescribTv);
			mGoodsPrice = (TextView) root.findViewById(R.id.foodMoneyTv);
			this.root = root;
		}
	}

}
