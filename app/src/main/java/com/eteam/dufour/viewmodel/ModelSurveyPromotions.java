package com.eteam.dufour.viewmodel;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.eteam.dufour.database.tables.TablePromotions.Promotion;
import com.eteam.dufour.database.tables.TableSurveyPromotions.SurveyPromotion;

public class ModelSurveyPromotions {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Promotion		   promotion;
	private SurveyPromotion	   survey;
	
	private String			 salesPersonCode;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ModelSurveyPromotions(long surveyId,String customerCode,String salesPersonCode,Cursor c,boolean usesAlias) {
		
		this.promotion 	 	 = new Promotion(c);
		this.survey 	   	 = new SurveyPromotion(surveyId,customerCode,salesPersonCode,promotion,c,usesAlias);
	
		this.salesPersonCode = salesPersonCode;
	}
	
	public ModelSurveyPromotions(long surveyId,String salesPersonCode,JSONObject json) throws JSONException {
		
//		this.promotion 	 	 = new Promotion(c);
		this.survey 	   	 = new SurveyPromotion(surveyId,salesPersonCode,json);
	
		this.salesPersonCode = salesPersonCode;
	}
	
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public Promotion getPromotion() {
		return promotion;
	}
	
	
	
	public SurveyPromotion getSurvey() {
		return survey;
	}
	
	public String getSalesPersonCode() {
		return salesPersonCode;
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
