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
		// ������յ�����㲥����ô����service ����
		Log.d("SERVICE_RESTARTED", "����������");
		Intent intentService = new Intent(context, AlarmService.class);
		context.startService(intentService);
	}

}
