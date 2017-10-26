/*******************************************************************************
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ings.gogo.application;

import java.io.File;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.LinkedList;
import java.util.List;

import com.ings.gogo.activity.WelcomActivity;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.okhttp.OkHttpClient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

public class UILApplication extends Application {
	private static UILApplication instance;
	private String aspnetauth;
	private String ASP_NET_SessionId;
	private String telNum;
	private String pointID;
	private String pointName;
	private String jingDu;
	private String weiDu;
	private String categoryClean;
	private String categoryFast;
	private String ranges;
	private List<Activity> mList = new LinkedList<Activity>();

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		instance = this;
		if (Constants.Config.DEVELOPER_MODE
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}

		super.onCreate();
		File cacheDir = StorageUtils.getCacheDirectory(this); // �����ļ���·��
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(3).denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiskCache(cacheDir)) // default �Զ��建��·��
				.build();
		ImageLoader.getInstance().init(config);

		initImageLoader(getApplicationContext());
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public static UILApplication getInstance() {
		return instance;
	}

	public String getPointID() {
		return pointID;
	}

	public void setPointID(String pointID) {
		this.pointID = pointID;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getAspnetauth() {
		return aspnetauth;
	}

	public void setAspnetauth(String aspnetauth) {
		this.aspnetauth = aspnetauth;
	}

	public String getASP_NET_SessionId() {
		return ASP_NET_SessionId;
	}

	public void setASP_NET_SessionId(String aSP_NET_SessionId) {
		ASP_NET_SessionId = aSP_NET_SessionId;
	}

	public String getJingDu() {
		return jingDu;
	}

	public void setJingDu(String jingDu) {
		this.jingDu = jingDu;
	}

	public String getWeiDu() {
		return weiDu;
	}

	public void setWeiDu(String weiDu) {
		this.weiDu = weiDu;
	}

	public String getCategoryClean() {
		return categoryClean;
	}

	public void setCategoryClean(String categoryClean) {
		this.categoryClean = categoryClean;
	}

	public String getCategoryFast() {
		return categoryFast;
	}

	public void setCategoryFast(String categoryFast) {
		this.categoryFast = categoryFast;
	}

	public String getRanges() {
		return ranges;
	}

	public void setRanges(String ranges) {
		this.ranges = ranges;
	}

	public static void initImageLoader(Context context) {

		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app
		ImageLoader.getInstance().init(config.build());
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true);// �������ص�ͼƬ�Ƿ񻺴����ڴ���
		builder.cacheOnDisk(true);// �������ص�ͼƬ�Ƿ񻺴���SD����

	}
}