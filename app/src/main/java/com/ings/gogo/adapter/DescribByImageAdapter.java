package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.entity.DetailFoodsEntity;
import com.ings.gogo.entity.DetailFoodsEntity.DetailFoosdPic;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity.FastFoodsDatas;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class DescribByImageAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<DetailFoodsEntity.DetailFoosdPic> entitys;

	public DescribByImageAdapter(Context context, List<DetailFoosdPic> entitys) {
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
					R.layout.layout_detaildescbypicitem, null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(DetailFoodsEntity.DetailFoosdPic entity,
			ViewHolder holder) {
		ImageLoader.getInstance().displayImage(entity.getImgurl(),
				holder.mFastFodsImage, BaseActivity.options);
	}

	public class ViewHolder {
		private final ImageView mFastFodsImage;
		public final View root;

		// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

		public ViewHolder(View root) {
			mFastFodsImage = (ImageView) root
					.findViewById(R.id.descriGoodsByIvAndWordIv);
			this.root = root;
		}
	}
}
