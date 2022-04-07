package com.eteam.dufour.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.dufour.database.tables.TableSurvey.Survey;
import com.eteam.dufour.database.tables.TableSurveyAssortimento;
import com.eteam.dufour.database.tables.TableSurveyCompetitor;
import com.eteam.dufour.database.tables.TableSurveyPromotions;
import com.eteam.dufour.database.tables.TableTempSurveyAssortimento;
import com.eteam.dufour.mobile.LoginActivity.LoginStatus;
import com.eteam.testdb.AndroidDatabaseManager;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.Util;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public class SurveyCreateActivity extends Activity implements OnClickListener {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final String INTENT_CUSTOMER_NAME = "com.eteam.dufour.release.backup.SurveyButtonActivity.INTENT_CUSTOMER_NAME";
    private static final String INTENT_CUSTOMER_CODE = "com.eteam.dufour.release.backup.SurveyButtonActivity.INTENT_CUSTOMER_CODE";

    private static final String KEY_REQUEST = "reqfor";
    private static final String KEY_CUST_CODE = "cust_code";
    private static final String KEY_CURRENT_TIME = "ct";
//	private static final String KEY_SALESPERSON_CODE = "sales_code";

    private static final String VALUE_SURVEY_CHECK = "previousSurveyCheck";
    private static final String VALUE_SURVEY_GET_PREVIOUS = "previousSurvey";


    // ===========================================================
    // Fields
    // ===========================================================

    private View mLayoutBtn;
    private View mBtnCopySurvey;
    private View mBtnErrorCopying;
    private View mLayoutCopyBtn;
    private View mBtnNewSurvey;
    private View mBtnCancel;
    private View mBtnDrafts;
    private TextView mLblNetNotAvailable;

    private String customerName;
    private String customerCode;
    private String salesPersonCode;

    private Toast mToast;

    private ElahDBHelper dbHelper;

    private PreviousSurveyCheckTask mCheckTask;
    private GetPreviousSurveyTask mCopyTask;


    private SharedPreferences mPrefs;

    private NetConnectionReceiver mReceiver;

    private long draftId;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Launchers
    // ===========================================================
    public static final void startActivity(Activity activity
            , String customerName, String customerCode) {
        Intent intent = new Intent(activity, SurveyCreateActivity.class);
        intent.putExtra(INTENT_CUSTOMER_NAME, customerName);
        intent.putExtra(INTENT_CUSTOMER_CODE, customerCode);
        activity.startActivity(intent);
    }

    public static final void startActivityForResult(Activity activity
            , String customerName, String customerCode, int callingActivity) {
        Intent intent = new Intent(activity, SurveyCreateActivity.class);
        intent.putExtra(INTENT_CUSTOMER_NAME, customerName);
        intent.putExtra(INTENT_CUSTOMER_CODE, customerCode);
        activity.startActivityForResult(intent, callingActivity);
    }
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_btn_layout);
        setFromIntent(savedInstanceState);

        mToast = Toast.makeText(this, "Sample", Toast.LENGTH_SHORT);

        TextView fieldPDVCode = (TextView) findViewById(R.id.field_pdv);
        fieldPDVCode.setText(Html.fromHtml("<b>" + getResources().getString(R.string.pdv) + "</b>: " + customerCode));

        mLayoutBtn = findViewById(R.id.layout_btn);
        mBtnCopySurvey = findViewById(R.id.btn_copia_valori);
        mBtnErrorCopying = findViewById(R.id.btn_error_copying);
        mLayoutCopyBtn = findViewById(R.id.layout_copy);
        mBtnNewSurvey = findViewById(R.id.btn_attivita_vuota);
        mBtnCancel = findViewById(R.id.btn_annulla);
        mBtnDrafts = findViewById(R.id.btn_riprendi_la_bozza_esistente);


        mLblNetNotAvailable = (TextView) findViewById(R.id.lbl_msg_no_net);


        mBtnCopySurvey.setOnClickListener(this);
        mBtnNewSurvey.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnDrafts.setOnClickListener(this);
        mBtnErrorCopying.setOnClickListener(this);

        mPrefs = getSharedPreferences(Consts.PREF_ELAH, MODE_PRIVATE);
        salesPersonCode = mPrefs.getString(LoginActivity.PREF_SALESPERSON, "");

        dbHelper = ElahDBHelper.getInstance(this);
        setUpDraftsButtons();
        setUpCopyButton();
        try {
            LoginActivity.copyAppDbToDownloadFolder(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        setUpReceiver();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        outState.putString(INTENT_CUSTOMER_CODE, customerCode);
        outState.putString(INTENT_CUSTOMER_NAME, customerName);
        super.onSaveInstanceState(outState);

    }

    public void onClick(View v) {
        if (v == mBtnCopySurvey) {
            if (mCopyTask == null) {
                mCopyTask = new GetPreviousSurveyTask(this, Consts.COPY_SURVEY);
                mCopyTask.execute(customerCode);
            } else {
                if (mCopyTask.getStatus().equals(Status.FINISHED)) {
                    mCopyTask = new GetPreviousSurveyTask(this, Consts.COPY_SURVEY);
                    mCopyTask.execute(customerCode);
                }
            }
//			finish();
        } else if (v == mBtnNewSurvey) {

            /*long surveyId = TableSurvey.createNewSurvey(new Survey(salesPersonCode, customerCode, Util.getCurrentDateTime(), 1, 0));
            SurveyTabActivity.startActivity(this, surveyId, customerName, customerCode);
            setResult(RESULT_OK);
            finish();*/

            if (mCopyTask == null) {
                mCopyTask = new GetPreviousSurveyTask(this, Consts.NEW_SURVEY);
                mCopyTask.execute(customerCode);
            } else {
                if (mCopyTask.getStatus().equals(Status.FINISHED)) {
                    mCopyTask = new GetPreviousSurveyTask(this, Consts.NEW_SURVEY);
                    mCopyTask.execute(customerCode);
                }
            }

        } else if (v == mBtnCancel) {
            finish();
        } else if (v == mBtnDrafts) {
            if (draftId != -1) {
                SurveyTabActivity.startActivity(this, draftId, customerName, customerCode);
                setResult(RESULT_OK);
                finish();
            }
        } else if (v == mBtnErrorCopying) {
            startSurveyCheckActivity();
        }

    }
    // ===========================================================
    // Methods
    // ===========================================================

    private void setFromIntent(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (savedInstanceState != null) {
            customerCode = savedInstanceState.getString(INTENT_CUSTOMER_CODE);
            customerName = savedInstanceState.getString(INTENT_CUSTOMER_NAME);
        } else {
            customerCode = getIntent().getStringExtra(INTENT_CUSTOMER_CODE);
            customerName = getIntent().getStringExtra(INTENT_CUSTOMER_NAME);
        }
    }

    private void startSurveyCheckActivity() {
        if (mCheckTask == null) {
            mCheckTask = new PreviousSurveyCheckTask(this);
            mCheckTask.execute(customerCode);
        } else {
            if (mCheckTask.getStatus() == Status.FINISHED) {
                mCheckTask = new PreviousSurveyCheckTask(this);
                mCheckTask.execute(customerCode);
            }
        }
    }

    private void setUpReceiver() {
        // TODO Auto-generated method stub
        mReceiver = new NetConnectionReceiver();
        IntentFilter mFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    private void setUpCopyButton() {
        // TODO Auto-generated method stub
        if (Util.haveNetworkConnection(this)) {
            if (mLayoutCopyBtn != null)
                mLayoutCopyBtn.setVisibility(View.GONE);
            if (mLblNetNotAvailable != null)
                mLblNetNotAvailable.setVisibility(View.GONE);
            startSurveyCheckActivity();
        } else {
            if (mLayoutCopyBtn != null)
                mLayoutCopyBtn.setVisibility(View.VISIBLE);
            if (mLblNetNotAvailable != null)
                mLblNetNotAvailable.setVisibility(View.VISIBLE);
            if (mBtnCopySurvey != null)
                mBtnCopySurvey.setVisibility(View.INVISIBLE);
            if (mBtnErrorCopying != null)
                mBtnErrorCopying.setVisibility(View.GONE);
        }
    }

    private void setUpDraftsButtons() {

        draftId = TableSurvey.getDraftFor(customerCode);
        if (draftId != -1) {
            mBtnDrafts.setVisibility(View.VISIBLE);
        } else {
            mBtnDrafts.setVisibility(View.GONE);
        }
    }

    public void startSurveyTabActivity(Long surveyId) {
        SurveyTabActivity.startActivity(SurveyCreateActivity.this, surveyId
                , customerName, customerCode);
        setResult(RESULT_OK);
        finish();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class PreviousSurveyCheckTask extends AsyncTask<String, Integer, String> {

        private final static String RESPONSE_SURVEY_EXISTS = "1";
        private final static String RESPONSE_SURVEY_EXPIRED = "2";
        private final static String KEY_RESPONSE = "S";
        private static final String LOGIN_FAILED = "lf";
        private static final String PERMISSION_NOT_ALLOWED = "pna";
        private static final String LOGIN_SERVER_ERROR = "lse";

        private ElahProgress mProgress;
        private Context mContext;

        // ===========================================================
        // Constructors
        // ===========================================================

        private PreviousSurveyCheckTask(Context context) {
            this.mContext = context;
        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (mLayoutBtn != null)
                mLayoutBtn.setVisibility(View.INVISIBLE);

            mProgress = new ElahProgress(SurveyCreateActivity.this);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setCancelable(false);
            if (mProgress != null)
                mProgress.show(R.string.msg_loading);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (mProgress != null)
                    mProgress.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(result)) {
                if (result.equals(LOGIN_FAILED)) {
                    if (mContext != null)
                        Util.logout(mContext, R.string.msg_pw_changed);
                } else if (result.equals(PERMISSION_NOT_ALLOWED)) {
                    if (mContext != null)
                        Util.logout(mContext, R.string.msg_permission_changed);
                } else if (result.equals(LOGIN_SERVER_ERROR)) {
                    if (mLayoutCopyBtn != null)
                        mLayoutCopyBtn.setVisibility(View.INVISIBLE);
                    if (mBtnCopySurvey != null)
                        mBtnCopySurvey.setVisibility(View.INVISIBLE);
                    if (mBtnErrorCopying != null) {
                        mBtnErrorCopying.setVisibility(View.VISIBLE);
                    }
//					Util.showToast(mToast, R.string.msg_login_server_not_responding);
                } else if (result.equals(RESPONSE_SURVEY_EXISTS)) {
                    if (mLayoutCopyBtn != null)
                        mLayoutCopyBtn.setVisibility(View.VISIBLE);
                    if (mBtnCopySurvey != null)
                        mBtnCopySurvey.setVisibility(View.VISIBLE);
                    if (mBtnErrorCopying != null)
                        mBtnErrorCopying.setVisibility(View.GONE);

                } else if (result.equals(RESPONSE_SURVEY_EXPIRED)) {
                    Util.showToast(mToast, R.string.msg_data_expired);
                    if (mLayoutCopyBtn != null)
                        mLayoutCopyBtn.setVisibility(View.GONE);
                } else {
                    if (mLayoutCopyBtn != null)
                        mLayoutCopyBtn.setVisibility(View.GONE);
                    if (mBtnCopySurvey != null)
                        mBtnCopySurvey.setVisibility(View.INVISIBLE);
                    if (mBtnErrorCopying != null)
                        mBtnErrorCopying.setVisibility(View.GONE);
                }
            } else {
                if (mLayoutCopyBtn != null)
                    mLayoutCopyBtn.setVisibility(View.VISIBLE);
                if (mBtnCopySurvey != null)
                    mBtnCopySurvey.setVisibility(View.INVISIBLE);
                if (mBtnErrorCopying != null)
                    mBtnErrorCopying.setVisibility(View.VISIBLE);
//				Util.showToast(mToast, R.string.error_server);
            }

            if (mLayoutBtn != null)
                mLayoutBtn.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;

            try {


                JSONObject jsonResponse = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL + "/sync?" + KEY_REQUEST + "=" + VALUE_SURVEY_CHECK
                        + "&" + KEY_CUST_CODE + "=" + params[0].replace(" ", "%20"));
                response = jsonResponse.optString(Consts.JSON_KEY_SESSION_TIME_OUT);
                if (response != null && response.equals(Consts.RESPONSE_SESSION_TIMED_OUT)) {
                    LoginStatus loginStatus = LoginActivity.login(dbHelper, mPrefs);
                    switch (loginStatus) {
                        case SUCCESS:
                            jsonResponse = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL + "/sync?" + KEY_REQUEST + "=" + VALUE_SURVEY_CHECK
                                    + "&" + KEY_CUST_CODE + "=" + params[0].replace(" ", "%20"));
                            break;

                        case FAILED:
                            return LOGIN_FAILED;

                        case NO_PERMISSION:
                            return PERMISSION_NOT_ALLOWED;

                        case SERVER_ERROR:
                            return LOGIN_SERVER_ERROR;
                    }

                }

                response = jsonResponse.getString(KEY_RESPONSE);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }

    }

    private class GetPreviousSurveyTask extends AsyncTask<String, Integer, Long> {

        private static final long SERVER_ERROR = -101;
        private static final long JSON_EXCEPTION = -103;
        private static final long DISK_ERROR = -104;
        private static final long URL_SET_ERROR = -105;
        private static final long LOGIN_FAILED = -112;
        private static final long PERMISSION_NOT_ALLOWED = -113;
        private static final long LOGIN_SERVER_ERROR = -114;

        private ElahProgress mProgress;
        private Context mContext;
        private int copyOrNew;

        // ===========================================================
        // Constructors
        // ===========================================================

        private GetPreviousSurveyTask(Context context, int copyOrNew) {
            this.mContext = context;
            this.copyOrNew = copyOrNew;
        }

        // ===========================================================
        // ===========================================================

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        // Getter & Setter
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgress = new ElahProgress(SurveyCreateActivity.this);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setCancelable(false);
            if (mProgress != null)
                mProgress.show(R.string.msg_copying);
        }

        @Override
        protected void onPostExecute(Long result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mProgress != null)
                mProgress.dismiss();


            if (result != null) {
                if (result == SERVER_ERROR) {
                    if (copyOrNew != Consts.NEW_SURVEY) {
                        Util.showToast(mToast, R.string.error_server);
                    }
                    finish();
                } else if (result == JSON_EXCEPTION) {
                    if (copyOrNew != Consts.NEW_SURVEY) {
                        Util.showToast(mToast, R.string.error_json);
                    }
                    finish();
                } else if (result == DISK_ERROR) {
                    if (copyOrNew != Consts.NEW_SURVEY) {
                        Util.showToast(mToast, R.string.error_json);
                    }
                    finish();
                } else if (result == URL_SET_ERROR) {
                    if (copyOrNew != Consts.NEW_SURVEY) {
                        Util.showToast(mToast, R.string.error_json);
                    }
                    finish();
                } else if (result == LOGIN_FAILED) {
                    if (mContext != null) {
                        if (copyOrNew != Consts.NEW_SURVEY) {
                            Util.logout(mContext, R.string.msg_pw_changed);
                        } else {
                            finish();
                        }
                    }
                } else if (result == PERMISSION_NOT_ALLOWED) {
                    if (mContext != null) {
                        if (copyOrNew != Consts.NEW_SURVEY) {
                            Util.logout(mContext, R.string.msg_permission_changed);
                        } else {
                            finish();
                        }

                    }
                } else if (result == LOGIN_SERVER_ERROR) {
                    if (copyOrNew != Consts.NEW_SURVEY) {
                        Util.showToast(mToast, R.string.msg_login_server_not_responding);
                    } else {
                        finish();
                    }

                } else if (result > 0) {
                    startSurveyTabActivity(result);
                }
            } else {
                if (copyOrNew == Consts.NEW_SURVEY) {
                    mOldSurveyFetchFailedCase();
                } else {
                    Util.showToast(mToast, R.string.error_server);
                }
                finish();
            }
        }

        @Override
        protected Long doInBackground(String... params) {
            Long surveyId = SERVER_ERROR;
            try {
                JSONObject jsonResponse = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL + "/sync?"
                        + KEY_REQUEST + "=" + VALUE_SURVEY_GET_PREVIOUS
                        + "&" + KEY_CUST_CODE + "=" + params[0].replace(" ", "%20")
                        + "&" + KEY_CURRENT_TIME + "=" + Util.getCurrentTimeInMilliSeconds());
                Log.d("Bibin", "Bibin: " + Consts.SERVER_URL + "/sync?"
                        + KEY_REQUEST + "=" + VALUE_SURVEY_GET_PREVIOUS
                        + "&" + KEY_CUST_CODE + "=" + params[0].replace(" ", "%20")
                        + "&" + KEY_CURRENT_TIME + "=" + Util.getCurrentTimeInMilliSeconds());
                String loginResponse = jsonResponse.optString(Consts.JSON_KEY_SESSION_TIME_OUT);
                if (loginResponse != null && loginResponse.equals(Consts.RESPONSE_SESSION_TIMED_OUT)) {
                    LoginStatus loginStatus = LoginActivity.login(dbHelper, mPrefs);
                    switch (loginStatus) {
                        case SUCCESS:
                            jsonResponse = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL + "/sync?"
                                    + KEY_REQUEST + "=" + VALUE_SURVEY_GET_PREVIOUS
                                    + "&" + KEY_CUST_CODE + "=" + params[0].replace(" ", "%20")
                                    + "&" + KEY_CURRENT_TIME + "=" + Util.getCurrentTimeInMilliSeconds());

                            break;

                        case FAILED:
                            return LOGIN_FAILED;

                        case NO_PERMISSION:
                            return PERMISSION_NOT_ALLOWED;

                        case SERVER_ERROR:
                            return LOGIN_SERVER_ERROR;
                    }
                }

                JSONObject survey = jsonResponse.getJSONObject("survey");
                JSONArray surveyArray = survey.getJSONArray(TableSurveyAssortimento.KEY_ARRAY_NAME);
                JSONArray competitorArray = survey.getJSONArray(TableSurveyCompetitor.KEY_ARRAY_NAME);
                JSONArray promotionsArray = survey.getJSONArray(TableSurveyPromotions.KEY_ARRAY_NAME);

                //Prevent empty json from creating survey
                if (surveyArray.length() == 0 && competitorArray.length() == 0 && promotionsArray.length() == 0)
                    return SERVER_ERROR;

                int old_cluster1 = 0;
                int old_cluster2 = 0;
                int old_cluster3 = 0;
                if (survey.getString("SCL") != null && !TextUtils.isEmpty(survey.getString("SCL"))) {
                    old_cluster1 = Integer.parseInt(survey.getString("SCL"));

                }

                if (survey.getString("SCLC") != null && !TextUtils.isEmpty(survey.getString("SCLC"))) {

                    old_cluster2 = Integer.parseInt(survey.getString("SCLC"));


                }

                if (survey.getString("SCLD") != null && !TextUtils.isEmpty(survey.getString("SCLD"))) {

                    old_cluster3 = Integer.parseInt(survey.getString("SCLD"));


                }


                if (copyOrNew == Consts.COPY_SURVEY) {
                    //get cluster from backend here

                   /* int old_cluster1 = 0;
                    if (survey.getString("SCL") != null && !TextUtils.isEmpty(survey.getString("SCL"))) {
                        old_cluster1 = Integer.parseInt(survey.getString("SCL"));
                    }*/

                    surveyId = TableSurvey.createNewSurvey(new Survey(salesPersonCode, customerCode, TableSurvey.FLAG_FROM_SERVER, Util.getCurrentDateTime(), old_cluster1, old_cluster2, old_cluster3, 0));
                    TableSurveyAssortimento.create(surveyId, salesPersonCode, survey.getJSONArray(TableSurveyAssortimento.KEY_ARRAY_NAME));
                    TableTempSurveyAssortimento.create(surveyId, salesPersonCode, survey.getJSONArray(TableSurveyAssortimento.KEY_ARRAY_NAME)); //temp table for dashboard
                    TableSurveyCompetitor.create(surveyId, salesPersonCode, survey.getJSONArray(TableSurveyCompetitor.KEY_ARRAY_NAME));
                    TableSurveyPromotions.create(surveyId, salesPersonCode, survey.getJSONArray(TableSurveyPromotions.KEY_ARRAY_NAME));
                } else if (copyOrNew == Consts.NEW_SURVEY) {
                    //  surveyId = TableSurvey.createNewSurvey(new Survey(salesPersonCode, customerCode, Util.getCurrentDateTime(), 0, 0));
                    surveyId = TableSurvey.createNewSurvey(new Survey(salesPersonCode, customerCode, Util.getCurrentDateTime(), old_cluster1, old_cluster2, old_cluster3, 0));
                    TableTempSurveyAssortimento.create(surveyId, salesPersonCode, survey.getJSONArray(TableSurveyAssortimento.KEY_ARRAY_NAME)); //temp table for dashboard
                }


                return surveyId;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                mOldSurveyFetchFailedCase();
                surveyId = SERVER_ERROR;
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                mOldSurveyFetchFailedCase();
                surveyId = URL_SET_ERROR;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                mOldSurveyFetchFailedCase();
                surveyId = DISK_ERROR;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                mOldSurveyFetchFailedCase();
                surveyId = JSON_EXCEPTION;
            }

            return surveyId;


        }
    }

    private void mOldSurveyFetchFailedCase() {
        Long surveyId;
        surveyId = TableSurvey.createNewSurvey(new Survey(salesPersonCode, customerCode, Util.getCurrentDateTime(), 0, 0, 0, 0));
        SurveyTabActivity.startActivity(SurveyCreateActivity.this, surveyId, customerName, customerCode);
        setResult(RESULT_OK);
    }

    private class NetConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context content, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                setUpCopyButton();
            }

        }


    }


}
