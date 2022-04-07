package com.eteam.dufour.database.tables;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.tables.TableCompetitors.BaseCompetitor;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class TableSurveyCompetitor extends DBTable{
	

	// ===========================================================
	// Constants
	// ===========================================================
	

	public static final String KEY_JSON_ARRAYNAME 		= "competitor";
	public static final String TABLE_NAME 				= "survey_competitor";
	
	public static final String KEY_ARRAY_NAME 			= "COMP";
	
	public static final String ALIAS_ID 				= "alias_survey_id";
	public static final String COLUMN_ID 				= "_id";
	public static final String COLUMN_SURVEY_ID 		= "Survey_Id";
	public static final String COLUMN_PRODUCT_ID 		= "Product_Id";
	public static final String COLUMN_VISIBILITY 		= "Visibility";
	public static final String COLUMN_VOLANTINO 		= "Volantino";
	public static final String COLUMN_PREZZO 			= "Prezzo";
	public static final String COLUMN_TAGLIO 			= "Taglio";
	public static final String COLUMN_FACING 			= "Facing";
	public static final String COLUMN_POS_LINEA 		= "Pos_Linea";
	public static final String COLUMN_NOTE 				= "Note";
	public static final String COLUMN_UNIQUE_FIELD 		= "Unique_Field";
	public static final String COLUMN_SURVEY_MODIFIED 	= "Survey_Competitor_Modify";
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
												+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
													+COLUMN_SURVEY_ID+" INTEGER, "
													+COLUMN_PRODUCT_ID+" INTEGER, "
													+COLUMN_VISIBILITY+" INTEGER, "
													+COLUMN_VOLANTINO+" INTEGER, "
													+COLUMN_PREZZO+" TEXT, "
													+COLUMN_TAGLIO+" TEXT, "
													+COLUMN_FACING+" INTEGER, "
													+COLUMN_POS_LINEA+" TEXT, "
													+COLUMN_NOTE+" TEXT, "
													+COLUMN_UNIQUE_FIELD+" TEXT,"
													+COLUMN_SURVEY_MODIFIED+" TEXT, "
												+"FOREIGN KEY("+COLUMN_SURVEY_ID+") REFERENCES "
													+TableSurvey.TABLE_NAME+"("+TableSurvey.COLUMN_ID+"))";
	
	
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

	public static void create(long surveyId,
			String salesPersonCode,JSONArray competitorArray) 
			throws JSONException{
		SQLiteDatabase db = getDB();
		db.beginTransaction();
		int size = competitorArray.length();
		for(int i=0;i<size;i++){
			SurveyCompetitor surveyCompetitor = new SurveyCompetitor(surveyId, 
					salesPersonCode, competitorArray.getJSONObject(i));
			if(surveyCompetitor.isValid()){
//				insertOrUpdate(surveyCompetitor, surveyCompetitor.getValues());
				getDB().insert(TableSurveyCompetitor.TABLE_NAME
						, null, surveyCompetitor.getValues());
			}
			
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
			
	private static void insertOrUpdate(SurveyCompetitor survey,ContentValues values){
		long id = survey.id;
		if(survey.isValid()){
			if(id==0||id==-1){
				survey.id = getDB().insert(TABLE_NAME, null, values);
			}
			getDB().update(TABLE_NAME, values, COLUMN_ID+"= "+survey.id, null);
		}
		else if(id!=0&&id!=-1){
			getDB().delete(TABLE_NAME, COLUMN_ID+"="+id, null);
			survey.id = -1;
		}
	}
	
	public static long createCompetitorSurvey(SurveyCompetitor survey){
		Cursor c = getDB().query(TableSurveyCompetitor.TABLE_NAME, 
				new String[]{TableSurveyCompetitor.COLUMN_ID}, TableSurveyCompetitor.COLUMN_SURVEY_ID+"= ?"
				+" AND "+TableSurveyCompetitor.COLUMN_PRODUCT_ID+"= ?", 
				new String[]{String.valueOf(survey.surveyId),String.valueOf(survey.productId)}
						, null, null, null);
		long id = -1;
		if(c.moveToFirst()){
			id = c.getLong(c.getColumnIndex(TableSurveyCompetitor.COLUMN_ID));
			c.close();
			return id;
		}
		c.close();		
		
		if(survey.isValid()){
			id = getDB().insert(TableSurveyCompetitor.TABLE_NAME
					, null, survey.getValues());
		}
		return id;
	}
	
	public static void delete(long surveyId){
		getDB().delete(TABLE_NAME, COLUMN_SURVEY_ID+"="+surveyId, null);
	}
	
	public static void delete(long surveyId,String productId){
		getDB().delete(TABLE_NAME, COLUMN_SURVEY_ID+"="+surveyId
				+" AND "+COLUMN_PRODUCT_ID+"='"+productId+"'", null);
	}

	
	public static void delete(String surveyIdList){
		getDB().delete(TABLE_NAME
				, COLUMN_SURVEY_ID+" IN (?)", new String[]{surveyIdList});
	}
	
	public static JSONArray getSurveyAsJSON(long surveyId) throws JSONException{
		
		Cursor c = getDB().query(TableSurveyCompetitor.TABLE_NAME, 
				new String[]{TableSurveyCompetitor.COLUMN_ID,TableSurveyCompetitor.COLUMN_SURVEY_ID
				,TableSurveyCompetitor.COLUMN_PRODUCT_ID,TableSurveyCompetitor.COLUMN_VISIBILITY
				,TableSurveyCompetitor.COLUMN_VOLANTINO,TableSurveyCompetitor.COLUMN_PREZZO
				,TableSurveyCompetitor.COLUMN_TAGLIO,TableSurveyCompetitor.COLUMN_FACING
				,TableSurveyCompetitor.COLUMN_POS_LINEA,TableSurveyCompetitor.COLUMN_NOTE
				,TableSurveyCompetitor.COLUMN_UNIQUE_FIELD,TableSurveyCompetitor.COLUMN_SURVEY_MODIFIED}, 
				TableSurveyCompetitor.COLUMN_SURVEY_ID+"= ?", 
				new String[]{String.valueOf(surveyId)}, null, null, null);
		JSONArray array = new JSONArray();
		
		if(c.moveToFirst()){
			while(!c.isAfterLast()){
				SurveyCompetitor survey = new SurveyCompetitor(c);
				array.put(survey.getAsJSON());
				c.moveToNext();
			}
		}
		c.close();
			
		return array;
		
	}
		
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class SurveyCompetitor {
		// ===========================================================
		// Constants
		// ===========================================================
		private static final ContentValues values = new ContentValues();
		
		
		public static final String KEY_PRODUCT_ID = "CP";
		public static final String KEY_SURVEY_PREZZO = "CSP";
		public static final String KEY_SURVEY_VISIBILITY = "CSV";
		public static final String KEY_SURVEY_VOLANTINO = "CSVO";
		public static final String KEY_SURVEY_TAGLIO_PZ = "CSTP";
		public static final String KEY_SURVEY_FACING = "CSF";
		public static final String KEY_SURVEY_PREZZO_LINEA = "CSPL";
		public static final String KEY_SURVEY_NOTE = "CSN";
		
		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		
		// ===========================================================
		// Fields
		// ===========================================================
		
		private long   id;
		private long   surveyId;
		private String productId;
		private int    visibility;
		private int    volantino;
		private String prezzo;
		private String taglioPZ;
		private int    facing;
		private String posLn;
		private String note;
		private String uniqueId;
		private String surveyModified;
		// ===========================================================
		// Constructors
		// ===========================================================

		public SurveyCompetitor(Cursor c) {
			id			= c.getLong(c.getColumnIndex(COLUMN_ID));
			surveyId	= c.getLong(c.getColumnIndex(COLUMN_SURVEY_ID));
			productId	= c.getString(c.getColumnIndex(COLUMN_PRODUCT_ID));
			facing   	= c.getInt(c.getColumnIndex(TableSurveyCompetitor.COLUMN_FACING));
			note	 	= c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_NOTE));
			prezzo   	= c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_PREZZO));
			posLn	 	= c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_POS_LINEA));
			taglioPZ   	= c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_TAGLIO));
			visibility 	= c.getInt(c.getColumnIndex(TableSurveyCompetitor.COLUMN_VISIBILITY));
			volantino	= c.getInt(c.getColumnIndex(TableSurveyCompetitor.COLUMN_VOLANTINO));
			uniqueId	= c.getString(c.getColumnIndex(COLUMN_UNIQUE_FIELD));
			surveyModified = c.getString(c.getColumnIndex(COLUMN_SURVEY_MODIFIED));
			addPropertyChangeListener(changeListener);
		}
		
		public SurveyCompetitor(long surveyId, String salesPersonCode, BaseCompetitor competitor,Cursor c,boolean useAlias) {
			
			id				= c.getLong(c.getColumnIndex(useAlias?ALIAS_ID:COLUMN_ID));
			this.surveyId	= surveyId;
			this.productId	= competitor.getProductId();
			this.facing   	= c.getInt(c.getColumnIndex(TableSurveyCompetitor.COLUMN_FACING));
			this.note	 	= c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_NOTE));
			this.prezzo   	= c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_PREZZO));
			this.posLn	 	= c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_POS_LINEA));
			this.taglioPZ   = c.getString(c.getColumnIndex(TableSurveyCompetitor.COLUMN_TAGLIO));
			this.visibility = c.getInt(c.getColumnIndex(TableSurveyCompetitor.COLUMN_VISIBILITY));
			this.volantino	= c.getInt(c.getColumnIndex(TableSurveyCompetitor.COLUMN_VOLANTINO));
			
			if(id==0||id==-1){
				uniqueId	   = Util.getUniqueId(salesPersonCode);
				updateModifiedTime();
			}
			else{
				uniqueId	= c.getString(c.getColumnIndex(COLUMN_UNIQUE_FIELD));
				surveyModified = c.getString(c.getColumnIndex(COLUMN_SURVEY_MODIFIED));
			}
			
			addPropertyChangeListener(changeListener);
		}
		
		public SurveyCompetitor(long surveyId,String salesPersonCode,JSONObject json) throws JSONException{
			this.surveyId 	= surveyId;
			this.uniqueId	= Util.getUniqueId(salesPersonCode);
			this.productId 	= json.optString(KEY_PRODUCT_ID);
			this.visibility	= json.optInt(KEY_SURVEY_VISIBILITY);
			this.volantino 	= json.optInt(KEY_SURVEY_VOLANTINO);
			this.prezzo		= json.optString(KEY_SURVEY_PREZZO);
			this.taglioPZ	= json.optString(KEY_SURVEY_TAGLIO_PZ);
			this.facing		= json.optInt(KEY_SURVEY_FACING);
			this.posLn		= json.optString(KEY_SURVEY_PREZZO_LINEA);
			this.note		= json.optString(KEY_SURVEY_NOTE);
			updateModifiedTime();
		}
		
		public SurveyCompetitor(long surveyId,
				String productId,int visibility,
				int volantino,	String prezzo,
				String taglioPZ,int facing,
				String posLn,String note,
				String salesPerson) {
			this.surveyId  	= surveyId;
			this.productId  = productId;
			this.visibility = visibility;
			this.volantino  = volantino;
			this.prezzo 	= Util.getAsElahDBNumFormat(prezzo);
			this.taglioPZ 	= Util.getAsElahDBNumFormat(taglioPZ);
			this.facing		= facing;
			this.posLn		= posLn;
			this.note		= note;
			this.uniqueId	= Util.getUniqueId(salesPerson);
			updateModifiedTime();
		}
		
		
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================
		
		
		public long getId() {
			return id;
		}


		
		
		 public void addPropertyChangeListener(PropertyChangeListener listener) {
	         this.pcs.addPropertyChangeListener(listener);
	     }

	     public void removePropertyChangeListener(PropertyChangeListener listener) {
	         this.pcs.removePropertyChangeListener(listener);
	     }
	     
	     
	     public long getSurveyId() {
			return surveyId;
		}
		
		public String getProductId() {
			return productId;
		}
		
		public String getPrezzo() {
			return prezzo;
		}
		public void setPrezzo(String prezzo) {
			String oldValue = this.prezzo;
			this.prezzo = prezzo;
	        this.pcs.firePropertyChange(COLUMN_PREZZO
	        		, oldValue, prezzo);
		}
		
		public int getVisibilty() {
			return visibility;
		}
		public void setVisibilty(int visibility) {
			int oldValue = this.visibility;
			this.visibility = visibility;
			this.pcs.firePropertyChange(COLUMN_VISIBILITY
					, oldValue, visibility);
		}
		
		public int getVolantino() {
			return volantino;
		}
		public void setVolantino(int volantino) {
			int oldValue = this.volantino;
			this.volantino = volantino;
			this.pcs.firePropertyChange(COLUMN_VOLANTINO
					, oldValue, volantino);
		}
		
		public String getTaglioPZ() {
			return taglioPZ;
		}
		public void setTaglioPZ(String taglioPZ) {
			String oldValue = this.taglioPZ;
			this.taglioPZ = taglioPZ;
			this.pcs.firePropertyChange(COLUMN_TAGLIO
					, oldValue, taglioPZ);
		}
		
		public int getFacing() {
			return facing;
		}
		public void setFacing(int facing) {			
			int oldValue = this.facing;
			this.facing = facing;
			this.pcs.firePropertyChange(COLUMN_FACING
					, oldValue, facing);
		}
		
		public int getLineaOne(){
			if(TextUtils.isEmpty(posLn))
				return 0;
			int slash = posLn.indexOf("/");
			try{
				String one = posLn.substring(0,slash);
				return Integer.parseInt(one);
			}
			catch(Exception e){
				return 0;
			}
			
		}
		
		public int getLineaTwo(){
			if(TextUtils.isEmpty(posLn))
				return 0;
			int slash = posLn.indexOf("/");
			try{
				String two = posLn.substring(slash+1,posLn.length());
				return Integer.parseInt(two);
			}
			catch(Exception e){
				return 0;
			}
		}
		
		
		
		public void setPrezzoLinea(String prezzoLinea) {
			if(TextUtils.isEmpty(prezzoLinea)
					||prezzoLinea.equals("/")
					||prezzoLinea.equals("0/0"))
				posLn = "";
			String oldValue = this.posLn;
			this.posLn = prezzoLinea;
			this.pcs.firePropertyChange(COLUMN_POS_LINEA
					, oldValue, posLn);
		}
		
		public String getNote() {
			return note;
		}
		public void setNote(String note) {
			String oldValue = this.note;
			this.note 		= note;
			this.pcs.firePropertyChange(COLUMN_NOTE
					, oldValue, note);
		}
		
		private void updateModifiedTime(){
			surveyModified = Util.getCurrentDateTime();
		}
		
		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================
		
		public void removeListener(){
			removePropertyChangeListener(changeListener);
		}
		
		public void updateBaseValues(){
			updateModifiedTime();
			values.put(TableSurveyCompetitor.COLUMN_SURVEY_ID, surveyId);
			values.put(TableSurveyCompetitor.COLUMN_PRODUCT_ID, productId);
			values.put(TableSurveyCompetitor.COLUMN_UNIQUE_FIELD, uniqueId);
			values.put(TableSurveyCompetitor.COLUMN_SURVEY_MODIFIED, surveyModified);
		}
		
		public void setBaseValues(long surveyId,String productId,String salesPerson){
			this.surveyId = surveyId;
			this.productId = productId;
			this.uniqueId  = Util.getUniqueId(salesPerson);
			updateModifiedTime();
		}
		
		
		public ContentValues getValues(){
			values.clear();
			values.put(TableSurveyCompetitor.COLUMN_SURVEY_ID, surveyId);
			values.put(TableSurveyCompetitor.COLUMN_PRODUCT_ID, productId);
			values.put(TableSurveyCompetitor.COLUMN_VISIBILITY, visibility);
			values.put(TableSurveyCompetitor.COLUMN_VOLANTINO, volantino);
			values.put(TableSurveyCompetitor.COLUMN_PREZZO, Util.getAsElahDBNumFormat(prezzo));
			values.put(TableSurveyCompetitor.COLUMN_TAGLIO, Util.getAsElahDBNumFormat(taglioPZ));
			values.put(TableSurveyCompetitor.COLUMN_FACING, facing);
			values.put(TableSurveyCompetitor.COLUMN_POS_LINEA, posLn);
			values.put(TableSurveyCompetitor.COLUMN_NOTE, note);
			values.put(TableSurveyCompetitor.COLUMN_UNIQUE_FIELD, uniqueId);
			surveyModified = Util.getCurrentDateTime();
			values.put(TableSurveyCompetitor.COLUMN_SURVEY_MODIFIED, surveyModified);
			return values;
		}
		
		public JSONObject getAsJSON() throws JSONException{
			JSONObject json = new JSONObject();
			json.put(Consts.JSON_KEY_ID, id);			
			json.put(COLUMN_SURVEY_ID,surveyId);			
			json.put(COLUMN_PRODUCT_ID, productId);			
			json.put(COLUMN_VISIBILITY,visibility);			
			json.put(COLUMN_VOLANTINO,volantino);
			json.put(COLUMN_PREZZO, formatStringToJSON(prezzo));			
			json.put(COLUMN_TAGLIO, formatStringToJSON(taglioPZ));			
			json.put(COLUMN_FACING, facing);			
			json.put(COLUMN_POS_LINEA, formatStringToJSON(posLn));			
			json.put(COLUMN_NOTE, formatStringToJSON(note));			
			json.put(COLUMN_UNIQUE_FIELD, uniqueId);			
			json.put(COLUMN_SURVEY_MODIFIED, surveyModified);
			
			return json;
		}
		
		
		public boolean isValid(){
			if(facing==0&&volantino==0&&visibility==0){
				if(TextUtils.isEmpty(posLn)||posLn.equals("0/0")){
					if(TextUtils.isEmpty(prezzo)){
						if(TextUtils.isEmpty(taglioPZ)){
							if(TextUtils.isEmpty(note)){
								return false;
							}
						}
					}
				}
			}
			return true;
		}
		
		

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
		
		PropertyChangeListener changeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				updateModifiedTime();
				values.clear();
				String propertyName = event.getPropertyName();
				if(propertyName.equals(COLUMN_FACING)){
					values.put(COLUMN_FACING, facing);
				}
				else if(propertyName.equals(COLUMN_NOTE)){
					values.put(COLUMN_NOTE, note);
				}
				else if(propertyName.equals(COLUMN_POS_LINEA)){
					values.put(COLUMN_POS_LINEA, posLn);
				}
				else if(propertyName.equals(COLUMN_PREZZO)){
					values.put(COLUMN_PREZZO, prezzo);
				}
				else if(propertyName.equals(COLUMN_TAGLIO)){
					values.put(COLUMN_TAGLIO, taglioPZ);
				}
				else if(propertyName.equals(COLUMN_VISIBILITY)){
					values.put(COLUMN_VISIBILITY, visibility);
				}
				else if(propertyName.equals(COLUMN_VOLANTINO)){
					values.put(COLUMN_VOLANTINO, volantino);
				}
				if(id==0||id==-1){
					updateBaseValues();
				}
				values.put(COLUMN_SURVEY_MODIFIED, surveyModified);
				insertOrUpdate(SurveyCompetitor.this, values);
			}
		};
	}
}
