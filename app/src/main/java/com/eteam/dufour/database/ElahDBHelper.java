package com.eteam.dufour.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eteam.dufour.database.tables.TableCompetitors;
import com.eteam.dufour.database.tables.TableCustomers;
import com.eteam.dufour.database.tables.TableItems;
import com.eteam.dufour.database.tables.TableList;
import com.eteam.dufour.database.tables.TableLogin;
import com.eteam.dufour.database.tables.TablePromotions;
import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.dufour.database.tables.TableSurveyAssortimento;
import com.eteam.dufour.database.tables.TableSurveyCompetitor;
import com.eteam.dufour.database.tables.TableSurveyCustomers;
import com.eteam.dufour.database.tables.TableSurveyPromotions;
import com.eteam.dufour.database.tables.TableSurveyPromotionsItem;
import com.eteam.dufour.database.tables.TableTempSurveyAssortimento;
import com.eteam.utils.Consts;

import java.util.ArrayList;

public class ElahDBHelper extends SQLiteOpenHelper {

    // ===========================================================
    // Constants
    // ===========================================================
//	public static final String NAME_DB 	= "dufour.db";
    private static final int VERSION_DB = 5;
  //  private static final int VERSION_DB = 5;
// version 3 for test and verion 4 for production


    // ===========================================================
    // Fields
    // ===========================================================
    private static ElahDBHelper mInstance;
    private static SQLiteDatabase elahDB;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ElahDBHelper(Context context) {
        super(context, Consts.DB_NAME, null, VERSION_DB);
        // TODO Auto-generated constructor stub
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public static ElahDBHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information: 
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new ElahDBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub


    }

    //On DBVersion 2 MP and OP field where added to login table
    //Has to drop the login table and create new one on update

    //On DBVersion 3 taglioPrezzo fields are changed from free number to combo box.
    //So update existing draft values to combo values and tobesent to oldtobesent
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Log", "Updating");
        if (oldVersion < 2) {
            if (doesTableExist(db, TableLogin.TABLE_NAME)) {
                db.execSQL("DROP TABLE " + TableLogin.TABLE_NAME);
            }
        }
        if (oldVersion < 3) {
            TableSurvey.convertToOldSurveys(db);
            TableSurveyPromotionsItem.convertDraftsTaglioToCombo(db);
        }

        //by bibin update the version code .
        // on test build please comment below checking ( if (oldVersion < 4) ) on production uncomment it
        if (oldVersion < 4) {
            if (doesTableExist(db, TableSurvey.TABLE_NAME)) {
                //db.execSQL("DROP TABLE " + TableSurvey.TABLE_NAME);
               // db.execSQL(TableCustomers.CREATE_TABLE);
                String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "
                        + TableSurvey.TABLE_NAME + " ADD COLUMN " + TableSurvey.COLUMN_SALESPERSON_NAME + " string;";
                String DATABASE_ALTER_TEAM_2 = "ALTER TABLE "
                        + TableSurvey.TABLE_NAME + " ADD COLUMN " + TableSurvey.COLUMN_SURVEY_CLUSTER + " string;";
                String DATABASE_ALTER_TEAM_3 = "ALTER TABLE "
                        + TableSurvey.TABLE_NAME + " ADD COLUMN " + TableSurvey.COLUMN_SURVEY_CLUSTER_STATUS + " int;";
                //  db.execSQL("DROP TABLE " + TableSurvey.TABLE_NAME);
                db.execSQL(DATABASE_ALTER_TEAM_1);
                db.execSQL(DATABASE_ALTER_TEAM_2);
                 db.execSQL(DATABASE_ALTER_TEAM_3);
            } else {
                db.execSQL(TableCustomers.CREATE_TABLE);
            }
            if (doesTableExist(db, TableCustomers.TABLE_NAME)) {
                String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "
                        + TableCustomers.TABLE_NAME + " ADD COLUMN " + TableCustomers.COLUMN_CL + " string;";
                db.execSQL(DATABASE_ALTER_TEAM_1);
            }
        }

