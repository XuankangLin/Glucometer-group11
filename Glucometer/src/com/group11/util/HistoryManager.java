package com.group11.util;

import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * It deals with the History Records with database 
 */
public class HistoryManager {

	public static final int MAX_RESULTS = 30;
	
	public synchronized void addTestResult(Context context, TestResult result) {
		LinkedList<TestResult> list = this.getTestResults(context);
		list.addLast(result);

		if (list.size() > MAX_RESULTS) {
			list.removeFirst();
		}
		
		DBHelper helper = new DBHelper(context);
		helper.onUpgrade(helper.getWritableDatabase(), 1, 1);

		for (TestResult tr : list) {
			helper.insert(tr.getValue(), tr.getUnit().ordinal(), tr.getTime().getTime());
		}
	}
	
	public LinkedList<TestResult> getTestResults(Context context) {
		DBHelper helper = new DBHelper(context);
		
		Cursor cursor = helper.selectAll();
		cursor.moveToFirst();

		LinkedList<TestResult> resultList = new LinkedList<TestResult>();
		for (int i = 0; i < cursor.getCount(); i++) {
			TestResult info = new TestResult(cursor.getDouble(1),
					cursor.getInt(2), cursor.getLong(3));
			resultList.add(info);
			cursor.moveToNext();
		}
		return resultList;
	}
	
	
	private static class DBHelper extends SQLiteOpenHelper {
		private static final String DB_NAME = "glucometer_db";
		private static final int DB_VERSION = 1;

		private static final String TABLE_NAME = "glucometer_table";
		private static final String FIELD_ID = "_id";
		private static final String FIELD_VALUE = "result_value";
		private static final String FIELD_UNIT = "result_unit";
		private static final String FIELD_TIME = "result_time";
		
		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + TABLE_NAME + " (" + FIELD_ID
					+ " INTEGER primary key autoincrement,  " + FIELD_VALUE
					+ " real,  " + FIELD_UNIT + " smallint,  " + FIELD_TIME
					+ " int)";
			//TODO to change text into long/double/...
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
			db.execSQL(sql);
			onCreate(db);
		}
		
		public long insert(double value, int unit, long time) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(FIELD_VALUE, value);
			cv.put(FIELD_UNIT, unit);
			cv.put(FIELD_TIME, time);

			long row = db.insert(TABLE_NAME, null, cv);
			return row;
		}
		
		public Cursor selectAll() {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
			return cursor;
		}
		
		public void delete(int id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String where = FIELD_ID + " = ?";
			String[] whereValue = {Integer.toString(id)};
			
			db.delete(TABLE_NAME, where, whereValue);
		}
		
		public void update (int id, double value, int unit, long time) {
			SQLiteDatabase db = this.getWritableDatabase();
			String where = FIELD_ID + " = ?";
			String[] whereValue = {Integer.toString(id)};
			
			ContentValues cv = new ContentValues();
			cv.put(FIELD_VALUE, value);
			cv.put(FIELD_UNIT, unit);
			cv.put(FIELD_TIME, time);

			db.update(TABLE_NAME, cv, where, whereValue);
		}
	}
}
