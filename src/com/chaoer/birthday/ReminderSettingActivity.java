package com.chaoer.birthday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class ReminderSettingActivity extends Activity {
	private int mWorkType = 0; // 0:第一次设置， 1：更新
	private Switch mSwitchButton = null;
	private TimePicker mTimePicker = null;
	private Button mBackBtn = null;
	private Button mPositiveBtn = null;
	private Button mNegativeBtn = null;
	private boolean mIsOpen = false; // 默认没有打开
	private boolean mbChanged = false; // 是否改变
	private String mReminderTime = null; // 24小时格式
	public final static int EDIT_REMIDER_TIME = 179;
	public final static int OPENED = 1;
	public final static int DINOT_OPEN = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_reminder);

		mbChanged = false;
		getDataFromIntent();
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

	private void getDataFromIntent() {
		Intent intent = getIntent();
		mIsOpen = intent.getBooleanExtra("IsOpen", false);
		mWorkType = 0;
		if (mIsOpen) {
			mWorkType = 1;
		}
		if (intent.hasExtra("Remind_Time")) {
			mReminderTime = intent.getStringExtra("Remind_Time");
		} else {
			Calendar cal = Calendar.getInstance();
			mReminderTime = cal.get(Calendar.HOUR_OF_DAY) + "-"
					+ cal.get(Calendar.MINUTE);
		}
	}

	private void initUI() {
		mSwitchButton = (Switch) findViewById(R.id.reminder_set_open);
		mTimePicker = (TimePicker) findViewById(R.id.reminder_set_timepicker);
		mBackBtn = (Button) findViewById(R.id.reminder_set_title_btn_back);
		mPositiveBtn = (Button) findViewById(R.id.reminder_set_positive_btn);
		mNegativeBtn = (Button) findViewById(R.id.reminder_set_nagetive_btn);

		//setNumberPickerTextColor(mTimePicker);
		
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mbChanged = false;
				setResult();
			}
		});

		mNegativeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mbChanged = false;
				setResult();
			}
		});

		if (mIsOpen) {
			mPositiveBtn.setText(R.string.reminder_set_positive_btn_txt2);
		}
		mPositiveBtn.setEnabled(mIsOpen);
		mPositiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult();
			}
		});

		mSwitchButton.setChecked(mIsOpen);
		mSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				mIsOpen = isChecked;
				mTimePicker.setEnabled(mIsOpen);
				if (mWorkType == 0) {
					mPositiveBtn.setEnabled(mIsOpen);
				}
				mbChanged = !mbChanged;
			}
		});

		mTimePicker.setEnabled(mIsOpen);
		String[] split = mReminderTime.split("-");
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(Integer.valueOf(split[0]).intValue());
		mTimePicker.setCurrentMinute(Integer.valueOf(split[1]).intValue());
		mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				mReminderTime = hourOfDay + "-" + minute;
				mbChanged = true;
			}
		});
	}

	private void setResult() {
		Intent result = new Intent();
		result.putExtra("Changed", mbChanged);
		if (mIsOpen) {
			result.putExtra("ResultCode", OPENED);
			result.putExtra("Remind_Time", mReminderTime);
		} else {
			result.putExtra("ResultCode", DINOT_OPEN);
			result.putExtra("Remind_Time", mReminderTime);
		}

		setResult(EDIT_REMIDER_TIME, result);
		// 关闭掉这个Activity
		finish();
	}
	
	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup)
    {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        
        if (null != viewGroup)
        {
            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker)
                {
                    npList.add((NumberPicker)child);
                }
                else if (child instanceof LinearLayout)
                {
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if (result.size() > 0)
                    {
                        return result;
                    }
                }
            }
        }
        
        return npList;
    }
    
    /*private EditText findEditText(NumberPicker np)
    {
        if (null != np)
        {
            for (int i = 0; i < np.getChildCount(); i++)
            {
                View child = np.getChildAt(i);
                
                if (child instanceof EditText)
                {
                    return (EditText)child;
                }
            }
        }
        
        return null;
    }*/
    
    private void setNumberPickerTextColor(ViewGroup viewGroup)
    {
        List<NumberPicker> npList = findNumberPicker(viewGroup);
        if (null != npList)
        {
            for (NumberPicker np : npList)
            {
            	np.setBackgroundColor(Color.BLACK);
                //EditText et = findEditText(np);
                //et.setTextColor(Color.BLACK);    
                //et.setHintTextColor(Color.BLACK);
            }
        }
    }
}
