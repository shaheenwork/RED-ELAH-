package com.eteam.dufour.async;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableList;
import com.eteam.dufour.database.tables.TableList.Table;
import com.eteam.dufour.database.tables.TableLogin;
import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.dufour.database.tables.TableSurvey.Survey;
import com.eteam.dufour.database.tables.TableSurveyCustomers;
import com.eteam.dufour.database.tables.TableSyncInfo;
import com.eteam.dufour.database.tables.TableSyncInfo.Session;
import com.eteam.dufour.mobile.BuildConfig;
import com.eteam.dufour.mobile.LoginActivity;
import com.eteam.dufour.mobile.LoginActivity.LoginStatus;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.Util;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLDataException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SyncTask extends AsyncTask<Void, Integer, Integer> {
    // ===========================================================
// Constants
// ===========================================================
    private static final String KEY_PAGE_NO = "page_no";

    private static final String KEY_POST_JSON = "syncdata";
    private static final String KEY_REQ_FOR = "reqfor";
    private static final String KEY_USER_NAME = "user";
    private static final String KEY_PASSWORD = "pwd";
    private static final String KEY_VERSION_ID = "versionId";
    private static final String KEY_CURRENT_TIME = "ct";

    private static final String KEY_SYNC_STATUS = "syncTypeStatus";
    private static final String KEY_UPDATE_STATUS = "U";

    private static final String KEY_RESPONSE = "errorcode";

    private static final String RESPONSE_SUCCESS = "1";
    private static final String RESPONSE_UPDATE_AVAILABLE = "1";

    public static final int SURVEY_SEND_FAILURE = -101;
    public static final int SURVEY_SEND_SUCCESS = -102;
    public static final int SYNC_STARTED = -111;

    public static final int SYNC_FROM_SERVER_SUCCESSFUL = -104;
    public static final int SYNC_FROM_SERVER_FAILED = -105;

    public static final int NO_UPDATE_AVAILABLE = -109;
    public static final int LOGIN_FAILED = -112;
    public static final int PERMISSION_NOT_ALLOWED = -113;
    public static final int LOGIN_SERVER_ERROR = -114;

// ===========================================================
// Fields
// ===========================================================

    private ElahDBHelper dbHelper;
    private String userName;
    private String passWord;
    private String versionId;
    private SharedPreferences mPrefs;
    private String androidVersion;

// ===========================================================
// Constructors
// ===========================================================

    public SyncTask(ElahDBHelper dbHelper, SharedPreferences prefs
            , String userName, String password, String versionId) {
        // TODO Auto-generated constructor stub
        this.dbHelper = dbHelper;
        this.userName = userName;
        this.mPrefs = prefs;
        this.passWord = password;
        this.versionId = versionId;
        androidVersion = Build.VERSION.RELEASE;
    }

// ===========================================================
// Getter & Setter
// ===========================================================

// ===========================================================
// Methods for/from SuperClass/Interfaces
// ===========================================================

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        // TODO Auto-generated method stub
        super.onCancelled();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null && db.inTransaction())
            db.endTransaction();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        //Actual network operation
        String response = null;
        SQLiteDatabase xdb = dbHelper.getWritableDatabase();
        ArrayList<Survey> surveys = TableSurvey.getToBeSentSurveys();
        int count = 0;
        try {


            for (Survey survey : surveys) {

                JSONObject toBeSentSurvey = null;
                switch (survey.getFlag()) {
                    case TableSurvey.FLAG_TO_BE_SENT:
                        toBeSentSurvey = TableSurvey.getSurveyAsJSON(survey.getId(), true);
                        break;
                    case TableSurvey.FLAG_TO_BE_SENT_OLD:
                        toBeSentSurvey = TableSurvey.getSurveyAsJSON(survey.getId(), false);
                        break;
                }

                JSONObject currentSurveyJSON = getCompleteSurvey(toBeSentSurvey, count);
                JSONObject sentresponse = ElahHttpClient.getJSONPost(Consts.SERVER_URL + "/" + "SurveySave"
                        , getPostData(currentSurveyJSON.toString()
                                , survey.getSentDateTime()));

                if (sentresponse.get(KEY_RESPONSE).equals(RESPONSE_SUCCESS)) {
//					Survey surveyCustomer = SurveyDBOperations.getCustomerSurvey(dbHelper, survey.getId());
                    TableSurveyCustomers.create(Util.convertToCustomer(survey));
                    TableSurvey.delete(survey);

                } else {

                    if (!BuildConfig.DEBUG) {
					/*nwwd	Crashlytics.log(Log.ERROR, SyncTask.class.getName(),
								"Error sending msg\n User Name is " + userName + "\n" + "JSON is " + currentSurveyJSON.toString(4) + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);
				*/


                        FirebaseCrashlytics.getInstance().log(SyncTask.class.getName() + ": " +
                                "Error sending msg\n User Name is " + userName + "\n" + "JSON is " + currentSurveyJSON.toString(4) + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);


                    }
                    return SURVEY_SEND_FAILURE;

                }
                count++;
            }

        } catch (Exception e) {
            if (!BuildConfig.DEBUG) {
			  /*nwwd Crashlytics.log(Log.ERROR, SyncTask.class.getName()
					   , "User Name is " + userName + "\n" + "The error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);
*/


                FirebaseCrashlytics.getInstance().log(SyncTask.class.getName() + ": " +
                        "User Name is " + userName + "\n" + "The error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);

                FirebaseCrashlytics.getInstance().recordException(e);

            }
            return SURVEY_SEND_FAILURE;


        }
        if (count > 0)
            publishProgress(SURVEY_SEND_SUCCESS);
        publishProgress(SYNC_STARTED);
        try {

            if (TableLogin.isSyncCompleted(userName)) {
                JSONObject updateResponse = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL + "/sync?reqfor=checkupdates" + "&ct=" + Util.getCurrentTimeInMilliSeconds());
                String updateStatus = updateResponse.optString(KEY_UPDATE_STATUS);
                if (updateStatus != null && !updateStatus.equals(RESPONSE_UPDATE_AVAILABLE)) {
                    return NO_UPDATE_AVAILABLE;
                }
            }

            response = ElahHttpClient.executeGet(Consts.SERVER_URL + "/sync?reqfor=tablelist&Starting_date=" + getCurrentDate() + "&ct=" + getMilliSeconds());
            Log.d("Log", "Bibin = " + Consts.SERVER_URL + "/sync?reqfor=tablelist&Starting_date=" + getCurrentDate() + "&ct=" + getMilliSeconds());
            if (Consts.DEBUGGABLE)
                Log.d("___", "response: " + response.intern());
            Log.d("Log", "Bibin = " + Consts.SERVER_URL + "/sync?reqfor=tablelist&Starting_date=" + getCurrentDate() + "&ct=" + getMilliSeconds());
            if (response.startsWith("(") && response.endsWith("")) {
                response = response.substring(1).replace(");", "");
            }

            JSONObject obj = new JSONObject(response);

            String status = obj.optString(Consts.JSON_KEY_SESSION_TIME_OUT);
            if (status != null && status.equals(Consts.RESPONSE_SESSION_TIMED_OUT)) {
                LoginStatus loginStatus = LoginActivity.login(dbHelper, mPrefs);
                switch (loginStatus) {
                    case SUCCESS:
                        response = ElahHttpClient.executeGet(Consts.SERVER_URL + "/sync?reqfor=tablelist&Starting_date="
                                + getCurrentDate() + "&ct=" + getMilliSeconds());
                        Log.d("Log", "Bibin= " + Consts.SERVER_URL + "/sync?reqfor=tablelist&Starting_date=" + getCurrentDate() + "&ct=" + getMilliSeconds());
                        break;

                    case FAILED:
                        return LOGIN_FAILED;

                    case NO_PERMISSION:
                        return PERMISSION_NOT_ALLOWED;

                    case SERVER_ERROR:
                        return LOGIN_SERVER_ERROR;
                }
            }

            JSONObject syncObj = obj.getJSONObject("syncdata");

            String syncStatus = syncObj.getString(KEY_SYNC_STATUS);

            if (syncStatus.equals(RESPONSE_UPDATE_AVAILABLE)) {

                TableLogin.setSyncStatus(userName, TableLogin.SYNC_INCOMPLETE);
                xdb.execSQL("CREATE TABLE IF NOT EXISTS SYNCDATA (_id INTEGER PRIMARY KEY, session VARCHAR, date VARCHAR)");
                TableSyncInfo.createSession(new Session(syncObj));
                ElahDBHelper.dropAllDataTables(dbHelper);
                ElahDBHelper.createAllDataTables(dbHelper);
                JSONArray tableListArray = syncObj.getJSONArray(TableList.TABLE_NAME);
                TableList.createAndPopulateTableList(tableListArray);
            } else {

                return NO_UPDATE_AVAILABLE;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (!BuildConfig.DEBUG) {
			/* nwwd  Crashlytics.log(Log.ERROR, SyncTask.class.getName()
					   , "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);
*/

                FirebaseCrashlytics.getInstance().log(SyncTask.class.getName() + ": " +
                        "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);

                FirebaseCrashlytics.getInstance().recordException(e);


            }
            return SYNC_FROM_SERVER_FAILED;

        }


        long pagenumber = TableList.getTotalPages();
        ArrayList<Table> tableList = TableList.getAllTableInfo();
        int tableNumber = 0;
        int tableListSize = tableList.size();
        xdb.beginTransaction();

        //table insert logic
        try {
            if (tableListSize > 0) {
                for (int k = 0; k < tableListSize; k++) {
                    Table table = tableList.get(k);
                    String tablename = table.getTableName();
                    int totalpages = table.getTotalPages();
                    SQLiteStatement insert = null;
                    String currentTable = "";
                    String url;
                    for (int q = 0; q < totalpages; q++) {

                        String currentDate = getCurrentDate();
                        if (Consts.DEBUGGABLE) {
                            url = Consts.SERVER_URL + "/sync?reqfor=" + tablename + "&pageno=" + (q + 1) + "&Starting_date=" + currentDate + "&ct=" + getMilliSeconds();
                            Log.d("Log", "Table url is = " + url);
                        }

                        response = ElahHttpClient.executeGet(Consts.SERVER_URL + "/sync?reqfor=" + tablename + "&pageno=" + (q + 1)
                                + "&Starting_date=" + currentDate + "&ct=" + getMilliSeconds());
                        if (isCancelled()) {
                            xdb.endTransaction();
                            return SYNC_FROM_SERVER_FAILED;
                        }

                        if (response.startsWith("(") && response.endsWith("")) {
                            response = response.substring(1).replace(");", "");
                        }

                        JSONObject jsonTable = new JSONObject(response);
                        JSONArray tableInfoArray = jsonTable.getJSONArray(tablename);
                        if (Consts.DEBUGGABLE)
                            Log.d("LOG", "JSON Array Length " + tableInfoArray.length());
                        int oldInfoKeyLength = -1;
                        int infoArraySize = tableInfoArray.length();
                        for (int i = 0; i < infoArraySize; i++) {
                            if (isCancelled()) {
                                xdb.endTransaction();
                                return SYNC_FROM_SERVER_FAILED;
                            }
                            JSONObject tableInfo = tableInfoArray.getJSONObject(i);
                            Iterator<?> iterator;
                            String tablelist_keys = "";
                            String paramts = "";

                            boolean first = true;
                            int index = 1;
                            if (!currentTable.equals(tablename) || oldInfoKeyLength != tableInfo.length()) {

                                oldInfoKeyLength = tableInfo.length();
                                iterator = tableInfo.keys();
                                while (iterator.hasNext()) {
                                    String key = String.valueOf(iterator.next());
                                    if (first) {
                                        tablelist_keys += key;
                                        paramts += "?";
                                        first = false;
                                    } else {
                                        tablelist_keys += "," + key;
                                        paramts += ",?";
                                    }

                                }

                                insert = xdb.compileStatement("INSERT INTO " + tablename + "(" + tablelist_keys + ") " + "VALUES(" + paramts + ")");
                                currentTable = tablename;


                            }
                            iterator = tableInfo.keys();
                            while (iterator.hasNext() && !isCancelled()) {
                                String key = String.valueOf(iterator.next());
                                insert.bindString(index, tableInfo.getString(key));
                                index++;
                            }

                            if (insert.executeInsert() == -1) {
                                throw new SQLDataException("Insertion failed during normal flow");
                            }
                            insert.clearBindings();
                        }
                        tableNumber++;
                        publishProgress((int) (tableNumber * 100 / pagenumber));
                    }
                    if (insert != null)
                        insert.close();
                    if (!TableList.isAllRecordsPresentForTable(tablename)) {
                        throw new IllegalStateException("Records from server does not match local db");

                    }

                }

                xdb.setTransactionSuccessful();
            } else
                return SYNC_FROM_SERVER_FAILED;
        } catch (Exception e) {
            if (!BuildConfig.DEBUG) {
		/*nwwd	   Crashlytics.log(Log.ERROR, SyncTask.class.getName()
					   , "Error is " + e.getMessage() + " The response from server is " + response + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);
*/

                FirebaseCrashlytics.getInstance().log(SyncTask.class.getName() + ": " +
                        "Error is " + e.getMessage() + " The response from server is " + response + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + androidVersion);

                FirebaseCrashlytics.getInstance().recordException(e);


            }
            e.printStackTrace();
            return SYNC_FROM_SERVER_FAILED;

        } finally {

            if (xdb != null) {
                xdb.endTransaction();
            }
        }
        if (!isCancelled()) {
            return SYNC_FROM_SERVER_SUCCESSFUL;
        } else
            return SYNC_FROM_SERVER_FAILED;


    }

// ===========================================================
// Methods
// ===========================================================

    private JSONObject getCompleteSurvey(JSONObject survey, int page) throws JSONException {

        JSONObject object = new JSONObject();
        object.put(Survey.KEY_JSON_ARRAY_NAME, survey);
        object.put(KEY_PAGE_NO, page);
        return object;
    }

    private List<NameValuePair> getPostData(String json, String sentTime) {
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

        BasicNameValuePair request = new BasicNameValuePair(KEY_REQ_FOR, "survey");
        BasicNameValuePair syncdata = new BasicNameValuePair(KEY_POST_JSON, json);
        BasicNameValuePair user = new BasicNameValuePair(KEY_USER_NAME, userName);
        BasicNameValuePair pwd = new BasicNameValuePair(KEY_PASSWORD, passWord);
        BasicNameValuePair version = new BasicNameValuePair(KEY_VERSION_ID, versionId);
        BasicNameValuePair ct = new BasicNameValuePair(KEY_CURRENT_TIME, sentTime);

        list.add(request);
        list.add(syncdata);
        list.add(user);
        list.add(pwd);
        list.add(version);
        list.add(ct);

        return list;
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyyHH:mm:ss:SSS
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    private long getMilliSeconds() {
        // TODO Auto-generated method stub
        Date now = new Date();
        return now.getTime();
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentDateAndTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyyHH:mm:ss:SSS
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }


// ===========================================================
// Inner and Anonymous Classes
// ===========================================================
}