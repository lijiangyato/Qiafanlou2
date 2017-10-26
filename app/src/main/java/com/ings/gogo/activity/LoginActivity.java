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
import com.ings.gogo.utils.JudgeTelNum;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.utils.MakeFileHash;
import com.ings.gogo.utils.StringMD5Utils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class LoginActivity extends BaseActivity implements OnClickListener {
	private static final int GET_DATA_OK = 1;
	private static final int LOGIN_ERROR = 2;
	// 返回父界面
	private ImageView mLoginBackToParent;
	// 跳转到regist
	private TextView mLoginIntent2Regist;
	// 输入电话号码
	private EditText mLoginTelNumEdt;
	// 密码
	private EditText mLoginPwdEdt;
	// 登录按钮
	private Button mLoginLoginBt;
	// 找回密码
	private Button mLoginFindPwdBt;
	// 短信登录
	private Button mLoginByCode;
	private RegistEntity mLoginEntity;
	private String tem1;
	private String tem2;
	private String jiequ;
	private String jiequ2;
	public String aspnetauth;
	public String ASP_NET_SessionId;
	private SharedPreferences sharedPreferences = null;
	public static SharedPreferences.Editor editor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.layout_loginbynamepwd);
		sharedPreferences = getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();// 获取编辑器
		initViews();

	}

	private void initViews() {
		// TODO Auto-generated method stub
		mLoginBackToParent = (ImageView) this
				.findViewById(R.id.login_topbackToParentBt);
		mLoginBackToParent.setOnClickListener(this);
		mLoginIntent2Regist = (TextView) this.findViewById(R.id.login_registTv);
		mLoginIntent2Regist.setOnClickListener(this);
		mLoginTelNumEdt = (EditText) this.findViewById(R.id.login_inputNameEdt);
		mLoginPwdEdt = (EditText) this.findViewById(R.id.login_inputPwdEdt);
		mLoginLoginBt = (Button) this.findViewById(R.id.login_loginBt);
		mLoginLoginBt.setOnClickListener(this);
		mLoginFindPwdBt = (Button) this.findViewById(R.id.login_findPwdBt);
		mLoginFindPwdBt.setOnClickListener(this);
		mLoginByCode = (Button) this.findViewById(R.id.login_loginByIdentifyBt);
		mLoginByCode.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_loginByIdentifyBt:
			Intent intent2Code = new Intent(getApplicationContext(),
					LoginByCodeActivity.class);
			startActivity(intent2Code);
			break;
		case R.id.login_topbackToParentBt:
			this.finish();
			break;
		case R.id.login_registTv:
			Intent intent2Regist = new Intent(getApplicationContext(),
					RegistGetIndentifyCodeActivity.class);
			startActivity(intent2Regist);

			break;
		case R.id.login_loginBt:
			if (!telNumIsOk()) {

				return;
			}
			if (!pwdIsOk()) {

				return;
			}
			if (JudgeTelNum.isTelNum(mLoginTelNumEdt.getText().toString()
					.trim()) == true) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						login();
					}
				}).start();
			} else {
				showToastLong(R.string.telnumerrorremian);
			}
			break;
		case R.id.login_findPwdBt:
			Intent intent2FindPwd = new Intent(getApplicationContext(),
					FindPwdGetIdentifyCodeActivity.class);
			startActivity(intent2FindPwd);

			break;

		default:
			break;
		}

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
						StringMD5Utils.MD5(mLoginPwdEdt.getText().toString()
								.trim()));

				editor.commit();// 提交修改
				Intent intent2Person = new Intent(getApplicationContext(),
						MainPageActivity.class);
				startActivity(intent2Person);

				break;
			case LOGIN_ERROR:
				showToastLong(mLoginEntity.getMsg());
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
						StringMD5Utils.MD5(mLoginPwdEdt.getText().toString()
								.trim())).build();

		Request request = new Request.Builder().url(BaseData.LOGIN_URL)
				.post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String loginBody = response.body().string();
				String loginhead = response.headers().toString();
				LogUtils.e("手动登录结果--》》", loginBody + "\n" + loginhead);
				Gson gson = new Gson();
				mLoginEntity = gson.fromJson(loginBody, RegistEntity.class);
				if (mLoginEntity.getSuccess().booleanValue() == true) {
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

	private boolean pwdIsOk() {
		String pwd = mLoginPwdEdt.getText().toString().trim();
		if (TextUtils.isEmpty(mLoginPwdEdt.getText())) {
			showToastShort(R.string.pwdnotnull);
			return false;
		} else if (pwd.length() < 6) {
			showToastShort(R.string.pwdisshort);
			return false;
		}
		return true;
	}

	// 双击退出
	// // 双击退出 第一次点击的时间设置为0
	private long firstTime = 0;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				firstTime = System.currentTimeMillis();
				return true;
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				System.exit(0);
				finish();
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