        if (oldVersion<5){
            if (doesTableExist(db, TableSurvey.TABLE_NAME)) {
                String DATABASE_ALTER_TEAM_4 = "ALTER TABLE "
                        + TableSurvey.TABLE_NAME + " ADD COLUMN " + TableSurvey.COLUMN_SURVEY_CLUSTER2 + " string;";
                String DATABASE_ALTER_TEAM_5 = "ALTER TABLE "
                        + TableSurvey.TABLE_NAME + " ADD COLUMN " + TableSurvey.COLUMN_SURVEY_CLUSTER3 + " string;";


                db.execSQL(DATABASE_ALTER_TEAM_4);
                db.execSQL(DATABASE_ALTER_TEAM_5);
            }
            else {
                db.execSQL(TableSurvey.CREATE_TABLE);
            }
        }

       /* if (oldVersion<5){
            if (doesTableExist(db, TableItems.TABLE_NAME)) {
                String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "
                        + TableItems.TABLE_NAME + " ADD COLUMN " + TableItems.COLOUMN_EAN + " string;";
                db.execSQL(DATABASE_ALTER_TEAM_1);
            }
        }*/

    }

    @Override
    public synchronized void close() {
        // TODO Auto-generated method stub
        super.close();
        if (elahDB != null) {
            elahDB.close();
            elahDB = null;
        }
    }


    // ===========================================================
    // Methods
    // ===========================================================
    public static boolean doesTableExist(ElahDBHelper dbHelper, String tblName) {
        Cursor c = null;
        try {
            c = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + tblName, null);
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if (c != null)
                c.close();
        }
    }

    public static boolean doesTableExist(SQLiteDatabase xdb, String tblName) {
        Cursor c = null;
        try {
            c = xdb.rawQuery("SELECT * FROM " + tblName, null);
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if (c != null)
                c.close();
        }
    }

    public SQLiteDatabase getElahWriteAbleDB() {
        if ((elahDB == null) || !elahDB.isOpen()) {
            elahDB = getWritableDatabase();
        }
        return elahDB;
    }

    public static final void dropTable(ElahDBHelper dbHelper, String tblName) {
        dbHelper.getWritableDatabase().execSQL("drop table " + tblName);
    }

    public static final void createAllTables(ElahDBHelper dbHelper) {
        dbHelper.getWritableDatabase().execSQL(TableLogin.CREATE_TABLE);
        dbHelper.getWritableDatabase().execSQL(TableSurveyAssortimento.CREATE_TABLE);
        dbHelper.getWritableDatabase().execSQL(TableTempSurveyAssortimento.CREATE_TABLE); // temp table for dashboard
        dbHelper.getWritableDatabase().execSQL(TableSurveyPromotions.CREATE_TABLE);
        dbHelper.getWritableDatabase().execSQL(TableSurvey.CREATE_TABLE);
        dbHelper.getWritableDatabase().execSQL(TableSurveyCompetitor.CREATE_TABLE);
        dbHelper.getWritableDatabase().execSQL(TableSurveyPromotionsItem.CREATE_TABLE);
    }

    public static final void createAllDataTables(ElahDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(TableList.CREATE_TABLE);
        db.execSQL(TableCustomers.CREATE_TABLE);
        db.execSQL(TableSurveyCustomers.CREATE_TABLE);
        db.execSQL(TablePromotions.CREATE_TABLE);
        db.execSQL(TableItems.CREATE_TABLE);
        db.execSQL(TableCompetitors.CREATE_TABLE);
    }

    public static final void dropAllDataTables(ElahDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TableList.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableCustomers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableSurveyCustomers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TablePromotions.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableItems.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableCompetitors.TABLE_NAME);
    }

//    public static final void deleteAllSurveyTables(ElahDBHelper dbHelper){
//    	dbHelper.getWritableDatabase().execSQL("drop table "+TableSurveyAssortimento.TABLE_NAME);
//    	dbHelper.getWritableDatabase().execSQL("drop table "+TableSurveyPromotions.TABLE_NAME);
//    	dbHelper.getWritableDatabase().execSQL("drop table "+TableSurvey.TABLE_NAME);
//    	dbHelper.getWritableDatabase().execSQL("drop table "+TableSurveyCompetitor.TABLE_NAME);
//    	dbHelper.getWritableDatabase().execSQL("drop table "+TableSurveyPromotionsItem.TABLE_NAME);
//    }

    public static final void closeConnections() {
        Log.d("Log", "Closing connections");
        if (mInstance != null) {
            mInstance.close();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================


    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}
