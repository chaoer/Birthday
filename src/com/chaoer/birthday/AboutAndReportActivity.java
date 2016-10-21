package com.chaoer.birthday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutAndReportActivity extends Activity {
	private TextView mTitle = null;
	private TextView mMsg = null;
	private Button mPositiveBtn = null;
	private Button mBackBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_and_report);

		initUI();
		getIntentData();
	}

	// 屏蔽掉返回键的效果
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		int workType = 0;
		if (intent.hasExtra("Type")) {
			workType = intent.getIntExtra("Type", 0);
			if (workType == 0) {
				// about
				mTitle.setText(R.string.about_report_title_txt_about);
				mMsg.setText(R.string.about_report_about_infromation);
			} else {
				// report and help
				mTitle.setText(R.string.about_report_title_txt_report);
				mMsg.setText(R.string.about_report_report_infromation);
			}
		}
	}

	private void initUI() {
		mTitle = (TextView) findViewById(R.id.about_report_title_txt);
		mMsg = (TextView) findViewById(R.id.about_report_info);

		mPositiveBtn = (Button) findViewById(R.id.about_report_confirm_btn);
		mPositiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		// 长按的彩蛋功能
		mPositiveBtn.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(AboutAndReportActivity.this, MsgSettingActivity.class);
//				startActivity(intent);
				return false;
			}
		});
		mBackBtn = (Button) findViewById(R.id.about_report_title_btn_back);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
