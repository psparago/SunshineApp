package org.sparago.udacity.sunshine.app;

import java.util.Date;

import org.sparago.udacity.sunshine.app.R;
import org.sparago.udacity.sunshine.app.data.WeatherContract;
import org.sparago.udacity.sunshine.app.data.WeatherContract.LocationEntry;
import org.sparago.udacity.sunshine.app.data.WeatherContract.WeatherEntry;

import android.content.Context;
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

public class DetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String LOG_TAG = DetailFragment.class.getSimpleName();
	private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
	private static final int DETAIL_LOADER = 0;

	public static final String DATE_ARGUMENT = "dateArg";

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
	private ShareActionProvider shareActionProvider;

	// Save the root view so we can get the View Holder in its tag.
	// NOTE: Can't get the View returned from onCreateView using getView()
	// because the compatibility version of Fragment inserts a
	// NoSaveStateFrameLayout as the getView() view.
	private View rootView;

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
		// The course version avoids this call if there is no date argument.
		// In our version, we allow it because the cursor loader will default to today.
		getLoaderManager().initLoader(DETAIL_LOADER, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		rootView.setTag(new DetailViewHolder(rootView));
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
		Bundle arguments = getArguments();
		if (arguments != null
				&& arguments.containsKey(DATE_ARGUMENT)
				&& mLocation != null
				&& !mLocation.equals(Utility
						.getPreferredLocation(getActivity()))) {
			getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(DetailActivity.LOCATION_KEY, mLocation);
		super.onSaveInstanceState(outState);
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
		String dateStr = null;
		if (getArguments() != null && getArguments().containsKey(DATE_ARGUMENT)) {
			dateStr = getArguments().getString(DATE_ARGUMENT);
		}
		if (dateStr == null) {
			dateStr = WeatherContract.getDbDateString(new Date());
		}

		// Sort order: Ascending, by detailDate.
		String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

		mLocation = Utility.getPreferredLocation(getActivity());
		Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithDate(
				mLocation, dateStr);

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), weatherForLocationUri,
				FORECAST_COLUMNS, null, null, sortOrder);

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Context context = getActivity();
		clearWidgets();
		DetailViewHolder viewHolder = getDetailViewHolder();
		if (cursor.moveToFirst()) {

			viewHolder.dayView.setText(Utility.getDayName(context,
					cursor.getString(COL_WEATHER_DATE)));

			viewHolder.dateView.setText(Utility.getFormattedMonthDay(context,
					cursor.getString(COL_WEATHER_DATE)));

			String description = cursor.getString(COL_WEATHER_DESC);
			viewHolder.forecastView.setText(description);

			// weather condition ID
			int weatherId = cursor.getInt(COL_WEATHER_WEATHER_ID);
			viewHolder.iconView.setImageResource(Utility
					.getArtResourceForWeatherCondition(weatherId));
			// accessibility
			viewHolder.iconView.setContentDescription(description);

			viewHolder.highView.setText(Utility.formatTemperature(context,
					cursor.getDouble(COL_WEATHER_MAX_TEMP)));
			viewHolder.lowView.setText(Utility.formatTemperature(context,
					cursor.getDouble(COL_WEATHER_MIN_TEMP)));

			viewHolder.humidityView.setText(context.getString(
					R.string.format_humidity,
					cursor.getFloat(COL_WEATHER_HUMIDITY)));

			viewHolder.windView.setText(Utility.getFormattedWind(context,
					cursor.getFloat(COL_WEATHER_WIND_SPEED),
					cursor.getFloat(COL_WEATHER_DEGREES)));

			viewHolder.pressureView.setText(context.getString(
					R.string.format_pressure,
					cursor.getFloat(COL_WEATHER_PRESSURE)));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// careful not to call clearWidets() unless destroy lifecycle can be
		// detected.
		getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
	}

	private void clearWidgets() {
		DetailViewHolder viewHolder = getDetailViewHolder();
		viewHolder.dateView.setText("");
		viewHolder.forecastView.setText("");
		viewHolder.highView.setText("");
		viewHolder.lowView.setText("");
		viewHolder.humidityView.setText("");
		viewHolder.windView.setText("");
		viewHolder.pressureView.setText("");
	}

	private String getShareData() {
		DetailViewHolder viewHolder = getDetailViewHolder();
		return String.format("%s - %s - %s / %s",
				viewHolder.dateView.getText(),
				viewHolder.forecastView.getText(),
				viewHolder.highView.getText(), viewHolder.lowView.getText());
	}

	private DetailViewHolder getDetailViewHolder() {
		return (DetailViewHolder) rootView.getTag();
	}
}
