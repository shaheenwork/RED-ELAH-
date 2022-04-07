package com.eteam.dufour.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class UpdateReceiver extends BroadcastReceiver{


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
		Log.d("UpdateReceiver", "Received Intent");
		if(intent.getAction().equals(UpdateService.ACTION_UPDATION)){
			int status = intent.getIntExtra(UpdateService.INTENT_UPDATE_STATUS, UpdateService.UPDATE_DEFAULT_VALUE);
			Log.d("UpdateReceiver", "Status is = "+status);
			SharedPreferences prefs = context.getSharedPreferences(Consts.PREF_ELAH,
					Context.MODE_PRIVATE);
			if(status!=UpdateService.UPDATE_DEFAULT_VALUE){
				Editor editor = prefs.edit();
				editor.putInt(UpdateService.PREF_UPDATE_STATUS, status);
				editor.commit();
				Log.d("UpdateReceiver", "Status - "+status);
			}
			if (status!=UpdateService.UPDATE_AVAILABLE){
				Util.checkAndPromptUpdate(context, prefs);
			}
		}
		
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
