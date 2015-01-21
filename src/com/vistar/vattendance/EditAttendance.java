package com.vistar.vattendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditAttendance extends Activity {
	private String attvariable;
	private char[] cattvariable;
	private SharedPreferences spteacher;
	private SharedPreferences spattendance;
	private EditText etrollno;
	private TextView tvattendanceof;
	private String srollno;
	private String frollno;
	private String newattvariable = "";
	private String ratt;
	private Button battendance;
	private Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_attendance);
		etrollno = (EditText) findViewById(R.id.et_rollno);
		battendance = (Button) findViewById(R.id.b_attendance);
		tvattendanceof = (TextView) findViewById(R.id.tv_attendanceof);
		i = getIntent();
		attvariable = i.getStringExtra("attendance");
		cattvariable = attvariable.toCharArray();
	}

	public void clickGetAttendance(View v) {
		srollno = Integer.parseInt(etrollno.getText().toString()) + "";
		if (Integer.parseInt(srollno) > attvariable.length()) {
			Toast.makeText(EditAttendance.this,
					"Wrong roll number.\nPlease insert correct roll number.",
					Toast.LENGTH_SHORT).show();
		} else {
			String nameofstudent = TakeAttendance.nameofstudent[Integer
					.parseInt(srollno)];
			tvattendanceof.setText(nameofstudent + " is :");
			spteacher = this.getSharedPreferences(
					"com.vistar.vattendance.spteacher", Context.MODE_PRIVATE);
			String tid = spteacher.getString("tid", "");
			frollno = srollno;
			spattendance = this
					.getSharedPreferences(
							"com.vistar.vattendance.spattendance",
							Context.MODE_PRIVATE);
			ratt = spattendance.getString(frollno, "");
			cattvariable[Integer.parseInt(srollno) - 1] = ratt.charAt(0);
			if (ratt.contentEquals("1")) {
				battendance.setText("Present");
			} else if (ratt.contentEquals("0")) {
				battendance.setText("Absent");
			}
		}
	}

	public void clickAttendance(View v) {
		if (battendance.getText().toString().contentEquals("Present")) {
			battendance.setText("Absent");
		} else if (battendance.getText().toString().contentEquals("Absent")) {
			battendance.setText("Present");
		}
	}

	public void clickSubmit(View v) {
		String checkatt = "";
		if (battendance.getText().toString().contentEquals("Present")) {
			checkatt = "1";
		} else if (battendance.getText().toString().contentEquals("Absent")) {
			checkatt = "0";
		}
		spattendance = this.getSharedPreferences(
				"com.vistar.vattendance.spattendance", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = spattendance.edit();
		edit.putString(frollno, checkatt);
		edit.commit();
		cattvariable[Integer.parseInt(srollno) - 1] = checkatt.charAt(0);
		for (int i = 0; i < cattvariable.length; i++) {
			newattvariable = newattvariable + cattvariable[i];
		}
		i.putExtra("newattendance", newattvariable);
		setResult(RESULT_OK, i);
		finish();
	}
}
