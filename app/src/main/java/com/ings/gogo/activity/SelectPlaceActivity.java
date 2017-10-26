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
	// ����AMapLocationClient�����
	public AMapLocationClient mLocationClient = null;
	// ��λ��������
	private AMapLocationClientOption mLocationOption;
	// γ��
	private double Latitude;
	// ����
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
	private PoiSearch.Query query;// Poi��ѯ������
	private PoiSearch poiSearch;// POI����
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
		initLocation();// ��ʼ����λ����
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
		mSelectPlaceAT.addTextChangedListener(this);// ����ı����������¼�
		mSelectAsLocationBt = (Button) this
				.findViewById(R.id.setAsTheCurerentPlaceBt);
		mSelectAsLocationBt.setOnClickListener(this);
		mSuggesionPlaceLV = (ListView) this
				.findViewById(R.id.selectActivityNearByPleaceLV);
	}

	private void initLocation() {
		// TODO Auto-generated method stub
		// ��ʼ����λ
		mLocationClient = new AMapLocationClient(getApplicationContext());
		// ���ö�λ�ص�����
		mLocationClient.setLocationListener(mLocationListener);

		// ��ʼ����λ����
		mLocationOption = new AMapLocationClientOption();
		// ���ö�λģʽΪ�߾���ģʽ��Battery_SavingΪ�͹���ģʽ��Device_Sensors�ǽ��豸ģʽ
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// �����Ƿ񷵻ص�ַ��Ϣ��Ĭ�Ϸ��ص�ַ��Ϣ��
		mLocationOption.setNeedAddress(true);
		// �����Ƿ�ֻ��λһ��,Ĭ��Ϊfalse
		mLocationOption.setOnceLocation(true);
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

	// ������λ�ص�������
	public AMapLocationListener mLocationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) {
					// ��λ�ɹ��ص���Ϣ�����������Ϣ
					Latitude = amapLocation.getLatitude();// ��ȡγ��
					Longitude = amapLocation.getLongitude();// ��ȡ����
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
						desc = tem.replace("����ʡ ������", "");
					}
					LogUtils.e("desc", desc);
					LogUtils.e("buffer", buffer.toString());

				} else {
					// ��ʾ������ϢErrCode�Ǵ����룬errInfo�Ǵ�����Ϣ������������
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
				showToastLong("��λʧ�ܣ�");
			} else {
				mSelectAsLocationBt.setText(desc);
				Intent intent = new Intent();
				intent.putExtra("keyPlaceWord", desc);
				intent.putExtra("weidu", Latitude);
				intent.putExtra("jingdu", Longitude);
				LogUtils.e("���صĶ�λ������", desc);
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
	 * ��������
	 */
	private void doSearchQuery() {
		query = new PoiSearch.Query(mSelectPlaceAT.getText().toString(),
				"������ַ��Ϣ|��ͨ��ʩ����|�����������������|������ַ��Ϣ|����סլ|ס�޷���", "����");
		query.setPageSize(20);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(1);// ���ò��һҳ
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);// ���ûص����ݵļ�����
		// �㸽��2000���ڵ��������
		if (latLonPoint != null) {
			searchBound = new PoiSearch.SearchBound(latLonPoint, 1000);
			poiSearch.setBound(searchBound);
			poiSearch.setBound(new SearchBound(latLonPoint, 1000));
		}

		poiSearch.searchPOIAsyn();// ��ʼ����
	}

	/**
	 * ��������
	 */
	private void searchMove(LatLng locationLatLng) {
		query = new PoiSearch.Query(mSelectPlaceAT.getText().toString(),
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
		InputtipsQuery inputquery = new InputtipsQuery(newText, "����");
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

	// ������ʾ �����������õ�listView����
	@Override
	public void onGetInputtips(final List<Tip> tipList, int rCode) {
		mSelectAsLocationBt.setVisibility(View.GONE);
		mNearUnuseBt.setVisibility(View.GONE);
		if (rCode == 0) {// ��ȷ����
			List<String> listString = new ArrayList<String>();
			for (int i = 0; i < tipList.size(); i++) {
				listString.add(tipList.get(i).getName());
				LogUtils.e("tipsList-->>name", tipList.get(i).getName());
				LogUtils.e("tipsList-->>", tipList.get(i) + "");
			}
			LogUtils.e("tipsList-->>Size", tipList.size() + "");

			/**
			 * �ı���ʾ�ַ�����ɫ����ֱ�ӵ�item_input_hint_layout����������textColor����
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
					String street = tem.replace("����ʡ", "");
					LogUtils.e("���ص�������ʾ������", street
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

	// poi�����Ļ�� ���� ����ȡ���������õ�listView����
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
				LogUtils.e("���ص��Ƽ���ֵ--������", result.getPois().get(position)
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
