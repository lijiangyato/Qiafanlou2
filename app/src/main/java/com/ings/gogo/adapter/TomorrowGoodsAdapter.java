package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.activity.MainPageActivity;
import com.ings.gogo.activity.OtherMainPageActivity;
import com.ings.gogo.base.BaseActivity;
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

public class TomorrowGoodsAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<FastFoodsEntity.FastFoodsDatas> entitys;

	public TomorrowGoodsAdapter(Context context, List<FastFoodsDatas> entitys) {
		super();
		this.context = context;
		this.entitys = entitys;
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

	private void initializeViews(FastFoodsEntity.FastFoodsDatas entity,
			ViewHolder holder) {
		android.view.ViewGroup.LayoutParams params = holder.mFastFodsImage
				.getLayoutParams();

		params.height = (int) (OtherMainPageActivity.mWidth / 2.8);
		params.width = (int) (OtherMainPageActivity.mWidth / 2.8);
		holder.mFastFodsImage.setLayoutParams(params);
		ImageLoader.getInstance().displayImage(entity.getImgurl(),
				holder.mFastFodsImage, BaseActivity.options);
		holder.mFastFoodsNameTv.setText(entity.getProname());
		holder.mPriceTv.setText("¥ " + entity.getPrice());
		if (entity.getPrice() == entity.getMarkprice()) {
			holder.mFormalPriceTv.setVisibility(View.GONE);
		} else {
			holder.mFormalPriceTv.setVisibility(View.VISIBLE);
			holder.mFormalPriceTv.setText("¥ " + entity.getMarkprice());
		}

		holder.mShortDesc.setText(entity.getShortdesc());
	}

	public class ViewHolder {
		private final ImageView mFastFodsImage;
		private final ImageView mFastFoodsIsOut;
		private final TextView mFastFoodsNameTv;
		private final TextView mPriceTv;
		private final TextView mFormalPriceTv;
		private final TextView mShortDesc;
		public final View root;

		// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

		public ViewHolder(View root) {
			mFastFodsImage = (ImageView) root.findViewById(R.id.foodBgIV);
			mFastFoodsNameTv = (TextView) root.findViewById(R.id.foodDescribTv);
			mPriceTv = (TextView) root.findViewById(R.id.foodMoneyTv);
			mFastFoodsIsOut = (ImageView) root.findViewById(R.id.foodIsOut);
			mFormalPriceTv = (TextView) root
					.findViewById(R.id.foodbegainmoneyTv);
			mFormalPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中划线
			mShortDesc = (TextView) root
					.findViewById(R.id.foodbegainShortDescTv);
			this.root = root;
		}
	}
}
