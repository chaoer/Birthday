package com.chaoer.birthday;

import com.chaoer.common.tools.CommonTools;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ReminderDialogActivity extends Activity {
	private Button mPositiveBtn = null;
	private Button mNegativeBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_reminder);
		initUI();
	}

	// ���ε����ؼ���Ч��
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initUI() {
		mPositiveBtn = (Button) findViewById(R.id.reminder_dialog_positive_btn);
		mNegativeBtn = (Button) findViewById(R.id.reminder_dialog_negative_btn);

		// �����Ч��
		Vibrator va = (Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		va.vibrate(2000);
		
		mPositiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ����г�����ǰ̨����ô�ص����Ի���ͺ���
				// ����ں�̨���У���ô��ֱ�ӷŵ�ǰ̨�ͺ���
				// ���� ����������
				if (CommonTools.isAppOnForeground(getApplicationContext())) {
					Log.d("START_NEW", "ǰ̨����");
				} else if (CommonTools
						.isAppOnBackground(getApplicationContext())) {
					Log.d("START_NEW", "��̨����");
				} else {
					Log.d("START_NEW", "��������");
				}
				Intent intent = new Intent(getApplicationContext(),
						BirthdayMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		mNegativeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}
}
