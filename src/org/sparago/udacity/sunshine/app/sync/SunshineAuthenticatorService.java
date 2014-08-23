package org.sparago.udacity.sunshine.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SunshineAuthenticatorService extends Service {
	private SunshineAuthenticator mAuthenticator;
	
	@Override
	public void onCreate() {
		// Create a new authenticator object
		mAuthenticator = new SunshineAuthenticator(this);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mAuthenticator.getIBinder();
	}

}
