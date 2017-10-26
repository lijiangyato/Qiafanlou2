package com.ings.gogo.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.phone.mrpc.core.ac;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ShopCarActivity.ViewHolder;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.MyOrderAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.db.MyShopSqlHelper;
import com.ings.gogo.entity.MyOrderResultEntity;
import com.ings.gogo.entity.SelectedGoodsEntity;
import com.ings.gogo.entity.SubmitResultEntity;
import com.ings.gogo.entity.TotalScoreEntity;
import com.ings.gogo.utils.LogUtils;
import com.loc.ax;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class MyOrderActivity extends BaseActivity implements OnClickListener {
	private final int SUBMIT_OK = 1;
	private final int GET_TOTAL_SCORE = 2;
	private final int STOCK_IS_OK = 6;
	// 获取地址的返回参数
	private final int ORDER_RESULT = 103;
	// 快餐 获取地址的请求参数
	private final int ORDER_REQUEST = 3;
	// 获取时间段的返回参数
	private final int TIME_RESULT = 104;
	// 快餐 获取时间段的请求参数
	private final int TIME_REQUEST = 4;
	// 获取备注返回参数
	private final int MORE_RESULT = 105;
	// 获取备注请求参数
	private final int MORE_REQUEST = 5;
	// 回到父界面
	private ImageView mOrderBack2Parent;
	// 快餐设置时间
	private TextView mOrderFastSetTime;
	// 快餐设置地址
	private TextView mOrderFastSetPlace;
	// 获取积分
	private TextView mOrderScore;
	// 以下三个值 主要用来 获取cookie 信息
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	// 获取积分的实体
	private TotalScoreEntity mTotalScoreEntity;
	// 用户的积分总数
	private float mOrderTotalScore;
	// 用户可以兑换的人民币
	private float mOrderScoreMoney;
	// 是否使用积分
	private CheckBox mOrderIsUserScore;
	// 备注
	private TextView mOrderMoreMsg;
	// 选择支付方式
	private TextView mOrderPayWayTv;
	// 取消选择支付方式的dialog
	private TextView mDialogCancel;
	// 自定义的选择支付方式的dialog
	private Dialog myDialog;
	private Dialog myTimeDialog;
	// 在线支付的checkBox
	private TextView mOrderPayOnNet;
	// 在货到付款的checkBox
	private TextView mOrderPayByHandler;
	// 订单 shopPointName
	private TextView mOrderPointName;
	// 从购物车传来的商品 总金额
	private String mOrderTotalMoney;
	// 实体bean
	private List<SelectedGoodsEntity> mSelectEntity = null;
	// 显示需要支付的商品的listView
	private ListView mOrderGoodsLV;
	// 适配器
	private MyOrderAdapter mOrderAdapter;
	// 提交订单
	private TextView mOrderGoPayMoney;
	// 检查库存的jsonArray
	private JSONArray stockArray;
	// 提交订单的json字符串
	private JSONObject submitOrderJOBJ;
	// 需要支付的money
	private TextView mOrderShouldPay;
	// 商品id
	private String mOrderProid;
	// pointname
	private String mPointName;
	// pointId
	private String mPointID;
	// checkStock返回的实体
	private MyOrderResultEntity mResultEntity;
	private String mPayWay;
	private String mIsTakeBySelf = "0";
	private String mIsTaday;
	private String mTemIsToDay;
	private String mIsUserScore = "0";
	private CheckBox mIsTakeBySelfCB;
	private CheckBox mIsUseScoreCB;
	private String mPlaceID;
	private String versionName;
	private int versioncode;
	private SubmitResultEntity mSubmitEntity;
	private MyShopSqlHelper mySqlHelper;
	private SQLiteDatabase db;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_addorder);
		PackageManager pm = this.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(this.getPackageName(), 0);
			versionName = pi.versionName;
			versioncode = pi.versionCode;
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mySqlHelper = new MyShopSqlHelper(getApplicationContext(), "GOGO",
				null, 1);
		db = mySqlHelper.getWritableDatabase();

		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		mPointName = myApplication.getPointName();
		mPointID = myApplication.getPointID();
		LogUtils.e("pointID和pointname", mPointID + mPointName + "---" + "版本名称"
				+ versionName + "版本号" + versioncode);
		Bundle bundle = this.getIntent().getExtras();
		mOrderTotalMoney = bundle.getString("totalMoney");

		mSelectEntity = (List<SelectedGoodsEntity>) bundle
				.getSerializable("goodsList");

		LogUtils.e("购物出传过来的值---？？？", "金额：" + mOrderTotalMoney + "商品集合大小："
				+ mSelectEntity.size());
		for (int i = 0; i < mSelectEntity.size(); i++) {
			mTemIsToDay = mSelectEntity.get(0).getIsToday();
		}
		if (mTemIsToDay.equals("2")) {
			mIsTaday = "1";
		} else if (mTemIsToDay.equals("3")) {
			mIsTaday = "0";
		}

		stockArray = new JSONArray();

		initViews();

	}

	@SuppressLint("CutPasteId")
	private void initViews() {
		// TODO Auto-generated method stub
		mOrderBack2Parent = (ImageView) this.findViewById(R.id.order_back);
		mOrderBack2Parent.setOnClickListener(this);
		mOrderFastSetTime = (TextView) this
				.findViewById(R.id.order_fastFoodsSetTime);
		mOrderFastSetTime.setOnClickListener(this);
		mOrderFastSetPlace = (TextView) this
				.findViewById(R.id.order_fastFoodsSetPlace);
		mOrderFastSetPlace.setOnClickListener(this);
		mOrderScore = (TextView) this.findViewById(R.id.order_ScoreEdt);
		mOrderIsUserScore = (CheckBox) this.findViewById(R.id.order_isUseScore);
		mOrderMoreMsg = (TextView) this.findViewById(R.id.order_moreMsg);
		mOrderMoreMsg.setOnClickListener(this);
		mOrderPayWayTv = (TextView) this.findViewById(R.id.order_payWay);
		mOrderPayWayTv.setOnClickListener(this);
		mOrderGoodsLV = (ListView) this.findViewById(R.id.order_fastFoodsLV);
		mOrderAdapter = new MyOrderAdapter(getApplicationContext(),
				mSelectEntity);
		mOrderGoodsLV.setAdapter(mOrderAdapter);
		setListViewHeightBasedOnChildren(mOrderGoodsLV);
		mOrderGoPayMoney = (TextView) this.findViewById(R.id.order_goPayMoney);
		mOrderGoPayMoney.setOnClickListener(this);
		mOrderShouldPay = (TextView) this.findViewById(R.id.order_totalMoney);
		mOrderShouldPay.setText("￥" + mOrderTotalMoney);
		mIsTakeBySelfCB = (CheckBox) this.findViewById(R.id.order_isTakeBySelf);
		mIsUseScoreCB = (CheckBox) this.findViewById(R.id.order_isUseScore);
		mOrderPointName = (TextView) this
				.findViewById(R.id.order_fastFoodsPoints);
		mOrderPointName.setText(mPointName);
		mIsTakeBySelfCB
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							mIsTakeBySelf = "1";
							LogUtils.e("mIsTakeBySelf---->>>1", mIsTakeBySelf);
						} else {
							mIsTakeBySelf = "0";
							LogUtils.e("mIsTakeBySelf---->>>0", mIsTakeBySelf);
						}

					}
				});
		mIsUseScoreCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mIsUserScore = "1";
					LogUtils.e("mIsUserScore---->>>1", mIsUserScore);
				} else {
					mIsUserScore = "0";
					LogUtils.e("mIsUserScore---->>>0", mIsUserScore);
				}

			}
		});

	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(10, 10);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getMyScore();
			}
		}).start();
	}

	public void getFastTime(View view) {
		LogUtils.e("选则时间执行到了---》》》", "选则时间执行到了---》》》");
		Intent intent = new Intent(this, SelectDeliverTimeActivity.class);
		startActivityForResult(intent, TIME_REQUEST);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUBMIT_OK:

				if (mSubmitEntity.getSuccess().equals(true) && mPayWay == "2") {
					// 清空已经支付了的数据
					for (SelectedGoodsEntity goodsSelected : mSelectEntity) {

						Cursor cursor = db.rawQuery(
								"delete from mycar where name = '"
										+ goodsSelected.getGoodsName() + "'",
								null);
						cursor.moveToNext();

					}
					Intent intent2Ok = new Intent(getApplicationContext(),
							BuyOkActivity.class);
					startActivity(intent2Ok);

				} else if (mSubmitEntity.getSuccess().equals(true)
						&& mSubmitEntity.getData().getPaytotal() == 0) {
					Intent intent2Ok = new Intent(getApplicationContext(),
							BuyOkActivity.class);
					startActivity(intent2Ok);
				} else if (mSubmitEntity.getSuccess().equals(true)
						&& mPayWay == "1"
						&& mSubmitEntity.getData().getPaytotal() > 0) {
					for (SelectedGoodsEntity goodsSelected : mSelectEntity) {

						Cursor cursor = db.rawQuery(
								"delete from mycar where name = '"
										+ goodsSelected.getGoodsName() + "'",
								null);
						cursor.moveToNext();

					}
					finish();
					Intent intent2Pay = new Intent(getApplicationContext(),
							MyOrderPayActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("LeftTime", mSubmitEntity.getData()
							.getLasttime());
					bundle.putDouble("PostMoney", mSubmitEntity.getData()
							.getPostage());
					bundle.putDouble("PayMoney", mSubmitEntity.getData()
							.getPaytotal());
					bundle.putString("CheapInfo", mSubmitEntity.getData()
							.getCheapinfo());
					bundle.putString("OrderID", mSubmitEntity.getData()
							.getOrderno());
					bundle.putSerializable("goodsList",
							(Serializable) mSelectEntity);
					intent2Pay.putExtras(bundle);
					startActivity(intent2Pay);

				}

				break;
			case STOCK_IS_OK:
				if (mResultEntity.getSuccess().equals(true)) {

					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							submitMyOrder();
						}

					}).start();

				} else {
					showToastLong(mResultEntity.getMsg());
				}

				break;
			case GET_TOTAL_SCORE:
				// if (mTotalScoreEntity.getMsg().equals("未授权")) {
				// showToastLong(mTotalScoreEntity.getMsg() + "，请登录");
				//
				// } else {}
				if (mTotalScoreEntity.getData().getScore() == 0) {

					mOrderScore.setText("剩余积分（0）");
					mOrderIsUserScore.setClickable(false);
				} else if (mTotalScoreEntity.getData().getScore() < Float
						.parseFloat(mOrderTotalMoney) * 100) {
					mOrderTotalScore = mTotalScoreEntity.getData().getScore();
					mOrderScoreMoney = mOrderTotalScore / 100;
					mOrderScore.setText("可用" + mOrderTotalScore + "积分兑换" + "¥ "
							+ mOrderScoreMoney);

				} else if (mTotalScoreEntity.getData().getScore() > Float
						.parseFloat(mOrderTotalMoney) * 100) {

					mOrderScore.setText("可用"
							+ Float.parseFloat(mOrderTotalMoney) * 100 + "积分兑换"
							+ "¥ " + mOrderTotalMoney);

				} else if (mSubmitEntity.getSuccess().equals(false)) {
					Intent intent = new Intent(getApplicationContext(),
							MainPageActivity.class);
					startActivity(intent);
				}

				break;

			default:
				break;
			}
		};
	};

	// 捕获返回键的方法1
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下BACK，同时没有重复
			ShopCarActivity.mSeclectedEntity.clear();
			ShopCarActivity.mCheckAll.setChecked(false);
			// ShopCarActivity.ViewHolder.checkBox.setChecked(false);
			// NewShopCarActivity.mSeclectedEntity.clear();
			// NewShopCarActivity.mCheckAll.setChecked(false);
			// NewShopCarActivity.ViewHolder.checkBox.setChecked(false);
			LogUtils.e("回退键点击到了---》》", "回退键点击到了---》》》");
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.order_back:
			ShopCarActivity.mSeclectedEntity.clear();
			ShopCarActivity.mCheckAll.setChecked(false);
			// ShopCarActivity.ViewHolder.checkBox.setChecked(false);
			// NewShopCarActivity.mSeclectedEntity.clear();
			// NewShopCarActivity.mCheckAll.setChecked(false);
			// NewShopCarActivity.ViewHolder.checkBox.setChecked(false);

			this.finish();

			break;

		case R.id.order_fastFoodsSetPlace:
			getOrderPlace();
			break;
		case R.id.order_moreMsg:
			Intent intent = new Intent(getApplicationContext(),
					MoreMsgActvity.class);

			startActivityForResult(intent, MORE_REQUEST);
			break;
		case R.id.order_payWay:
			myDialog = new Dialog(this);
			myDialog.setContentView(R.layout.layout_mydialog);
			myDialog.setTitle(R.string.order_selectpayway);
			myDialog.show();
			innitDiaLog();
			break;
		case R.id.order_goPayMoney:
			innitPayWay();
			if (!isOkplaceMsg()) {
				return;
			}
			if (!isOktimeMsg()) {
				return;
			}
			if (!isOkPayOnLine()) {
				return;
			}
			for (int i = 0; i < mSelectEntity.size(); i++) {
				JSONObject myObject = new JSONObject();
				try {
					myObject.put("amount", mSelectEntity.get(i).getGoodsNum());
					myObject.put("proid", mSelectEntity.get(i).getGoodsProid());
					// stockJobj.put("", mSelectEntity.get(i))
					stockArray.put(i, myObject);
					LogUtils.e("Myorder--->>create方法中的objectss",
							myObject.toString());

				} catch (JSONException e) {

					return;
				}
			}
			LogUtils.e("Myorder--->>create方法中的array", stockArray.toString());
			new Thread(new Runnable() {

				@Override
				public void run() {

					// 提交订单之前 先检查库存信息
					checkStock();
				}
			}).start();

			break;
		case R.id.order_fastFoodsSetTime:

			Intent intent2Time = new Intent(this,
					SelectDeliverTimeActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("IsToday", mIsTaday);
			intent2Time.putExtras(bundle);
			startActivityForResult(intent2Time, TIME_REQUEST);
			break;

		default:
			break;
		}

	}

	// 检查库存

	private void checkStock() {
		// TODO Auto-generated method stub
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder()
				.add("pointid", mPointID).add("Stream", stockArray.toString())
				.build();
		LogUtils.e("拼接的字符---》》》", stockArray.toString().trim());
		Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.CHECK_STOCK).post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String checkStockBody = response.body().string();
				LogUtils.e("检查库存的Body---？？？", checkStockBody);
				Gson gson = new Gson();
				mResultEntity = gson.fromJson(checkStockBody,
						MyOrderResultEntity.class);
				Message msg = handler.obtainMessage(STOCK_IS_OK);
				msg.sendToTarget();
			} else {
				LogUtils.e("检查库存的Body---？？？", response.body().string());

			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	// 初始化自定义的选择支付方式的dialog
	private void innitDiaLog() {
		// TODO Auto-generated method stub
		mOrderPayOnNet = (TextView) myDialog
				.findViewById(R.id.mOrderPayOnLineTv);
		mOrderPayByHandler = (TextView) myDialog
				.findViewById(R.id.mOrderPayOnHandlerTv);
		mOrderPayOnNet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mOrderPayWayTv.setText("在线支付");
				mOrderPayByHandler.setClickable(false);
				myDialog.dismiss();
			}
		});

		mOrderPayByHandler.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 如果选中，显示密码
				mOrderPayWayTv.setText("货到付款");
				mOrderPayOnNet.setClickable(false);
				myDialog.dismiss();
			}
		});

		mDialogCancel = (TextView) myDialog.findViewById(R.id.myDialog_cancel);
		mDialogCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
			}
		});

	}

	private void innitPayWay() {
		// TODO Auto-generated method stub
		if (mOrderPayWayTv.getText().toString().equals("在线支付")) {
			mPayWay = "1";
			LogUtils.e("mPayWay---->>>1", mPayWay);

		} else if (mOrderPayWayTv.getText().toString().equals("货到付款")) {
			mPayWay = "2";
			LogUtils.e("mPayWay---->>>2", mPayWay);
		}

	}

	// 获取积分的总数
	protected void getMyScore() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.GET_TOTAL_SCORE).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response response) throws IOException {
				String orderScoreBody = response.body().string();
				LogUtils.e("订单积分总数--->>", orderScoreBody);
				Gson gson = new Gson();
				mTotalScoreEntity = gson.fromJson(orderScoreBody,
						TotalScoreEntity.class);

				Message msg = handler.obtainMessage(GET_TOTAL_SCORE);
				msg.sendToTarget();

			}

			@Override
			public void onFailure(Request request, IOException e) {
			}

		});
	}

	// 提交订单
	private void submitMyOrder() {
		// TODO Auto-generated method stub
		LogUtils.e("提交订单执行到了", "提交订单执行到了");
		// Encoded
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder()
				.add("usescore", mIsUserScore).add("istoday", mIsTaday)
				.add("consigneeid", mPlaceID)
				.add("consigneetime", mOrderFastSetTime.getText().toString())
				.add("pointid", mPointID).add("befrom", "android")
				.add("takeself", mIsTakeBySelf).add("versionno", versionName)
				.add("mark", mOrderMoreMsg.getText().toString())
				.add("payway", mPayWay).add("Stream", stockArray.toString())
				.build();
		LogUtils.e("拼接的字符---》》》", stockArray.toString().trim() + "支付方式"
				+ mPayWay + "是否使用积分" + mIsUserScore + "地址id" + mPlaceID
				+ "上门自提" + mIsTakeBySelf);
		Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.SUBMIT_ORDER).post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String submitOrderBody = response.body().string();
				LogUtils.e("提交订单的的Body---？？？", submitOrderBody);
				Gson gson = new Gson();
				mSubmitEntity = gson.fromJson(submitOrderBody,
						SubmitResultEntity.class);
				Message msg = handler.obtainMessage(SUBMIT_OK);
				msg.sendToTarget();
			} else {
				LogUtils.e("检查库存的Body---？？？", response.body().string());

			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	// 设置收货地址
	private void getOrderPlace() {
		// TODO Auto-generated method stub
		Intent intent2Map = new Intent(getApplicationContext(),
				PlaceManageActivity.class);
		startActivityForResult(intent2Map, ORDER_REQUEST);

	}

	// 检查地址信息是否为空
	private boolean isOkplaceMsg() {

		if (TextUtils.isEmpty(mOrderFastSetPlace.getText())) {
			showToastShort(R.string.placenotnull);
			return false;
		}

		return true;
	}

	// 检查送货时间是否为空
	private boolean isOktimeMsg() {

		if (TextUtils.isEmpty(mOrderFastSetTime.getText())) {
			showToastShort(R.string.order_settime);
			return false;
		}

		return true;
	}

	// 检查支付方式是否为空
	private boolean isOkPayOnLine() {

		if (TextUtils.isEmpty(mPayWay)) {
			showToastShort(R.string.order_selectpayway);
			return false;
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ORDER_REQUEST) {
			if (resultCode == ORDER_RESULT) {
				String AddPlace = data.getStringExtra("orderPlaceName");
				mPlaceID = data.getStringExtra("orderPlaceID");
				LogUtils.e("result地址选择--->>>AddPlace", AddPlace + "--"
						+ mPlaceID);
				mOrderFastSetPlace.setText(AddPlace);

			}
		}

		if (requestCode == TIME_REQUEST) {
			if (resultCode == TIME_RESULT) {
				String orderTime = data.getStringExtra("orderTime");
				LogUtils.e("result--->>>orderTime", orderTime);
				mOrderFastSetTime.setText(orderTime);

			}
		}
		if (requestCode == MORE_REQUEST) {
			if (resultCode == MORE_RESULT) {
				String moreMsg = data.getStringExtra("moreMsg");
				LogUtils.e("result--->>>moreMsg", moreMsg);
				mOrderMoreMsg.setText(moreMsg);

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
