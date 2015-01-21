package com.vistar.vattendance;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ViewData extends Activity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_data);
		webView = (WebView) findViewById(R.id.wv_attendance);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://www.vistarpvtltd.co.nf/viewattendance.php");
	}

}
