package com.chaoer.birthday;

import java.io.File;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BirthdayMainActivity extends Activity {
	// ����
	private MySQLiteMananger mBirDBManager = null;
	private List<BirthdayData> mBirListAll = null;
	private List<BirthdayData> mBirListRecent = null;
	private List<BirthdayData> mBirListToday = null;
	public static Context mContextInstance = null;
	private boolean mbFirst = true;
	private int mLastAction = 0; // ����Ĳ���
	// �Զ�������صı���
	private boolean mbReminderIsOpen = false;
	private String mRemindTime = null;

	// ui����ر���
	private ViewPager mTabPager = null;
	private ImageView mTabImg = null;// ����ͼƬ
	private View mRecentView = null;
	private View mAllView = null;
	private View mSettingsView = null;
	private ImageView mTab1 = null, mTab2 = null, mTab3 = null;
	private int zero = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int one = 0;// ����ˮƽ����λ��
	private int two = 0;
	private ListView mBirRecentListView = null;
	private MyListAdapter mBirRecentAdapter = null;
	private ListView mBirAllListView = null;
	private MyListAdapter mBirAllAdapter = null;

	// �Ƿ������������
	private boolean mIsContinuelyClickBack = false;

	public static int mReminderWorkType = 0; // �������1�Ļ�����ô����һֱ��������

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_birthday_main);
		// ����activityʱ���Զ����������
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		mbFirst = true;
		initData();
		initData2();
		initUI();
		setNotify();
		initClick();
	}

	@Override
	protected void onResume() {
		if (!mbFirst) {
			initData();
			attachData();
			// ����Ǵ��� ��� ���� ɾ������ ����������ô��ʾ���еĽ���
			if (mLastAction == 1 || mLastAction == 2) {
				mTabPager.setCurrentItem(1);
			}

		} else {
			mbFirst = false;
		}
		super.onResume();
	};

	protected void onDestroy() {
		mBirListRecent.clear();
		mBirListAll.clear();
		super.onDestroy();
	}

	// ���ε����ؼ���Ч��
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// ��ʽ���� �����������
			if (mIsContinuelyClickBack) {
				finish();
			} else {
				Toast.makeText(this.getApplicationContext(), "�ٰ�һ���˳�Birthday",
						Toast.LENGTH_SHORT).show();
				mIsContinuelyClickBack = true;
				// ��һ���Ժ����û�е�����أ���ô����Ϊ�����������
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mIsContinuelyClickBack = false;
					}
				}, 1000);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		if (mBirDBManager == null) {
			mContextInstance = this.getApplicationContext();
			mBirDBManager = new MySQLiteMananger(this.getApplicationContext());
		}
		// �����ݿ��ж�ȡ
		// mBirDBManager.deleteDatabase(getApplicationContext());
		mBirDBManager.open();
		// mBirDBManager.analog();
		if (mBirListAll == null) {
			mBirListAll = new ArrayList<BirthdayData>();
		}
		if (!mBirListAll.isEmpty())
			mBirListAll.clear();
		mBirListAll = mBirDBManager.query();
		// mBirListAll = sortListByName(mBirListAll); // ����һ��
		Collections.sort(mBirListAll, new CollatorComparatorByName());
		mBirDBManager.close();

		// �����е��б�����, ���������һ����
		if (mBirListRecent == null) {
			mBirListRecent = new ArrayList<BirthdayData>();
		}
		if (!mBirListRecent.isEmpty())
			mBirListRecent.clear();
		for (BirthdayData bir : mBirListAll) {
			if (bir.isRecent()) {
				mBirListRecent.add(bir);
			}
		}
		// mBirListRecent = sortListByDay(mBirListRecent);
		Collections.sort(mBirListRecent, new CollatorComparatorByDay());

		// �ҵ��������յ���
		if (mBirListToday == null) {
			mBirListToday = new ArrayList<BirthdayData>();
		}
		if (!mBirListToday.isEmpty())
			mBirListToday.clear();
		for (BirthdayData bir : mBirListRecent) {
			if (bir.isToday()) {
				mBirListToday.add(bir);
			}
		}
		// mBirListRecent = sortListByDay(mBirListRecent);
		Collections.sort(mBirListRecent, new CollatorComparatorByDay());
	}

	private void initData2() {
		if (mBirDBManager == null) {
			mContextInstance = this.getApplicationContext();
			mBirDBManager = new MySQLiteMananger(this.getApplicationContext());
		}
		// �����ݿ��ж�ȡ
		mBirDBManager.open();
		mbReminderIsOpen = mBirDBManager.queryIsAutoRemind();
		mRemindTime = mBirDBManager.queryRemindTime();
		mBirDBManager.close();

		// ��������
		if (mbReminderIsOpen) {
			setReminder();
		}

	}

	@SuppressLint("InflateParams")
	private void initUI() {
		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mTab1 = (ImageView) findViewById(R.id.img_recent);
		mTab2 = (ImageView) findViewById(R.id.img_all);
		mTab3 = (ImageView) findViewById(R.id.img_settings);
		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		mTab1.setOnClickListener(new MyOnClickListener(0));
		mTab2.setOnClickListener(new MyOnClickListener(1));
		mTab3.setOnClickListener(new MyOnClickListener(2));

		Display currDisplay = getWindowManager().getDefaultDisplay();// ��ȡ��Ļ��ǰ�ֱ���
		@SuppressWarnings("deprecation")
		int displayWidth = currDisplay.getWidth();
		zero = 14;
		one = displayWidth / 3 + 5; // ����ˮƽ����ƽ�ƴ�С
		two = one * 2 + 5;

		// ��Ҫ��ҳ��ʾ��Viewװ��������
		LayoutInflater mLi = LayoutInflater.from(this);
		mRecentView = mLi.inflate(R.layout.main_tab_recently, null);
		mAllView = mLi.inflate(R.layout.main_tab_all, null);
		mSettingsView = mLi.inflate(R.layout.main_tab_settings, null);

		// ÿ��ҳ���view����
		final ArrayList<View> views = new ArrayList<View>();
		views.add(mRecentView);
		views.add(mAllView);
		views.add(mSettingsView);

		// ���ViewPager������������
		mTabPager.setAdapter(new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		});
		mTabPager.setCurrentItem(0);

		// ����Ľ��洦��
		mBirRecentListView = (ListView) mRecentView
				.findViewById(R.id.lv_recentbir);
		// ���н���Ĵ���
		mBirAllListView = (ListView) mAllView.findViewById(R.id.lv_allbir);
		// ��������
		attachData();
	}

	@SuppressWarnings("deprecation")
	private void setNotify() {
		int todayNum = mBirListToday.size();
		if (todayNum <= 0) {
			return;
		}

		String contentTitle = "����" + todayNum + "λ���ѽ��������Ŷ!";
		String contentText = "����:\n";
		int i = 0;
		for (i = 0; i < todayNum; i++) {
			contentText += mBirListToday.get(i).mName + " ["
					+ mBirListToday.get(i).getAge() + "]";
			if (i <= todayNum - 2) {
				contentText += "��  \n";
			} else {
				contentText += ".";
			}
		}
		// ����֪ͨ��֪ͨ
		NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "�����µ���������!";
		notification.when = System.currentTimeMillis();
		notification.defaults = Notification.DEFAULT_LIGHTS
				| Notification.DEFAULT_SOUND;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				getIntent(), 0);
		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);
		notifyManager.notify(R.drawable.ic_launcher, notification);
	}

	private void attachData() {
		if (mBirRecentAdapter == null) {
			mBirRecentAdapter = new MyListAdapter(getApplicationContext(),
					mBirListRecent);
		} else {
			mBirRecentAdapter.setList(mBirListRecent);
		}
		mBirRecentListView.setAdapter(mBirRecentAdapter);
		// setListViewHeightBasedOnChildren(mBirRecentListView);

		if (mBirAllAdapter == null) {
			mBirAllAdapter = new MyListAdapter(getApplicationContext(),
					mBirListAll);
		} else {
			mBirAllAdapter.setList(mBirListAll);
		}
		mBirAllListView.setAdapter(mBirAllAdapter);
		// setListViewHeightBasedOnChildren(mBirAllListView);
	}

	private void initClick() {
		if (mAllView != null) {
			// ���Ϸ��༭��ť
			ImageButton btn_right_add = (ImageButton) mAllView
					.findViewById(R.id.right_btn);
			btn_right_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onAddClicked();
				}
			});

			// �����༭��
			EditText txt_all_search = (EditText) mAllView
					.findViewById(R.id.txt_search);
			txt_all_search.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub
					if (mBirDBManager != null) {
						mBirDBManager.open();
						mBirListAll.clear();
						mBirListAll = mBirDBManager.query(s.toString());
						mBirDBManager.close();
						attachData();
					}

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					// ������Ϊ�յĻ�����ô������ʾ���е�����
					if (s.toString().isEmpty()) {
						if (mBirDBManager != null) {
							mBirDBManager.open();
							mBirListAll.clear();
							mBirListAll = mBirDBManager.query();
							mBirDBManager.close();
							attachData();
						}
					}
				}
			});
		}

		if (mSettingsView != null) {
			// ���ý������Ӱ�ť
			RelativeLayout relaLayAdd = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_add_bir);
			relaLayAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onAddClicked();
				}
			});

			// ���ý���� ���Ѱ�ť
			RelativeLayout relaLaySetReminder = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_set_reminder);
			relaLaySetReminder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onSetReminderClicked();
				}
			});

			// ���ý���ĵ��밴ť
			RelativeLayout relaLayImport = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_import_bir_data);
			relaLayImport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onImportClicked();
				}
			});

			// ���ý���� ������ť
			RelativeLayout relaLayExport = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_export_bir_data);
			relaLayExport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onExportClicked();
				}
			});

			// ���ý���� ɾ����ť
			RelativeLayout relaLayDelete = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_delete_all_bir);
			relaLayDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onDeleteClicked();
				}
			});

			// ���ý���� �����ͷ�����ť
			RelativeLayout relaLayReportAndHelp = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_report_help);
			relaLayReportAndHelp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onReportAndHelpClicked();
				}
			});

			// ���ý���� ���ڰ�ť
			RelativeLayout relaLayAbout = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_about);
			relaLayAbout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onAboutClicked();
				}
			});

			// ���ý���� �˳���ť
			Button exitBtn = (Button) mSettingsView
					.findViewById(R.id.btn_btn_exit);
			exitBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onExitClicked();
				}
			});
		}
	}

	/**
	 * ͷ��������
	 */
	public class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mTabPager.setCurrentItem(index);
		}
	};

	/**
	 * ҳ���л�����(ԭ����:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_recent_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, zero, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_all_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, zero, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_all_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_recent_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_recent_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_all_normal));
				}
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * ��̬����ListView�齨�ĸ߶�
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// params.height += 5;// if without this statement,the listview will be
		// a
		// little short
		// listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
		// params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
		listView.setLayoutParams(params);
	}

	@SuppressWarnings("rawtypes")
	public class CollatorComparatorByName implements Comparator {
		Collator collator = Collator.getInstance();

		public int compare(Object element1, Object element2) {

			CollationKey key1 = collator
					.getCollationKey(((BirthdayData) element1).mName);
			CollationKey key2 = collator
					.getCollationKey(((BirthdayData) element2).mName);
			return key1.compareTo(key2);
		}
	}

	@SuppressWarnings("rawtypes")
	public class CollatorComparatorByDay implements Comparator {
		Collator collator = Collator.getInstance();

		public int compare(Object element1, Object element2) {
			BirthdayData birTmp1, birTmp2;
			birTmp1 = (BirthdayData) element1;
			birTmp2 = (BirthdayData) element2;

			CollationKey key1 = collator.getCollationKey(LunarCalendar
					.recentDays(birTmp1.mbir_date, birTmp1.mbir_type) + "��");
			CollationKey key2 = collator.getCollationKey(LunarCalendar
					.recentDays(birTmp2.mbir_date, birTmp2.mbir_type) + "��");
			return key1.compareTo(key2);
		}
	}

	/*
	 * private List<BirthdayData> sortListByDay(List<BirthdayData> birList) {
	 * List<BirthdayData> birSortList = new ArrayList<BirthdayData>();
	 * BirthdayData birTmp1, birTmp2; int index = 0; int birNum =
	 * birList.size(); int day1, day2; int i=0, j=0;
	 * 
	 * if(birNum<=0) { return birSortList; }
	 * 
	 * for(i=0; i<birNum; i++) { if(birList.size()<=0)break; birTmp1 =
	 * birList.get(0); index = 0; for(j=0; j<birList.size(); j++) { birTmp2 =
	 * birList.get(j); day1 = LunarCalendar.recentDays(birTmp1.mbir_date,
	 * birTmp1.mbir_type); day2 = LunarCalendar.recentDays(birTmp2.mbir_date,
	 * birTmp2.mbir_type); if(day1 > day2) { birTmp1 = birTmp2; index = j; } }
	 * birSortList.add(birTmp1); birList.remove(index); } return birSortList; }
	 * 
	 * private List<BirthdayData> sortListByName(List<BirthdayData> birList) {
	 * List<BirthdayData> birSortList = new ArrayList<BirthdayData>();
	 * BirthdayData birTmp1, birTmp2; int index = 0; int birNum =
	 * birList.size(); String name1, name2; int i=0, j=0;
	 * 
	 * if(birNum<=0) { return birSortList; }
	 * 
	 * for(i=0; i<birNum; i++) { if(birList.size()<=0)break; birTmp1 =
	 * birList.get(0); index = 0; for(j=0; j<birList.size(); j++) { birTmp2 =
	 * birList.get(j); name1 = birTmp1.mName; name2 = birTmp2.mName;
	 * if(name1.compareToIgnoreCase(name2) > 0 ) { birTmp1 = birTmp2; index = j;
	 * } } birSortList.add(birTmp1); birList.remove(index); } return
	 * birSortList; }
	 */

	private void onAddClicked() {
		mLastAction = 1;
		Intent intent = new Intent(BirthdayMainActivity.this,
				EditBirthdayActivity.class);
		intent.putExtra("WorkType", 1);
		startActivity(intent);
		// mTabPager.setCurrentItem(1); //����ʾ���н���
	}

	private void onDeleteClicked() {
		mLastAction = 2;
		Intent intent = new Intent(BirthdayMainActivity.this,
				ConfirmDialogActivity.class);
		intent.putExtra("Title", "����");
		intent.putExtra("Msg", "����ɾ�����е��������ݣ�����\n���ɻָ�, ȷ�ϼ���ô��");
		intent.putExtra("RequestCode", ConfirmDialogActivity.DELETE_CODE);
		startActivityForResult(intent, ConfirmDialogActivity.DELETE_CODE);
	}

	public void onImportClicked() {
		File sdcardDir = Environment.getExternalStorageDirectory();
		// �õ�һ��·����������sdcard���ļ���·��������
		String path = sdcardDir.getPath() + "/Birthday/myBir.xml";
		File file1 = new File(path);
		if (!file1.exists()) {
			Toast.makeText(mContextInstance, "��ȷ�������ļ�����:\n" + path,
					Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(mContextInstance, DataTransportService.class);
		intent.putExtra("WorkType", 0);
		startService(intent);

		// ��1���Ժ�ˢ������
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initData();
				attachData();
			}
		}, 1000);
	}

	public void onExportClicked() {
		if (mBirListAll.size() == 0) {
			Toast.makeText(mContextInstance, "û������!", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Intent intent = new Intent(mContextInstance, DataTransportService.class);
		intent.putExtra("WorkType", 1);
		startService(intent);
	}

	private void onReportAndHelpClicked() {
		mLastAction = 3;
		Intent intent = new Intent(BirthdayMainActivity.this,
				AboutAndReportActivity.class);
		intent.putExtra("Type", 1);
		startActivity(intent);
	}

	private void onAboutClicked() {
		mLastAction = 4;
		Intent intent = new Intent(BirthdayMainActivity.this,
				AboutAndReportActivity.class);
		intent.putExtra("Type", 0);
		startActivity(intent);
	}

	private void onSetReminderClicked() {
		mLastAction = 5;
		Intent intent = new Intent(BirthdayMainActivity.this,
				ReminderSettingActivity.class);
		intent.putExtra("IsOpen", mbReminderIsOpen);
		if (mbReminderIsOpen) {
			intent.putExtra("Remind_Time", mRemindTime);
		}
		startActivityForResult(intent,
				ReminderSettingActivity.EDIT_REMIDER_TIME);
	}

	private void onExitClicked() {
		mLastAction = 6;
		Intent intent = new Intent(BirthdayMainActivity.this,
				ConfirmDialogActivity.class);
		intent.putExtra("RequestCode", ConfirmDialogActivity.EXIT_CODE);
		startActivityForResult(intent, ConfirmDialogActivity.EXIT_CODE);
	}

	private void setReminderTime() {
		if (mBirDBManager == null) {
			mContextInstance = this.getApplicationContext();
			mBirDBManager = new MySQLiteMananger(this.getApplicationContext());
		}
		// �����ݿ��ж�ȡ
		mBirDBManager.open();
		mBirDBManager.setReminder(mbReminderIsOpen, mRemindTime);
		mBirDBManager.close();
	}

	private void setReminder() {
		if (mBirListToday.isEmpty()) {
			return;
		}
		if (!mbReminderIsOpen) {
			return;
		}
		String[] split = mRemindTime.split("-");
		if (split == null || split.length != 2) {
			return;
		}
		int hourOfDay = Integer.valueOf(split[0]).intValue();
		int minute = Integer.valueOf(split[1]).intValue();
		Calendar cc = Calendar.getInstance();
		if (hourOfDay < cc.get(Calendar.HOUR_OF_DAY)
				|| (hourOfDay == cc.get(Calendar.HOUR_OF_DAY) && minute <= cc
						.get(Calendar.MINUTE))) {
			// ����Ѿ����˼�¼���õ�ʱ�䣬��ô�������Զ�����
			Log.d("SET_REMINDER", "ʱ���Ѿ�����");
			return;
		}

		// ָ������AlarmActivity���
		Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		
		if (android.os.Build.VERSION.SDK_INT >= 12) {
			// 3.1�Ժ�İ汾��Ҫ����Intent.FLAG_INCLUDE_STOPPED_PACKAGES
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			// intent.setFlags(32);
		}
		// ����PendingIntent����
		PendingIntent pi = PendingIntent.getBroadcast(
				BirthdayMainActivity.this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		// ����ȡ���Ѿ����ù�������
		// aManager.cancel(pi);
		// Log.d("LOG_SET_REMIDER", "1. cancel old");

		// Ȼ���������µ�����
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		// �����û�ѡ��ʱ��������Calendar����
		// c.add(Calendar.SECOND, 8);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		AlarmManager aManager;
		// ��ȡAlarmManager����
		aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		// ����AlarmManager����Calendar��Ӧ��ʱ������ָ�����
		aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		// aManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
		// (10*1000), pi);
		// Log.d("LOG_SET_REMIDER", "1. create new");
		// Log.d("LOG_SET_REMIDER", c.getTimeInMillis() + " ms");

		if (mBirListToday.size() > 0) {
			mReminderWorkType = 1;
			// ͬʱ����service
			Intent serviceIntent = new Intent(BirthdayMainActivity.this,
					AlarmService.class);
			startService(serviceIntent);
		}

	}

	private void cancelReminder() {
		AlarmManager aManager;
		// ��ȡAlarmManager����
		aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		// ָ������AlarmActivity���
		Intent intent = new Intent(this.getApplicationContext(),
				AlarmReceiver.class);
		// ����PendingIntent����
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		// ����ȡ���Ѿ����ù�������
		aManager.cancel(pi);
		// Log.d("LOG_SET_REMIDER", "2. cancel old");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ���Ը��ݶ���������������Ӧ�Ĳ���
		int chooseCode = data.getIntExtra("ResultCode", 0);
		switch (resultCode) {
		case ConfirmDialogActivity.DELETE_CODE:
			if (chooseCode == ConfirmDialogActivity.RESULT_CODE_POSITIVE) {
				if (mBirDBManager != null) {
					mBirDBManager.deleteDatabase(getApplicationContext());
					Toast.makeText(getApplicationContext(), "������ȫ��ɾ��!",
							Toast.LENGTH_LONG).show();
				}
			} else {
				// Toast.makeText(getApplicationContext(), "ȡ��ɾ������!",
				// Toast.LENGTH_LONG).show();
			}
			break;
		case ConfirmDialogActivity.EXIT_CODE:
			if (chooseCode == ConfirmDialogActivity.RESULT_CODE_POSITIVE) {
				this.finish();
			}
			break;
		case ReminderSettingActivity.EDIT_REMIDER_TIME:
			boolean changed = data.getBooleanExtra("Changed", false);
			if (changed) {
				mRemindTime = data.getStringExtra("Remind_Time");
				if (chooseCode == ReminderSettingActivity.OPENED) {
					mbReminderIsOpen = true;
					setReminder();
				} else {
					mbReminderIsOpen = false;
					cancelReminder();
				}
				setReminderTime();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
