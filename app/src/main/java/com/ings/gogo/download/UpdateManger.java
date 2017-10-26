package com.ings.gogo.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ings.gogo.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class UpdateManger {

	// Ӧ�ó���Context
	private Context mContext;
	// ��ʾ��Ϣ
	private String updateMsg = "�����µ�������������أ�";
	// ���ذ�װ��������·��
	private String apkUrl = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";
	private Dialog noticeDialog;// ��ʾ��������µĶԻ���
	private Dialog downloadDialog;// ���ضԻ���
	@SuppressLint("SdCardPath")
	private static final String savePath = "/sdcard/updatedemo/";// ����apk���ļ���
	private static final String saveFileName = savePath
			+ "UpdateDemoRelease.apk";

	// ��������֪ͨUIˢ�µ�handler��msg����
	private ProgressBar mProgress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private int progress;// ��ǰ����
	private Thread downLoadThread; // �����߳�
	private boolean interceptFlag = false;// �û�ȡ������
	// ֪ͨ����ˢ�½����handler
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
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

	public UpdateManger(Context context) {
		this.mContext = context;
	}

	/**
	 * ��ʾ���³���Ի��򣬹����������
	 */
	public void checkUpdateInfo() {
		showNoticeDialog();
	}

	private void showNoticeDialog() {
		// Builder������ͨ����builder���øı�AleartDialog��Ĭ�ϵ�������ʽ�����������Ϣ
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				mContext);
		builder.setTitle("����汾����");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("����", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();// ��ȡ���Ի������в���һ���Ĵ��룿ȡ���Ի���
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("�Ժ���˵", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	@SuppressLint("InflateParams")
	protected void showDownloadDialog() {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				mContext);
		builder.setTitle("����汾����");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		builder.setView(v);// ���öԻ��������Ϊһ��View
		builder.setNegativeButton("ȡ��", new OnClickListener() {
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
		mContext.startActivity(i);
	}

	/**
	 * ����apk
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			URL url;
			try {
				url = new URL(apkUrl);
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
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// �������֪ͨ��װ
						mHandler.sendEmptyMessage(DOWN_OVER);
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
}
