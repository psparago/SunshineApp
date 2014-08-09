package org.sparago.udacity.sunshine.app.models.weather;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Day {
	private static final String OWM_WEATHER = "weather";
	private static final String OWM_TEMPERATURE = "temp";
	private static final String OWM_DATETIME = "dt";
	private static final String OWM_PRESSURE = "pressure";
	private static final String OWM_HUMIDITY = "humidity";
	private static final String OWM_SPEED = "speed";
	private static final String OWM_DEGREES = "deg";
	
	private Date date;
	private Temps temps;
	private Weather weather;
	private double pressure;
	private double humidity;
	private double speed;
	private double degrees;
	private double rain;
	
	public void parseJson(JSONObject dayForecast) throws JSONException {
		// convert from Unix time stamp to Java Date
		date = new Date(dayForecast.getLong(OWM_DATETIME) * 1000);

		JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
		temps = new Temps();
		temps.parseJson(temperatureObject);

		JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER)
				.getJSONObject(0);
		weather = new Weather();
		weather.parseJson(weatherObject);

		this.pressure = dayForecast.getDouble(OWM_PRESSURE);
		this.humidity = dayForecast.getDouble(OWM_HUMIDITY);
		this.speed = dayForecast.getDouble(OWM_SPEED);
		this.degrees = dayForecast.getDouble(OWM_DEGREES);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Temps getTemps() {
		return temps;
	}

	public void setTemps(Temps temps) {
		this.temps = temps;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getDegrees() {
		return degrees;
	}

	public void setDegrees(double degrees) {
		this.degrees = degrees;
	}

	public double getRain() {
		return rain;
	}

	public void setRain(double rain) {
		this.rain = rain;
	}
}
