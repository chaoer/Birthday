package com.chaoer.birthday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// Toast.makeText(context, "Ҫ�ǵø����������������ף��Ŷ!",
		// Toast.LENGTH_LONG).show();
		Log.d("TAG_ALARM", "Alarm");
		start(context);
	}

	private void start(Context context) {

		// ��service������activity����Ҫ�� FLAG_ACTIVITY_NEW_TASK
		// Ϊ�˱�֤�������ں�̨�Ѿ����е�ʱ��������ܷ���ԭ����task
		// ��manifest �ļ����Ѿ����������е�activityΪ singleTask
		Intent intent = new Intent(context, ReminderDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_USER_ACTION);
		context.startActivity(intent);
	}
}
