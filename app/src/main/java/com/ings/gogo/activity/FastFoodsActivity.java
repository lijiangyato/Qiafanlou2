package com.ings.gogo.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.FastFoodsAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.PictureUrlEntity;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.view.AutoScrollViewPager;
import com.ings.gogo.view.DepthPageTransformer;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class FastFoodsActivity extends BaseActivity {
	private static final int GET_DATA_OK = 1;
	private static final int GET_PICOK = 0;
	private static final int GET_DATA_OK1 = 4;
	private final String isToday = "2";
	private static ListView mNewFastFoodsLV;
	private TextView mGoodsDesrcTv;
	private ImageView mTopBackIv;
	private LinearLayout mMainCiclePointsInVP;
	private AutoScrollViewPager mMyViewPage;
	private FastFoodsEntity mFastEntity;
	private FastFoodsAdapter mFastAdapter;
	private static Context context;
	private BitmapUtils bitmapUtils = null;

	private List<String> posterImage = null;
	public static List<ImageView> points = null;
	// 图片滚动的时间
	private int interval = 3000;
	// 图片的总张数
	private int count = 3;
	private int index;
	private PictureUrlEntity picEntity;
	private List<String> mainLunBoPic = new ArrayList<String>();
	private String allBgPicUrl;
	private UILApplication myApplication;
	private String pointID;
	private String pointName;
	private String rangers;
	private List<String> mShopDesc = null;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_fastfoods);
		myApplication = (UILApplication) getApplication();
		Bundle bundle = this.getIntent().getExtras();
		pointID = bundle.getString("fastPointId");
		pointName = bundle.getString("fastPointName");
		rangers = bundle.getString("mFastRangers");
		mShopDesc = (List<String>) bundle.getSerializable("pointDesc");
		LogUtils.e("快餐查看传值信息----》》", pointID + pointName + rangers
				+ "??????????");
		context = FastFoodsActivity.this;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getMainBgImg();
				getProductDatas();
			}
		}).start();
		ininitViews();
	}

	public void ininitViews() {
		// TODO Auto-generated method stub

		mNewFastFoodsLV = (ListView) this.findViewById(R.id.listView_fastFoods);
		View viewpagerTop = View.inflate(this, R.layout.headviewpager_fast,
				null);
		View viewpagerBottom = View.inflate(this,
				R.layout.layout_listviewbottom, null);
		viewpagerBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogUtils.e("", "");
			}
		});
		mMyViewPage = (AutoScrollViewPager) viewpagerTop
				.findViewById(R.id.mMainPageViewPager);
		mMainCiclePointsInVP = (LinearLayout) viewpagerTop
				.findViewById(R.id.mMainPageCicleInVPLL);
		mGoodsDesrcTv = (TextView) viewpagerTop.findViewById(R.id.alltop_title);
		mTopBackIv = (ImageView) viewpagerTop.findViewById(R.id.alltop_back);
		mTopBackIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// mIsInTheRangeTv.setText(pointName + rangers);

		mNewFastFoodsLV.addHeaderView(viewpagerTop);
		mNewFastFoodsLV.addFooterView(viewpagerBottom);
		handler.sendEmptyMessageDelayed(0, 1000);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_DATA_OK:
				if (mFastEntity.getData().size() == 0) {
					mGoodsDesrcTv.setText("");
				} else {
					mGoodsDesrcTv.setText("今日快餐（共"
							+ mFastEntity.getData().size() + "个商品）");
				}

				mNewFastFoodsLV.setAdapter(mFastAdapter);
				mNewFastFoodsLV
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(
										getApplicationContext(),
										DetailFastFoodsActivity.class);
								Bundle bundle = new Bundle();
								//异常报错=long长整型转换成int id=变成了-1
								bundle.putString("fastFoodsName", mFastEntity
										.getData().get((int) position).getProname());

								bundle.putString("fastFoodsPrice", mFastEntity
										.getData().get((int) position).getPrice()
										+ "");
								bundle.putString("proid", mFastEntity.getData()
										.get((int) position).getProid());
								bundle.putString("goodsImage", mFastEntity
										.getData().get((int) position).getImgurl());
								bundle.putString("isToday", isToday);
								bundle.putInt("Stock", mFastEntity.getData()
										.get((int) position).getStock());
								bundle.putString("ShortDesc", mFastEntity
										.getData().get((int) position).getShortdesc());
								bundle.putSerializable("pointDesc",
										(Serializable) mShopDesc);
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

				break;

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

	protected void getProductDatas() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();

		final Request request = new Request.Builder().url(
				BaseData.GET_FAST_PRODUCTS + pointID
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
				Message msg = handler.obtainMessage(GET_DATA_OK);
				msg.sendToTarget();

			}
		});
	}

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

	private void initPoints() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(10, 30, 10, 30);
		for (int i = 0; i < count; i++) {
			ImageView point = new ImageView(context);
			if (i == index % count) {
				point.setBackgroundResource(R.drawable.feature_point_cur);
			} else {
				point.setBackgroundResource(R.drawable.feature_point);
			}
			point.setLayoutParams(lp);
			points.add(point);
			mMainCiclePointsInVP.addView(point);
			mMainCiclePointsInVP.setVisibility(View.GONE);
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

}
