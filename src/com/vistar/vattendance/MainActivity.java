package com.vistar.vattendance;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Thread homeTimer = new Thread() {
			public void run() {
				try {
					int homeTimer = 0;
					while (homeTimer <= 1500) {
						sleep(100);
						homeTimer = homeTimer + 100;
					}
					startActivity(new Intent(
							"com.vistar.vattendance.CLEARSCREEN"));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					finish();
				}

			}
		};
		homeTimer.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
