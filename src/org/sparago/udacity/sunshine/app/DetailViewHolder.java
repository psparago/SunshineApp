package org.sparago.udacity.sunshine.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailViewHolder {
	public final TextView dayView;
	public final TextView dateView;
	public final TextView forecastView;
	public final TextView highView;
	public final TextView lowView;
	public final TextView humidityView;
	public final TextView windView;
	public final TextView pressureView;
	public final ImageView iconView;
	
	public DetailViewHolder(View view) {
		dayView = (TextView) view.findViewById(R.id.detail_day_textview);
		dateView = (TextView) view.findViewById(R.id.detail_date_textview);
		forecastView = (TextView) view.findViewById(R.id.detail_forecast_textview);
		highView = (TextView) view.findViewById(R.id.detail_high_textview);
		lowView = (TextView) view.findViewById(R.id.detail_low_textview);
		humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
		windView = (TextView) view.findViewById(R.id.detail_wind_textview);
		pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
		iconView = (ImageView)view.findViewById(R.id.detail_icon);
	}
}
