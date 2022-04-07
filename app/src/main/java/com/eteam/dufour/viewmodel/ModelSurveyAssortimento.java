package com.eteam.dufour.viewmodel;

import android.database.Cursor;

import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.dufour.database.tables.TableSurveyAssortimento.SurveyAssortimento;

public class ModelSurveyAssortimento {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Item   			   item;
	private int allMandatoryFieldsFilled;
	private SurveyAssortimento survey;
	
	private String			 salesPersonCode;
	private long			 surveyId;
	private boolean			 viewToUpdate;
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ModelSurveyAssortimento(long surveyId,String salesPersonCode,Cursor c,boolean usesAlias) {
		this.surveyId		 = surveyId;
		this.item	 	 	 = new Item(c);
		this.survey 	   	 = new SurveyAssortimento(surveyId,salesPersonCode,item,c,usesAlias);
		allMandatoryFieldsFilled=1; //initially no rows are highlighted in red. highlight only after chiusura Attiva button clicked
	
		this.salesPersonCode = salesPersonCode;
		if(this.survey.getSurveyId()==0){
			populateSurveyBase();
		}
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public Item getItem() {
		return item;
	}
	
	public void setViewToUpdate(boolean viewToUpdate) {
		this.viewToUpdate = viewToUpdate;
	}
	
	public boolean isViewToUpdate() {
		return viewToUpdate;
	}
	
	public SurveyAssortimento getSurvey() {
		return survey;
	}
	
	public String getSalesPersonCode() {
		return salesPersonCode;
	}
	
	public void populateSurveyBase(){
		survey.setBaseValues(surveyId, item, salesPersonCode);
	}

	public int getAllMandatoryFieldsFilled() {
		return allMandatoryFieldsFilled;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setAllMandatoryFieldsFilled(int allMandatoryFieldsFilled) {
		this.allMandatoryFieldsFilled = allMandatoryFieldsFilled;
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
