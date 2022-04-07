package com.eteam.dufour.mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.async.SyncTask;
import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableLogin;
import com.eteam.dufour.database.tables.TableSyncInfo;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.Util;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.fragment.app.FragmentActivity;

import static com.eteam.dufour.mobile.ElahApplication.mFirebaseAnalytics;

public class ElahSyncPage extends FragmentActivity implements OnClickListener {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================
    private Button btnPopUp, btnSync;
    private View btnGestionOridini, btnGestionePdv;

    private View layoutSyncStat;
    private TextView fieldDate;
    private TextView fieldTime;
    private TextView fieldAccountDetails;
    private TextView lblVersion;

    private View layoutUpdate;
    private View btnUpdate;
    private View btnClose;

    private PopupWindow popUpEsc;
    private View mBtnEsci;


    private ElahDBHelper dbHelper;
    private SyncPageSyncTask mSyncTask;
    private UpdateCheckTask mUpdateTask;

    private Button btnExport;


    private Toast mToast;

    private String userName;
    private String passWord;
    private String versionId;
    private SharedPreferences mPrefs;
    private View mLblSyncNeeded;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.syncpage);

        btnSync = (Button) findViewById(R.id.switch_synch);
        btnGestionePdv = findViewById(R.id.switch_centerbar);
        btnGestionOridini = findViewById(R.id.btn_gestione_ordini);
        btnPopUp = (Button) findViewById(R.id.shadow_b2);
        layoutSyncStat = findViewById(R.id.layout_sync_field);
        btnExport = (Button) findViewById(R.id.export_db_button);

        lblVersion = (TextView) findViewById(R.id.lbl_version);

        layoutUpdate = findViewById(R.id.layout_update);
        btnClose = findViewById(R.id.btn_close);
        btnUpdate = findViewById(R.id.btn_update);

        fieldAccountDetails = (TextView) findViewById(R.id.switch_title);
        fieldDate = (TextView) findViewById(R.id.field_date);
        fieldTime = (TextView) findViewById(R.id.field_time);

        mLblSyncNeeded = findViewById(R.id.lbl_update_app);

        btnSync.setOnClickListener(this);
        btnGestionePdv.setOnClickListener(this);
        btnPopUp.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        lblVersion.setText(Util.getAppFullVersionName(this));

        dbHelper = ElahDBHelper.getInstance(this);

        mToast = Toast.makeText(this, "Sample", Toast.LENGTH_SHORT);
       // Util.setUpToast(mToast);

        SharedPreferences mPref = getSharedPreferences(Consts.PREF_ELAH, MODE_PRIVATE);
        userName = mPref.getString(LoginActivity.PREF_USER_NAME, "");
        passWord = TableLogin.getPassword(userName);
        fieldAccountDetails.setText(TableLogin.getCompleteName(userName));
        versionId = Util.getApplicationVersion(this);
        mPrefs = getSharedPreferences(Consts.PREF_ELAH, MODE_PRIVATE);
        if (mSyncTask == null || (mSyncTask != null && mSyncTask.getStatus().equals(Status.FINISHED))) {
            Util.checkAndPromptUpdate(this, mPrefs);
        }
        startUpdateCheckTask();
        setPopUpWindow();
    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        if (mSyncTask == null || (mSyncTask != null && mSyncTask.getStatus().equals(Status.FINISHED))) {
            Util.checkAndPromptUpdate(this, mPrefs);
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mUpdateTask != null)
            mUpdateTask.cancel(true);
        if (mSyncTask != null)
            mSyncTask.cancel(true);
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mSyncTask != null) {
            if (mSyncTask.getStatus().equals(Status.RUNNING)) {
                return;
            } else if (mSyncTask.getStatus() == Status.FINISHED) {
            }
        }
        checkSyncStatAndSetButton();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
