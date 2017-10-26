package com.ings.gogo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.DescribByImageAdapter;
import com.ings.gogo.adapter.DescribByWordAdapter;
import com.ings.gogo.adapter.FastFoodsAdapter;
import com.ings.gogo.adapter.GalleryAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.db.MyShopSqlHelper;
import com.ings.gogo.entity.DetailFoodsEntity;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.entity.MyShopCarEntity;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.view.AutoScrollViewPager;
import com.ings.gogo.view.DepthPageTransformer;
import com.ings.gogo.view.MyProductContentPage;
import com.ings.gogo.view.MyProductDetailInfoPage;
import com.ings.gogo.view.MySnapPageLayout;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class DetailCleanFoodsActivity extends BaseActivity implements
		OnClickListener {
	private static final int GET_DETAIL_MSG_OK = 1;
	// 获取到的doods的唯一标识
	private String mProid;
	// 食物名
	private String mDetailCleanFoodsName;
	// 食物价格
	private String mDetailCleanFoodsPrice;
	// 返回上一界面
	private ImageView mDetailBackToParent;
	// 返回主页
	private ImageButton mDetailBackToMainBt;
	// 购物车
	private RelativeLayout mDetalShopCarRe;
	// 加入购物车
	private Button mJoinInShopCarBt;
	// 自定义的view
	private MySnapPageLayout mcoySnapPageLayout = null;
	// 上拉加载出来的信息
	private MyProductContentPage bottomPage = null;
	// 首先显示的商品信息
	private MyProductDetailInfoPage topPage = null;
	// 显示商品基本信息的Listview一些简单介绍
	private ListView mFoodsDetailLV;
	// 用图文介绍商品的信息的ListView
	private ListView mDescFoodsByImgWordLV;
	// foods详细界面的商品名
	private TextView mDetailFoodsNameTv;
	// foods详细界面的商品价格
	private TextView mDetailFoodsPriceTv;
	// 对用foods的ViewPage
	private AutoScrollViewPager mDetailFoodsImgViewPager;
	// 存放轮播图上面的小圆点的LinearLayout
	private LinearLayout mDetailCirclePointInVPLL;
	private Context context;
	private BitmapUtils bitmapUtils = null;
	private int index = 0;
	private List<String> posterImage = null;
	private List<ImageView> points = null;
	// 图片滚动的时间
	private int interval = 3000;
	// 图片的总张数
	private int allPicCount;
	private int lunBoPicCount;
	private int descPicCount;
	// 获取对商品的描述信息
	private String[] mFoodsDescrib;
	private String mFoodsDescribAll;
	// private List<String> bgUrls = new ArrayList<String>();
	// private String AllUrls;
	private MyShopSqlHelper mySqlHelper;
	private SQLiteDatabase db;
	private String mCleanFoodsImageUrl;
	private DetailFoodsEntity mDetailFoodsEntity;
	private List<String> mFastFoodsDetailList = null;
	private DescribByWordAdapter mWordAdapter;
	private String isToday;
	private int stock;
	private String isToday2;
	private List<MyShopCarEntity> entity = new ArrayList<MyShopCarEntity>();
	private UILApplication myApplication;
	private String telNum;
	private TextView mShopName;
	private String LunBoUrls;
	private List<String> allLunBoUrl = new ArrayList<String>();
	private String goodsBgUrls;
	private List<String> allGoodsBgUrl = new ArrayList<String>();
	private RecyclerView mDescShopPointRV;
	private List<String> mShopDesc = null;
	private String mShortDesc;
	private TextView mShortDescTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detailfoodslayout);
		myApplication = (UILApplication) getApplication();
		telNum = myApplication.getTelNum();
		Bundle bundle = this.getIntent().getExtras();
		mProid = bundle.getString("proid");
		mDetailCleanFoodsName = bundle.getString("cleanFoodsName");
		mDetailCleanFoodsPrice = bundle.getString("cleanFoodsPrice");
		mCleanFoodsImageUrl = bundle.getString("cleanFoodsImage");
		mShortDesc = bundle.getString("ShortDesc");
		mShopDesc = (List<String>) bundle.getSerializable("pointDesc");
		isToday = bundle.getString("isToday");
		stock = bundle.getInt("Stock");
		LogUtils.e("传值是否成功---》》", mDetailCleanFoodsName
				+ mDetailCleanFoodsPrice);
		mySqlHelper = new MyShopSqlHelper(getApplicationContext(), "GOGO",
				null, 1);
		db = mySqlHelper.getWritableDatabase();
		innitAllViews();

	}

	private void initPoints() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(10, 30, 10, 30);
		for (int i = 0; i < lunBoPicCount; i++) {
			ImageView point = new ImageView(context);
			if (i == index % lunBoPicCount) {
				point.setBackgroundResource(R.drawable.feature_point_cur);
			} else {
				point.setBackgroundResource(R.drawable.feature_point);
			}
			point.setLayoutParams(lp);
			points.add(point);
			mDetailCirclePointInVPLL.addView(point);
			mDetailCirclePointInVPLL.setVisibility(View.GONE);
		}
	}

	// 实例化各种控件
	private void innitAllViews() {
		// TODO Auto-generated method stub
		mDetailBackToParent = (ImageView) this
				.findViewById(R.id.detailtopbackToParentBt);
		mDetailBackToParent.setOnClickListener(this);
		mDetailBackToMainBt = (ImageButton) this
				.findViewById(R.id.detailtopbacktomain);
		mDetailBackToMainBt.setOnClickListener(this);
		mDetalShopCarRe = (RelativeLayout) this
				.findViewById(R.id.mDetailShopCarRe);
		mDetalShopCarRe.setOnClickListener(this);
		mJoinInShopCarBt = (Button) this.findViewById(R.id.mJoinInShopCar);
		mJoinInShopCarBt.setOnClickListener(this);
		mcoySnapPageLayout = (MySnapPageLayout) findViewById(R.id.flipLayout);

		topPage = new MyProductDetailInfoPage(DetailCleanFoodsActivity.this,
				getLayoutInflater().inflate(R.layout.detaifoods_mainmsg_layout,
						null));
		bottomPage = new MyProductContentPage(DetailCleanFoodsActivity.this,
				getLayoutInflater().inflate(
						R.layout.detalfoods_picanddesc_layout, null));
		mcoySnapPageLayout.setSnapPages(topPage, bottomPage);
		mDescShopPointRV = (RecyclerView) mcoySnapPageLayout
				.findViewById(R.id.detail_recyclerview_horizontal);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mDescShopPointRV.setLayoutManager(linearLayoutManager);
		if (mShopDesc.size() != 0) {
			GalleryAdapter adapter = new GalleryAdapter(
					getApplicationContext(), mShopDesc);
			mDescShopPointRV.setAdapter(adapter);
		} else {
			LogUtils.d("shoppoint暂无描述", "shoppoint暂无描述");
		}
		mDetailFoodsNameTv = (TextView) mcoySnapPageLayout
				.findViewById(R.id.detailpagefoodDescribTv);
		mDetailFoodsNameTv.setText(mDetailCleanFoodsName);
		mDetailFoodsPriceTv = (TextView) mcoySnapPageLayout
				.findViewById(R.id.detailfoodMoneyTv);
		mDetailFoodsPriceTv.setText("¥ " + mDetailCleanFoodsPrice);
		mDetailFoodsImgViewPager = (AutoScrollViewPager) mcoySnapPageLayout
				.findViewById(R.id.detailFoodsViewPanger);
		mDetailCirclePointInVPLL = (LinearLayout) mcoySnapPageLayout
				.findViewById(R.id.detailPicPoints);
		mDescFoodsByImgWordLV = (ListView) mcoySnapPageLayout
				.findViewById(R.id.describByImgAndWordLV);
		mFoodsDetailLV = (ListView) mcoySnapPageLayout
				.findViewById(R.id.detailFastFoodsMsgLV);
		mShopName = (TextView) mcoySnapPageLayout
				.findViewById(R.id.detailShopName);
		mShopName.setText(myApplication.getPointName());
		mShortDescTv = (TextView) mcoySnapPageLayout
				.findViewById(R.id.detailpagefoodshortDescTv);
		mShortDescTv.setText(mShortDesc);

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_DETAIL_MSG_OK:
				addImage();
				initPoints();
				initPoster();
				mWordAdapter = new DescribByWordAdapter(
						getApplicationContext(), mFastFoodsDetailList);
				mFoodsDetailLV.setAdapter(mWordAdapter);
				setListViewHeightBasedOnChildren(mFoodsDetailLV);
				// DescribByImageAdapter imageAdapter = new
				// DescribByImageAdapter(
				// getApplicationContext(),
				// mDetailFoodsEntity.getProimages());
				mDescFoodsByImgWordLV.setAdapter(new MyImageAdapter());
				break;

			default:
				break;
			}
		};
	};

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(10, 10);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getDetailFoodsMsg();
			}
		}).start();

		// mDetailFoodsImgViewPager.startAutoScroll();

	}

	private void addImage() {
		context = DetailCleanFoodsActivity.this;
		points = new LinkedList<ImageView>();
		posterImage = new LinkedList<String>();
		bitmapUtils = new BitmapUtils(context, null, 1024, 1024);
		for (int i = 0; i < allLunBoUrl.size(); i++) {
			posterImage.add(allLunBoUrl.get(i));
		}

	}

	// 联网获取数据
	private void getDetailFoodsMsg() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(
				BaseData.GET_DETAIL_FOODSURL + mProid).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String getProductDetailBody = response.body().string();
				LogUtils.e("商品详情---》》", getProductDetailBody);
				Gson gson = new Gson();
				mDetailFoodsEntity = gson.fromJson(getProductDetailBody,
						DetailFoodsEntity.class);
				allPicCount = mDetailFoodsEntity.getProimages().size();
				LogUtils.e("轮播图的数量", allPicCount + "");

				// for (int i = 0; i < allPicCount; i++) {
				// AllUrls = mDetailFoodsEntity.getProimages().get(i)
				// .getImgurl();
				// LogUtils.e("string 擦擦擦", AllUrls);
				// // allUrls = new String[] {
				// // mDetailFoodsEntity.getProimages()
				// // .get(i).getImgurl() };
				// bgUrls.add(AllUrls);
				// }
				for (int i = 0; i < allPicCount; i++) {
					if (mDetailFoodsEntity.getProimages().get(i)
							.getImgclassify().equals("1")) {
						LunBoUrls = mDetailFoodsEntity.getProimages().get(i)
								.getImgurl();
						allLunBoUrl.add(LunBoUrls);

					} else if (mDetailFoodsEntity.getProimages().get(i)
							.getImgclassify().equals("2")) {
						goodsBgUrls = mDetailFoodsEntity.getProimages().get(i)
								.getImgurl();
						allGoodsBgUrl.add(goodsBgUrls);

					}

				}

				lunBoPicCount = allLunBoUrl.size();
				descPicCount = allGoodsBgUrl.size();
				mFoodsDescribAll = mDetailFoodsEntity.getProattri();
				LogUtils.e("商品 详情 介绍--》》", mFoodsDescribAll);
				mFoodsDescrib = mFoodsDescribAll.split("[*]");
				mFastFoodsDetailList = new ArrayList<String>();
				Collections.addAll(mFastFoodsDetailList, mFoodsDescrib);
				LogUtils.e("dddd", mFoodsDescrib.length + "+");
				for (int i = 0; i < mFoodsDescrib.length; i++) {
					LogUtils.e("切割后的商品 数组 介绍--》》", mFoodsDescrib[i].toString());
				}
				Message msg = handler.obtainMessage(GET_DETAIL_MSG_OK);
				msg.sendToTarget();

			}
		});
	}

	// 监听事件
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.detailtopbackToParentBt:
			this.finish();
			break;
		case R.id.detailtopbacktomain:
			Intent inetent2MainPage = new Intent(getApplicationContext(),
					MainPageActivity.class);
			startActivity(inetent2MainPage);

			break;
		case R.id.mDetailShopCarRe:
			finish();
			Intent intent2ShopCar = new Intent(getApplicationContext(),
					NewShopCarActivity.class);
			startActivity(intent2ShopCar);

			break;
		case R.id.mJoinInShopCar:
			if (!numIsOk()) {
				return;
			}
			if (OtherMainPageActivity.rangers < 0) {
				showToastLong("超出配送范围");
				return;
			}
			if (stock == 0) {
				showToastLong("已售完，无法加入购物车");
				return;
			}
			Cursor c = db.rawQuery("select * from mycar WHERE name= ?",
					new String[] { mDetailCleanFoodsName });
			int m = 0;
			while (c.moveToNext()) {
				m++;
			}
			if (m != 0) {
				showToastLong("购物车中已经存在该商品");
				return;
			}
			Cursor cursor = db.rawQuery("select * from mycar", null);
			while (cursor.moveToNext()) {
				entity.add(new MyShopCarEntity(cursor.getString(1), cursor
						.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5), cursor
								.getString(6)));

			}
			if (entity.size() == 0) {
				ContentValues values = new ContentValues();
				values.put("name", mDetailCleanFoodsName);
				values.put("image", mCleanFoodsImageUrl);
				values.put("num", "1");
				values.put("price", mDetailCleanFoodsPrice);
				values.put("proid", mProid);
				values.put("istoday", isToday);
				db.insert("mycar", null, values);
				showToastLong("加入购物车成功");
			} else {
				for (int i = 0; i < entity.size(); i++) {
					isToday2 = entity.get(i).getIsToday().toString();
					LogUtils.e("详情界面数据库里面实体的内容---。。》》》", isToday2);
				}
				if (isToday2.equals(isToday)) {
					LogUtils.e("这里是今日快餐", "这里是今日快餐");
					ContentValues values = new ContentValues();
					values.put("name", mDetailCleanFoodsName);
					values.put("image", mCleanFoodsImageUrl);
					values.put("num", "1");
					values.put("price", mDetailCleanFoodsPrice);
					values.put("proid", mProid);
					values.put("istoday", isToday);
					db.insert("mycar", null, values);
					showToastLong("加入购物车成功");

				} else {
					showToastLong("购物车已经存在明天的商品，请先提交订单");
				}
			}

			break;

		default:
			break;
		}
	}

	private boolean numIsOk() {

		if (TextUtils.isEmpty(telNum)) {
			showToastLong("请登录");
			return false;
		}

		return true;
	}

	public DisplayMetrics getScreen(Activity activity) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics;
	}

	private void initPoster() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				(int) (getScreen(this).widthPixels));
		mDetailFoodsImgViewPager.setPageTransformer(true,
				new DepthPageTransformer());
		mDetailFoodsImgViewPager.setLayoutParams(params);

		mDetailFoodsImgViewPager.setAdapter(new PosterPagerAdapter());
		mDetailFoodsImgViewPager.setCurrentItem(lunBoPicCount * 500);
		mDetailFoodsImgViewPager.setInterval(interval);
		mDetailFoodsImgViewPager
				.setOnPageChangeListener(new PosterPageChange());
		mDetailFoodsImgViewPager
				.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
		mDetailFoodsImgViewPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mDetailFoodsImgViewPager.stopAutoScroll();

					break;
				case MotionEvent.ACTION_MOVE:
					mDetailFoodsImgViewPager.startAutoScroll();

					break;
				case MotionEvent.ACTION_UP:
					mDetailFoodsImgViewPager.startAutoScroll();

					break;

				default:
					break;
				}

				return false;
			}

		});
	}

	private class MyImageAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return allGoodsBgUrl.size();
		}

		@Override
		public Object getItem(int position) {
			return allGoodsBgUrl.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.layout_detaildescbypicitem, null);
				ViewHolder viewHolder = new ViewHolder(convertView);

				convertView.setTag(viewHolder);
			}

			initializeViews(allGoodsBgUrl.get(position),
					(ViewHolder) convertView.getTag());
			return convertView;
		}

		private void initializeViews(String string, ViewHolder tag) {
			// TODO Auto-generated method stub
			ImageLoader.getInstance().displayImage(string,
					tag.mDetailImageView, BaseActivity.options);
		}

		public class ViewHolder {

			private final ImageView mDetailImageView;

			public final View root;

			// http://192.168.1.106:8082/app/StationInfo?stcd=20161022

			public ViewHolder(View root) {

				mDetailImageView = (ImageView) root
						.findViewById(R.id.descriGoodsByIvAndWordIv);

				this.root = root;
			}
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

			for (int i = 0; i < lunBoPicCount; i++) {
				points.get(i).setBackgroundResource(R.drawable.feature_point);
			}
			points.get(position % lunBoPicCount).setBackgroundResource(
					R.drawable.feature_point_cur);

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
			mDetailFoodsImgViewPager.stopAutoScroll();
		}

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

			bitmapUtils.display(imageView,
					posterImage.get(position % lunBoPicCount));

			container.addView(imageView);

			imageView.setOnClickListener(new PosterClickListener(position
					% lunBoPicCount));
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

}
