package com.eteam.dufour.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SurveySuccessActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String INTENT_SENT_STATUS = "com.eteam.dufour.release.backup.SurveySucessActivity.INTENT_SENT_STATUS";
	public static final int    STATUS_ONLINE = 1;
	public static final int	   STATUS_OFFLINE = 2;
	public static final int	   STATUS_DA_INVIARE_SURVEY_SENDING_FAILED = 3;
	public static final int	   STATUS_SAVED_TO_DA_INVIARE = 4;
	// ===========================================================
	// Fields
	// ===========================================================

	private View mSucessBtn;
	private TextView mLblMsg;
	
		
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
		setContentView(R.layout.survey_sent_success);
		mLblMsg = (TextView) findViewById(R.id.lbl_success_msg);
		mSucessBtn = findViewById(R.id.btn_success);
		mSucessBtn.setOnClickListener(this);
		getIntentAndSetView(savedInstanceState);
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mSucessBtn){
			finish();
		}
	}
	
	
	// ===========================================================
	// Methods
	// ===========================================================

	private void getIntentAndSetView(Bundle savedInstanceState) {
		if(savedInstanceState==null){
			int status = getIntent().getIntExtra(INTENT_SENT_STATUS, -1);
			if(status==-1){
				Log.e("Log", "Survey status not set");
			}
			else{
				checkStatAndSetLabel(status);
			}
		}
		else{
			int status = savedInstanceState.getInt(INTENT_SENT_STATUS);
			checkStatAndSetLabel(status);
		}
	}
	
	private void checkStatAndSetLabel(int status){
		if(status==STATUS_ONLINE){
			mLblMsg.setText(R.string.msg_survey_sent_sucess_online);
		}
		else if(status==STATUS_OFFLINE){
			mLblMsg.setText(R.string.msg_survey_sent_sucess_offline);
		}
		else if(status==STATUS_DA_INVIARE_SURVEY_SENDING_FAILED){
			mLblMsg.setText(R.string.msg_invia_dati_survey_failed);
		}
		else if (status==STATUS_SAVED_TO_DA_INVIARE) {
			mLblMsg.setText(R.string.msg_survey_saved_da_inviare);
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
