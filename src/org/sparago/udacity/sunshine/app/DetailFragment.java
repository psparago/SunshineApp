package org.sparago.udacity.sunshine.app;

import org.sparago.udacity.sunshine.app.R;
import org.sparago.udacity.sunshine.app.data.WeatherContract;
import org.sparago.udacity.sunshine.app.data.WeatherContract.LocationEntry;
import org.sparago.udacity.sunshine.app.data.WeatherContract.WeatherEntry;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String LOG_TAG = DetailFragment.class.getSimpleName();
	private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
	private static final int DETAIL_LOADER = 0;

	// Subset of columns used for the forecast view.
	private static final String[] FORECAST_COLUMNS = {
			WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
			WeatherEntry.COLUMN_DATETEXT, WeatherEntry.COLUMN_SHORT_DESC,
			WeatherEntry.COLUMN_MAX_TEMP, WeatherEntry.COLUMN_MIN_TEMP,
			WeatherEntry.COLUMN_HUMIDITY, WeatherEntry.COLUMN_PRESSURE,
			WeatherEntry.COLUMN_WIND_SPEED, WeatherEntry.COLUMN_DEGREES,
			WeatherEntry.COLUMN_WEATHER_ID,
			LocationEntry.COLUMN_LOCATION_SETTING };

	// Indexes of columns above (FORECAST_COLUMNS). Must sync.
	private static final int COL_WEATHER_ID = 0;
	private static final int COL_WEATHER_DATE = 1;
	private static final int COL_WEATHER_DESC = 2;
	private static final int COL_WEATHER_MAX_TEMP = 3;
	private static final int COL_WEATHER_MIN_TEMP = 4;
	private static final int COL_WEATHER_HUMIDITY = 5;
	private static final int COL_WEATHER_PRESSURE = 6;
	private static final int COL_WEATHER_WIND_SPEED = 7;
	private static final int COL_WEATHER_DEGREES = 8;
	private static final int COL_WEATHER_WEATHER_ID = 9;
	private static final int COL_LOCATION_SETTING = 10;

	private String mLocation;

	private String date;
	private ShareActionProvider shareActionProvider;

	public DetailFragment() {
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(DetailActivity.LOCATION_KEY)) {
			mLocation = savedInstanceState
					.getString(DetailActivity.LOCATION_KEY);
		}
		getLoaderManager().initLoader(DETAIL_LOADER, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail, container,
				false);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detail_fragment, menu);

		MenuItem menuItem = menu.findItem(R.id.action_share);
		shareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(menuItem);
		if (shareActionProvider != null) {
			shareActionProvider
					.setShareHistoryFileName("sunshine_share_history.xml");
			shareActionProvider
					.setShareIntent(createShareIntent(getShareData()));
		} else {
			Log.d(LOG_TAG, "Share Action Provider is null?");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mLocation != null
				&& !mLocation.equals(Utility
						.getPreferredLocation(getActivity()))) {
			getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mLocation != null) {
			outState.putString(DetailActivity.LOCATION_KEY, mLocation);
		}
	}

	private Intent createShareIntent(String text) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_TEXT, text + FORECAST_SHARE_HASHTAG);
		return shareIntent;
	}

	// This is called when a new Loader needs to be created. This
	// fragment only uses one loader, so we don't care about checking the id.
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Intent intent = getActivity().getIntent();
		date = intent.getStringExtra(DetailActivity.DATE_KEY);

		// Sort order: Ascending, by date.
		String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

		mLocation = Utility.getPreferredLocation(getActivity());
		Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithDate(
				mLocation, date);

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), weatherForLocationUri,
				FORECAST_COLUMNS, null, null, sortOrder);

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		clearWidgets();
		if (cursor.moveToFirst()) {
			boolean farenheit = Utility.isFarenheit(getActivity());
			((TextView) this.getView().findViewById(R.id.detail_date_textview))
					.setText(Utility.formatDate(cursor
							.getString(COL_WEATHER_DATE)));
			((TextView) this.getView().findViewById(
					R.id.detail_forecast_textview)).setText(cursor
					.getString(COL_WEATHER_DESC));
			((TextView) this.getView().findViewById(R.id.detail_high_textview))
					.setText(Utility.formatTemperature(
							cursor.getDouble(COL_WEATHER_MAX_TEMP), farenheit)
							+ "\u00B0");
			((TextView) this.getView().findViewById(R.id.detail_low_textview))
					.setText(Utility.formatTemperature(
							cursor.getDouble(COL_WEATHER_MIN_TEMP), farenheit)
							+ "\u00B0");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// careful not to call clearWidets() unless destroy lifecycle can be
		// detected.
		getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
	}

	private void clearWidgets() {
		((TextView) this.getView().findViewById(R.id.detail_date_textview))
				.setText("");
		((TextView) this.getView().findViewById(R.id.detail_forecast_textview))
				.setText("");
		((TextView) this.getView().findViewById(R.id.detail_high_textview))
				.setText("");
		((TextView) this.getView().findViewById(R.id.detail_low_textview))
				.setText("");
	}

	private String getShareData() {
		return String.format(
				"%s - %s - %s / %s",
				((TextView) this.getView().findViewById(
						R.id.detail_date_textview)).getText(),
				((TextView) this.getView().findViewById(
						R.id.detail_forecast_textview)).getText(),
				((TextView) this.getView().findViewById(
						R.id.detail_high_textview)).getText(), ((TextView) this
						.getView().findViewById(R.id.detail_low_textview))
						.getText());
	}
}
