package com.eteam.dufour.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ConfirmDraftActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String INTENT_SURVEY_ID = "com.eteam.dufour.mobile.ConfirmDraftActivity.INTENT_SURVEY_ID";
	public static final String INTENT_CUSTOMER_NUMBER = "com.eteam.dufour.mobile.ConfirmDraftActivity.INTENT_CUSTOMER_NUMBER";
	public static final String INTENT_CUSTOMER_NAME = "com.eteam.dufour.mobile.ConfirmDraftActivity.INTENT_CUSTOMER_NAME";
	public static final String INTENT_CANCELLED = "com.eteam.dufour.mobile.ConfirmDraftActivity.INTENT_CANCELLED";
	// ===========================================================
	// Fields
	// ===========================================================

	private View	btnSi;
	private View	btnNo;
	private TextView fieldCustomerCode;
	
	private int surveyId;
	private String customerName;
	private String customerNumber;
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
		setContentView(R.layout.popup_confirm);
		getIntentAndSetData(savedInstanceState);
		btnSi = findViewById(R.id.btn_si);
		btnNo = findViewById(R.id.btn_no);
		fieldCustomerCode = (TextView) findViewById(R.id.field_customer_code);
		
		fieldCustomerCode.setText(customerNumber);
		
		btnSi.setOnClickListener(this);
		btnNo.setOnClickListener(this);
	
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString(INTENT_CUSTOMER_NAME, customerName);
		outState.putString(INTENT_CUSTOMER_NUMBER, customerNumber);
		outState.putInt(INTENT_SURVEY_ID, surveyId);
		super.onSaveInstanceState(outState);

		
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view==btnSi){
			SurveyTabActivity.startActivity(this, surveyId, 
					customerName, customerNumber);
			finish();
		}
		else if(view==btnNo){
			Intent intent = new Intent();
			intent.putExtra(INTENT_CANCELLED, true);
			setResult(RESULT_OK,intent);
			finish();
		}
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	private void getIntentAndSetData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			customerName = savedInstanceState.getString(INTENT_CUSTOMER_NAME);
			customerNumber = savedInstanceState.getString(INTENT_CUSTOMER_NUMBER);
			surveyId = savedInstanceState.getInt(INTENT_SURVEY_ID);
		}
		else{
			customerName = getIntent().getStringExtra(INTENT_CUSTOMER_NAME);
			customerNumber = getIntent().getStringExtra(INTENT_CUSTOMER_NUMBER);
			surveyId = getIntent().getIntExtra(INTENT_SURVEY_ID, -1);
		}
	}



	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
