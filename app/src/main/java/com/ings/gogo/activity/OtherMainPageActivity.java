package com.ings.gogo.activity;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.alipay.android.phone.mrpc.core.ac;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.FastFoodsActivity.PosterClickListener;
import com.ings.gogo.activity.FastFoodsActivity.PosterPageChange;
import com.ings.gogo.activity.FastFoodsActivity.PosterPagerAdapter;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.FastFoodsAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.PictureUrlEntity;
import com.ings.gogo.entity.ShopPointEntity;
import com.ings.gogo.interfacep.OnTabActivityResultListener;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.view.AutoScrollViewPager;
import com.ings.gogo.view.ColorTrackView;
import com.ings.gogo.view.DepthPageTransformer;
import com.lidroid.xutils.BitmapUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OtherMainPageActivity extends BaseActivity implements
		OnClickListener, OnTabActivityResultListener {
	// 用于轮播图的整形数据
	private final int GET_DATAOK = 0;
	private final int GET_TUIJIAN = 1;
	private final int GET_PICOK = 2;
	// 用于子界面返回父界面的两个参数
	private final int RESULT_CODE = 200;
	private final int REQUEST_CODE = 100;
	// 轮播图使用viewPage
	private TextView mLocationPlaceNameTv;
	// 声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	// 定位参数配置
	private AMapLocationClientOption mLocationOption;
	// 纬度
	private double Latitude;
	// 经度
	private double Longitude;
	// 定位或者位置描述获取到的经纬度信息
	private LatLng locationLatLng;
	// 地理位置描述1 String类型
	private String desc;
	// 地理位置描述2 StringBuffer类型
	private StringBuffer buffer;

	// 双击退出 第一次点击的时间设置为0
	private long firstTime = 0;

	// 用于距离计算的latng
	private LatLng lanlng;
	// 纬度
	private Double dgpsy;
	// 经度
	private Double dgpsx;
	// 存储临时的地址信息
	private String tem;
	private ShopPointEntity shopPointEntity;
	private float distance;
	private List<Float> mydistance = new ArrayList<Float>();
	// 获取数据需要的距离参数
	public static int rangers;
	// 获取到的pointID
	private String mPointID;
	// 集合里面的初始值
	private Float min;
	private int index;
	public static int mWidth;
	private ImageView mShopPopWindowIm;
	private PopupWindow popupwindow;
	private String mPointName;
	private UILApplication myApplication;
	// 选择地址栏返回的地址信息
	private String resultSelect;
	//
	private ListView mAllGoodsListView;
	private RelativeLayout mFastFoodsRe;
	private RelativeLayout mCleanFoodsRe;
	private RelativeLayout mNextFastFoodsRe;
	private RelativeLayout mNextCleanFoodsRe;
	// 显示当前配送的点
	private TextView mTuiJianTv;
	private FastFoodsEntity mFastEntity;
	private FastFoodsAdapter mFastAdapter;
	private final String isToday = "2";
	private BitmapUtils bitmapUtils = null;

	private List<String> posterImage = null;
	public static List<ImageView> points = null;
	// 图片滚动的时间
	private int interval = 3000;
	// 图片的总张数
	private int count = 3;
	private int indexPic;
	private PictureUrlEntity picEntity;
	private List<String> mainLunBoPic = new ArrayList<String>();
	private String allBgPicUrl;
	private LinearLayout mMainCiclePointsInVP;
	private AutoScrollViewPager mMyViewPage;
	private Context context;
	private String[] mPointDescrib;
	private String mPointDescribAll;
	private List<String> mPointDetailList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_othermain);
		myApplication = (UILApplication) getApplication();
		DisplayMetrics dm = new DisplayMetrics();
		// 初始化
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getMainBgImg();
				getShopPointData();
			}
		}).start();
		context = this.getApplicationContext();

        /* 当前位置可能有错误*/
		// 初始化定位配置
		initLocation();
		innitViews();
		Log.d("xiaolijiang",sHA1(this));

	}





	public static String sHA1(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
			byte[] cert = info.signatures[0].toByteArray();
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(cert);
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < publicKey.length; i++) {
				String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
				if (appendString.length() == 1)
					hexString.append("0");
				hexString.append(appendString);
				hexString.append(":");
			}
			String result = hexString.toString();
			return result.substring(0, result.length()-1);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}





	@SuppressLint("ResourceAsColor")
	private void innitViews() {
		// TODO Auto-generated method stub
		// geocoderSearch = new GeocodeSearch(this);
		// geocoderSearch.setOnGeocodeSearchListener(this);
		mLocationPlaceNameTv = (TextView) this
				.findViewById(R.id.mMainPagelocationPlaceTv);
		mLocationPlaceNameTv.setOnClickListener(this);
		mShopPopWindowIm = (ImageView) this
				.findViewById(R.id.mMainPageManagePlace);
		mShopPopWindowIm.setOnClickListener(this);

		mAllGoodsListView = (ListView) this.findViewById(R.id.allGoodsMsgLV);
		View allGoodsLVTop = View.inflate(this,
				R.layout.layout_allgoodslistviewhead, null);
		View viewpagerBottom = View.inflate(this,
				R.layout.layout_listviewbottom, null);
		viewpagerBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogUtils.e("", "");
			}
		});
		mMyViewPage = (AutoScrollViewPager) allGoodsLVTop
				.findViewById(R.id.mAllGoodsViewPager);
		mMainCiclePointsInVP = (LinearLayout) allGoodsLVTop
				.findViewById(R.id.mAllGoodsCicleInVPLL);
		mFastFoodsRe = (RelativeLayout) allGoodsLVTop
				.findViewById(R.id.mFastFoodsRe);
		mFastFoodsRe.setOnClickListener(this);
		mCleanFoodsRe = (RelativeLayout) allGoodsLVTop
				.findViewById(R.id.mCleanFoodsRe);
		mCleanFoodsRe.setOnClickListener(this);
		mNextFastFoodsRe = (RelativeLayout) allGoodsLVTop
				.findViewById(R.id.mNextDayFastFoodsRe);
		mNextFastFoodsRe.setOnClickListener(this);
		mNextCleanFoodsRe = (RelativeLayout) allGoodsLVTop
				.findViewById(R.id.mNextDayCleanFoodsRe);
		mNextCleanFoodsRe.setOnClickListener(this);
		mTuiJianTv = (TextView) allGoodsLVTop
				.findViewById(R.id.mAllGoodsHeadTuiJianTv);

		mAllGoodsListView.addHeaderView(allGoodsLVTop);
		mAllGoodsListView.addFooterView(viewpagerBottom);
		handler.sendEmptyMessageDelayed(0, 1000);

	}

	public void invisibleOnScreen() {

		if (resultSelect != null) {

			// getLatlon(resultSelect);

		} else {
			LogUtils.e("bbbbbbbbbb", "bbbbbbbbbb");
		}

	}

	public void goneOnScreen() {

		// popupwindow.dismiss();
		if (popupwindow != null && popupwindow.isShowing()) {
			popupwindow.dismiss();
			return;
		} else {
			LogUtils.e("111111111", "111111111");

		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case GET_DATAOK:
				LogUtils.e("11111111122", "ponitid" + mPointID + "pointName"
						+ mPointName + "距离" + rangers);
				if (rangers < 0) {
					mTuiJianTv.setText("您所指定的位置还未提供配送服务");
				} else {
					mTuiJianTv.setText("由" + mPointName + "提供配送服务");
				}

				// if (mPointName.equals("")) {
				// mTuiJianTv.setText("请稍后，正在定位");
				// } else {
				//
				// }
				// if (mPointID.equals(null) || rangers < 0) {
				// LogUtils.e("正在为你定位", "正在为你定位");
				//
				// } else {
				if (mPointID == null) {
					LogUtils.e("", "");
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							getProductDatas();
						}
					}).start();
				}

				break;
			case GET_TUIJIAN:
				mAllGoodsListView.setAdapter(mFastAdapter);
				mAllGoodsListView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub

								Intent intent = new Intent(getApplicationContext(), DetailFastFoodsActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("fastFoodsName", mFastEntity
										.getData().get((int) id).getProname());
								bundle.putString("fastFoodsPrice", mFastEntity
										.getData().get((int) id).getPrice()
										+ "");
								bundle.putString("proid", mFastEntity.getData()
										.get((int) id).getProid());
								bundle.putString("goodsImage", mFastEntity
										.getData().get((int) id).getImgurl());
								bundle.putString("isToday", isToday);
								bundle.putInt("Stock", mFastEntity.getData()
										.get((int) id).getStock());
								bundle.putString("ShortDesc", mFastEntity
										.getData().get((int) id).getShortdesc());
								bundle.putSerializable("pointDesc",
										(Serializable) mPointDetailList);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						});
				break;
			case GET_PICOK:
				if (mainLunBoPic.size() == 0) {
					LogUtils.e("数据为空", "数据为空");

				} else {
					mMyViewPage.startAutoScroll();
					addImage();
					initPoints();
					initPoster();
				}
			default:
				break;
			}

		};
	};

	private void addImage() {

		points = new LinkedList<ImageView>();
		posterImage = new LinkedList<String>();
		bitmapUtils = new BitmapUtils(context, null, 1024, 1024);
		for (int i = 0; i < mainLunBoPic.size(); i++) {
			posterImage.add(mainLunBoPic.get(i));
		}

	}

	private void initPoints() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(10, 30, 10, 30);
		for (int i = 0; i < count; i++) {
			ImageView point = new ImageView(context);
			if (i == indexPic % count) {
				point.setBackgroundResource(R.drawable.feature_point_cur);
			} else {
				point.setBackgroundResource(R.drawable.feature_point);
			}
			point.setLayoutParams(lp);
			points.add(point);
			mMainCiclePointsInVP.addView(point);
			mMainCiclePointsInVP.setVisibility(View.VISIBLE);
		}
	}

	public DisplayMetrics getScreen(Activity activity) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics;
	}

	private void initPoster() {
		// 设置 ViewPager的高度为屏幕宽度的1/2
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				(int) (getScreen(this).widthPixels / 2.5));
		mMyViewPage.setPageTransformer(true, new DepthPageTransformer());
		mMyViewPage.setLayoutParams(params);

		mMyViewPage.setAdapter(new PosterPagerAdapter());
		mMyViewPage.setCurrentItem(count * 500);
		mMyViewPage.setInterval(interval);
		mMyViewPage.setOnPageChangeListener(new PosterPageChange());
		mMyViewPage
				.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);

	}

	class PosterPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			ImageView imageView = new ImageView(context);
			imageView.setAdjustViewBounds(true);
			// TODO 调整图片大小
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			imageView.setLayoutParams(params);

			bitmapUtils.display(imageView, posterImage.get(position % count));

			container.addView(imageView);

			imageView.setOnClickListener(new PosterClickListener(position
					% count));
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView((ImageView) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	class PosterClickListener implements View.OnClickListener {

		private int position;

		public PosterClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// viewpager点击事件
			mMyViewPage.stopAutoScroll();
		}

	}

	// 轮播图的滑动监听
	class PosterPageChange implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {

			for (int i = 0; i < count; i++) {
				points.get(i).setBackgroundResource(R.drawable.feature_point);
			}
			points.get(position % count).setBackgroundResource(
					R.drawable.feature_point_cur);

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mMainPageManagePlace:

			if (popupwindow != null && popupwindow.isShowing()) {
				popupwindow.dismiss();
				return;
			} else {
				showPopWindow();
				popupwindow.showAsDropDown(v, 0, 2);
			}

			break;
		case R.id.mMainPagelocationPlaceTv:
			mydistance.clear();
			Intent intentToSelectPlace = new Intent(getApplicationContext(),
					SelectPlaceActivity.class);
			getParent().startActivityForResult(intentToSelectPlace,
					REQUEST_CODE);
			break;

		case R.id.mFastFoodsRe:
			Intent intent2Fast = new Intent(getApplicationContext(),
					FastFoodsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("fastPointId", mPointID);
			bundle.putString("mFastRangers", rangers + "");
			bundle.putString("fastPointName", mPointName);
			bundle.putSerializable("pointDesc", (Serializable) mPointDetailList);
			intent2Fast.putExtras(bundle);
			startActivity(intent2Fast);
			break;
		case R.id.mCleanFoodsRe:
			Intent intent2Clean = new Intent(getApplicationContext(),
					CleanFoodsActivity.class);
			Bundle bundleClean = new Bundle();
			bundleClean.putString("fastPointId", mPointID);
			bundleClean.putString("mFastRangers", rangers + "");
			bundleClean.putString("fastPointName", mPointName);
			bundleClean.putSerializable("pointDesc",
					(Serializable) mPointDetailList);
			intent2Clean.putExtras(bundleClean);
			startActivity(intent2Clean);
			break;
		case R.id.mNextDayFastFoodsRe:
			Intent intent2NextFast = new Intent(getApplicationContext(),
					TomorrowFastFoodsActivity.class);
			Bundle bundleNextFast = new Bundle();
			bundleNextFast.putString("fastPointId", mPointID);
			bundleNextFast.putString("mFastRangers", rangers + "");
			bundleNextFast.putString("fastPointName", mPointName);
			bundleNextFast.putSerializable("pointDesc",
					(Serializable) mPointDetailList);
			intent2NextFast.putExtras(bundleNextFast);
			startActivity(intent2NextFast);
			break;
		case R.id.mNextDayCleanFoodsRe:
			Intent intent2NextClean = new Intent(getApplicationContext(),
					TomorrowCleanFoodsActivity.class);
			Bundle bundleNextClean = new Bundle();
			bundleNextClean.putString("fastPointId", mPointID);
			bundleNextClean.putString("mFastRangers", rangers + "");
			bundleNextClean.putString("fastPointName", mPointName);
			bundleNextClean.putSerializable("pointDesc",
					(Serializable) mPointDetailList);
			intent2NextClean.putExtras(bundleNextClean);
			startActivity(intent2NextClean);
			break;

		default:
			break;
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_CODE) {
				mydistance.clear();
				resultSelect = data.getStringExtra("keyPlaceWord");
				LogUtils.e("选择地址主页的返回值--->>>kk", resultSelect);
				mLocationPlaceNameTv.setText(resultSelect);
				Double weidu = data.getDoubleExtra("weidu", 0);
				Double jingdu = data.getDoubleExtra("jingdu", 0);
				LogUtils.e("主页选择地址传过来的值", weidu + "   " + jingdu);
				locationLatLng = new LatLng(weidu, jingdu);
				getMinRanges(locationLatLng);

			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (popupwindow != null && popupwindow.isShowing()) {
			popupwindow.dismiss();
			return;
		} else {
			LogUtils.e("111111111", "111111111");

		}

	}

	// 显示地址管理的popuWindow
	private void showPopWindow() {
		// TODO Auto-generated method stub
		// // 获取自定义布局文件pop.xml的视图
		View customView = getLayoutInflater().inflate(
				R.layout.layout_popwindowitem, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		popupwindow = new PopupWindow(customView, 250, 180);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		popupwindow.setAnimationStyle(R.style.AnimationFade);
		// 自定义view添加触摸事件

		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					popupwindow = null;
				}

				return false;
			}
		});

		/** 在这里可以实现自定义视图的功能 */
		TextView mManagePlace = (TextView) customView
				.findViewById(R.id.popwindow_manageplace);
		mManagePlace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent2Place = new Intent(getApplicationContext(),
						PlaceManageActivity.class);
				startActivity(intent2Place);
			}
		});

	}

	// 获取轮播图片
	private void getMainBgImg() {
		// TODO Auto-generated method stub
		// 创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(BaseData.GET_ADS_PIC)
				.build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String getPicBody = response.body().string();
				LogUtils.e("今日快餐轮播图的body--->>", getPicBody);
				Gson gson = new Gson();
				picEntity = gson.fromJson(getPicBody, PictureUrlEntity.class);
				count = picEntity.getData().size();
				LogUtils.e("今日快餐count啊啊啊啊啊啊", count + "+");
				for (int i = 0; i < picEntity.getData().size(); i++) {
					allBgPicUrl = picEntity.getData().get(i).getImgurl();
					mainLunBoPic.add(allBgPicUrl);
				}
				Message msg = handler.obtainMessage(GET_PICOK);
				msg.sendToTarget();

			}
		});
	}

	// 获取shopPonit
	protected void getShopPointData() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		final Request request = new Request.Builder().url(BaseData.GET_POINT)
				.build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String pointBody = response.body().string();
				LogUtils.e("配送点的位置 点点点--->>", pointBody);
				Gson gson = new Gson();
				shopPointEntity = gson.fromJson(pointBody,
						ShopPointEntity.class);

			}

		});
	}

	// 获取首页的商品信息
	protected void getProductDatas() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();

		final Request request = new Request.Builder().url(
				BaseData.GET_FAST_PRODUCTS + mPointID
						+ "&categoryid=8792CBB4-B0CA-4276-AC5A-1F1B08562563"
						+ "&figure=" + rangers).build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String getProductBody = response.body().string();
				LogUtils.e("今日快餐的body--->>", getProductBody);
				Gson gson = new Gson();
				mFastEntity = gson.fromJson(getProductBody,
						FastFoodsEntity.class);
				mFastAdapter = new FastFoodsAdapter(getApplicationContext(),
						mFastEntity.getData());
				Message msg = handler.obtainMessage(GET_TUIJIAN);
				msg.sendToTarget();

			}
		});
	}

	// 传入地址信息 将地址信息转化为经纬度 信息
	// public void getLatlon(String name) {
	// LogUtils.e("选择低脂的返回值--->>", "name:" + name);
	// mGeocodeQuery = new GeocodeQuery(name, "贵阳");//
	// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
	// geocoderSearch.getFromLocationNameAsyn(mGeocodeQuery);// 设置同步地理编码请求
	// }

	// 声明定位回调监听器
	public AMapLocationListener mLocationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) {
					// 定位成功回调信息，设置相关消息
					buffer = new StringBuffer();
					buffer.append(amapLocation.getCity() + " "
							+ amapLocation.getDistrict() + " "
							+ amapLocation.getStreet() + " "
							+ amapLocation.getStreetNum());
					desc = "";
					Bundle locBundle = amapLocation.getExtras();
					if (locBundle != null) {
						tem = locBundle.getString("desc");
						desc = tem.replace("贵州省 贵阳市", "");
					}

					mLocationPlaceNameTv.setText(desc);

					locationLatLng = new LatLng(amapLocation.getLatitude(),
							amapLocation.getLongitude());
					// getLatlon(desc.trim());
					getMinRanges(locationLatLng);
				} else {
					// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
					Log.e("AmapError", "location Error, ErrCode:"
							+ amapLocation.getErrorCode() + ", errInfo:"
							+ amapLocation.getErrorInfo());
					showToastLong(amapLocation.getErrorInfo());
				}
			}
		}

	};

	// 前面一个参数是输入的地址坐标或者定位的坐标 后面一个参数为店面坐标
	public void getMinRanges(LatLng inputLatLng) {
		for (int i = 0; i < shopPointEntity.getData().size(); i++) {
			String pointName = shopPointEntity.getData().get(i).getPointname();
			// rangers = entity.getData().get(i).getRanges();
			LogUtils.e("pointName--->>>", pointName);
			String gpsx = new String(shopPointEntity.getData().get(i).getGpsx());
			dgpsx = Double.parseDouble(gpsx);
			LogUtils.e("dgpsx--->>", dgpsx + "");
			String gpsy = new String(shopPointEntity.getData().get(i).getGpsy());
			dgpsy = Double.parseDouble(gpsy);
			LogUtils.e("dgpsy--->>", dgpsy + "");
			lanlng = new LatLng(dgpsy, dgpsx);
			// 该lanlng固定 传入另一个经纬度进行计算
			// getDistance(locationLatLng, lanlng);
			distance = AMapUtils.calculateLineDistance(inputLatLng, lanlng);
			mydistance.add(distance);

		}
		for (int j = 0; j < mydistance.size(); j++) {
			LogUtils.e("距离集合的内容是--》》", mydistance.get(j) + "");
		}
		min = mydistance.get(0);
		LogUtils.e("最初的min值", "min初始值:" + min);
		indexPic = 0;
		for (int m = 1; m < mydistance.size(); m++) {
			if (min > mydistance.get(m)) {
				min = mydistance.get(m);
				indexPic = m;
			}

		}
		rangers = (int) (shopPointEntity.getData().get(0).getRanges() - min);
		// LogUtils.e("下角标index--->>", "下角标:" + index);
		mPointID = shopPointEntity.getData().get(indexPic).getPointid();

		mPointName = shopPointEntity.getData().get(indexPic).getPointname();
		mPointDescribAll = shopPointEntity.getData().get(indexPic).getTips();
		mPointDescrib = mPointDescribAll.split("[*]");
		mPointDetailList = new ArrayList<String>();
		Collections.addAll(mPointDetailList, mPointDescrib);
		for (int i = 0; i < mPointDetailList.size(); i++) {
			LogUtils.e("店铺的描述---》》》", mPointDetailList.get(i));
		}

		LogUtils.e("三个参数rangers--->>>", "ID:" + mPointID + "name:" + mPointName
				+ "rangers:" + rangers);
		// }
		if (shopPointEntity.getData().size() != 0) {
			myApplication.setPointID(mPointID);
			// 经度
			myApplication.setJingDu(shopPointEntity.getData().get(indexPic)
					.getGpsx());
			// 纬度
			myApplication.setWeiDu(shopPointEntity.getData().get(indexPic)
					.getGpsy());
			myApplication.setPointName(mPointName);
			myApplication.setRanges(rangers + "");
		} else {
			LogUtils.e("全局变量暂未设置成功", "全局变量暂未设置成功");
		}

		Message msg = handler.obtainMessage(GET_DATAOK);
		msg.sendToTarget();
	}

	// 初始化定位信息
	private void initLocation() {
		// TODO Auto-generated method stub
		// 初始化定位
		mLocationClient = new AMapLocationClient(this);


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






	// 双击退出
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				firstTime = System.currentTimeMillis();
				return true;
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置
				startActivity(intent);
				// android.os.Process.killProcess(android.os.Process.myPid());
				myApplication.getInstance().exit();
				System.exit(0);
				finish();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

}
