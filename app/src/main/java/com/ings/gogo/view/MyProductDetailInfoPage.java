package com.ings.gogo.view;

import com.ings.gogo.R;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class MyProductDetailInfoPage implements MySnapPageLayout.McoySnapPage {

	private Context context;
	private Button button;
	private View rootView = null;
	private MyScrollView mcoyScrollView = null;

	public MyProductDetailInfoPage(Context context, View rootView) {
		this.context = context;
		this.rootView = rootView;
		mcoyScrollView = (MyScrollView) this.rootView
				.findViewById(R.id.product_scrollview);

	}

	@Override
	public View getRootView() {
		return rootView;
	}

	@Override
	public boolean isAtTop() {
		return true;
	}

	@Override
	public boolean isAtBottom() {
		int scrollY = mcoyScrollView.getScrollY();
		int height = mcoyScrollView.getHeight();
		int scrollViewMeasuredHeight = mcoyScrollView.getChildAt(0)
				.getMeasuredHeight();

		if ((scrollY + height) >= scrollViewMeasuredHeight) {
			return true;
		}
		return false;
	}

}
