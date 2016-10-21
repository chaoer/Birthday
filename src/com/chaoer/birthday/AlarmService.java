package com.chaoer.birthday;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class AlarmService extends Service {
	private static String mStrReminderTime = "12:00";
	private boolean mTimerAlreadyRun = false;
	private Timer mTimer = null;
	private TimerTask mTimerTask = null;
	private Handler mHandler = null;
	private final static int MESS_START_WORK = 772;
	private boolean mStartWork = false;
	private Context mContext = null;
	private int mWorkType = 0;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("BIR_SERVICE", "onCreate");
		mContext = this.getApplicationContext();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d("BIR_SERVICE", "onDestroy");
		mStartWork = false;
		stopTimer();
		// �������Ҫ���������Ļ�����ô�ͷ���һ���㲥
		if (mWorkType == 1) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			intent.setAction(getResources().getString(
					R.string.broadcast_bir_reminder_not_kill));
			sendBroadcast(intent);
//			Log.d("SERVICE_RESTARTED", "����������");
//			Intent intentService = new Intent(mContext, AlarmService.class);
//			mContext.startService(intentService);
		}
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("BIR_SERVICE", "onStart");
		mStartWork = true;
		mWorkType = getWorkType();

		// ����Ѿ��������ˣ���ô��ֹͣ
		if (mTimerAlreadyRun) {
			stopTimer();
		}
		startWork();
		super.onStart(intent, startId);
	}

	// @Override
	// public int onStartCommand(Intent intent, int flags, int startId) {
	// // TODO Auto-generated method stub
	// Log.d("BIR_SERVICE", "onStartCommand");
	//
	// flags = START_STICKY;
	// return super.onStartCommand(intent, flags, startId);
	// }
	private int getWorkType() {
		return BirthdayMainActivity.mReminderWorkType;
	}

	private void getReminderTime() {
		MySQLiteMananger dbMan = new MySQLiteMananger(mContext);
		if (dbMan.open()) {
			mStrReminderTime = dbMan.queryRemindTime();
		}
		dbMan.close();
	}

	private void startTimer() {
		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mStartWork) {
						int timeSpan = whichTimeSpan(mStrReminderTime);
						// Log.d("BIR_SERVICE", "��������!");
						// ʱ���Ѿ����ˣ���ôֱ�ӵ��� stop
						if (timeSpan == 1) {
							Log.d("BIR_SERVICE", "ʱ�����,ֹͣ!");
							// ��ʱ�Ѿ�������ȫdestory��
							mWorkType = 0;
							onDestroy();
						} else if (timeSpan == 0) {
							Log.d("BIR_SERVICE", "ʱ�䵽����������!");
							Message msg = new Message();
							msg.what = MESS_START_WORK;
							mHandler.sendMessage(msg);
							// ��ʱ�Ѿ�������ȫdestory��
							mWorkType = 0;
							onDestroy();
						}
					}
				}
			};
		}
		if (mTimer == null) {
			mTimer = new Timer();
		}
		if (mTimerTask != null && mTimer != null) {
			mTimer.schedule(mTimerTask, 0, 1000); // 1s ִ��һ��
			mTimerAlreadyRun = true;
		}
	}

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	/**
	 * �ж�strTime�������ʱ�� �� ϵͳʱ�� ��Թ�ϵ
	 * 
	 * @param strTime
	 * @return -1 û�й� �� 0 ��ȣ� 1 ����
	 */
	private int whichTimeSpan(String strTime) {
		String[] split = strTime.split("-");
		if (split == null || split.length != 2) {
			return 1;
		}
		int hourOfDay = Integer.valueOf(split[0]).intValue();
		int minute = Integer.valueOf(split[1]).intValue();
		Calendar cc = Calendar.getInstance();
		int curHour = cc.get(Calendar.HOUR_OF_DAY);
		int curMinute = cc.get(Calendar.MINUTE);
		Log.d("BIR_SERVICE", "����ʱ��:" + strTime);
		Log.d("BIR_SERVICE", "��ǰʱ��:" + curHour + "-" + curMinute);
		if (hourOfDay < curHour || (hourOfDay == curHour && minute < curMinute)) {
			// ����Ѿ����˼�¼���õ�ʱ�䣬��ô�������Զ�����
			return 1;
		} else if (hourOfDay == curHour && minute == curMinute) {
			return 0;
		} else {
			return -1;
		}
	}

	@SuppressLint("HandlerLeak")
	private void startWork() {
		// ��ȡ���ѵ�ʱ��
		getReminderTime();
		// ��Ϣ����
		if (mHandler == null) {
			mHandler = new Handler() {
				@SuppressLint("HandlerLeak")
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case MESS_START_WORK:
						Intent intent = new Intent(mContext,
								ReminderDialogActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
						mContext.startActivity(intent);
						break;
					}
				}
			};
		}
		// ��ʼ��ʱ������
		startTimer();
	}
}
