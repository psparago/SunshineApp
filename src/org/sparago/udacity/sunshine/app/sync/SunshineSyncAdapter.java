package org.sparago.udacity.sunshine.app.sync;

import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.sparago.udacity.sunshine.app.MainActivity;
import org.sparago.udacity.sunshine.app.R;
import org.sparago.udacity.sunshine.app.Utility;
import org.sparago.udacity.sunshine.app.WeatherFetcher;
import org.sparago.udacity.sunshine.app.data.WeatherContract;
import org.sparago.udacity.sunshine.app.data.WeatherContract.WeatherEntry;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String LOG_TAG = SunshineSyncAdapter.class
			.getSimpleName();

	// Interval at which to sync with the weather, in milliseconds.
	// 60 seconds (1 minute) * 180 = 3 hours
	private static final int SYNC_INTERVAL = 60 * 180;
	private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

	private static final String[] NOTIFY_WEATHER_PROJECTION = {
			WeatherEntry.COLUMN_WEATHER_ID, WeatherEntry.COLUMN_MAX_TEMP,
			WeatherEntry.COLUMN_MIN_TEMP, WeatherEntry.COLUMN_SHORT_DESC };
	private static final int INDEX_WEATHER_ID = 0;
	private static final int INDEX_MAX_TEMP = 1;
	private static final int INDEX_MIN_TEMP = 2;
	private static final int INDEX_SHORT_DESC = 3;

	private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	private static final int WEATHER_NOTIFICATION_ID = 3004;

	public SunshineSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.d(LOG_TAG, "onPerformSync called");
		String location = Utility.getPreferredLocation(getContext());
		WeatherFetcher.fetchWeather(getContext(), location);
		notifyWeather();
	}

	/**
	 * Helper method to have the sync adapter sync immediately
	 * 
	 * @param context
	 *            The context used to access the account service
	 */
	public static void syncImmediately(Context context) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		ContentResolver.requestSync(getSyncAccount(context),
				context.getString(R.string.content_authority), bundle);
	}

	/**
	 * Helper method to get the fake account to be used with SyncAdapter, or
	 * make a new one if the fake account doesn't exist yet. If we make a new
	 * account, we call the onAccountCreated method so we can initialize things.
	 * 
	 * @param context
	 *            The context used to access the account service
	 * @return a fake account.
	 */
	public static Account getSyncAccount(Context context) {
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(Context.ACCOUNT_SERVICE);

		// Create the account type and default account
		Account newAccount = new Account(context.getString(R.string.app_name),
				context.getString(R.string.sync_account_type));

		// If the password doesn't exist, the account doesn't exist
		if (null == accountManager.getPassword(newAccount)) {

			/*
			 * Add the account and account type, no password or user data If
			 * successful, return the Account object, otherwise report an error.
			 */
			if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
				return null;
			}
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call
			 * ContentResolver.setIsSyncable(account, AUTHORITY, 1) here.
			 */
			onAccountCreated(newAccount, context);

		}
		return newAccount;
	}

	/**
	 * Helper method to schedule the sync adapter periodic execution
	 */
	@SuppressLint("NewApi")
	public static void configurePeriodicSync(Context context, int syncInterval,
			int flexTime) {
		Account account = getSyncAccount(context);
		String authority = context.getString(R.string.content_authority);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// we can enable inexact timers in our periodic sync
			SyncRequest request = new SyncRequest.Builder()
					.syncPeriodic(syncInterval, flexTime)
					.setSyncAdapter(account, authority).build();
			ContentResolver.requestSync(request);
		} else {
			ContentResolver.addPeriodicSync(account, authority, new Bundle(),
					syncInterval);
		}
	}

	private static void onAccountCreated(Account newAccount, Context context) {
		SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL,
				SYNC_FLEXTIME);
		ContentResolver.setSyncAutomatically(newAccount,
				context.getString(R.string.content_authority), true);
		syncImmediately(context);
	}

	public static void initializeSyncAdapter(Context context) {
		getSyncAccount(context);
	}

	private void notifyWeather() {
		Context context = getContext();
		
		if (!Utility.isNotifyWeather(context))
			return;

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String lastNotificationKey = context
				.getString(R.string.pref_last_notification);
		long lastSync = prefs.getLong(lastNotificationKey, 0);

		if (System.currentTimeMillis() - lastSync > DAY_IN_MILLIS) {
			String locationQuery = Utility.getPreferredLocation(context);
			Uri weatherUri = WeatherEntry.buildWeatherLocationWithDate(
					locationQuery, WeatherContract.getDbDateString(new Date()));
			Cursor cursor = context.getContentResolver().query(weatherUri,
					NOTIFY_WEATHER_PROJECTION, null, null, null);
			if (cursor.moveToFirst()) {
				int weatherId = cursor.getInt(INDEX_WEATHER_ID);
				double high = cursor.getDouble(INDEX_MAX_TEMP);
				double low = cursor.getDouble(INDEX_MIN_TEMP);
				String desc = cursor.getString(INDEX_SHORT_DESC);

				int iconId = Utility
						.getIconResourceForWeatherCondition(weatherId);
				String title = context.getString(R.string.app_name);
				String contentText = String.format(
						context.getString(R.string.format_notification), desc,
						Utility.formatTemperature(context, high),
						Utility.formatTemperature(context, low));

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(iconId).setContentTitle(title)
						.setContentText(contentText);
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(context, MainActivity.class);

				// The stack builder object will contain an artificial back stack for 
				// the started Activity. This ensures that navigating backward from
				// the Activity leads out of your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(MainActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder
						.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				mNotificationManager.notify(WEATHER_NOTIFICATION_ID,
						mBuilder.build());

				SharedPreferences.Editor editor = prefs.edit();
				editor.putLong(lastNotificationKey, System.currentTimeMillis());
				editor.commit();
			}
		}
	}
}
