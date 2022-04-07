package com.eteam.dufour.database.tables;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.utils.Util;

public class TableLogin extends DBTable{
	// ===========================================================
	// Constants
	// ===========================================================

	

	public static final int SYNC_COMPLETED = 1;
	public static final int SYNC_INCOMPLETE = 0;
	
	
	public static final String TABLE_NAME = "login";
	
	public static final String COLUMN_ID 			= "_id";
	public static final String COLUMN_ERROR_CODE 	= "errorcode";
	public static final String COLUMN_SESSION 		= "session";
	public static final String COLUMN_NAME 			= "name";
	public static final String COLUMN_CODE 			= "code";
	public static final String COLUMN_USERNAME 		= "username";
	public static final String COLUMN_PASSWORD 		= "password";
	public static final String COLUMN_DATE 			= "date";
	public static final String COLUMN_SURNAME 		= "surname";
	public static final String COLUMN_SYNC_STAT 	= "syncstat";
	public static final String COLUMN_MP 			= "MP";
	public static final String COLUMN_OP 			= "OP";
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
											+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
												+COLUMN_ERROR_CODE+" INTEGER, "
												+COLUMN_SESSION+" TEXT, "
												+COLUMN_NAME+" text, "	
												+COLUMN_CODE+" TEXT, "
												+COLUMN_USERNAME+" TEXT, "
												+COLUMN_PASSWORD+" TEXT, "
												+COLUMN_DATE+" INTEGER, "
												+COLUMN_SURNAME+" TEXT,"
												+COLUMN_SYNC_STAT+" INTEGER, "
												+COLUMN_MP+" INTEGER, " 
												+COLUMN_OP+" INTEGER)";
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
	
	/*****************************************************
	 * Inserts the user info to the login table, 
	 * --RULE ONLY ONE LOGIN IS PERMITTED PER ROOM---
	 * ***************************************************/
	public static void addLogin(UserInfo user){
		SQLiteDatabase db = getDB();
		
		String 	 selection	  = TableLogin.COLUMN_ID+"= ?";
		String[] selectionArg = new String[]{"1"}; 
		
		Cursor c = db.query(TableLogin.TABLE_NAME, 
				new String[]{TableLogin.COLUMN_ID},
				selection, selectionArg
				, null, null, null);
		if(c.moveToFirst()){
			c.close();
			
			selection 	 = TableLogin.COLUMN_ID+"= ? AND "+TableLogin.COLUMN_USERNAME+"= ?";
			selectionArg = new String[]{"1",user.credentials.username}; 
			
			c = db.query(TableLogin.TABLE_NAME, 
					new String[]{TableLogin.COLUMN_ID},
					selection, selectionArg
					, null, null, null);
			boolean includeSyncStat = false;
			if(!c.moveToFirst()){
				user.setSyncStatus(SYNC_INCOMPLETE);
				includeSyncStat = true;
			}
			c.close();
						
			db.update(TABLE_NAME, user.getValues(includeSyncStat)
					,TableLogin.COLUMN_ID+"= ?" , new String[]{"1"});
			
			
		}
		else{
			c.close();
			db.insert(TABLE_NAME, null, user.getValues(true));
		}
	}
	
//	public static void insertLogin(ElahDBHelper dbHelper,int errorCode,String session
//			,String name, String sur_name,String code,String username,String password
//			,long date,int sync_status){
//		ContentValues values = new ContentValues();
//		
//		values.put(TableLogin.COLUMN_SESSION, session);
//		values.put(TableLogin.COLUMN_ERROR_CODE, errorCode);
//		values.put(TableLogin.COLUMN_SESSION, session);
//		values.put(TableLogin.COLUMN_NAME, name);
//		values.put(TableLogin.COLUMN_SURNAME, sur_name);
//		values.put(TableLogin.COLUMN_CODE, code);
//		values.put(TableLogin.COLUMN_USERNAME, username);
//		values.put(TableLogin.COLUMN_PASSWORD, password);
//		values.put(TableLogin.COLUMN_DATE, date);
//		values.put(TableLogin.COLUMN_SYNC_STAT,sync_status);
//		
//		dbHelper.getWritableDatabase().insert(TableLogin.TABLE_NAME, null, values);
//	}
	
//	public static void updateLogin(ElahDBHelper dbHelper,int id,int errorCode,String session
//			,String name, String sur_name,String code,String username,String password
//			,long date,int sync_status){
//		ContentValues values = new ContentValues();
//		
//		values.put(TableLogin.COLUMN_SESSION, session);
//		values.put(TableLogin.COLUMN_ERROR_CODE, errorCode);
//		values.put(TableLogin.COLUMN_SESSION, session);
//		values.put(TableLogin.COLUMN_NAME, name);
//		values.put(TableLogin.COLUMN_SURNAME, sur_name);
//		values.put(TableLogin.COLUMN_CODE, code);
//		values.put(TableLogin.COLUMN_USERNAME, username);
//		values.put(TableLogin.COLUMN_PASSWORD, password);
//		values.put(TableLogin.COLUMN_DATE, date);
//		if(sync_status!=SYNC_UNCHANGED)
//			values.put(TableLogin.COLUMN_SYNC_STAT, sync_status);
//		
//		dbHelper.getWritableDatabase().update(TableLogin.TABLE_NAME, values, TableLogin.COLUMN_ID+"="+id,null);
//	}
	
//	private static boolean doesLoginExist(ElahDBHelper dbHelper,String username){
//		Cursor c = dbHelper.getReadableDatabase().query(TableLogin.TABLE_NAME, 
//				new String[]{TableLogin.COLUMN_ID}, TableLogin.COLUMN_USERNAME+"=?", new String[]{username}
//					, null, null, null);
//		
//		boolean userExists = c.moveToFirst();
//		
//		c.close();
//		return userExists;
//	}
//	
//	private static boolean doesLoginExist(ElahDBHelper dbHelper){
//		Cursor c = dbHelper.getReadableDatabase().query(TableLogin.TABLE_NAME, 
//				new String[]{TableLogin.COLUMN_ID}, null, null
//					, null, null, null);
//		boolean status = false;
//		if(c.moveToFirst()){
//			status = true;
//		}
//		c.close();
//		return status;
//	}
	
