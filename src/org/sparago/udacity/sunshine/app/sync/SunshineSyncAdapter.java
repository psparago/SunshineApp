package org.sparago.udacity.sunshine.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import org.sparago.udacity.sunshine.app.R;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {

	public SunshineSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
	}

	public static void syncImmediately(Context context) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		ContentResolver.requestSync(getSyncAccount(context),
				context.getString(R.string.content_authority), bundle);
	}

	/**
	 * Helper method to get the fake accounts to be used with SyncAdapter
	 */
	public static Account getSyncAccount(Context context) {
		AccountManager accountManager = (AccountManager) context
				.getSystemService(Context.ACCOUNT_SERVICE);

		Account newAccount = new Account(context.getString(R.string.app_name),
				context.getString(R.string.sync_account_type));

		// If the password doesn't exist, the account doesn't exist
		if (accountManager.getPassword(newAccount) == null) {
			if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
				return null;
			}

			/*
			 * If you don't set android:syncable="true" in your <provider>
			 * element in the manifest then call context.setIsSyncable(account,
			 * AUTHORITY) here
			 */
		}
		return newAccount;
	}
}
