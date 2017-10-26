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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.mapcore2d.en;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.AddressAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.AddressEntity;
import com.ings.gogo.entity.ShopPointEntity;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PlaceManageActivity extends BaseActivity implements
		OnClickListener {
	private static final int GET_DATAOK = 1;
	private final int ORDER_RESULT = 103;
	// 地址列表
	private ListView mPlaceListView;
	// 添加地址
	private TextView mAddPlace;
	// 回到上一页
	private ImageView mBackToParent;

	private AddressAdapter mAddressAdapter;
	private AddressEntity entity;
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	private String telNum;
	// shopPoint
	private LatLng lanlngPoint;
	// 输入的地址信息
	private LatLng lanlngPlace;
	private float tureRange;
	private float distance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_manageplace);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		lanlngPoint = new LatLng(Double.parseDouble(myApplication.getWeiDu()),
				Double.parseDouble(myApplication.getJingDu()));
		innitViews();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_DATAOK:
				if (entity.getData().size() == 0) {
					showToastLong("暂无数据，请先添加信息");
				} else {
					mPlaceListView.setAdapter(mAddressAdapter);
					mPlaceListView
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									lanlngPlace = new LatLng(Double
											.parseDouble(entity.getData()
													.get(position).getGpsy()),
											Double.parseDouble(entity.getData()
													.get(position).getGpsx()));
									distance = AMapUtils.calculateLineDistance(
											lanlngPoint, lanlngPlace);
									tureRange = 500 - distance;
									LogUtils.e("选收获地址--》》》", tureRange+"   "+distance);
									if (tureRange > 0) {
										Intent intent = new Intent();
										intent.putExtra(
												"orderPlaceName",
												entity.getData().get(position)
														.getConsignee_add()
														+ "\n"
														+ entity.getData()
																.get(position)
																.getConsignee_name()
														+ "  "
														+ entity.getData()
																.get(position)
																.getSex()
														+ "  "
														+ entity.getData()
																.get(position)
																.getConsignee_phone());
										intent.putExtra("orderPlaceID", entity
												.getData().get(position)
												.getConsigneeid());
										setResult(ORDER_RESULT, intent);
										finish();
									} else {
										showToastLong("超出配送范围");
									}

								}
							});

				}

				break;

			default:
				break;
			}
		};
	};

	private void innitViews() {
		// TODO Auto-generated method stub
		mAddPlace = (TextView) this.findViewById(R.id.placeManage_addPlace);
		mAddPlace.setOnClickListener(this);
		mBackToParent = (ImageView) this.findViewById(R.id.placeManage_back);
		mBackToParent.setOnClickListener(this);
		mPlaceListView = (ListView) this
				.findViewById(R.id.placeManage_placeListView);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getPlaceDataList();
			}
		}).start();
	}

	protected void getPlaceDataList() {
		// TODO Auto-generated method stub
		// 创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.GET_ADDRESS_DATA).build();
		LogUtils.e("Cookie-->>", aspnetauth + ";" + ASP_NET_SessionId);
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String pointBody = response.body().string();
				LogUtils.e("地址列表--->>", pointBody);
				Gson gson = new Gson();
				entity = gson.fromJson(pointBody, AddressEntity.class);
				mAddressAdapter = new AddressAdapter(getApplicationContext(),
						entity.getData());
				Message msg = handler.obtainMessage(GET_DATAOK);
				msg.sendToTarget();

			}

		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.placeManage_addPlace:
			Intent intent2AddPlace = new Intent(getApplicationContext(),
					AddPlaceActivity.class);
			startActivity(intent2AddPlace);

			break;
		case R.id.placeManage_back:
			finish();

			break;

		default:
			break;
		}
	}
}
