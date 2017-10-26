package com.ings.gogo.activity;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.AddressAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.AddressEntity;
import com.ings.gogo.entity.AddressTagEntity;
import com.ings.gogo.utils.JudgeTelNum;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.utils.ToastUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class EditPlaceActivity extends BaseActivity implements OnClickListener,
		OnGeocodeSearchListener {
	private final int DELETE_DATAOK = 3;
	private final int RESULT = 102;
	private final int REQUEST = 2;
	private final int SAVE_OK = 1;
	private ImageView mAddBack;
	private TextView mAddSaveMsg;
	private EditText mAddNameEdt;
	private EditText mAddTelNumEdt;
	private CheckBox mAddManCB;
	private CheckBox mAddWomanCB;
	private TextView mAddSelectPlaceTv;
	private EditText mAddDoorNumEdt;
	private UILApplication myApplication;
	private String ASP_NET_SessionId;
	private String aspnetauth;
	private String telNum;
	private JSONObject jobj;
	private AddressTagEntity entity;
	private String mEditName;
	private String mEditPhoneNum;
	private String mEditPlaceName;
	private String mEditPlaceId;
	private String mEditSex;
	private TextView mDeletePlace;
	// 纬度
	private String Latitude;
	// 经度
	private String Longitude;
	private GeocodeAddress address;
	private String addressName;
	private GeocodeSearch geocoderSearch;
	private GeocodeQuery mGeocodeQuery;
	private String sex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_editplace);
		myApplication = (UILApplication) getApplication();
		ASP_NET_SessionId = myApplication.getASP_NET_SessionId();
		aspnetauth = myApplication.getAspnetauth();
		telNum = myApplication.getTelNum();
		Bundle bundle = this.getIntent().getExtras();
		mEditName = bundle.getString("EditName");
		mEditPhoneNum = bundle.getString("EditPhoneNum");
		mEditPlaceName = bundle.getString("EditPlaceName");
		mEditPlaceId = bundle.getString("EditPlaceId");
		mEditSex = bundle.getString("EditSex");
		Latitude = bundle.getString("WeiDu");
		Longitude = bundle.getString("JinDu");
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		innitViews();
	}

	private void innitViews() {
		// TODO Auto-generated method stub
		mAddBack = (ImageView) this.findViewById(R.id.addplace_back);
		mAddBack.setOnClickListener(this);
		mAddSaveMsg = (TextView) this.findViewById(R.id.addplace_saveplace);
		mAddSaveMsg.setOnClickListener(this);
		mAddNameEdt = (EditText) this.findViewById(R.id.addplace_nameEdt);
		mAddNameEdt.setText(mEditName);
		mAddTelNumEdt = (EditText) this.findViewById(R.id.addplace_phoneEdt);
		mAddTelNumEdt.setText(mEditPhoneNum);
		mAddManCB = (CheckBox) this.findViewById(R.id.addplace_manCb);
		mAddManCB
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							sex = "先生";
							mAddWomanCB.setClickable(false);
						} else {
							sex = "女士";
						}
					}
				});
		mAddWomanCB = (CheckBox) this.findViewById(R.id.addplace_womanCb);
		mAddWomanCB
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							sex = "女士";
							mAddManCB.setClickable(false);
						} else {
							sex = "先生";
						}
					}
				});
		mAddSelectPlaceTv = (TextView) this
				.findViewById(R.id.addplace_selecPlaceTv);
		mAddSelectPlaceTv.setText(mEditPlaceName);
		mAddSelectPlaceTv.setOnClickListener(this);
		mAddDoorNumEdt = (EditText) this.findViewById(R.id.addplace_doornumEdt);
		mDeletePlace = (TextView) this.findViewById(R.id.delete_placeMsg);
		mDeletePlace.setOnClickListener(this);

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SAVE_OK:
				if (entity.getSuccess().equals(true)) {
					showToastLong(entity.getMsg());
					finish();
				} else {
					showToastLong(entity.getMsg());
				}

				break;
			case DELETE_DATAOK:
				if (entity.getSuccess().equals(true)) {
					showToastLong(entity.getMsg());
					finish();
				} else {
					showToastLong(entity.getMsg());
				}
				break;

			default:
				break;
			}
		};
	};

	public void savePlaceMsg() {
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder().add("Stream",
				jobj.toString()).build();
		LogUtils.e("编辑地址拼接的字符---》》》", jobj.toString().trim());
		Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.ADD_ADDRESS_DATA).post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String addPlaceBody = response.body().string();
				LogUtils.e("编辑地址的Body---？？？", addPlaceBody);
				Gson gson = new Gson();
				entity = gson.fromJson(addPlaceBody, AddressTagEntity.class);
				Message msg = handler.obtainMessage(SAVE_OK);
				msg.sendToTarget();
			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.addplace_saveplace:
			if (!nameIsOk()) {
				return;
			}
			if (!sexIsOk()) {
				return;
			}
			if (!telNumIsOk()) {
				return;
			}
			if (!placeIsOk()) {
				return;
			}
			if (JudgeTelNum.isTelNum(mAddTelNumEdt.getText().toString().trim()) == true) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						jobj = new JSONObject();
						try {
							jobj.put("consigneeid", mEditPlaceId);

							jobj.put("sex", "");

							jobj.put("consignee_add", mAddSelectPlaceTv
									.getText().toString().trim());
							jobj.put("consignee_name", mAddNameEdt.getText()
									.toString().trim());
							jobj.put("consignee_phone", mAddTelNumEdt.getText()
									.toString().trim());
							jobj.put("gpsy", Latitude);
							jobj.put("gpsx", Longitude);
							jobj.put("consignee_no", mAddDoorNumEdt.getText()
									.toString().trim());

						} catch (JSONException e) {

							return;
						}
						savePlaceMsg();
					}
				}).start();
			} else {
				showToastLong(R.string.telnumerrorremian);
			}

			break;
		case R.id.addplace_back:

			finish();

			break;
		case R.id.addplace_selecPlaceTv:
			Intent intent2Map = new Intent(getApplicationContext(),
					SelectByMapActivity.class);
			startActivityForResult(intent2Map, REQUEST);

			break;
		case R.id.delete_placeMsg:
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					deletePlace();
				}
			}).start();
			break;
		default:
			break;
		}
	}

	// 根据地址转化为经纬度信息
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				address = result.getGeocodeAddressList().get(0);
				addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
						+ address.getFormatAddress();
				Latitude = address.getLatLonPoint().getLatitude() + "";
				Longitude = address.getLatLonPoint().getLongitude() + "";
				//ToastUtil.show(EditPlaceActivity.this, addressName);
				LogUtils.e("添加地址中的经纬度--》》", addressName);

			} else {
				ToastUtil.show(EditPlaceActivity.this, R.string.no_result);

			}
		} else if (rCode == 27) {
			ToastUtil.show(EditPlaceActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(EditPlaceActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(EditPlaceActivity.this,
					getString(R.string.error_other) + rCode);
		}
	}

	/**
	 * 将经纬度转化为地址值，需传递经纬度
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addressName = result.getRegeocodeAddress().getFormatAddress()
						+ "附近";
				ToastUtil.show(EditPlaceActivity.this, addressName);
				Log.e("附近的值是：：：", addressName);
			} else {
				ToastUtil.show(EditPlaceActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(EditPlaceActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(EditPlaceActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(EditPlaceActivity.this,
					getString(R.string.error_other) + rCode);
		}
	}

	protected void deletePlace() {
		// 创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder()
				.addHeader("Cookie", aspnetauth + ";" + ASP_NET_SessionId)
				.url(BaseData.DELETE_ADDRESS_DATA + mEditPlaceId).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String deleteBody = response.body().string();
				LogUtils.e("删除地址--->>", deleteBody);
				Gson gson = new Gson();
				entity = gson.fromJson(deleteBody, AddressTagEntity.class);
				Message msg = handler.obtainMessage(DELETE_DATAOK);
				msg.sendToTarget();

			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST) {
			if (resultCode == RESULT) {
				final String result = data.getStringExtra("keyPlaceWord");
				LogUtils.e("修改地址的返回值result--->>>AddPlace", result);
				mAddSelectPlaceTv.setText(result);
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getLatlon(result);
					}
				}).start();

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void getLatlon(final String name) {
		mGeocodeQuery = new GeocodeQuery(name, "贵阳");//
		// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(mGeocodeQuery);// 设置同步地理编码请求
	}

	private boolean nameIsOk() {

		if (TextUtils.isEmpty(mAddNameEdt.getText())) {
			showToastShort(R.string.namenotnull);
			return false;
		}

		return true;
	}

	private boolean sexIsOk() {

		if (TextUtils.isEmpty(sex)) {
			showToastShort(R.string.select_sex);
			return false;
		}

		return true;
	}

	private boolean telNumIsOk() {

		if (TextUtils.isEmpty(mAddTelNumEdt.getText())) {
			showToastShort(R.string.telnumnotnull);
			return false;
		}

		return true;
	}

	private boolean placeIsOk() {

		if (TextUtils.isEmpty(mAddSelectPlaceTv.getText())) {
			showToastShort(R.string.placenotnull);
			return false;
		}

		return true;
	}

}
