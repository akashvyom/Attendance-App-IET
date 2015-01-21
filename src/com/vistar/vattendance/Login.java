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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {
	static final String EXTRA_USERID = null;
	private Button blogin;
	private EditText etusername;
	private EditText etpassword;
	private CheckBox cbautologin;
	private String stringUsername;
	private String stringPassword;
	private String stringUrl;
	private String updatevar;
	private ProgressDialog progdiag;
	private SharedPreferences spupdate;
	private SharedPreferences spteacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		blogin = (Button) findViewById(R.id.b_login);
		etusername = (EditText) findViewById(R.id.et_username);
		etpassword = (EditText) findViewById(R.id.et_password);
		cbautologin = (CheckBox) findViewById(R.id.cb_autologin);
		blogin.setOnClickListener(this);
		loadPrefs();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		stringUsername = etusername.getText().toString();
		stringPassword = etpassword.getText().toString();
		savePrefs("AUTOLOGIN", cbautologin.isChecked());
		if (cbautologin.isChecked()) {
			savePrefs("USERNAME", stringUsername);
			savePrefs("PASSWORD", stringPassword);
		}
		if (etusername.getText().toString().length() != 6) {
			Toast.makeText(Login.this, "Please enter 6 digit username",
					Toast.LENGTH_LONG).show();
			etusername.requestFocus();
			etusername.setText("");
		} else {

			stringUrl = "http://www.ietap.co.nf/applogin.php?uid="
					+ stringUsername + "&pwd=" + stringPassword;
			// Log.d("url", stringUrl);
			new DownloadWebpageTask().execute(stringUrl);
			// Log.d("time", "akash");
		}
	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progdiag = ProgressDialog.show(Login.this, "Logging",
					"Please wait...");
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				return DownloadURL(arg0[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return "Unable to connect";
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			progdiag.dismiss();
			Log.d("result", result.trim().length() + "");
			result = result.trim();
			Log.d("result", result);
			if (result.trim().contentEquals("")) {
				Toast.makeText(Login.this, "Wrong Username or Password",
						Toast.LENGTH_LONG).show();
				etusername.setText("");

				etpassword.setText("");

			} else {
				try {

					JSONObject respDetail = new JSONObject(result);
					JSONArray respTeacher = respDetail.getJSONArray("teacher");
					for (int i = 0; i < respTeacher.length(); i++) {
						JSONObject jteacher = respTeacher.getJSONObject(i);
						String jfail = jteacher.getString("fail");
						if (jfail.contentEquals("1")) {
							String tname = jteacher.getString("name");
							String subject = jteacher.getString("av");
							String classes = Integer.parseInt(subject) / 100
									+ "";

							updatevar = jteacher.getString(classes);
							saveTeacher("tid", etusername.getText().toString());
							saveTeacher("name", tname);
							saveTeacher("classes", classes);
							saveTeacher("subject", subject);
							saveUpdate(classes + "new", updatevar);
							Intent intent = new Intent(Login.this,
									DbUpdater.class);
							startActivity(intent);
						} else {
							Toast.makeText(Login.this,
									"Wrong Username or Password",
									Toast.LENGTH_LONG).show();
							etusername.setText("");

							etpassword.setText("");
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		private void saveUpdate(String string, String updatevar) {
			// TODO Auto-generated method stub
			spupdate = Login.this.getSharedPreferences(
					"com.vistar.vattendance.spupd", Context.MODE_PRIVATE);
			Editor edit = spupdate.edit();
			edit.putString(string, updatevar);
			edit.commit();
		}

	}

	private void saveTeacher(String string, String value) {
		// TODO Auto-generated method stub
		spteacher = Login.this.getSharedPreferences(
				"com.vistar.vattendance.spteacher", Context.MODE_PRIVATE);
		Editor edit = spteacher.edit();
		edit.putString(string, value);
		edit.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */

	private String DownloadURL(String string) throws IOException {

		// TODO Auto-generated method stub
		InputStream is = null;
		String stringContent = null;
		int len = 500;
		try {
			// Log.d("akash", string);
			URL url = new URL(string);
			// Log.d("url", String.valueOf(url));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
			len = conn.getContentLength();

			is = conn.getInputStream();

			stringContent = readIt(is, len);
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

	private void savePrefs(String string, String string2) {
		// TODO Auto-generated method stub
		SharedPreferences splogin = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = splogin.edit();
		edit.putString(string, string2);
		edit.commit();
	}

	private void savePrefs(String string, boolean checked) {
		// TODO Auto-generated method stub
		SharedPreferences splogin = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = splogin.edit();
		edit.putBoolean(string, checked);
		edit.commit();
	}

	private void loadPrefs() {
		// TODO Auto-generated method stub
		SharedPreferences splogin = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean cbvalue = splogin.getBoolean("AUTOLOGIN", false);
		String name = splogin.getString("USERNAME", "");
		String passwod = splogin.getString("PASSWORD", "");
		if (cbvalue) {
			cbautologin.setChecked(true);
		} else {
			cbautologin.setChecked(false);
		}
		etusername.setText(name);
		etpassword.setText(passwod);

	}

}
