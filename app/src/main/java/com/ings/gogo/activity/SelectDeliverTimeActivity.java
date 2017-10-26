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
import com.ings.gogo.adapter.TimeSelectAdapter;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.OrderTimeEntity;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class SelectDeliverTimeActivity extends BaseActivity {
	private static final int GET_TIME = 1;
	private final int TIME_RESULT = 104;
	private ListView mTimeAllTime;
	private TextView mTimeCancel;
	private OrderTimeEntity entity;
	private TimeSelectAdapter adapter;
	private String IsToday;
	private TextView mTimeTips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_selecttime);
		mTimeTips = (TextView) this.findViewById(R.id.time_tips);
		Bundle bundle = this.getIntent().getExtras();
		IsToday = bundle.getString("IsToday");
		LogUtils.e("选择时间中的是否是今日", IsToday);
		if (IsToday.equals("1")) {
			mTimeTips.setText("今天");
		} else {
			mTimeTips.setText("明天");
		}
		mTimeAllTime = (ListView) this.findViewById(R.id.time_allTimeLV);

		mTimeCancel = (TextView) this.findViewById(R.id.time_cancel);
		mTimeCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getTimeDetail();
			}
		}).start();

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_TIME:
				mTimeAllTime.setAdapter(adapter);
				mTimeAllTime.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.putExtra("orderTime",
								entity.getData().get(position).getTimestr());
						setResult(TIME_RESULT, intent);
						finish();
					}
				});
				break;

			default:
				break;
			}
		};
	};

	protected void getTimeDetail() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(
				BaseData.GET_TIME + IsToday).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String orderTimeBody = response.body().string();
				LogUtils.e("订单中的时间段--->>", orderTimeBody);
				Gson gson = new Gson();
				entity = gson.fromJson(orderTimeBody, OrderTimeEntity.class);
				adapter = new TimeSelectAdapter(getApplicationContext(), entity
						.getData());
				Message msg = handler.obtainMessage(GET_TIME);
				msg.sendToTarget();

			}

		});
	}

}
