package org.sparago.udacity.sunshine.app;

import org.sparago.udacity.sunshine.app.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	private static final String LOG_TAG = DetailFragment.class.getSimpleName();
	private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
	private String forecast;
	private ShareActionProvider shareActionProvider;
	
	public DetailFragment() {
		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail,
				container, false);

		Intent intent = getActivity().getIntent();
		forecast = intent.getStringExtra(Intent.EXTRA_TEXT);
		
		TextView tv = (TextView)rootView.findViewById(R.id.detail_textview);
		tv.setText(forecast);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detail_fragment, menu);
		
		MenuItem menuItem = menu.findItem(R.id.action_share);
		shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
		if (shareActionProvider != null) {
			shareActionProvider.setShareHistoryFileName("sunshine_share_history.xml");
			shareActionProvider.setShareIntent(createShareIntent(forecast));
		} else {
			Log.d(LOG_TAG, "Share Action Provider is null?");
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private Intent createShareIntent(String text) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_TEXT, text + FORECAST_SHARE_HASHTAG);
		return shareIntent;
	}

}
