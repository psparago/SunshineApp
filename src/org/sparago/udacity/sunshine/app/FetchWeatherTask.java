package org.sparago.udacity.sunshine.app;

import android.content.Context;
import android.os.AsyncTask;

// To Call: new FetchWeatherTask(this).execute(value);	// value is location        	
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
	private Context context;
	
	public FetchWeatherTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(String... params) {
		WeatherFetcher.fetchWeather(context, params[0]);
		return null;
	}

}
