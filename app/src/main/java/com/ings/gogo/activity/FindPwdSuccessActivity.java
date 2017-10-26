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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.utils.JudgeTelNum;

public class FindPwdSuccessActivity extends BaseActivity implements
		OnClickListener {

	private ImageView mFindBackToLoginIv;
	private TextView mFindBackToLoginTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_findpwdok);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		mFindBackToLoginTv = (TextView) this
				.findViewById(R.id.findok_backToLoginTv);
		mFindBackToLoginTv.setOnClickListener(this);

		mFindBackToLoginIv = (ImageView) this
				.findViewById(R.id.findok_backToLoginIv);
		mFindBackToLoginIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.findok_backToLoginTv:
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.findok_backToLoginIv:
			Intent intent2 = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent2);
			finish();
			break;

		default:
			break;
		}
	}

}
