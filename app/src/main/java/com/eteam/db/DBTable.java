package com.eteam.db;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DBTable {
	
	// ===========================================================
	// Constants
	// ===========================================================
	private static SQLiteDatabase db;
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public DBTable() {
		
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	protected static SQLiteDatabase getDB(){
		if(db==null)
			db = DBApp.getDB();
		return db;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	// ===========================================================
	// Methods
	// ===========================================================
	public static String formatStringToJSON(String value){
		
		return (TextUtils.isEmpty(value))?"":value;
		
	}
	
	public static void createOrUpdate(DBModel dbModel,String table,String[] projection,
			String selection,String[] selectionArgs){
		Cursor c = getDB().query(table, projection, selection, selectionArgs, null, null, null);
		if(c.moveToFirst()){
			c.close();
			getDB().update(table, dbModel.getContentValues(), selection, selectionArgs);
		}
		else{
			c.close();
			getDB().insert(table, null, dbModel.getContentValues());
		}
	}

	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public abstract static class DBModel {
		// ===========================================================
		// Constants
		// ===========================================================
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
		public abstract ContentValues getContentValues();
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}


}
