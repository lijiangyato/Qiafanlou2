package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.entity.NewShopCarEntity;
import com.ings.gogo.entity.MyShopCarEntity;
import com.ings.gogo.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class NewShopCarAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<MyShopCarEntity> entitys;

	public NewShopCarAdapter(Context context, List<MyShopCarEntity> entitys) {
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
			convertView = layoutInflater.inflate(R.layout.shopcart_list_item,
					null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(final MyShopCarEntity entity,
			final ViewHolder holder) {

		ImageLoader.getInstance().displayImage(entity.getGoodsPrice(),
				holder.mGoodsImage, BaseActivity.options);
		holder.mGoodsNameTv.setText(entity.getGoodsName());
		holder.mGoodsPriceTv.setText("¥ " + entity.getGoodsImage());
		// final data=entitys.get(po)
		// final ShopCarEntity data = mListData.get(position);
		// bindListItem(holder, data);
		//
		// if (data != null) {
		// // 判断是否选择
		// if (data.isChoose()) {
		// holder.checkBox.setChecked(true);
		// } else {
		// holder.checkBox.setChecked(false);
		// }
		//
		// // 选中操作
		// holder.checkBox.setOnClickListener(new CheckBoxOnClick(data));
		// // 减少操作
		// holder.red.setOnClickListener(new ReduceOnClick(data,
		// holder.carNum));
		//
		// // 增加操作
		// holder.add.setOnClickListener(new AddOnclick(data,
		// holder.carNum));
		//
		// }

	}

	private class AddOnclick implements OnClickListener {
		NewShopCarEntity shopcartEntity;
		TextView shopcart_number_btn;

		private AddOnclick(NewShopCarEntity shopcartEntity,
				TextView shopcart_number_btn) {
			this.shopcartEntity = shopcartEntity;
			this.shopcart_number_btn = shopcart_number_btn;
		}

		@Override
		public void onClick(View arg0) {
			shopcartEntity.setChoose(true);
			String numberStr = shopcart_number_btn.getText().toString();
			if (!TextUtils.isEmpty(numberStr)) {
				int number = Integer.parseInt(numberStr);

				int currentNum = number + 1;
				// 设置列表
				shopcartEntity.setCarNum(currentNum);
				// holder.carNum.setText("" + currentNum);
				notifyDataSetChanged();
			}
			// count();
		}

	}

	private class ReduceOnClick implements OnClickListener {
		NewShopCarEntity shopcartEntity;
		TextView shopcart_number_btn;

		private ReduceOnClick(NewShopCarEntity shopcartEntity,
				TextView shopcart_number_btn) {
			this.shopcartEntity = shopcartEntity;
			this.shopcart_number_btn = shopcart_number_btn;
		}

		@Override
		public void onClick(View arg0) {
			shopcartEntity.setChoose(true);
			String numberStr = shopcart_number_btn.getText().toString();
			if (!TextUtils.isEmpty(numberStr)) {
				int number = Integer.parseInt(numberStr);
				if (number == 1) {
					// Toast.makeText(CartListActivity.this, "不能往下减少了",
					// Toast.LENGTH_SHORT).show();
				} else {
					int currentNum = number - 1;
					// 设置列表
					shopcartEntity.setCarNum(currentNum);

					// holder.carNum.setText("" + currentNum);
					notifyDataSetChanged();

				}

			}
			// count();
		}

	}

	public class ViewHolder {
		//
		private CheckBox mIsGoodsSelected;
		private ImageView mGoodsImage;
		private TextView mGoodsNameTv;
		private TextView mGoodsPriceTv;
		private TextView mAddGoodsTv;
		private TextView mReducesNumTv;
		private TextView mGoodsNumTv;
		public View root;

		// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

		public ViewHolder(View root) {
			mIsGoodsSelected = (CheckBox) root
					.findViewById(R.id.mycar_checkbox);
			mGoodsImage = (ImageView) root.findViewById(R.id.mycar_goodsImage);
			mGoodsNameTv = (TextView) root.findViewById(R.id.mycar_goodsName);
			mGoodsPriceTv = (TextView) root.findViewById(R.id.mycar_goodsPrice);
			mAddGoodsTv = (TextView) root.findViewById(R.id.mycar_addGoodsNum);
			mReducesNumTv = (TextView) root
					.findViewById(R.id.mycar_reduceGoodsNum);
			mGoodsNumTv = (TextView) root.findViewById(R.id.mycar_goodsNum);
			this.root = root;
		}
	}

}
