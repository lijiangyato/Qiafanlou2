package com.ings.gogo.activity;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.MyOrderResultEntity;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.utils.PayResult;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class SelectPayWayActivity extends BaseActivity implements
		OnClickListener {
	private final int PAY_CONFIRM = 1;
	private final int SDK_PAY_FLAG = 2;
	private ImageView mPayWayBack;
	private CheckBox mPayByZhiFuBao;
	private String mOrderID;
	private String mTotalMoney;
	private Button mCheckPay;
	private String mPayWay = "2";
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	private MyOrderResultEntity mPayEntity;
	private String orderInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_selectpayway);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		Bundle bundle = this.getIntent().getExtras();
		mOrderID = bundle.getString("OrderID");
		mTotalMoney = bundle.getString("PayMoney");
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		mPayWayBack = (ImageView) this
				.findViewById(R.id.selectpay_backToParentIv);
		mPayWayBack.setOnClickListener(this);
		mPayByZhiFuBao = (CheckBox) this
				.findViewById(R.id.selectpay_isUseZhifuBao);
		mPayByZhiFuBao
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							mPayWay = "2";
						} else {
							mPayWay = "2";
						}
					}
				});
		mCheckPay = (Button) this.findViewById(R.id.selectpay_paymoney);
		mCheckPay.setText("确认支付¥" + mTotalMoney);
		mCheckPay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.selectpay_backToParentIv:
			finish();

			break;
		case R.id.selectpay_paymoney:
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					checkPayData();
				}
			}).start();

			break;

		default:
			break;
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PAY_CONFIRM:
				if (mPayEntity.getSuccess().equals(true)) {
					orderInfo = mPayEntity.getMsg();
					testPayMoney(orderInfo);
				} else {
					showToastLong(mPayEntity.getMsg());
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
					Intent intent2Ok = new Intent(getApplicationContext(),
							MainPageActivity.class);
					startActivity(intent2Ok);
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {

						Toast.makeText(SelectPayWayActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else if (TextUtils.equals(resultStatus, "4000")) {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(SelectPayWayActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					} else if (TextUtils.equals(resultStatus, "5000")) {

					} else if (TextUtils.equals(resultStatus, "6001")) {

					} else if (TextUtils.equals(resultStatus, "6002")) {

					} else if (TextUtils.equals(resultStatus, "6004")) {

					}
				}
				break;

			default:
				break;
			}
		};
	};

	protected void checkPayData() {
		// TODO Auto-generated method stub
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder()
				.add("orderno", mOrderID).add("paymodel", mPayWay)
				.add("total", mTotalMoney + "").build();
		Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.CHECK_PAY).post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String payConfirmBody = response.body().string();
				LogUtils.e("个人中心支付确认的Body---？？？", payConfirmBody);
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

	protected void testPayMoney(final String orderInfo2) {
		// TODO Auto-generated method stub
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(SelectPayWayActivity.this);
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
