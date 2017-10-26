package com.ings.gogo.adapter;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.CheckMyOrderActivity;
import com.ings.gogo.activity.MainPageActivity;
import com.ings.gogo.activity.MyOrderPayActivity;
import com.ings.gogo.activity.OtherMainPageActivity;
import com.ings.gogo.activity.SelectPayWayActivity;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseActivity;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.AddressTagEntity;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity.FastFoodsDatas;
import com.ings.gogo.entity.MyCheckOrderEntity;
import com.ings.gogo.entity.MyCheckOrderEntity.OrderDatas;
import com.ings.gogo.entity.MyOrderResultEntity;
import com.ings.gogo.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CheckOrderAdapter extends BaseAdapter {
	private final int CANCEL_OK = 1;
	private final int DELETE_OK = 2;
	private Context context;
	private LayoutInflater layoutInflater;
	private List<MyCheckOrderEntity.OrderDatas> entitys;
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	private MyOrderResultEntity mResultEntity;

	public CheckOrderAdapter(Context context, List<OrderDatas> entitys) {
		super();
		this.context = context;
		this.entitys = entitys;
		this.layoutInflater = LayoutInflater.from(context);
		this.myApplication = (UILApplication) context.getApplicationContext();
		this.ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		this.aspnetauth = myApplication.getAspnetauth();
	}

	@Override
	public int getCount() {

		return entitys.size();
	}

	@Override
	public Object getItem(int position) {
		return entitys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.layout_checkorderlistview, null);
			ViewHolder viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		initializeViews(entitys.get(position),
				(ViewHolder) convertView.getTag());
		return convertView;
	}

	private void initializeViews(final MyCheckOrderEntity.OrderDatas entity,
			ViewHolder holder) {
		holder.mOrderID.setText("订单号：" + entity.getOrderno());
		if (entity.getState().equals("待付款")) {
			holder.mOrderGoPay.setVisibility(View.VISIBLE);
			holder.mOrderCancel.setVisibility(View.VISIBLE);
			holder.mOrderCancel.setText("取消订单");
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
		} else if (entity.getState().equals("已作废")
				|| entity.getState().equals("已取消")
				|| entity.getState().equals("已完成")) {
			holder.mOrderDelete.setVisibility(View.VISIBLE);
			holder.mOrderCancel.setVisibility(View.GONE);
			holder.mOrderGoPay.setVisibility(View.GONE);

		} else if (entity.getState().equals("待配送")) {
			holder.mOrderCancel.setVisibility(View.VISIBLE);
			holder.mOrderDelete.setVisibility(View.GONE);
			holder.mOrderGoPay.setVisibility(View.GONE);
			holder.mOrderCancel.setText("取消订单");
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
								.addHeader("Cookie",
										aspnetauth + ";" + ASP_NET_SessionId)
								.url(BaseData.CANCEL_ORDER).post(requestBody)
								.build();
						Response response = null;

						try {
							response = okHttpClient.newCall(request).execute();
							if (response.isSuccessful()) {
								String cancelOrderBody = response.body()
										.string();
								LogUtils.e("取消的Body---？？？", cancelOrderBody);
								Gson gson = new Gson();
								mResultEntity = gson.fromJson(cancelOrderBody,
										MyOrderResultEntity.class);
								Message msg = handler.obtainMessage(CANCEL_OK);
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
								.addHeader("Cookie",
										aspnetauth + ";" + ASP_NET_SessionId)
								.url(BaseData.CANCEL_ORDER).post(requestBody)
								.build();
						Response response = null;

						try {
							response = okHttpClient.newCall(request).execute();
							if (response.isSuccessful()) {
								String deleteOrderBody = response.body()
										.string();
								LogUtils.e("删除订单Body---？？？", deleteOrderBody);
								Gson gson = new Gson();
								mResultEntity = gson.fromJson(deleteOrderBody,
										MyOrderResultEntity.class);
								Message msg = handler.obtainMessage(DELETE_OK);
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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CANCEL_OK:
				if (mResultEntity.getSuccess().equals(true)) {
					// CheckMyOrderActivity activity = new
					// CheckMyOrderActivity();
					// activity.refreshAdapter();
				}

				break;
			case DELETE_OK:
				if (mResultEntity.getSuccess().equals(true)) {
					// CheckMyOrderActivity activity = new
					// CheckMyOrderActivity();
					// activity.refreshAdapter();
					// CheckOrderAdapter adapter = new
					// CheckOrderAdapter(context,
					// entitys);
					// adapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
		};
	};

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
