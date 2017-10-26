package com.ings.gogo.activity;

import java.io.IOException;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.db.MyShopSqlHelper;
import com.ings.gogo.entity.RegistEntity;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.utils.StringMD5Utils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class RegistInputPwdActivity extends BaseActivity implements
		OnClickListener {
	private static final int GET_CODE_OK = 1;
	private TextView mPwdTelNumTipsTv;
	private ImageView mPwdBackToParent;
	private EditText mPwdInputEdt;
	private EditText mPwdAgainEdt;
	private Button mPwdConfirmRegistBt;
	private String mTelNum;
	private String mIdentifyCode;
	private RegistEntity mRegistEntity;
	private CheckBox mPwdShowCB1;
	private CheckBox mPwdShowCB2;
	private StringBuilder sb;
	private MyShopSqlHelper mySqlHelper;
	private SQLiteDatabase db;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_CODE_OK:
				if (mRegistEntity.getSuccess().equals(true)) {
					showToastLong(mRegistEntity.getMsg());
					Intent intent2Login = new Intent(getApplicationContext(),
							LoginActivity.class);
					startActivity(intent2Login);
				} else {
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
		setContentView(R.layout.layout_registinputpwd);
		mySqlHelper = new MyShopSqlHelper(getApplicationContext(), "GOGO",
				null, 1);
		db = mySqlHelper.getWritableDatabase();
		Bundle bundle = this.getIntent().getExtras();
		mTelNum = bundle.getString("telNum");
		mIdentifyCode = bundle.getString("IdentifyCode");
		LogUtils.e("电话号码和验证码", mTelNum + "和" + mIdentifyCode);
		if (!TextUtils.isEmpty(mTelNum) && mTelNum.length() > 6) {
			sb = new StringBuilder();
			for (int i = 0; i < mTelNum.length(); i++) {
				char c = mTelNum.charAt(i);
				if (i >= 3 && i <= 7) {
					sb.append('*');
				} else {
					sb.append(c);
				}
			}

		}
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		mPwdTelNumTipsTv = (TextView) this.findViewById(R.id.pwd_telNumTipsEdt);
		mPwdTelNumTipsTv.setText("你正在设置账号" + sb.toString() + "的登录密码");
		mPwdInputEdt = (EditText) this.findViewById(R.id.pwd_inputPwdEdt);
		mPwdAgainEdt = (EditText) this.findViewById(R.id.pwd_inputPwdAgainEdt);
		mPwdConfirmRegistBt = (Button) this.findViewById(R.id.pwd_confirmBt);
		mPwdConfirmRegistBt.setOnClickListener(this);
		mPwdBackToParent = (ImageView) this
				.findViewById(R.id.pwd_topbackToParentBt);
		mPwdBackToParent.setOnClickListener(this);
		mPwdShowCB1 = (CheckBox) this.findViewById(R.id.pwd_isShowPwdCb);
		mPwdShowCB2 = (CheckBox) this.findViewById(R.id.pwd_isShowPwdCb2);
		mPwdShowCB1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 如果选中，显示密码
					mPwdInputEdt
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
				} else {
					// 否则隐藏密码
					mPwdInputEdt
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				}

			}

		});
		mPwdShowCB2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 如果选中，显示密码
					mPwdAgainEdt
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
				} else {
					// 否则隐藏密码
					mPwdAgainEdt
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				}

			}

		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.pwd_confirmBt:
			if (!pwd1IsOk()) {

				return;
			}
			if (!pwd2IsOk()) {

				return;
			}
			if (mPwdInputEdt.getText().toString().trim()
					.equalsIgnoreCase(mPwdAgainEdt.getText().toString().trim())) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						checkRegistResult();
					}
				}).start();

			} else {
				showToastShort(R.string.pwdnosameto);
			}

			break;
		case R.id.pwd_topbackToParentBt:
			this.finish();
			break;

		default:
			break;
		}
	}

	private void checkRegistResult() {
		// TODO Auto-generated method stub
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder()
				.add("phone", mTelNum.trim())
				.add("smscode", mIdentifyCode.trim())
				.add("loginkey",
						StringMD5Utils.MD5(mPwdInputEdt.getText().toString()
								.trim())).build();

		Request request = new Request.Builder().url(BaseData.REGIST_URL)
				.post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String checkRegistBody = response.body().string();
				LogUtils.e("验证注册结果--》》", checkRegistBody);
				Gson gson = new Gson();
				mRegistEntity = gson.fromJson(checkRegistBody,
						RegistEntity.class);
				Message msg = handler.obtainMessage(GET_CODE_OK);
				msg.sendToTarget();

			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	private boolean pwd1IsOk() {
		String pwd = mPwdInputEdt.getText().toString().trim();
		if (TextUtils.isEmpty(mPwdInputEdt.getText())) {
			showToastShort(R.string.pwdnotnull);
			return false;
		} else if (pwd.length() > 18 || pwd.length() < 6) {
			showToastShort(R.string.pwdisshort);
			return false;
		}
		return true;
	}

	private boolean pwd2IsOk() {
		String pwd = mPwdAgainEdt.getText().toString().trim();
		if (TextUtils.isEmpty(mPwdAgainEdt.getText())) {
			showToastShort(R.string.pwdnotnull);
			return false;
		} else if (pwd.length() > 18 || pwd.length() < 6) {
			showToastShort(R.string.pwdisshort);
			return false;
		}
		return true;
	}

}
