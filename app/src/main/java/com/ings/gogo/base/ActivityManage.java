package com.ings.gogo.base;

import java.util.Stack;

import android.app.Activity;

public class ActivityManage {
	private static volatile ActivityManage instance;
	private Stack<Activity> mActivityStack = new Stack<Activity>();

	private ActivityManage() {

	}

	public static ActivityManage getInstance() {
		if (instance == null) {
			synchronized (ActivityManage.class) {
				if (instance == null) {
					instance = new ActivityManage();
				}
			}

		}
		return instance;
	}

	public void addActicity(Activity act) {
		mActivityStack.push(act);
	}

	public void removeActivity(Activity act) {
		mActivityStack.remove(act);
	}

	public void killMyProcess() {
		int nCount = mActivityStack.size();
		for (int i = nCount - 1; i >= 0; i--) {
			Activity activity = mActivityStack.get(i);
			activity.finish();
		}

		mActivityStack.clear();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
