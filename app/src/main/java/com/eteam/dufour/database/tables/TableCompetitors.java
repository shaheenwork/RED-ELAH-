package com.eteam.dufour.database.tables;

import java.util.ArrayList;

import android.database.Cursor;
import android.text.TextUtils;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.DBFilterArg;
import com.eteam.dufour.viewmodel.ModelSurveyCompetitor;
import com.eteam.utils.Util;

public class TableCompetitors extends DBTable{
	// ===========================================================
	// Constants
	// ===========================================================


	public static final String MARCHIO_UNSELECTED 	= "Marchio";
	public static final String LINEA_UNSELECTED 	= "Linea";
	
	
	public static final String TABLE_NAME = "competitors";
	
	private static final String COLUMN_ID 							= "_id";
	private static final String COLUMN_COMPETITOR_ID 				= "competitor_id";
	private static final String COLUMN_PRODUCT_ACTIVE 				= "product_active";
	private static final String COLUMN_PRODUCT_BRAND_ID 			= "product_brand_id";
	private static final String COLUMN_PRODUCT_LINE_ID 				= "product_line_id";
	private static final String COLUMN_PRODUCT_ID 					= "product_id";
	private static final String COLUMN_COMPETITOR_NAME 				= "competitor_name";
	private static final String COLUMN_PRODUCT_LINE_DESCRIPTION 	= "product_line_description";
	private static final String COLUMN_PRODUCT_BRAND_DESCRIPTION 	= "product_brand_description";
	private static final String COLUMN_PRODUCT_DESCRIPTION 			= "product_description";	
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
										+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
											+COLUMN_PRODUCT_ID+" TEXT, "
											+COLUMN_PRODUCT_DESCRIPTION+" varchar, "
											+COLUMN_PRODUCT_ACTIVE+" varchar, "
											+COLUMN_COMPETITOR_ID+" varchar, "
											+COLUMN_COMPETITOR_NAME+" varchar, "
											+COLUMN_PRODUCT_LINE_ID+" varchar, "
											+COLUMN_PRODUCT_LINE_DESCRIPTION+" varchar, "
											+COLUMN_PRODUCT_BRAND_ID+" varchar, "
											+COLUMN_PRODUCT_BRAND_DESCRIPTION+" varchar)";
	
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

	public static final void populateMarchio(ArrayList<String> list){
		list.clear();
		list.add(MARCHIO_UNSELECTED);
		Cursor c = getDB().query(true, TableCompetitors.TABLE_NAME, new String[]{TableCompetitors.COLUMN_PRODUCT_BRAND_DESCRIPTION}
					, null,	null, null, null, null, null);
		
		if(	c.moveToFirst()){
			while(!c.isAfterLast()){
				list.add(c.getString(c.getColumnIndex(TableCompetitors.COLUMN_PRODUCT_BRAND_DESCRIPTION)));
				c.moveToNext();
			}
		}
		c.close();
	}
	
	public static final void populateLinea(ArrayList<String> list, String marchio){
		list.clear();
		list.add(LINEA_UNSELECTED);
		Cursor c = getDB().query(true, TableCompetitors.TABLE_NAME, new String[]{TableCompetitors.COLUMN_PRODUCT_LINE_DESCRIPTION}, 
				TableCompetitors.COLUMN_PRODUCT_BRAND_DESCRIPTION+"='"+marchio+"'", null, null, null, null, null);
		
		if(c.moveToFirst()){;
			while(!c.isAfterLast()){
				list.add(c.getString(c.getColumnIndex(TableCompetitors.COLUMN_PRODUCT_LINE_DESCRIPTION)));
				c.moveToNext();
			}
		}
		c.close();
		
	}
	
	public static int getCount(long surveyId){
		Cursor c = getDB().rawQuery("SELECT COUNT(1) FROM ( SELECT "
												+TableCompetitors.TABLE_NAME+"."+TableCompetitors.COLUMN_PRODUCT_ID
										+" FROM "+TableCompetitors.TABLE_NAME
										+" LEFT JOIN "+TableSurveyCompetitor.TABLE_NAME+" ON "
											+TableCompetitors.TABLE_NAME+"."+TableCompetitors.COLUMN_PRODUCT_ID+"="
										+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_PRODUCT_ID
										+" AND "+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_SURVEY_ID+"="+surveyId+")"
									,null);
		 int count = 0;
		 if(c.moveToFirst()){
			 count = c.getInt(0);
		 }
		 c.close();
		 return count;
	}
		
	public static ArrayList<ModelSurveyCompetitor> getCompetitors(long surveyId,String salesPersonCode){
		return getCompetitors(surveyId,salesPersonCode, null);
	}
	
	
	public static ArrayList<ModelSurveyCompetitor> getCompetitors(long surveyId,String salesPersonCode,SearchCriteria criteria){
		ArrayList<ModelSurveyCompetitor> competitors = new ArrayList<ModelSurveyCompetitor>();
		populateCompetitors( surveyId,criteria,salesPersonCode,competitors);
		return competitors;
	}
	
