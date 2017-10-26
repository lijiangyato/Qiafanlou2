package com.ings.gogo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.LocationAdapter;
import com.ings.gogo.adapter.LocationTipsAdapter;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.utils.ToastUtil;

public class SelectPlaceActivity extends BaseActivity implements
		OnClickListener, TextWatcher, InputtipsListener, OnPoiSearchListener {
	private final int RESULT_CODE = 200;
	// 声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	// 定位参数配置
	private AMapLocationClientOption mLocationOption;
	// 纬度
	private double Latitude;
	// 经度
	private double Longitude;
	private LatLng locationLatLng;
	private String addressName;
	private TextView mBackToParentBt;
	private AutoCompleteTextView mSelectPlaceAT;
	private Button mSelectAsLocationBt;
	private Button mNearUnuseBt;
	private ListView mSuggesionPlaceLV;
	private StringBuffer buffer;
	private String desc;
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private LatLonPoint latLonPoint;
	private PoiSearch.SearchBound searchBound;
	private GeocodeQuery mGeocodeQuery;
	private GeocodeSearch geocoderSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.selectplaceactivity_layout);
		initLocation();// 初始化定位配置
		innitAllViews();
		// doSearchQuery();
	}

	private void innitAllViews() {
		// TODO Auto-generated method stub
		mBackToParentBt = (TextView) this.findViewById(R.id.backToParentBt);
		mBackToParentBt.setOnClickListener(this);
		mNearUnuseBt = (Button) this.findViewById(R.id.nearByUnuse);
		mSelectPlaceAT = (AutoCompleteTextView) this
				.findViewById(R.id.mSelectActvityPlaceTv);
		mSelectPlaceAT.addTextChangedListener(this);// 添加文本输入框监听事件
		mSelectAsLocationBt = (Button) this
				.findViewById(R.id.setAsTheCurerentPlaceBt);
		mSelectAsLocationBt.setOnClickListener(this);
		mSuggesionPlaceLV = (ListView) this
				.findViewById(R.id.selectActivityNearByPleaceLV);
	}

	private void initLocation() {
		// TODO Auto-generated method stub
		// 初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		// 设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);

		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(true);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		// 给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		// 启动定位
		mLocationClient.startLocation();
	}

	// 声明定位回调监听器
	public AMapLocationListener mLocationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) {
					// 定位成功回调信息，设置相关消息
					Latitude = amapLocation.getLatitude();// 获取纬度
					Longitude = amapLocation.getLongitude();// 获取经度
					locationLatLng = new LatLng(Latitude, Longitude);
					searchMove(locationLatLng);
					buffer = new StringBuffer();
					buffer.append(amapLocation.getCity() + " "
							+ amapLocation.getDistrict() + " "
							+ amapLocation.getStreet() + " "
							+ amapLocation.getStreetNum());
					desc = "";
					Bundle locBundle = amapLocation.getExtras();
					if (locBundle != null) {
						String tem = locBundle.getString("desc");
						desc = tem.replace("贵州省 贵阳市", "");
					}
					LogUtils.e("desc", desc);
					LogUtils.e("buffer", buffer.toString());

				} else {
					// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
					Log.e("AmapError", "location Error, ErrCode:"
							+ amapLocation.getErrorCode() + ", errInfo:"
							+ amapLocation.getErrorInfo());
				}
			}
		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setAsTheCurerentPlaceBt:
			if (desc == null) {
				showToastLong("定位失败！");
			} else {
				mSelectAsLocationBt.setText(desc);
				Intent intent = new Intent();
				intent.putExtra("keyPlaceWord", desc);
				intent.putExtra("weidu", Latitude);
				intent.putExtra("jingdu", Longitude);
				LogUtils.e("返回的定位的数据", desc);
				setResult(RESULT_CODE, intent);
				finish();
			}

			break;
		case R.id.backToParentBt:
			this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 搜索操作
	 */
	private void doSearchQuery() {
		query = new PoiSearch.Query(mSelectPlaceAT.getText().toString(),
				"地名地址信息|交通设施服务|政府机构及社会团体|地名地址信息|商务住宅|住宿服务", "贵阳");
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(1);// 设置查第一页
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);// 设置回调数据的监听器
		// 点附近2000米内的搜索结果
		if (latLonPoint != null) {
			searchBound = new PoiSearch.SearchBound(latLonPoint, 1000);
			poiSearch.setBound(searchBound);
			poiSearch.setBound(new SearchBound(latLonPoint, 1000));
		}

		poiSearch.searchPOIAsyn();// 开始搜索
	}

	/**
	 * 搜索操作
	 */
	private void searchMove(LatLng locationLatLng) {
		query = new PoiSearch.Query(mSelectPlaceAT.getText().toString(),
				"地名地址信息|交通设施服务|政府机构及社会团体|地名地址信息|商务住宅|住宿服务|购物服务", "贵阳");
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(1);// 设置查第一页
		PoiSearch poiSearch = new PoiSearch(this, query);
		// 如果不为空值
		if (Latitude != 0.0 && Longitude != 0.0) {
			poiSearch.setBound(new SearchBound(new LatLonPoint(Latitude,
					Longitude), 1000));// 设置周边搜索的中心点以及区域
			poiSearch.setOnPoiSearchListener(this);// 设置数据返回的监听器
			poiSearch.searchPOIAsyn();// 开始搜索

		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		String newText = s.toString().trim();
		Log.e("next-->>>", newText);
		InputtipsQuery inputquery = new InputtipsQuery(newText, "贵阳");
		Inputtips inputTips = new Inputtips(SelectPlaceActivity.this,
				inputquery);
		inputTips.setInputtipsListener(this);
		inputTips.requestInputtipsAsyn();
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		// getLatlon(mSelectPlaceAT.getText().toString());
	}

	// 输入提示 并将数据设置到listView上面
	@Override
	public void onGetInputtips(final List<Tip> tipList, int rCode) {
		mSelectAsLocationBt.setVisibility(View.GONE);
		mNearUnuseBt.setVisibility(View.GONE);
		if (rCode == 0) {// 正确返回
			List<String> listString = new ArrayList<String>();
			for (int i = 0; i < tipList.size(); i++) {
				listString.add(tipList.get(i).getName());
				LogUtils.e("tipsList-->>name", tipList.get(i).getName());
				LogUtils.e("tipsList-->>", tipList.get(i) + "");
			}
			LogUtils.e("tipsList-->>Size", tipList.size() + "");

			/**
			 * 改变提示字符的颜色可以直接到item_input_hint_layout布局中设置textColor属性
			 */
			LocationTipsAdapter adapter = new LocationTipsAdapter(
					getApplicationContext(), tipList);
			mSuggesionPlaceLV.setAdapter(adapter);
			mSuggesionPlaceLV.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					// mSelectPlaceAT.setText(tipList.get(arg2).getName());
					String tem = tipList.get(arg2).getDistrict();
					String street = tem.replace("贵州省", "");
					LogUtils.e("返回的输入提示的数据", street
							+ tipList.get(arg2).getName());
					Intent intent = new Intent();
					intent.putExtra("keyPlaceWord", street
							+ tipList.get(arg2).getName());
					intent.putExtra("weidu", tipList.get(arg2).getPoint()
							.getLatitude());
					intent.putExtra("jingdu", tipList.get(arg2).getPoint()
							.getLongitude());
					setResult(RESULT_CODE, intent);
					finish();
				}
			});
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	// poi检索的会掉 方法 将获取的数据设置到listView上面
	@Override
	public void onPoiSearched(final PoiResult result, int code) {
		// TODO Auto-generated method stub
		LocationAdapter loacationAdapter = new LocationAdapter(
				getApplicationContext(), result.getPois());
		mSuggesionPlaceLV.setAdapter(loacationAdapter);
		mSuggesionPlaceLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				LogUtils.e("返回的推荐的值--》》》", result.getPois().get(position)
						+ result.getPois().get(position).getSnippet());
				Intent intent = new Intent();
				intent.putExtra("keyPlaceWord", result.getPois().get(position)
						+ result.getPois().get(position).getSnippet());
				intent.putExtra("weidu", result.getPois().get(position)
						.getLatLonPoint().getLatitude());
				intent.putExtra("jingdu", result.getPois().get(position)
						.getLatLonPoint().getLongitude());
				setResult(RESULT_CODE, intent);
				finish();
			}
		});
		loacationAdapter.notifyDataSetChanged();
	}

}
