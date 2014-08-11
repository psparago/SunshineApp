package org.sparago.udacity.sunshine.app;

import java.util.Date;
import java.util.Locale;

import org.sparago.udacity.sunshine.app.data.WeatherContract;
import org.sparago.udacity.sunshine.app.data.WeatherContract.LocationEntry;
import org.sparago.udacity.sunshine.app.data.WeatherContract.WeatherEntry;
import org.sparago.udacity.sunshine.app.models.weather.WeatherLocation;
import org.sparago.udacity.sunshine.app.R;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ForecastFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String SELECTED_POSITION_KEY = "selectedPosition";
	private static final int FORECAST_LOADER = 0;

	// Callback interface to notify when a forecast item is selected.
	public interface ForecastItemSelectedListener {
		public void onForecastItemSelected(String date);
	}

	// Subset of columns used for the forecast view.
	private static final String[] FORECAST_COLUMNS = {
			WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
			WeatherEntry.COLUMN_DATETEXT, WeatherEntry.COLUMN_SHORT_DESC,
			WeatherEntry.COLUMN_MAX_TEMP, WeatherEntry.COLUMN_MIN_TEMP,
			WeatherEntry.COLUMN_WEATHER_ID };

	// Indexes of columns above (FORECAST_COLUMNS). Must sync.
	public static final int COL_WEATHER_ID = 0;
	public static final int COL_WEATHER_DATE = 1;
	public static final int COL_WEATHER_DESC = 2;
	public static final int COL_WEATHER_MAX_TEMP = 3;
	public static final int COL_WEATHER_MIN_TEMP = 4;
	public static final int COL_WEATHER_WEATHER_ID = 5;

	private static final String[] LOCATION_COLUMNS = {
			LocationEntry.COLUMN_COORD_LAT, LocationEntry.COLUMN_COORD_LONG };

	// Indexes of columns above (LOCATION_COLUMNS). Must sync.
	public static final int COL_LOCATION_COORD_LAT = 0;
	public static final int COL_LOCATION_COORD_LONG = 1;

	private ForecastCursorAdapter forecastAdapter = null;
	private String mLocation;

	private ListView listView;
	private int selectedPosition = ListView.INVALID_POSITION;

	public ForecastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(FORECAST_LOADER, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main,
				container, false);

		forecastAdapter = new ForecastCursorAdapter(getActivity(), null,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		listView.setAdapter(forecastAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedPosition = position;
				Cursor c = ((CursorAdapter) parent.getAdapter()).getCursor();
				if (c.moveToPosition(position)
						&& getActivity() instanceof ForecastItemSelectedListener) {
					((ForecastItemSelectedListener) getActivity())
							.onForecastItemSelected(c
									.getString(COL_WEATHER_DATE));
				}
			}
		});

		// if there's a saved selected position, restore it
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(SELECTED_POSITION_KEY)) {
			selectedPosition = savedInstanceState.getInt(SELECTED_POSITION_KEY);
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (selectedPosition != ListView.INVALID_POSITION) {
			outState.putInt(SELECTED_POSITION_KEY, selectedPosition);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.forecast_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			updateWeather();
			return true;
		}
		if (item.getItemId() == R.id.action_showOnMap) {
			showLocationOnMap();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showLocationOnMap() {
		if (mLocation != null) {
			Cursor cursor = getActivity().getContentResolver().query(
					LocationEntry.CONTENT_URI, LOCATION_COLUMNS,
					LocationEntry.getLocationConstraint(),
					new String[] { mLocation }, null);
			if (cursor.moveToFirst()) {
				String uri = String.format(Locale.ENGLISH, "geo:%f,%f(%s)",
						cursor.getFloat(COL_LOCATION_COORD_LAT),
						cursor.getFloat(COL_LOCATION_COORD_LONG), mLocation);
				Uri geoLocation = Uri.parse(uri);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(geoLocation);
				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(),
							"Cannot show: " + mLocation + " on the map",
							Toast.LENGTH_LONG).show();
				}
			}
		}

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
			getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
		}
	}

	private void updateWeather() {
		mLocation = Utility.getPreferredLocation(getActivity());
		new FetchWeatherTask(getActivity()).execute(mLocation);
	}

	// This is called when a new Loader needs to be created. This
	// fragment only uses one loader, so we don't care about checking the id.
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// To only show current and future dates, get the String
		// representation for today, and filter the query to
		// return weather only for dates after or including today.
		// Only return data after today.
		String startDate = WeatherContract.getDbDateString(new Date());

		// Sort order: Ascending, by date.
		String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

		mLocation = Utility.getPreferredLocation(getActivity());
		Uri weatherForLocationUri = WeatherEntry
				.buildWeatherLocationWithStartDate(mLocation, startDate);

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), weatherForLocationUri,
				FORECAST_COLUMNS, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		forecastAdapter.swapCursor(cursor);
		if (selectedPosition != ListView.INVALID_POSITION) {
			listView.setSelection(selectedPosition);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		forecastAdapter.swapCursor(null);
	}
}
