package com.chaoer.birthday;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MsgSettingActivity extends Activity {

	private boolean mbSendMsg = false;
	private String mSendNumber = null;
	private Button mBackBtn = null;
	private Button mPositiveBtn = null;
	private EditText mTxtNumber = null;
	private Switch mSwitchOpen = null;

	public MsgSettingActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_setting);
		
		initData();
		initUI();
	}

	// 屏蔽掉返回键的效果
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initData() {
		mbSendMsg = getShouldSendMsg(this.getApplicationContext());
		mSendNumber = getSendNumber(this.getApplicationContext());
	}

	private void initUI() {
		mBackBtn = (Button) findViewById(R.id.msg_setting_title_btn_back);
		mPositiveBtn = (Button) findViewById(R.id.msg_setting_positive_btn);
		mTxtNumber = (EditText) findViewById(R.id.msg_setting_number);
		mSwitchOpen = (Switch) findViewById(R.id.msg_setting_open);

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mPositiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishWork();
				finish();
			}
		});

		mSwitchOpen.setChecked(mbSendMsg);
		mSwitchOpen.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				mbSendMsg = isChecked;
			}
		});

		if (mbSendMsg) {
			mTxtNumber.setText(mSendNumber);
		}

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
	
	private void updateInfo(Context context)
	{
		SharedPreferences mySharePreferences = context.getApplicationContext()
				.getSharedPreferences("Bir_shut_down", Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象
		SharedPreferences.Editor editor = mySharePreferences.edit();
		editor.putBoolean("SEND_MSG", mbSendMsg);
		editor.putString("SEND_NUMBER", mSendNumber);
		editor.commit();
	}
	
	private void finishWork()
	{
		mbSendMsg = mSwitchOpen.isChecked();
		mSendNumber = mTxtNumber.getText().toString();
		updateInfo(this.getApplicationContext());
	}
	
}
