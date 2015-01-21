package com.vistar.vattendance;

import android.provider.BaseColumns;

public final class VAttendanceContract {
	public VAttendanceContract() {
	}

	public static abstract class StudentDetails implements BaseColumns {
		public static final String TABLE_NAME = "s_detail";
		public static final String CN_SID = "s_id";
		public static final String CN_NAME = "s_name";
		public static final String CN_ROLLNO = "s_rollno";
		public static final String CN_CLASS = "s_class";

	}

}
