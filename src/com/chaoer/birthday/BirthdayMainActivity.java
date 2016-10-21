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
	// 数据
	private MySQLiteMananger mBirDBManager = null;
	private List<BirthdayData> mBirListAll = null;
	private List<BirthdayData> mBirListRecent = null;
	private List<BirthdayData> mBirListToday = null;
	public static Context mContextInstance = null;
	private boolean mbFirst = true;
	private int mLastAction = 0; // 点击的操作
	// 自动提醒相关的变量
	private boolean mbReminderIsOpen = false;
	private String mRemindTime = null;

	// ui的相关变量
	private ViewPager mTabPager = null;
	private ImageView mTabImg = null;// 动画图片
	private View mRecentView = null;
	private View mAllView = null;
	private View mSettingsView = null;
	private ImageView mTab1 = null, mTab2 = null, mTab3 = null;
	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one = 0;// 单个水平动画位移
	private int two = 0;
	private ListView mBirRecentListView = null;
	private MyListAdapter mBirRecentAdapter = null;
	private ListView mBirAllListView = null;
	private MyListAdapter mBirAllAdapter = null;

	// 是否连续点击返回
	private boolean mIsContinuelyClickBack = false;

	public static int mReminderWorkType = 0; // 如果等于1的话，那么将会一直调用提醒

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_birthday_main);
		// 启动activity时不自动弹出软键盘
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
			// 如果是从其 添加 或者 删除界面 返回来，那么显示所有的界面
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

	// 屏蔽掉返回键的效果
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 方式二： 连续点击返回
			if (mIsContinuelyClickBack) {
				finish();
			} else {
				Toast.makeText(this.getApplicationContext(), "再按一次退出Birthday",
						Toast.LENGTH_SHORT).show();
				mIsContinuelyClickBack = true;
				// 在一秒以后如果没有点击返回，那么就认为不是连续点击
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
		// 从数据库中读取
		// mBirDBManager.deleteDatabase(getApplicationContext());
		mBirDBManager.open();
		// mBirDBManager.analog();
		if (mBirListAll == null) {
			mBirListAll = new ArrayList<BirthdayData>();
		}
		if (!mBirListAll.isEmpty())
			mBirListAll.clear();
		mBirListAll = mBirDBManager.query();
		// mBirListAll = sortListByName(mBirListAll); // 排序一下
		Collections.sort(mBirListAll, new CollatorComparatorByName());
		mBirDBManager.close();

		// 从所有的列表里面, 或许最近的一部分
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

		// 找到今天生日的人
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
		// 从数据库中读取
		mBirDBManager.open();
		mbReminderIsOpen = mBirDBManager.queryIsAutoRemind();
		mRemindTime = mBirDBManager.queryRemindTime();
		mBirDBManager.close();

		// 设置提醒
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

		Display currDisplay = getWindowManager().getDefaultDisplay();// 获取屏幕当前分辨率
		@SuppressWarnings("deprecation")
		int displayWidth = currDisplay.getWidth();
		zero = 14;
		one = displayWidth / 3 + 5; // 设置水平动画平移大小
		two = one * 2 + 5;

		// 将要分页显示的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(this);
		mRecentView = mLi.inflate(R.layout.main_tab_recently, null);
		mAllView = mLi.inflate(R.layout.main_tab_all, null);
		mSettingsView = mLi.inflate(R.layout.main_tab_settings, null);

		// 每个页面的view数据
		final ArrayList<View> views = new ArrayList<View>();
		views.add(mRecentView);
		views.add(mAllView);
		views.add(mSettingsView);

		// 填充ViewPager的数据适配器
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

		// 最近的界面处理
		mBirRecentListView = (ListView) mRecentView
				.findViewById(R.id.lv_recentbir);
		// 所有界面的处理
		mBirAllListView = (ListView) mAllView.findViewById(R.id.lv_allbir);
		// 　绑定数据
		attachData();
	}

	@SuppressWarnings("deprecation")
	private void setNotify() {
		int todayNum = mBirListToday.size();
		if (todayNum <= 0) {
			return;
		}

		String contentTitle = "你有" + todayNum + "位好友今天过生日哦!";
		String contentText = "包括:\n";
		int i = 0;
		for (i = 0; i < todayNum; i++) {
			contentText += mBirListToday.get(i).mName + " ["
					+ mBirListToday.get(i).getAge() + "]";
			if (i <= todayNum - 2) {
				contentText += "、  \n";
			} else {
				contentText += ".";
			}
		}
		// 设置通知栏通知
		NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "您有新的生日提醒!";
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
			// 右上方编辑按钮
			ImageButton btn_right_add = (ImageButton) mAllView
					.findViewById(R.id.right_btn);
			btn_right_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onAddClicked();
				}
			});

			// 搜索编辑框
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
					// 如果最后为空的话，那么还是显示所有的数据
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
			// 设置界面的添加按钮
			RelativeLayout relaLayAdd = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_add_bir);
			relaLayAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onAddClicked();
				}
			});

			// 设置界面的 提醒按钮
			RelativeLayout relaLaySetReminder = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_set_reminder);
			relaLaySetReminder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onSetReminderClicked();
				}
			});

			// 设置界面的导入按钮
			RelativeLayout relaLayImport = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_import_bir_data);
			relaLayImport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onImportClicked();
				}
			});

			// 设置界面的 导出按钮
			RelativeLayout relaLayExport = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_export_bir_data);
			relaLayExport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onExportClicked();
				}
			});

			// 设置界面的 删除按钮
			RelativeLayout relaLayDelete = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_delete_all_bir);
			relaLayDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onDeleteClicked();
				}
			});

			// 设置界面的 帮助和反馈按钮
			RelativeLayout relaLayReportAndHelp = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_report_help);
			relaLayReportAndHelp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onReportAndHelpClicked();
				}
			});

			// 设置界面的 关于按钮
			RelativeLayout relaLayAbout = (RelativeLayout) mSettingsView
					.findViewById(R.id.btn_re_about);
			relaLayAbout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onAboutClicked();
				}
			});

			// 设置界面的 退出按钮
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
	 * 头标点击监听
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
	 * 页卡切换监听(原作者:D.Winter)
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
			animation.setFillAfter(true);// True:图片停在动画结束位置
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
	 * 动态设置ListView组建的高度
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
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
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
					.recentDays(birTmp1.mbir_date, birTmp1.mbir_type) + "天");
			CollationKey key2 = collator.getCollationKey(LunarCalendar
					.recentDays(birTmp2.mbir_date, birTmp2.mbir_type) + "天");
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
		// mTabPager.setCurrentItem(1); //　显示所有界面
	}

	private void onDeleteClicked() {
		mLastAction = 2;
		Intent intent = new Intent(BirthdayMainActivity.this,
				ConfirmDialogActivity.class);
		intent.putExtra("Title", "提醒");
		intent.putExtra("Msg", "将会删除所有的生日数据，并且\n不可恢复, 确认继续么？");
		intent.putExtra("RequestCode", ConfirmDialogActivity.DELETE_CODE);
		startActivityForResult(intent, ConfirmDialogActivity.DELETE_CODE);
	}

	public void onImportClicked() {
		File sdcardDir = Environment.getExternalStorageDirectory();
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path = sdcardDir.getPath() + "/Birthday/myBir.xml";
		File file1 = new File(path);
		if (!file1.exists()) {
			Toast.makeText(mContextInstance, "请确保如下文件存在:\n" + path,
					Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(mContextInstance, DataTransportService.class);
		intent.putExtra("WorkType", 0);
		startService(intent);

		// 在1秒以后刷新数据
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
			Toast.makeText(mContextInstance, "没有数据!", Toast.LENGTH_SHORT)
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
		// 从数据库中读取
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
			// 如果已经过了记录设置的时间，那么则跳过自动设置
			Log.d("SET_REMINDER", "时间已经过了");
			return;
		}

		// 指定启动AlarmActivity组件
		Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		
		if (android.os.Build.VERSION.SDK_INT >= 12) {
			// 3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			// intent.setFlags(32);
		}
		// 创建PendingIntent对象
		PendingIntent pi = PendingIntent.getBroadcast(
				BirthdayMainActivity.this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		// 　先取消已经设置过的闹钟
		// aManager.cancel(pi);
		// Log.d("LOG_SET_REMIDER", "1. cancel old");

		// 然后再设置新的闹钟
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		// 根据用户选择时间来设置Calendar对象
		// c.add(Calendar.SECOND, 8);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		AlarmManager aManager;
		// 获取AlarmManager对象
		aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		// 设置AlarmManager将在Calendar对应的时间启动指定组件
		aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		// aManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
		// (10*1000), pi);
		// Log.d("LOG_SET_REMIDER", "1. create new");
		// Log.d("LOG_SET_REMIDER", c.getTimeInMillis() + " ms");

		if (mBirListToday.size() > 0) {
			mReminderWorkType = 1;
			// 同时启动service
			Intent serviceIntent = new Intent(BirthdayMainActivity.this,
					AlarmService.class);
			startService(serviceIntent);
		}

	}

	private void cancelReminder() {
		AlarmManager aManager;
		// 获取AlarmManager对象
		aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		// 指定启动AlarmActivity组件
		Intent intent = new Intent(this.getApplicationContext(),
				AlarmReceiver.class);
		// 创建PendingIntent对象
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		// 　先取消已经设置过的闹钟
		aManager.cancel(pi);
		// Log.d("LOG_SET_REMIDER", "2. cancel old");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 可以根据多个请求代码来作相应的操作
		int chooseCode = data.getIntExtra("ResultCode", 0);
		switch (resultCode) {
		case ConfirmDialogActivity.DELETE_CODE:
			if (chooseCode == ConfirmDialogActivity.RESULT_CODE_POSITIVE) {
				if (mBirDBManager != null) {
					mBirDBManager.deleteDatabase(getApplicationContext());
					Toast.makeText(getApplicationContext(), "数据已全部删除!",
							Toast.LENGTH_LONG).show();
				}
			} else {
				// Toast.makeText(getApplicationContext(), "取消删除操作!",
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
