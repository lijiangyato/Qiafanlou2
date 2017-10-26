package com.ings.gogo.activity;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.CheckOrderAdapter;
import com.ings.gogo.adapter.DetailScoreAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.DetailScoreEntity;
import com.ings.gogo.entity.MyCheckOrderEntity;
import com.ings.gogo.entity.TotalScoreEntity;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class ScoreDetaiActivity extends BaseActivity implements OnClickListener {
	private final int GET_DATA_OK = 1;
	// 以下三个值 主要用来 获取cookie 信息
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	// 订单列表
	private ListView mDetailScoreLV;
	// 第三列表适配器
	private DetailScoreAdapter mScoreAdapter;
	// 实体bean
	private DetailScoreEntity mScoreEntity;
	private TextView mScoreBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_myscoredetailmsg);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();

		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		mDetailScoreLV = (ListView) this
				.findViewById(R.id.detailscore_datalist);
		mScoreBack = (TextView) this.findViewById(R.id.detailscore_tittle);
		mScoreBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.detailscore_tittle:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getPersonScore();
			}
		}).start();
	}

	protected void getPersonScore() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.GET_SCORE_MSG).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response response) throws IOException {
				String personScoreBody = response.body().string();
				LogUtils.e("个人积分详情--->>", personScoreBody);
				Gson gson = new Gson();
				mScoreEntity = gson.fromJson(personScoreBody,
						DetailScoreEntity.class);
				Message msg = handler.obtainMessage(GET_DATA_OK);
				msg.sendToTarget();

			}

			@Override
			public void onFailure(Request request, IOException e) {
			}

		});
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_DATA_OK:
				mScoreAdapter = new DetailScoreAdapter(getApplicationContext(),
						mScoreEntity.getData());
				mDetailScoreLV.setAdapter(mScoreAdapter);

				break;

			default:
				break;
			}
		};
	};

}
