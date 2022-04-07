package com.eteam.dufour.service;

import com.eteam.utils.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceLauncher extends BroadcastReceiver{


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
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(Util.haveNetworkConnection(context)){
			Log.d("ServiceLauncher", "Launching service");
			Intent serviceIntent = new Intent(context, UpdateService.class);
			context.startService(serviceIntent);
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
