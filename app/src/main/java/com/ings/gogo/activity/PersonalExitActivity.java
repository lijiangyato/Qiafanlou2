package com.ings.gogo.activity;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalExitActivity extends BaseActivity implements
		OnClickListener {
	private final int GET_DATA_OK = 1;
	private TextView mExitAppTv;
	// 以下三个值 主要用来 获取cookie 信息
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_personexitapp);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		mExitAppTv = (TextView) this.findViewById(R.id.person_exitApp);
		mExitAppTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.person_exitApp:
			exitDialog();
			break;

		default:
			break;
		}
	}

	private void exitDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this).setIcon(R.drawable.tishi)
				.setTitle("温馨提示").setMessage("确定退出恰饭喽？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								getExitData();
							}
						}).start();
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_DATA_OK:
				if (LoginActivity.editor != null) {
					LoginActivity loginActivity = new LoginActivity();
					loginActivity.clearData();
					myApplication.setASP_NET_SessionId("");
					myApplication.setAspnetauth("");
				} else {
					MainPageActivity main = new MainPageActivity(
							MainPageActivity.preferences,
							MainPageActivity.editor);
					main.clearData();
					myApplication.setASP_NET_SessionId("");
					myApplication.setAspnetauth("");
				}

				Intent intent2Main = new Intent(getApplicationContext(),
						MainPageActivity.class);
				startActivity(intent2Main);
				break;

			default:
				break;
			}
		};
	};

	protected void getExitData() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.EXIT_APP).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response response) throws IOException {
				String exitBody = response.body().string();
				LogUtils.e("退出系统--->>", exitBody + "---------");
				Gson gson = new Gson();
				// mTotalScoreEntity = gson.fromJson(orderScoreBody,
				// TotalScoreEntity.class);
				Message msg = handler.obtainMessage(GET_DATA_OK);
				msg.sendToTarget();

			}

			@Override
			public void onFailure(Request request, IOException e) {
			}

		});
	}

}
