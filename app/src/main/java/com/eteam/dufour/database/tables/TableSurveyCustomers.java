package com.eteam.dufour.database.tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.eteam.db.DBTable;

public class TableSurveyCustomers extends DBTable{
	// ===========================================================
	// Constants
	// ===========================================================
	
	public static final String TABLE_NAME 				 = "survey_customers";
	
	public static final String COLUMN_ID 				 = "_id";
	public static final String COLUMN_SURVEY_ID 		 = "survey_id";
	public static final String COLUMN_SURVEY_DATE 		 = "survey_date";
	public static final String COLUMN_CUSTOMER_CODE 	 = "customer_code";
	public static final String COLUMN_SALESPERSON_CODE 	 = "salesperson_code";
	public static final String COLUMN_SURVEY_POST_STATUS = "survey_post_status";
	public static final String COLUMN_SYNC_DATE 		 = "survey_sync_date";
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
			+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COLUMN_SURVEY_ID+" INTEGER, "
			+COLUMN_SURVEY_DATE+" TEXT, "+COLUMN_CUSTOMER_CODE+" TEXT, "
			+COLUMN_SALESPERSON_CODE+" TEXT, "+COLUMN_SURVEY_POST_STATUS+" TEXT, "
			+COLUMN_SYNC_DATE+" TEXT)";
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
	
//	public static final void createCustomerSurvey(ElahDBHelper dbHelper,String syncDate,String postStatus
//			,String salesPersonCode,String customerCode,long surveyId
//			,String surveyDate){
//		ContentValues values = new ContentValues();
//		
//		
//		
//	}
	
	public static final void create(SurveyCustomer customer){
		getDB().insert(TABLE_NAME, null, customer.getValues());
	}
	
		
	public static final int getSurveySendCount(){
		Cursor c = getDB().rawQuery(
							"SELECT COUNT(1)"
							+" FROM(SELECT "+"a."+TableCustomers.COLUMN_ID
				    				+" FROM "+TableCustomers.TABLE_NAME+" a" 
				    					+" INNER JOIN "+TABLE_NAME+" b"
				    				+" WHERE a."+TableCustomers.COLUMN_NO+"=b."+COLUMN_CUSTOMER_CODE+")", null);
		int count = 0;
		if(c.moveToFirst()){
			count = c.getInt(0);
		}
		c.close();
		
		return count;
	}
	
	

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class SurveyCustomer {
		// ===========================================================
		// Constants
		// ===========================================================
		private static ContentValues values = new ContentValues();
		// ===========================================================
		// Fields
		// ===========================================================
		
		private long   id;
		private String syncDate;
		private int	   postStatus;
		private String salesPersonCode;
		private String customerCode;
		private long   surveyId;
		private String surveyDate;

		
		// ===========================================================
		// Constructors
		// ===========================================================
		public SurveyCustomer() {
			// TODO Auto-generated constructor stub
		}
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================
		
		public long getId() {
			return id;
		}
		
		public String getSyncDate() {
			return syncDate;
		}
		public void setSyncDate(String syncDate) {
			this.syncDate = syncDate;
		}
		public int getPostStatus() {
			return postStatus;
		}
		public void setPostStatus(int postStatus) {
			this.postStatus = postStatus;
		}
		public String getSalesPersonCode() {
			return salesPersonCode;
		}
		public void setSalesPersonCode(String salesPersonCode) {
			this.salesPersonCode = salesPersonCode;
		}
		public String getCustomerCode() {
			return customerCode;
		}
		public void setCustomerCode(String customerCode) {
			this.customerCode = customerCode;
		}
		public long getSurveyId() {
			return surveyId;
		}
		public void setSurveyId(long surveyId) {
			this.surveyId = surveyId;
		}
		public String getSurveyDate() {
			return surveyDate;
		}
		public void setSurveyDate(String surveyDate) {
			this.surveyDate = surveyDate;
		}
		
		public ContentValues getValues() {
			values.clear();
			values.put(TableSurveyCustomers.COLUMN_SYNC_DATE, syncDate);
			values.put(TableSurveyCustomers.COLUMN_SURVEY_POST_STATUS, postStatus);
			values.put(TableSurveyCustomers.COLUMN_SURVEY_ID, surveyId);
			values.put(TableSurveyCustomers.COLUMN_SURVEY_DATE, surveyDate);
			values.put(TableSurveyCustomers.COLUMN_CUSTOMER_CODE, customerCode);
			return values;
		}
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}

}
