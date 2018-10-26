package edu.ua.cs.cs495.caladrius.android;

import android.app.Application;
import android.content.Context;
import edu.ua.cs.cs495.caladrius.fitbit.FitbitAccount;

public class Caladrius extends Application
{
	protected static Context cntxt;
	public static FitbitAccount user = null;

	public static Context getContext()
	{
		if (cntxt == null) {
			throw new RuntimeException(Caladrius.class.toString() + " was not properly initialized");
		}
		return cntxt;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		if (cntxt != null) {
			throw new RuntimeException(Caladrius.class.toString() + " was initialized multiple times");
		}

		cntxt = getApplicationContext();
	}
}
