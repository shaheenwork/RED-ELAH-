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
import android.text.TextUtils;
//import android.util.Log;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TablePromotions.Promotion;
import com.eteam.dufour.database.tables.TableSurveyPromotionsItem.SurveyPromotionItem;
import com.eteam.dufour.viewmodel.ModelSurveyPromotionItems;
import com.eteam.dufour.viewmodel.ModelSurveyPromotions;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class TableSurveyPromotions extends DBTable {
    // ===========================================================
    // Constants
    // ===========================================================

    private static final String TAG = TablePromotions.class.getName();

    public static final String KEY_ARRAY_NAME = "PROMO";

    public static final String KEY_JSON_ARRAYNAME = "promotions";

    public static final String TABLE_NAME = "survey_promotions";

    public static final String ALIAS_ID = "alias_promotions_id";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SURVEY_ID = "Survey_Id";
    public static final String COLUMN_PROMOTION_CODE = "Promotion_Code";
    public static final String COLUMN_PROMOTION_SELL_IN = "Promotion_Sell_In";
    public static final String COLUMN_PROMOTION_SELL_OUT = "Promotion_Sell_Out";
    public static final String COLUMN_SURVEY_VISIBILITY = "Survey_Visibility";
    public static final String COLUMN_FLAG = "flag";
    public static final String COLUMN_UNIQUE_FIELD = "Unique_Field";
    public static final String COLUMN_SURVEY_MODIFY = "Survey_Promo_Modify";

    public static final int ENTRY_EXISTS = 1;
    public static final int ENTRY_NOT_EXISTS = 0;

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SURVEY_ID + " INTEGER, "
            + COLUMN_PROMOTION_CODE + " TEXT, "
            + COLUMN_PROMOTION_SELL_IN + " TEXT, "
            + COLUMN_PROMOTION_SELL_OUT + " TEXT, "
            + COLUMN_SURVEY_VISIBILITY + " INTEGER, "
            + COLUMN_FLAG + " INTEGER, "
            + COLUMN_UNIQUE_FIELD + " TEXT, "
            + COLUMN_SURVEY_MODIFY + " TEXT, "
            + " FOREIGN KEY(" + COLUMN_SURVEY_ID + ") REFERENCES "
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

    public static long create(ElahDBHelper dbHelper, long surveyId, String promoCode,
                              String sellIn, String sellOut, int visibility, int flag,
                              String salesPerson) {
        ContentValues values = new ContentValues();
        values.put(TableSurveyPromotions.COLUMN_SURVEY_ID, surveyId);
        if (promoCode != null)
            values.put(TableSurveyPromotions.COLUMN_PROMOTION_CODE, promoCode);
        if (sellIn != null)
            values.put(TableSurveyPromotions.COLUMN_PROMOTION_SELL_IN, sellIn);
        if (sellOut != null)
            values.put(TableSurveyPromotions.COLUMN_PROMOTION_SELL_OUT, sellOut);
        values.put(TableSurveyPromotions.COLUMN_SURVEY_VISIBILITY, visibility);
        values.put(TableSurveyPromotions.COLUMN_FLAG, flag);
        values.put(TableSurveyPromotions.COLUMN_UNIQUE_FIELD, Util.getUniqueId(salesPerson));
        values.put(TableSurveyPromotions.COLUMN_SURVEY_MODIFY, Util.getCurrentDateTime());
        return dbHelper.getWritableDatabase().insert(TableSurveyPromotions.TABLE_NAME, null, values);
    }

    public static long create(ElahDBHelper dbHelper, long surveyId, String promoCode, String salesPersonCode) {
        ContentValues values = new ContentValues();
        values.put(TableSurveyPromotions.COLUMN_SURVEY_ID, surveyId);
        values.put(TableSurveyPromotions.COLUMN_PROMOTION_CODE, promoCode);
        values.put(TableSurveyPromotions.COLUMN_UNIQUE_FIELD, Util.getUniqueId(salesPersonCode));
        values.put(TableSurveyPromotions.COLUMN_SURVEY_MODIFY, Util.getCurrentDateTime());
        return dbHelper.getWritableDatabase().insert(TableSurveyPromotions.TABLE_NAME, null, values);
    }


    public static void create(long surveyId, String salesPersonCode, JSONArray proddotiArray) throws JSONException {
        SQLiteDatabase db = getDB();
        getDB().beginTransaction();
        for (int i = 0; i < proddotiArray.length(); i++) {
            ModelSurveyPromotions modelSurvey = new ModelSurveyPromotions(surveyId, salesPersonCode, proddotiArray.getJSONObject(i));
            SurveyPromotion promotion = modelSurvey.getSurvey();

//				Log.i(TAG, "Promotion created with id "+promotion.isValid());
            if (promotion.isValid()) {
                createNew(promotion);
//				Log.i(TAG, "Promotion created with id "+promotion.getId());
                ArrayList<ModelSurveyPromotionItems> items = promotion.getItems();
                int itemSize = items.size();
                for (int j = 0; j < itemSize; j++) {
                    SurveyPromotionItem surveyItem = items.get(j).getSurvey();
                    TableSurveyPromotionsItem.createNew(surveyItem);
//					Log.i(TAG, "Creating promotion items for promo id : "+surveyItem.getPromotionId());
                }
            }

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public static void updateFlag(long id, int flag) {
        ContentValues values = new ContentValues();
        values.put(TableSurveyPromotions.COLUMN_FLAG, flag);
        values.put(TableSurveyPromotions.COLUMN_SURVEY_MODIFY, Util.getCurrentDateTime());
        getDB().update(TableSurveyPromotions.TABLE_NAME, values, TableSurveyPromotions.COLUMN_ID + "=" + id, null);
    }

    public static void updateFlag(ElahDBHelper dbHelper, long surveyId, String promoCode, int flag) {
        ContentValues values = new ContentValues();
        values.put(TableSurveyPromotions.COLUMN_FLAG, flag);
        values.put(TableSurveyPromotions.COLUMN_SURVEY_MODIFY, Util.getCurrentDateTime());
        dbHelper.getWritableDatabase().update(TableSurveyPromotions.TABLE_NAME, values, TableSurveyPromotions.COLUMN_SURVEY_ID + "=" + surveyId
                + " AND " + TableSurveyPromotions.COLUMN_PROMOTION_CODE + "='" + promoCode + "'", null);
    }

    public static void delete(SurveyPromotion survey) {
//		Log.i(TAG, "Deleting promotion at id :"+survey.id);
        getDB().delete(TABLE_NAME, COLUMN_ID + " = " + survey.id, null);
        TableSurveyPromotionsItem.delete(survey.id);
        survey.setId(-1);
    }

    public static void delete(long surveyId) {
        Cursor c = getDB().query(TableSurveyPromotions.TABLE_NAME, new String[]{TableSurveyPromotions.COLUMN_ID},
                TableSurveyPromotions.COLUMN_SURVEY_ID + "=" + surveyId, null, null, null, null);
        String itemIdToDelete = "";
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                long id = c.getLong(c.getColumnIndex(TableSurveyPromotions.COLUMN_ID));
                itemIdToDelete += id + ",";
                c.moveToNext();
            }
        }
        c.close();
//		Log.i(TAG, "Deleting promotion at ids :"+itemIdToDelete);
        if (!TextUtils.isEmpty(itemIdToDelete)) {
            if (itemIdToDelete.endsWith(",")) {
                itemIdToDelete = itemIdToDelete.substring(0, itemIdToDelete.length() - 1);
            }
            TableSurveyPromotionsItem.delete(itemIdToDelete);
        }


        getDB().delete(TABLE_NAME, COLUMN_SURVEY_ID + "=" + surveyId, null);

    }

    public static void delete(String idList) {
        Cursor c = getDB().query(TableSurveyPromotions.TABLE_NAME,
                new String[]{TableSurveyPromotions.COLUMN_ID},
                TableSurveyPromotions.COLUMN_SURVEY_ID + " IN (?)",
                new String[]{idList}, null, null, null);
        String itemIdToDelete = "";
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                itemIdToDelete += c.getLong(c.getColumnIndex(TableSurveyPromotions.COLUMN_ID)) + ",";
                c.moveToNext();
            }
        }
        c.close();
