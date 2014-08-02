package org.sparago.udacity.sunshine.app.models.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class City {
	private static final String OWM_NAME = "name";
	private static final String OWM_COORD = "coord";
	private static final String OWM_COUNTRY = "country";
	
	private String name;
	private Coord coord;
	private String country;
	
	public void parseJson(JSONObject cityObject) throws JSONException {
		this.name = cityObject.getString(OWM_NAME);
		this.country = cityObject.getString(OWM_COUNTRY);
		this.coord = new Coord();
		this.coord.parseJson(cityObject.getJSONObject(OWM_COORD));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Coord getCoord() {
		return coord;
	}

	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
