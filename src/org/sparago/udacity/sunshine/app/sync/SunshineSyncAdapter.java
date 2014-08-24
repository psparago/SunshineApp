package org.sparago.udacity.sunshine.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.sparago.udacity.sunshine.app.R;
import org.sparago.udacity.sunshine.app.Utility;
import org.sparago.udacity.sunshine.app.WeatherFetcher;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();
	
	// Interval at which to sync with the weather, in milliseconds.
	// 60 seconds (1 minute) * 180 = 3 hours
	public static final int SYNC_INTERVAL = 60 * 180;
	public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
	
	public SunshineSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.d(LOG_TAG, "onPerformSync called");
		String location = Utility.getPreferredLocation(getContext());
		WeatherFetcher.fetchWeather(getContext(), location);
	}

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
    
    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
 
        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
 
        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
 
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                 return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
 
        }
        return newAccount;
    }
    
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
    	Account account = getSyncAccount(context);
    	String authority = context.getString(R.string.content_authority);
    	ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
    }
    
    private static void onAccountCreated(Account newAccount, Context context) {
    	SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
    	ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
    	syncImmediately(context);
    }
    
    public static void initializeSyncAdapter(Context context) {
    	getSyncAccount(context);
    }
}
