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
 * {@link ForecastCursorAdapter} exposes a list of weather forecasts from a
 * {@link Cursor} to a {@link ListView}.
 */
public class ForecastCursorAdapter extends CursorAdapter {

	private final int VIEW_TYPE_TODAY = 0;
	private final int VIEW_TYPE_FUTURE_DAY = 1;

	public ForecastCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public int getItemViewType(int position) {
		return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		int viewType = getItemViewType(cursor.getPosition());
		int layoutId = viewType == VIEW_TYPE_TODAY ? R.layout.list_item_forecast_today
				: R.layout.list_item_forecast;
		View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
		view.setTag(new ForecastViewHolder(view));
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int viewType = getItemViewType(cursor.getPosition());
		ForecastViewHolder viewHolder = (ForecastViewHolder)view.getTag();
		
		// Read weather icon ID from cursor
		int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_WEATHER_ID);

		// Use placeholder image for now
		viewHolder.iconView
				.setImageResource(viewType == VIEW_TYPE_TODAY ? Utility
						.getArtResourceForWeatherCondition(weatherId) : Utility
						.getIconResourceForWeatherCondition(weatherId));

		// Read date from cursor
		String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
		viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

		// Read weather forecast from cursor
		String description = cursor
				.getString(ForecastFragment.COL_WEATHER_DESC);
		viewHolder.descriptionView.setText(description);

		// Read user preference for metric or imperial temperature units
		boolean isFarenheit = Utility.isFarenheit(context);

		// Read high temperature from cursor
		float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
		viewHolder.hiTempView.setText(Utility.formatTemperature(context, high, isFarenheit));

		// Read low temperature from cursor
		float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
		viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isFarenheit));
	}
}