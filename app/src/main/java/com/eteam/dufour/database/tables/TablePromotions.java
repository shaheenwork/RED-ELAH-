package com.eteam.dufour.database.tables;

import java.util.ArrayList;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.DBFilterArg;
import com.eteam.dufour.database.DBUtils;
import com.eteam.dufour.viewmodel.ModelSurveyPromotions;
import com.eteam.utils.Util;

public class TablePromotions extends DBTable{
	// ===========================================================
	// Constants
	// ===========================================================
	
	public enum PromotionType{ACTIVE,NON_ACTIVE,FUTURE};
	
	public static final String TABLE_NAME = "promotions";
	
	public static final String COLUMN_ID 				= "_id";
	public static final String COLUMN_SELL_OUT_FINE 	= "Sell_out_fine";
	public static final String COLUMN_SELL_IN_INIZIO 	= "Sell_in_inizio";
	public static final String COLUMN_ORDER_INIZIO 		= "order_inizio";
	public static final String COLUMN_SELL_OUT_INIZIO	= "Sell_out_inizio";
	public static final String COLUMN_CAMP_CODE 		= "Camp_code";
	public static final String COLUMN_SELL_IN_FINE 		= "Sell_in_fine";
	public static final String COLUMN_ORDER_DATE 		= "Order_date";
	public static final String COLUMN_LINK_CODE 		= "Link_code";
	public static final String COLUMN_PROMO_DESC 		= "Promo_Desc";
	public static final String COLUMN_PROMO_STATUS		= "Promo_status";
	public static final String COLUMN_ORDER_FINE 		= "order_fine";
	public static final String COLUMN_CUSTOMER_NO 		= "Customer_No";
	public static final String COLUMN_PROMO_CODE 		= "Promo_code";
	public static final String COLUMN_ITEM_CODE 		= "Item_Code";

	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME+ "("
												+COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+COLUMN_SELL_OUT_FINE+" TEXT, "
												+COLUMN_SELL_IN_INIZIO+" TEXT, "
												+COLUMN_ORDER_INIZIO+" TEXT, "
												+COLUMN_SELL_OUT_INIZIO+" TEXT, "
												+COLUMN_CAMP_CODE+" TEXT, "
												+COLUMN_SELL_IN_FINE+" TEXT, "
												+COLUMN_ORDER_DATE+" TEXT, "
												+COLUMN_LINK_CODE+" TEXT, "
												+COLUMN_PROMO_DESC+" TEXT, "
												+COLUMN_PROMO_STATUS+" TEXT, "
												+COLUMN_ORDER_FINE+" TEXT, "
												+COLUMN_CUSTOMER_NO+" TEXT, "
												+COLUMN_PROMO_CODE+" TEXT, "
												+COLUMN_ITEM_CODE+" TEXT);";
	
//	DB.execSQL("CREATE TABLE IF NOT EXISTS " + PROMOTIONS + 
//			"(_id INTEGER PRIMARY KEY,Item_Code VARCHAR,Customer_No VARCHAR,Promo_code VARCHAR,Sell_in_inizio VARCHAR," +
//			"Sell_in_fine VARCHAR,order_inizio VARCHAR,order_fine VARCHAR,Sconto1 VARCHAR,Sconto2 VARCHAR,Sconto3 VARCHAR," +
//			"Sconto4 VARCHAR,Promo_Desc VARCHAR,Camp_code VARCHAR,Camp_Desc VARCHAR,SCONTO_MERCE VARCHAR,Sell_out_inizio VARCHAR," +
//			"Sell_out_fine VARCHAR,Promo_status VARCHAR,unit_price VARCHAR,disount_price VARCHAR,FinalPrice VARCHAR,Order_date VARCHAR);");
	
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

	// ===========================================================
	// Methods
	// ===========================================================
	
