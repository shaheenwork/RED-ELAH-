package com.eteam.dufour.database.tables;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.eteam.db.DBTable;
import com.eteam.utils.Consts;

public class TableList extends DBTable{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = TableList.class.getName();
	
	public static final String TABLE_NAME = "tablelist";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TOTAL_PAGES = "totalpages";
	public static final String COLUMN_STARTING_DATE = "starting_date";
	public static final String COLUMN_TOTAL_RECORDS = "totalrecords";
	public static final String COLUMN_TABLE_ID = "tableid";
	
	
	
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + "( " 
												  + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
												  +COLUMN_TOTAL_PAGES+" TEXT, "
												  +COLUMN_STARTING_DATE+" TEXT, "
												  +COLUMN_TOTAL_RECORDS+" TEXT, "
												  +COLUMN_TABLE_ID+" TEXT)";     	
	
	
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
	
	public static final ArrayList<Table> getAllTableInfo(){
		String[] selection = new String[] {COLUMN_TABLE_ID,COLUMN_TOTAL_PAGES};
		Cursor c = getDB().query(TABLE_NAME,selection,null,null,null,null,null);
		ArrayList<Table> list = new ArrayList<Table>();
		if(c.moveToFirst()){
			while(!c.isAfterLast()){
				list.add(new Table(c));
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}
	
	public static void createAndPopulateTableList(JSONArray tableListArray) throws JSONException, SQLDataException {
		// TODO Auto-generated method stub
//   	     if(ElahDBHelper.doesTableExist(dbHelper, TableList.TABLE_NAME))
//   	    	ElahDBHelper.dropTable(dbHelper, TableList.TABLE_NAME);
		
   	     if(tableListArray.length()>1){
    		 SQLiteStatement insert = null;
   	    	 for(int i=0;i<tableListArray.length();i++){
   	    		 JSONObject tableStruct = tableListArray.getJSONObject(i);
   	    		 Iterator<?> iterator;
   	    		 String  tablelist_keys = "";
   	    		 String paramts = "";
	    		 ArrayList<String> type = new ArrayList<String>();
   	    		 boolean first = true;
   	    		 int index = 1;
   	    		 if(i==0){
   	    			 iterator = tableStruct.keys();
   	    			 while(iterator.hasNext()){
   	    				String key = String.valueOf(iterator.next());
   	   	          		//Log.d("Project","Key values = "+key);
   	   	          			if(first) {
   	   	          				tablelist_keys += key;
   	   	          				paramts += "?";
   	   	          				first = false;
   	   	          				type.add("VARCHAR");
   	   	          			}  else {
   	   	          				tablelist_keys += ","+key;
   	   	          				paramts += ",?"; 
   	   	          				type.add("VARCHAR");
   	   	                	     
   	   	          			}
   	   	          			
   	   	          		}
//   	    			 	dbHelper.getWritableDatabase().execSQL("create table if not exists "+TableList.TABLE_NAME+"("
//  	          					+TableList.COLUMN_ID+" integer primary key autoincrement, "
//  	          					+tablelist_keys+" "+type+")");
  	    				insert = getDB().compileStatement("INSERT INTO "+TABLE_NAME+" ("+tablelist_keys+") "  + "VALUES("+paramts+")");
   	    		}
   	    		iterator = tableStruct.keys(); 
   	    		while(iterator.hasNext()){
   	    			String key = String.valueOf(iterator.next());
   	          		insert.bindString(index, tableStruct.getString(key));
   	          		index++; 
   	    		}
   	    		if(insert.executeInsert()==-1){
   	    			throw new SQLDataException("Unable to insert data") ;
   	    		}
   	    	 }
   	     }
   	     else{
   	    	 Log.e(TAG, "No table list arrays in JSON");
   	     }
	}
	
	public static long getTotalPages(){
		Cursor c = getDB().rawQuery("SELECT SUM ("+COLUMN_TOTAL_PAGES+") FROM "
				+ TABLE_NAME, null);
		long count = 0;
		
		if(c.moveToFirst()){
			count = c.getLong(0);
			
		}
		c.close();
		
		return count;
	}
	
	public static boolean isAllRecordsPresentForTable(String tableName){
		SQLiteDatabase db = getDB();
		Cursor c = db.rawQuery("SELECT COUNT(*) FROM "+tableName,null);
		int recordsInDB = 0;
		if(c.moveToFirst()){
			recordsInDB = c.getInt(0);
		}
		c.close();
		c = db.query(TableList.TABLE_NAME, new String[]{TableList.COLUMN_TOTAL_RECORDS}
		, TableList.COLUMN_TABLE_ID+"= ? ",new String[]{tableName}, null, null, null);
		
		int recordsInServer = 0;
		if(c.moveToFirst()){
			recordsInServer = c.getInt(c.getColumnIndex(TableList.COLUMN_TOTAL_RECORDS));
			
		}
		c.close();
		
		if(Consts.DEBUGGABLE){
			Log.d(TAG, "Records in database "+recordsInDB);
			Log.d(TAG, "Records from server "+recordsInServer);
		}
		
		return recordsInDB==recordsInServer;
	}
	

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class Table {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================
		private String tableName;
		private int    totalPages;
		// ===========================================================
		// Constructors
		// ===========================================================
		public Table(Cursor c) {
			tableName  = c.getString(c.getColumnIndex(COLUMN_TABLE_ID));
			totalPages = c.getInt(c.getColumnIndex(COLUMN_TOTAL_PAGES));
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================
		public String getTableName() {
			return tableName;
		}
		public int getTotalPages() {
			return totalPages;
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
