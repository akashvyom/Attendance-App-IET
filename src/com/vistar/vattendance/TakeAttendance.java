package com.vistar.vattendance;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.vistar.vattendance.VAttendanceContract.StudentDetails;

public class TakeAttendance extends Activity {

	private TextView tvrollno;
	private TextView tvname;
	private TextView tvdate;
	private TextView tvclass;
	private TextView tvatt1;
	private TextView tvatt2;
	private TextView tvrollnoreal;
	private TextView tvbranchrollno;

	private SharedPreferences spteacher;

	private SharedPreferences spattendance;
	private SharedPreferences sp1trueattendance;
	private SharedPreferences sp2trueattendance;

	static String[] nameofstudent = new String[100];
	static String[] rollno = new String[100];
	private String[] srollno = new String[100];
	private String[] attendance1 = new String[100];
	private String[] attendance2 = new String[100];
	private String attendancevariable = "";
	private static String classes;
	private String tid;
	private int i = 1;
	private int[] brollno = new int[100];
	private int[] rrollno = new int[100];
	private String[] srrollno = new String[100];
	private int noofrows = 0;
	private Calendar calendar;
	private static String sclass;
	private String detectclass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_attendance);
		spteacher = TakeAttendance.this.getSharedPreferences(
				"com.vistar.vattendance.spteacher", Context.MODE_PRIVATE);
		spattendance = TakeAttendance.this.getSharedPreferences(
				"com.vistar.vattendance.spattendance", Context.MODE_PRIVATE);
		classes = spteacher.getString("classes", "");
		// Log.d("class",classes);
		tid = spteacher.getString("tid", "");
		tvrollno = (TextView) findViewById(R.id.tv_rollno);
		tvbranchrollno = (TextView) findViewById(R.id.tv_branchrollno);
		tvrollnoreal = (TextView) findViewById(R.id.tv_realrollno);
		tvname = (TextView) findViewById(R.id.tv_nameofstudent);
		tvdate = (TextView) findViewById(R.id.tv_date);
		tvclass = (TextView) findViewById(R.id.tv_class);
		tvatt1 = (TextView) findViewById(R.id.tv_att1);
		tvatt2 = (TextView) findViewById(R.id.tv_att2);

		// getting date from calendar
		calendar = Calendar.getInstance();
		int date = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		String sdate = date + "/" + month + "/" + year;
		tvdate.setText(sdate);

		// loading previous attendances

		sp1trueattendance = TakeAttendance.this.getSharedPreferences(
				"com.vistar.vattendance.sp1trueattendance",
				Context.MODE_PRIVATE);
		sp2trueattendance = TakeAttendance.this.getSharedPreferences(
				"com.vistar.vattendance.sp2trueattendance",
				Context.MODE_PRIVATE);

		// loading class
		detectclass = detectClass(classes);
		tvclass.setText("Class: " + detectclass);

		// now SQL statement for loading data from database
		VAttendanceDbHelper mDbHelper = new VAttendanceDbHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		String[] projection = { StudentDetails.CN_SID, StudentDetails.CN_NAME,
				StudentDetails.CN_ROLLNO };
		String selection = StudentDetails.CN_CLASS + " LIKE ?";
		String[] selectionArgs = new String[] { "500" };
		String orderBy = StudentDetails.CN_SID + " ASC";

		Cursor c = db.query(StudentDetails.TABLE_NAME, projection, selection,
				selectionArgs, null, null, orderBy);
		c.moveToFirst();
		noofrows = c.getCount();
		// Log.d("noofrows", String.valueOf(noofrows));
		while (!c.isAfterLast()) {
			// Log.d("here", "ok");
			int indexSid = c.getColumnIndex(StudentDetails.CN_SID);
			int indexName = c.getColumnIndex(StudentDetails.CN_NAME);
			int indexRollno = c.getColumnIndex(StudentDetails.CN_ROLLNO);
			// Log.d("here", String.valueOf(indexName));

			rollno[i] = c.getString(indexSid);
			nameofstudent[i] = c.getString(indexName);
			srollno[i] = c.getString(indexRollno);
			int rollnocomp = Integer.parseInt(srollno[i]);
			brollno[i] = rollnocomp / 1000;
			rrollno[i] = rollnocomp % 1000;
			if ((rrollno[i] + "").length() == 1) {
				srrollno[i] = "00" + rrollno[i];
			}
			if ((rrollno[i] + "").length() == 2) {
				srrollno[i] = "0" + rrollno[i];
			}
			if ((rrollno[i] + "").length() == 3) {
				srrollno[i] = "" + rrollno[i];
			}
			// Log.d("name", nameofstudent[i]);
			attendance1[i] = load1attendance(rollno[i]);
			// Log.d("attendance1", attendance1[i]);
			attendance2[i] = load2attendance(rollno[i]);
			// Log.d("attendance2", attendance2[i]);
			if (attendance1[i].contentEquals("1")) {
				attendance1[i] = "P";
			} else if (attendance1[i].contentEquals("0")) {
				attendance1[i] = "A";
			} else {
				attendance1[i] = "N";
			}
			if (attendance2[i].contentEquals("1")) {
				attendance2[i] = "P";
			} else if (attendance1[i].contentEquals("0")) {
				attendance2[i] = "A";
			} else {
				attendance2[i] = "N";
			}
			// Log.d("here", "ok3");
			i++;
			// Log.d("i", String.valueOf(i));
			c.moveToNext();
		}
		tvname.setText(nameofstudent[1]);
		tvrollno.setText(classes + "001");
		tvatt1.setText(attendance1[1]);
		tvatt2.setText(attendance2[1]);
		tvbranchrollno.setText("(" + brollno[1]);
		tvrollnoreal.setText(srrollno[1] + ")");
	}

	public static String detectClass(String value) {
		// TODO Auto-generated method stub
		// Log.d("value", value);
		char[] classchar = value.toCharArray();
		String sem = "" + classchar[0];
		// Log.d("check", class);
		String branchcode = "" + classchar[1] + classchar[2];
		sclass = "Civil Engg.:V SEM";
		return sclass;
	}

	private String load2attendance(String string) {
		// TODO Auto-generated method stub
		String attendance = sp2trueattendance.getString(string, "");
		// Log.d("attendance2", attendance);
		return attendance;
	}

	private String load1attendance(String string) {
		// TODO Auto-generated method stub
		String attendance = sp1trueattendance.getString(string, "");
		// Log.d("attendance1", attendance);
		return attendance;
	}

	public void clickPresent(android.view.View v) {
		attendancevariable = attendancevariable + "1";
		String rno = tvrollno.getText().toString();
		int counter = Integer.parseInt(rno);
		counter++;
		// Log.d("counter", String.valueOf(counter));
		if (counter % 1000 <= noofrows) {
			tvname.setText(nameofstudent[counter % 1000]);
			tvrollno.setText("" + counter);
			tvatt1.setText(attendance1[counter]);
			tvatt2.setText(attendance2[counter]);
			tvbranchrollno.setText("(" + brollno[counter % 1000]);
			tvrollnoreal.setText(srrollno[counter % 1000] + ")");
		} else {
			char[] att = attendancevariable.toCharArray();
			int len = attendancevariable.length();
			for (int i = 1; i <= len; i++) {
				saveattendance(rollno[i], att[i - 1]);
			}
			Intent i = new Intent(this, SubmitAttendance.class);
			i.putExtra("attdata", attendancevariable);
			startActivity(i);
			this.finish();
		}
	}

	private void saveattendance(String string, char c) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor = spattendance.edit();
		editor.putString(string, c + "");
		editor.commit();
	}

	public void clickAbsent(android.view.View v) {
		attendancevariable = attendancevariable + "0";
		String rno = tvrollno.getText().toString();
		int counter = Integer.parseInt(rno);
		counter++;
		// Log.d("counter", String.valueOf(counter));
		if (counter % 1000 <= noofrows) {
			tvname.setText(nameofstudent[counter % 1000]);
			tvrollno.setText("" + counter);
			tvatt1.setText(attendance1[counter]);
			tvatt2.setText(attendance2[counter]);
			tvbranchrollno.setText("(" + brollno[counter % 1000]);
			tvrollnoreal.setText(srrollno[counter % 1000] + ")");
		} else {
			char[] att = attendancevariable.toCharArray();
			int len = attendancevariable.length();
			for (int i = 1; i <= len; i++) {
				saveattendance(rollno[i], att[i - 1]);
			}

			Intent i = new Intent(this, SubmitAttendance.class);
			i.putExtra("attdata", attendancevariable);
			startActivity(i);
			this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
