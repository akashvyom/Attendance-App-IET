package com.vistar.vattendance;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MarkUpload extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mark_upload);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mark_upload, menu);
		return true;
	}

}
