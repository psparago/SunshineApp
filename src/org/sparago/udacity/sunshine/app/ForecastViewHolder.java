package org.sparago.udacity.sunshine.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastViewHolder {
	public final ImageView iconView;
	public final TextView dateView;
	public final TextView descriptionView;
	public final TextView hiTempView;
	public final TextView lowTempView;
	
	public ForecastViewHolder(View view) {
		iconView = (ImageView) view.findViewById(R.id.list_item_icon);
		dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
		descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
		hiTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
		lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
	}
}