	public static final boolean doesPromoExist(String promo_code){
		boolean stat = false;
		Cursor c = getDB().query(TablePromotions.TABLE_NAME, 
						new String[]{TablePromotions.COLUMN_ID}, 
						TablePromotions.COLUMN_PROMO_CODE+"=?"+" AND "+TablePromotions.COLUMN_PROMO_STATUS+"= 1",
				new String[]{promo_code}, null, null, null);
		
		if(c.moveToFirst()){
			stat = true;
		}
		c.close();
		
		return stat;
	}
	
	public static final boolean doesItemExist(String promocode,String itemcode){
		boolean stat = false;
		Cursor c = getDB().query(TablePromotions.TABLE_NAME, new String[]{TablePromotions.COLUMN_ID}, 
				TablePromotions.COLUMN_PROMO_CODE+"= ?"	+" AND "+TablePromotions.COLUMN_ITEM_CODE+"= ?",
				new String[]{promocode,itemcode}, null, null, null);
		
		if(c.moveToFirst()){
			stat = true;
		}
		c.close();
		
		return stat;
	}
	
	public static BasePromotions getProductDetails(String promoCode,String customerCode){
		Cursor c = getDB().query(TablePromotions.TABLE_NAME, 
				new String[]{TablePromotions.COLUMN_SELL_OUT_INIZIO,
				TablePromotions.COLUMN_SELL_OUT_FINE},
				TablePromotions.COLUMN_PROMO_CODE+"=? AND "
				+TablePromotions.COLUMN_CUSTOMER_NO+"=?", 
				new String[]{promoCode,customerCode}, null, null, null);
		BasePromotions promotion = null;
		
		if(c.moveToFirst()){
			promotion = new BasePromotions(promoCode, c);
		}
		c.close();
			
		
		return promotion;
	}
	
	
	public static String getPromoAttiveCount(long surveyId,String customerCode){
		 return String.valueOf(getPromoCount(surveyId, customerCode, "1"));
	}
	
	public static String getPromoNonAttiveCount(long surveyId,String customerCode){
		 return String.valueOf(getPromoCount(surveyId, customerCode, "2"));
	}
	
	public static String getPromoProssimeCount(long surveyId,String customerCode){
		 return String.valueOf(getPromoCount(surveyId, customerCode, "3"));
	}
	
	private static int getPromoCount(long surveyId,String customerCode,String status){
		 Cursor c = getDB().rawQuery("SELECT COUNT(1) FROM"
				 					+" (SELECT "+TablePromotions.COLUMN_PROMO_CODE
										+" FROM "+TablePromotions.TABLE_NAME
										+" LEFT JOIN "+TableSurveyPromotions.TABLE_NAME
										+" ON "+TablePromotions.TABLE_NAME+"."+TablePromotions.COLUMN_PROMO_CODE
												+"="
											+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_PROMOTION_CODE
											+" AND "+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_SURVEY_ID+"="+surveyId
										+" WHERE "+TablePromotions.COLUMN_CUSTOMER_NO+"= ?"
											+" AND "+TablePromotions.COLUMN_PROMO_STATUS+"= ?"
										+" GROUP BY "+TablePromotions.COLUMN_PROMO_CODE+")",
										new String[]{customerCode,status});
		 int count = 0;
		 if(c.moveToFirst()){
			 count = c.getInt(0);
		 }
		 c.close();
		 
		 return count;
	}
	
	public static boolean doesPromoExist(String customerCode,String itemNo){
		Cursor c = getDB().query(TablePromotions.TABLE_NAME, 
				new String[]{TablePromotions.COLUMN_ID}, 
				TablePromotions.COLUMN_CUSTOMER_NO+" = ? AND "+TablePromotions.COLUMN_ITEM_CODE+"= ?",
				new String[]{customerCode,itemNo}, null, null, null);
		boolean status = false;
		
		if(c.moveToFirst()){
			status =  true;
		}
		c.close();
		return status;
	}
	
