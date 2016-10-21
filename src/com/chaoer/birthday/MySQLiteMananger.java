package com.chaoer.birthday;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteMananger extends SQLiteOpenHelper {

	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "birthday.db";
	private final static String DATABASE_TABLE_NAME = "t_chaoer_bir";

	private final static String KEY_ID = "_id";
	private final static String KEY_NAME = "name";
	private final static String KEY_BIRTHDAYDATE = "bir_date";
	private final static String KEY_BIRTHDAYTYPE = "bir_type";

	private final static String DATABASE_TABLE_NAME2 = "t_chaoer_reminder";
	private final static String KEY_ID2 = "_id";
	private final static String KEY_AUTO_REMIDE = "bOpen";
	private final static String KEY_REMIND_TIME = "time";

	private SQLiteDatabase mdb = null;

	public MySQLiteMananger(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public MySQLiteMananger(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 创建两个表
		// 生日表
		String sqlCreate = "CREATE TABLE IF NOT EXISTS[" + DATABASE_TABLE_NAME
				+ "](" + "[" + KEY_ID + "] INTEGER PRIMARY KEY," + "["
				+ KEY_NAME + "] VARCHAR(20) NOT NULL ON CONFLICT FAIL," + "["
				+ KEY_BIRTHDAYDATE + "] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
				+ "[" + KEY_BIRTHDAYTYPE
				+ "] INTEGER(1) NOT NULL ON CONFLICT FAIL," + "UNIQUE ("
				+ KEY_NAME + "),"
				+ "CONSTRAINT[sqlite_autoindex_t_chaoer_bir_1])";
		db.execSQL(sqlCreate);

		// 提醒时间的表
		String sqlCreate2 = "CREATE TABLE IF NOT EXISTS["
				+ DATABASE_TABLE_NAME2 + "](" + "[" + KEY_ID2
				+ "] INTEGER PRIMARY KEY," + "[" + KEY_AUTO_REMIDE
				+ "] INTEGER(1) NOT NULL ON CONFLICT FAIL," + "["
				+ KEY_REMIND_TIME + "] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
				+ "CONSTRAINT[sqlite_autoindex_t_chaoer_bir_2])";
		db.execSQL(sqlCreate2);
		// 最开始就默认打开提醒
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_AUTO_REMIDE, true);
		initialValues.put(KEY_REMIND_TIME, "12-00");
		db.insert(DATABASE_TABLE_NAME2, null, initialValues);
		
		/*
		 * String sqlCreate = "CREATE TABLE["+DATABASE_TABLE_NAME+"](" +
		 * "["+KEY_ID+"] INTEGER PRIMARY KEY," +
		 * "["+KEY_NAME+"] VARCHAR(20) NOT NULL ON CONFLICT FAIL," +
		 * "["+KEY_BIRTHDAYDATE+"] VARCHAR(20) NOT NULL ON CONFLICT FAIL," +
		 * "["+KEY_BIRTHDAYTYPE+"] INTEGER(1) NOT NULL ON CONFLICT FAIL," +
		 * "CONSTRAINT[sqlite_autoindex_t_chaoer_bir_1])," +
		 * "CREATE TABLE["+DATABASE_TABLE_NAME2+"](" +
		 * "["+KEY_ID2+"] INTEGER PRIMARY KEY," +
		 * "["+KEY_AUTO_REMIDE+"] INTEGER(1) NOT NULL ON CONFLICT FAIL," +
		 * "["+KEY_REMIND_TIME+"] VARCHAR(20) NOT NULL ON CONFLICT FAIL," +
		 * "CONSTRAINT[sqlite_autoindex_t_chaoer_bir_1])";
		 * db.execSQL(sqlCreate);
		 */
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// 目前默认不对数据库进行升级
	}

	public boolean open() {
		boolean bOpened = false;
		mdb = this.getWritableDatabase();
		if (mdb != null) {
			bOpened = true;
		}
		return bOpened;
	}

	public void analog() {
		BirthdayData data1 = new BirthdayData("小明", "1992-12-25", 0);
		BirthdayData data2 = new BirthdayData("小华", "1992-12-25", 1);
		BirthdayData data3 = new BirthdayData("小刚", "1992-01-01", 0);
		BirthdayData data4 = new BirthdayData("小丽", "1992-01-01", 1);
		BirthdayData data5 = new BirthdayData("小婉", "1991-12-10", 1);
		BirthdayData data6 = new BirthdayData("小何", "1993-02-03", 0);
		BirthdayData data7 = new BirthdayData("小苗", "1993-01-12", 1);
		BirthdayData data8 = new BirthdayData("小志", "1992-01-28", 1);
		BirthdayData data9 = new BirthdayData("小爱", "1992-01-28", 0);
		insert(data1);
		insert(data2);
		insert(data3);
		insert(data4);
		insert(data5);
		insert(data6);
		insert(data7);
		insert(data8);
		insert(data9);
	}

	public void close() {
		mdb.close();
		// this.close();
	}

	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}

	public boolean insert(BirthdayData data) {
		long resCode = 0;		
		// 如果没有设定name的唯一性，那么需要先检查
		List<BirthdayData> queryName = query(data.mName);
		if(queryName.size() > 0)
		{
			return false;
		}
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, data.mName);
		initialValues.put(KEY_BIRTHDAYDATE, data.mbir_date);
		initialValues.put(KEY_BIRTHDAYTYPE, data.mbir_type);
		
		resCode = mdb.insert(DATABASE_TABLE_NAME, null, initialValues);
		if (resCode == 0) {
			return false;
		}
		return true;
	}

	public boolean delete(BirthdayData data) {
		long resCode = 0;
		// String deleteString = KEY_NAME + "=" + data.mName;
		// resCode = mdb.delete(DATABASE_TABLE_NAME, deleteString, null);
		String whereClause = KEY_NAME + "=?";// 删除的条件
		String[] whereArgs = { data.mName };// 删除的条件参数
		resCode = mdb.delete(DATABASE_TABLE_NAME, whereClause, whereArgs);
		if (resCode > 0) {
			return true;
		}
		return false;
	}

	public boolean update(BirthdayData data) {
		long resCode = 0;
		ContentValues initialValues = new ContentValues();// 实例化ContentValues
		initialValues.put(KEY_NAME, data.mName); // 添加要更改的字段及内容
		initialValues.put(KEY_BIRTHDAYDATE, data.mbir_date);
		initialValues.put(KEY_BIRTHDAYTYPE, data.mbir_type);
		String whereClause = KEY_NAME + "=?";// 修改的条件
		String[] whereArgs = { data.mName };// 修改的条件参数

		resCode = mdb.update(DATABASE_TABLE_NAME, initialValues, whereClause,
				whereArgs);// 执行修改

		if (resCode > 0) {
			return true;
		}
		return false;
	}

	public Cursor queryC() {
		Cursor alldata = null;
		alldata = mdb.query(DATABASE_TABLE_NAME, new String[] { KEY_NAME,
				KEY_BIRTHDAYDATE, KEY_BIRTHDAYTYPE }, null, null, null, null,
				KEY_NAME);
		if (!alldata.moveToFirst()) {
			return null;
		}
		return alldata;
	}

	public boolean insert(List<BirthdayData> birthdays) {
		boolean bsuccess = false;
		mdb.beginTransaction(); // 开始事务
		try {
			String sql = "INSERT INTO" + DATABASE_TABLE_NAME
					+ "VALUES(null, ?, ?, ?)";
			for (BirthdayData bir : birthdays) {
				mdb.execSQL(sql, new Object[] { bir.mName, bir.mbir_date,
						bir.mbir_type });
			}
			mdb.setTransactionSuccessful(); // 设置事务完成
			bsuccess = true;
		} finally {
			mdb.endTransaction(); // 结束事务
		}
		return bsuccess;
	}

	public Cursor queryTheCursor() {
		String sql = "SELECT * FROM " + DATABASE_TABLE_NAME;
		Cursor alldata = mdb.rawQuery(sql, null);
		return alldata;
	}

	public boolean queryIsAutoRemind() {
		String sql = "SELECT * FROM " + DATABASE_TABLE_NAME2;
		Cursor c = mdb.rawQuery(sql, null);
		if (c.getCount() <= 0)
			return false;
		int open = 0;
		while (c.moveToNext()) {
			open = c.getInt(c.getColumnIndex(KEY_AUTO_REMIDE));
		}
		c.close();
		if (open > 0) {
			return true;
		} else {
			return false;
		}
	}

	public String queryRemindTime() {
		String sql = "SELECT * FROM " + DATABASE_TABLE_NAME2;
		Cursor c = mdb.rawQuery(sql, null);
		String time = null;
		while (c.moveToNext()) {
			time = c.getString(c.getColumnIndex(KEY_REMIND_TIME));
		}
		c.close();
		return time;
	}

	private void insertReminder(boolean bOpen, String time) {
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_AUTO_REMIDE, bOpen ? 1 : 0);
		initialValues.put(KEY_REMIND_TIME, time);

		mdb.insert(DATABASE_TABLE_NAME2, null, initialValues);
	}

	private void updateReminder(boolean bOpen, String time) {
		ContentValues initialValues = new ContentValues();// 实例化ContentValues
		initialValues.put(KEY_AUTO_REMIDE, bOpen ? 1 : 0);
		initialValues.put(KEY_REMIND_TIME, time);
		String whereClause = KEY_ID2 + "=?";// 修改的条件
		String[] whereArgs = { "1" };// 修改的条件参数

		mdb.update(DATABASE_TABLE_NAME2, initialValues, whereClause, whereArgs);// 执行修改
	}

	public void setReminder(boolean bOpen, String time) {
		if(queryRemindTime() == null)
		{
			// 没有数据，那么实际执行插入操作
			insertReminder(bOpen, time);
		}
		else
		{
			// 有一条记录，那么实际执行更新操作
			updateReminder(bOpen, time);
		}
		
	}

	public List<BirthdayData> query() {
		ArrayList<BirthdayData> birthdays = new ArrayList<BirthdayData>();
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			BirthdayData bir = new BirthdayData();
			bir._id = c.getInt(c.getColumnIndex(KEY_ID));
			bir.mName = c.getString(c.getColumnIndex(KEY_NAME));
			bir.mbir_date = c.getString(c.getColumnIndex(KEY_BIRTHDAYDATE));
			bir.mbir_type = c.getInt(c.getColumnIndex(KEY_BIRTHDAYTYPE));

			birthdays.add(bir);
		}
		c.close();
		return birthdays;
	}

	public List<BirthdayData> query(String name) {
		ArrayList<BirthdayData> birthdays = new ArrayList<BirthdayData>();
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			BirthdayData bir = new BirthdayData();
			bir._id = c.getInt(c.getColumnIndex(KEY_ID));
			bir.mName = c.getString(c.getColumnIndex(KEY_NAME));
			bir.mbir_date = c.getString(c.getColumnIndex(KEY_BIRTHDAYDATE));
			bir.mbir_type = c.getInt(c.getColumnIndex(KEY_BIRTHDAYTYPE));

			if (bir.mName.contains(name)) {
				birthdays.add(bir);
			}
		}
		c.close();
		return birthdays;
	}
}
