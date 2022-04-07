package com.eteam.dufour.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SurveyFailActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	
	public static final String ACTION_RETRY = "com.eteam.dufour.mobile.ACTION_RETRY";
	public static final String ACTION_SEND_LATER = "com.eteam.dufour.mobile.ACTION_SEND_LATER";
	// ===========================================================
	// Fields
	// ===========================================================
	private View btnCancel;
	private View btnRetry;
	private View btnSendLater;
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alertsurveyfailed);
		btnCancel = findViewById(R.id.btn_cancel);
		btnRetry = findViewById(R.id.btn_retry);
		btnSendLater = findViewById(R.id.btn_send_later);
		
		btnCancel.setOnClickListener(this);
		btnRetry.setOnClickListener(this);
		btnSendLater.setOnClickListener(this);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnCancel){
			finish();
		}
		else if(v==btnRetry){
			Intent data = new Intent();
			data.setAction(ACTION_RETRY);
			setResult(RESULT_OK, data);
			finish();
		}
		else if(v==btnSendLater){
			Intent data = new Intent();
			data.setAction(ACTION_SEND_LATER);
			setResult(RESULT_OK, data);
			finish();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
