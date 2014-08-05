package org.sparago.udacity.sunshine.app;

import java.text.DateFormat;
import java.util.Date;

import org.sparago.udacity.sunshine.app.data.WeatherContract;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {
	public static String getPreferredLocation(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(context.getString(R.string.pref_location_key),
				context.getString(R.string.pref_location_default));
	}
	
	public static boolean isFarenheit(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getString(
				context.getString(R.string.pref_temp_units_key), "").equals("F");
	}

    static String formatTemperature(double temperature, boolean isFarenheit) {
        double temp;
        if ( isFarenheit ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }
 
    static String formatDate(String dateString) {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }
}
