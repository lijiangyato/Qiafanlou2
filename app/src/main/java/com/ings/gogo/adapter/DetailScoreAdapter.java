package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.activity.MainPageActivity;
import com.ings.gogo.activity.OtherMainPageActivity;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.entity.DetailScoreEntity;
import com.ings.gogo.entity.DetailScoreEntity.DetailScoreDatas;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity.FastFoodsDatas;
import com.ings.gogo.entity.MyCheckOrderEntity;
import com.ings.gogo.entity.MyCheckOrderEntity.OrderDatas;
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

public class DetailScoreAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<DetailScoreEntity.DetailScoreDatas> entitys;

	public DetailScoreAdapter(Context context, List<DetailScoreDatas> entitys) {
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
					R.layout.layout_detailscoreitem, null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(DetailScoreEntity.DetailScoreDatas entity,
			ViewHolder holder) {
		if (entity.getRecordnum() < 0) {
			holder.mDetailScoreChange.setText(entity.getRecordnum()+"");
		} else {
			holder.mDetailScoreChange.setText("+" + entity.getRecordnum());
		}

		holder.mChangeTime.setText(entity.getCreatedate());
		holder.mRemark.setText(entity.getRemark());
		holder.mLeftScore.setText("剩余" + entity.getScore() + "积分");

	}

	public class ViewHolder {

		private final TextView mDetailScoreChange;
		private final TextView mChangeTime;
		private final TextView mRemark;
		private final TextView mLeftScore;
		public final View root;
		

		// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

		public ViewHolder(View root) {

			mDetailScoreChange = (TextView) root
					.findViewById(R.id.detailscore_scorechange);
			mChangeTime = (TextView) root
					.findViewById(R.id.detailscore_changetime);
			mRemark = (TextView) root
					.findViewById(R.id.detailscore_scoreremark);
			mLeftScore = (TextView) root
					.findViewById(R.id.detailscore_leftscore);

			this.root = root;
		}
	}
}
