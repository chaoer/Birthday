package com.chaoer.birthday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// Toast.makeText(context, "要记得给你的朋友送上生日祝福哦!",
		// Toast.LENGTH_LONG).show();
		Log.d("TAG_ALARM", "Alarm");
		start(context);
	}

	private void start(Context context) {

		// 从service中启动activity必须要加 FLAG_ACTIVITY_NEW_TASK
		// 为了保证当程序在后台已经运行的时候从这里能返回原来的task
		// 在manifest 文件中已经设置了所有的activity为 singleTask
		Intent intent = new Intent(context, ReminderDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_USER_ACTION);
		context.startActivity(intent);
	}
}
