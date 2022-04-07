package com.eteam.dufour.database.tables;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.dufour.objects.TPromotionItem;
import com.eteam.model.ItemsModel;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class TableSurveyPromotionsItem extends DBTable {
    // ===========================================================
    // Constants
    // ===========================================================

    private static final String TAG = TableSurveyPromotionsItem.class.getName();

    public static final String KEY_JSON_ARRAYNAME = "promotionItems";

    public static final String KEY_ARRAY_NAME = "ITEMS";

    public static final String TABLE_NAME = "survey_promotions_items";


    public static final String ALIAS_ID = "alias_survey_promotion_items_id";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SURVEY_PROMOTION_ID = "Survey_Promotion_Id";
    public static final String COLUMN_ITEM_CODE = "Item_Code";
    public static final String COLUMN_ITEM_DESC = "Item_Desc";
    public static final String COLUMN_ITEM_MARCHIO = "Item_Marchio";
    public static final String COLUMN_ITEM_LINEA = "Item_Linea";
    public static final String COLUMN_ITEM_MACROFAMIGLIA = "Item_Macrofamiglia";
    public static final String COLUMN_SURVEY_VOLANTINO = "Survey_Volantino";
    public static final String COLUMN_SURVEY_PREZZO = "Survey_Prezzo";
    public static final String COLUMN_UNIQUE_FIELD = "Unique_Field";
    public static final String COLUMN_SURVEY_MODIFIED = "Survey_Promo_Item_Modify";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SURVEY_PROMOTION_ID + " INTEGER, "
            + COLUMN_ITEM_CODE + " TEXT, "
            + COLUMN_ITEM_DESC + " TEXT, "
            + COLUMN_ITEM_MARCHIO + " TEXT, "
            + COLUMN_ITEM_LINEA + " TEXT, "
            + COLUMN_ITEM_MACROFAMIGLIA + " TEXT, "
            + COLUMN_SURVEY_VOLANTINO + " TEXT, "
            + COLUMN_SURVEY_PREZZO + " TEXT, "
            + COLUMN_UNIQUE_FIELD + " TEXT, "
            + COLUMN_SURVEY_MODIFIED + " TEXT, "
            + " FOREIGN KEY(" + COLUMN_SURVEY_PROMOTION_ID + ") REFERENCES "
            + TableSurveyPromotions.TABLE_NAME + "(" + TableSurveyPromotions.COLUMN_ID + "))";
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

    public static long createNew(SurveyPromotionItem survey) {
//		survey.clearValues();
//		survey.updateBaseValues();
        ContentValues values = survey.getValues();
        Log.d(TAG, "Volantino : " + values.getAsString(COLUMN_SURVEY_VOLANTINO));
        Log.d(TAG, "Promotion id :" + values.getAsString(COLUMN_SURVEY_PROMOTION_ID));
        long id = getDB().insert(TABLE_NAME, null, values);
        survey.setId(id);
        return id;
    }


    //	returns the number of items inserted
    //	if no items inserted 0 is returned
//	public static long create(long promotionId
//			,String promoCode,String salesPersonCode, JSONArray promoItemArray) throws JSONException{
//		int count = 0;
//		int size  = promoItemArray.length();
//		for(int i=0;i<size;i++){
//			SurveyPromotionItem survey = new SurveyPromotionItem(promotionId
//					,salesPersonCode, promoItemArray.getJSONObject(i));
//			if(TablePromotions.doesItemExist(promoCode, survey.itemCode)
//					&&survey.isValid()){
//				getDB().insert(TABLE_NAME, null, survey.getValues());
//				count++;
//			}
//				
//		}
//		return count;
//	}


    public static final void delete(long promoId) {
        getDB().delete(TableSurveyPromotionsItem.TABLE_NAME, TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID + "=" + promoId, null);
    }

//	public static final void delete(ElahDBHelper dbHelper,long promoId,String itemCode){
//		dbHelper.getWritableDatabase().delete(TableSurveyPromotionsItem.TABLE_NAME, TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID+"="+promoId
//				+" AND "+TableSurveyPromotionsItem.COLUMN_ITEM_CODE+"='"+itemCode+"'", null);
//	}

    public static final void delete(String idList) {
        getDB().delete(TableSurveyPromotionsItem.TABLE_NAME, TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID + " IN (?)", new String[]{idList});

    }


    public static JSONArray getSurveyAsJSON(boolean isNew, long promotionId) throws JSONException {
        Log.d("Log", "Population started completed for " + promotionId + "in promotionItem Survey operation");
        Cursor c = getDB().query(TableSurveyPromotionsItem.TABLE_NAME,
                new String[]{TableSurveyPromotionsItem.COLUMN_ID
                        , TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID
                        , TableSurveyPromotionsItem.COLUMN_ITEM_CODE
                        , TableSurveyPromotionsItem.COLUMN_ITEM_DESC
                        , TableSurveyPromotionsItem.COLUMN_ITEM_MARCHIO
                        , TableSurveyPromotionsItem.COLUMN_ITEM_LINEA
                        , TableSurveyPromotionsItem.COLUMN_ITEM_MACROFAMIGLIA
                        , TableSurveyPromotionsItem.COLUMN_SURVEY_VOLANTINO
                        , TableSurveyPromotionsItem.COLUMN_SURVEY_PREZZO
                        , TableSurveyPromotionsItem.COLUMN_UNIQUE_FIELD
                        , TableSurveyPromotionsItem.COLUMN_SURVEY_MODIFIED},
                TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID + "=" + promotionId, null, null, null, null);
        JSONArray array = new JSONArray();
        if (c != null) {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    SurveyPromotionItem survey = new SurveyPromotionItem(isNew, c);
                    array.put(survey.getAsJson());
                    c.moveToNext();
                }
            }
            c.close();

        }
        Log.d("Log", "Population started completed for " + promotionId + "in promotion Survey operation");
        return array;
    }

    public static int getCountForId(ElahDBHelper dbHelper, long promoid) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT COUNT(" + TableSurveyPromotionsItem.COLUMN_ID + ") FROM " + TableSurveyPromotionsItem.TABLE_NAME
                + " WHERE " + TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID + "=" + promoid, null);
        int count = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            c.close();
        }
        return count;
    }

    public static void convertDraftsTaglioToCombo(SQLiteDatabase xdb) {
        ArrayList<TPromotionItem> items = getTaglioPZDraftValues(xdb);
        if (!items.isEmpty()) {
            convertToComboValues(xdb, items);
        }
    }

    private static ArrayList<TPromotionItem> getTaglioPZDraftValues(SQLiteDatabase xdb) {
        String sql = "SELECT " + TableSurveyPromotionsItem.COLUMN_ID + "," + TableSurveyPromotionsItem.COLUMN_SURVEY_PREZZO + " FROM "
                + TableSurveyPromotionsItem.TABLE_NAME + " WHERE " + TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID + " IN ("
                + "SELECT " + TableSurveyPromotions.COLUMN_ID + " FROM " + TableSurveyPromotions.TABLE_NAME + " WHERE "
                + TableSurveyPromotions.COLUMN_SURVEY_ID + " IN ( SELECT " + TableSurvey.COLUMN_ID + " FROM " + TableSurvey.TABLE_NAME
                + " WHERE " + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_DRAFTS + "))";
        Cursor c = xdb.rawQuery(sql, null);
        ArrayList<TPromotionItem> tempItems = new ArrayList<TPromotionItem>();

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                tempItems.add(cursorToTempProItem(c));
                c.moveToNext();
            }
        }
        c.close();

        return tempItems;
    }

    private static void convertToComboValues(SQLiteDatabase xdb, ArrayList<TPromotionItem> items) {
        ContentValues values = new ContentValues();
        for (TPromotionItem tempItem : items) {
            String taglioPZ = Util.getComboBoxValue(tempItem.getTaglioPZ());
            values.put(TableSurveyPromotionsItem.COLUMN_SURVEY_PREZZO, Util.getIndexOf(Consts.ARRAY_PREZZO_PROMO, taglioPZ));
            xdb.update(TableSurveyPromotionsItem.TABLE_NAME, values, TableSurveyPromotionsItem.COLUMN_ID + "=" + tempItem.getId(), null);
        }
    }

    private static TPromotionItem cursorToTempProItem(Cursor c) {
        TPromotionItem item = new TPromotionItem();
        item.setId(c.getLong(c.getColumnIndex(TableSurveyPromotionsItem.COLUMN_ID)));
        item.setTaglioPZ(c.getString(c.getColumnIndex(TableSurveyPromotionsItem.COLUMN_SURVEY_PREZZO)));
        return item;
    }

    private static void insertOrUpdate(SurveyPromotionItem survey, ContentValues values) {
        long id = survey.id;
        if (survey.isValid()) {
            if (id == 0 || id == -1) {
                survey.id = getDB().insert(TABLE_NAME, null, values);
                Log.d(TAG, "Promotion item survey is created :" + survey.id);
            }
            getDB().update(TABLE_NAME, values, COLUMN_ID + "= " + survey.id, null);
            Log.d(TAG, "Promotion item survey id is :" + survey.id + " for item :" + survey.itemCode);
        } else if (id != 0 && id != -1) {
            getDB().delete(TABLE_NAME, COLUMN_ID + "=" + id, null);

            Log.d(TAG, "Promotion item survey id deleting :" + survey.id + " for item :" + survey.itemCode);
            survey.id = -1;

        }
    }

    public static void delete(SurveyPromotionItem promotionItem) {
        getDB().delete(TABLE_NAME, COLUMN_ID + "= " + promotionItem.id, null);

    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class SurveyPromotionItem {
        // ===========================================================
        // Constants
        // ===========================================================
        private static final ContentValues values = new ContentValues();


        public static final String KEY_ITEM_CODE = "PIC";
        public static final String KEY_ITEM_DESCRIPTION = "PITD";
        public static final String KEY_ITEM_MARCHIO = "PIM";
        public static final String KEY_ITEM_LINEA = "PIL";
        public static final String KEY_ITEM_MACROFAMIGLIA = "PIMF";
        public static final String KEY_SURVEY_VOLANTINO = "PISV";
        public static final String KEY_SURVEY_TAGLIO_PZ = "PISP";

        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        // ===========================================================
        // Fields
        // ===========================================================

        private long id;
        private long promotionId;
        private String itemCode;
        private String itemDesc;
        private String itemMarchio;
        private String itemLinea;
        private String itemMacroFamiglia;
        private int volantino;
        private int prezzo;
        private String uniqueId;
        private String surveyModified;
        private boolean isNew;

        // ===========================================================
        // Constructors
        // ===========================================================

        public SurveyPromotionItem(Cursor c) {
            this(true, c);
        }


        public SurveyPromotionItem(boolean isNew, Cursor c) {

            id = c.getLong(c.getColumnIndex(COLUMN_ID));
            promotionId = c.getLong(c.getColumnIndex(COLUMN_SURVEY_PROMOTION_ID));
            itemCode = c.getString(c.getColumnIndex(COLUMN_ITEM_CODE));

            //get itemModel from item table if the field empty
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


            itemMacroFamiglia = c.getString(c.getColumnIndex(COLUMN_ITEM_MACROFAMIGLIA));
            if (valueNullCheck(itemMacroFamiglia)) {
                if (itemsModel != null) {
                    if (itemsModel.getMacroFamiglia() == null) {
                        itemMacroFamiglia = "";
                    } else {
                        itemMacroFamiglia = itemsModel.getMacroFamiglia();
                    }
                } else {
                    itemMacroFamiglia = "";
                }
            }

            volantino = c.getInt(c.getColumnIndex(COLUMN_SURVEY_VOLANTINO));
            if (isNew)
                prezzo = c.getInt(c.getColumnIndex(COLUMN_SURVEY_PREZZO));
            else {
                String prezzoValue = c.getString(c.getColumnIndex(COLUMN_SURVEY_PREZZO));
                prezzo = Util.getIndexOf(Consts.ARRAY_PREZZO_PROMO, prezzoValue);
            }
            uniqueId = c.getString(c.getColumnIndex(COLUMN_UNIQUE_FIELD));
            surveyModified = c.getString(c.getColumnIndex(COLUMN_SURVEY_MODIFIED));
            if (surveyModified == null) {
                surveyModified = Util.getCurrentDateTime();
            }
            this.isNew = isNew;
            addPropertyChangeListener(changeListener);
        }

        private Boolean valueNullCheck(String item) {
            if (item == null) {
                return false;
            } else
                return true;
        }

        public SurveyPromotionItem(long promotionId, String salesPersonCode, JSONObject json) {
            this.promotionId = promotionId;
            itemCode = json.optString(KEY_ITEM_CODE);
            itemDesc = json.optString(KEY_ITEM_DESCRIPTION);
            itemMarchio = json.optString(KEY_ITEM_MARCHIO);
            itemLinea = json.optString(KEY_ITEM_LINEA);
            itemMacroFamiglia = json.optString(KEY_ITEM_MACROFAMIGLIA);
            volantino = json.optInt(KEY_SURVEY_VOLANTINO);
            prezzo = Util.getIndexOf(Consts.ARRAY_PREZZO_PROMO, json.optString(KEY_SURVEY_TAGLIO_PZ));
            uniqueId = Util.getUniqueId(salesPersonCode);
        }

        public SurveyPromotionItem(long promotionId, String salesPersonCode,
                                   Item item, Cursor c, boolean usesAlias) {
            updateModifiedTime();
            this.id = c.getLong(c.getColumnIndex(usesAlias ? ALIAS_ID : COLUMN_ID));
            this.promotionId = promotionId;
            this.uniqueId = Util.getUniqueId(salesPersonCode);
            this.itemCode = item.getCode();
            this.itemDesc = item.getDescription();
            this.itemMarchio = item.getItemMarchio();
            this.itemLinea = item.getItemLinea();
            this.itemMacroFamiglia = item.getItemMacrofamiglia();
            this.volantino = c.getInt(c.getColumnIndex(COLUMN_SURVEY_VOLANTINO));
            this.prezzo = c.getInt(c.getColumnIndex(COLUMN_SURVEY_PREZZO));
            addPropertyChangeListener(changeListener);

        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================

        public void setVolantino(long promotionId, int volantino) {
            this.promotionId = promotionId;
            int oldValue = this.volantino;
            this.volantino = volantino;
            pcs.firePropertyChange(COLUMN_SURVEY_VOLANTINO, oldValue, volantino);
        }

        public void setPrezzo(long promotionId, int prezzo) {
            this.promotionId = promotionId;
            int oldValue = this.prezzo;
            this.prezzo = prezzo;
            pcs.firePropertyChange(COLUMN_SURVEY_PREZZO, oldValue, prezzo);
        }

        public void setPromotionId(long promotionId) {
            this.promotionId = promotionId;
        }

        public int getVolantino() {
            return volantino;
        }

        public int getPrezzo() {
            return prezzo;
        }

        public long getPromotionId() {
            return promotionId;
        }

        public boolean isVolantinoValid(int volantino) {
            return volantino != 0 || prezzo != 0;
        }

        public boolean isPrezzoValid(int prezzo) {
            return volantino != 0 || prezzo != 0;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public String getItemCode() {
            return itemCode;
        }

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================


        // ===========================================================
        // Methods
        // ===========================================================

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.removePropertyChangeListener(listener);
        }


        public ContentValues getValues() {
            values.clear();
            values.put(TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID, promotionId);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_CODE, itemCode);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_DESC, itemDesc);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_MARCHIO, itemMarchio);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_LINEA, itemLinea);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_MARCHIO, itemMarchio);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_MACROFAMIGLIA, itemMacroFamiglia);
            values.put(TableSurveyPromotionsItem.COLUMN_SURVEY_VOLANTINO, volantino);
            values.put(TableSurveyPromotionsItem.COLUMN_SURVEY_PREZZO, prezzo);
            values.put(TableSurveyPromotionsItem.COLUMN_UNIQUE_FIELD, uniqueId);
            values.put(TableSurveyPromotionsItem.COLUMN_SURVEY_MODIFIED, surveyModified);
            return values;
        }

        public void updateBaseValues() {
            updateModifiedTime();
            values.put(TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID, promotionId);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_CODE, itemCode);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_DESC, itemDesc);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_LINEA, itemLinea);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_MARCHIO, itemMarchio);
            values.put(TableSurveyPromotionsItem.COLUMN_ITEM_MACROFAMIGLIA, itemMacroFamiglia);
            values.put(TableSurveyPromotionsItem.COLUMN_UNIQUE_FIELD, uniqueId);
            values.put(TableSurveyPromotionsItem.COLUMN_SURVEY_MODIFIED, surveyModified);
        }


        private void updateModifiedTime() {
            surveyModified = Util.getCurrentDateTime();
        }


        public boolean isValid() {
            return volantino != 0 || prezzo != 0;
        }

        public JSONObject getAsJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(Consts.JSON_KEY_ID, id);
            json.put(COLUMN_SURVEY_PROMOTION_ID, promotionId);
            json.put(COLUMN_ITEM_CODE, itemCode);
            json.put(COLUMN_ITEM_DESC, itemDesc);
            json.put(COLUMN_ITEM_MARCHIO, itemMarchio);
            json.put(COLUMN_ITEM_LINEA, itemLinea);
            json.put(COLUMN_ITEM_MACROFAMIGLIA, itemMacroFamiglia);
            json.put(COLUMN_SURVEY_VOLANTINO, volantino);
            json.put(COLUMN_SURVEY_PREZZO, formatStringToJSON(Consts.ARRAY_PREZZO_PROMO[prezzo]));
            json.put(COLUMN_UNIQUE_FIELD, uniqueId);
            json.put(COLUMN_SURVEY_MODIFIED, surveyModified);
            return json;
        }

        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================

        PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                updateModifiedTime();
                values.clear();
                if (event.getPropertyName().equals(COLUMN_SURVEY_PREZZO)) {
                    values.put(COLUMN_SURVEY_PREZZO, prezzo);
                } else if (event.getPropertyName().equals(COLUMN_SURVEY_VOLANTINO)) {
                    values.put(COLUMN_SURVEY_VOLANTINO, volantino);
                }

                if (id == -1 || id == 0)
                    updateBaseValues();
                values.put(COLUMN_SURVEY_MODIFIED, surveyModified);
                insertOrUpdate(SurveyPromotionItem.this, values);
            }
        };

        public void clearValues() {
            values.clear();
        }


    }

    public static void deleteCorruptIds() {
        getDB().delete(TABLE_NAME, COLUMN_SURVEY_PROMOTION_ID + "=0", null);
    }


}
