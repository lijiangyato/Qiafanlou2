package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.activity.EditPlaceActivity;
import com.ings.gogo.activity.MainPageActivity;
import com.ings.gogo.activity.OtherMainPageActivity;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.entity.AddressEntity;
import com.ings.gogo.entity.AddressEntity.PlaceDatas;
import com.ings.gogo.entity.CleanFoodsEntity;
import com.ings.gogo.entity.CleanFoodsEntity.CleanFoodsDatas;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity.FastFoodsDatas;
import com.ings.gogo.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AddressAdapter extends BaseAdapter {
	private static final int GET_DATAOK = 1;
	private Context context;
	private LayoutInflater layoutInflater;
	private List<AddressEntity.PlaceDatas> entitys;
	private String mEditName;
	private String mEditPhoneNum;
	private String mEditPlaceName;
	private String mEditPlaceId;
	private String mEditSex;

	public AddressAdapter(Context context, List<PlaceDatas> entitys) {
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
			convertView = layoutInflater.inflate(
					R.layout.layout_manageplaceitem, null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(final AddressEntity.PlaceDatas entity,
			ViewHolder holder) {

		holder.mPlaceName.setText(entity.getConsignee_add());
		holder.mPlaceUserNameTv.setText(entity.getConsignee_name());
		holder.mPlaceSexTv.setText(entity.getSex());
		holder.mPlaceTelNumTv.setText(entity.getConsignee_phone());
		holder.mClickToEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogUtils.e("地址编辑点击到了", "地址编辑点击到了");
				mEditPlaceId = entity.getConsigneeid();
				mEditName = entity.getConsignee_name();
				mEditPhoneNum = entity.getConsignee_phone();
				mEditPlaceName = entity.getConsignee_add();
				mEditSex = entity.getSex();
				Intent intent = new Intent(context, EditPlaceActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("EditPlaceId", mEditPlaceId);
				bundle.putString("EditName", mEditName);
				bundle.putString("EditPhoneNum", mEditPhoneNum);
				bundle.putString("EditPlaceName", mEditPlaceName);
				bundle.putString("EditSex", mEditSex);
				bundle.putString("JinDu", entity.getGpsx());// 经度
				bundle.putString("WeiDu", entity.getGpsy());// 纬度
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				// Message msg = handler.obtainMessage(GET_DATAOK);
				// msg.sendToTarget();
			}
		});

	}

	// Handler handler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// switch (msg.what) {
	// case GET_DATAOK:

	// break;
	//
	// default:
	// break;
	// }
	//
	// };
	// };

	public class ViewHolder {
		private final TextView mPlaceName;
		private final TextView mPlaceUserNameTv;
		private final TextView mPlaceSexTv;
		private final TextView mPlaceTelNumTv;
		private final ImageView mClickToEdit;
		public final View root;

		// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

		public ViewHolder(View root) {
			mPlaceName = (TextView) root.findViewById(R.id.placeitem_placename);
			mPlaceUserNameTv = (TextView) root
					.findViewById(R.id.placeitem_peoplename);
			mPlaceSexTv = (TextView) root
					.findViewById(R.id.placeitem_peoplesex);
			mPlaceTelNumTv = (TextView) root
					.findViewById(R.id.placeitem_peoplephonenum);
			mClickToEdit = (ImageView) root
					.findViewById(R.id.placeitem_editPlaceIm);
			this.root = root;
		}
	}
}
