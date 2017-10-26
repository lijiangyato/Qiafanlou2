package com.ings.gogo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.interfacep.OnTabActivityResultListener;
import com.ings.gogo.utils.LogUtils;
import com.ings.gogo.view.NoScrollViewPager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class MainPageActivity extends BaseActivity implements OnClickListener {
	// 是否获取cookie的标识
	private final int GET_COOKIE_OK = 1;
	private Context context = null;
	@SuppressWarnings("deprecation")
	private LocalActivityManager manager = null;
	private NoScrollViewPager pager = null;
	// 三个主界面 RelativeLayout
	private RelativeLayout t1, t2, t3;
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ImageView cursor;// 动画图片
	private String tem1;
	private String tem2;
	private String jiequ;
	private String jiequ2;
	public String aspnetauth;
	public String ASP_NET_SessionId;
	private UILApplication myApplication;
	private String username;
	private String password;
	private ArrayList<View> list = null;
	public static SharedPreferences preferences = null;

	public static SharedPreferences.Editor editor = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_mainpage);
		myApplication = (UILApplication) getApplication();
		context = MainPageActivity.this;
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		preferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
		editor = preferences.edit();// 获取编辑器
		initViews();
		InitImageView();
		initPagerViewer();
	}

	private void initViews() {

		t1 = (RelativeLayout) findViewById(R.id.mMainPageRe);
		t2 = (RelativeLayout) findViewById(R.id.mShopCarRe);
		t3 = (RelativeLayout) findViewById(R.id.mPersonalRe);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));

	}

	public MainPageActivity(SharedPreferences preferences, Editor editor) {
		super();
		this.preferences = preferences;
		this.editor = editor;
	}

	public MainPageActivity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void clearData() {

		editor.clear();
		editor.commit();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.e("onResume", "主界面中的onResume");
		manager.dispatchResume();
		if (pager != null) {
			switch (pager.getCurrentItem()) {

			case 0:
				Activity person_activity = manager
						.getActivity(" PersonalActivity");
				if (person_activity != null
						&& person_activity instanceof PersonalActivity) {
					((PersonalActivity) person_activity).invisibleOnScreen();
				}
				Activity other_activity = manager
						.getActivity("OtherMainPageActivity");
				if (other_activity != null
						&& other_activity instanceof OtherMainPageActivity) {
					((OtherMainPageActivity) other_activity)
							.invisibleOnScreen();
					((OtherMainPageActivity) other_activity).goneOnScreen();
				}
				Activity shopCar_activity = manager
						.getActivity("ShopCarActivity");
				if (shopCar_activity != null
						&& shopCar_activity instanceof ShopCarActivity) {
					((ShopCarActivity) shopCar_activity).invisibleOnScreen();
					((ShopCarActivity) shopCar_activity).goneOnScreen();
				}

				break;

			default:
				break;
			}
		}

	}

	/**
	 * 初始化PageViewer
	 */
	@SuppressWarnings("deprecation")
	private void initPagerViewer() {
		pager = (NoScrollViewPager) findViewById(R.id.viewpage);
		pager.setOffscreenPageLimit(0);
		list = new ArrayList<View>();
		Intent intent = new Intent(context, OtherMainPageActivity.class);
		list.add(getView("OtherMainPageActivity", intent));

		username = preferences.getString("username", "");

		password = preferences.getString("password", "");

		// 如果不是第一次登陆
		myApplication.setTelNum(username);
		if (username != null && password != null

		&& !"".equals(username) && !"".equals(password)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					login(username, password);
				}
			}).start();

			// LoginActivity activity = new LoginActivity();
			// activity.login(username, password);
		} else {
			Intent intent2 = new Intent(context, ShopCarActivity.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			list.add(getView("ShopCarActivity", intent2));
			Intent intent4 = new Intent(context, LoginActivity.class);
			list.add(getView("LoginActivity", intent4));
		}
		pager.setAdapter(new MyPagerAdapter(list));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 自己写的onActivityResult方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		@SuppressWarnings("deprecation")
		OtherMainPageActivity activity_t3 = (OtherMainPageActivity) manager
				.getActivity("OtherMainPageActivity");
		if (activity_t3 instanceof OnTabActivityResultListener) {
			// 获取返回值接口实例
			OnTabActivityResultListener listener = (OnTabActivityResultListener) activity_t3;
			// 转发请求到子Activity
			activity_t3.onTabActivityResult(requestCode, resultCode, data);
		}

	}

	/**
	 * 自动登录
	 */
	private void login(String username, String password) {
		// TODO Auto-generated method stub
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new FormEncodingBuilder()
				.add("phone", username.trim()).add("loginkey", password.trim())
				.build();

		Request request = new Request.Builder().url(BaseData.LOGIN_URL)
				.post(requestBody).build();
		Response response = null;

		try {
			response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String loginBody = response.body().string();
				LogUtils.e("自动登录 -》》ttit811 ", loginBody);
				String loginhead = response.headers().toString();
				// LogUtils.e("自动登录--》》 请求头", loginhead);
				tem1 = response.headers().value(3).toString();// asp.net

				tem2 = response.headers().value(6).toString();// aspnetauth

				jiequ = tem1.substring(tem1.indexOf("ASP.NET_SessionId="),
						tem1.indexOf("; path=/;"));
				ASP_NET_SessionId = jiequ;
				jiequ2 = tem2.substring(tem2.indexOf("aspnetauth="),
						tem2.indexOf("; path=/;"));
				aspnetauth = jiequ2;
				myApplication.setASP_NET_SessionId(ASP_NET_SessionId);
				myApplication.setAspnetauth(aspnetauth);
				LogUtils.e("ASP_NET_SessionId--》》", ASP_NET_SessionId);
				LogUtils.e("aspnetauth--》》", aspnetauth);
				Message msg = handler.obtainMessage(GET_COOKIE_OK);
				msg.sendToTarget();

			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_COOKIE_OK:
				Intent intent2 = new Intent(context, ShopCarActivity.class);
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				list.add(getView("ShopCarActivity", intent2));
				Intent intent3 = new Intent(context, PersonalActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("telNum", username.toString());
				intent3.putExtras(bundle);
				list.add(getView("PersonalActivity", intent3));
				pager.setAdapter(new MyPagerAdapter(list));
				pager.setCurrentItem(0);
				pager.setOnPageChangeListener(new MyOnPageChangeListener());
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
		case R.id.mMainPageRe:
			Intent intent2Main = new Intent(context,
					OtherMainPageActivity.class);
			startActivity(intent2Main);

			break;
		case R.id.mShopCarRe:
			Intent intent2Car = new Intent(context, ShopCarActivity.class);
			startActivity(intent2Car);

			break;
		case R.id.mPersonalRe:
			Intent intent2Person = new Intent(context, PersonalActivity.class);
			startActivity(intent2Person);

			break;

		default:
			break;
		}

	};

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor1);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.roller)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 通过activity获取视图
	 * 
	 * @param id
	 * @param intent
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	/**
	 * Pager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;

			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;

		}

		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);
		}
	}

}
