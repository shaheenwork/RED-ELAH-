package com.eteam.dufour.database.tables;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eteam.db.DBTable;

public class TableSyncInfo extends DBTable{
	// ===========================================================
	// Constants
	// ===========================================================



	private static final String NOT_SYNCED = "Database not synced";
	
	public static final String TABLE_NAME = "syncdata";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SESSION = "session";
	public static final String COLUMN_DATE = "date";
	
	private static final String CREATE_TABLE = "create table if not exists "
			+ TABLE_NAME + "( " + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SESSION + " varchar, " + COLUMN_DATE + " varchar)";     
	
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

	public static final void createSession(Session session){
		
		String selection 	  = COLUMN_ID+"=?";
		String[] selectionArg = new String[]{"1"};
		SQLiteDatabase db = getDB();
		Cursor c = db.query(TABLE_NAME, 
				new String[]{COLUMN_ID}, selection,
				selectionArg, null, null, null);
		if(c.moveToFirst()){
			c.close();
			db.update(TABLE_NAME, session.getValues(), selection, selectionArg);
		}
		else{
			c.close();
			db.insert(TABLE_NAME, null, session.getValues());
		}
	}
	
//	public static final void updateSession(ElahDBHelper dbHelper,String session,String date){
//		ContentValues values = new ContentValues();
//  		values.put(TableSyncInfo.COLUMN_SESSION, session);
//  		values.put(TableSyncInfo.COLUMN_DATE, date);
//  		dbHelper.getWritableDatabase().update(TableSyncInfo.TABLE_NAME, values,TableSyncInfo.COLUMN_ID+"="+1,null);
//	}
	
	public static final String getSyncDate(){

		Cursor c = getDB().query(TABLE_NAME, 
				new String[]{COLUMN_DATE}, 
				null, null, null, null, null);
		String syncdate = NOT_SYNCED;
		if(c.moveToFirst()){
			syncdate = c.getString(c.getColumnIndex(COLUMN_DATE));
		}
		c.close();
		
		return syncdate;
	}
	
//	private static boolean doesOldSyncExist(){
//		Cursor c = getDB().query(TableSyncInfo.TABLE_NAME, 
//				new String[]{TableSyncInfo.COLUMN_ID}, null, null, null, null, null);
//		boolean status = c.moveToFirst();
//		c.close();
//		return status;
//	}
	
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class Session {
		// ===========================================================
		// Constants
		// ===========================================================
		private static ContentValues values = new ContentValues();
		
		private static final String KEY_SESSION = "session";
		private static final String KEY_DATE	= "date";
		// ===========================================================
		// Fields
		// ===========================================================
		
		protected String session;
		protected String date;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public Session(String session,String date) {
			this.session = session;
			this.date	 = date;
		}
		
		public Session(JSONObject json) throws JSONException{
			this.session = json.getString(KEY_SESSION);
			this.date	 = json.getString(KEY_DATE);
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public String getSession() {
			return session;
		}
		
		public String getDate() {
			return date;
		}
		
		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================
		
		// ===========================================================
		// Methods
		// ===========================================================
		public ContentValues getValues() {
			values.clear();
			
			values.put(COLUMN_SESSION, session);
			values.put(COLUMN_DATE, date);
			
			return values;
		}
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
