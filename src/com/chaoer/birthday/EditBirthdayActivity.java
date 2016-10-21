package com.chaoer.birthday;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class EditBirthdayActivity extends Activity {
	// ����
	private Activity mActivity = null;
	private MySQLiteMananger mBirDBManager = null;
	private int mWorkType = 0; // 0���༭�� 1�����
	private BirthdayData mBirdata = null;
	private int mDateType = 0; // 0:����, 1������

	// ui
	private Button mAddBtn = null;
	private Button mCancelBtn = null;
	private Button mBackBtn = null;
	private EditText mTxtName = null;
	private RadioGroup mRadioG = null;
	private TextView mTxtDate = null;
	private DatePicker mDatepicker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_bir);
		// ����activityʱ���Զ����������
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		initData();
		getParaFromIntent();
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

	private void initData() {
		if (mBirDBManager == null) {
			mBirDBManager = new MySQLiteMananger(
					BirthdayMainActivity.mContextInstance);
		}
		if (mActivity == null) {
			mActivity = this;
		}
		mWorkType = 0;
		mDateType = 0;
		mBirdata = null;
	}

	private void getParaFromIntent() {
		Intent intent = getIntent();
		mWorkType = intent.getIntExtra("WorkType", 0);
		if (mWorkType == 0) {
			mBirdata = (BirthdayData) intent.getSerializableExtra("Birdata");
		}

	}

	private void initUI() {
		mAddBtn = (Button) findViewById(R.id.add_bir_btn_add);
		mCancelBtn = (Button) findViewById(R.id.add_bir_btn_cancle);
		mBackBtn = (Button) findViewById(R.id.add_bir_title_btn_back);
		mTxtName = (EditText) findViewById(R.id.add_bir_txt_name);
		mRadioG = (RadioGroup) findViewById(R.id.add_bir_radio_type);
		mTxtDate = (TextView) findViewById(R.id.add_bir_txt_date);
		mDatepicker = (DatePicker) findViewById(R.id.add_bir_datepicker);

		// setNumberPickerTextColor(mDatepicker);

		// radioG �ı�ʱ���ʽ
		mRadioG.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.add_bir_radio_type_lunar) {
					mDateType = 1;
				} else {
					mDateType = 0;
				}
				setDateText();
			}
		});

		// ��datepicker �� txtDate ����
		mDateType = 0;
		if (mBirdata == null) {
			int[] curTime = LunarCalendar.getCurrentDate(mDateType);
			mDatepicker.init(curTime[0], curTime[1] - 1, curTime[2],
					new OnDateChangedListener() {
						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub
							setDateText();
						}
					});
		}
		setDateText();

		// �п��ܵĻ���Ҫ������Ӧ������
		if (mWorkType == 0) {
			mAddBtn.setText(R.string.add_bir_item_btn_txt_update);
			TextView txtTitle = (TextView) findViewById(R.id.add_bir_title_txt);
			txtTitle.setText(R.string.add_bir_item_btn_txt_update);
		}

		// �����������Ϣ����ô����Ҫ����Ӧ������
		if (mBirdata != null) {
			// name
			mTxtName.setText(mBirdata.mName);
			mTxtName.setEnabled(false); // ���ɸ���

			// type
			if (mBirdata.mbir_type == 0) {
				mRadioG.check(R.id.add_bir_radio_type_solar);
				mDateType = 0;
			} else {
				mRadioG.check(R.id.add_bir_radio_type_lunar);
				mDateType = 1;
			}

			// date
			// mTxtDate.setText(mBirdata.getDateinfo());

			// datepicker
			int[] dateInt = mBirdata.getIntDate();
			mDatepicker.init(dateInt[0], dateInt[1] - 1, dateInt[2],
					new OnDateChangedListener() {
						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub
							setDateText();
						}
					});
			setDateText();
		}

		// ���ð�ť��Ӧ�Ĺ���
		mAddBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mTxtName.getText().toString().isEmpty()) {
					Toast.makeText(getApplicationContext(), "��, TA�����ֿɲ�����!",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (!canContinue()) {
					Toast.makeText(getApplicationContext(), "��, ũ�����û��31Ŷ!",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (mWorkType == 0) {
					update();
				} else {
					add();
				}
				mActivity.finish();
			}
		});

		// �ʵ���ɾ������
		mAddBtn.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				// ֻ���ڸ��µ�ģʽ�²���ɾ���Ĺ���
				if (mWorkType == 0) {
					// ������ɫ������
					mAddBtn.setBackgroundResource(R.drawable.btn_style_red);
					mAddBtn.setText(R.string.add_bir_item_btn_txt_delete);
					Intent intent = new Intent(EditBirthdayActivity.this,
							ConfirmDialogActivity.class);
					intent.putExtra("Title", "����");
					intent.putExtra("Msg", "����ɾ��TA���������ѣ�����\n���ɻָ�, ȷ�ϼ���ô��");
					intent.putExtra("RequestCode",
							ConfirmDialogActivity.DELETE_ONE_CODE);
					startActivityForResult(intent,
							ConfirmDialogActivity.DELETE_ONE_CODE);
				}

				return false;
			}
		});
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.finish();
			}
		});

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.finish();
			}
		});
	}

	private void setDateText() {
		int year, month, day;
		year = mDatepicker.getYear();
		month = mDatepicker.getMonth() + 1;
		day = mDatepicker.getDayOfMonth();
		String date = null;
		if (mDateType == 0) {
			date = "(����)" + year + "-" + month + "-" + day;
		} else {
			// ����û��31
			if (day == 31) {
				date = "����,����ũ��û��31Ŷ...";
			} else {
				String temp = year + "-" + month + "-" + day;
				date = "(ũ��)" + year + "-"
						+ LunarCalendar.getChineseMonth(temp) + "-"
						+ LunarCalendar.getChineseDays(temp);
			}

		}
		mTxtDate.setText(date);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ֻ���ڸ��µ�ģʽ�²�������
		if (mWorkType == 0) {
			// ���Ը��ݶ���������������Ӧ�Ĳ���
			// ���������ý���
			mAddBtn.setBackgroundResource(R.drawable.btn_style_green);
			mAddBtn.setText(R.string.add_bir_item_btn_txt_update);

			int chooseCode = data.getIntExtra("ResultCode", 0);
			switch (resultCode) {
			case ConfirmDialogActivity.DELETE_ONE_CODE:
				if (chooseCode == ConfirmDialogActivity.RESULT_CODE_POSITIVE) {
					delete();
					// �رո�activity
					finish();
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void update() {
		int year, month, day;
		BirthdayData bir = new BirthdayData();
		bir._id = 0;
		bir.mName = mTxtName.getText().toString();
		bir.mbir_type = mDateType;
		year = mDatepicker.getYear();
		month = mDatepicker.getMonth() + 1;
		day = mDatepicker.getDayOfMonth();
		bir.mbir_date = year + "-" + month + "-" + day;

		mBirDBManager.open();
		mBirDBManager.update(bir);
		mBirDBManager.close();
	}

	private void add() {
		int year, month, day;
		BirthdayData bir = new BirthdayData();
		bir._id = 0;
		bir.mName = mTxtName.getText().toString();
		bir.mbir_type = mDateType;
		year = mDatepicker.getYear();
		month = mDatepicker.getMonth() + 1;
		day = mDatepicker.getDayOfMonth();
		bir.mbir_date = year + "-" + month + "-" + day;

		mBirDBManager.open();
		mBirDBManager.insert(bir);
		mBirDBManager.close();
	}

	private void delete() {
		int year, month, day;
		BirthdayData bir = new BirthdayData();
		bir._id = 0;
		bir.mName = mTxtName.getText().toString();
		bir.mbir_type = mDateType;
		year = mDatepicker.getYear();
		month = mDatepicker.getMonth() + 1;
		day = mDatepicker.getDayOfMonth();
		bir.mbir_date = year + "-" + month + "-" + day;

		mBirDBManager.open();
		mBirDBManager.delete(bir);
		mBirDBManager.close();
	}

	private boolean canContinue() {
		int day;
		day = mDatepicker.getDayOfMonth();
		if (day == 31 && mDateType == 1) {
			return false;
		}
		return true;
	}

	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;

		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}

		return npList;
	}

	/*
	 * private EditText findEditText(NumberPicker np) { if (null != np) { for
	 * (int i = 0; i < np.getChildCount(); i++) { View child = np.getChildAt(i);
	 * 
	 * if (child instanceof EditText) { return (EditText)child; } } }
	 * 
	 * return null; }
	 */

	private void setNumberPickerTextColor(ViewGroup viewGroup) {
		List<NumberPicker> npList = findNumberPicker(viewGroup);
		if (null != npList) {
			for (NumberPicker np : npList) {
				np.setBackgroundColor(Color.BLACK);
				// EditText et = findEditText(np);
				// et.setTextColor(Color.BLACK);
				// et.setHintTextColor(Color.BLACK);
			}
		}
	}
}
