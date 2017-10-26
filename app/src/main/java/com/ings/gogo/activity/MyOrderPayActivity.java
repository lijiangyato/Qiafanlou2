package com.ings.gogo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.db.MyShopSqlHelper;
import com.ings.gogo.entity.MyOrderResultEntity;
import com.ings.gogo.entity.MyShopCarEntity;
import com.ings.gogo.entity.SelectedGoodsEntity;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.utils.PayResult;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MyOrderPayActivity extends BaseActivity implements OnClickListener {
	// 支付回调的标志
	private final int SDK_PAY_FLAG = 1;
	// 支付倒计时的标志
	private final int LEFT_TIME = 2;
	// 支付确认的标识
	private final int PAY_CONFIRM = 3;
	// 以下三个值 主要用来 获取cookie 信息
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	// 传过来的剩余时间
	private int mPayLeftTime;
	// 传过来的邮费信息
	private Double mPayPostMoney;
	// 传过来的优惠信息
	private String mPayCheapInfo;
	// 传过来的需要支付的金额
	private Double mPayTotalMoney;
	// 传过来的订单id
	private String mPayOrderID;
	// 显示订单信息
	private TextView mPayShowOrderTotalMoney;
	private TextView mPayOrderCheapMsg;
	// 显示倒计时
	private TextView mPayLeftTimeTv;
	// 提交支付
	private Button mPayButton;
	// 是否选用支付宝的checkBox
	private CheckBox mPayWayCheckBox;
	// 支付方式
	private String mPayWay = "2";
	// 支付数据验证bean
	private MyOrderResultEntity mPayEntity;
	private Button mGiveUpPay;
	private String orderInfo;
	private List<SelectedGoodsEntity> mSelectEntity = null;
	private MyShopSqlHelper mySqlHelper;
	private SQLiteDatabase db;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_orderpaymoney);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		mySqlHelper = new MyShopSqlHelper(getApplicationContext(), "GOGO",
				null, 1);
		db = mySqlHelper.getWritableDatabase();
		Bundle bundle = this.getIntent().getExtras();
		mPayLeftTime = bundle.getInt("LeftTime") * 60;
		mPayCheapInfo = bundle.getString("CheapInfo");
		mPayOrderID = bundle.getString("OrderID");
		mPayPostMoney = bundle.getDouble("PostMoney");
		mPayTotalMoney = bundle.getDouble("PayMoney");
		mSelectEntity = (List<SelectedGoodsEntity>) bundle
				.getSerializable("goodsList");
		mPayLeftTimeTv = (TextView) this.findViewById(R.id.paymoney_lefttime);
		Message message = handler.obtainMessage(LEFT_TIME); // Message
		handler.sendMessageDelayed(message, 1000);
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		mPayButton = (Button) this.findViewById(R.id.paymoney_money);
		mPayButton.setText("确认支付¥" + mPayTotalMoney);
		mPayButton.setOnClickListener(this);
		mPayShowOrderTotalMoney = (TextView) this
				.findViewById(R.id.paymoney_ordertotalMoney);
		mPayOrderCheapMsg = (TextView) this
				.findViewById(R.id.paymoney_ordercheapmsg);
		mPayShowOrderTotalMoney.setText("订单金额：" + "￥" + mPayTotalMoney + "（含"
				+ mPayPostMoney + "元配送费）");
		mPayOrderCheapMsg.setText("优惠信息：" + mPayCheapInfo);
		mPayWayCheckBox = (CheckBox) this
				.findViewById(R.id.paymoney_isUseZhifuBao);
		mPayWayCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							mPayWay = "2";
						}

					}
				});
		mGiveUpPay = (Button) this.findViewById(R.id.paymoney_backToParentIv);
		mGiveUpPay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.paymoney_money:
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					checkPayData();
				}
			}).start();

			break;
		case R.id.paymoney_backToParentIv:
			exitDialog();
			break;
		default:
			break;
		}
	}

	// 捕获返回键的方法1
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下BACK，同时没有重复
			exitDialog();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void exitDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.tishi)
				.setTitle("温馨提示")
				.setMessage("放弃支付？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						finish();
						Intent intent = new Intent(getApplicationContext(),
								CheckNewOrderActivity.class);
						startActivity(intent);
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

	private void checkPayData() {
		// TODO Auto-generated method stub
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder()
				.add("orderno", mPayOrderID).add("paymodel", mPayWay)
				.add("total", mPayTotalMoney + "").build();
		Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.CHECK_PAY).post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String payConfirmBody = response.body().string();
				LogUtils.e("支付确认的Body---？？？", payConfirmBody);
				Gson gson = new Gson();
				mPayEntity = gson.fromJson(payConfirmBody,
						MyOrderResultEntity.class);
				Message msg = handler.obtainMessage(PAY_CONFIRM);
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

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressLint("ResourceAsColor")
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LEFT_TIME:
				mPayLeftTime--;
				mPayLeftTimeTv.setText("" + mPayLeftTime);

				if (mPayLeftTime > 0) {
					Message message = handler.obtainMessage(LEFT_TIME);
					handler.sendMessageDelayed(message, 1000); // send message
				} else {
					mPayButton.setClickable(false);
					mPayButton.setBackgroundColor(R.color.gray);
					mPayButton.setText("支付超时");
				}
				break;
			case SDK_PAY_FLAG:
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {

					showToastLong("支付成功");
					finish();
					Intent intent2Ok = new Intent(getApplicationContext(),
							MainPageActivity.class);
					startActivity(intent2Ok);

				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						showToastLong("支付结果确认中");
					} else if (TextUtils.equals(resultStatus, "4000")) {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						showToastLong("支付失败");
					} else if (TextUtils.equals(resultStatus, "5000")) {
						showToastLong("重复请求");

					} else if (TextUtils.equals(resultStatus, "6001")) {
						showToastLong("用户中途取消");

					} else if (TextUtils.equals(resultStatus, "6002")) {
						showToastLong("网络连接出错");

					} else if (TextUtils.equals(resultStatus, "6004")) {
						showToastLong("支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态");

					} else {
						showToastLong("其它支付错误");
					}
				}
				break;

			case PAY_CONFIRM:
				if (mPayEntity.getSuccess().equals(true)) {
					orderInfo = mPayEntity.getMsg();
					testPayMoney(orderInfo);
				} else {
					showToastLong(mPayEntity.getMsg());
				}
				break;
			default:
				break;
			}
		}

	};

	protected void testPayMoney(final String orderInfo2) {
		// TODO Auto-generated method stub
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(MyOrderPayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(orderInfo2, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

}
