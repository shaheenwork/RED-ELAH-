package com.eteam.dufour.mobile;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.items.IStatistics;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.Util;

public class DetailSalesStatActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String INTENT_ITEM_CODE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_ITEM_CODE";
	public static final String INTENT_CUSTOMER_CODE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_CUSTOMER_CODE";
	public static final String INTENT_ITEM_DESC = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_ITEM_DESC";

	private static final String ARG_REQUEST = "reqfor";
	private static final String ARG_CUST_CODE = "cust_code";
	private static final String ARG_ITEM_CODE = "item_code";
	private static final String ARG_CURRENT_DATE = "current_date";
	private static final String ARG_CURRENT_TIME = "ct";

	private static final String VALUE_REQUEST = "statistics";
	// ===========================================================
	// Fields
	// ===========================================================


	private String itemCode;
	private String customerCode;
	private String itemDesc;

	private IStatistics stats;

	private SalesStatLoadingTask mTask;

	private Toast mToast;

	private TextView fieldPPD;
	private TextView fieldPrecendenteOne;
	private TextView fieldPCD;
	private TextView fieldPrecendenteTwo;
	private TextView fieldPND;
	private TextView fieldPrecendentThree;
	private TextView fieldOrdine;
	private TextView fieldCosegna;

	private TextView fieldLpr;
	private TextView fieldTitleDesc;

	private View	mBtnBack;
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
		setContentView(R.layout.salesstatistics);
		initialize();
		setListeners();
		setDetailsFromIntent(savedInstanceState);
