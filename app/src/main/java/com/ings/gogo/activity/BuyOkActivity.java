package com.ings.gogo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.utils.JudgeTelNum;
import com.ings.gogo.utils.LogUtils;

public class BuyOkActivity extends BaseActivity implements OnClickListener {

	private ImageButton mBuyOkBack2MainTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_buygoodsok);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		mBuyOkBack2MainTv = (ImageButton) this
				.findViewById(R.id.buyok_backToMainIv);
		mBuyOkBack2MainTv.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buyok_backToMainIv:
			Intent intent2 = new Intent(getApplicationContext(),
					MainPageActivity.class);
			startActivity(intent2);
			break;

		default:
			break;
		}
	}

	// 捕获返回键的方法1
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下BACK，同时没有重复
			Intent intent2 = new Intent(getApplicationContext(),
					MainPageActivity.class);
			startActivity(intent2);
		}

		return super.onKeyDown(keyCode, event);
	}

}
