package com.ings.gogo.activity;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.DetailScoreEntity;
import com.ings.gogo.entity.OrderStateEntity;
import com.ings.gogo.utils.LogUtils;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalActivity extends BaseActivity implements OnClickListener {
	private final int GET_STATE_OK = 1;
	// ��ʾ�û�����Ϣ
	private TextView mUserNameTv;
	// ƴ���ַ�����sb
	private StringBuilder sb;
	// �������洫�����ĵ绰����
	private String phoneNumber;
	// ͷ��
	private ImageView mPersonHeadIV;
	// ���˻���
	private TextView mPersonalScoreTv;
	// һ������Ϊ ȫ�ֲ���
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	// ϵͳ����
	private TextView mPersonSystemSeting;
	// �鿴����
	private TextView mPersonCheckMyOrder;
	// ��ַ����
	private TextView mPersonPlaceManage;
	// ������֧����������
	private RelativeLayout mWaitPayRL;
	// ���������Ͷ�������
	private RelativeLayout mWaitGoRL;
	// ��ת���Ѿ����Ͷ�������
	private RelativeLayout mHadGoRL;
	// ���������۶�������
	private RelativeLayout mWaitMarkRL;
	// ����״̬��ʵ��bean
	private OrderStateEntity stateEntity;
	// ��֧���Ķ�������
	private TextView mWaitPayNum;
	// �����͵Ķ�������
	private TextView mWaitGoNum;
	// �����͵�����
	private TextView mHadGoNum;
	// �����۵�����
	private TextView mWaitMarkNum;
	private TextView mPersonCheckUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_personal_myorder);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		LogUtils.e("���˽����cookie��Ϣ---->>", ASP_NET_SessionId + "   "
				+ aspnetauth);
		Bundle bundle = this.getIntent().getExtras();
		phoneNumber = bundle.getString("telNum");
		if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length() > 6) {
			sb = new StringBuilder();
			for (int i = 0; i < phoneNumber.length(); i++) {
				char c = phoneNumber.charAt(i);
				if (i >= 3 && i <= 7) {
					sb.append('*');
				} else {
					sb.append(c);
				}
			}

		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getStateData();
			}
		}).start();

		inntAllViews();
	}

	private void inntAllViews() {
		// TODO Auto-generated method stub
		mUserNameTv = (TextView) this.findViewById(R.id.foodDescribUseNameTv);
		mUserNameTv.setText(sb.toString());
		mPersonHeadIV = (ImageView) this.findViewById(R.id.person_headImg);
		mPersonalScoreTv = (TextView) this
				.findViewById(R.id.mPersonActivity_myScore);
		mPersonalScoreTv.setOnClickListener(this);
		mPersonSystemSeting = (TextView) this
				.findViewById(R.id.mPersonActivity_mySystemSeting);
		mPersonSystemSeting.setOnClickListener(this);
		mPersonCheckMyOrder = (TextView) this
				.findViewById(R.id.mPersonal_myOrder);
		mPersonCheckMyOrder.setOnClickListener(this);
		mPersonPlaceManage = (TextView) this
				.findViewById(R.id.mPersonActivity_myAddressManage);
		mPersonPlaceManage.setOnClickListener(this);
		mWaitPayRL = (RelativeLayout) this.findViewById(R.id.mPerson_waitPayRe);
		mWaitPayRL.setOnClickListener(this);
		mWaitGoRL = (RelativeLayout) this.findViewById(R.id.mPerson_waitGoRe);
		mWaitGoRL.setOnClickListener(this);
		mHadGoRL = (RelativeLayout) this.findViewById(R.id.mPerson_hadGoRe);
		mHadGoRL.setOnClickListener(this);
		mWaitMarkRL = (RelativeLayout) this
				.findViewById(R.id.mPerson_hadComplicRe);
		mWaitMarkRL.setOnClickListener(this);
		mWaitPayNum = (TextView) this.findViewById(R.id.waitPayNum);
		mWaitGoNum = (TextView) this.findViewById(R.id.waitgoNum);
		mHadGoNum = (TextView) this.findViewById(R.id.hadgoNum);
		mWaitMarkNum = (TextView) this.findViewById(R.id.hadcomplicNum);
		mPersonCheckUpdate = (TextView) this
				.findViewById(R.id.mPersonActivity_myRefresh);
		mPersonCheckUpdate.setOnClickListener(this);
	}

	public void invisibleOnScreen() {
		LogUtils.e("���˽����Ƿ�ÿ�ζ�ִ�е���", "���˽����Ƿ�ÿ�ζ�ִ�е���");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getStateData();
			}
		}).start();
	}

	private void getStateData() {
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// ����һ��Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.GET_ORDER_STATE).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// ����������
		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response response) throws IOException {
				String stateBody = response.body().string();
				LogUtils.e("���˽����״ֵ̬--->>", stateBody);
				Gson gson = new Gson();
				stateEntity = gson.fromJson(stateBody, OrderStateEntity.class);
				Message msg = handler.obtainMessage(GET_STATE_OK);
				msg.sendToTarget();

			}

			@Override
			public void onFailure(Request request, IOException e) {
			}

		});
	}

	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_STATE_OK:
				// ������
				if (stateEntity.getData().get(0).getDfk().equals("0")) {
					mWaitPayNum.setVisibility(View.GONE);
				} else {
					mWaitPayNum.setText(stateEntity.getData().get(0).getDfk());
				}
				// ������
				if (stateEntity.getData().get(0).getDps().equals("0")) {
					mWaitGoNum.setVisibility(View.GONE);
				} else {
					mWaitGoNum.setText(stateEntity.getData().get(0).getDps());
				}
				// ���ͳ�
				if (stateEntity.getData().get(0).getYsc().equals("0")) {
					mHadGoNum.setVisibility(View.GONE);
				} else {
					mHadGoNum.setText(stateEntity.getData().get(0).getYsc());
				}
				// ������
				if (stateEntity.getData().get(0).getDpl().equals("0")) {
					mWaitMarkNum.setVisibility(View.GONE);
				} else {
					mWaitMarkNum.setText(stateEntity.getData().get(0).getDpl());
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mPersonActivity_myScore:
			Intent intent2Score = new Intent(getApplicationContext(),
					ScoreDetaiActivity.class);
			startActivity(intent2Score);

			break;
		case R.id.mPersonActivity_mySystemSeting:
			Intent intent2Exit = new Intent(getApplicationContext(),
					PersonalExitActivity.class);
			startActivity(intent2Exit);
			break;
		case R.id.mPersonal_myOrder:
			Intent intent = new Intent(getApplicationContext(),
					CheckMyOrderActivity.class);
			startActivity(intent);
			break;
		case R.id.mPersonActivity_myAddressManage:
			Intent intent2ManagePlace = new Intent(getApplicationContext(),
					PlaceManageActivity.class);
			startActivity(intent2ManagePlace);
			break;
		case R.id.mPerson_waitPayRe:
			Intent intent2WaitPay = new Intent(getApplicationContext(),
					CheckWaitPayActivity.class);
			startActivity(intent2WaitPay);
			break;
		case R.id.mPerson_waitGoRe:
			Intent intent2WaitGo = new Intent(getApplicationContext(),
					CheckWaitGoActivity.class);
			startActivity(intent2WaitGo);
			break;
		case R.id.mPerson_hadGoRe:
			Intent intent2HadGo = new Intent(getApplicationContext(),
					CheckHadGoActivity.class);
			startActivity(intent2HadGo);
			break;
		case R.id.mPerson_hadComplicRe:
			Intent intent2Complic = new Intent(getApplicationContext(),
					CheckWaitMarkActivity.class);
			startActivity(intent2Complic);
			break;
		case R.id.mPersonActivity_myRefresh:
			Intent intent2CheckUpdate = new Intent(getApplicationContext(),
					CheckUpdateActivity.class);
			startActivity(intent2CheckUpdate);
			break;

		default:
			break;
		}
	}

	// ˫���˳�
	// // ˫���˳� ��һ�ε����ʱ������Ϊ0
	private long firstTime = 0;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();
				firstTime = System.currentTimeMillis();
				return true;
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				System.exit(0);
				finish();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

}
