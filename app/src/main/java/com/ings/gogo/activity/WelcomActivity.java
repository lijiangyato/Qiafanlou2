package com.ings.gogo.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.adapter.DescribByWordAdapter;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.entity.GoodsCategaryEntity;
import com.ings.gogo.entity.UpdateAppEntity;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WelcomActivity extends BaseActivity {
	// 获取更新数据的标识
	private static final int OK = 1;
	//
	private static final int DOWN_UPDATE = 2;
	//
	private static final int DOWN_OVER = 3;
	private ImageView mWelComeBgImage;
	public static boolean isNeting = true;
	// 分类
	private String categoryFast;
	private String categoryClean;
	private UILApplication myApplication;
	private UpdateAppEntity mUpdateEntity;
	// 更新提示
	private Dialog myDialog;
	private Context context;
	// 更新内容的listView
	private ListView mUpdateTips;
	// 取消更新
	private Button cancelUpdate;
	// 确认更新
	private Button confirmUpdate;
	// 显示当前版本的相关信息
	private TextView mUpdateTitle;
	// 跟新信息的适配器
	private DescribByWordAdapter mUpdateAdapter;
	// 对*进行 分割后的内容
	private String[] mUpdateDescrib;
	// 所有的更新信息
	private String mUpdateMsg;
	// 更新数组 放入 String集合中
	private List<String> mUpdateDetailList = null;
	// Android 版本名 int型 只能增加 不能减少
	private String versionName;
	// 版本码 float
	private int versioncode;
	// 弹出 下载apk的对话框
	private Dialog downloadDialog;
	// 保存apk的文件夹
	@SuppressLint("SdCardPath")
	private static final String savePath = "/sdcard/gogo/";
	// 保存的apk的名字
	private static final String saveFileName = savePath + "go go.apk";
	// 进度条与通知UI刷新的handler和msg常量
	private ProgressBar mProgress;
	private int progress;// 当前进度
	private Thread downLoadThread; // 下载线程
	private boolean interceptFlag = false;// 用户取消下载
	// 提醒 更新的布局
	private View dialogLayout;
	// 渲染 布局
	private LayoutInflater inflater;
	// 下载apk的布局文件
	private View downLoadView;
	// 获取 服务器放回的版本号
	private String mGetVirsionNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcom);
		PackageManager pm = this.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(this.getPackageName(), 0);
			versionName = pi.versionName;
			versioncode = pi.versionCode;
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogUtils.e("当前的版本信息", "版本号" + versioncode + "版本名" + versionName);
		myApplication = (UILApplication) getApplication();
		mWelComeBgImage = (ImageView) this.findViewById(R.id.welcomeBgIv);
		context = this.getApplicationContext();
		inflater = LayoutInflater.from(context);
		downLoadView = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) downLoadView.findViewById(R.id.progress);

	}

	// 通知处理刷新界面的handler
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OK:
				if (mUpdateEntity.getSuccess().equals(true)
						&& mUpdateEntity.getData().getVersiondigit() > versioncode) {
					mGetVirsionNo = mUpdateEntity.getData().getVersionno();
					mUpdateMsg = mUpdateEntity.getData().getMark();
					mUpdateDescrib = mUpdateMsg.split("[*]");
					mUpdateDetailList = new ArrayList<String>();
					Collections.addAll(mUpdateDetailList, mUpdateDescrib);
					showMyDialog();

				} else {
					Intent intent = new Intent(getApplicationContext(),
							MainPageActivity.class);
					startActivity(intent);// 第二个参数为requestCode 请求码
				}
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			}
			super.handleMessage(msg);
		}
	};

	protected void showMyDialog() {
		// TODO Auto-generated method stub
		myDialog = new Dialog(this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLayout = View.inflate(this, R.layout.layout_updatetips, null);
		myDialog.setContentView(dialogLayout);
		// myDialog.setTitle(R.string.app_update);
		myDialog.show();
		innitDiaLog();
	}

	/**
	 * 初始化提示下载的弹框
	 */
	protected void innitDiaLog() {
		// TODO Auto-generated method stub
		mUpdateTips = (ListView) dialogLayout.findViewById(R.id.update_tipslv);
		mUpdateTips.setDivider(null);
		mUpdateTips.setDividerHeight(0);
		mUpdateAdapter = new DescribByWordAdapter(getApplicationContext(),
				mUpdateDetailList);
		mUpdateTips.setAdapter(mUpdateAdapter);
		mUpdateTitle = (TextView) dialogLayout.findViewById(R.id.update_title);
		mUpdateTitle.setText("发现新版本" + "(" + mGetVirsionNo + ")");
		cancelUpdate = (Button) dialogLayout.findViewById(R.id.update_cancel);
		cancelUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
				Intent intent = new Intent(getApplicationContext(),
						MainPageActivity.class);
				startActivity(intent);// 第二个参数为requestCode 请求码
			}
		});
		confirmUpdate = (Button) dialogLayout.findViewById(R.id.update_ok);
		confirmUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDownloadDialog();
				myDialog.dismiss();

			}
		});

	}

	/**
	 * 显示下载弹框
	 */
	protected void showDownloadDialog() {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				this);
		builder.setTitle("版本更新");
		builder.setView(downLoadView);// 设置对话框的内容为一个View
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();

	}

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ConnectivityManager con = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		@SuppressWarnings("deprecation")
		boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();

		if (wifi | internet) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					SystemClock.sleep(2000);
					getUpdateData();
					getGoodsCategary();

				}
			}).start();

		} else {
			showToastLong("亲，您的网络连了么？");
		}

	}

	/**
	 * 下载apk
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			URL url;
			try {
				url = new URL(mUpdateEntity.getData().getDownload());
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream ins = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream outStream = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = ins.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 下载进度
					// handler.sendEmptyMessage();
					Message msg = mHandler.obtainMessage(DOWN_UPDATE);
					msg.sendToTarget();
					if (numread <= 0) {
						// 下载完成通知安装
						Message msg1 = mHandler.obtainMessage(DOWN_OVER);
						msg1.sendToTarget();
						// handler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					outStream.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消停止下载
				outStream.close();
				ins.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 安装apk
	 */
	protected void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		// File.toString()会返回路径信息
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

	/**
	 * 获取是否需要更新的数据
	 */
	protected void getUpdateData() {
		// TODO Auto-generated method stub
		// 创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(BaseData.APP_UPDATE)
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
				String updateBody = response.body().string();
				LogUtils.e("App更新的body--->>", updateBody);
				Gson gson = new Gson();
				mUpdateEntity = gson
						.fromJson(updateBody, UpdateAppEntity.class);

				Message msg = mHandler.obtainMessage(OK);
				msg.sendToTarget();

			}

		});
	}

	/**
	 * 获取goods分类信息
	 */
	protected void getGoodsCategary() {
		// 创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(
				BaseData.GET_GOODS_GETCATEGORY).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String categoryBody = response.body().string();
				LogUtils.e("categoryBody--->>", categoryBody);
				Gson gson = new Gson();
				GoodsCategaryEntity categoryEntitys = gson.fromJson(
						categoryBody, GoodsCategaryEntity.class);
				categoryFast = categoryEntitys.getData().get(0).getCategoryid();
				categoryClean = categoryEntitys.getData().get(1)
						.getCategoryid();
				myApplication.setCategoryClean(categoryFast);
				myApplication.setCategoryClean(categoryClean);

			}

		});
	}

}
