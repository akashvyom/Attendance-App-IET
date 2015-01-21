package com.vistar.vattendance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import com.vistar.vattendance.VAttendanceContract.StudentDetails;

public class VAttendanceDbHelper extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = " ,";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
			+ StudentDetails.TABLE_NAME
			+ " ("
			+ StudentDetails._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT"
			+ COMMA_SEP
			+ StudentDetails.CN_SID
			+ TEXT_TYPE
			+ COMMA_SEP
			+ StudentDetails.CN_NAME
			+ TEXT_TYPE
			+ COMMA_SEP
			+ StudentDetails.CN_ROLLNO
			+ TEXT_TYPE
			+ COMMA_SEP
			+ StudentDetails.CN_CLASS + TEXT_TYPE + " )";
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ StudentDetails.TABLE_NAME;
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "1111school.db";

	public VAttendanceDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		arg0.execSQL(SQL_DELETE_ENTRIES);
		onCreate(arg0);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

}
