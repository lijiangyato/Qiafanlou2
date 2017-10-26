package com.ings.gogo.view;

import com.ings.gogo.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class MyProductContentPage implements MySnapPageLayout.McoySnapPage {

	private Context context;
	private boolean isAtTop;
	private boolean isIdle;
	private View rootView = null;
	private ListView mContentLV;

	public MyProductContentPage(Context context, View rootView) {
		this.context = context;
		this.rootView = rootView;
		mContentLV = (ListView) this.rootView
				.findViewById(R.id.describByImgAndWordLV);
	}

	@Override
	public View getRootView() {
		return rootView;
	}

	@Override
	public boolean isAtTop() {
		mContentLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.e("log", "onScrollStateChanged");
				isIdle = scrollState == OnScrollListener.SCROLL_STATE_IDLE;
				Log.e("log", "isIdle is " + isIdle);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.e("log", "firstVisibleItem is " + firstVisibleItem);
				if (firstVisibleItem == 0 && isIdle) {
					Log.e("log", "滑到顶部");
					isAtTop = true;
				}
				if (visibleItemCount + firstVisibleItem == totalItemCount) {
					Log.e("log", "滑到底部");
				}
			}
		});

		return isAtTop;
	}

	@Override
	public boolean isAtBottom() {
		int scrollY = mContentLV.getScrollY();
		int height = mContentLV.getHeight();
		int scrollViewMeasuredHeight = mContentLV.getChildAt(1)
				.getMeasuredHeight();

		if ((scrollY + height) >= scrollViewMeasuredHeight) {
			return true;
		}
		return false;
	}

}
