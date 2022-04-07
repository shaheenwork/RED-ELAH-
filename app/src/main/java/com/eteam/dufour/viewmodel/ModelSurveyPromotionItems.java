package com.eteam.dufour.viewmodel;

import org.json.JSONObject;

import android.database.Cursor;

import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.dufour.database.tables.TableSurveyPromotionsItem.SurveyPromotionItem;

public class ModelSurveyPromotionItems {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Item		  		  item;
	private SurveyPromotionItem	  survey;
	
	private String			 salesPersonCode;
	private long			 promotionId;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ModelSurveyPromotionItems(long promotionId,String salesPersonCode,Cursor c,boolean usesAlias) {
		this.item 	 	 	 = new Item(c);
		this.survey 	   	 = new SurveyPromotionItem(promotionId,salesPersonCode,item,c,usesAlias);
		this.promotionId	 = promotionId;
		this.salesPersonCode = salesPersonCode;
//		if(this.survey.getSurveyId()==0){
//			populateSurveyBase();
//		}
	}
	
	public ModelSurveyPromotionItems(long promotionId,String salesPersonCode,JSONObject json) {
//		this.item 	 	 	 = new Item(c);
//		this.survey 	   	 = new SurveyPromotionItem(promotionId,salesPersonCode,item,c,usesAlias);
		this.survey			 = new SurveyPromotionItem(promotionId, salesPersonCode, json);
		this.promotionId	 = promotionId;
		this.salesPersonCode = salesPersonCode;
//		if(this.survey.getSurveyId()==0){
//			populateSurveyBase();
//		}
	}
	
	

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public Item getItem() {
		return item;
	}
	

	
	
	public SurveyPromotionItem getSurvey() {
		return survey;
	}
	
	public String getSalesPersonCode() {
		return salesPersonCode;
	}
	
	public long getPromotionId() {
		return promotionId;
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
