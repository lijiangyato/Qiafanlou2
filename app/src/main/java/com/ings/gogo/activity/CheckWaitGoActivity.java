package com.ings.gogo.activity;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.CheckOrderAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.MyCheckOrderEntity;
import com.ings.gogo.entity.MyOrderResultEntity;
import com.ings.gogo.entity.TotalScoreEntity;
import com.ings.gogo.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class CheckWaitGoActivity extends BaseActivity implements
		OnClickListener {
	private final int GET_DATA_OK = 1;
	private final int CANCEL_OK = 2;
	private final int DELETE_OK = 3;
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
	private MyOrderResultEntity mResultEntity;
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
					// mCheckAdapter = new CheckOrderAdapter(
					// getApplicationContext(), mCheckEntity.getData());
					mCheckDataLV.setAdapter(new MyCheckAdapter());
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
				showToastLong("取消成功");
				if (mResultEntity.getSuccess().equals(true)) {
					getOrderData();
				}

				break;

			default:
				break;
			}
		};
	};

	protected void getOrderData() {
		// TODO Auto-generated method stub

		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.GET_DIFFERENT_STATE + "1").build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response response) throws IOException {
				orderListBody = response.body().string();
				LogUtils.e("代配送订单列表body--->>", orderListBody);
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

	private class MyCheckAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mCheckEntity.getData().size();
		}

		@Override
		public Object getItem(int position) {
			return mCheckEntity.getData().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.layout_checkorderlistview, null);

				ViewHolder viewHolder = new ViewHolder(convertView);

				convertView.setTag(viewHolder);
			}

			initializeViews(mCheckEntity.getData().get(position),
					(ViewHolder) convertView.getTag());
			return convertView;
		}

		private void initializeViews(
				final MyCheckOrderEntity.OrderDatas entity, ViewHolder holder) {
			holder.mOrderID.setText("订单号：" + entity.getOrderno());
			if (entity.getState().equals("待付款")) {
				holder.mOrderGoPay.setVisibility(View.VISIBLE);
				holder.mOrderDelete.setVisibility(View.GONE);
				holder.mOrderGoPay.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent inetnt2Pay = new Intent(myApplication,
								SelectPayWayActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("OrderID", entity.getOrderno());
						bundle.putString("PayMoney", entity.getTotal() + "");
						inetnt2Pay.putExtras(bundle);
						inetnt2Pay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(inetnt2Pay);

					}
				});
			} else if (entity.getState().equals("待配送")) {
				holder.mOrderCancel.setVisibility(View.VISIBLE);
				holder.mOrderDelete.setVisibility(View.GONE);
				holder.mOrderGoPay.setVisibility(View.GONE);
				holder.mOrderCancel.setText("取消订单");

			} else if (entity.getState().equals("已作废")
					|| entity.getState().equals("已取消")
					|| entity.getState().equals("已完成")) {
				holder.mOrderDelete.setVisibility(View.VISIBLE);
				holder.mOrderCancel.setVisibility(View.GONE);
				holder.mOrderGoPay.setVisibility(View.GONE);

			}
			holder.mOrderCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							cancelOrder();
						}

						private void cancelOrder() {
							// TODO Auto-generated method stub
							OkHttpClient okHttpClient = new OkHttpClient();
							RequestBody requestBody = new FormEncodingBuilder()
									.add("orderno", entity.getOrderno())
									.add("type", "1").build();
							Request request = new Request.Builder()
									.addHeader(
											"Cookie",
											aspnetauth + ";"
													+ ASP_NET_SessionId)
									.url(BaseData.CANCEL_ORDER)
									.post(requestBody).build();
							Response response = null;

							try {
								response = okHttpClient.newCall(request)
										.execute();
								if (response.isSuccessful()) {
									String cancelOrderBody = response.body()
											.string();
									LogUtils.e("取消的Body---？？？", cancelOrderBody);
									Gson gson = new Gson();
									mResultEntity = gson.fromJson(
											cancelOrderBody,
											MyOrderResultEntity.class);
									Message msg = handler
											.obtainMessage(CANCEL_OK);
									msg.sendToTarget();
								}
							} catch (IOException e) {
								e.printStackTrace();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}

					}).start();
				}
			});
			holder.mOrderDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							deleteOrder();
						}

						private void deleteOrder() {
							// TODO Auto-generated method stub
							OkHttpClient okHttpClient = new OkHttpClient();
							RequestBody requestBody = new FormEncodingBuilder()
									.add("orderno", entity.getOrderno())
									.add("type", "4").build();
							Request request = new Request.Builder()
									.addHeader(
											"Cookie",
											aspnetauth + ";"
													+ ASP_NET_SessionId)
									.url(BaseData.CANCEL_ORDER)
									.post(requestBody).build();
							Response response = null;

							try {
								response = okHttpClient.newCall(request)
										.execute();
								if (response.isSuccessful()) {
									String deleteOrderBody = response.body()
											.string();
									LogUtils.e("删除订单Body---？？？",
											deleteOrderBody);
									Gson gson = new Gson();
									mResultEntity = gson.fromJson(
											deleteOrderBody,
											MyOrderResultEntity.class);
									Message msg = handler
											.obtainMessage(DELETE_OK);
									msg.sendToTarget();
								}
							} catch (IOException e) {
								e.printStackTrace();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
					}).start();
				}

			});

			android.view.ViewGroup.LayoutParams params = holder.mOrderListImage
					.getLayoutParams();
			params.height = (int) (OtherMainPageActivity.mWidth / 4);
			params.width = (int) (OtherMainPageActivity.mWidth / 4);
			holder.mOrderListImage.setLayoutParams(params);
			ImageLoader.getInstance().displayImage(
					entity.getOrderPro().getImgurl(), holder.mOrderListImage,
					BaseActivity.options);
			holder.mOrderListName.setText(entity.getOrderPro().getProname());
			holder.mOrderListNum.setText("共" + entity.getOrderPro().getPronum()
					+ "个商品");
			holder.mOrderListState.setText(entity.getState());
			holder.mOrderMoney.setText("¥" + entity.getTotal());

		}

		public class ViewHolder {
			private final ImageView mOrderListImage;
			private final TextView mOrderListName;
			private final TextView mOrderListNum;
			private final TextView mOrderListState;
			private final TextView mOrderID;
			private final Button mOrderCancel;
			private final ImageView mOrderDelete;
			private final Button mOrderGoPay;
			private final TextView mOrderMoney;
			public final View root;

			// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

			public ViewHolder(View root) {
				mOrderListImage = (ImageView) root
						.findViewById(R.id.orderlist_goodsamage);
				mOrderListNum = (TextView) root
						.findViewById(R.id.orderlist_orderNum);
				mOrderListState = (TextView) root
						.findViewById(R.id.orderlist_goodsState);
				mOrderListName = (TextView) root
						.findViewById(R.id.orderlist_ordermsg);
				mOrderID = (TextView) root.findViewById(R.id.orderlist_orderID);
				mOrderCancel = (Button) root
						.findViewById(R.id.orderlist_orderCancel);
				mOrderDelete = (ImageView) root
						.findViewById(R.id.orderlist_orderDelete);
				mOrderGoPay = (Button) root.findViewById(R.id.orderlist_gopay);
				mOrderMoney = (TextView) root
						.findViewById(R.id.orderlist_orderTotalMoney);

				this.root = root;
			}
		}

	}

}
