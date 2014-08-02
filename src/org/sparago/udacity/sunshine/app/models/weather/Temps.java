package org.sparago.udacity.sunshine.app.models.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class Temps {
	private static final String OWM_MAX = "max";
	private static final String OWM_MIN = "min";
	
	private double day;
	private double low;
	private double high;
	private double night;
	private double eve;
	private double morn;
	
	public Temps() {
	}
	
	public void parseJson(JSONObject temperatureObject, boolean farenheit) throws JSONException {
		this.high = adjustTemperatureUnits(
				temperatureObject.getDouble(OWM_MAX), farenheit);
		this.low = adjustTemperatureUnits(
				temperatureObject.getDouble(OWM_MIN), farenheit);
	}
	
	public double getDay() {
		return day;
	}
	public void setDay(double day) {
		this.day = day;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getNight() {
		return night;
	}
	public void setNight(double night) {
		this.night = night;
	}
	public double getEve() {
		return eve;
	}
	public void setEve(double eve) {
		this.eve = eve;
	}
	public double getMorn() {
		return morn;
	}
	public void setMorn(double morn) {
		this.morn = morn;
	}
	
	public String getHighAndLow() {
		return formatHighLows(high, low);
	}
	
	/**
	 * Prepare the weather high/lows for presentation.
	 */
	private String formatHighLows(double high, double low) {
		// For presentation, assume the user doesn't care about tenths of a
		// degree.
		long roundedHigh = Math.round(high);
		long roundedLow = Math.round(low);

		String highLowStr = roundedHigh + "/" + roundedLow;
		return highLowStr;
	}
	
	private double adjustTemperatureUnits(double temp, boolean farenheit) {
		if (farenheit) {
			temp = (temp * 1.8) + 32;
		}
		return temp;
	}
}