	public static final Promotion getPromotionForItem(String customerCode,String itemNo){
		Cursor c = getDB().query(TablePromotions.TABLE_NAME, 
				new String[]{TablePromotions.COLUMN_CAMP_CODE,TablePromotions.COLUMN_LINK_CODE
				,TablePromotions.COLUMN_PROMO_CODE,TablePromotions.COLUMN_PROMO_STATUS
				,TablePromotions.COLUMN_SELL_OUT_INIZIO,TablePromotions.COLUMN_SELL_OUT_FINE
				,TablePromotions.COLUMN_SELL_IN_INIZIO,TablePromotions.COLUMN_SELL_IN_FINE
				,TablePromotions.COLUMN_ORDER_INIZIO,TablePromotions.COLUMN_ORDER_FINE
				,TablePromotions.COLUMN_PROMO_DESC}, 
				TablePromotions.COLUMN_CUSTOMER_NO+"='"+customerCode
				+"' AND "+TablePromotions.COLUMN_ITEM_CODE+"='"+itemNo+"'", null, null, null, null);
		
		Promotion promotion = null;
		if(c.moveToFirst()){
			promotion = new Promotion(c);
		}
		c.close();
		return promotion;
	}
	
	public static final Promotion getPromotionForPromo(String customerCode,String promoCode){
		Cursor c = getDB().query(TablePromotions.TABLE_NAME, 
				new String[]{TablePromotions.COLUMN_CAMP_CODE,TablePromotions.COLUMN_LINK_CODE
				,TablePromotions.COLUMN_PROMO_CODE,TablePromotions.COLUMN_PROMO_STATUS
				,TablePromotions.COLUMN_SELL_OUT_INIZIO,TablePromotions.COLUMN_SELL_OUT_FINE
				,TablePromotions.COLUMN_SELL_IN_INIZIO,TablePromotions.COLUMN_SELL_IN_FINE
				,TablePromotions.COLUMN_ORDER_INIZIO,TablePromotions.COLUMN_ORDER_FINE
				,TablePromotions.COLUMN_PROMO_DESC}, 
				TablePromotions.COLUMN_CUSTOMER_NO+"= ? AND "+TablePromotions.COLUMN_PROMO_CODE+"= ? LIMIT 1"
				, new String[]{customerCode,promoCode}, null, null, null);
		
		Promotion promotion = null;
		if(c.moveToFirst()){
			promotion = new Promotion(c);
		}
		c.close();
		return promotion;
			
	}
	
	private static final String TAG = TablePromotions.class.getName();
	
	public static ArrayList<ModelSurveyPromotions> getSurveyPromotions(
			long surveyId, String customerCode, String salesPersonCode,
			PromotionType type, SearchCriteria criteria) {
		ArrayList<ModelSurveyPromotions> promotions = new ArrayList<ModelSurveyPromotions>();
		populateSurveyPromotions(surveyId, customerCode
				, salesPersonCode, type, criteria, promotions);
		return promotions;
	}
	
