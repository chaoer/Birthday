package com.chaoer.common.tools;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class CommonTools {
	public static boolean isAppOnForeground(Context context) {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAppOnBackground(Context context) {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
				return true;
			}
		}
		return false;
	}
}