//		unregisterReceiver(mReceiver);
//		}


    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnSync) {
            if (Util.haveNetworkConnection(this)) {
                if (mSyncTask == null || mSyncTask.getStatus() == Status.FINISHED) {
                    SyncPageSyncTask mSyncTask = new SyncPageSyncTask(dbHelper, mPrefs, userName, passWord, versionId);
                    this.mSyncTask = (SyncPageSyncTask) mSyncTask.execute();

                }
            } else {
                Util.showToast(mToast, R.string.msg_intenet_not_connected);
            }

        }
        if (v == btnGestionePdv) {
            Intent intent = new Intent(this, ElahToBeSent.class);
            startActivity(intent);
            finish();
        }
        if (v == btnPopUp) {
            try {

                btnPopUp.post(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub
                        popUpEsc.showAsDropDown(btnPopUp);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (v == btnExport) {
            FileChannel src = null;
            FileChannel dst = null;
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "//data//" + getPackageName() + "//databases//" + Consts.DB_NAME;
                    String backupDBPath = Consts.DB_NAME;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        src = new FileInputStream(currentDB).getChannel();
                        dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (v == mBtnEsci) {
            popUpEsc.dismiss();
            finish();
        }
        if (v == btnClose) {
            layoutUpdate.setVisibility(View.GONE);
        }
        if (v == btnUpdate) {
            btnSync.performClick();
            layoutUpdate.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (!popUpEsc.isShowing()) {
            super.onBackPressed();
        } else {
            popUpEsc.dismiss();
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    @SuppressWarnings("deprecation")
    private void setPopUpWindow() {
        // TODO Auto-generated method stub

        popUpEsc = new PopupWindow(this);
        popUpEsc.setBackgroundDrawable(new BitmapDrawable());
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.popup_esci, (ViewGroup) findViewById(R.id.root), false);
        popUpEsc.setContentView(v);
        popUpEsc.setOutsideTouchable(true);

        mBtnEsci = v.findViewById(R.id.btn_esci);
//		View mBtnSchema = v.findViewById(R.id.btn_schema);

        mBtnEsci.setOnClickListener(this);
//		mBtnSchema.setVisibility(View.GONE);

        popUpEsc.setWidth(ListPopupWindow.WRAP_CONTENT);
        popUpEsc.setHeight(ListPopupWindow.WRAP_CONTENT);

    }

    private void startUpdateCheckTask() {
        // TODO Auto-generated method stub
        if (Util.haveNetworkConnection(this)) {
            if (TableLogin.isSyncCompleted(userName)) {
                if (mUpdateTask != null && mUpdateTask.getStatus() == Status.FINISHED) {
                    mUpdateTask = new UpdateCheckTask();
                    mUpdateTask.execute();
                } else if (mUpdateTask == null) {
                    mUpdateTask = new UpdateCheckTask();
                    mUpdateTask.execute();
                }
            }
//			Intent i = new Intent(this, SyncFromServer.class);
//			i.putExtra(SyncFromServer.INTENT_USER_NAME, userName);
//			startService(i);
//			mSyncProgress.show();
        }
    }


    private void checkSyncStatAndSetButton() {
        // TODO Auto-generated method stub

        if (TableLogin.isSyncCompleted(userName)) {
            btnGestionePdv.setVisibility(View.VISIBLE);
           // btnGestionOridini.setVisibility(View.VISIBLE);

            String[] array = TableSyncInfo.getSyncDate().split("/");
            if (array != null && array.length == 2) {
                fieldDate.setText(array[0]);
                fieldTime.setText(array[1]);
            } else {
                if (Consts.DEBUGGABLE)
                    Log.e("Log", "Check syncdate is set as date/time in sync table");
            }
            layoutSyncStat.setVisibility(View.VISIBLE);
            mLblSyncNeeded.setVisibility(View.GONE);
        } else {
            btnGestionePdv.setVisibility(View.GONE);
            btnGestionOridini.setVisibility(View.GONE);
            layoutSyncStat.setVisibility(View.GONE);
            mLblSyncNeeded.setVisibility(View.VISIBLE);
        }
//		xdb.close();
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class UpdateCheckTask extends AsyncTask<Void, Void, Boolean> {
        private static final String KEY_UPDATE_STATUS = "U";


        private static final String RESPONSE_UPDATE_AVAILABLE = "1";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result) {
                layoutUpdate.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                JSONObject updateResponse = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL
                        + "/sync?reqfor=checkupdates" + "&ct="
                        + Util.getCurrentTimeInMilliSeconds());

                String updateStatus = updateResponse.optString(KEY_UPDATE_STATUS);

                if (updateStatus != null && updateStatus.equals(RESPONSE_UPDATE_AVAILABLE)) {
                    return true;
                }

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
            return false;
        }

    }

    private class SyncPageSyncTask extends SyncTask {

        // ===========================================================
        // Constants
        // ===========================================================
        // ===========================================================
        // Fields
        // ===========================================================
        private ElahProgress mSyncProgress;

        // ===========================================================
        // Constructors
        // ===========================================================

        public SyncPageSyncTask(ElahDBHelper dbHelper, SharedPreferences prefs,
                                String userName, String password, String versionId) {
            super(dbHelper, prefs, userName, password, versionId);
            // TODO Auto-generated constructor stub
            mSyncProgress = new ElahProgress(ElahSyncPage.this);
            mSyncProgress.setCanceledOnTouchOutside(false);
            mSyncProgress.setCancelable(false);
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

            if (mSyncProgress != null) {
                try {
                    mSyncProgress.show(R.string.senting);
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel(true);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            if (mSyncProgress != null) {
                try {
                    if (!mSyncProgress.isShowing())
                        mSyncProgress.show();
                    if (values[0] == SURVEY_SEND_SUCCESS) {
                        mSyncProgress.setMessage(R.string.senting_successful);
                    } else if (values[0] == SYNC_STARTED) {
                        mSyncProgress.setMessage(R.string.syncing);
                        mSyncProgress.setProgress(0);
                    } else {
                        mSyncProgress.setMessage(R.string.syncing);
                        mSyncProgress.setProgress(values[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel(true);
                }
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (mSyncProgress != null) {
                    mSyncProgress.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }
            switch (result) {
                case SYNC_FROM_SERVER_SUCCESSFUL:
                    TableLogin.setSyncStatus(userName, TableLogin.SYNC_COMPLETED);

                    Util.showToast(mToast, R.string.sync_success);
                    mSetCrashlyticsSyncEvent( "syc from server succesful");
                    break;
                case SYNC_FROM_SERVER_FAILED:
                    TableLogin.setSyncStatus(userName, TableLogin.SYNC_INCOMPLETE);
                    Util.showToast(mToast, R.string.sync_failed);
                    mSetCrashlyticsSyncEvent( "syc from server failed");
                    break;
                case SURVEY_SEND_FAILURE:
                    Util.showToast(mToast, R.string.sync_to_server_failed);
                    mSetCrashlyticsSyncEvent("survey send failure");
                    break;
                case NO_UPDATE_AVAILABLE:
                    Util.showToast(mToast, R.string.msg_no_update_available);
                    mSetCrashlyticsSyncEvent( "no update available");
                    break;
                case LOGIN_FAILED:
                    if (ElahSyncPage.this != null)
                        Util.logout(ElahSyncPage.this, R.string.msg_pw_changed);
                    mSetCrashlyticsSyncEvent( "login failed");
                    break;
                case PERMISSION_NOT_ALLOWED:
                    if (ElahSyncPage.this != null)
                        Util.logout(ElahSyncPage.this, R.string.msg_permission_changed);
                    mSetCrashlyticsSyncEvent( "permission not allowed");
                    break;
                case LOGIN_SERVER_ERROR:
                    Util.showToast(mToast, R.string.msg_login_server_not_responding);
                    mSetCrashlyticsSyncEvent( "server error");
                    break;
                default:
                    break;
            }
            checkSyncStatAndSetButton();
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            if (mSyncProgress != null) {
                mSyncProgress.dismiss();
                mSyncProgress = null;

            }
            TableLogin.setSyncStatus(userName, TableLogin.SYNC_INCOMPLETE);

            Util.showToast(mToast, R.string.sync_failed);
        }

        // ===========================================================
        // Methods
        // ===========================================================

        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================

    }


    private void mSetCrashlyticsSyncEvent(String failure_type) {

        if (!BuildConfig.DEBUG) {
         /* nwwd  Answers.getInstance().logCustom(new CustomEvent(Consts.KEY_SYNC_EVENT)

                    .putCustomAttribute(Consts.KEY_SYNC_STATUS, failure_type)
                    .putCustomAttribute(Consts.KEY_ANDROID_VERSION, Build.VERSION.RELEASE)
                    .putCustomAttribute(Consts.KEY_SYNC_TIME, getCurrentDate())
                    .putCustomAttribute(Consts.KEY_USERNAME, userName));*/


            Bundle params = new Bundle();
            params.putString(Consts.KEY_SYNC_STATUS, failure_type);
            params.putString(Consts.KEY_ANDROID_VERSION,  Build.VERSION.RELEASE);
            params.putString(Consts.KEY_SYNC_TIME, getCurrentDate());
            params.putString(Consts.KEY_USERNAME, userName);
            mFirebaseAnalytics.logEvent(Consts.KEY_SYNC_EVENT, params);

        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyyHH:mm:ss:SSS
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

}
