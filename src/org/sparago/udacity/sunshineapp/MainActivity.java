package org.sparago.udacity.sunshineapp;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set default values the very first time the app is run
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);		
		
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new ForecastFragment()).commit();
		}
		Log.d(LOG_TAG, "***********onCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "***********onPause");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "***********onResume");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(LOG_TAG, "***********onStart");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(LOG_TAG, "***********onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "***********onDestroy");
	}
}
