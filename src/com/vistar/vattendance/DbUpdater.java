package com.vistar.vattendance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.vistar.vattendance.VAttendanceContract.StudentDetails;

public class DbUpdater extends Activity {
	private SharedPreferences spupdate;
	private SharedPreferences spteacher;
	private String userid;
	private String subject;
	private String classes;
	private String updurl;
	private WebView wvupdate;
	private TextView haha;
	public ProgressDialog proDia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db_updater);
		wvupdate = (WebView) findViewById(R.id.wv_update);
		haha = (TextView) findViewById(R.id.textView1);
		spteacher = DbUpdater.this.getSharedPreferences(
				"com.vistar.vattendance.spteacher", Context.MODE_PRIVATE);
		userid = spteacher.getString("tid", "");
		subject = spteacher.getString("subject", "");
		classes = Integer.parseInt(subject) / 100 + "";
		spupdate = DbUpdater.this.getSharedPreferences(
				"com.vistar.vattendance.spupd", Context.MODE_PRIVATE);
		String updold = spupdate.getString(classes + "old", "0");
		String updnew = spupdate.getString(classes + "new", "1");
		Log.d("check", "check1");

		if (updold.contentEquals(updnew)) {
			Toast.makeText(DbUpdater.this, "Databse Already Updated",
					Toast.LENGTH_LONG).show();
			Intent i = new Intent(this, OptionMenu.class);
			startActivity(i);
			this.finish();
		}

		else {

			updurl = "http://www.ietap.co.nf/appstudentdata.php?uid=" + userid
					+ "&class=" + classes;
			wvupdate.loadUrl(updurl);

			// Toast.makeText(this, updurl,Toast.LENGTH_LONG).show();

		}
	}

	private class UpdateStudentDatabase extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				return DownloadUrl(arg0[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Unable to Connect";
			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			proDia = ProgressDialog.show(DbUpdater.this, "Updating",
					"Please wait...");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			proDia.dismiss();
			result = result.trim();
			// Log.d("result", result.length()+"");

			proDia = ProgressDialog.show(DbUpdater.this, "Saving data",
					"Please wait...");
			JSONObject respDetail;
			// Log.d("resultjson", result);

			try {

				respDetail = new JSONObject(result);

				JSONArray respStudent = respDetail.getJSONArray("students");

				for (int i = 0; i < respStudent.length(); i++) {
					JSONObject jstudent = respStudent.getJSONObject(i);
					String sid = jstudent.getString("i");
					String name = jstudent.getString("n");
					String rollno = jstudent.getString("r");
					String sclass = jstudent.getString("c");

					VAttendanceDbHelper mDbHelper = new VAttendanceDbHelper(
							DbUpdater.this);
					SQLiteDatabase db = mDbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put(StudentDetails.CN_SID, sid);
					values.put(StudentDetails.CN_NAME, name);
					values.put(StudentDetails.CN_ROLLNO, rollno);
					values.put(StudentDetails.CN_CLASS, sclass);
					db.insert(StudentDetails.TABLE_NAME, null, values);

					// Log.d("insertname", name);

				}
				proDia.dismiss();

				String updnew = spupdate.getString(classes + "new", "");
				saveUpdate(classes + "old", updnew);

				Intent i = new Intent(DbUpdater.this, OptionMenu.class);
				startActivity(i);
				DbUpdater.this.finish();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void saveUpdate(String string, String value) {
			// TODO Auto-generated method stub

			spupdate = DbUpdater.this.getSharedPreferences(
					"com.vistar.vattendance.spupd", Context.MODE_PRIVATE);
			Editor edit = spupdate.edit();
			edit.putString(string, value);
			edit.commit();
		}
	}

	private String DownloadUrl(String string) throws IOException {
		InputStream is = null;
		String stringContent = null;
		int len = 10000;
		try {
			// Log.d("Akash", string);
			URL url = new URL(string);
			// Log.d("url", String.valueOf(url));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// Log.d("connection open", "conn");
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Log.d("check", "check3");

			// query

			conn.connect();

			// Log.d("connect", "connected");
			// Log.d("datalength", conn.getContentLength()+"");
			// int response = conn.getResponseCode();
			// Log.d("response", String.valueOf(response));
			len = conn.getContentLength();
			// Log.d("string len", len+"");

			is = conn.getInputStream();
			Log.d("is", is.read() + "");
			stringContent = readIt(is, len);
			Log.d("stringcontent", stringContent);
			conn.disconnect();

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

	public void clickUpdate(View v) {
		new UpdateStudentDatabase().execute(updurl);

	}
}
