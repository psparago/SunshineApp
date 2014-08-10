package org.sparago.udacity.sunshine.app.models.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {
	private static final String OWM_DESCRIPTION = "main";
	private static final String OWM_ID = "id";

	private int id;
	private String main;
	
	public void parseJson(JSONObject weatherObject) throws JSONException {
		this.main = weatherObject.getString(OWM_DESCRIPTION);
		this.id = weatherObject.getInt(OWM_ID);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMain() {
		return main;
	}
	public void setMain(String main) {
		this.main = main;
	}
	
}
