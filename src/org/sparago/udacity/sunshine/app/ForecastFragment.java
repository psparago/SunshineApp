package org.sparago.udacity.sunshine.app;

import java.util.ArrayList;
import java.util.Locale;

import org.sparago.udacity.sunshine.app.models.weather.WeatherLocation;
import org.sparago.udacity.sunshine.app.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ForecastFragment extends Fragment {
	protected ArrayAdapter<String> forecastAdapter = null;

	private WeatherLocation currentLocation;

	public ForecastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main,
				container, false);
		forecastAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_forecast, R.id.list_item_forecast_textview,
				new ArrayList<String>());
		ListView listView = (ListView) rootView
				.findViewById(R.id.listview_forecast);
		listView.setAdapter(forecastAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String forecast = forecastAdapter.getItem(position);
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(Intent.EXTRA_TEXT, forecast);
				startActivity(intent);
			}
		});
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.forecast_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			updateWeather();
			return true;
		}
		if (item.getItemId() == R.id.action_showOnMap) {
			showLocationOnMap();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showLocationOnMap() {
		if (currentLocation != null) {
			String uri = String.format(Locale.ENGLISH, "geo:%f,%f(%s)",
					currentLocation.getCity().getCoord().getLatitude(),
					currentLocation.getCity().getCoord().getLongitude(),
					currentLocation.getCity().getName());
			Uri geoLocation = Uri.parse(uri);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(geoLocation);
			if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(
						getActivity(),
						"Cannot show: " + currentLocation.getCity().getName()
								+ " on the map", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		updateWeather();
	}

	private void updateWeather() {
		String location = Utility.getPreferredLocaiton(getActivity());
		new FetchWeatherTask(getActivity(), new WeatherLocationObserver() {
			@Override
			public void onWeatherLocationChanged(WeatherLocation weatherLocation) {
				currentLocation = weatherLocation;
			}
		}, forecastAdapter).execute(location);
	}

}