	public static boolean isOfflineCredentialsValid(String userName,String password){
		Cursor c = getDB().query(TableLogin.TABLE_NAME, new String[]{TableLogin.COLUMN_ID}
				, TableLogin.COLUMN_USERNAME+"= ? AND "
			    +TableLogin.COLUMN_PASSWORD+"= ?"
			    , new String[]{userName,password}, null, null, null);
		boolean valid = c.moveToFirst();
		c.close();
		return valid;
	}
	
	public static boolean isSyncCompleted(String userName){
		return (getSyncStatus(userName)==SYNC_COMPLETED);
	}
	
	public static int getSyncStatus(String userName){
		int status = 0;
		Cursor c = getDB().query(TABLE_NAME, new String[]{COLUMN_SYNC_STAT}
								, COLUMN_USERNAME+"= ?"
								, new String[]{userName}, null, null, null);
		
		if(c.moveToFirst())
			status = c.getInt(c.getColumnIndex(COLUMN_SYNC_STAT));
		
		c.close();
		return status;
		
	}
	
	public static void setSyncStatus(String username,int status){
		ContentValues values = new ContentValues();
		values.put(TableLogin.COLUMN_SYNC_STAT, status);
		getDB().update(TableLogin.TABLE_NAME, values
				, TableLogin.COLUMN_USERNAME+"= ?"
				, new String[]{username});
	}
	
	public static void resetSyncStatus(SQLiteDatabase xdb){
		ContentValues values = new ContentValues();
		values.put(TableLogin.COLUMN_SYNC_STAT, SYNC_INCOMPLETE);
		xdb.update(TableLogin.TABLE_NAME, values, null, null);
	}
	
	public static String getSessionId(ElahDBHelper dbHelper,String username){
		String session =null;
		Cursor c = dbHelper.getReadableDatabase().query(TableLogin.TABLE_NAME, new String[]{TableLogin.COLUMN_SESSION}, 
				TableLogin.COLUMN_USERNAME+"='"+username+"'", null, null, null, null);
		
		if(c.moveToFirst());
			session = c.getString(c.getColumnIndex(TableLogin.COLUMN_SESSION));
		c.close();
		
		return session;
	}
	
	public static String getSalesPersonCode(String username){
		String code =null;
		Cursor c = getDB().query(TableLogin.TABLE_NAME, new String[]{TableLogin.COLUMN_CODE}, 
				TableLogin.COLUMN_USERNAME+"='"+username+"'", null, null, null, null);
		
		if(c.moveToFirst())
			code = c.getString(c.getColumnIndex(TableLogin.COLUMN_CODE));
		c.close();
		
		return code;
	}
	
	public static String getPassword(String username){
		String password =null;
		Cursor c = getDB().query(TableLogin.TABLE_NAME
				, new String[]{TableLogin.COLUMN_PASSWORD}
				, COLUMN_USERNAME+"= ?"
				, new String[]{username}, null, null, null);
		
		if(c.moveToFirst())
			password = c.getString(c.getColumnIndex(COLUMN_PASSWORD));
		c.close();
		
		return password;
	}
	
