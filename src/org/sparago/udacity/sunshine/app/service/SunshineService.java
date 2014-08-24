package org.sparago.udacity.sunshine.app.service;

import org.sparago.udacity.sunshine.app.WeatherFetcher;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SunshineService extends IntentService {
	public final static String INTENT_LOCATION_KEY = "locationKey";

	public SunshineService() {
		super("SunshineService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String location = intent.getStringExtra(INTENT_LOCATION_KEY);
		WeatherFetcher.fetchWeather(this, location);
	}

	public static class AlarmReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String location = intent
					.getStringExtra(SunshineService.INTENT_LOCATION_KEY);
			context.startService(new Intent(context, SunshineService.class)
					.putExtra(SunshineService.INTENT_LOCATION_KEY, location));
		}
	}
}
