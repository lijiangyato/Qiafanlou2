package com.ings.gogo.adapter;

import java.util.List;

import com.ings.gogo.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends
		RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
	private Context context;
	private LayoutInflater mInflater;
	private List<String> mDatas;

	public GalleryAdapter(Context context, List<String> mDatas) {
		super();
		this.context = context;
		this.mDatas = mDatas;
		mInflater = LayoutInflater.from(context);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View arg0) {
			super(arg0);
		}

		TextView mTxt;
	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = mInflater.inflate(R.layout.layout_shoppointitem, viewGroup,
				false);
		ViewHolder viewHolder = new ViewHolder(view);

		viewHolder.mTxt = (TextView) view.findViewById(R.id.shopPoint_describ);
		return viewHolder;
	}

	/**
	 * 设置值V
	 */
	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
		viewHolder.mTxt.setText(mDatas.get(i));
	}

}
