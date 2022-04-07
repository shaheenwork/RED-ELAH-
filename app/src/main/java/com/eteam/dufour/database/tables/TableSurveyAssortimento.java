package com.eteam.dufour.database.tables;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.model.ItemsModel;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class TableSurveyAssortimento extends DBTable {
    // ===========================================================
    // Constants
    // ===========================================================

    private static final String TAG = TableSurveyAssortimento.class.getName();

    public static final String KEY_ARRAY_NAME = "ASSORT";
    public static final String KEY_JSON_ARRAYNAME = "assortimento";

    public static final String TABLE_NAME = "survey_assortimento";

    public static final String ALIAS_ID = "alias_assortimento_id";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SURVEY_ID = "Survey_Id";
    public static final String COLUMN_ITEM_CODE = "Item_Code";
    public static final String COLUMN_ITEM_DESC = "Item_Desc";
    public static final String COLUMN_ITEM_MARCHIO = "Item_Marchio";
    public static final String COLUMN_ITEM_LINEA = "Item_Linea";
    public static final String COLUMN_ITEM_MACROFAMIGLIA = "Item_Macrofamiglia";
    public static final String COLUMN_SURVEY_PREZZO_PROMO = "Prezzo_Promo";
    public static final String COLUMN_SURVEY_PREZZO = "Survey_Prezzo";
    public static final String COLUMN_SURVEY_FACING = "Survey_Facing";
    public static final String COLUMN_SURVEY_LINEA = "Survey_Linea";
    public static final String COLUMN_SURVEY_FUORI_BANCO = "Survey_Fuori_Banco";
    public static final String COLUMN_SURVEY_LEVEL = "Survey_Level";
    public static final String COLUMN_UNIQUE_FIELD = "Unique_Field";
    public static final String COLUMN_SURVEY_ASSORT_MODIFY = "Survey_Assort_Modify";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SURVEY_ID + " INTEGER, "
            + COLUMN_ITEM_CODE + " TEXT, "
            + COLUMN_ITEM_DESC + " TEXT, "
            + COLUMN_ITEM_MARCHIO + " TEXT, "
            + COLUMN_ITEM_LINEA + " TEXT, "
            + COLUMN_ITEM_MACROFAMIGLIA + " TEXT, "
            + COLUMN_SURVEY_PREZZO + " TEXT, "
            + COLUMN_SURVEY_PREZZO_PROMO + " TEXT, "
            + COLUMN_SURVEY_FACING + " TEXT, "
            + COLUMN_SURVEY_LINEA + " TEXT, "
            //  + COLUMN_SURVEY_FUORI_BANCO + " TEXT, "
            + COLUMN_SURVEY_LEVEL + " INTEGER, "
            + COLUMN_UNIQUE_FIELD + " TEXT,"
            + COLUMN_SURVEY_ASSORT_MODIFY + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_SURVEY_ID + ") REFERENCES "
            + TableSurvey.TABLE_NAME + "(" + TableSurvey.COLUMN_ID + "))";


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

    public static long createNew(SurveyAssortimento survey) {
        long id = -1;
        if (survey.isValid()) {
            id = getDB().insert(TABLE_NAME, null, survey.getValues());
        }
        return id;
    }


    public static void create(long surveyId, String salesPersonCode, JSONArray arraySurveyAssortimento) throws JSONException {
        SQLiteDatabase db = getDB();
        db.beginTransaction();
        int length = arraySurveyAssortimento.length();
        for (int i = 0; i < length; i++) {

            SurveyAssortimento surveyAssortimento = new SurveyAssortimento(surveyId, salesPersonCode,
                    arraySurveyAssortimento.getJSONObject(i));
            getDB().insert(TABLE_NAME
                    , null, surveyAssortimento.getValues());


        }
        db.setTransactionSuccessful();
        db.endTransaction();
        joinDuplicates(db, surveyId);
    }

    /**
     * Method to deal with duplicate entry bug created using version 3.2 (HTML5) version
     * <b>(A new db entry is created each time user tries to modify the survey ,
     * when duplicate entries are present for the same item.
     * To resolve this we join the duplicate entries from latest to old, creating a single survey.
     * In this it is assumed that user did not clear any of the fields in the survey.) </b>
     *
     * @param db instance of elah's sqlite database
     */
    public static void joinDuplicates(SQLiteDatabase db, long surveyId) {
        Cursor c = db.rawQuery("SELECT " + TableSurveyAssortimento.COLUMN_ITEM_CODE + " FROM "
                + TableSurveyAssortimento.TABLE_NAME
                + " WHERE " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId
                + " GROUP BY " + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " HAVING COUNT(" + TableSurveyAssortimento.COLUMN_ITEM_CODE + ")>1", null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String itemCode = c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_ITEM_CODE));
                convergeAllItems(db, itemCode, surveyId);
                c.moveToNext();
            }
        }
        c.close();
    }


    private static void convergeAllItems(SQLiteDatabase db, String itemCode, long surveyId) {
        Cursor c = db.query(TableSurveyAssortimento.TABLE_NAME, getConvergeColumns()
                 , TableSurveyAssortimento.COLUMN_ITEM_CODE + "=" + itemCode
                        + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId, null
                , null, null, TableSurveyAssortimento.COLUMN_ID + " DESC");
        SurveyAssortimento product = new SurveyAssortimento();
        String idsToDelete = new String();
        int idToUpdate = -1;
        if (c.moveToFirst()) {
            boolean first = true;
            while (!c.isAfterLast()) {
                Log.d("item code",itemCode);
                int id = c.getInt(c.getColumnIndex(TableSurveyAssortimento.COLUMN_ID));
                product.copySurveyWithNonNullFields(new SurveyAssortimento(c));
//				idsToDelete = String.valueOf(id);
                if (c.moveToNext()) {
                    if (first) {
                        idsToDelete += id;
                        first = false;
                    } else
                        idsToDelete += "," + id;
                } else {
                    idToUpdate = id;
                }
            }
        }
        c.close();
        if (!idsToDelete.isEmpty() && idToUpdate != -1) {

                db.delete(TableSurveyAssortimento.TABLE_NAME, TableSurveyAssortimento.COLUMN_ID + " IN (?)", new String[]{idsToDelete});
            ContentValues values = new ContentValues();
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO, product.prezzo);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO, product.prezzoPromo);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_FACING, product.facing);
            //   values.put(TableSurveyAssortimento.COLUMN_SURVEY_FUORI_BANCO, product.fb);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL, product.assorti);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_LINEA, product.prezzoLinea);
            db.update(TableSurveyAssortimento.TABLE_NAME, values, TableSurveyAssortimento.COLUMN_ID + "=" + idToUpdate, null);
        }


    }

    private static String[] convergeColumns;


    public static String[] getConvergeColumns() {
        if (convergeColumns == null) { // changed here, added coloumns
            convergeColumns = new String[]{TableTempSurveyAssortimento.COLUMN_ID
                    , TableSurveyAssortimento.COLUMN_SURVEY_PREZZO
                    , TableSurveyAssortimento.COLUMN_ITEM_CODE
                    , TableSurveyAssortimento.COLUMN_ITEM_LINEA
                    , TableSurveyAssortimento.COLUMN_ITEM_MACROFAMIGLIA
                    , TableSurveyAssortimento.COLUMN_SURVEY_ASSORT_MODIFY
                    , TableSurveyAssortimento.COLUMN_UNIQUE_FIELD
                    , TableSurveyAssortimento.COLUMN_ITEM_DESC
                    , TableSurveyAssortimento.COLUMN_ITEM_MARCHIO
                    , TableSurveyAssortimento.COLUMN_SURVEY_ID
                    , TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO
                    , TableSurveyAssortimento.COLUMN_SURVEY_FACING
                    , TableSurveyAssortimento.COLUMN_SURVEY_LINEA
                    //    , TableSurveyAssortimento.COLUMN_SURVEY_FUORI_BANCO
                    , TableSurveyAssortimento.COLUMN_SURVEY_LEVEL};

        }

        return convergeColumns;
    }

    private static void insertOrUpdate(SurveyAssortimento survey, ContentValues values) {
        long id = survey.id;
        if (survey.isValid()) {

            if (id == 0 || id == -1) {
                survey.id = getDB().insert(TABLE_NAME, null, values);
//				Log.d(TAG, "Survey is inserted and id is"+survey.id);
            }
            getDB().update(TABLE_NAME, values, COLUMN_ID + "= " + survey.id, null);
//			Log.d(TAG, "Survey is updated and id is"+survey.id+" for item "+survey.itemCode);
        } else if (id != 0 && id != -1) {
            getDB().delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
            survey.id = -1;
        }
    }

    public static void delete(ElahDBHelper dbHelper, long surveyId, String itemCode) {
        dbHelper.getWritableDatabase().delete(TableSurveyAssortimento.TABLE_NAME,
                TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId
                        + " AND " + TableSurveyAssortimento.COLUMN_ITEM_CODE + "='" + itemCode + "'", null);
    }

    public static void delete(long surveyId) {
        getDB().delete(TableSurveyAssortimento.TABLE_NAME,
                TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId, null);
    }

    public static void delete(String idToDelete) {
        getDB().delete(TableSurveyAssortimento.TABLE_NAME,
                TableSurveyAssortimento.COLUMN_SURVEY_ID + " IN (" + idToDelete + ")", null);
    }

    public static ArrayList<String> getItemCodes(long surveyId) {
        ArrayList<String> array = new ArrayList<String>();
        Cursor c = getDB().query(TableSurveyAssortimento.TABLE_NAME, new String[]{TableSurveyAssortimento.COLUMN_ITEM_CODE},
                TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId, null, null, null, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                array.add(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_ITEM_CODE)));
                c.moveToNext();
            }
        }
        c.close();

        return array;
    }

    public static JSONArray getSurveyAsJSON(long surveyId) throws JSONException {
//		Log.d("Log", "Population started for "+surveyId+"in Prodotti Survey operation");
        Cursor c = getDB().query(TableSurveyAssortimento.TABLE_NAME,
                new String[]{TableSurveyAssortimento.COLUMN_ID
                        , TableSurveyAssortimento.COLUMN_SURVEY_ID
                        , TableSurveyAssortimento.COLUMN_ITEM_CODE
                        , TableSurveyAssortimento.COLUMN_ITEM_DESC
                        , TableSurveyAssortimento.COLUMN_ITEM_MARCHIO
                        , TableSurveyAssortimento.COLUMN_ITEM_LINEA
                        , TableSurveyAssortimento.COLUMN_ITEM_MACROFAMIGLIA
                        , TableSurveyAssortimento.COLUMN_SURVEY_PREZZO
                        , TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO
                        , TableSurveyAssortimento.COLUMN_SURVEY_FACING
                        , TableSurveyAssortimento.COLUMN_SURVEY_LINEA
                        //   , TableSurveyAssortimento.COLUMN_SURVEY_FUORI_BANCO
                        , TableSurveyAssortimento.COLUMN_SURVEY_LEVEL
                        , TableSurveyAssortimento.COLUMN_UNIQUE_FIELD
                        , TableSurveyAssortimento.COLUMN_SURVEY_ASSORT_MODIFY},

                TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId , null, null, null, null);
               JSONArray array = new JSONArray();

        if (c.moveToFirst()) {

            Log.d(TAG, "assortimento count - " + c.getCount());

            while (!c.isAfterLast()) {
                SurveyAssortimento survey = new SurveyAssortimento(c);
                if (survey.prezzo!=null && !survey.prezzo.equals("")
                        &&survey.facing!=0
                        &&survey.prezzoLinea!=null &&!survey.prezzoLinea.equals("")
                        && survey.assorti!=0
                        &&survey.itemLinea!=null && !survey.itemLinea.equals("")
                        && survey.itemMarchio!=null && !survey.itemMarchio.equals("")
                        &&survey.itemMacrofamiglia!=null && !survey.itemMacrofamiglia.equals("")) {
                    array.put(survey.getAsJson());
                }
                c.moveToNext();

            }
        }
        c.close();

        Log.d(TAG, "new count - " + array.length());
        Log.d("Log", "Population completed for " + surveyId + "in Prodotti Survey operation");
        return array;

    }

  public static int getNumberOfProductsByLineaAndSurveyLevel(long surveyId, String linea, int surveyLevel)  {
//		Log.d("Log", "Population started for "+surveyId+"in Prodotti Survey operation");
        Cursor c = getDB().query(TableSurveyAssortimento.TABLE_NAME,
                new String[]{
                          TableSurveyAssortimento.COLUMN_SURVEY_ID
                        , TableSurveyAssortimento.COLUMN_ITEM_LINEA
                        , TableSurveyAssortimento.COLUMN_SURVEY_LEVEL},
                TableSurveyAssortimento.COLUMN_ITEM_LINEA + "=" + "\""+linea+"\""
                        + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" +  surveyId+ " AND " + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL + "=" +  surveyLevel, null, null, null, null);


        return c.getCount();

    }
    public static int getTotalNumberOfProductsByLinea(long surveyId,String linea)  {
//		Log.d("Log", "Population started for "+surveyId+"in Prodotti Survey operation");
        Cursor c = getDB().query(TableSurveyAssortimento.TABLE_NAME,
                new String[]{
                        TableSurveyAssortimento.COLUMN_SURVEY_ID
                        , TableSurveyAssortimento.COLUMN_ITEM_CODE
                        , TableSurveyAssortimento.COLUMN_ITEM_LINEA
                        , TableSurveyAssortimento.COLUMN_SURVEY_LEVEL},
                TableSurveyAssortimento.COLUMN_ITEM_LINEA + "=" + "\""+linea+"\""
                        + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" +  surveyId, null, null, null, null);



    //    Log.v("Cursor Object-s", DatabaseUtils.dumpCursorToString(c));

        return c.getCount();

    }

    /*public static boolean isAllMandatoryFieldsFilledCorrectly(long surveyId) {


        //fields are not mandatory if survey level is 2 (NonAssortito)

        String selection = TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId + " AND ("
                + COLUMN_SURVEY_PREZZO + " is null or " + COLUMN_SURVEY_PREZZO + " = \"\" or "
                + COLUMN_SURVEY_LEVEL + " is null or " + COLUMN_SURVEY_LEVEL + " = \"\" or " + COLUMN_SURVEY_LEVEL + " =2 or "+ COLUMN_SURVEY_LEVEL + " =0 or "
                + COLUMN_SURVEY_FACING + " is null or " + COLUMN_SURVEY_FACING + " = \"\" or "+ COLUMN_SURVEY_FACING + " =0 or "
                + COLUMN_SURVEY_LINEA + " is null or " + COLUMN_SURVEY_LINEA + " = \"\" or " + COLUMN_SURVEY_LINEA + " = " + "\"/\" )";

        Cursor c = getDB().query(TableSurveyAssortimento.TABLE_NAME,
                new String[]{TableSurveyAssortimento.COLUMN_SURVEY_ID
                        , TableSurveyAssortimento.COLUMN_ID //
                        , TableSurveyAssortimento.COLUMN_ITEM_CODE //
                        , TableSurveyAssortimento.COLUMN_SURVEY_PREZZO
                        , TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO
                        , TableSurveyAssortimento.COLUMN_SURVEY_FACING
                        , TableSurveyAssortimento.COLUMN_SURVEY_LINEA
                        , TableSurveyAssortimento.COLUMN_SURVEY_LEVEL
                        , TableSurveyAssortimento.COLUMN_SURVEY_ASSORT_MODIFY}, selection


                , null, null, null, null);


        int count = 0;
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(c));
        while (c.moveToNext()){
           if (c.getString(c.getColumnIndex(COLUMN_SURVEY_LEVEL))!=null)
            {
                if (!(c.getString(c.getColumnIndex(COLUMN_SURVEY_LEVEL)).equals("2"))){
                    count++;
                }
            }
            else {
               count++;
           }
        }

        if (count == 0) {
            return true;
        } else {
            return false;
        }

    }*/


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================


    public static class SurveyAssortimento implements Parcelable {

        // ===========================================================
        // Constants
        // ===========================================================

        public static final String KEY_ITEM_CODE = "IC";
        public static final String KEY_ITEM_DESCRIPTION = "ID";
        public static final String KEY_ITEM_MARCHIO = "IM";
        public static final String KEY_ITEM_LINEA = "IL";
        public static final String KEY_ITEM_MACROFAMIGLIA = "IMF";
        public static final String KEY_SURVEY_PREZZO = "SP";
        public static final String KEY_SURVEY_PREZZO_PROMO = "PP";
        public static final String KEY_SURVEY_FACING = "SF";
        public static final String KEY_SURVEY_PREZZO_LINEA = "SPL";
        //  public static final String KEY_SURVEY_FB = "SFB";
        public static final String KEY_SURVEY_LEVEL = "SLA";

        private static final ContentValues values = new ContentValues();

        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        // ===========================================================
        // Fields
        // ===========================================================

        private long id;
        private long surveyId;
        private String itemCode;
        private String itemDesc;
        private String itemMarchio;
        private String itemLinea;
        private String itemMacrofamiglia;
        private String prezzo;
        private String prezzoPromo;
        private int facing;
        private String prezzoLinea;
        // private int fb;
        private int assorti;
        private String uniqueId;
        private String surveyModified;

        private boolean viewToBeUpdated;

        public static final Parcelable.Creator<SurveyAssortimento> CREATOR
                = new Parcelable.Creator<SurveyAssortimento>() {
            public SurveyAssortimento createFromParcel(Parcel in) {
                return new SurveyAssortimento(in);
            }

            public SurveyAssortimento[] newArray(int size) {
                return new SurveyAssortimento[size];
            }
        };


        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeLong(surveyId);
            dest.writeString(itemCode);
            dest.writeString(itemDesc);
            dest.writeString(itemMarchio);
            dest.writeString(itemLinea);
            dest.writeString(itemMacrofamiglia);
            dest.writeString(prezzo);
            dest.writeString(prezzoPromo);
            dest.writeInt(facing);
            dest.writeString(prezzoLinea);
            //    dest.writeInt(fb);
            dest.writeInt(assorti);
            dest.writeString(uniqueId);
            dest.writeString(surveyModified);

        }

        private SurveyAssortimento(Parcel in) {
            id = in.readLong();
            surveyId = in.readLong();
            itemCode = in.readString();
            itemDesc = in.readString();
            itemMarchio = in.readString();
            itemLinea = in.readString();
            itemMacrofamiglia = in.readString();
            prezzo = in.readString();
            prezzoPromo = in.readString();
            facing = in.readInt();
            prezzoLinea = in.readString();
            //fb = in.readInt();
            assorti = in.readInt();
            uniqueId = in.readString();
            surveyModified = in.readString();
        }


        // ===========================================================
        // Constructors
        // ===========================================================
        public SurveyAssortimento(Cursor c) {
            id = c.getInt(c.getColumnIndex(COLUMN_ID));
            surveyId = c.getLong(c.getColumnIndex(COLUMN_SURVEY_ID));
            itemCode = c.getString(c.getColumnIndex(COLUMN_ITEM_CODE));
            ///////////////////////////////
            //change by bibin some fileds are missing maricho ..then updation it filled in item table didn't reflect on sub table that issue slove by this
            //get itemModel from
            ItemsModel itemsModel = TableItems.populateParticulrItem(itemCode);


            itemDesc = c.getString(c.getColumnIndex(COLUMN_ITEM_DESC));

            itemMarchio = c.getString(c.getColumnIndex(COLUMN_ITEM_MARCHIO));
            if (valueNullCheck(itemMarchio)) {
                if (itemsModel != null) {
                    if (itemsModel.getMarchio() == null) {
                        itemMarchio = "";
                    } else {
                        itemMarchio = itemsModel.getMarchio();
                    }
                } else {
                    itemMarchio = "";
                }
            }


            itemLinea = c.getString(c.getColumnIndex(COLUMN_ITEM_LINEA));

            if (valueNullCheck(itemLinea)) {
                if (itemsModel != null) {
                    if (itemsModel.getLinea() == null) {
                        itemLinea = "";
                    } else {
                        itemLinea = itemsModel.getLinea();
                    }
                } else {
                    itemLinea = "";
                }
            }

            itemMacrofamiglia = c.getString(c.getColumnIndex(COLUMN_ITEM_MACROFAMIGLIA));
            if (valueNullCheck(itemMacrofamiglia)) {
                if (itemsModel != null) {
                    if (itemsModel.getMacroFamiglia() == null) {
                        itemMacrofamiglia = "";
                    } else {
                        itemMacrofamiglia = itemsModel.getMacroFamiglia();
                    }
                } else {
                    itemMacrofamiglia = "";
                }
            }

            //////////////////////////////
            prezzo = c.getString(c.getColumnIndex(COLUMN_SURVEY_PREZZO));
            prezzoPromo = c.getString(c.getColumnIndex(COLUMN_SURVEY_PREZZO_PROMO));
            facing = c.getInt(c.getColumnIndex(COLUMN_SURVEY_FACING));
            prezzoLinea = c.getString(c.getColumnIndex(COLUMN_SURVEY_LINEA));
            //fb = c.getInt(c.getColumnIndex(COLUMN_SURVEY_FUORI_BANCO));
            assorti = c.getInt(c.getColumnIndex(COLUMN_SURVEY_LEVEL));
            uniqueId = c.getString(c.getColumnIndex(COLUMN_UNIQUE_FIELD));
            surveyModified = c.getString(c.getColumnIndex(COLUMN_SURVEY_ASSORT_MODIFY));

        }

        //update by bibin for null check
        private Boolean valueNullCheck(String item) {
            if (item == null) {
                return false;
            } else
                return true;
        }


        public SurveyAssortimento(long surveyId, String salesMan, Item item, Cursor c, boolean usesAlias) {
            id = c.getLong(c.getColumnIndex(usesAlias ? ALIAS_ID : COLUMN_ID));
            this.surveyId = surveyId;
            itemCode = item.getCode();
            itemDesc = item.getDescription();
            itemMarchio = item.marchio;
            itemLinea = item.linea;
            itemMacrofamiglia = item.macrofamiglia;
            prezzo = c.getString(c.getColumnIndex(COLUMN_SURVEY_PREZZO));
            prezzoPromo = c.getString(c.getColumnIndex(COLUMN_SURVEY_PREZZO_PROMO));
            facing = c.getInt(c.getColumnIndex(COLUMN_SURVEY_FACING));
            prezzoLinea = c.getString(c.getColumnIndex(COLUMN_SURVEY_LINEA));
            //fb = c.getInt(c.getColumnIndex(COLUMN_SURVEY_FUORI_BANCO));
            assorti = c.getInt(c.getColumnIndex(COLUMN_SURVEY_LEVEL));
            uniqueId = Util.getUniqueId(salesMan);
            addPropertyChangeListener(changeListener);
        }

        public SurveyAssortimento() {
            // TODO Auto-generated constructor stub
            //fb = -1;
            facing = -1;
            assorti = -1;
        }

        public SurveyAssortimento(long surveyId, String salesPerson, JSONObject json)
                throws JSONException {
            this.surveyId = surveyId;
            itemCode = json.optString(KEY_ITEM_CODE);
            itemDesc = json.optString(KEY_ITEM_DESCRIPTION);
            itemMarchio = json.optString(KEY_ITEM_MARCHIO);
            itemLinea = json.optString(KEY_ITEM_LINEA);
            itemMacrofamiglia = json.optString(KEY_ITEM_MACROFAMIGLIA);
            prezzo = json.optString(KEY_SURVEY_PREZZO);
            prezzoPromo = json.optString(KEY_SURVEY_PREZZO_PROMO);
            facing = json.optInt(KEY_SURVEY_FACING);
            prezzoLinea = json.optString(KEY_SURVEY_PREZZO_LINEA);
            //fb = json.optInt(KEY_SURVEY_FB);
            assorti = json.optInt(KEY_SURVEY_LEVEL);
            uniqueId = Util.getUniqueId(salesPerson);
            updateModifiedTime();

        }


        // ===========================================================
        // Getter & Setter
        // ===========================================================

        private static final String TAG = TableSurveyAssortimento.class.getName();

        public void setViewToBeUpdated(boolean viewToBeUpdated) {
            this.viewToBeUpdated = viewToBeUpdated;
        }

        public boolean isViewToBeUpdated() {
            // TODO Auto-generated method stub
            return viewToBeUpdated;
        }

        public void copy(SurveyAssortimento survey) {
            setPrezzo(survey.prezzo);
            setPrezzoPromo(survey.prezzoPromo);
            setFacing(survey.facing);
            // setFb(survey.fb);
            setPrezzoLinea(survey.prezzoLinea);
            setAssorti(survey.assorti);
        }

        PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                updateModifiedTime();
                values.clear();
                if (event.getPropertyName().equals(COLUMN_SURVEY_FACING)) {
                    values.put(COLUMN_SURVEY_FACING, facing);
                }/* else if (event.getPropertyName().equals(COLUMN_SURVEY_FUORI_BANCO)) {
                    values.put(COLUMN_SURVEY_FUORI_BANCO, fb);
                }*/ else if (event.getPropertyName().equals(COLUMN_SURVEY_LEVEL)) {
                    values.put(COLUMN_SURVEY_LEVEL, assorti);
                } else if (event.getPropertyName().equals(COLUMN_SURVEY_LINEA)) {
                    values.put(COLUMN_SURVEY_LINEA, prezzoLinea);
                } else if (event.getPropertyName().equals(COLUMN_SURVEY_PREZZO)) {
                    values.put(COLUMN_SURVEY_PREZZO, prezzo);
                } else if (event.getPropertyName().equals(COLUMN_SURVEY_PREZZO_PROMO)) {
                    values.put(COLUMN_SURVEY_PREZZO_PROMO, prezzoPromo);

                }
                Iterator<String> keys = values.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
//					Log.d(TAG, "Value is "+key+":"+values.get(key));
                    ;
                }
                viewToBeUpdated = true;
                if (id == -1 || id == 0)
                    updateBaseValues();
                values.put(COLUMN_SURVEY_ASSORT_MODIFY, surveyModified);
                insertOrUpdate(SurveyAssortimento.this, values);
            }
        };

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.removePropertyChangeListener(listener);
        }

        public String getItemCode() {
            return itemCode;
        }

        public long getSurveyId() {
            return surveyId;
        }

        public String getPrezzo() {
            return prezzo;
        }

        public long getId() {
            return id;
        }

        public void setPrezzo(String surveyPrezzo) {
            String oldValue = this.prezzo;
            this.prezzo = surveyPrezzo;
            pcs.firePropertyChange(COLUMN_SURVEY_PREZZO, oldValue, surveyPrezzo);
        }

        public int getFacing() {
            return facing;
        }

        public void setFacing(int facing) {
            int oldValue = this.facing;
            this.facing = facing;
//			Log.d(TAG, "Changing facing value "+facing);
            pcs.firePropertyChange(COLUMN_SURVEY_FACING
                    , oldValue, facing);
        }

        public String getPrezzoLinea() {
            return prezzoLinea;
        }

        public void setPrezzoLinea(String prezzoLinea) {
            if (TextUtils.isEmpty(prezzoLinea)
                    || prezzoLinea.equals("/")
                    || prezzoLinea.equals("0/0"))
                this.prezzoLinea = "";
            String oldValue = this.prezzoLinea;
            this.prezzoLinea = prezzoLinea;
            pcs.firePropertyChange(COLUMN_SURVEY_LINEA
                    , oldValue, prezzoLinea);
        }

       /* public int getFb() {
            return fb;
        }*/

     /*   public void setFb(int fb) {
            int oldValue = this.fb;
            this.fb = fb;
            pcs.firePropertyChange(COLUMN_SURVEY_FUORI_BANCO
                    , oldValue, fb);
        }*/

        public int getAssorti() {
            return assorti;
        }

        public void setAssorti(int assorti) {
            int oldValue = this.assorti;
            this.assorti = assorti;
            pcs.firePropertyChange(COLUMN_SURVEY_LEVEL, oldValue, assorti);
        }

        public int getLineaOne() {
            if (TextUtils.isEmpty(prezzoLinea))
                return 0;
            int slash = prezzoLinea.indexOf("/");
            try {
                String one = prezzoLinea.substring(0, slash);
                return Integer.parseInt(one);
            } catch (Exception e) {
                return 0;
            }

        }

        public int getLineaTwo() {
            if (TextUtils.isEmpty(prezzoLinea))
                return 0;
            int slash = prezzoLinea.indexOf("/");
            try {
                String two = prezzoLinea.substring(slash + 1, prezzoLinea.length());
                return Integer.parseInt(two);
            } catch (Exception e) {
                return 0;
            }
        }

        public String getPrezzoPromo() {
            return prezzoPromo;
        }

        public void setPrezzoPromo(String prezzoPromo) {
            String oldValue = this.prezzoPromo;
            this.prezzoPromo = prezzoPromo;
            pcs.firePropertyChange(COLUMN_SURVEY_PREZZO_PROMO
                    , oldValue, prezzoPromo);
        }

        /************************************************************************
         * A Bug fix function which merges all the duplicate values into a single
         * entity.
         * NOTE- No property change listener is set
         * *********************************************************************/
        public void copySurveyWithNonNullFields(SurveyAssortimento survey) {
            if (!TextUtils.isEmpty(survey.prezzo) && TextUtils.isEmpty(prezzo)) {
                prezzo = survey.prezzo;
            }
            if (!TextUtils.isEmpty(survey.prezzoPromo) && TextUtils.isEmpty(prezzoPromo)) {
                prezzoPromo = survey.prezzoPromo;
            }
            if (!TextUtils.isEmpty(survey.prezzoLinea) && TextUtils.isEmpty(prezzoLinea)) {
                prezzoLinea = survey.prezzoLinea;
            }
            if (survey.assorti != -1 && assorti == -1) {
                assorti = survey.assorti;
            }
            if (survey.facing != -1 && facing == -1) {
                facing = survey.facing;
            }
           /* if (survey.fb != -1 && fb == -1) {
                fb = survey.fb;
            }*/
        }

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        // ===========================================================
        // Methods
        // ===========================================================


        private void updateBaseValues() {
            updateModifiedTime();
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_ID, surveyId);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_CODE, itemCode);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_DESC, itemDesc);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_LINEA, itemLinea);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_MARCHIO, itemMarchio);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_MACROFAMIGLIA, itemMacrofamiglia);
            values.put(TableSurveyAssortimento.COLUMN_UNIQUE_FIELD, uniqueId);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_ASSORT_MODIFY, surveyModified);
        }

        public ContentValues getValues() {
            updateModifiedTime();
            values.clear();

            values.put(TableSurveyAssortimento.COLUMN_ITEM_CODE, itemCode);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_DESC, itemDesc);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_MARCHIO, itemMarchio);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_LINEA, itemLinea);
            values.put(TableSurveyAssortimento.COLUMN_ITEM_MACROFAMIGLIA, itemMacrofamiglia);

            values.put(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO, Util.getAsElahDBNumFormat(prezzo));
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO, Util.getAsElahDBNumFormat(prezzoPromo));
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_LINEA, prezzoLinea);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_ID, surveyId);
            //   values.put(TableSurveyAssortimento.COLUMN_SURVEY_FUORI_BANCO, fb);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL, assorti);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_FACING, facing);
            values.put(TableSurveyAssortimento.COLUMN_UNIQUE_FIELD, uniqueId);
            values.put(TableSurveyAssortimento.COLUMN_SURVEY_ASSORT_MODIFY, surveyModified);

            return values;
        }

        public boolean isValid() {
            //   if (facing == 0 && fb == 0 && assorti == 0) {
            if (facing == 0 && assorti == 0) {
                if (TextUtils.isEmpty(prezzoLinea)
                        || prezzoLinea.equals("0/0")
                        || prezzoLinea.trim().equals("/")) {
                    if (TextUtils.isEmpty(prezzo)) {
                        if (TextUtils.isEmpty(prezzoPromo)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void updateModifiedTime() {
            surveyModified = Util.getCurrentDateTime();
        }


        public JSONObject getAsJson() throws JSONException {

            JSONObject survey = new JSONObject();

            survey.put(Consts.JSON_KEY_ID, id);
            survey.put(TableSurveyAssortimento.COLUMN_ITEM_CODE, formatStringToJSON(itemCode));
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_ID, surveyId);
            survey.put(TableSurveyAssortimento.COLUMN_ITEM_DESC, formatStringToJSON(itemDesc));
            survey.put(TableSurveyAssortimento.COLUMN_ITEM_MARCHIO, formatStringToJSON(itemMarchio));
            survey.put(TableSurveyAssortimento.COLUMN_ITEM_LINEA, formatStringToJSON(itemLinea));
            survey.put(TableSurveyAssortimento.COLUMN_ITEM_MACROFAMIGLIA, formatStringToJSON(itemMacrofamiglia));
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO, formatStringToJSON(prezzo));
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO, formatStringToJSON(prezzoPromo));
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_FACING, facing);
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_LINEA, formatStringToJSON(prezzoLinea));
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_FUORI_BANCO, "");  //send as empty
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL, assorti);
            survey.put(TableSurveyAssortimento.COLUMN_UNIQUE_FIELD, formatStringToJSON(uniqueId));
            survey.put(TableSurveyAssortimento.COLUMN_SURVEY_ASSORT_MODIFY, formatStringToJSON(surveyModified));

            return survey;
        }

        public void setBaseValues(long surveyId, Item item,
                                  String salesPersonCode) {
            this.surveyId = surveyId;
            this.itemCode = item.getCode();
            this.itemDesc = item.getDescription();
            this.itemLinea = item.linea;
            this.itemMarchio = item.marchio;
            this.uniqueId = Util.getUniqueId(salesPersonCode);
            updateModifiedTime();

        }


        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
    }


}
