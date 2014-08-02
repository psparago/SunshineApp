package org.sparago.udacity.sunshine.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.sparago.udacity.sunshine.app.models.weather.Day;
import org.sparago.udacity.sunshine.app.models.weather.WeatherLocation;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class FetchWeatherTask extends AsyncTask<String, Void, WeatherLocation> {

	private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
	private Context context;
	private ArrayAdapter<String> forecastAdapter;
	private WeatherLocationObserver locationObserver;

	public FetchWeatherTask(Context context, WeatherLocationObserver locationObserver, ArrayAdapter<String> forecastAdapter) {
		this.context = context;
		this.forecastAdapter = forecastAdapter;
		this.locationObserver = locationObserver;
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
		final int daysValue = 14;
		
		if (params.length == 0) {
			return null;
		}
		String locationQuery = params[0];
		
		WeatherLocation location = null;
		boolean farenheit = Utility.isFarenheit(context);

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
					.appendQueryParameter(queryParam, locationQuery)
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
	protected void onPostExecute(WeatherLocation weatherLocation) {
		locationObserver.onWeatherLocationChanged(weatherLocation);
		if (weatherLocation != null) {
			forecastAdapter.clear();
			for (Day d : weatherLocation.getDays()) {
				forecastAdapter.add(d.toString());
			}
		}
	}

}
