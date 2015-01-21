package com.vistar.vattendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OptionMenu extends Activity {
	private SharedPreferences spteacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option_menu);
		TextView tname = (TextView) findViewById(R.id.tv_teachername);
		spteacher = this.getSharedPreferences(
				"com.vistar.vattendance.spteacher", Context.MODE_PRIVATE);
		String teachername = spteacher.getString("name", "");
		tname.setText(teachername);
	}

	public void bAttendance(View arg0) {
		Intent i = new Intent(this, TakeAttendance.class);
		startActivity(i);
	}

	public void bTestschedule(View arg1) {
		Intent i = new Intent(this, TestSchedule.class);
		startActivity(i);
	}

	public void bMarkupload(View arg2) {
		Intent i = new Intent(this, MarkUpload.class);
		startActivity(i);
	}

	public void bViewdata(View arg3) {
		Intent i = new Intent(this, ViewData.class);
		startActivity(i);
	}

}