//		Log.i(TAG, "Deleting promotion at ids :"+itemIdToDelete);
        if (!TextUtils.isEmpty(itemIdToDelete)) {
            if (itemIdToDelete.endsWith(",")) {
                itemIdToDelete = itemIdToDelete.substring(0, itemIdToDelete.length() - 1);
            }
            TableSurveyPromotionsItem.delete(itemIdToDelete);
        }


        getDB().delete(TABLE_NAME, COLUMN_SURVEY_ID + " IN (?)", new String[]{idList});

    }

    public static void delete(long surveyId, String promoCode) {
        String selection = TableSurveyPromotions.COLUMN_SURVEY_ID + "=" + surveyId
                + " AND " + TableSurveyPromotions.COLUMN_PROMOTION_CODE + "= ?";
        String[] selectionArg = new String[]{promoCode};

        Cursor c = getDB().query(TableSurveyPromotions.TABLE_NAME, new String[]{TableSurveyPromotions.COLUMN_ID},
                selection, selectionArg, null, null, null);
        long promoId = -1;
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                promoId = c.getInt(c.getColumnIndex(TableSurveyPromotions.COLUMN_ID));

                c.moveToNext();
            }

        }
        c.close();
//		Log.i(TAG, "Deleting promotion at id :"+promoId);
        TableSurveyPromotionsItem.delete(promoId);
        getDB().delete(TABLE_NAME, selection, selectionArg);

    }


    public static long createPromoSurvey(ElahDBHelper dbHelper, long surveyId, String promoCode, String salesPersonCode) {
        Cursor c = dbHelper.getReadableDatabase().query(TableSurveyPromotions.TABLE_NAME,
                new String[]{TableSurveyPromotions.COLUMN_ID}, TableSurveyPromotions.COLUMN_SURVEY_ID + "=" + surveyId
                        + " AND " + TableSurveyPromotions.COLUMN_PROMOTION_CODE + "='" + promoCode + "'",
                null, null, null, null);

        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(TableSurveyPromotions.COLUMN_ID));
            c.close();
            return id;
        }
        c.close();
        return create(dbHelper, surveyId, promoCode, salesPersonCode);
    }

    public static void insertOrUpdate(SurveyPromotion survey, ContentValues values) {
        long id = survey.id;
        if (survey.isValid()) {
            if (id == 0 || id == -1) {
                survey.setId(getDB().insert(TABLE_NAME, null, values));
            }
            getDB().update(TABLE_NAME, values, COLUMN_ID + "= " + survey.id, null);
        } else if (id != 0 && id != -1) {
            delete(survey);

        }
    }

    public static long createNew(SurveyPromotion survey) {

        long id = getDB().insert(TABLE_NAME, null, survey.getValues());
        survey.setId(id);
        return id;
    }

    public static JSONArray getSurveyAsJSON(boolean isNewSurvey, long surveyId) throws JSONException {
//		Log.d("Log", "Population started for "+surveyId+"in Prodotti Survey operation");
        Cursor c = getDB().query(TableSurveyPromotions.TABLE_NAME,
                new String[]{TableSurveyPromotions.COLUMN_ID,
                        TableSurveyPromotions.COLUMN_SURVEY_ID
                        , TableSurveyPromotions.COLUMN_PROMOTION_CODE
                        , TableSurveyPromotions.COLUMN_PROMOTION_SELL_IN
                        , TableSurveyPromotions.COLUMN_PROMOTION_SELL_OUT
                        , TableSurveyPromotions.COLUMN_SURVEY_VISIBILITY
                        , TableSurveyPromotions.COLUMN_UNIQUE_FIELD
                        , TableSurveyPromotions.COLUMN_SURVEY_MODIFY, COLUMN_FLAG
                },
                TableSurveyPromotions.COLUMN_SURVEY_ID + "=" + surveyId, null, null, null, null);
        JSONArray array = new JSONArray();

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                SurveyPromotion survey = new SurveyPromotion(c);
                JSONArray pItem = TableSurveyPromotionsItem.getSurveyAsJSON(isNewSurvey, survey.id);
                JSONObject surveyJson = survey.getAsJson();
                if (surveyJson != null) {
                    surveyJson.put(TableSurveyPromotionsItem.KEY_JSON_ARRAYNAME, pItem);
                    array.put(surveyJson);
                }
                c.moveToNext();
            }
        }
        c.close();


