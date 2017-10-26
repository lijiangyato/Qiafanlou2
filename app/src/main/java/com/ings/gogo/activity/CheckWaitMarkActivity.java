package com.ings.gogo.activity;

import java.io.IOException;

import android.content.Context;
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
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.MyCheckOrderEntity;
import com.ings.gogo.entity.TotalScoreEntity;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class CheckWaitMarkActivity extends BaseActivity implements
		OnClickListener {
	private final int GET_DATA_OK = 1;
	private final int CANCEL_OK = 2;
	// 以下三个值 主要用来 获取cookie 信息
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	// 订单列表
	private ListView mCheckDataLV;
	// 第三列表适配器
	private CheckOrderAdapter mCheckAdapter;
	// 实体bean
	private MyCheckOrderEntity mCheckEntity;
	private TextView mCheckBack;
	private Context context;
	private String orderListBody;
	private TextView mNoMoreOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_mycheckorderlist);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		context = this.getApplicationContext();
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		View viewpagerBottom = View.inflate(this, R.layout.layout_orderbottom,
				null);
		mNoMoreOrder = (TextView) viewpagerBottom
				.findViewById(R.id.order_nomoreorder);
		mNoMoreOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogUtils.e("", "");
			}
		});
		mCheckDataLV = (ListView) this.findViewById(R.id.checkorder_datalist);
		mCheckDataLV.addFooterView(viewpagerBottom);
		handler.sendEmptyMessageDelayed(0, 1000);
		mCheckBack = (TextView) this.findViewById(R.id.checkorder_tittle);
		mCheckBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.checkorder_tittle:
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
				getOrderData();
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_DATA_OK:
				if (mCheckEntity.getData().size() == 0) {
					showToastLong("暂无数据");
				} else {
					mCheckAdapter = new CheckOrderAdapter(
							getApplicationContext(), mCheckEntity.getData());
					mCheckDataLV.setAdapter(mCheckAdapter);
					mCheckDataLV
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											getApplicationContext(),
											CheckOrderDetailActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("OrderNo", mCheckEntity
											.getData().get(position)
											.getOrderno());
									intent.putExtras(bundle);
									startActivity(intent);
								}
							});
				}
				break;
			case CANCEL_OK:
				mCheckAdapter = new CheckOrderAdapter(context,
						mCheckEntity.getData());
				mCheckDataLV.setAdapter(mCheckAdapter);
				break;

			default:
				break;
			}
		};
	};

	public void refreshAdapter() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getRefreshData();
			}
		}).start();
	}

	protected void getRefreshData() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.GET_DIFFERENT_STATE + "3").build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response response) throws IOException {
				orderListBody = response.body().string();
				// LogUtils.e("待评论订单列表body--->>", orderListBody);
				Gson gson = new Gson();
				mCheckEntity = gson.fromJson(orderListBody,
						MyCheckOrderEntity.class);
				Message msg = handler.obtainMessage(CANCEL_OK);
				msg.sendToTarget();

			}

			@Override
			public void onFailure(Request request, IOException e) {
			}

		});
	}

	protected void getOrderData() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.GET_DIFFERENT_STATE + "3").build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response response) throws IOException {
				orderListBody = response.body().string();
				LogUtils.e("待评论订单列表body--->>", orderListBody);
				Gson gson = new Gson();
				mCheckEntity = gson.fromJson(orderListBody,
						MyCheckOrderEntity.class);
				Message msg = handler.obtainMessage(GET_DATA_OK);
				msg.sendToTarget();

			}

			@Override
			public void onFailure(Request request, IOException e) {
			}

		});
	}

}
