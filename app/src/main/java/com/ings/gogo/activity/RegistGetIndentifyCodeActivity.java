package com.ings.gogo.activity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.RegistEntity;
import com.ings.gogo.utils.CountDownTimerUtils;
import com.ings.gogo.utils.JudgeTelNum;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RegistGetIndentifyCodeActivity extends BaseActivity implements
		OnClickListener {
	private static final int CHECK_CODE_OK = 1;
	private static final int GET_CODE_OK = 2;
	private ImageView mRegistBackToParent;
	private Button mRegistGetIdenftCodeBt;
	private CountDownTimerUtils mCountDownTimerUtils;
	private EditText mRegistIdentifyCodeEdt;
	private EditText mRegistTelNumEdt;
	private Button mCheckIdentifyCodeBt;
	private RegistEntity mRegistEntity;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CHECK_CODE_OK:
				if (mRegistEntity.getSuccess().equals(true)) {
					Intent intent2PWD = new Intent(getApplicationContext(),
							RegistInputPwdActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("telNum", mRegistTelNumEdt.getText()
							.toString().trim());
					bundle.putString("IdentifyCode", mRegistIdentifyCodeEdt
							.getText().toString().trim());
					intent2PWD.putExtras(bundle);
					startActivity(intent2PWD);
				} else {
					showToastLong(mRegistEntity.getMsg());
				}

				break;
			case GET_CODE_OK:
				if (mRegistEntity.getSuccess().equals(false)) {
					mCountDownTimerUtils.stop();
					showToastLong(mRegistEntity.getMsg());
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_registactivity);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		mRegistGetIdenftCodeBt = (Button) this
				.findViewById(R.id.regist_getIdenfityCodeBt);
		mRegistGetIdenftCodeBt.setOnClickListener(this);
		mCountDownTimerUtils = new CountDownTimerUtils(mRegistGetIdenftCodeBt,
				60000, 1000);
		mRegistIdentifyCodeEdt = (EditText) this
				.findViewById(R.id.regist_inputIdentifyCodeEdt);
		mRegistTelNumEdt = (EditText) this
				.findViewById(R.id.regist_inputTelNumEdt);
		mCheckIdentifyCodeBt = (Button) this
				.findViewById(R.id.regist_checkIdentifyCodeBt);
		mCheckIdentifyCodeBt.setOnClickListener(this);
		mRegistBackToParent = (ImageView) this
				.findViewById(R.id.regist_topbackToParentBt);
		mRegistBackToParent.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.regist_getIdenfityCodeBt:
			if (!telNumIsOk()) {

				return;
			}
			if (JudgeTelNum.isTelNum(mRegistTelNumEdt.getText().toString()
					.trim()) == true) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mCountDownTimerUtils.start();
						getIndentifyCode();
					}
				}).start();
			} else {
				showToastLong(R.string.telnumerrorremian);
			}

			break;
		case R.id.regist_checkIdentifyCodeBt:
			if (!IdentifyCodeIsOk()) {

				return;
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					checkIndentifyCode();
				}
			}).start();
			break;
		case R.id.regist_topbackToParentBt:
			this.finish();
			break;

		default:
			break;
		}
	}

	protected void checkIndentifyCode() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(
				BaseData.CHECK_SMS_MSG
						+ mRegistTelNumEdt.getText().toString().trim()
						+ "&code="
						+ mRegistIdentifyCodeEdt.getText().toString().trim())
				.build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String CheckCodeBody = response.body().string();
				LogUtils.e("注册时校验验证码--->>", CheckCodeBody);
				Gson gson = new Gson();
				mRegistEntity = gson
						.fromJson(CheckCodeBody, RegistEntity.class);
				Message msg = handler.obtainMessage(CHECK_CODE_OK);
				msg.sendToTarget();
			}

		});
	}

	protected void getIndentifyCode() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(
				BaseData.GET_SMS_MSG
						+ mRegistTelNumEdt.getText().toString().trim()
						+ "&verclassify=1").build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String getCodeBody = response.body().string();
				LogUtils.e("注册时的验证码--->>", getCodeBody);
				Gson gson = new Gson();
				mRegistEntity = gson.fromJson(getCodeBody, RegistEntity.class);
				Message msg = handler.obtainMessage(GET_CODE_OK);
				msg.sendToTarget();
			}

		});
	}

	private boolean telNumIsOk() {

		if (TextUtils.isEmpty(mRegistTelNumEdt.getText())) {
			showToastShort(R.string.telnumnotnull);
			return false;
		}

		return true;
	}

	private boolean IdentifyCodeIsOk() {

		if (TextUtils.isEmpty(mRegistIdentifyCodeEdt.getText())) {
			showToastShort(R.string.identifynotnull);
			return false;
		}

		return true;
	}

}