	public static String getCompleteName(String username){
		Cursor c = getDB().query(TableLogin.TABLE_NAME,
				new String[]{TableLogin.COLUMN_NAME,TableLogin.COLUMN_SURNAME },
				TableLogin.COLUMN_USERNAME+"= ?", new String[]{username}, null, null, null);
		String name = "";
		
		if(c.moveToFirst()){
			name = c.getString(c.getColumnIndex(TableLogin.COLUMN_NAME))
				+" "+c.getString(c.getColumnIndex(TableLogin.COLUMN_SURNAME));
		}
		c.close();
		
		return name;
	}
	public static String getCompleteNameWithCode(String code){
		Cursor c = getDB().query(TableLogin.TABLE_NAME,
				new String[]{TableLogin.COLUMN_NAME,TableLogin.COLUMN_SURNAME },
				TableLogin.COLUMN_CODE+"= ?", new String[]{code}, null, null, null);
		String name = "";
		
		if(c.moveToFirst()){
			name = c.getString(c.getColumnIndex(TableLogin.COLUMN_SURNAME))
				+" "+c.getString(c.getColumnIndex(TableLogin.COLUMN_NAME));
		}
		c.close();
		
		return name;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class UserInfo {
		// ===========================================================
		// Constants
		// ===========================================================
		private static final ContentValues values = new ContentValues();
		
		private static final String KEY_SESSION = "session";
		private static final String KEY_NAME	= "name";
		private static final String KEY_SURNAME = "sur_name";
		private static final String KEY_CODE	= "code";
		
		// ===========================================================
		// Fields
		// ===========================================================
		private int 	errorCode;
		private String 	session;
		private String  name;
		private String 	sur_name;
		private String 	salesPersonCode;
		private long 	date;
		private int 	sync_status;
		
		private Credentials credentials;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public UserInfo(Cursor c) {
			errorCode 	= c.getInt(c.getColumnIndex(COLUMN_ERROR_CODE));
			session		= c.getString(c.getColumnIndex(COLUMN_SESSION));
			name		= c.getString(c.getColumnIndex(COLUMN_NAME));
			sur_name	= c.getString(c.getColumnIndex(COLUMN_SURNAME));
			salesPersonCode		= c.getString(c.getColumnIndex(COLUMN_CODE));
			
			date		= c.getLong(c.getColumnIndex(COLUMN_DATE));
			sync_status = c.getInt(c.getColumnIndex(COLUMN_SYNC_STAT));
			
			credentials = new Credentials(c);
		}
		
		public UserInfo(JSONObject userInfo,Credentials credentials) 
				throws NumberFormatException, JSONException{
			this(userInfo, credentials,
					Long.parseLong(Util.getCurrentTimeInMilliSeconds()));
			
		}
		
		public UserInfo(JSONObject userInfo,Credentials credentials,long time) 
				throws JSONException{
			this.errorCode = 0;
			this.session   = userInfo.getString(KEY_SESSION);
			this.name	   = userInfo.getString(KEY_NAME);
			this.sur_name  = userInfo.getString(KEY_SURNAME);
			this.salesPersonCode   = userInfo.getString(KEY_CODE);
			this.date	   = time;
			this.credentials = credentials;
			this.sync_status = SYNC_INCOMPLETE;
		}
		

		public UserInfo(int errorCode,String session
				,String name, String sur_name
				,String code,String username
				,String password,long date
				,int sync_status){
			this.errorCode 	 = errorCode;
			this.session 	 = session;
			this.name		 = name;
			this.sur_name	 = sur_name;
			this.salesPersonCode		 = code;
			this.credentials = new Credentials(username,password);
			this.date		 = date;
			this.sync_status = sync_status;
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================
		public void setSyncStatus(int syncStatus) {
			this.sync_status = syncStatus;
			
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
		
		public ContentValues getValues(boolean includeSyncStatus) {
			values.clear();
			
			values.put(TableLogin.COLUMN_SESSION, session);
			values.put(TableLogin.COLUMN_ERROR_CODE, errorCode);
			values.put(TableLogin.COLUMN_SESSION, session);
			values.put(TableLogin.COLUMN_NAME, name);
			values.put(TableLogin.COLUMN_SURNAME, sur_name);
			values.put(TableLogin.COLUMN_CODE, salesPersonCode);
			values.put(TableLogin.COLUMN_USERNAME, credentials.username);
			values.put(TableLogin.COLUMN_PASSWORD, credentials.password);
			values.put(TableLogin.COLUMN_DATE, date);
			if(includeSyncStatus)
				values.put(TableLogin.COLUMN_SYNC_STAT,sync_status);
			
			return values;
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
	
	public static class Credentials {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================
		private String 	username;
		private String 	password;
		// ===========================================================
		// Constructors
		// ===========================================================
		public Credentials(Cursor c) {
			username	= c.getString(c.getColumnIndex(COLUMN_USERNAME));
			password	= c.getString(c.getColumnIndex(COLUMN_PASSWORD));
		}
		
		public Credentials(String username,String password) {
			this.username = username;
			this.password = password;
		}
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public String getUsername() {
			return username;
		}
		
		public String getPassword() {
			return password;
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
}
