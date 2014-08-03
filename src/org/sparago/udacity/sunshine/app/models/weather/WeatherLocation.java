package org.sparago.udacity.sunshine.app.models.weather;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherLocation {
	private static final String OWM_CITY = "city";
	private static final String OWM_LIST = "list";
	private String location;
	private City city;
	private List<Day> days = new ArrayList<Day>();
	
	public WeatherLocation(String location) {
		this.setLocation(location);
	}
	
	public void parseJson(String forecastJsonStr, boolean farenheit) throws JSONException {
		JSONObject forecastJson = new JSONObject(forecastJsonStr);
		city = new City();
		city.parseJson(forecastJson.getJSONObject(OWM_CITY));
		JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
		days.clear();
		for (int i = 0; i < weatherArray.length(); i++) {
			JSONObject dayForecast = weatherArray.getJSONObject(i);
			Day day = new Day();
			day.parseJson(dayForecast, farenheit);
			days.add(day);
		}
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
