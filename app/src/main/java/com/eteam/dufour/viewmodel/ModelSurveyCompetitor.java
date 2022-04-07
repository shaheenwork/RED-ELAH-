package com.eteam.dufour.viewmodel;

import android.database.Cursor;

import com.eteam.dufour.database.tables.TableCompetitors.BaseCompetitor;
import com.eteam.dufour.database.tables.TableSurveyCompetitor.SurveyCompetitor;

public class ModelSurveyCompetitor {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	
	private BaseCompetitor   competitor;
	private SurveyCompetitor survey;
	
	private String			 salesPersonCode;
	private long			 surveyId;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ModelSurveyCompetitor(long surveyId,String salesPersonCode,Cursor c,boolean useAlias) {
		this.surveyId		 = surveyId;
		this.competitor 	 = new BaseCompetitor(c);
		this.survey 	   	 = new SurveyCompetitor(surveyId,salesPersonCode,competitor,c,useAlias);
		
		this.salesPersonCode = salesPersonCode;
		if(this.survey.getSurveyId()==0){
			populateSurveyBase();
		}
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public BaseCompetitor getCompetitor() {
		return competitor;
	}
	
	public SurveyCompetitor getSurvey() {
		return survey;
	}
	
	public String getSalesPersonCode() {
		return salesPersonCode;
	}
	
	public void populateSurveyBase(){
		survey.setBaseValues(surveyId, competitor.getProductId(), salesPersonCode);
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