//		productOffline = PromoDBOperation.getPromoDetails(dbHelper, customerCode, itemCode);
		setProductDetails();
		restoreAsyncTask();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString(INTENT_ITEM_CODE, itemCode);
		outState.putString(INTENT_CUSTOMER_CODE, customerCode);
		outState.putString(INTENT_ITEM_DESC,itemDesc);
		super.onSaveInstanceState(outState);

	}

	@SuppressWarnings("deprecation")
	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub

		if (mTask != null){
			Log.d("Log", "Retainin AsynTask");
			return (mTask);
		}
		return super.onRetainNonConfigurationInstance();
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mBtnBack){
			finish();
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================
	@SuppressLint("ShowToast")
	private void initialize() {
		// TODO Auto-generated method stub
		mToast = Toast.makeText(this, "Sample", Toast.LENGTH_SHORT);

		mBtnBack = findViewById(R.id.btn_esc);

		fieldPPD = (TextView) findViewById(R.id.statistics1);
		fieldPrecendenteOne = (TextView) findViewById(R.id.field_precendente_one);
		fieldPCD = (TextView) findViewById(R.id.statistics2);
		fieldPrecendenteTwo = (TextView) findViewById(R.id.field_precendente_two);
		fieldPND = (TextView) findViewById(R.id.statistics3);
		fieldPrecendentThree = (TextView) findViewById(R.id.field_precendente_three);
		fieldOrdine = (TextView) findViewById(R.id.field_ultimo_ordine);
		fieldCosegna = (TextView) findViewById(R.id.field_ultima_cosegna);

		fieldTitleDesc =(TextView) findViewById(R.id.field_description);
		fieldLpr = (TextView) findViewById(R.id.field_prezzo_di_listino);

	}
	@SuppressWarnings("deprecation")
	private void restoreAsyncTask() {
        if (getLastNonConfigurationInstance() != null) {
            mTask = (SalesStatLoadingTask) getLastNonConfigurationInstance();
            if (mTask != null) {
                if (!(mTask.getStatus()
                        .equals(AsyncTask.Status.FINISHED))) {
                    mTask.setContext(DetailSalesStatActivity.this);
                }
            }
        }
    }


	private void setListeners() {
		// TODO Auto-generated method stub
		mBtnBack.setOnClickListener(this);
	}



	private void setDetailsFromIntent(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			itemCode = savedInstanceState.getString(INTENT_ITEM_CODE);
			customerCode = savedInstanceState.getString(INTENT_CUSTOMER_CODE);
			itemDesc = savedInstanceState.getString(INTENT_ITEM_DESC);
		}
		else{
			itemCode = getIntent().getStringExtra(INTENT_ITEM_CODE);
			customerCode = getIntent().getStringExtra(INTENT_CUSTOMER_CODE);
			itemDesc = getIntent().getStringExtra(INTENT_ITEM_DESC);
		}
	}

	private void setProductDetails() {
		// TODO Auto-generated method stub
		if(mTask!=null&&mTask.getStatus().equals(Status.FINISHED)){
			mTask = new SalesStatLoadingTask(this);
			mTask.execute();
		}
		else if(mTask==null){
			mTask = new SalesStatLoadingTask(this);
			mTask.execute();
		}

	}



	public void setUpView() {
		// TODO Auto-generated method stub
		fieldTitleDesc.setText(itemDesc);
		fieldPPD.setText(stats.getPPDAsDate());
		fieldPrecendenteOne.setText(stats.getPpq()+stats.getPpp());
		fieldPCD.setText(stats.getPCDAsDate());
		fieldPrecendenteTwo.setText(stats.getPcq()+stats.getPcp());
		fieldPND.setText(stats.getPNDAsDate());
		fieldPrecendentThree.setText(stats.getPmq()+stats.getPmp());
		fieldOrdine.setText(stats.getOrdine());
		fieldCosegna.setText(stats.getCosegna());
		fieldLpr.setText(stats.getLpr());


	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class SalesStatLoadingTask extends AsyncTask<String, Integer, Integer>{

		private static final int SERVER_ERROR = -101;
		private static final int ITEM_LOADED = -102;
		private static final int JSON_EXCEPTION = -103;
		private static final int DISK_ERROR = -104;
		private static final int URL_SET_ERROR = -105;

		private ElahProgress mSyncProgress;
		private Context 	   mContext;

		private SalesStatLoadingTask(Context context){
			this.mContext = context;
		}


		public void setContext(Context context){
			this.mContext = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mSyncProgress = new ElahProgress(mContext);
			mSyncProgress.setCanceledOnTouchOutside(false);
			mSyncProgress.setCancelable(false);
			if(mSyncProgress!=null){
	    			 mSyncProgress.show(R.string.msg_loading);
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(mSyncProgress!=null){
	        	mSyncProgress.dismiss();
	        }
			if(result!=null){
				switch (result) {
					case SERVER_ERROR:
						Util.showToast(mToast, R.string.error_server);
						finish();
						break;
					case ITEM_LOADED:
						setUpView();
						break;
					case JSON_EXCEPTION:
						Util.showToast(mToast, R.string.error_json);
						finish();
						break;
					case DISK_ERROR:
						Util.showToast(mToast, R.string.error_json);
						finish();
						break;
					case URL_SET_ERROR:
						Util.showToast(mToast, R.string.error_json);
						finish();
						break;
				default:
					break;
				}
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			try {
				JSONObject json = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL+"/sync?"+
						ARG_REQUEST+"="+VALUE_REQUEST+"&"+ARG_CUST_CODE+"="+customerCode
						+"&"+ARG_ITEM_CODE+"="+itemCode+"&"+ARG_CURRENT_DATE+"="+Util.getCurrentDate()
						+"&"+ARG_CURRENT_TIME+"="+Util.getCurrentTimeInMilliSeconds());
				/*JSONObject json = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL+"/sync?"+
						ARG_REQUEST+"="+VALUE_REQUEST+"&"+ARG_CUST_CODE+"="+customerCode
						+"&"+ARG_ITEM_CODE+"="+itemCode+"&"+ARG_CURRENT_DATE+"="+"2016-04-01"
						+"&"+ARG_CURRENT_TIME+"="+Util.getCurrentTimeInMilliSeconds());*/
				stats = new IStatistics(json);
				return ITEM_LOADED;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return SERVER_ERROR;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return URL_SET_ERROR;
			} catch (IOException e) {

				e.printStackTrace();
				return DISK_ERROR;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return JSON_EXCEPTION;
			}
		}

	}



}
