package com.eteam.dufour.items;

import org.json.JSONObject;

public class IPromoItemOnline {
	// ===========================================================
	// Constants
	// ===========================================================


	
	private static final String KEY_UNIT_PRICE = "unit_price";
	private static final String KEY_DISCOUNT_PRICE = "disount_price";
	private static final String KEY_SCONTO_ONE = "Sconto1";
	private static final String KEY_SCONTO_TWO = "Sconto2";
	private static final String KEY_SCONTO_THREE = "Sconto3";
	private static final String KEY_SCONTO_FOUR = "Sconto4";
	private static final String KEY_CAMP_DESC = "Camp_Desc";
	private static final String KEY_SCONTO_MERCE = "SCONTO_MERCE";
	private static final String KEY_FINAL_PRICE = "FinalPrice";
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private String unitPrice;
	private String discountPrice;
	private String scontoOne;
	private String scontoTwo;
	private String scontoThree;
	private String scontoFour;
	private String campDesc;
	private String scontoMerce;
	private String finalPrice;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public IPromoItemOnline(JSONObject json) {
		campDesc 		= json.optString(IPromoItemOnline.KEY_CAMP_DESC,"");
		discountPrice 	= json.optString(IPromoItemOnline.KEY_DISCOUNT_PRICE,"");
		finalPrice		= json.optString(IPromoItemOnline.KEY_FINAL_PRICE,"");
		scontoOne		= json.optString(IPromoItemOnline.KEY_SCONTO_ONE,"");
		scontoTwo		= json.optString(IPromoItemOnline.KEY_SCONTO_TWO,"");
		scontoThree		= json.optString(IPromoItemOnline.KEY_SCONTO_THREE,"");
		scontoFour		= json.optString(IPromoItemOnline.KEY_SCONTO_FOUR,"");
		scontoMerce		= json.optString(IPromoItemOnline.KEY_SCONTO_MERCE,"");
		unitPrice		= json.optString(IPromoItemOnline.KEY_UNIT_PRICE,"");
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}
	public String getScontoOne() {
		return scontoOne;
	}
	public void setScontoOne(String scontoOne) {
		this.scontoOne = scontoOne;
	}
	public String getScontoTwo() {
		return scontoTwo;
	}
	public void setScontoTwo(String scontoTwo) {
		this.scontoTwo = scontoTwo;
	}
	public String getScontoThree() {
		return scontoThree;
	}
	public void setScontoThree(String scontoThree) {
		this.scontoThree = scontoThree;
	}
	public String getScontoFour() {
		return scontoFour;
	}
	public void setScontoFour(String scontoFour) {
		this.scontoFour = scontoFour;
	}
	public String getCampDesc() {
		return campDesc;
	}
	public void setCampDesc(String campDesc) {
		this.campDesc = campDesc;
	}
	public String getScontoMerce() {
		return scontoMerce;
	}
	public void setScontoMerce(String scontoMerce) {
		this.scontoMerce = scontoMerce;
	}
	public String getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
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
