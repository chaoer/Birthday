package com.chaoer.birthday;

import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MyShutDownReceiver extends BroadcastReceiver {

	public MyShutDownReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "Send Msg", Toast.LENGTH_LONG).show();
		saveShouDownInfo(context);
	}

	private void saveShouDownInfo(Context context) {
		SharedPreferences mySharePreferences = context.getApplicationContext()
				.getSharedPreferences("Bir_shut_down", Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象
		SharedPreferences.Editor editor = mySharePreferences.edit();
		Calendar cc = Calendar.getInstance();
		int year = cc.get(Calendar.YEAR);
		int month = cc.get(Calendar.MONTH) + 1;
		int day = cc.get(Calendar.DAY_OF_MONTH);
		int curHour = cc.get(Calendar.HOUR_OF_DAY);
		int curMinute = cc.get(Calendar.MINUTE);
		String msg = "ShutDown At: " + year + "." + month + "." + day + "--"
				+ curHour + ":" + curMinute;
		editor.putString("LAST_SHUT_DOWN", msg);
		editor.commit();
	}
}
