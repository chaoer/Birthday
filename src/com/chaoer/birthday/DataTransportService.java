package com.chaoer.birthday;

import java.io.File;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class DataTransportService extends IntentService {

	private Context mContext = null;
	private int mWorkType = 0; // 0：导入， 1: 导出

	public DataTransportService() {
		super("DataTransportService");
		// TODO Auto-generated constructor stub
	}

	public DataTransportService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("BIR_DATA_SERVICE", "onHandleIntent");
		mWorkType = intent.getIntExtra("WorkType", 0);
		if (mWorkType == 0) {
			Log.d("BIR_DATA_SERVICE", "import");
			import_data();
		} else if (mWorkType == 1) {
			Log.d("BIR_DATA_SERVICE", "export");
			export_data();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("BIR_DATA_SERVICE", "onCreate");
		mContext = this.getApplicationContext();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d("BIR_DATA_SERVICE", "onDestroy");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// Log.d("BIR_DATA_SERVICE", "onStartCommand");
		// mWorkType = intent.getIntExtra("WorkType", 0);
		// if (mWorkType == 0) {
		// Log.d("BIR_DATA_SERVICE", "import");
		// import_data();
		// } else if (mWorkType == 1) {
		// Log.d("BIR_DATA_SERVICE", "export");
		// export_data();
		// }
		return super.onStartCommand(intent, flags, startId);
	}

	private void export_data() {
		List<BirthdayData> birdataList = null;
		MySQLiteMananger dbMan = new MySQLiteMananger(mContext);
		if (dbMan.open()) {
			birdataList = dbMan.query();
		}
		dbMan.close();

		if (DataTransport.export_bir_data(birdataList)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/Birthday/myBir.xml";
			showToastByRunnable(this, "导出记录到:\n" + path, Toast.LENGTH_SHORT);
		} else {
			showToastByRunnable(this, "导出记录失败!", Toast.LENGTH_SHORT);
		}
	}

	private void import_data() {
		MySQLiteMananger dbMan = new MySQLiteMananger(mContext);
		if (dbMan.open()) {
			List<BirthdayData> importData = null;
			importData = DataTransport.import_bir_data();
			if (importData != null) {
				int iSuccess = 0;
				for (BirthdayData bir : importData) {
					if (dbMan.insert(bir)) {
						iSuccess++;
					}
				}
				showToastByRunnable(this, "导入 " + iSuccess + " 条记录!",
						Toast.LENGTH_SHORT);
				if (iSuccess > 0) {

				}
			} else {
				showToastByRunnable(this, "导入记录失败!", Toast.LENGTH_SHORT);
			}
		}
		dbMan.close();
	}

	private void showToastByRunnable(final IntentService context,
			final CharSequence text, final int duration) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, duration).show();
			}
		});
	}
}
