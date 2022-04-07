package com.eteam.dufour.database.tables;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.tables.TableCustomers.Customer;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class TableSurvey extends DBTable {


    // ===========================================================
    // Constants
    // ===========================================================
    private static final String TAG = TableSurvey.class.getName();

    public static final String TABLE_NAME = "survey";

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_SURVEY_DATE = "Survey_Date";
    public static final String COLUMN_CUSTOMER_CODE = "Customer_Code";
    public static final String COLUMN_SALESPERSON_CODE = "Salesperson_Code";
    public static final String COLUMN_SALESPERSON_NAME = "Salesperson_Name";
    public static final String COLUMN_SURVEY_POST_STATUS = "Survey_Post_Status";
    public static final String COLUMN_SURVEY_SYNC_DATE = "Survey_Sync_Date";
    public static final String COLUMN_SURVEY_SENT_DATE = "Survey_Sent_Date";
    public static final String COLUMN_SURVEY_CLUSTER = "Survey_Cluster";
    public static final String COLUMN_SURVEY_CLUSTER2 = "Clustre_Caramelle";
    public static final String COLUMN_SURVEY_CLUSTER3 = "Cluster_Dessert";
    public static final String COLUMN_SURVEY_CLUSTER_STATUS = "Survey_Cluster_STATUS";
    public static final String COLUMN_UNIQUE_ID = "Unique_Field";
    public static final String COLUMN_FLAG = "Flag";


    //Flag Values
    public static final int FLAG_NEW_SURVEY = 0;
    public static final int FLAG_DRAFTS = 1;
    public static final int FLAG_TO_BE_SENT = 2;
    public static final int FLAG_FROM_SERVER = 3;
    public static final int FLAG_TO_BE_SENT_OLD = 4;

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SURVEY_DATE + " TEXT, "
            + COLUMN_CUSTOMER_CODE + " TEXT, "
            + COLUMN_SALESPERSON_CODE + " TEXT, "
            + COLUMN_SALESPERSON_NAME + " TEXT, "
            + COLUMN_SURVEY_POST_STATUS + " TEXT, "
            + COLUMN_SURVEY_SYNC_DATE + " TEXT, "
            + COLUMN_SURVEY_SENT_DATE + " TEXT, "
            + COLUMN_SURVEY_CLUSTER + " INTEGER, "
            + COLUMN_SURVEY_CLUSTER2 + " INTEGER, "
            + COLUMN_SURVEY_CLUSTER3 + " INTEGER, "
            + COLUMN_SURVEY_CLUSTER_STATUS + " INTEGER, "
            + COLUMN_FLAG + " INTEGER, "
            + COLUMN_UNIQUE_ID + " TEXT)";
    // ===========================================================
    // Fields
    // ===========================================================

    /*********************************************************************************
     * Create a new survey in table.
     * If any survey associated with the customer with flag other than
     * {@link TableSurvey#FLAG_TO_BE_SENT} or {@link TableSurvey#FLAG_TO_BE_SENT_OLD}
     * exists, it will be deleted
     * *******************************************************************************/
    public static final long createNewSurvey(Survey survey) {
        deleteInvalidSurveys(survey.customerCode);
        return getDB().insert(TABLE_NAME, null, survey.getValues());
    }

    public static final long updateSurvey(Survey survey) {
        return getDB().update(TABLE_NAME, survey.getValues()
                , TableSurvey.COLUMN_ID + "= ?"
                , new String[]{String.valueOf(survey.getId())});
    }


    //Deletes all survey relating to a pdv other than the surveys that are to be sent
    //** RULE - ONLY ONE NEW SURVEY CAN BE ASSOCIATED WITH A CUSTOMER **
    private static final void deleteInvalidSurveys(String customerCode) {

        String selection = COLUMN_CUSTOMER_CODE + "= ?" + " AND " + COLUMN_FLAG + "!= ?";
        String[] selectionArg = new String[]{customerCode, String.valueOf(FLAG_TO_BE_SENT)};
        Cursor c = getDB().query(TABLE_NAME, new String[]{TableSurvey.COLUMN_ID},
                selection, selectionArg, null, null, null);

        String idToDelete = "";
        if (c.moveToFirst()) {
            idToDelete = "";
            int indexId = c.getColumnIndex(TableSurvey.COLUMN_ID);
            while (!c.isAfterLast()) {
                idToDelete = idToDelete + c.getInt(indexId) + ",";
                c.moveToNext();
            }
        }
        c.close();
        if (!TextUtils.isEmpty(idToDelete)) {
            if (idToDelete.endsWith(",")) {
                idToDelete = idToDelete.substring(0, idToDelete.length() - 1);
            }
            delete(idToDelete);
        }
        TableSurveyPromotionsItem.deleteCorruptIds();
    }


    public static final void deleteCorruptedData() {
        Cursor c = getDB().query(TableSurvey.TABLE_NAME,
                new String[]{TableSurvey.COLUMN_ID}, TableSurvey.COLUMN_FLAG + "= ? "
                        + " OR " + TableSurvey.COLUMN_FLAG + "= ?",
                new String[]{String.valueOf(FLAG_NEW_SURVEY)
                        , String.valueOf(FLAG_FROM_SERVER)}, null, null, null);
        String idToDelete = "";
        if (c.moveToFirst()) {
            idToDelete = "";
            while (!c.isAfterLast()) {
                idToDelete = idToDelete + c.getInt(c.getColumnIndex(TableSurvey.COLUMN_ID)) + ",";
                c.moveToNext();
            }

        }
        c.close();
        if (!TextUtils.isEmpty(idToDelete)) {
            if (idToDelete.endsWith(",")) {
                idToDelete = idToDelete.substring(0, idToDelete.length() - 1);
            }
            delete(idToDelete);
        }
        TableSurveyPromotionsItem.deleteCorruptIds();
    }

    public static final Survey getSurvey(long surveyId) {
        Cursor c = getDB().query(TableSurvey.TABLE_NAME, new String[]{COLUMN_SURVEY_SENT_DATE, COLUMN_SURVEY_SYNC_DATE
                        , COLUMN_CUSTOMER_CODE, COLUMN_SALESPERSON_CODE, COLUMN_SALESPERSON_NAME, COLUMN_SURVEY_POST_STATUS, COLUMN_SURVEY_DATE, COLUMN_SURVEY_CLUSTER,COLUMN_SURVEY_CLUSTER2,COLUMN_SURVEY_CLUSTER3, COLUMN_SURVEY_CLUSTER_STATUS, COLUMN_ID
                        , COLUMN_FLAG, COLUMN_UNIQUE_ID},
                TableSurvey.COLUMN_ID + "= ?",
                new String[]{String.valueOf(surveyId)}, null, null, null);
        Survey survey = null;

        if (c.moveToFirst()) {
            survey = new Survey(c);
        }

        c.close();
        return survey;
    }

    public static ArrayList<Survey> getToBeSentSurveys() {
        ArrayList<Survey> list = new ArrayList<Survey>();
        Cursor c = getDB().query(TABLE_NAME,
                new String[]{COLUMN_SURVEY_SENT_DATE
                        , COLUMN_CUSTOMER_CODE
                        , COLUMN_SALESPERSON_CODE
                        , COLUMN_SALESPERSON_NAME
                        , COLUMN_SURVEY_SYNC_DATE
                        , COLUMN_SURVEY_POST_STATUS
                        , COLUMN_SURVEY_DATE
                        , COLUMN_FLAG
                        , COLUMN_SURVEY_CLUSTER
                        , COLUMN_SURVEY_CLUSTER2
                        , COLUMN_SURVEY_CLUSTER3
                        , COLUMN_SURVEY_CLUSTER_STATUS
                        , COLUMN_ID
                        , COLUMN_UNIQUE_ID},
                TableSurvey.COLUMN_FLAG + " IN (?,?)",
                new String[]{String.valueOf(TableSurvey.FLAG_TO_BE_SENT)
                        , String.valueOf(TableSurvey.FLAG_TO_BE_SENT_OLD)},
                null, null,
                TableSurvey.COLUMN_ID + " ASC");
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                list.add(new Survey(c));
                c.moveToNext();
            }
        }
        c.close();
        return list;
    }

    public static ArrayList<BaseSurvey> getToBeSentBaseSurveys() {
        ArrayList<BaseSurvey> list = new ArrayList<BaseSurvey>();
        Cursor c = getDB().query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_SURVEY_SENT_DATE, COLUMN_FLAG},
                COLUMN_FLAG + " IN (?,?)",
                new String[]{String.valueOf(TableSurvey.FLAG_TO_BE_SENT)
                        , String.valueOf(TableSurvey.FLAG_TO_BE_SENT_OLD)},
                null, null,
                COLUMN_ID + " ASC");
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                list.add(new BaseSurvey(c));
                c.moveToNext();
            }
        }
        c.close();
        return list;
    }

    public static JSONObject getSurveyAsJSON(long surveyId, boolean isNewSurvey) throws JSONException {

        Cursor c = getDB().query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_SURVEY_DATE,
                        COLUMN_CUSTOMER_CODE, COLUMN_SALESPERSON_CODE, COLUMN_SALESPERSON_NAME
                        , COLUMN_SURVEY_POST_STATUS, COLUMN_SURVEY_SYNC_DATE
                        , COLUMN_SURVEY_SENT_DATE, COLUMN_SURVEY_CLUSTER,COLUMN_SURVEY_CLUSTER2,COLUMN_SURVEY_CLUSTER3, COLUMN_SURVEY_CLUSTER_STATUS, COLUMN_UNIQUE_ID, COLUMN_FLAG},
                TableSurvey.COLUMN_ID + "= ?", new String[]{String.valueOf(surveyId)}, null, null, null);
        JSONObject json = null;
        if (c.moveToFirst()) {
            Survey survey = new Survey(c);
            json = survey.getAsJson();
            Log.d("Log", "Populated survey for " + surveyId + "in Survey operation");
            c.close();

            json.put(TableSurveyAssortimento.KEY_JSON_ARRAYNAME, TableSurveyAssortimento.getSurveyAsJSON(surveyId));
            json.put(TableSurveyPromotions.KEY_JSON_ARRAYNAME, TableSurveyPromotions.getSurveyAsJSON(isNewSurvey, surveyId));
            json.put(TableSurveyCompetitor.KEY_JSON_ARRAYNAME, TableSurveyCompetitor.getSurveyAsJSON(surveyId));
            Log.d("Log", "Populated survey completed " + surveyId + "in Survey operation");
        } else {
            Log.e("com.eteam.dufour.database.operations.SurveyDBOperations", "No survey id " + surveyId + "exists in" +
                    "Survey table");
        }
        return json;

    }

    public static final void updateFlag(long surveyId, int flag) {
        ContentValues values = new ContentValues();
        values.put(TableSurvey.COLUMN_FLAG, flag);
        getDB().update(TableSurvey.TABLE_NAME, values, TableSurvey.COLUMN_ID + "= ?"
                , new String[]{String.valueOf(surveyId)});
    }

    public static final void updateSurveySentDate(long surveyId, String sendDate, int clustValue1,int clustValue2,int clustValue3, int clustStatus) {
        ContentValues values = new ContentValues();
        values.put(TableSurvey.COLUMN_SURVEY_SENT_DATE, sendDate);
        values.put(TableSurvey.COLUMN_SURVEY_CLUSTER, clustValue1);
        values.put(TableSurvey.COLUMN_SURVEY_CLUSTER2, clustValue2);
        values.put(TableSurvey.COLUMN_SURVEY_CLUSTER3, clustValue3);
        values.put(TableSurvey.COLUMN_SURVEY_CLUSTER_STATUS, clustStatus);
        Survey survey = TableSurvey.getSurvey(surveyId);
        if (survey != null) {
            String name = TableLogin.getCompleteNameWithCode(survey.getSalesPersonCode());
            if (name != null)
                values.put(TableSurvey.COLUMN_SALESPERSON_NAME, name);
            else
                System.out.println("NAme Not Null ");

        } else {
            System.out.println("Survey Not Null ");
        }
        getDB().update(TableSurvey.TABLE_NAME, values, TableSurvey.COLUMN_ID + "=?"
                , new String[]{String.valueOf(surveyId)});
    }
    //Lijo
