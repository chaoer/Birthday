package com.chaoer.birthday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyServiceClosedReceiver extends BroadcastReceiver {

	public MyServiceClosedReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 如果接收到这个广播，那么重启service 服务
		Log.d("SERVICE_RESTARTED", "服务重启了");
		Intent intentService = new Intent(context, AlarmService.class);
		context.startService(intentService);
	}

}
