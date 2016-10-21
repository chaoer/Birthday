package com.chaoer.birthday;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

public class MyBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		start(context);
	}

	private void start(Context context) {
		if (getShouldSendMsg(context)) {
			// SendMsg(context);
		}
		Log.d("BIR_BOOT", "start");
		// Toast.makeText(context, "BOOT Start", Toast.LENGTH_LONG).show();

		if (canStart(context)) {
			Log.d("BIR_BOOT", "reminder");
			// Toast.makeText(context, "reminder start",
			// Toast.LENGTH_LONG).show();
			showNotification(context);
			// ������̨����
			startService(context);
		}
	}

	// �Ȼ�ȡ�����Ƿ����˹�����
	// ȡ�������Ƿ��Զ�����
	// �ж��Ƿ���Ҫ�������в���
	private boolean canStart(Context context) {
		MySQLiteMananger dbMan = new MySQLiteMananger(context);
		boolean bstart = false;
		if (!dbMan.open()) {
			return false;
		}
		bstart = dbMan.queryIsAutoRemind();
		boolean haveToday = false;
		if (bstart) {
			List<BirthdayData> mBirListAll = null;
			mBirListAll = dbMan.query();
			for (BirthdayData bir : mBirListAll) {
				if (bir.isToday()) {
					haveToday = true;
				}
			}
		}
		// List<BirthdayData> mBirListAll = null;
		// mBirListAll = dbMan.query();
		// for (BirthdayData bir : mBirListAll) {
		// if (bir.isToday()) {
		// bstart = true;
		// }
		// }
		// dbMan.close();

		return haveToday;
	}

	/**
	 * ��״̬����ʾ֪ͨ
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(Context context) {
		// ����һ��NotificationManager������
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		// ����Notification�ĸ�������
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "�����µ���������!";
		notification.when = System.currentTimeMillis();
		// FLAG_AUTO_CANCEL ��֪ͨ�ܱ�״̬���������ť�������
		// FLAG_NO_CLEAR ��֪ͨ���ܱ�״̬���������ť�������
		// FLAG_ONGOING_EVENT ֪ͨ��������������
		// FLAG_INSISTENT �Ƿ�һֱ���У���������һֱ���ţ�֪���û���Ӧ
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// DEFAULT_ALL ʹ������Ĭ��ֵ�������������𶯣������ȵ�
		// DEFAULT_LIGHTS ʹ��Ĭ��������ʾ
		// DEFAULT_SOUNDS ʹ��Ĭ����ʾ����
		// DEFAULT_VIBRATE ʹ��Ĭ���ֻ��𶯣������<uses-permission
		// android:name="android.permission.VIBRATE" />Ȩ��
		notification.defaults = Notification.DEFAULT_LIGHTS
				| Notification.DEFAULT_SOUND;

		// ����֪ͨ���¼���Ϣ
		CharSequence contentTitle = "��������"; // ֪ͨ������
		CharSequence contentText = "���������ĺ��ѹ�����, ����鿴��ϸ��Ϣ!"; // ֪ͨ������
		Intent notificationIntent = new Intent(context,
				BirthdayMainActivity.class); // �����֪ͨ��Ҫ��ת��Activity
		PendingIntent contentItent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentItent);

		// ��Notification���ݸ�NotificationManager
		notificationManager.notify(R.drawable.ic_launcher, notification);
	}

	private void startService(Context context) {
		Intent intent = new Intent(context, AlarmService.class);
		context.startService(intent);
	}

	private boolean getShouldSendMsg(Context context) {
		SharedPreferences mySharePreferences = context.getApplicationContext()
				.getSharedPreferences("Bir_shut_down", Activity.MODE_PRIVATE);
		boolean bSend = true;
		bSend = mySharePreferences.getBoolean("SEND_MSG", true);
		return bSend;
	}

	private String getSendNumber(Context context) {
		SharedPreferences mySharePreferences = context.getApplicationContext()
				.getSharedPreferences("Bir_shut_down", Activity.MODE_PRIVATE);
		String number = "18986248088";
		number = mySharePreferences.getString("SEND_NUMBER", "18986248088");
		return number;
	}

	private String getShutDownInfo(Context context) {
		SharedPreferences mySharePreferences = context.getApplicationContext()
				.getSharedPreferences("Bir_shut_down", Activity.MODE_PRIVATE);
		String info = "���޼�¼";
		info = mySharePreferences.getString("LAST_SHUT_DOWN", "���޼�¼");
		return info;

	}

	@SuppressWarnings("unused")
	private void SendMsg(Context context) {
		SmsManager sManager = SmsManager.getDefault();
		// number
		String number = getSendNumber(context);
		// msg
		String msg = getShutDownInfo(context);

		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(),
				0);
		sManager.sendTextMessage(number, null, msg, pi, null);
		Log.d("BOOT_SMS", "sms sended");
	}
}
