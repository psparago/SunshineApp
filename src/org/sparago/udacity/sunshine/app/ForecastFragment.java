package org.sparago.udacity.sunshine.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import org.sparago.udacity.sunshine.app.models.weather.Day;
import org.sparago.udacity.sunshine.app.models.weather.WeatherLocation;
import org.sparago.udacity.sunshine.app.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ForecastFragment extends Fragment {
	protected ArrayAdapter<String> forecastAdapter = null;

	private WeatherLocation currentLocation;

	public ForecastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main,
				container, false);
		forecastAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_forecast, R.id.list_item_forecast_textview,
				new ArrayList<String>());
		ListView listView = (ListView) rootView
				.findViewById(R.id.listview_forecast);
		listView.setAdapter(forecastAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String forecast = forecastAdapter.getItem(position);
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(Intent.EXTRA_TEXT, forecast);
				startActivity(intent);
			}
		});
		return rootView;
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
		if (currentLocation != null) {
			String uri = String.format(Locale.ENGLISH, "geo:%f,%f(%s)",
					currentLocation.getCity().getCoord().getLatitude(),
					currentLocation.getCity().getCoord().getLongitude(),
					currentLocation.getCity().getName());
			Uri geoLocation = Uri.parse(uri);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(geoLocation);
			if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(
						getActivity(),
						"Cannot show: " + currentLocation.getCity().getName()
								+ " on the map", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		updateWeather();
	}

	private void updateWeather() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		String location = sharedPref.getString(
				getString(R.string.pref_location_key), "");
		new FetchWeatherTask().execute(location);
	}

	protected class FetchWeatherTask extends
			AsyncTask<String, Void, WeatherLocation> {

		private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

		public FetchWeatherTask() {
		}

		@Override
		protected WeatherLocation doInBackground(String... params) {
			final String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily";
			final String queryParam = "q";
			final String modeParam = "mode";
			final String modeValue = "json";
			final String unitsParam = "units";
			final String unitsValue = "metric";
			final String daysParam = "cnt";
			final int daysValue = 7;

			WeatherLocation location = null;

			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			boolean farenheit = sharedPref.getString(
					getString(R.string.pref_temp_units_key), "").equals("F");

			// These two need to be declared outside the try/catch
			// so that they can be closed in the finally block.
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			try {
				// Will contain the raw JSON response as a string.
				String forecastJsonStr = null;

				URL url = new URL(Uri
						.parse(baseUrl)
						.buildUpon()
						.appendQueryParameter(queryParam, params[0])
						.appendQueryParameter(modeParam, modeValue)
						.appendQueryParameter(unitsParam, unitsValue)
						.appendQueryParameter(daysParam,
								Integer.valueOf(daysValue).toString()).build()
						.toString());

				// Create the request to OpenWeatherMap, and open the connection
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				// Read the input stream into a String
				InputStream inputStream = urlConnection.getInputStream();
				StringBuffer buffer = new StringBuffer();
				if (inputStream == null) {
					// Nothing to do.
					forecastJsonStr = null;
				}
				reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					// Since it's JSON, adding a newline isn't necessary (it
					// won't affect parsing)
					// But it does make debugging a *lot* easier if you print
					// out the completed
					// buffer for debugging.
					buffer.append(line + "\n");
				}

				if (buffer.length() > 0) {
					forecastJsonStr = buffer.toString();
					location = new WeatherLocation();
					location.parseJson(forecastJsonStr, farenheit);
				}

			} catch (Exception e) {
				Log.e(LOG_TAG, "Error getting weather JSON", e);
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						Log.e(LOG_TAG, "Error closing stream", e);
					}
				}
			}

			return location;
		}

		@Override
		protected void onPostExecute(WeatherLocation location) {
			currentLocation = location;
			if (currentLocation != null) {
				forecastAdapter.clear();
				for (Day d : currentLocation.getDays()) {
					forecastAdapter.add(d.toString());
				}
			}
		}

	}
}