	public static final void populateSurveyPromotions(long surveyId,
			String customerCode,String salesPersonCode,
			PromotionType type,SearchCriteria criteria,ArrayList<ModelSurveyPromotions> promotions){
		promotions.clear();
		
		String query = "SELECT "+TablePromotions.COLUMN_PROMO_DESC
								+","+TablePromotions.COLUMN_PROMO_CODE
								+","+TablePromotions.COLUMN_CAMP_CODE
								+","+TablePromotions.COLUMN_LINK_CODE
								+","+TablePromotions.COLUMN_PROMO_STATUS
								+","+TablePromotions.COLUMN_SELL_IN_INIZIO
								+","+TablePromotions.COLUMN_SELL_IN_FINE
								+","+TablePromotions.COLUMN_ORDER_INIZIO
								+","+TablePromotions.COLUMN_ORDER_FINE
								+","+TablePromotions.COLUMN_SELL_OUT_INIZIO
								+","+TablePromotions.COLUMN_ITEM_CODE
								+","+TablePromotions.COLUMN_SELL_OUT_FINE
								+","+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_ID
								+","+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_FLAG
								+","+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_SURVEY_VISIBILITY
								+","+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_SURVEY_MODIFY
								+","+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_UNIQUE_FIELD
			+" FROM "+TablePromotions.TABLE_NAME
			+" LEFT JOIN "+TableSurveyPromotions.TABLE_NAME+" ON "
				+TablePromotions.TABLE_NAME+"."+TablePromotions.COLUMN_PROMO_CODE+"="
				+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_PROMOTION_CODE
			+" AND "+TableSurveyPromotions.TABLE_NAME+"."+TableSurveyPromotions.COLUMN_SURVEY_ID+"="+surveyId
			+" WHERE "+TablePromotions.COLUMN_CUSTOMER_NO+"= ?";
		
		ArrayList<String> arguments = new ArrayList<String>();
		arguments.add(customerCode);
		
		switch (type) {
			case ACTIVE:
				query += " AND "+TablePromotions.COLUMN_PROMO_STATUS+"='1'";
				break;
			case NON_ACTIVE:
				query += " AND "+TablePromotions.COLUMN_PROMO_STATUS+"='2'";
				break;
			case FUTURE:
				query += " AND "+TablePromotions.COLUMN_PROMO_STATUS+"='3'";
				break;
	
			default:
				break;
		}
		if(criteria!=null){
			DBFilterArg filter = criteria.getSelection();
			if(filter!=null){
				query += " AND "+filter.getSelection();
				arguments.addAll(filter.getSelectionArg());
			}
		}
		
		query += " GROUP BY "+TablePromotions.COLUMN_PROMO_CODE;
		
		String[] arg = null;
		if(!arguments.isEmpty()){
			arg = new String[arguments.size()];
			arguments.toArray(arg);
		}
		
		Log.d(TAG, "Query is "+query);
		
		Cursor c = getDB().rawQuery(query, arg);
		
		if(c.moveToFirst()){
			while(!c.isAfterLast()){
				promotions.add(new ModelSurveyPromotions(surveyId
						, customerCode, salesPersonCode, c,true));
				c.moveToNext();
			}
		}
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class SearchCriteria {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================
		
		private String argSearch;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public SearchCriteria(String argSearch) {
			this.argSearch = argSearch;
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================

				
		public void setArgSearch(String argSearch) {
			this.argSearch = argSearch;
		}
		
		public String getArgSearch() {
			return argSearch;
		}
		
				
		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================
		
		

		
		// ===========================================================
		// Methods
		// ===========================================================
		public boolean equals(SearchCriteria criteria){
			if(criteria==null){
				return false;
			}
			if(!Util.stringEquals(argSearch, criteria.argSearch)){
				return false;
			}
			
			return true;
		}
		
		public DBFilterArg getSelection(){
			String baseQuery = "";
			ArrayList<String> arguments = new ArrayList<String>();
			
			if(!TextUtils.isEmpty(argSearch)){
				baseQuery = baseQuery+" ("+COLUMN_PROMO_DESC+" LIKE ? OR "
						+COLUMN_PROMO_CODE+" LIKE ?)";
				
				argSearch = "%"+argSearch+"%";
				
				arguments.add(argSearch);
				arguments.add(argSearch);
			}
			
			if(arguments.size()==0)
				return null;
			
			
			return new DBFilterArg(baseQuery, arguments);
		}
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
	
	public static class Promotion extends BasePromotions {
		
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================
		private String campCode;
		private String linkCode;
		private String promoStatus;
		private String promoDesc;
		private String itemDesc;
		private String sellInInzio;
		private String sellInFine;
		private String orderInzio;
		private String orderFine;
		// ===========================================================
		// Constructors
		// ===========================================================
		
		public Promotion(Cursor c) {
			super(c);
			campCode 	= DBUtils.getStringForJSON(c, TablePromotions.COLUMN_CAMP_CODE);
			linkCode 	= DBUtils.getStringForJSON(c, TablePromotions.COLUMN_LINK_CODE);
			promoStatus = DBUtils.getStringForJSON(c, TablePromotions.COLUMN_PROMO_STATUS);
			sellInInzio = DBUtils.getStringForJSON(c, TablePromotions.COLUMN_SELL_IN_INIZIO);
			sellInFine  = DBUtils.getStringForJSON(c, TablePromotions.COLUMN_SELL_IN_FINE);
			orderInzio  = DBUtils.getStringForJSON(c, TablePromotions.COLUMN_ORDER_INIZIO);
			orderFine	= DBUtils.getStringForJSON(c, TablePromotions.COLUMN_ORDER_FINE);
			promoDesc	= DBUtils.getStringForJSON(c, TablePromotions.COLUMN_PROMO_DESC);
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================
		public String getCampCode() {
			return Util.filterDetails(campCode);
		}
		public void setCampCode(String campCode) {
			this.campCode = campCode;
		}
		public String getLinkCode() {
			return Util.filterDetails(linkCode);
		}
		
		public String getLinkCodeAsUrl() {
			return Util.filterUrlDetails(linkCode);
		}
		public void setLinkCode(String linkCode) {
			this.linkCode = linkCode;
		}
		public String getPromoCode() {
			return Util.filterDetails(promoCode);
		}
		public void setPromoCode(String promoCode) {
			this.promoCode = promoCode;
		}
		public String getPromoStatus() {
			return Util.filterDetails(promoStatus);
		}
		public void setPromoStatus(String promoStatus) {
			this.promoStatus = promoStatus;
		}
		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================
		public String getItemDesc() {
			return Util.filterDetails(itemDesc);
		}
		public void setItemDesc(String itemDesc) {
			this.itemDesc = itemDesc;
		}
		
		
		public String getSellInFine() {
			return sellInFine;
		}
		public void setSellInFine(String sellInFine) {
			this.sellInFine = sellInFine;
		}
		public String getSellInInzio() {
			return sellInInzio;
		}
		public void setSellInInzio(String sellInInzio) {
			this.sellInInzio = sellInInzio;
		}
		public String getOrderFine() {
			return orderFine;
		}
		public void setOrderFine(String orderFine) {
			this.orderFine = orderFine;
		}
		public String getOrderInzio() {
			return orderInzio;
		}
		public void setOrderInzio(String orderInzio) {
			this.orderInzio = orderInzio;
		}
		public String getPromoDesc() {
			return promoDesc;
		}
		public void setPromoDesc(String promoDesc) {
			this.promoDesc = promoDesc;
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}

	
	public static class BasePromotions {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================

//		protected long		id;
		protected String 	promoCode;
		private String		sellOutInzio;
		private String 		sellOutFine;
		
		
		// ===========================================================
		// Constructors
		// ===========================================================
		
		public BasePromotions(Cursor c) {
//			this.id				= c.getLong(c.getColumnIndex(COLUMN_ID));
			this.promoCode 		= c.getString(c.getColumnIndex(TablePromotions.COLUMN_PROMO_CODE));;
			this.sellOutInzio	= c.getString(c.getColumnIndex(TablePromotions.COLUMN_SELL_OUT_INIZIO));
			this.sellOutFine  	= c.getString(c.getColumnIndex(TablePromotions.COLUMN_SELL_OUT_FINE));
		}
		
		public BasePromotions(String promoCode,Cursor c) {
			this.promoCode 			= promoCode;
//			this.id					= c.getLong(c.getColumnIndex(COLUMN_ID));
			this.sellOutInzio 		= c.getString(c.getColumnIndex(TablePromotions.COLUMN_SELL_OUT_INIZIO));
			this.sellOutFine 		= c.getString(c.getColumnIndex(TablePromotions.COLUMN_SELL_OUT_FINE));
		}
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================
		public String getPromoCode() {
			return promoCode;
		}
		public void setPromoCode(String promoCode) {
			this.promoCode = promoCode;
		}

		public String getSellOutInzio() {
			return sellOutInzio;
		}

		public void setSellOutInzio(String sellOutInzio) {
			this.sellOutInzio = sellOutInzio;
		}

		public String getSellOutFine() {
			return sellOutFine;
		}

		public void setSellOutFine(String sellOutFine) {
			this.sellOutFine = sellOutFine;
		}
		
		
		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}


	

}
