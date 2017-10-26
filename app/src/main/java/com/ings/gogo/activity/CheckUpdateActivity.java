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
import com.ings.gogo.entity.UpdateAppEntity;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CheckUpdateActivity extends BaseActivity implements
		OnClickListener {
	private static final int OK = 1;
	private static final int DOWN_UPDATE = 2;
	private static final int DOWN_OVER = 3;
	private UILApplication myApplication;
	private UpdateAppEntity mUpdateEntity;
	private Dialog myDialog;
	private Context context;
	private ListView mUpdateTips;
	private Button cancelUpdate;
	private Button confirmUpdate;
	private TextView mUpdateTitle;
	private DescribByWordAdapter mUpdateAdapter;
	private String[] mUpdateDescrib;
	private String mUpdateMsg;
	private List<String> mUpdateDetailList = null;
	private String versionName;
	private int versioncode;
	private Dialog downloadDialog;// ���ضԻ���
	@SuppressLint("SdCardPath")
	private static final String savePath = "/sdcard/gogo/";// ����apk���ļ���
	private static final String saveFileName = savePath + "go go.apk";
	// ��������֪ͨUIˢ�µ�handler��msg����
	private ProgressBar mProgress;
	private int progress;// ��ǰ����
	private Thread downLoadThread; // �����߳�
	private boolean interceptFlag = false;// �û�ȡ������
	private View dialogLayout;
	private LayoutInflater inflater;
	private View downLoadView;
	private String mGetVirsionNo;
	private TextView mCheckBack;
	private TextView mCheckNowID;
	private Button mCheckUpdateBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_checkupdate);
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
		LogUtils.e("��ǰ�İ汾��Ϣ", "�汾��" + versioncode + "�汾��" + versionName);
		myApplication = (UILApplication) getApplication();
		context = this.getApplicationContext();
		inflater = LayoutInflater.from(context);
		downLoadView = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) downLoadView.findViewById(R.id.progress);
		initViews();

	}

	private void initViews() {
		// TODO Auto-generated method stub
		mCheckBack = (TextView) this
				.findViewById(R.id.checkupdate_backToParent);
		mCheckBack.setOnClickListener(this);
		mCheckNowID = (TextView) this
				.findViewById(R.id.checkupdate_nowversionid);
		mCheckNowID.setHint("�汾�ţ�" + versionName);
		mCheckUpdateBt = (Button) this.findViewById(R.id.checkupdate_checkBt);
		mCheckUpdateBt.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.checkupdate_backToParent:
			finish();

			break;
		case R.id.checkupdate_checkBt:
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					getUpdateData();
				}
			}).start();
			break;

		default:
			break;
		}
	}

	// ֪ͨ����ˢ�½����handler
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

				} else if (mUpdateEntity.getSuccess().equals(true)
						&& mUpdateEntity.getData().getVersiondigit() == versioncode) {
					showToastLong("��ǰ�Ѿ������°汾");
				} else if (mUpdateEntity.getSuccess().equals(false)) {
					showToastLong("��ǰ�Ѿ������°汾");
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
		myDialog.show();
		innitDiaLog();
	}

	protected void innitDiaLog() {
		// TODO Auto-generated method stub
		mUpdateTips = (ListView) dialogLayout.findViewById(R.id.update_tipslv);
		mUpdateTips.setDivider(null);
		mUpdateTips.setDividerHeight(0);
		mUpdateAdapter = new DescribByWordAdapter(getApplicationContext(),
				mUpdateDetailList);
		mUpdateTips.setAdapter(mUpdateAdapter);
		mUpdateTitle = (TextView) dialogLayout.findViewById(R.id.update_title);
		mUpdateTitle.setText("�����°汾" + "(" + mGetVirsionNo + ")");
		cancelUpdate = (Button) dialogLayout.findViewById(R.id.update_cancel);
		cancelUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myDialog.dismiss();

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

	protected void showDownloadDialog() {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				this);
		builder.setTitle("����汾����");
		builder.setView(downLoadView);// ���öԻ��������Ϊһ��View
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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

	/**
	 * ����apk
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
					// ���ؽ���
					// handler.sendEmptyMessage();
					Message msg = mHandler.obtainMessage(DOWN_UPDATE);
					msg.sendToTarget();
					if (numread <= 0) {
						// �������֪ͨ��װ
						Message msg1 = mHandler.obtainMessage(DOWN_OVER);
						msg1.sendToTarget();
						// handler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					outStream.write(buf, 0, numread);
				} while (!interceptFlag);// ���ȡ��ֹͣ����
				outStream.close();
				ins.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * ��װapk
	 */
	protected void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		// File.toString()�᷵��·����Ϣ
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

	protected void getUpdateData() {
		// TODO Auto-generated method stub
		// ����okHttpClient����
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// ����һ��Request
		final Request request = new Request.Builder().url(BaseData.APP_UPDATE)
				.build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// ����������
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String updateBody = response.body().string();
				LogUtils.e("App���µ�body--->>", updateBody);
				Gson gson = new Gson();
				mUpdateEntity = gson
						.fromJson(updateBody, UpdateAppEntity.class);

				Message msg = mHandler.obtainMessage(OK);
				msg.sendToTarget();

			}

		});
	}

}
