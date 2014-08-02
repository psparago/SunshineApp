package org.sparago.udacity.sunshine.app.models.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class Coord {
	private static final String OWM_LON = "lon";
	private static final String OWM_LAT = "lat";
	
	private double longitude;
	private double latitude;

	public void parseJson(JSONObject coordObject) throws JSONException {
		this.longitude = coordObject.getDouble(OWM_LON);
		this.latitude = coordObject.getDouble(OWM_LAT);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
