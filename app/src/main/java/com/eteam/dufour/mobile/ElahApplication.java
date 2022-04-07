package com.eteam.dufour.mobile;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;

import com.eteam.db.DBApp;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.utils.ElahHttpClient;
import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ElahApplication extends DBApp{

	public static FirebaseAnalytics mFirebaseAnalytics;
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ElahHttpClient.enableCookie();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//		ElahHttpClient.enableHttpResponseCache(this);
		FirebaseApp.initializeApp(this);
		Stetho.initializeWithDefaults(this);
		ElahHttpClient.disableConnectionReuseIfNecessary();
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
	}
	
	
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		ElahDBHelper.closeConnections();
		
	}



	@Override
	public SQLiteOpenHelper getDBHelper() {
		// TODO Auto-generated method stub
		return ElahDBHelper.getInstance(this);
	}
	
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
