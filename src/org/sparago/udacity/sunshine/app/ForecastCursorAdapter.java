package org.sparago.udacity.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastCursorAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link ListView}.
 */
public class ForecastCursorAdapter extends CursorAdapter {
    public ForecastCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }
 
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
    }
 
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
 
        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        
        // Use placeholder image for now
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.ic_launcher);
 
        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        dateView.setText(Utility.getFriendlyDayString(context, dateString));
 
        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        TextView descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        descriptionView.setText(description);
 
        // Read user preference for metric or imperial temperature units
        boolean isFarenheit = Utility.isFarenheit(context);
 
        // Read high temperature from cursor
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView hiTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        hiTempView.setText(Utility.formatTemperature(high, isFarenheit) + "\u00B0");
 
        // Read low temperature from cursor
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        lowTempView.setText(Utility.formatTemperature(low, isFarenheit) + "\u00B0");
    }
}