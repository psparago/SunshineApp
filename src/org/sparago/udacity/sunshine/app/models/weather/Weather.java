package org.sparago.udacity.sunshine.app.models.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {
	private static final String OWM_DESCRIPTION = "main";

	private int id;
	private String main;
	private String description;
	private String icon;
	
	public void parseJson(JSONObject weatherObject) throws JSONException {
		this.description = weatherObject.getString(OWM_DESCRIPTION);
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