//	public static final void updateSurveySentDate(long surveyId,String sendDate,int clustValue){
//		ContentValues values = new ContentValues();
//		values.put(TableSurvey.COLUMN_SURVEY_SENT_DATE, sendDate);
//		values.put(TableSurvey.COLUMN_SURVEY_CLUSTER, clustValue);
//		getDB().update(TableSurvey.TABLE_NAME, values, TableSurvey.COLUMN_ID+"=?"
//				, new String[]{String.valueOf(surveyId)});
//	}



    public static final int getCluster1(long Survey_id){
        Cursor c = getDB().query(TABLE_NAME,new String[]{COLUMN_SURVEY_CLUSTER},TableSurvey.COLUMN_ID + "= ?",new String[]{String.valueOf(Survey_id)},null,null,null);
        int cluster = 99;
        if(c.moveToFirst()){
            cluster = c.getInt(c.getColumnIndex(COLUMN_SURVEY_CLUSTER));
        }
        c.close();
        return cluster;
    }
    public static final int getCluster2(long Survey_id){
        Cursor c = getDB().query(TABLE_NAME,new String[]{COLUMN_SURVEY_CLUSTER2},TableSurvey.COLUMN_ID + "= ?",new String[]{String.valueOf(Survey_id)},null,null,null);
        int cluster = 99;
        if(c.moveToFirst()){
            cluster = c.getInt(c.getColumnIndex(COLUMN_SURVEY_CLUSTER2));
        }
        c.close();
        return cluster;
    }
    public static final int getCluster3(long Survey_id){
        Cursor c = getDB().query(TABLE_NAME,new String[]{COLUMN_SURVEY_CLUSTER3},TableSurvey.COLUMN_ID + "= ?",new String[]{String.valueOf(Survey_id)},null,null,null);
        int cluster = 99;
        if(c.moveToFirst()){
            cluster = c.getInt(c.getColumnIndex(COLUMN_SURVEY_CLUSTER3));
        }
        c.close();
        return cluster;
    }

    public static final void delete(BaseSurvey survey) {
        delete(survey.getId());
    }

    public static final void delete(long id) {

        getDB().delete(TABLE_NAME, TableSurvey.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        TableSurveyAssortimento.delete(id);
        TableTempSurveyAssortimento.delete(id); //temp table for dashboard
        TableSurveyPromotions.delete(id);
        TableSurveyCompetitor.delete(id);
    }

    public static final void delete(String idList) {
        Log.d(TAG, "To delete " + idList);
        getDB().delete(TABLE_NAME, TableSurvey.COLUMN_ID + " IN (?)", new String[]{idList});
        TableSurveyAssortimento.delete(idList);
        TableTempSurveyAssortimento.delete(idList); //temp table for dashboard
        TableSurveyPromotions.delete(idList);
        TableSurveyCompetitor.delete(idList);
    }

    public static final void convertToOldSurveys(SQLiteDatabase xdb) {
        ContentValues values = new ContentValues();
        values.put(TableSurvey.COLUMN_FLAG, TableSurvey.FLAG_TO_BE_SENT_OLD);
        xdb.update(TableSurvey.TABLE_NAME, values, TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_TO_BE_SENT, null);
    }


    public static final int getToBeSentCount() {
        Cursor c = getDB().rawQuery("SELECT COUNT(1) FROM(SELECT "
                        + "a." + TableCustomers.COLUMN_NM
                        + " FROM " + TableCustomers.TABLE_NAME + " a"
                        + " INNER JOIN " + TABLE_NAME + " b"
                        + " WHERE a." + TableCustomers.COLUMN_NO + "=b." + TableSurvey.COLUMN_CUSTOMER_CODE
                        + " AND b." + COLUMN_FLAG + " IN ( " + FLAG_TO_BE_SENT + "," + FLAG_TO_BE_SENT_OLD + "))"
                , null);
        int count = 0;

        if (c.moveToFirst()) ;
        count = c.getInt(0);
        c.close();

        return count;
    }

    public static final int getDraftsCount() {
        Cursor c = getDB().rawQuery("SELECT COUNT(1) FROM(SELECT "
                + "a." + TableCustomers.COLUMN_NM
                + " FROM " + TableCustomers.TABLE_NAME + " a" +
                " INNER JOIN " + TableSurvey.TABLE_NAME + " b"
                + " WHERE a." + TableCustomers.COLUMN_NO + "=b." + TableSurvey.COLUMN_CUSTOMER_CODE
                + " AND b." + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_DRAFTS + ")", null);
        int count = 0;

        if (c.moveToFirst()) ;
        count = c.getInt(0);
        c.close();

        return count;
    }

    //return -1 if no id exists
    public static final long getDraftFor(String customerCode) {
        long id = -1;
        Cursor c = getDB().query(TableSurvey.TABLE_NAME,
                new String[]{TableSurvey.COLUMN_ID},
                TableSurvey.COLUMN_CUSTOMER_CODE + "= ? "
                        + " AND " + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_DRAFTS,
                new String[]{customerCode}, null, null, null);
        if (c.moveToFirst()) {
            id = c.getLong(c.getColumnIndex(TableSurvey.COLUMN_ID));
        }
        c.close();
        return id;
    }

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

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class Survey extends BaseSurvey {
        // ===========================================================
        // Constants
        // ===========================================================

        public static final String KEY_JSON_ARRAY_NAME = "survey";

        // ===========================================================
        // Fields
        // ===========================================================

        protected String customerCode;
        protected String salesPersonCode;
        protected String salesPersonName;
        protected int postStatus;
        protected int clustValue;
        protected int clustValue2;
        protected int clustValue3;
        protected int clustStatus;
        protected String syncDate;
        protected String uniqueId;
        protected String surveyDate;

        // ===========================================================
        // Constructors
        // ===========================================================

        public Survey(String salesPersonCode, String customerCode
                , String sentDateTime, int cluster,int cluster2,int cluster3, int clustStatus) {
            this(salesPersonCode, customerCode, sentDateTime,
                    0, TableSyncInfo.getSyncDate(), cluster,cluster2,cluster3, clustStatus);
        }

        public Survey(String salesPersonCode, String customerCode
                , int flag, String sentDateTime, int cluster,int cluster2,int cluster3, int clustStatus) {
            this(salesPersonCode, customerCode,
                    sentDateTime, flag,
                    0, TableSyncInfo.getSyncDate(), cluster,cluster2,cluster3, clustStatus);
        }

        public Survey(String salesPersonCode, String customerCode
                , String sentDateTime
                , int postStatus, int cluster,int cluster2,int cluster3, int clustStatus) {
            this(salesPersonCode, customerCode, sentDateTime,
                    postStatus, TableSyncInfo.getSyncDate(), cluster,cluster2,cluster3, clustStatus);
        }

        public Survey(String salesPersonCode, String customerCode
                , String sentDateTime
                , int postStatus, String syncDate, int cluster,int cluster2,int cluster3, int cluststatus) {
            this(salesPersonCode, customerCode,
                    sentDateTime, FLAG_NEW_SURVEY,
                    postStatus, syncDate, cluster,cluster2,cluster3, cluststatus);
        }

        public Survey(String salesPersonCode, String customerCode
                , String sentDateTime, int flag
                , int postStatus, String syncDate, int cluster,int cluster2,int cluster3, int cluststatus) {
            super(sentDateTime, flag);
            this.postStatus = postStatus;
            this.syncDate = syncDate;
            this.salesPersonCode = salesPersonCode;
            String name = TableLogin.getCompleteNameWithCode(salesPersonCode);
            if (name != null)
                this.salesPersonName = name;
            this.customerCode = customerCode;
            this.clustValue = cluster;
            this.clustValue2 = cluster2;
            this.clustValue3 = cluster3;
            this.clustStatus = cluststatus;
        }

        public Survey(Cursor c) {
            super(c);
            this.surveyDate = c.getString(c.getColumnIndex(COLUMN_SURVEY_DATE));
            syncDate = c.getString(c.getColumnIndex(COLUMN_SURVEY_SYNC_DATE));
            customerCode = c.getString(c.getColumnIndex(COLUMN_CUSTOMER_CODE));
            postStatus = c.getInt(c.getColumnIndex(COLUMN_SURVEY_POST_STATUS));
            salesPersonCode = c.getString(c.getColumnIndex(COLUMN_SALESPERSON_CODE));
            salesPersonName = c.getString(c.getColumnIndex(COLUMN_SALESPERSON_NAME));
            uniqueId = c.getString(c.getColumnIndex(COLUMN_UNIQUE_ID));
            clustValue = c.getInt(c.getColumnIndex(COLUMN_SURVEY_CLUSTER));
            clustValue2 = c.getInt(c.getColumnIndex(COLUMN_SURVEY_CLUSTER2));
            clustValue3 = c.getInt(c.getColumnIndex(COLUMN_SURVEY_CLUSTER3));




            clustStatus = c.getInt(c.getColumnIndex(COLUMN_SURVEY_CLUSTER_STATUS));
        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================

        public ContentValues getValues() {
            ContentValues values = super.getValues();
            values.put(COLUMN_CUSTOMER_CODE, customerCode);
            values.put(COLUMN_SALESPERSON_CODE, salesPersonCode);
            values.put(COLUMN_SALESPERSON_NAME, salesPersonName);
            values.put(COLUMN_UNIQUE_ID, Util.getUniqueId(salesPersonCode));
            values.put(COLUMN_SURVEY_SYNC_DATE, syncDate);
            values.put(COLUMN_SURVEY_POST_STATUS, postStatus);
            values.put(COLUMN_SURVEY_CLUSTER, clustValue);
            values.put(COLUMN_SURVEY_CLUSTER2, clustValue2);
            values.put(COLUMN_SURVEY_CLUSTER3, clustValue3);
            values.put(COLUMN_SURVEY_CLUSTER_STATUS, clustStatus);
            return values;
        }
        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        public String getSyncDate() {
            return syncDate;
        }

        public String getSalesPersonCode() {
            return salesPersonCode;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public int getPostStatus() {
            return postStatus;
        }

        public int getClusterValue() {
            return clustValue;
        }

        public int getClustStatus() {
            return clustStatus;
        }

        public String getSalesPersonName() {
            return salesPersonName;
        }

        // ===========================================================
        // Methods
        // ===========================================================


        public JSONObject getAsJson() throws JSONException {
            JSONObject json = super.getAsJson();
            json.put(COLUMN_SURVEY_DATE, surveyDate);
            json.put(TableSurvey.COLUMN_CUSTOMER_CODE, customerCode);
            json.put(TableSurvey.COLUMN_SALESPERSON_CODE, salesPersonCode);
            json.put(TableSurvey.COLUMN_SALESPERSON_NAME, salesPersonName);
            json.put(TableSurvey.COLUMN_SURVEY_POST_STATUS,
                    postStatus);
            json.put(TableSurvey.COLUMN_SURVEY_SYNC_DATE,
                    syncDate);
            json.put(TableSurvey.COLUMN_UNIQUE_ID, uniqueId);
            json.put(TableSurvey.COLUMN_SURVEY_CLUSTER, clustValue);
            json.put(TableSurvey.COLUMN_SURVEY_CLUSTER2, clustValue2);
            json.put(TableSurvey.COLUMN_SURVEY_CLUSTER3, clustValue3);
            json.put(TableSurvey.COLUMN_SURVEY_CLUSTER_STATUS, clustStatus);

            return json;
        }

        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
    }

    public static class BaseSurvey {
        // ===========================================================
        // Constants
        // ===========================================================

        private static final ContentValues values = new ContentValues();

        // ===========================================================
        // Fields
        // ===========================================================

        private long id;
        private int flag;
        private String sentDateTime;

        // ===========================================================
        // Constructors
        // ===========================================================

        public BaseSurvey(Cursor c) {
            id = c.getLong(c.getColumnIndex(COLUMN_ID));
            sentDateTime = c.getString(c
                    .getColumnIndex(COLUMN_SURVEY_SENT_DATE));
            flag = c.getInt(c.getColumnIndex(COLUMN_FLAG));
            mcheckSurvetDataHaveMillSeconsd(sentDateTime);
        }


        //not need  this method for  next updation ..its for the clear the bug of previous version 4/26/2018.Here add milliseconds and seconds
        private void mcheckSurvetDataHaveMillSeconsd(String sentDateTime) {
            if (sentDateTime != null) {
                if (isValidDate(sentDateTime)) {
                    SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-MM-yyyy / HH:mm:ss.SSS");
                    StringBuilder stringBuilder = new StringBuilder();
                    Date date = null;
                    int count = 100;
                    try {
                        stringBuilder.append(sentDateTime);
                        stringBuilder.append(":00.");
                        stringBuilder.append(count);
                        count++;
                        date = newDateFormat.parse(stringBuilder.toString());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        long changedTime = calendar.getTimeInMillis();
                        this.sentDateTime = Util.getNewChangedTime(changedTime);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public boolean isValidDate(String date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy / HH:mm");
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (ParseException e) {
                return false;
            }

            if (!sdf.format(testDate).equals(date)) {
                return false;
            }

            return true;
        }

        public BaseSurvey(String sentDateTime, int flag) {
            this.flag = flag;
            this.sentDateTime = sentDateTime;
        }

        public BaseSurvey(String sentDateTime) {
            this.flag = FLAG_NEW_SURVEY;
            this.sentDateTime = sentDateTime;
        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public String getSentDateTime() {
            return sentDateTime;
        }


        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================


        // ===========================================================
        // Methods
        // ===========================================================
        public ContentValues getValues() {
            values.clear();
            values.put(TableSurvey.COLUMN_SURVEY_DATE, sentDateTime);
            values.put(TableSurvey.COLUMN_FLAG, flag);
            return values;
        }


        public JSONObject getAsJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(Consts.JSON_KEY_ID, id);
            json.put(COLUMN_SURVEY_SENT_DATE, sentDateTime);

            return json;
        }
        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
    }


}