	public static void populateCompetitors(long surveyId,String salesPersonCode,ArrayList<ModelSurveyCompetitor> surveys ){
		populateCompetitors(surveyId, null, salesPersonCode, surveys);
	}
	
	
	public static void populateCompetitors(long surveyId,SearchCriteria criteria,String salesPersonCode,ArrayList<ModelSurveyCompetitor> surveys){
		
		String query = "SELECT "+TABLE_NAME+"."	+COLUMN_COMPETITOR_NAME
								+","+TABLE_NAME+"."+COLUMN_PRODUCT_ID
								+","+TABLE_NAME+"."+COLUMN_PRODUCT_DESCRIPTION
								+","+TABLE_NAME+"."+COLUMN_ID
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_ID+" AS "+TableSurveyCompetitor.ALIAS_ID
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_SURVEY_ID
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_PRODUCT_ID
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_FACING
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_NOTE
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_PREZZO
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_POS_LINEA
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_TAGLIO
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_VISIBILITY
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_VOLANTINO
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_UNIQUE_FIELD
								+","+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_SURVEY_MODIFIED
						+" FROM "+TableCompetitors.TABLE_NAME
						+" LEFT JOIN "+TableSurveyCompetitor.TABLE_NAME+" ON "
								+TABLE_NAME+"."+COLUMN_PRODUCT_ID+"="
								+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_PRODUCT_ID
							+" AND "
								+TableSurveyCompetitor.TABLE_NAME+"."+TableSurveyCompetitor.COLUMN_SURVEY_ID+"="+surveyId;
		
		
		String[] selectionArg = null; 
		if(criteria!=null){
			DBFilterArg searchFilter = criteria.getSelection();
			if(searchFilter!=null){
				query 		 += " WHERE "+searchFilter.getSelection();
				selectionArg = searchFilter.getSelectionArgAsArray();
			}
		}
		
		query = query+" ORDER BY "+TableCompetitors.COLUMN_PRODUCT_DESCRIPTION;
		
		Cursor c = getDB().rawQuery(query,selectionArg );
		surveys.clear();
		if(c.moveToFirst()){
			while(!c.isAfterLast()){
				surveys.add(new ModelSurveyCompetitor(surveyId,salesPersonCode,c,true));
				c.moveToNext();
			}
		}
	}
	


	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class BaseCompetitor {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================
		private long   id;
		private String productId;
		private String name;
		private String productDescription;
		// ===========================================================
		// Constructors
		// ===========================================================
		public BaseCompetitor(Cursor c) {
			id 		= c.getLong(c.getColumnIndex(COLUMN_ID));
			productId = c.getString(c.getColumnIndex(COLUMN_PRODUCT_ID));
			name 	= c.getString(c.getColumnIndex(COLUMN_COMPETITOR_NAME));
			productDescription = c.getString(c.getColumnIndex(COLUMN_PRODUCT_DESCRIPTION));
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
		
		public String getProductId() {
			return productId;
		}
		
		public String getName() {
			return name;
		}
		
		public String getProductDescription() {
			return productDescription;
		}
		
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
	
	

	public static class SearchCriteria {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================
		
		private String marchio;
		private String linea;
		private String argSearch;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public SearchCriteria(String marchio,String linea,String argSearch) {
			this.marchio = marchio;
			this.linea   = linea;
			this.argSearch = argSearch;
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public void setMarchio(String marchio) {
			this.marchio = marchio;
		}
		
		public String getMarchio() {
			return marchio;
		}
		
		public void setLinea(String linea) {
			this.linea = linea;
		}
		
		public String getLinea() {
			return linea;
		}
		
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
		public DBFilterArg getSelection(){
			String baseQuery = "";
			ArrayList<String> arguments = new ArrayList<String>();
			boolean multipleArg = false;
			
			if(!TextUtils.isEmpty(marchio)){
				if(multipleArg){
					baseQuery = baseQuery+" AND ";
				}
				else {
					multipleArg = true;
				}
				baseQuery = baseQuery+TableCompetitors.COLUMN_PRODUCT_BRAND_DESCRIPTION+" = ?";
				arguments.add(marchio);
			}
			if(!TextUtils.isEmpty(linea)){
				if(multipleArg){
					baseQuery = baseQuery+" AND ";
				}
				else {
					multipleArg = true;
				}
				baseQuery = baseQuery+TableCompetitors.COLUMN_PRODUCT_LINE_DESCRIPTION+" = ?";
				arguments.add(linea);
			}
			
			if(!TextUtils.isEmpty(argSearch)){
				if(multipleArg){
					baseQuery = baseQuery+" AND ";
				}
				else {
					multipleArg = true;
				}
				baseQuery = baseQuery+" ("+COLUMN_PRODUCT_DESCRIPTION+" LIKE ? OR "
						+COLUMN_COMPETITOR_NAME+" LIKE ?)";
				
				argSearch = "%"+argSearch+"%";
				
				arguments.add(argSearch);
				arguments.add(argSearch);
			}
			
			if(arguments.size()==0)
				return null;
						
			return new DBFilterArg(baseQuery, arguments);
		}
		
		public boolean equals(SearchCriteria criteria){
			if(criteria==null){
				return false;
			}
			if(!Util.stringEquals(argSearch, criteria.argSearch)){
				return false;
			}
			else if(!Util.stringEquals(marchio, criteria.marchio)){
				return false;
			}
			else if(!Util.stringEquals(linea, criteria.linea)){
				return false;
			}
			
			return true;
		}
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
	
	
}
