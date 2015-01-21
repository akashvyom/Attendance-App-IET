package com.vistar.vattendance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitAttendance extends Activity {
	private ProgressDialog prodia;
	private String serverAtt;
	private SharedPreferences sp1trueattendance;
	private SharedPreferences sp2trueattendance;

	private class UploadAttendanceTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				return UploadAttendance(arg0[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Unable to retrieve data";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			// Log.d("result", result);
			prodia.dismiss();
			result = result.trim();
			JSONObject respDetail;
			try {
				respDetail = new JSONObject(result);
				JSONArray respAttendance = respDetail
						.getJSONArray("attendance");
				for (int i = 0; i < respAttendance.length(); i++) {
					JSONObject restAtt = respAttendance.getJSONObject(i);
					serverAtt = restAtt.getString("attendance");

				}
				if (serverAtt.contentEquals(attvariable)) {
					char[] att = attvariable.toCharArray();
					int len = attvariable.length();
					for (int i = 0; i < len; i++) {
						String att2 = load1attendance(TakeAttendance.rollno[i + 1]);
						save2attendance(TakeAttendance.rollno[i + 1], att2);
						save1attendance(TakeAttendance.rollno[i + 1], att[i]);
					}

					Toast.makeText(SubmitAttendance.this,
							"Attendance Successfully Uploaded",
							Toast.LENGTH_LONG).show();
					SubmitAttendance.this.finish();
				} else {
					Toast.makeText(
							SubmitAttendance.this,
							"Error:Please clear all data and start application again",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private String load1attendance(String string) {
			// TODO Auto-generated method stub
			String attendance = sp1trueattendance.getString(string, "");
			return attendance;
		}

		private void save1attendance(String string, char c) {
			// TODO Auto-generated method stub
			SharedPreferences.Editor editor = sp1trueattendance.edit();
			editor.putString(string, c + "");
			editor.commit();

		}

		private void save2attendance(String string, String value) {
			// TODO Auto-generated method stub
			SharedPreferences.Editor editor = sp2trueattendance.edit();
			editor.putString(string, value);
			editor.commit();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			prodia = ProgressDialog.show(SubmitAttendance.this,
					"Uploading Attendance", "Please wait...");
		}

		private String UploadAttendance(String string) throws IOException {
			// TODO Auto-generated method stub
			InputStream is = null;
			String stringContent = null;
			int len = 5000;
			try {
				// Log.d("akash", string);
				URL url = new URL(string);
				// Log.d("url", String.valueOf(url));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// Log.d("connection open", "conn");
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);

				// query
				conn.connect();
				// Log.d("connect", "connected");
				int response = conn.getResponseCode();
				Log.d("response", String.valueOf(response));

				is = conn.getInputStream();
				stringContent = readIt(is, len);
				return stringContent;
			} finally {
				is.close();
			}
		}

		public String readIt(InputStream stream, int len) throws IOException,
				UnsupportedEncodingException {
			Reader reader = null;
			reader = new InputStreamReader(stream, "UTF-8");
			char[] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}

	}

	private SharedPreferences spteacher;
	private SharedPreferences spattendance;
	private String attvariable;
	private String sclass;
	private String school;
	private String sess;
	private String sday;
	private String smonth;
	private String teacherid;
	private Calendar calendar;
	private String atturl;
	private TextView tvtotal;
	private TextView tvpresent;
	private TextView tvabsent;
	private int pr;
	private int ab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_attendance);
		spteacher = this.getSharedPreferences(
				"com.vistar.vattendance.spteacher", Context.MODE_PRIVATE);
		tvtotal = (TextView) findViewById(R.id.tv_total);
		tvpresent = (TextView) findViewById(R.id.tv_present);
		tvabsent = (TextView) findViewById(R.id.tv_absent);
		spattendance = this.getSharedPreferences(
				"com.vistar.vattendance.spattendance", Context.MODE_PRIVATE);
		sp1trueattendance = this.getSharedPreferences(
				"com.vistar.vattendance.sp1trueattendance",
				Context.MODE_PRIVATE);
		sp2trueattendance = this.getSharedPreferences(
				"com.vistar.vattendance.sp2trueattendance",
				Context.MODE_PRIVATE);
		teacherid = spteacher.getString("tid", "");
		char[] usertransformation = teacherid.toCharArray();
		school = "" + usertransformation[0] + usertransformation[1]
				+ usertransformation[2] + usertransformation[3];
		sclass = "" + usertransformation[4] + usertransformation[5]
				+ usertransformation[6] + usertransformation[7];
		// Log.d("school", school);
		// Log.d("class", sclass);
		Intent i = getIntent();
		attvariable = i.getStringExtra("attdata");

		// marking in activity
		int total = attvariable.length();
		char[] checkingpresent = attvariable.toCharArray();
		for (int im = 0; im < total; im++) {

			String chck = "" + checkingpresent[im];
			if (chck.contentEquals("1")) {
				pr++;
			} else {
				ab++;
			}
		}
		tvtotal.setText(tvtotal.getText().toString() + " " + total);
		tvpresent.setText(tvpresent.getText().toString() + " " + pr);
		tvabsent.setText(tvabsent.getText().toString() + " " + ab);
		// calendar
		calendar = Calendar.getInstance();
		int date = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		sday = "" + date;
		smonth = "" + month;
		sclass = "9999";
		atturl = "http://www.vistarpvtltd.co.nf/vksappattendance.php?sess=1314&uid="
				+ teacherid
				+ "&att="
				+ attvariable
				+ "&day="
				+ sday
				+ "&month=" + smonth + "&school=" + school + "&class=" + sclass;

	}

	public void clickSubmit(View v) {
		atturl = "http://www.vistarpvtltd.co.nf/vksappattendance.php?sess=1314&uid="
				+ teacherid
				+ "&att="
				+ attvariable
				+ "&day="
				+ sday
				+ "&month=" + smonth + "&school=" + school + "&class=" + sclass;
		new UploadAttendanceTask().execute(atturl);
	}

	public void clickEdit(View v) {
		Intent iedit = new Intent(this, EditAttendance.class);
		iedit.putExtra("attendance", attvariable);
		startActivityForResult(iedit, 1);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		attvariable = data.getStringExtra("newattendance");
		int total = attvariable.length();
		char[] checkingpresent = attvariable.toCharArray();
		pr = 0;
		ab = 0;
		for (int im = 0; im < total; im++) {
			// Log.d("attchar", attvariable);
			String chck = "" + checkingpresent[im];
			if (chck.contentEquals("1")) {
				pr++;
			} else {
				ab++;
			}
		}
		tvtotal.setText("Total no of students:" + " " + total);
		tvpresent.setText("Total no of present:" + " " + pr);
		tvabsent.setText("Total no of absent:" + " " + ab);
	}

}
