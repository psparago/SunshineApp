package org.sparago.udacity.sunshine.app;

import org.sparago.udacity.sunshine.app.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements
		ForecastFragment.ForecastItemSelectedListener {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set default values the very first time the app is run
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

		setContentView(R.layout.activity_main);

		// The forecast fragment will be present only in the large-screen
		// (tablet) layouts. If the forecast fragment isn't there, 
		// we will need to create the forecast fragment.
		ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_forecast);
		if (forecastFragment == null) {
			if (savedInstanceState == null) {
				forecastFragment = new ForecastFragment();
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, forecastFragment).commit();
			}
		}
		// Set this activity as the forecast item selected listener on the
		// forecast fragment
		forecastFragment.setForecastListener(this);

		// The detail container view will be present only in the large-screen
		// layouts. If this view is present, then the activity should be
		// in two-pane mode.
		if (findViewById(R.id.weather_detail_container) != null) {
			mTwoPane = true;
			// In two-pane mode,show the detail view in this activity by
			// adding or replacing the detail fragment using a fragment
			// transaction.
			if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.weather_detail_container, new DetailFragment())
						.commit();
			}
		} else {
			mTwoPane = false;
		}
	}

	@Override
	public void onForecastItemSelected(String date) {
		if (mTwoPane) {
			Bundle args = new Bundle();
			args.putString(DetailFragment.DATE_ARGUMENT, date);
			DetailFragment detailFragment = new DetailFragment();
			detailFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.weather_detail_container, detailFragment)
				.commit();
		} else {
			Intent intent = new Intent(this, DetailActivity.class)
				.putExtra(DetailActivity.DATE_KEY, date);
			startActivity(intent);
		}
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

}