//		Log.d("Log", "Population completed for "+surveyId+"in promotion Survey operation");
        return array;

    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class SurveyPromotion {
        // ===========================================================
        // Constants
        // ===========================================================

        private static final ContentValues values = new ContentValues();

        public static final String KEY_PROMO_CODE = "PC";
        public static final String KEY_SELL_IN = "PSI";
        public static final String KEY_SELL_OUT = "PSO";
        public static final String KEY_SURVEY_VISIBILTY = "PSV";

        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        // ===========================================================
        // Fields
        // ===========================================================
        private long id;
        private long surveyId;
        private String promoCode;
        private String sellIn;
        private String sellOut;
        private int visibility;
        private int flag;
        private String uniqueId;
        private String surveyModified;

        private ArrayList<ModelSurveyPromotionItems> items;

        // ===========================================================
        // Constructors
        // ===========================================================
        public SurveyPromotion(long surveyId, String salesPersonCode, JSONObject json) throws JSONException {
            this.surveyId = surveyId;
            promoCode = json.optString(KEY_PROMO_CODE);
            sellIn = json.optString(KEY_SELL_IN);
            sellOut = json.optString(KEY_SELL_OUT);
            visibility = json.optInt(KEY_SURVEY_VISIBILTY);
            uniqueId = Util.getUniqueId(salesPersonCode);
            items = new ArrayList<ModelSurveyPromotionItems>();

            JSONArray promotionItem = json.optJSONArray(TableSurveyPromotionsItem.KEY_ARRAY_NAME);
            int size = promotionItem.length();
            for (int i = 0; i < size; i++) {
                ModelSurveyPromotionItems modelItem = new ModelSurveyPromotionItems(id, salesPersonCode, promotionItem.getJSONObject(i));
                SurveyPromotionItem surveyItem = modelItem.getSurvey();
                if (surveyItem.isValid()
                        && TablePromotions.doesItemExist(promoCode, surveyItem.getItemCode())) {
                    items.add(modelItem);
                }

            }
            addPropertyChangeListener(changeListener);

        }

        public SurveyPromotion(Cursor c) {

            this.id = c.getLong(c.getColumnIndex(COLUMN_ID));
            this.surveyId = c.getLong(c.getColumnIndex(COLUMN_SURVEY_ID));
            this.promoCode = c.getString(c.getColumnIndex(COLUMN_PROMOTION_CODE));
            this.sellIn = c.getString(c.getColumnIndex(COLUMN_PROMOTION_SELL_IN));
            this.sellOut = c.getString(c.getColumnIndex(COLUMN_PROMOTION_SELL_OUT));
            this.visibility = c.getInt(c.getColumnIndex(COLUMN_SURVEY_VISIBILITY));
            this.uniqueId = c.getString(c.getColumnIndex(COLUMN_UNIQUE_FIELD));
            this.surveyModified = c.getString(c.getColumnIndex(COLUMN_SURVEY_MODIFY));
            this.flag = c.getInt(c.getColumnIndex(COLUMN_FLAG));
            addPropertyChangeListener(changeListener);
            items = new ArrayList<ModelSurveyPromotionItems>();
        }


        public SurveyPromotion(long surveyId, String customerNumber, String salesPersonCode,
                               Promotion promotion, Cursor c, boolean usesAlias) {
            if (usesAlias)
                this.id = c.getLong(c.getColumnIndex(COLUMN_ID));
            else
                this.id = c.getLong(c.getColumnIndex(COLUMN_ID));
            this.surveyId = surveyId;
            this.promoCode = promotion.getPromoCode();
            this.sellIn = promotion.getSellOutInzio();
            this.sellOut = promotion.getSellOutFine();

            this.visibility = c.getInt(c.getColumnIndex(COLUMN_SURVEY_VISIBILITY));
            this.flag = c.getInt(c.getColumnIndex(COLUMN_FLAG));
            if (this.id == 0 || id == -1) {
                updateModifiedTime();
                this.uniqueId = Util.getUniqueId(salesPersonCode);
            } else {
                this.surveyModified = c.getString(c.getColumnIndex(COLUMN_SURVEY_MODIFY));
                this.uniqueId = c.getString(c.getColumnIndex(COLUMN_UNIQUE_FIELD));
            }
            items = new ArrayList<ModelSurveyPromotionItems>();
//			Log.i(TAG, "Got id of promotion as "+id);
            TableItems.populatePromotionItemsSurvey(id, customerNumber, promotion.promoCode, salesPersonCode, items);
            addPropertyChangeListener(changeListener);

        }

        public ContentValues getValues() {
            updateModifiedTime();
            values.clear();
            values.put(COLUMN_PROMOTION_CODE, promoCode);
            values.put(COLUMN_SURVEY_ID, surveyId);
            values.put(COLUMN_PROMOTION_SELL_IN, sellIn);
            values.put(COLUMN_PROMOTION_SELL_OUT, sellOut);
            values.put(COLUMN_SURVEY_VISIBILITY, visibility);
            values.put(COLUMN_FLAG, flag);
            values.put(COLUMN_UNIQUE_FIELD, uniqueId);
            values.put(COLUMN_SURVEY_MODIFY, surveyModified);
            return values;
        }

        public boolean isValid() {

            if (visibility != 0)
                return true;
            for (ModelSurveyPromotionItems model : items) {
                if (model.getSurvey().isValid()) {
                    return true;
                }
            }
            return false;

        }


        // ===========================================================
        // Getter & Setter
        // ===========================================================
        public ArrayList<ModelSurveyPromotionItems> getItems() {
            return items;
        }

        public long getId() {
            return id;
        }

        public int getVisibility() {
            return visibility;
        }

        public long getSurveyId() {
            return surveyId;
        }

        public String getPromoCode() {
            return promoCode;
        }

        public void setVisibility(int visibility) {
            int oldValue = this.visibility;
            this.visibility = visibility;
            pcs.firePropertyChange(COLUMN_SURVEY_VISIBILITY, oldValue, visibility);
        }

        public void setId(long id) {
            long oldValue = this.id;
            this.id = id;
            pcs.firePropertyChange(COLUMN_ID, oldValue, id);
        }

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        // ===========================================================
        // Methods
        // ===========================================================

        PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {


                String propertyName = event.getPropertyName();
                if (propertyName.equals(COLUMN_ID)) {
                    for (ModelSurveyPromotionItems item
                            : items) {
                        item.getSurvey().setPromotionId(id);

                    }
//					Log.d(TAG, "Promotion id created with children "+id);
                    return;
                } else if (propertyName.equals(COLUMN_SURVEY_VISIBILITY)) {
                    updateModifiedTime();
                    values.clear();
                    values.put(COLUMN_SURVEY_VISIBILITY, visibility);
                }
                if (id == 0 || id == -1) {
                    updateBaseValues();
                }

                values.put(COLUMN_SURVEY_MODIFY, surveyModified);
                insertOrUpdate(SurveyPromotion.this, values);
            }
        };

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.removePropertyChangeListener(listener);
        }


        public JSONObject getAsJson()
                throws JSONException {
            JSONObject json = new JSONObject();
            json.put(Consts.JSON_KEY_ID, id);
            json.put(COLUMN_SURVEY_ID, surveyId);
            json.put(COLUMN_PROMOTION_CODE, promoCode);
            json.put(COLUMN_PROMOTION_SELL_IN, sellIn);
            json.put(COLUMN_PROMOTION_SELL_OUT, sellOut);
            json.put(COLUMN_SURVEY_VISIBILITY, visibility);
            json.put(COLUMN_UNIQUE_FIELD, uniqueId);
            json.put(COLUMN_SURVEY_MODIFY, surveyModified);
            return json;
        }

        public void clearValues() {
            values.clear();
        }

        public void updateBaseValues() {
            updateModifiedTime();
            values.put(COLUMN_PROMOTION_CODE, promoCode);
            values.put(COLUMN_PROMOTION_SELL_IN, sellIn);
            values.put(COLUMN_PROMOTION_SELL_OUT, sellOut);
            values.put(COLUMN_UNIQUE_FIELD, uniqueId);
            values.put(COLUMN_SURVEY_ID, surveyId);
            values.put(COLUMN_SURVEY_MODIFY, surveyModified);
        }


        private void updateModifiedTime() {
            surveyModified = Util.getCurrentDateTime();
        }

        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
    }
}
