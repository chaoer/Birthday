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
			// 开启后台服务
			startService(context);
		}
	}

	// 先获取今天是否有人过生日
	// 取消并且是否自动提醒
	// 判断是否需要继续经行操作
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
	 * 在状态栏显示通知
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(Context context) {
		// 创建一个NotificationManager的引用
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		// 定义Notification的各种属性
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "您有新的生日提醒!";
		notification.when = System.currentTimeMillis();
		// FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉
		// FLAG_NO_CLEAR 该通知不能被状态栏的清除按钮给清除掉
		// FLAG_ONGOING_EVENT 通知放置在正在运行
		// FLAG_INSISTENT 是否一直进行，比如音乐一直播放，知道用户响应
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// DEFAULT_ALL 使用所有默认值，比如声音，震动，闪屏等等
		// DEFAULT_LIGHTS 使用默认闪光提示
		// DEFAULT_SOUNDS 使用默认提示声音
		// DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission
		// android:name="android.permission.VIBRATE" />权限
		notification.defaults = Notification.DEFAULT_LIGHTS
				| Notification.DEFAULT_SOUND;

		// 设置通知的事件消息
		CharSequence contentTitle = "生日提醒"; // 通知栏标题
		CharSequence contentText = "今天有您的好友过生日, 点击查看详细信息!"; // 通知栏内容
		Intent notificationIntent = new Intent(context,
				BirthdayMainActivity.class); // 点击该通知后要跳转的Activity
		PendingIntent contentItent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentItent);

		// 把Notification传递给NotificationManager
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
		String info = "暂无记录";
		info = mySharePreferences.getString("LAST_SHUT_DOWN", "暂无记录");
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
