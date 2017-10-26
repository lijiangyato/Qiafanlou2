package com.ings.gogo.base;

import android.app.Application;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		/**
		 * 整个程序 只需要 一个 ，从这里初始化 需要 Context 尤其 第三方
		 */

	}
}
