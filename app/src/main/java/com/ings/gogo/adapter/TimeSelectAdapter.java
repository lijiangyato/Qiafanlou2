package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.activity.MainPageActivity;
import com.ings.gogo.activity.OtherMainPageActivity;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.entity.AddressEntity;
import com.ings.gogo.entity.AddressEntity.PlaceDatas;
import com.ings.gogo.entity.CleanFoodsEntity;
import com.ings.gogo.entity.CleanFoodsEntity.CleanFoodsDatas;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity.FastFoodsDatas;
import com.ings.gogo.entity.OrderTimeEntity;
import com.ings.gogo.entity.OrderTimeEntity.TimeDatas;
import com.ings.gogo.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
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

public class TimeSelectAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<OrderTimeEntity.TimeDatas> entitys;

	public TimeSelectAdapter(Context context, List<TimeDatas> entitys) {
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
			convertView = layoutInflater
					.inflate(R.layout.layout_timeitem, null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(OrderTimeEntity.TimeDatas entity,
			ViewHolder holder) {

		holder.mTimeSelect.setText(entity.getTimestr());

	}

	public class ViewHolder {
		private final TextView mTimeSelect;

		public final View root;

		// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

		public ViewHolder(View root) {
			mTimeSelect = (TextView) root.findViewById(R.id.timeItem_tiemes);

			this.root = root;
		}
	}
}
