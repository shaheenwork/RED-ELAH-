package com.eteam.dufour.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class SurveySaveActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	
	public final static String INTENT_SURVEY_ID = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_SURVEY_ID";
	public final static String INTENT_CLUSTER_VALUE = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE";
	public final static String INTENT_CLUSTER_VALUE2 = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE2";
	public final static String INTENT_CLUSTER_VALUE3 = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE3";
	public final static String INTENT_CLUSTER_VALUE_STATUS = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE_STATUS";
	
	// ===========================================================
	// Fields
	// ===========================================================
		
	private long surveyId;
	private int clustValue;
	private int clustValue2;
	private int clustValue3;
	private int clustStat;
	
	private View btnAnnula;
	private View btnEliminaBozza;
	private View btnSalva;
	
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
		setContentView(R.layout.escapedialog);
				
		getIntentAndSetData(savedInstanceState);
		
		btnAnnula = findViewById(R.id.btn_cancel);
		btnEliminaBozza = findViewById(R.id.btn_elimina_bozza);
		btnSalva = findViewById(R.id.btn_save_drafts);
		
		btnAnnula.setOnClickListener(this);
		btnEliminaBozza.setOnClickListener(this);
		btnSalva.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(INTENT_SURVEY_ID, surveyId);
		outState.putInt(INTENT_CLUSTER_VALUE, clustValue);
		outState.putInt(INTENT_CLUSTER_VALUE2, clustValue2);
		outState.putInt(INTENT_CLUSTER_VALUE3, clustValue3);
		outState.putInt(INTENT_CLUSTER_VALUE_STATUS, clustStat);
		super.onSaveInstanceState(outState);
	
	}

	

	public void onClick(View v) {
		if(v == btnAnnula){
			
			finish();
		}
		else if(v == btnEliminaBozza){
			if(surveyId==-1){
				Log.e("Log", "surveyid not found");
				return;
			}
			TableSurvey.delete(surveyId);
			setResult(RESULT_OK);
			finish();
		}
		else if (v == btnSalva) {
			if(surveyId==-1){
				Log.e("Log", "surveyid not found");
				return;
			}
			TableSurvey.updateSurveySentDate(surveyId, Util.getCurrentDateTime(),clustValue,clustValue2,clustValue3,clustStat);
			TableSurvey.updateFlag(surveyId, TableSurvey.FLAG_DRAFTS);
			setResult(RESULT_OK);
			finish();
			
		}
		
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	private void getIntentAndSetData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			surveyId = savedInstanceState.getLong(INTENT_SURVEY_ID);
			clustValue = savedInstanceState.getInt(INTENT_CLUSTER_VALUE);
			clustValue2 = savedInstanceState.getInt(INTENT_CLUSTER_VALUE2);
			clustValue3 = savedInstanceState.getInt(INTENT_CLUSTER_VALUE3);
			clustStat = savedInstanceState.getInt(INTENT_CLUSTER_VALUE_STATUS);
		}
		else{
			surveyId = getIntent().getLongExtra(INTENT_SURVEY_ID, -1);
			clustValue = getIntent().getIntExtra(INTENT_CLUSTER_VALUE, Consts.DEFAULT_CLUSTER_VALUE);
			clustValue2 = getIntent().getIntExtra(INTENT_CLUSTER_VALUE2, Consts.DEFAULT_CLUSTER_VALUE);
			clustValue3 = getIntent().getIntExtra(INTENT_CLUSTER_VALUE3, Consts.DEFAULT_CLUSTER_VALUE);
			clustStat = getIntent().getIntExtra(INTENT_CLUSTER_VALUE_STATUS, 0);
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
