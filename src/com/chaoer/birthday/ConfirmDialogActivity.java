package com.chaoer.birthday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmDialogActivity extends Activity {
	private LinearLayout mLayout = null;
	private TextView mTitle = null;
	private TextView mMsg = null;
	private Button mPositiveBtn = null;
	private Button mNegativeBtn = null;
	private int mChooseResult = 0; // 0: negative; 1: positive
	private int mRequestCode = 0; // 177 :delete data, 178:exit
	public final static int DELETE_CODE = 177;
	public final static int EXIT_CODE = 178;
	public final static int DELETE_ONE_CODE = 180;
	public final static int RESULT_CODE_POSITIVE = 1;
	public final static int RESULT_CODE_NEGATIVE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_dialog);

		initUI();
		getIntentData();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mChooseResult = 0;
		setResult();
		return true;
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
		if (intent.hasExtra("Title")) {
			mTitle.setText(intent.getStringExtra("Title"));
		}
		if (intent.hasExtra("Msg")) {
			mMsg.setText(intent.getStringExtra("Msg"));
		}
		if (intent.hasExtra("RequestCode")) {
			mRequestCode = intent.getIntExtra("RequestCode", EXIT_CODE);
		}
	}

	private void initUI() {
		mLayout = (LinearLayout) findViewById(R.id.confirm_dialog_layout);
		mLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});

		mTitle = (TextView) findViewById(R.id.confirm_dialog_title);
		mMsg = (TextView) findViewById(R.id.confirm_dialog_msg);

		mPositiveBtn = (Button) findViewById(R.id.confirm_dialog_positive_btn);
		mPositiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mChooseResult = 1;
				setResult();
			}
		});

		mNegativeBtn = (Button) findViewById(R.id.confirm_dialog_negative_btn);
		mNegativeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mChooseResult = 0;
				setResult();
			}
		});
	}

	private void setResult() {
		Intent result = new Intent();
		result.putExtra("ResultCode", mChooseResult);
		// 请求代码可以自己设置，这里设置成20
		setResult(mRequestCode, result);
		// 关闭掉这个Activity
		finish();
	}
}
