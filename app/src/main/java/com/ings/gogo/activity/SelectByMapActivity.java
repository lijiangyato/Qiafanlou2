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
	// AMap�ǵ�ͼ����
	private AMap aMap;
	private MapView mapView;
	// ����AMapLocationClient����󣬶�λ�����
	private AMapLocationClient mLocationClient = null;
	// ����mLocationOption���󣬶�λ����
	public AMapLocationClientOption mLocationOption = null;
	// ����mListener���󣬶�λ������
	private OnLocationChangedListener mListener = null;
	// ��ʶ�������ж��Ƿ�ֻ��ʾһ�ζ�λ��Ϣ���û����¶�λ
	private boolean isFirstLoc = true;
	private AutoCompleteTextView mInputKeyWord;// ���������ؼ���
	private String keyWord = "";// Ҫ�����poi�����ؼ���
	private PoiResult poiResult; // poi���صĽ��
	// private int currentPage = 0;// ��ǰҳ�棬��0��ʼ����
	private PoiSearch.Query query;// Poi��ѯ������
	private PoiSearch poiSearch;// POI����
	// γ��
	private double Latitude;
	// ����
	private double Longitude;
	// �ƶ���γ��
	private double LatitudeMove;
	// �ƶ��ľ���
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
	private ProgressDialog progDialog = null;// ����ʱ������
	// private LatLonPoint latLonPoint;
	private PoiSearch.SearchBound searchBound;
	private AMapLocation aMapLocation;
	private ImageView mMapBackToParent;
	private TextView mNowPlaceTv;
	// ��Ļ�ĸ߶�
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
		// ��ȡ��ǰ�ؼ��Ĳ��ֶ���
		params.height = height / 3;// ���õ�ǰ�ؼ����ֵĸ߶�
		mapView.setLayoutParams(params);// �����úõĲ��ֲ���Ӧ�õ��ؼ���
		mapView.onCreate(savedInstanceState);
		initViews();
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnCameraChangeListener(cameraChangeListener);
			// ������ʾ��λ��ť ���ҿ��Ե��1
			UiSettings settings = aMap.getUiSettings();
			aMap.setLocationSource(this);// �����˶�λ�ļ���
			// �Ƿ���ʾ��λ��ť
			settings.setMyLocationButtonEnabled(true);
			aMap.setMyLocationEnabled(true);// ��ʾ��λ�㲢�ҿ��Դ�����λ,Ĭ����flase
		}
		// ��ʼ��λ
		location();

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		}
	};

	private void initViews() {
		// TODO Auto-generated method stub
		mInputKeyWord = (AutoCompleteTextView) findViewById(R.id.auto_txt_key_word);
		mInputKeyWord.addTextChangedListener(this);// ����ı����������¼�
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
		mGeocodeQuery = new GeocodeQuery(name, "����");//
		// ��һ��������ʾ��ַ���ڶ���������ʾ��ѯ���У����Ļ�������ȫƴ��citycode��adcode��
		geocoderSearch.getFromLocationNameAsyn(mGeocodeQuery);// ����ͬ�������������
	}

	private void location() {
		// ��ʼ����λ
		mLocationClient = new AMapLocationClient(getApplicationContext());
		// ���ö�λ�ص�����
		mLocationClient.setLocationListener(this);
		// ��ʼ����λ����
		mLocationOption = new AMapLocationClientOption();
		// ���ö�λģʽΪHight_Accuracy�߾���ģʽ��Battery_SavingΪ�͹���ģʽ��Device_Sensors�ǽ��豸ģʽ
		mLocationOption
				.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		// �����Ƿ񷵻ص�ַ��Ϣ��Ĭ�Ϸ��ص�ַ��Ϣ��
		mLocationOption.setNeedAddress(true);
		// �����Ƿ�ֻ��λһ��,Ĭ��Ϊfalse
		mLocationOption.setOnceLocation(false);
		// �����Ƿ�ǿ��ˢ��WIFI��Ĭ��Ϊǿ��ˢ��
		mLocationOption.setWifiActiveScan(true);
		// �����Ƿ�����ģ��λ��,Ĭ��Ϊfalse��������ģ��λ��
		mLocationOption.setMockEnable(false);
		// ���ö�λ���,��λ����,Ĭ��Ϊ2000ms
		mLocationOption.setInterval(2000);
		// ����λ�ͻ��˶������ö�λ����
		mLocationClient.setLocationOption(mLocationOption);
		// ������λ
		mLocationClient.startLocation();

	}

	// �ƶ��ҵ�ͼ��λ
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
						// String area = address.getProvince();// ʡ��ֱϽ��
						// String loc = address.getCity();// �ؼ��л�ֱϽ��
						String subLoc = address.getDistrict();// �����ػ��ؼ���
						// String ts = address.getTownship();// ����
						String thf = null;// ��·
						List<RegeocodeRoad> regeocodeRoads = address.getRoads();// ��·�б�
						if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
							RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
							if (regeocodeRoad != null) {
								thf = regeocodeRoad.getName();
							}
						}
						String subthf = null;// ���ƺ�
						StreetNumber streetNumber = address.getStreetNumber();
						if (streetNumber != null) {
							subthf = streetNumber.getNumber();
						}
						String fn = address.getBuilding();// ��־�Խ���,����·Ϊnullʱ��ʾ
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
							stringBuffer.append(fn + "����");
						LogUtils.e("ȥȥȥ", stringBuffer.toString());
						// �Ƿ���ʾ��λ��Ϣ
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
				// ��λ�ɹ��ص���Ϣ�����������Ϣ

				aMapLocation.getLocationType();// ��ȡ��ǰ��λ�����Դ�������綨λ���������ٷ���λ���ͱ�
				// Latitude = aMapLocation.getLatitude();// ��ȡγ��
				// Longitude = aMapLocation.getLongitude();// ��ȡ����
				aMapLocation.getAccuracy();// ��ȡ������Ϣ
				aMapLocation.getAddress();// ��ַ�����option������isNeedAddressΪfalse����û�д˽�������綨λ����л��е�ַ��Ϣ��GPS��λ�����ص�ַ��Ϣ��
				aMapLocation.getCountry();// ������Ϣ
				aMapLocation.getProvince();// ʡ��Ϣ
				aMapLocation.getCity();// ������Ϣ
				aMapLocation.getDistrict();// ������Ϣ
				aMapLocation.getStreet();// �ֵ���Ϣ
				aMapLocation.getStreetNum();// �ֵ����ƺ���Ϣ
				aMapLocation.getCityCode();// ���б���
				aMapLocation.getAdCode();// ��������

				// ��������ñ�־λ����ʱ���϶���ͼʱ�����᲻�Ͻ���ͼ�ƶ�����ǰ��λ��
				if (isFirstLoc) {
					// �������ż���
					aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
					// ����ͼ�ƶ�����λ��
					aMap.moveCamera(CameraUpdateFactory
							.changeLatLng(new LatLng(
									aMapLocation.getLatitude(), aMapLocation
											.getLongitude())));
					// �����λ��ť �ܹ�����ͼ�������ƶ�����λ��
					mListener.onLocationChanged(aMapLocation);
					// ��ȡ��λ��Ϣ
					buffer = new StringBuffer();
					buffer.append(aMapLocation.getCity() + " "
							+ aMapLocation.getDistrict() + " "
							+ aMapLocation.getStreet() + " "
							+ aMapLocation.getStreetNum());

					Bundle locBundle = aMapLocation.getExtras();
					if (locBundle != null) {
						String tem = locBundle.getString("desc");
						desc = tem.replace("����ʡ ������", "");
					}

					LogUtils.e("������locBundle-->>>", desc);
					LogUtils.e("������locbuffere-->>>", buffer.toString());
					mNowPlaceTv.setText(desc);
					addMarkerByString(locationLatLng, desc);
					locationMarker.showInfoWindow();
					isFirstLoc = false;
				}

			} else {
				// ��ʾ������ϢErrCode�Ǵ����룬errInfo�Ǵ�����Ϣ������������
				Log.e("AmapError",
						"location Error, ErrCode:"
								+ aMapLocation.getErrorCode() + ", errInfo:"
								+ aMapLocation.getErrorInfo());
				Toast.makeText(getApplicationContext(), "��λʧ��",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// ��ʾ��λ����Ϣ
	private void addMarkerByString(LatLng latLng, String desc) {
		// search();
		doSearchQuery();
		MarkerOptions markerOptions = new MarkerOptions();
		locationMarker = aMap.addMarker(markerOptions);
	}

	/**
	 * ��������
	 */
	private void searchMove(LatLonPoint latLonPoint) {
		query = new PoiSearch.Query(keyWord,
				"������ַ��Ϣ|��ͨ��ʩ����|�����������������|������ַ��Ϣ|����סլ|ס�޷���|�������", "����");
		query.setPageSize(20);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(1);// ���ò��һҳ
		PoiSearch poiSearch = new PoiSearch(this, query);
		// �����Ϊ��ֵ
		if (LatitudeMove != 0.0 && LongitudeMove != 0.0) {
			poiSearch.setBound(new SearchBound(new LatLonPoint(LatitudeMove,
					LongitudeMove), 1000));// �����ܱ����������ĵ��Լ�����
			poiSearch.setOnPoiSearchListener(this);// �������ݷ��صļ�����
			poiSearch.searchPOIAsyn();// ��ʼ����

		}

	}

	private void search(LatLonPoint latLonPoint) {
		if (aMapLocation == null) {
			onLocationChanged(aMapLocation);
			aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
			// ����ͼ�ƶ�����λ��
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
					latLonPoint.getLatitude(), latLonPoint.getLongitude())));
			// �����λ��ť �ܹ�����ͼ�������ƶ�����λ��
			mListener.onLocationChanged(aMapLocation);
		} else {
			// �������ż���
			aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
			// ����ͼ�ƶ�����λ��
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
					latLonPoint.getLatitude(), latLonPoint.getLongitude())));
			// �����λ��ť �ܹ�����ͼ�������ƶ�����λ��
			mListener.onLocationChanged(aMapLocation);
		}
		query = new PoiSearch.Query(keyWord,
				"������ַ��Ϣ|��ͨ��ʩ����|�����������������|������ַ��Ϣ|����סլ|ס�޷���|�������", "����");
		query.setPageSize(20);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(1);// ���ò��һҳ
		PoiSearch poiSearch = new PoiSearch(this, query);
		// �����Ϊ��ֵ
		if (Latitude != 0.0 && Longitude != 0.0) {
			poiSearch.setBound(new SearchBound(new LatLonPoint(Latitude,
					Longitude), 1000));// �����ܱ����������ĵ��Լ�����
			poiSearch.setOnPoiSearchListener(this);// �������ݷ��صļ�����
			poiSearch.searchPOIAsyn();// ��ʼ����

		}

	}

	/**
	 * ��������
	 */
	private void doSearchQuery() {
		query = new PoiSearch.Query(keyWord,
				"������ַ��Ϣ|��ͨ��ʩ����|�����������������|������ַ��Ϣ|����סլ|ס�޷���|�������", "����");
		query.setPageSize(20);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(1);// ���ò��һҳ
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);// ���ûص����ݵļ�����
		if (Latitude != 0.0 && Longitude != 0.0) {
			poiSearch.setBound(new SearchBound(new LatLonPoint(Latitude,
					Longitude), 1000));// �����ܱ����������ĵ��Լ�����
			poiSearch.setOnPoiSearchListener(this);// �������ݷ��صļ�����
			poiSearch.searchPOIAsyn();// ��ʼ����

		}
	}

	// ������ʾ
	@Override
	public void onGetInputtips(final List<Tip> tipList, int rCode) {
		mapView.setVisibility(View.GONE);
		mMapLL.setVisibility(View.GONE);
		if (rCode == 0) {// ��ȷ����
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
					String street = tem.replace("����ʡ������", "");
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
	 * POI��Ϣ��ѯ�ص�����
	 */

	@Override
	public void onPoiSearched(final PoiResult result, int code) {
		mLocationAdapter = new LocationAdapter(getApplicationContext(),
				result.getPois());
		LogUtils.e("���ϵĴ�С---����", mLocationAdapter.getCount() + "");
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

	// ����ʱ ��ʾ��Ϣ
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		String newText = s.toString().trim();
		InputtipsQuery inputquery = new InputtipsQuery(newText, "����");
		Inputtips inputTips = new Inputtips(SelectByMapActivity.this,
				inputquery);
		inputTips.setInputtipsListener(this);
		inputTips.requestInputtipsAsyn();

	}

	// ���ݵ�ַת��Ϊ��γ����Ϣ
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				address = result.getGeocodeAddressList().get(0);
				addressName = "��γ��ֵ:" + address.getLatLonPoint() + "\nλ������:"
						+ address.getFormatAddress();
				Latitude = address.getLatLonPoint().getLatitude();
				Longitude = address.getLatLonPoint().getLongitude();
				search(address.getLatLonPoint());
				ToastUtil.show(SelectByMapActivity.this, addressName);
				LogUtils.e("��õľ�γ����--����", addressName);

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
	 * ����γ��ת��Ϊ��ֵַ���贫�ݾ�γ��
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addressName = result.getRegeocodeAddress().getFormatAddress()
						+ "����";
				ToastUtil.show(SelectByMapActivity.this, addressName);
				Log.e("������ֵ�ǣ�����", addressName);
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
		mLocationClient.stopLocation();// ֹͣ��λ
		mLocationClient.onDestroy();// ���ٶ�λ�ͻ��ˡ�
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	// ���λ
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

	// ֹͣ��λ
	@Override
	public void deactivate() {
		mListener = null;
	}

}
