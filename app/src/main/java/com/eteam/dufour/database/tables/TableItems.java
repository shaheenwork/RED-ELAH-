package com.eteam.dufour.database.tables;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.eteam.db.DBTable;
import com.eteam.dufour.database.DBFilterArg;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.viewmodel.ModelProductForDashboard;
import com.eteam.dufour.viewmodel.ModelSurveyAssortimento;
import com.eteam.dufour.viewmodel.ModelSurveyPromotionItems;
import com.eteam.model.ItemsModel;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class TableItems extends DBTable {
    // ===========================================================
    // Constants
    // ===========================================================

    private static final String TAG = TableItems.class.getName();


    public static final String MARCHIO_UNSELECTED = "Marchio";
    public static final String LINEA_UNSELECTED = "Linea";
    public static final String MACROFAMIGLIA_UNSELECTED = "Macrofamiglia";

    public static final String TABLE_NAME = "items";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NO = "No_";
    public static final String COLUMN_ASSORTMENTISTATUS = "AssortmentiStatus";
    public static final String COLUMN_ASSORTMENTICUSTCODES = "AssortmentiCustCodes";
    public static final String COLUMN_MARCHIO = "Marchio";
    public static final String COLUMN_LINEA = "Linea";
    public static final String COLUMN_MACROFAMIGLIA = "MacroFamiglia";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_BASE_UNIT = "Base_Unit_of_Measure";
    public static final String COLOUMN_EAN = "EAN";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NO + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_BASE_UNIT + " TEXT, "
            + COLUMN_MARCHIO + " TEXT, "
            + COLUMN_LINEA + " TEXT, "
            + COLUMN_MACROFAMIGLIA + " TEXT, "
            + COLUMN_ASSORTMENTISTATUS + " TEXT, "
            + COLUMN_ASSORTMENTICUSTCODES + " TEXT)";



    public enum AssortimentoToLoad {ASSORTIMENTO, COMPLETE, PRODOTTI_RILEVATI}

    ;

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


    public static final void populateLinea(ArrayList<String> listLinea, String marchio) {
        populateLinea(listLinea, marchio, null);
    }

    public static final void populateLinea(ArrayList<String> listLinea, String marchio, String assortimentoStatus) {
        String selection = COLUMN_MARCHIO + "= ?";
        String[] selectionArg = null;
        if (!TextUtils.isEmpty(assortimentoStatus)) {
            selection = selection + " AND " + COLUMN_ASSORTMENTISTATUS + " = ?";
            selectionArg = new String[]{marchio, assortimentoStatus};
        } else if (!TextUtils.isEmpty(marchio)) {
            selectionArg = new String[]{marchio};
        } else {
            selection = null;
            selectionArg = null;
        }
        listLinea.clear();
        listLinea.add(LINEA_UNSELECTED);
        Cursor c = getDB().query(true, TableItems.TABLE_NAME, new String[]{TableItems.COLUMN_LINEA},
                selection, selectionArg, null, null, TableItems.COLUMN_LINEA, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                if (!TextUtils.isEmpty(c.getString(c.getColumnIndex(TableItems.COLUMN_LINEA)))){
                listLinea.add(c.getString(c.getColumnIndex(TableItems.COLUMN_LINEA)));
                }
                c.moveToNext();
            }
        }
        c.close();

    }

    public static final void populateMacrofamiglia(ArrayList<String> listMacrofamiglia,
                                                   String marchio, String linea) {
        populateMacrofamiglia(listMacrofamiglia, marchio, linea, null);

    }

    public static final void populateMacrofamiglia(ArrayList<String> listMacrofamiglia,
                                                   String marchio, String linea, String assortimentoStatus) {
        String selection = COLUMN_MARCHIO + "= ? AND " + COLUMN_LINEA + "= ?";
        String[] selectionArg = null;
        if (!TextUtils.isEmpty(assortimentoStatus)) {
            selection = selection + " AND " + COLUMN_ASSORTMENTISTATUS + " = ?";
            selectionArg = new String[]{marchio, linea, assortimentoStatus};
        } else {
            selectionArg = new String[]{marchio, linea};
        }


        listMacrofamiglia.clear();
        listMacrofamiglia.add(MACROFAMIGLIA_UNSELECTED);
        Cursor c = getDB().query(true, TableItems.TABLE_NAME, new String[]{TableItems.COLUMN_MACROFAMIGLIA},
                selection, selectionArg, null, null, TableItems.COLUMN_MACROFAMIGLIA, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                listMacrofamiglia.add(c.getString(c.getColumnIndex(TableItems.COLUMN_MACROFAMIGLIA)));
                c.moveToNext();
            }
        }
        c.close();

    }

    public static void populateMarchio(ArrayList<String> listMarchio) {
        populateMarchio(listMarchio, null);
    }

    public static void populateMarchio(ArrayList<String> listMarchio, String assortimentoStatus) {
        String selection = null;
        String[] selectionArg = null;

        if (!TextUtils.isEmpty(assortimentoStatus)) {
            selection = COLUMN_ASSORTMENTISTATUS + "=?";
            selectionArg = new String[]{assortimentoStatus};
        }

        listMarchio.clear();
        listMarchio.add(MARCHIO_UNSELECTED);
        Cursor c = getDB().query(true, TableItems.TABLE_NAME, new String[]{TableItems.COLUMN_MARCHIO}
                , selection, selectionArg, null, null, TableItems.COLUMN_MARCHIO, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                listMarchio.add(c.getString(c.getColumnIndex(TableItems.COLUMN_MARCHIO)));
                c.moveToNext();
            }
        }
        c.close();
    }


    public static final void populateItems(String customerNumber, String promocode, ArrayList<BaseItem> items) {
        items.clear();
        String query = "SELECT " + TABLE_NAME + "." + COLUMN_ID
                + "," + TABLE_NAME + "." + COLUMN_NO
                + "," + COLUMN_DESCRIPTION
                + " FROM " + TableItems.TABLE_NAME
                + " JOIN " + TablePromotions.TABLE_NAME
                + " WHERE " + TableItems.TABLE_NAME + "."
                + TableItems.COLUMN_NO + "=" + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_PROMO_CODE + "= ? AND " + TablePromotions.COLUMN_CUSTOMER_NO + "=?";
        Cursor c = getDB().rawQuery(query, new String[]{promocode, customerNumber});

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                items.add(new BaseItem(c));
                c.moveToNext();
            }
        }
        c.close();
    }

    public static final ItemsModel populateParticulrItem(String itemCode) {
        ItemsModel item = new ItemsModel();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_NO + "=?";
        Cursor cursor = getDB().rawQuery(query, new String[]{itemCode});
        if (cursor.moveToFirst()) {
            do {
                item.set_id(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                item.setNo_(cursor.getString(cursor.getColumnIndex(COLUMN_NO)));
                item.setAssortmentiStatus(cursor.getString(cursor.getColumnIndex(COLUMN_ASSORTMENTISTATUS)));
                item.setAssortmentiCustCodes(cursor.getString(cursor.getColumnIndex(COLUMN_ASSORTMENTICUSTCODES)));
                item.setMarchio(cursor.getString(cursor.getColumnIndex(COLUMN_MARCHIO)));
                item.setLinea(cursor.getString(cursor.getColumnIndex(COLUMN_LINEA)));
                item.setMacroFamiglia(cursor.getString(cursor.getColumnIndex(COLUMN_MACROFAMIGLIA)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                item.setBase_Unit_of_Measure(cursor.getString(cursor.getColumnIndex(COLUMN_BASE_UNIT)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return item;
    }

    public static final void populatePromotionItemsSurvey(long promotionId, String customerNumber, String promocode
            , String salesPersonCode, ArrayList<ModelSurveyPromotionItems> surveys) {
        surveys.clear();
        String query = "SELECT " + TABLE_NAME + "." + COLUMN_ID
                + "," + TABLE_NAME + "." + COLUMN_NO
                + "," + TABLE_NAME + "." + COLUMN_MARCHIO
                + "," + TABLE_NAME + "." + COLUMN_LINEA
                + "," + TABLE_NAME + "." + COLUMN_MACROFAMIGLIA
                + "," + TABLE_NAME + "." + COLUMN_DESCRIPTION

                + "," + TableSurveyPromotionsItem.TABLE_NAME + "." + TableSurveyPromotionsItem.COLUMN_ID + " AS " + TableSurveyPromotionsItem.ALIAS_ID
                + "," + TableSurveyPromotionsItem.TABLE_NAME + "." + TableSurveyPromotionsItem.COLUMN_SURVEY_VOLANTINO
                + "," + TableSurveyPromotionsItem.TABLE_NAME + "." + TableSurveyPromotionsItem.COLUMN_SURVEY_PREZZO
                + " FROM " + TableItems.TABLE_NAME
                + " JOIN " + TablePromotions.TABLE_NAME
                + " LEFT JOIN " + TableSurveyPromotionsItem.TABLE_NAME + " ON "
                + TABLE_NAME + "." + COLUMN_NO + "="
                + TableSurveyPromotionsItem.TABLE_NAME + "." + TableSurveyPromotionsItem.COLUMN_ITEM_CODE
                + " AND "
                + TableSurveyPromotionsItem.TABLE_NAME + "." + TableSurveyPromotionsItem.COLUMN_SURVEY_PROMOTION_ID + "=" + promotionId
                + " WHERE " + TableItems.TABLE_NAME + "."
                + TableItems.COLUMN_NO + "=" + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_PROMO_CODE + "= ? AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?";
        ;
        Cursor c = getDB().rawQuery(query, new String[]{promocode, customerNumber});
        Log.d(TAG, "Query is " + query);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                surveys.add(new ModelSurveyPromotionItems(promotionId, salesPersonCode, c, true));
                c.moveToNext();
            }
        }
        c.close();
    }

//	private static final ItemDetails cursorToItemDetails(Cursor c){
//		ItemDetails items = new ItemDetails();
//		items.description = c.getString(c.getColumnIndex(TableItems.COLUMN_DESCRIPTION));
//		items.item_code = c.getString(c.getColumnIndex(TableItems.COLUMN_NO));
//		return items;
//	}

    public static final boolean doesItemExist(String itemCode) {
        boolean stat = false;
        Cursor c = getDB().query(TableItems.TABLE_NAME
                , new String[]{TableItems.COLUMN_ID}, TableItems.COLUMN_NO + "=?",
                new String[]{itemCode}, null, null, null);

        stat = c.moveToFirst();

        c.close();

        return stat;
    }

//	public static Item getItem(String itemCode){
//		Cursor c = getDB().query(TableItems.TABLE_NAME, new String[]{TableItems.COLUMN_DESCRIPTION,
//				TableItems.COLUMN_LINEA,TableItems.COLUMN_MARCHIO,TableItems.COLUMN_MACROFAMIGLIA},
//				TableItems.COLUMN_NO+"='"+itemCode+"'", null, null, null, null);
//		Item item = null;
//		
//		if(c.moveToFirst()){;
//			item = new Item(itemCode,c);
//		}
//		c.close();
//		
//		return item;
//	}

//	public static SProdotti getSProductDetails(ElahDBHelper dbHelper,String itemCode){
//		Cursor c = dbHelper.getReadableDatabase().query(TableItems.TABLE_NAME, new String[]{TableItems.COLUMN_DESCRIPTION,
//				TableItems.COLUMN_LINEA,TableItems.COLUMN_MARCHIO,TableItems.COLUMN_MACROFAMIGLIA},
//				TableItems.COLUMN_NO+"='"+itemCode+"'", null, null, null, null);
//		SProdotti product = null;
//		
//		if(c.moveToFirst()){
//			product = cursorToSProdotti(itemCode,c);
//		}
//		c.close();
//		
//		return product;
//	}

//	public static IProdotti cursorToProdotti(Cursor c){
//		IProdotti prodotti = new IProdotti();
//		prodotti.setItemDescription(c.getString(c.getColumnIndex(TableItems.COLUMN_DESCRIPTION)));
//		prodotti.setItemLinea(c.getString(c.getColumnIndex(TableItems.COLUMN_LINEA)));
//		prodotti.setItemMacrofamiglia(c.getString(c.getColumnIndex(TableItems.COLUMN_MACROFAMIGLIA)));
//		prodotti.setItemMarchio(c.getString(c.getColumnIndex(TableItems.COLUMN_MARCHIO)));
//		return prodotti;
//	}

//	public static SProdotti cursorToSProdotti(Cursor c){
//		SProdotti prodotti = new SProdotti();
//		prodotti.setItemDescription(c.getString(c.getColumnIndex(TableItems.COLUMN_DESCRIPTION)));
//		prodotti.setItemLinea(c.getString(c.getColumnIndex(TableItems.COLUMN_LINEA)));
//		prodotti.setItemMacrofamiglia(c.getString(c.getColumnIndex(TableItems.COLUMN_MACROFAMIGLIA)));
//		prodotti.setItemMarchio(c.getString(c.getColumnIndex(TableItems.COLUMN_MARCHIO)));
//		return prodotti;
//	}

    public static int getAssortimentoTotalCount(long survey_id, String customerCode) {
        Cursor c = getDB().rawQuery("SELECT COUNT(1) FROM (" +
                "			SELECT " + COLUMN_NO + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON "
                + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableSurveyAssortimento.TABLE_NAME + " ON "
                + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO + "="
                + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + survey_id
                + " WHERE " + TableItems.COLUMN_ASSORTMENTISTATUS + "='1'"
                + " AND " + TableItems.COLUMN_ASSORTMENTICUSTCODES + " LIKE ?"
                + " GROUP BY " + TableItems.COLUMN_NO + ")", new String[]{customerCode, "%[" + customerCode + "]%"});
        int count = 0;

        if (c.moveToFirst()) {
            ;
            count = c.getInt(0);
        }
        c.close();

        return count;
    }

    public static String getListinoCompletoCount(long survey_id, String customerCode) {
        Cursor c = getDB().rawQuery("SELECT COUNT(1)"
                + " FROM ( SELECT " + TableItems.COLUMN_NO
                + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON "
                + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableSurveyAssortimento.TABLE_NAME
                + " ON "
                + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + survey_id
                + " GROUP BY " + TableItems.COLUMN_NO + ")", new String[]{customerCode});
        int count = 0;

        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();

        return String.valueOf(count);
    }

    public static String getProdottiRivelatiCount(long survey_id, String customerCode) {
        Cursor c = getDB().rawQuery("SELECT COUNT(1) FROM ( SELECT " + TableItems.COLUMN_NO + " FROM "
                + TableItems.TABLE_NAME + " LEFT JOIN " + TablePromotions.TABLE_NAME + " ON "
                + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableSurveyAssortimento.TABLE_NAME + " ON "
                + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO + "="
                + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + survey_id
                + " WHERE " + TableItems.COLUMN_NO + " IN (SELECT "
                + TableSurveyAssortimento.COLUMN_ITEM_CODE + " FROM " + TableSurveyAssortimento.TABLE_NAME
                + " LEFT JOIN " + TableSurvey.TABLE_NAME + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                + "=" + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                + "AND (" + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_NEW_SURVEY + " OR "
                + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_TO_BE_SENT + " OR "
                + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_FROM_SERVER + " OR "
                + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_DRAFTS + "))"
                + " GROUP BY " + TableItems.COLUMN_NO + ")", new String[]{customerCode, customerCode});
        int count = 0;

        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();

        return String.valueOf(count);
    }

    public static ArrayList<ModelSurveyAssortimento> getItems(long surveyId, String customerCode
            , String salesPersonCode, AssortimentoToLoad toLoad) {
        return getItems(surveyId, customerCode, salesPersonCode, toLoad, null);
    }

    public static ArrayList<ModelSurveyAssortimento> getItems(long surveyId, String customerCode
            , String salesPersonCode, AssortimentoToLoad toLoad
            , SearchCriteria criteria) {
        ArrayList<ModelSurveyAssortimento> models = new ArrayList<ModelSurveyAssortimento>();
        populateItems(surveyId, customerCode, salesPersonCode, toLoad, criteria, models);
        return models;
    }

    public static void populateItems(long surveyId, String customerCode
            , String salesPersonCode, AssortimentoToLoad toLoad
            , ArrayList<ModelSurveyAssortimento> models) {
        populateItems(surveyId, customerCode, salesPersonCode, toLoad, null, models);
    }

    private static final String ALIAS_PROMO_STATUS = "alias_it_p_status";

    public static void populateItems(long surveyId, String customerCode
            , String salesPersonCode, AssortimentoToLoad toLoad
            , SearchCriteria criteria, ArrayList<ModelSurveyAssortimento> models) {

        models.clear();

        String query = "SELECT " + TableItems.COLUMN_DESCRIPTION
                + "," + TableItems.COLUMN_NO
                + "," + TableItems.COLUMN_LINEA
                + "," + TableItems.COLUMN_MARCHIO
                + "," + TableItems.COLUMN_MACROFAMIGLIA
                + "," + TableItems.TABLE_NAME + "." + TableItems.COLUMN_ID
                + ",min(" + TablePromotions.COLUMN_PROMO_STATUS + ") AS " + ALIAS_PROMO_STATUS
                + "," + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ID + " AS " + TableSurveyAssortimento.ALIAS_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_PREZZO
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_FACING
                //     + "," + TableSurveyAssortimento.COLUMN_SURVEY_FUORI_BANCO
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LINEA
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL
                + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableSurveyAssortimento.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId;

        String[] arrayArg = null;
        ArrayList<String> selectionArg = new ArrayList<String>();
        selectionArg.add(customerCode);

        switch (toLoad) {
            case ASSORTIMENTO:
                query += " WHERE " + COLUMN_ASSORTMENTISTATUS + " ='1'"
                        + " AND " + COLUMN_ASSORTMENTICUSTCODES + " LIKE ?";
                selectionArg.add("%[" + customerCode + "]%");
                break;
            case COMPLETE:
                break;
            case PRODOTTI_RILEVATI:
                query += " WHERE " + COLUMN_NO + " IN ("
                        + " SELECT " + TableSurveyAssortimento.COLUMN_ITEM_CODE
                        + " FROM " + TableSurveyAssortimento.TABLE_NAME
                        + " LEFT JOIN " + TableSurvey.TABLE_NAME
                        + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                        + "=" + TableSurveyAssortimento.COLUMN_SURVEY_ID
                        + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                        + " AND (" + TableSurvey.COLUMN_FLAG + " = " + TableSurvey.FLAG_NEW_SURVEY + " OR "
                        + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_TO_BE_SENT + " OR "
                        + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_FROM_SERVER + " OR "
                        + TableSurvey.COLUMN_FLAG + "=" + TableSurvey.FLAG_DRAFTS + "))";
                selectionArg.add(customerCode);

                break;

            default:
                break;
        }

        if (criteria != null) {
            DBFilterArg argument = criteria.getSelection();
            if (argument != null) {
                switch (toLoad) {
                    case ASSORTIMENTO:
                    case PRODOTTI_RILEVATI:
                        query += " AND ";
                        break;
                    case COMPLETE:
                        query += " WHERE ";

                    default:
                        break;
                }
                query += argument.getSelection();
                selectionArg.addAll(argument.getSelectionArg());
            }
        }

        query += " GROUP BY " + COLUMN_NO
                + " ORDER BY " + COLUMN_LINEA + "," + COLUMN_MACROFAMIGLIA+ "," + COLUMN_NO
                + " COLLATE NOCASE ";
    /*  query += " GROUP BY " + COLUMN_NO
                + " ORDER BY FIELD("  + COLUMN_LINEA + ","+"Prep.dessert,"+"Toffee)" ;

*/


        if (Consts.DEBUGGABLE)
            Log.d(TAG, "The query is " + query);

        if (!selectionArg.isEmpty()) {
            arrayArg = new String[selectionArg.size()];
            selectionArg.toArray(arrayArg);
        }

        Log.d("ss", query);


        Cursor c = getDB().rawQuery(query, arrayArg);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                ModelSurveyAssortimento model = new ModelSurveyAssortimento(surveyId
                        , salesPersonCode, c, true);
                model.getItem().setPromoStatus(c.getString(c.getColumnIndex(ALIAS_PROMO_STATUS)));
                models.add(model);
                c.moveToNext();
            }
        }


    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class Item extends BaseItem {
        // ===========================================================
        // Constants
        // ===========================================================
        // ===========================================================
        // Fields
        // ===========================================================

        protected String marchio;
        protected String linea;
        protected String macrofamiglia;
        protected String promoStatus;

        // ===========================================================
        // Constructors
        // ===========================================================

        public static final Parcelable.Creator<Item> CREATOR
                = new Parcelable.Creator<Item>() {
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

        public Item(Parcel in) {
            id = in.readLong();
            description = in.readString();
            code = in.readString();

            marchio = in.readString();
            linea = in.readString();
            macrofamiglia = in.readString();
        }



        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(description);
            dest.writeString(code);

            dest.writeString(marchio);
            dest.writeString(linea);
            dest.writeString(macrofamiglia);
        }

        public Item(Cursor c) {
            super(c);
            marchio = c.getString(c.getColumnIndex(COLUMN_MARCHIO));
            linea = c.getString(c.getColumnIndex(COLUMN_LINEA));
            macrofamiglia = c.getString(c.getColumnIndex(COLUMN_MACROFAMIGLIA));
        }

        public Item(String code, Cursor c) {
            super(code, c);
            marchio = c.getString(c.getColumnIndex(COLUMN_MARCHIO));
            linea = c.getString(c.getColumnIndex(COLUMN_LINEA));
            macrofamiglia = c.getString(c.getColumnIndex(COLUMN_MACROFAMIGLIA));
        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================
        public String getItemMarchio() {
            return marchio;
        }

        public void setItemMarchio(String marchio) {
            this.marchio = marchio;
        }

        public String getItemLinea() {
            return linea;
        }

        public void setItemLinea(String linea) {
            this.linea = linea;
        }

        public String getItemMacrofamiglia() {
            return macrofamiglia;
        }

        public void setItemMacrofamiglia(String macrofamiglia) {
            this.macrofamiglia = macrofamiglia;
        }

        public String getPromoStatus() {
            // TODO Auto-generated method stub
            return promoStatus;
        }

        public void setPromoStatus(String promoStatus) {
            this.promoStatus = promoStatus;
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

    public static class SearchCriteria {
        // ===========================================================
        // Constants
        // ===========================================================
        // ===========================================================
        // Fields
        // ===========================================================

        private String marchio;
        private String linea;
        private String microFamilglia;
        private String argSearch;

        // ===========================================================
        // Constructors
        // ===========================================================

        public SearchCriteria(String marchio, String linea, String microFamilglia, String argSearch) {
            this.marchio = marchio;
            this.linea = linea;
            this.microFamilglia = microFamilglia;
            this.argSearch = argSearch;
        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================

        public void setMarchio(String marchio) {
            this.marchio = marchio;
        }

        public String getMarchio() {
            return marchio;
        }

        public void setLinea(String linea) {
            this.linea = linea;
        }

        public String getLinea() {
            return linea;
        }

        public void setArgSearch(String argSearch) {
            this.argSearch = argSearch;
        }

        public String getArgSearch() {
            return argSearch;
        }


        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        public DBFilterArg getSelection() {
            String baseQuery = "";
            ArrayList<String> arguments = new ArrayList<String>();
            boolean multipleArg = false;

            if (!TextUtils.isEmpty(marchio)) {
                if (multipleArg) {
                    baseQuery = baseQuery + " AND ";
                } else {
                    multipleArg = true;
                }
                baseQuery = baseQuery + COLUMN_MARCHIO + " = ?";
                arguments.add(marchio);
            }
            if (!TextUtils.isEmpty(linea)) {
                if (multipleArg) {
                    baseQuery = baseQuery + " AND ";
                } else {
                    multipleArg = true;
                }
                baseQuery = baseQuery + COLUMN_LINEA + " = ?";
                arguments.add(linea);
            }
            if (!TextUtils.isEmpty(microFamilglia)) {
                if (multipleArg) {
                    baseQuery = baseQuery + " AND ";
                } else {
                    multipleArg = true;
                }
                baseQuery = baseQuery + COLUMN_MACROFAMIGLIA + " = ?";
                arguments.add(microFamilglia);
            }

            if (!TextUtils.isEmpty(argSearch)) {
                if (multipleArg) {
                    baseQuery = baseQuery + " AND ";
                } else {
                    multipleArg = true;
                }
                baseQuery = baseQuery + " (" + COLUMN_DESCRIPTION + " LIKE ? OR "
                        + COLUMN_NO + " LIKE ?)";

                argSearch = "%" + argSearch + "%";

                arguments.add(argSearch);
                arguments.add(argSearch);
            }

            if (arguments.size() == 0)
                return null;


            return new DBFilterArg(baseQuery, arguments);
        }

        // ===========================================================
        // Methods
        // ===========================================================

        public boolean equals(SearchCriteria criteria) {
            if (criteria == null) {
                return false;
            }
            if (!Util.stringEquals(argSearch, criteria.argSearch)) {
                return false;
            } else if (!Util.stringEquals(marchio, criteria.marchio)) {
                return false;
            } else if (!Util.stringEquals(linea, criteria.linea)) {
                return false;
            } else if (!Util.stringEquals(microFamilglia,
                    criteria.microFamilglia)) {
                return false;
            }

            return true;
        }


        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
    }

    public static class BaseItem implements Parcelable {
        // ===========================================================
        // Constants
        // ===========================================================
        // ===========================================================
        // Fields
        // ===========================================================
        protected long id;
        protected String description;
        protected String code;

        public static final Parcelable.Creator<BaseItem> CREATOR
                = new Parcelable.Creator<BaseItem>() {
            public BaseItem createFromParcel(Parcel in) {
                return new BaseItem(in);
            }

            public BaseItem[] newArray(int size) {
                return new BaseItem[size];
            }
        };

        public BaseItem() {
            // TODO Auto-generated constructor stub
        }

        private BaseItem(Parcel in) {
            id = in.readLong();
            description = in.readString();
            code = in.readString();
        }

        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(description);
            dest.writeString(code);
        }


        // ===========================================================
        // Constructors
        // ===========================================================
        public BaseItem(Cursor c) {
            id = c.getLong(c.getColumnIndex(COLUMN_ID));
            description = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
            code = c.getString(c.getColumnIndex(COLUMN_NO));
            ;
        }

        public BaseItem(String code, Cursor c) {
            this.description = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
            this.code = code;
        }
        // ===========================================================
        // Getter & Setter
        // ===========================================================

        public long getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
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


    // for dashboard
    public static ArrayList<ModelProductForDashboard> getProductsInNewSurveyByLinea(long surveyId, String customerCode, String linea, String salesPersonCode) {

        ArrayList<ModelProductForDashboard> models = new ArrayList<ModelProductForDashboard>();
        SearchCriteria criteria = new SearchCriteria(null, linea, null, null);

        String query = "SELECT "
                + TableItems.COLUMN_NO
                + "," + TableItems.COLUMN_LINEA
                + "," + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ID + " AS " + TableSurveyAssortimento.ALIAS_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_FACING
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LINEA
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL
                + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableSurveyAssortimento.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId;

        String[] arrayArg = null;
        ArrayList<String> selectionArg = new ArrayList<String>();
        selectionArg.add(customerCode);


        query += " WHERE " + COLUMN_NO + " IN ("
                + " SELECT " + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " FROM " + TableSurveyAssortimento.TABLE_NAME
                + " LEFT JOIN " + TableSurvey.TABLE_NAME
                + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                + "=" + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                + ")";
        selectionArg.add(customerCode);


        if (criteria != null) {
            DBFilterArg argument = criteria.getSelection();
            if (argument != null) {

                query += " AND ";

                query += argument.getSelection();
                selectionArg.addAll(argument.getSelectionArg());

            }
        }


        if (Consts.DEBUGGABLE)
            //     Log.d(TAG, "The query is " + query);

            if (!selectionArg.isEmpty()) {
                arrayArg = new String[selectionArg.size()];
                selectionArg.toArray(arrayArg);
            }

        //   Log.d("shaheen", query);


        Cursor c = getDB().rawQuery(query, arrayArg);

        //   Log.e("ssss",linea+": "+c.getCount());

        //    return c.getCount();


        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                ModelProductForDashboard model = new ModelProductForDashboard(c.getInt(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)),
                        c.getInt(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_FACING)),
                        c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LINEA)));

                models.add(model);
                c.moveToNext();
            }
        }

        return models;
    }







 /*   public static int getNumberOfProductsInNewSurveyByLineAndSurveyLevel(long surveyId, String customerCode, String linea, String salesPersonCode,int surveyLevel) {

        ArrayList<ModelSurveyAssortimento> models=new ArrayList<ModelSurveyAssortimento>();
        SearchCriteria criteria = new SearchCriteria(null, linea, null, null);

        String query = "SELECT "
                + TableItems.COLUMN_NO
                + "," + TableItems.COLUMN_LINEA
                + "," + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ID + " AS " + TableSurveyAssortimento.ALIAS_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_FACING
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LINEA
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL
                + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableSurveyAssortimento.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId;

        String[] arrayArg = null;
        ArrayList<String> selectionArg = new ArrayList<String>();
        selectionArg.add(customerCode);


        query += " WHERE " + COLUMN_NO + " IN ("
                + " SELECT " + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " FROM " + TableSurveyAssortimento.TABLE_NAME
                + " LEFT JOIN " + TableSurvey.TABLE_NAME
                + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                + "=" + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                +  ")";
        selectionArg.add(customerCode);


        if (criteria != null) {
            DBFilterArg argument = criteria.getSelection();
            if (argument != null) {

                query += " AND ";

                query += argument.getSelection();
                selectionArg.addAll(argument.getSelectionArg());

            }
        }

        query+= " AND "+TableTempSurveyAssortimento.COLUMN_SURVEY_LEVEL+" = "+surveyLevel;

        if (Consts.DEBUGGABLE)
      //      Log.d(TAG, "The query is " + query);

        if (!selectionArg.isEmpty()) {
            arrayArg = new String[selectionArg.size()];
            selectionArg.toArray(arrayArg);
        }

    //    Log.d("shaheen", query);


        Cursor c = getDB().rawQuery(query, arrayArg);

     //   Log.e("ssss",linea+": "+c.getCount());

        return c.getCount();

    }
*/


    /*   public static int getNumberOfProductsInOldSurveyByLineAndSurveyLevel(long surveyId, String customerCode, String linea, String salesPersonCode,int surveyLevel) {

           ArrayList<ModelSurveyAssortimento> models=new ArrayList<ModelSurveyAssortimento>();
           SearchCriteria criteria = new SearchCriteria(null, linea, null, null);

           String query = "SELECT "
                   + TableItems.COLUMN_NO
                   + "," + TableItems.COLUMN_LINEA
                   + "," + TableTempSurveyAssortimento.TABLE_NAME + "." + TableTempSurveyAssortimento.COLUMN_ID + " AS " + TableTempSurveyAssortimento.ALIAS_ID
                   + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_ID
                   + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_FACING
                   + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_LINEA
                   + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_LEVEL
                   + " FROM " + TableItems.TABLE_NAME
                   + " LEFT JOIN " + TablePromotions.TABLE_NAME
                   + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                   + "="
                   + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                   + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                   + " LEFT JOIN " + TableTempSurveyAssortimento.TABLE_NAME
                   + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                   + "="
                   + TableTempSurveyAssortimento.TABLE_NAME + "." + TableTempSurveyAssortimento.COLUMN_ITEM_CODE
                   + " AND " + TableTempSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId;

           String[] arrayArg = null;
           ArrayList<String> selectionArg = new ArrayList<String>();
           selectionArg.add(customerCode);


           query += " WHERE " + COLUMN_NO + " IN ("
                   + " SELECT " + TableTempSurveyAssortimento.COLUMN_ITEM_CODE
                   + " FROM " + TableTempSurveyAssortimento.TABLE_NAME
                   + " LEFT JOIN " + TableSurvey.TABLE_NAME
                   + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                   + "=" + TableTempSurveyAssortimento.COLUMN_SURVEY_ID
                   + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                   +  ")";
           selectionArg.add(customerCode);


           if (criteria != null) {
               DBFilterArg argument = criteria.getSelection();
               if (argument != null) {

                   query += " AND ";

                   query += argument.getSelection();
                   selectionArg.addAll(argument.getSelectionArg());

               }
           }

           query+= " AND "+TableTempSurveyAssortimento.COLUMN_SURVEY_LEVEL+" = "+surveyLevel;


           if (Consts.DEBUGGABLE)
           //    Log.d(TAG, "The query is " + query);

           if (!selectionArg.isEmpty()) {
               arrayArg = new String[selectionArg.size()];
               selectionArg.toArray(arrayArg);
           }

        //   Log.d("shaheen", query);


           Cursor c = getDB().rawQuery(query, arrayArg);

       //    Log.e("ssss",linea+": "+c.getCount());

           return c.getCount();
       }
   */
    public static ArrayList<ModelProductForDashboard> getProductsInOldSurveyByLinea(long surveyId, String customerCode, String linea, String salesPersonCode) {

        ArrayList<ModelProductForDashboard> models = new ArrayList<ModelProductForDashboard>();
        SearchCriteria criteria = new SearchCriteria(null, linea, null, null);

        String query = "SELECT "
                + TableItems.COLUMN_NO
                + "," + TableItems.COLUMN_LINEA
                + "," + TableTempSurveyAssortimento.TABLE_NAME + "." + TableTempSurveyAssortimento.COLUMN_ID + " AS " + TableTempSurveyAssortimento.ALIAS_ID
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_ID
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_FACING
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_LINEA
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_LEVEL
                + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableTempSurveyAssortimento.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TableTempSurveyAssortimento.TABLE_NAME + "." + TableTempSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableTempSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId;

        String[] arrayArg = null;
        ArrayList<String> selectionArg = new ArrayList<String>();
        selectionArg.add(customerCode);


        query += " WHERE " + COLUMN_NO + " IN ("
                + " SELECT " + TableTempSurveyAssortimento.COLUMN_ITEM_CODE
                + " FROM " + TableTempSurveyAssortimento.TABLE_NAME
                + " LEFT JOIN " + TableSurvey.TABLE_NAME
                + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                + "=" + TableTempSurveyAssortimento.COLUMN_SURVEY_ID
                + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                + ")";
        selectionArg.add(customerCode);


        if (criteria != null) {
            DBFilterArg argument = criteria.getSelection();
            if (argument != null) {

                query += " AND ";

                query += argument.getSelection();
                selectionArg.addAll(argument.getSelectionArg());

            }
        }


        if (Consts.DEBUGGABLE)
            //   Log.d(TAG, "The query is " + query);

            if (!selectionArg.isEmpty()) {
                arrayArg = new String[selectionArg.size()];
                selectionArg.toArray(arrayArg);
            }

        //   Log.d("shaheen", query);


        Cursor c = getDB().rawQuery(query, arrayArg);

        //   Log.e("ssss",linea+": "+c.getCount());
        //   Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(c));


        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                ModelProductForDashboard model = new ModelProductForDashboard(c.getInt(c.getColumnIndex(TableTempSurveyAssortimento.COLUMN_SURVEY_LEVEL)),
                        c.getInt(c.getColumnIndex(TableTempSurveyAssortimento.COLUMN_SURVEY_FACING)),
                        c.getString(c.getColumnIndex(TableTempSurveyAssortimento.COLUMN_SURVEY_LINEA)));

                models.add(model);
                c.moveToNext();
            }
        }

        return models;
    }


    public static boolean isOldSurveyPresent(long surveyId, String customerCode) {

        String query = "SELECT "
                + TableItems.COLUMN_NO
                + "," + TableItems.COLUMN_LINEA
                + "," + TableTempSurveyAssortimento.TABLE_NAME + "." + TableTempSurveyAssortimento.COLUMN_ID + " AS " + TableTempSurveyAssortimento.ALIAS_ID
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_ID
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_FACING
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_LINEA
                + "," + TableTempSurveyAssortimento.COLUMN_SURVEY_LEVEL
                + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableTempSurveyAssortimento.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TableTempSurveyAssortimento.TABLE_NAME + "." + TableTempSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableTempSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId;

        String[] arrayArg = null;
        ArrayList<String> selectionArg = new ArrayList<String>();
        selectionArg.add(customerCode);


        query += " WHERE " + COLUMN_NO + " IN ("
                + " SELECT " + TableTempSurveyAssortimento.COLUMN_ITEM_CODE
                + " FROM " + TableTempSurveyAssortimento.TABLE_NAME
                + " LEFT JOIN " + TableSurvey.TABLE_NAME
                + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                + "=" + TableTempSurveyAssortimento.COLUMN_SURVEY_ID
                + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                + ")";
        selectionArg.add(customerCode);

        if (Consts.DEBUGGABLE)
            //   Log.d(TAG, "The query is " + query);

            if (!selectionArg.isEmpty()) {
                arrayArg = new String[selectionArg.size()];
                selectionArg.toArray(arrayArg);
            }

        //   Log.d("shaheen", query);


        Cursor c = getDB().rawQuery(query, arrayArg);

        //   Log.e("ssss",linea+": "+c.getCount());
        //   Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(c));


        return c.getCount() > 0;
    }


    public static ArrayList<String> isAllMandatoryFieldsFilledCorrectly(long surveyId, String customerCode, Toast mToast) {

        ArrayList<String> ids = new ArrayList<>();
        LinkedHashSet<String>hashSet=new LinkedHashSet<>();

        String query = "SELECT "
                + TableItems.COLUMN_NO
                + "," + TableItems.COLUMN_LINEA
                + "," + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ID + " AS " + TableSurveyAssortimento.ALIAS_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_FACING
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_PREZZO
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LINEA
                + "," + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL
                + " FROM " + TableItems.TABLE_NAME
                + " LEFT JOIN " + TablePromotions.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TablePromotions.TABLE_NAME + "." + TablePromotions.COLUMN_ITEM_CODE
                + " AND " + TablePromotions.COLUMN_CUSTOMER_NO + "= ?"
                + " LEFT JOIN " + TableSurveyAssortimento.TABLE_NAME
                + " ON " + TableItems.TABLE_NAME + "." + TableItems.COLUMN_NO
                + "="
                + TableSurveyAssortimento.TABLE_NAME + "." + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " AND " + TableSurveyAssortimento.COLUMN_SURVEY_ID + "=" + surveyId;

        String[] arrayArg = null;
        ArrayList<String> selectionArg = new ArrayList<String>();
        selectionArg.add(customerCode);


        query += " WHERE " + COLUMN_NO + " IN ("
                + " SELECT " + TableSurveyAssortimento.COLUMN_ITEM_CODE
                + " FROM " + TableSurveyAssortimento.TABLE_NAME
                + " LEFT JOIN " + TableSurvey.TABLE_NAME
                + " ON " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_ID
                + "=" + TableSurveyAssortimento.COLUMN_SURVEY_ID
                + " WHERE " + TableSurvey.TABLE_NAME + "." + TableSurvey.COLUMN_CUSTOMER_CODE + "= ?"
                + ")";
        selectionArg.add(customerCode);

        if (Consts.DEBUGGABLE) {
            //     Log.d(TAG, "The query is " + query);
        }

     /*   String selection = " AND ("
                + TableSurveyAssortimento.COLUMN_SURVEY_PREZZO + " is null or " + TableSurveyAssortimento.COLUMN_SURVEY_PREZZO + " = \"\" or "
                + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL + " is null or " + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL + " = \"\" or " + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL + " =2 or " + TableSurveyAssortimento.COLUMN_SURVEY_LEVEL + " =0 or "
                + TableSurveyAssortimento.COLUMN_SURVEY_FACING + " is null or " + TableSurveyAssortimento.COLUMN_SURVEY_FACING + " = \"\" or " + TableSurveyAssortimento.COLUMN_SURVEY_FACING + " =0 or "
                + TableSurveyAssortimento.COLUMN_SURVEY_LINEA + " is null or " + TableSurveyAssortimento.COLUMN_SURVEY_LINEA + " = \"\" or " + TableSurveyAssortimento.COLUMN_SURVEY_LINEA + " = " + "\"/\" )";
*/

        if (!selectionArg.isEmpty()) {
            arrayArg = new String[selectionArg.size()];
            selectionArg.toArray(arrayArg);
        }

        //   Log.d("shaheen", query);


        Cursor c = getDB().rawQuery(query, arrayArg);
           Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(c));
        //   Log.e("ssss",linea+": "+c.getCount());

        //    return c.getCount();

        int count = 0;
        //     Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(c));
        while (c.moveToNext()) {

            if (c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)) != null) {
                if (!(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)).equals("2"))) {

                    if (c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_FACING)) != null && c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO)) != null) {

                        if (!(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)).equals("2"))) {

                            if (c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)).equals("0") || TextUtils.isEmpty(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)))) {
                                Util.showToast(mToast, R.string.msg_fill_all_fields);
                               // return false;
                                if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                                    ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                                }
                            }

                            String posLin = c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LINEA));
                            int v1end = posLin.indexOf("/");
                            if (v1end != -1) {
                                try {
                                    int val1 = Integer.parseInt(posLin.substring(0, posLin.indexOf("/")));
                                    int val2 = Integer.parseInt(posLin.substring(posLin.indexOf("/") + 1));

                                } catch (NumberFormatException e) {
                                    Util.showToast(mToast, R.string.msg_fill_all_fields);
                                   // return false;
                                    if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                                        ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                                    }
                                }
                            } else {
                                Util.showToast(mToast, R.string.msg_fill_all_fields);
                              //  return false;
                                if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                                    ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                                }
                            }


                            if (c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_FACING)).equals("0") || TextUtils.isEmpty(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_FACING)))) {
                                Util.showToast(mToast, R.string.msg_fill_all_fields);
                              //  return false;
                                if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                                    ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                                }
                            }

                            if (c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO)).equals("0") || TextUtils.isEmpty(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO)))) {
                                Util.showToast(mToast, R.string.msg_fill_all_fields);
                               // return false;
                                if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                                    ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                                }
                            }

                        }

                        /*if (Double.parseDouble(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO)).replace(',', '.')) > 30) {
                            Util.showToast(mToast, R.string.prezzo_promo_limit);
                            return false;
                        }*/


                    } else {
                        Util.showToast(mToast, R.string.msg_fill_all_fields);
                      //  return false;
                        if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                            ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                        }
                    }
                   /* if (!TextUtils.isEmpty(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO)))) {
                        if (Double.parseDouble(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO)).replace(',', '.')) > 30) {
                            Util.showToast(mToast, R.string.prezzo_promo_limit);
                            return false;
                        }
                    }*/
                }

                if (!TextUtils.isEmpty(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO))) && !(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)).equals("2"))) {
                    if (Double.parseDouble(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO_PROMO)).replace(',', '.')) > 30) {
                        Util.showToast(mToast, R.string.prezzo_promo_limit);
                      //  return false;
                        if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                            ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                        }
                    }
                }

                if (!TextUtils.isEmpty(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO))) && !(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_LEVEL)).equals("2"))) {
                    if (Double.parseDouble(c.getString(c.getColumnIndex(TableSurveyAssortimento.COLUMN_SURVEY_PREZZO)).replace(',', '.')) > 30) {
                        Util.showToast(mToast, R.string.prezzo_promo_limit);
                      //  return false;
                        if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                            ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                        }
                    }
                }

            } else {
                Util.showToast(mToast, R.string.msg_fill_all_fields);
              //  return false;
                if (hashSet.add(c.getString(c.getColumnIndex(COLUMN_NO)))) {
                    ids.add(c.getString(c.getColumnIndex(COLUMN_NO)));
                }
            }
        }
        return ids;
    }

}
