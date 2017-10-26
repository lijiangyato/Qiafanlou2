package com.ings.gogo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
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

public class SelectByMapActivity extends BaseActivity implements
		LocationSource, AMapLocationListener, OnMarkerClickListener,
		InfoWindowAdapter, TextWatcher, OnPoiSearchListener, InputtipsListener,
		OnGeocodeSearchListener {
	private final int LOAD_DATA = 1;
	private final int RESULT = 102;
	// AMap是地图对象
	private AMap aMap;
	private MapView mapView;
	// 声明AMapLocationClient类对象，定位发起端
	private AMapLocationClient mLocationClient = null;
	// 声明mLocationOption对象，定位参数
	public AMapLocationClientOption mLocationOption = null;
	// 声明mListener对象，定位监听器
	private OnLocationChangedListener mListener = null;
	// 标识，用于判断是否只显示一次定位信息和用户重新定位
	private boolean isFirstLoc = true;
	private AutoCompleteTextView mInputKeyWord;// 输入搜索关键字
	private String keyWord = "";// 要输入的poi搜索关键字
	private PoiResult poiResult; // poi返回的结果
	// private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	// 纬度
	private double Latitude;
	// 经度
	private double Longitude;
	// 移动的纬度
	private double LatitudeMove;
	// 移动的经度
	private double LongitudeMove;
	private LatLng locationLatLng;
	private String desc;
	private ListView mSearchResultLv;
	private Marker locationMarker;
	private LocationAdapter mLocationAdapter;
	private GeocodeSearch geocoderSearch;
	private String addressName;
	private GeocodeQuery mGeocodeQuery;
	private StringBuffer buffer;
	private ProgressDialog progDialog = null;// 搜索时进度条
	// private LatLonPoint latLonPoint;
	private PoiSearch.SearchBound searchBound;
	private AMapLocation aMapLocation;
	private ImageView mMapBackToParent;
	private TextView mNowPlaceTv;
	// 屏幕的高度
	private int height;
	private LinearLayout mMapLL;
	private GeocodeAddress address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.selectbymaplayout);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		height = dm.heightPixels;
		mapView = (MapView) findViewById(R.id.map);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mapView
				.getLayoutParams();
		// 获取当前控件的布局对象
		params.height = height / 3;// 设置当前控件布局的高度
		mapView.setLayoutParams(params);// 将设置好的布局参数应用到控件中
		mapView.onCreate(savedInstanceState);
		initViews();
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnCameraChangeListener(cameraChangeListener);
			// 设置显示定位按钮 并且可以点击1
			UiSettings settings = aMap.getUiSettings();
			aMap.setLocationSource(this);// 设置了定位的监听
			// 是否显示定位按钮
			settings.setMyLocationButtonEnabled(true);
			aMap.setMyLocationEnabled(true);// 显示定位层并且可以触发定位,默认是flase
		}
		// 开始定位
		location();

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		}
	};

	private void initViews() {
		// TODO Auto-generated method stub
		mInputKeyWord = (AutoCompleteTextView) findViewById(R.id.auto_txt_key_word);
		mInputKeyWord.addTextChangedListener(this);// 添加文本输入框监听事件
		mMapBackToParent = (ImageView) this.findViewById(R.id.map_Back);
		mMapBackToParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mNowPlaceTv = (TextView) this.findViewById(R.id.map_NowPlaceNameTv);
		mNowPlaceTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("keyPlaceWord", desc);
				intent.putExtra("weidu", Latitude);
				intent.putExtra("jingdu", Longitude);
				setResult(RESULT, intent);
				finish();
			}
		});
		mMapLL = (LinearLayout) this.findViewById(R.id.map_nowPlaceLL);
		mSearchResultLv = (ListView) this.findViewById(R.id.result_listview);
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);

	}

	public void getLatlon(final String name) {
		mGeocodeQuery = new GeocodeQuery(name, "贵阳");//
		// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(mGeocodeQuery);// 设置同步地理编码请求
	}

	private void location() {
		// 初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		// 设置定位回调监听
		mLocationClient.setLocationListener(this);
		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption
				.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
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

	// 移动我地图定位
	OnCameraChangeListener cameraChangeListener = new OnCameraChangeListener() {

		@Override
		public void onCameraChangeFinish(final CameraPosition position) {
			if (locationMarker != null) {

				final LatLng latLng = position.target;
				new Thread(new Runnable() {

					@Override
					public void run() {
						LatLonPoint point = new LatLonPoint(latLng.latitude,
								latLng.longitude);
						LatitudeMove = latLng.latitude;
						LongitudeMove = latLng.longitude;
						locationLatLng = new LatLng(LatitudeMove, LongitudeMove);
						searchMove(point);
						RegeocodeQuery regeocodeQuery = new RegeocodeQuery(
								point, 1000, GeocodeSearch.AMAP);
						RegeocodeAddress address = null;
						try {
							address = geocoderSearch
									.getFromLocation(regeocodeQuery);
						} catch (AMapException e) {
							e.printStackTrace();
						}
						if (null == address) {
							return;
						}
						StringBuffer stringBuffer = new StringBuffer();
						// String area = address.getProvince();// 省或直辖市
						// String loc = address.getCity();// 地级市或直辖市
						String subLoc = address.getDistrict();// 区或县或县级市
						// String ts = address.getTownship();// 乡镇
						String thf = null;// 道路
						List<RegeocodeRoad> regeocodeRoads = address.getRoads();// 道路列表
						if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
							RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
							if (regeocodeRoad != null) {
								thf = regeocodeRoad.getName();
							}
						}
						String subthf = null;// 门牌号
						StreetNumber streetNumber = address.getStreetNumber();
						if (streetNumber != null) {
							subthf = streetNumber.getNumber();
						}
						String fn = address.getBuilding();// 标志性建筑,当道路为null时显示
						// if (area != null)
						// stringBuffer.append(area);
						// if (loc != null && !area.equals(loc))
						// stringBuffer.append(loc);
						if (subLoc != null)
							stringBuffer.append(subLoc);
						// if (ts != null)
						// stringBuffer.append(ts);
						if (thf != null)
							stringBuffer.append(thf);
						if (subthf != null)
							stringBuffer.append(subthf);
						if ((thf == null && subthf == null) && fn != null
								&& !subLoc.equals(fn))
							stringBuffer.append(fn + "附近");
						LogUtils.e("去去去", stringBuffer.toString());
						// 是否显示定位信息
						// locationMarker.setSnippet(stringBuffer.toString());
						handler.post(new Runnable() {

							@Override
							public void run() {
								locationMarker.showInfoWindow();
							}
						});
					}
				}).start();

			}
		}

		@Override
		public void onCameraChange(CameraPosition position) {
			if (locationMarker != null) {
				LatLng latLng = position.target;
				locationMarker.setPosition(latLng);
			}
		}

	};

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (aMapLocation != null) {
			if (aMapLocation.getErrorCode() == 0) {
				// 定位成功回调信息，设置相关消息

				aMapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见官方定位类型表
				// Latitude = aMapLocation.getLatitude();// 获取纬度
				// Longitude = aMapLocation.getLongitude();// 获取经度
				aMapLocation.getAccuracy();// 获取精度信息
				aMapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
				aMapLocation.getCountry();// 国家信息
				aMapLocation.getProvince();// 省信息
				aMapLocation.getCity();// 城市信息
				aMapLocation.getDistrict();// 城区信息
				aMapLocation.getStreet();// 街道信息
				aMapLocation.getStreetNum();// 街道门牌号信息
				aMapLocation.getCityCode();// 城市编码
				aMapLocation.getAdCode();// 地区编码

				// 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
				if (isFirstLoc) {
					// 设置缩放级别
					aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
					// 将地图移动到定位点
					aMap.moveCamera(CameraUpdateFactory
							.changeLatLng(new LatLng(
									aMapLocation.getLatitude(), aMapLocation
											.getLongitude())));
					// 点击定位按钮 能够将地图的中心移动到定位点
					mListener.onLocationChanged(aMapLocation);
					// 获取定位信息
					buffer = new StringBuffer();
					buffer.append(aMapLocation.getCity() + " "
							+ aMapLocation.getDistrict() + " "
							+ aMapLocation.getStreet() + " "
							+ aMapLocation.getStreetNum());

					Bundle locBundle = aMapLocation.getExtras();
					if (locBundle != null) {
						String tem = locBundle.getString("desc");
						desc = tem.replace("贵州省 贵阳市", "");
					}

					LogUtils.e("啊哈哈locBundle-->>>", desc);
					LogUtils.e("啊哈哈locbuffere-->>>", buffer.toString());
					mNowPlaceTv.setText(desc);
					addMarkerByString(locationLatLng, desc);
					locationMarker.showInfoWindow();
					isFirstLoc = false;
				}

			} else {
				// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				Log.e("AmapError",
						"location Error, ErrCode:"
								+ aMapLocation.getErrorCode() + ", errInfo:"
								+ aMapLocation.getErrorInfo());
				Toast.makeText(getApplicationContext(), "定位失败",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// 显示定位的信息
	private void addMarkerByString(LatLng latLng, String desc) {
		// search();
		doSearchQuery();
		MarkerOptions markerOptions = new MarkerOptions();
		locationMarker = aMap.addMarker(markerOptions);
	}

	/**
	 * 搜索操作
	 */
	private void searchMove(LatLonPoint latLonPoint) {
		query = new PoiSearch.Query(keyWord,
				"地名地址信息|交通设施服务|政府机构及社会团体|地名地址信息|商务住宅|住宿服务|购物服务", "贵阳");
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(1);// 设置查第一页
		PoiSearch poiSearch = new PoiSearch(this, query);
		// 如果不为空值
		if (LatitudeMove != 0.0 && LongitudeMove != 0.0) {
			poiSearch.setBound(new SearchBound(new LatLonPoint(LatitudeMove,
					LongitudeMove), 1000));// 设置周边搜索的中心点以及区域
			poiSearch.setOnPoiSearchListener(this);// 设置数据返回的监听器
			poiSearch.searchPOIAsyn();// 开始搜索

		}

	}

	private void search(LatLonPoint latLonPoint) {
		if (aMapLocation == null) {
			onLocationChanged(aMapLocation);
			aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
			// 将地图移动到定位点
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
					latLonPoint.getLatitude(), latLonPoint.getLongitude())));
			// 点击定位按钮 能够将地图的中心移动到定位点
			mListener.onLocationChanged(aMapLocation);
		} else {
			// 设置缩放级别
			aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
			// 将地图移动到定位点
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
					latLonPoint.getLatitude(), latLonPoint.getLongitude())));
			// 点击定位按钮 能够将地图的中心移动到定位点
			mListener.onLocationChanged(aMapLocation);
		}
		query = new PoiSearch.Query(keyWord,
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

	/**
	 * 搜索操作
	 */
	private void doSearchQuery() {
		query = new PoiSearch.Query(keyWord,
				"地名地址信息|交通设施服务|政府机构及社会团体|地名地址信息|商务住宅|住宿服务|购物服务", "贵阳");
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(1);// 设置查第一页
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);// 设置回调数据的监听器
		if (Latitude != 0.0 && Longitude != 0.0) {
			poiSearch.setBound(new SearchBound(new LatLonPoint(Latitude,
					Longitude), 1000));// 设置周边搜索的中心点以及区域
			poiSearch.setOnPoiSearchListener(this);// 设置数据返回的监听器
			poiSearch.searchPOIAsyn();// 开始搜索

		}
	}

	// 输入提示
	@Override
	public void onGetInputtips(final List<Tip> tipList, int rCode) {
		mapView.setVisibility(View.GONE);
		mMapLL.setVisibility(View.GONE);
		if (rCode == 0) {// 正确返回
			List<String> listString = new ArrayList<String>();
			for (int i = 0; i < tipList.size(); i++) {
				listString.add(tipList.get(i).getName());
			}

			LocationTipsAdapter adapter = new LocationTipsAdapter(
					getApplicationContext(), tipList);
			mSearchResultLv.setAdapter(adapter);
			mSearchResultLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					// mSelectPlaceAT.setText(tipList.get(arg2).getName());
					String tem = tipList.get(arg2).getDistrict();
					String street = tem.replace("贵州省贵阳市", "");
					Intent intent = new Intent();
					intent.putExtra("keyPlaceWord", street
							+ tipList.get(arg2).getName());
					intent.putExtra("weidu", tipList.get(arg2).getPoint()
							.getLatitude());
					intent.putExtra("jingdu", tipList.get(arg2).getPoint()
							.getLongitude());
					setResult(RESULT, intent);
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

	/**
	 * POI信息查询回调方法
	 */

	@Override
	public void onPoiSearched(final PoiResult result, int code) {
		mLocationAdapter = new LocationAdapter(getApplicationContext(),
				result.getPois());
		LogUtils.e("集合的大小---》》", mLocationAdapter.getCount() + "");
		mSearchResultLv.setAdapter(mLocationAdapter);
		mSearchResultLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("keyPlaceWord", result.getPois().get(position)
						+ result.getPois().get(position).getSnippet());
				intent.putExtra("weidu", result.getPois().get(position)
						.getLatLonPoint().getLatitude());
				intent.putExtra("jingdu", result.getPois().get(position)
						.getLatLonPoint().getLongitude());
				setResult(RESULT, intent);
				finish();
			}
		});

	}

	// 输入时 提示消息
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		String newText = s.toString().trim();
		InputtipsQuery inputquery = new InputtipsQuery(newText, "贵阳");
		Inputtips inputTips = new Inputtips(SelectByMapActivity.this,
				inputquery);
		inputTips.setInputtipsListener(this);
		inputTips.requestInputtipsAsyn();

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
				Latitude = address.getLatLonPoint().getLatitude();
				Longitude = address.getLatLonPoint().getLongitude();
				search(address.getLatLonPoint());
				ToastUtil.show(SelectByMapActivity.this, addressName);
				LogUtils.e("获得的经纬度是--》》", addressName);

			} else {
				ToastUtil.show(SelectByMapActivity.this, R.string.no_result);

			}
		} else if (rCode == 27) {
			ToastUtil.show(SelectByMapActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(SelectByMapActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(SelectByMapActivity.this,
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
				ToastUtil.show(SelectByMapActivity.this, addressName);
				Log.e("附近的值是：：：", addressName);
			} else {
				ToastUtil.show(SelectByMapActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(SelectByMapActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(SelectByMapActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(SelectByMapActivity.this,
					getString(R.string.error_other) + rCode);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		mLocationClient.stopLocation();// 停止定位
		mLocationClient.onDestroy();// 销毁定位客户端。
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	// 激活定位
	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		mListener = onLocationChangedListener;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	// 停止定位
	@Override
	public void deactivate() {
		mListener = null;
	}

}
