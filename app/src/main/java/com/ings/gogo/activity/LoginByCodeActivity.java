package com.ings.gogo.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.db.MyShopSqlHelper;
import com.ings.gogo.entity.RegistEntity;
import com.ings.gogo.utils.CountDownTimerUtils;
import com.ings.gogo.utils.JudgeTelNum;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.utils.MakeFileHash;
import com.ings.gogo.utils.StringMD5Utils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class LoginByCodeActivity extends BaseActivity implements
		OnClickListener {
	private static final int GET_DATA_OK = 1;
	private static final int LOGIN_ERROR = 2;
	private static final int CHECK_CODE_OK = 3;
	private static final int GET_CODE_OK = 4;
	// 返回父界面
	private ImageView mLoginBackToParent;
	// 输入电话号码
	private EditText mLoginTelNumEdt;
	// 密码
	private EditText mLoginCodeEdt;
	// 登录按钮
	private Button mLoginGetCodeBt;
	// 找回密码
	private Button mLoginByCodeBt;
	// 短信登录
	private Button mBackToNamePwd;
	private RegistEntity mLoginEntity;
	private String tem1;
	private String tem2;
	private String jiequ;
	private String jiequ2;
	public String aspnetauth;
	public String ASP_NET_SessionId;
	private SharedPreferences sharedPreferences = null;
	public static SharedPreferences.Editor editor = null;
	private CountDownTimerUtils mCountDownTimerUtils;
	private RegistEntity mRegistEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.layout_loginbyidentifycode);
		sharedPreferences = getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();// 获取编辑器
		initViews();

	}

	private void initViews() {
		// TODO Auto-generated method stub

		mLoginBackToParent = (ImageView) this
				.findViewById(R.id.loginbycode_topbackToParentBt);
		mLoginBackToParent.setOnClickListener(this);
		mLoginTelNumEdt = (EditText) this
				.findViewById(R.id.loginbycode_inputTelNumCodeEdt);
		mLoginCodeEdt = (EditText) this
				.findViewById(R.id.loginbycode_inputIdentifyCodeEdt);
		mLoginGetCodeBt = (Button) this
				.findViewById(R.id.loginbycode_getIdenfityCodeBt);
		mLoginGetCodeBt.setOnClickListener(this);
		mCountDownTimerUtils = new CountDownTimerUtils(mLoginGetCodeBt, 60000,
				1000);
		mLoginByCodeBt = (Button) this.findViewById(R.id.loginbycode_loginBt);
		mLoginByCodeBt.setOnClickListener(this);
		mBackToNamePwd = (Button) this
				.findViewById(R.id.loginbycode_backtonamepwdBt);
		mBackToNamePwd.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.loginbycode_topbackToParentBt:
			this.finish();
			break;
		case R.id.loginbycode_getIdenfityCodeBt:
			if (!telNumIsOk()) {

				return;
			}
			if (JudgeTelNum.isTelNum(mLoginTelNumEdt.getText().toString()
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
		case R.id.loginbycode_backtonamepwdBt:
			this.finish();

			break;
		case R.id.loginbycode_loginBt:
			if (!telNumIsOk()) {

				return;
			}
			if (!codeIsOk()) {

				return;
			}
			if (JudgeTelNum.isTelNum(mLoginTelNumEdt.getText().toString()
					.trim()) == true) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						// login();
						checkIndentifyCode();
					}
				}).start();
			} else {
				showToastLong(R.string.telnumerrorremian);
			}
			break;

		default:
			break;
		}

	}

	protected void checkIndentifyCode() {

		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(
				BaseData.CHECK_SMS_MSG
						+ mLoginTelNumEdt.getText().toString().trim()
						+ "&code=" + mLoginCodeEdt.getText().toString().trim())
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
				LogUtils.e("登录时校验验证码--->>", CheckCodeBody);
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
						+ mLoginTelNumEdt.getText().toString().trim()
						+ "&verclassify=3").build();
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
				LogUtils.e("登录时时的验证码--->>", getCodeBody);
				Gson gson = new Gson();
				mRegistEntity = gson.fromJson(getCodeBody, RegistEntity.class);
				Message msg = handler.obtainMessage(GET_CODE_OK);
				msg.sendToTarget();
			}

		});
	}

	public void clearData() {

		editor.clear();
		editor.commit();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case GET_DATA_OK:
				editor.putString("username", mLoginTelNumEdt.getText()
						.toString().trim());
				editor.putString(
						"password",
						StringMD5Utils.MD5(mLoginCodeEdt.getText().toString()
								.trim()));
				editor.commit();// 提交修改
				Intent intent2Person = new Intent(getApplicationContext(),
						MainPageActivity.class);
				startActivity(intent2Person);

				break;
			case LOGIN_ERROR:
				showToastLong(mLoginEntity.getMsg());
				break;
			case GET_CODE_OK:
				if (mRegistEntity.getSuccess().equals(false)) {
					mCountDownTimerUtils.stop();
					showToastLong(mRegistEntity.getMsg());
				}
				break;
			case CHECK_CODE_OK:
				if (mRegistEntity.getSuccess().equals(true)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							login();
						}
					}).start();

				} else {
					showToastLong(mRegistEntity.getMsg());
				}

				break;

			default:
				break;
			}

		};
	};

	protected void login() {
		// TODO Auto-generated method stub
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder()
				.add("phone", mLoginTelNumEdt.getText().toString().trim())
				.add("loginkey",
						StringMD5Utils.MD5(mLoginCodeEdt.getText().toString()
								.trim()))
				.add("code", mLoginCodeEdt.getText().toString()).build();

		Request request = new Request.Builder().url(BaseData.LOGIN_URL)
				.post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String loginBody = response.body().string();
				LogUtils.e("验证码登录结果--》》", loginBody);
				Gson gson = new Gson();
				mLoginEntity = gson.fromJson(loginBody, RegistEntity.class);
				if (mLoginEntity.getSuccess().equals(true)) {
					tem1 = response.headers().value(3).toString();// asp.net
					tem2 = response.headers().value(6).toString();// aspnetauth
					LogUtils.e("Login--->>tem2", tem2);
					jiequ = tem1.substring(tem1.indexOf("ASP.NET_SessionId="),
							tem1.indexOf("; path=/;"));
					ASP_NET_SessionId = jiequ;
					jiequ2 = tem2.substring(tem2.indexOf("aspnetauth="),
							tem2.indexOf("; path=/;"));
					aspnetauth = jiequ2;
					LogUtils.e("ASP_NET_SessionId--》》", ASP_NET_SessionId);
					LogUtils.e("aspnetauth", aspnetauth);
					Message msg = handler.obtainMessage(GET_DATA_OK);
					msg.sendToTarget();
				} else {
					Message msg = handler.obtainMessage(LOGIN_ERROR);
					msg.sendToTarget();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	private boolean telNumIsOk() {

		if (TextUtils.isEmpty(mLoginTelNumEdt.getText())) {
			showToastShort(R.string.telnumnotnull);
			return false;
		}

		return true;
	}

	private boolean codeIsOk() {
		String pwd = mLoginCodeEdt.getText().toString().trim();
		if (TextUtils.isEmpty(mLoginCodeEdt.getText())) {
			showToastShort(R.string.identifynotnull);
			return false;
		}
		return true;
	}

}
