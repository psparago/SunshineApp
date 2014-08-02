package org.sparago.udacity.sunshine.app;

import org.sparago.udacity.sunshine.app.models.weather.WeatherLocation;

public interface WeatherLocationObserver {
	void onWeatherLocationChanged(WeatherLocation weatherLocation);
}
