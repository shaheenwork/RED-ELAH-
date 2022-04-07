package com.eteam.dufour.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableItems.BaseItem;
import com.eteam.dufour.database.tables.TablePromotions;
import com.eteam.dufour.database.tables.TablePromotions.Promotion;
import com.eteam.dufour.items.IDiscount;
import com.eteam.dufour.items.IPromoItemOnline;
import com.eteam.dufour.mobile.LoginActivity.LoginStatus;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.Util;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public class DetailProdottiActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String EXTRA_ITEM = "com.eteam.dufour.mobile.ProductDetailActivity.EXTRA_ITEM";
	public static final String EXTRA_CUSTOMER_CODE = "com.eteam.dufour.mobile.ProductDetailActivity.EXTRA_CUSTOMER_CODE";

	private static final String ARG_REQUEST = "reqfor";
	private static final String ARG_CUST_CODE = "cc";
	private static final String ARG_ITEM_CODE = "ic";
	private static final String ARG_CAMP_CODE = "camc";
	private static final String ARG_LINK_CODE = "lc";
	private static final String ARG_PROMO_CODE = "pc";
	private static final String ARG_PROMO_STATUS = "ps";
	private static final String ARG_CURRENT_TIME = "ct";

	private static final String VALUE_REQUEST = "promotionDetail";

	// ===========================================================
	// Fields
	// ===========================================================


	private BaseItem   item;
	private String 	   customerCode;

	private ElahDBHelper dbHelper;

	private Promotion productOffline;
	private IPromoItemOnline productOnline;

	private ProductDetailLoadingTask mTask;
	private ProductOnlyLoading mOnlyTask;

	private Toast mToast;

	private TextView fieldTitleDesc;

	private TextView fieldItemDesc;
	private TextView fieldItemCode;
	private TextView fieldPrezzo;
	private TextView fieldScontoCanvas;
	private TextView fieldScontato;
	private TextView fieldPromo;

	private TextView fieldPromoDesc;
	private TextView fieldPromoCode;
	private TextView fieldSellOutOne;
	private TextView fieldSellOutTwo;
	private TextView fieldScontoOne;
	private TextView fieldScontoTwo;
	private TextView fieldScontoThree;
	private TextView fieldScontoFour;
	private TextView fieldScontoMerce;
	private View fieldPromoStatus;

	private View mPromoLayout;
	private View mStatisticsLayout;

	private View mBtnBack;

	private SharedPreferences mPrefs;
	// ===========================================================
	// Constructors
	// ===========================================================

	public static void startActivity(Context context,String customerCode,BaseItem item){
		Intent intent = new Intent(context, DetailProdottiActivity.class);
		intent.putExtra(EXTRA_CUSTOMER_CODE, customerCode);
		intent.putExtra(EXTRA_ITEM, item);
		context.startActivity(intent);

	}

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
		setContentView(R.layout.productdetail);
		initialize();
		setDetailsFromIntent(savedInstanceState);
		productOffline = TablePromotions.getPromotionForItem( customerCode, item.getCode());
		setProductDetails();
		restoreAsyncTask();
		setListeners();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putParcelable(EXTRA_ITEM, item);
		outState.putString(EXTRA_CUSTOMER_CODE, customerCode);
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
		if(v==mPromoLayout){
			startPromoDetailActivity();
		}
		else if(v==mStatisticsLayout){
			if(Util.haveNetworkConnection(this)){
				startSalesDetailActivity();
			}
			else{
				Util.startAlertMsgActivity(this,R.string.msg_no_net_connection
						,R.string.chiudi);
			}
		}
		else if(v==mBtnBack){
			finish();
		}
	}



	private void startSalesDetailActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,DetailSalesStatActivity.class);
		intent.putExtra(DetailSalesStatActivity.INTENT_CUSTOMER_CODE, customerCode);
		intent.putExtra(DetailSalesStatActivity.INTENT_ITEM_CODE, item.getCode());
		intent.putExtra(DetailSalesStatActivity.INTENT_ITEM_DESC, item.getDescription());
		startActivity(intent);
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}



	// ===========================================================
	// Methods
	// ===========================================================

	private void startPromoDetailActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,DetailPromozioniActivity.class);
		intent.putExtra(DetailPromozioniActivity.INTENT_ITEM_CODE, item.getCode());
		intent.putExtra(DetailPromozioniActivity.INTENT_CUSTOMER_CODE, customerCode);
		Bundle bundle = new Bundle();
		bundle.putString(DetailPromozioniActivity.INTENT_ITEM_DESC,item.getDescription());
		bundle.putString(DetailPromozioniActivity.INTENT_CAMP_CODE, productOffline.getCampCode());
		bundle.putString(DetailPromozioniActivity.INTENT_CAMP_DESC, productOnline.getCampDesc());
		bundle.putString(DetailPromozioniActivity.INTENT_PROMO_DESC, productOffline.getPromoDesc());
		bundle.putString(DetailPromozioniActivity.INTENT_PROMO_CODE, productOffline.getPromoCode());
		bundle.putString(DetailPromozioniActivity.INTENT_SELL_IN, productOffline.getSellInInzio()+" - "+productOffline.getSellInFine());
		bundle.putString(DetailPromozioniActivity.INTENT_SELL_OUT, productOffline.getSellOutInzio()+" - "+productOffline.getSellOutFine());
		bundle.putString(DetailPromozioniActivity.INTENT_PERIODO_ORDINE, productOffline.getOrderInzio()+" - "+productOffline.getOrderFine());
		bundle.putString(DetailPromozioniActivity.INTENT_SCONTI_VALUTA, productOnline.getScontoOne()+" + "+productOnline.getScontoTwo()
				+" + "+productOnline.getScontoThree()+" + "+productOnline.getScontoFour());
		bundle.putString(DetailPromozioniActivity.INTENT_SCONTO_MERCE, productOnline.getScontoMerce());

		intent.putExtra(DetailPromozioniActivity.INTENT_BUNDLE, bundle);
		startActivity(intent);
	}

	@SuppressWarnings("deprecation")
	private void restoreAsyncTask() {
		if (getLastNonConfigurationInstance() != null) {
			mTask = (ProductDetailLoadingTask) getLastNonConfigurationInstance();
			if (mTask != null) {
				if (!(mTask.getStatus()
						.equals(AsyncTask.Status.FINISHED))) {
					mTask.setContext(DetailProdottiActivity.this);
				}
			}
		}
	}

	@SuppressLint("ShowToast")
	private void initialize() {
		// TODO Auto-generated method stub
		dbHelper = ElahDBHelper.getInstance(this);
		mToast = Toast.makeText(this, "Sample", Toast.LENGTH_SHORT);

		fieldTitleDesc = (TextView) findViewById(R.id.field_description);

		fieldItemDesc = (TextView) findViewById(R.id.item_name);
		fieldItemCode = (TextView) findViewById(R.id.item_no);
		fieldPrezzo = (TextView) findViewById(R.id.field_prezzo);
		fieldScontoCanvas = (TextView) findViewById(R.id.field_sconto_canvas);
		fieldScontato = (TextView) findViewById(R.id.field_scontato);
		fieldPromo = (TextView) findViewById(R.id.field_prezzo_promo);

		fieldPromoDesc = (TextView) findViewById(R.id.title_name);
		fieldPromoCode = (TextView) findViewById(R.id.field_promo_code);
		fieldSellOutOne = (TextView) findViewById(R.id.field_sellout_one);
		fieldSellOutTwo = (TextView) findViewById(R.id.field_sellout_two);
		fieldScontoOne = (TextView) findViewById(R.id.percent1);
		fieldScontoTwo = (TextView) findViewById(R.id.percent2);
		fieldScontoThree = (TextView) findViewById(R.id.percent3);
		fieldScontoFour = (TextView) findViewById(R.id.percent4);
		fieldScontoMerce = (TextView) findViewById(R.id.percent5);

//		mLayoutTable = findViewById(R.id.layout_table);
//		mLayoutTable.forceLayout();

		fieldPromoStatus = findViewById(R.id.field_status);



		mPromoLayout = findViewById(R.id.bottom_rl);
		mStatisticsLayout = findViewById(R.id.bottom_rl2);

		mBtnBack = findViewById(R.id.btn_chiudi);

		mPrefs = getSharedPreferences(Consts.PREF_ELAH, MODE_PRIVATE);



	}

	private void setDetailsFromIntent(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			item		 = savedInstanceState.getParcelable(EXTRA_ITEM);
			customerCode = savedInstanceState.getString(EXTRA_CUSTOMER_CODE);
		}
		else{
			item 		 = getIntent().getParcelableExtra(EXTRA_ITEM);
			customerCode = getIntent().getStringExtra(EXTRA_CUSTOMER_CODE);
		}
	}

	private void setProductDetails() {
		// TODO Auto-generated method stub
		if(productOffline!=null){
			if(mTask!=null&&mTask.getStatus().equals(Status.FINISHED)){
				mTask = new ProductDetailLoadingTask(this);
				mTask.execute();
			}
			else if(mTask==null){
				mTask = new ProductDetailLoadingTask(this);
				mTask.execute();
			}
		}

		//promotion null case
		else{
			if(mOnlyTask!=null&&mOnlyTask.getStatus().equals(Status.FINISHED)){
				mOnlyTask = new ProductOnlyLoading(this);
				mOnlyTask.execute();
			}
			else if(mTask==null){
				mOnlyTask = new ProductOnlyLoading(this);
				mOnlyTask.execute();
			}
		}

	}


	private void setListeners() {
		// TODO Auto-generated method stub
		mPromoLayout.setOnClickListener(this);
		mStatisticsLayout.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
	}


	@SuppressLint("SetTextI18n")
	public void setUpView() {
		// TODO Auto-generated method stub
		mPromoLayout.setVisibility(View.VISIBLE);
		mPromoLayout.forceLayout();
		fieldTitleDesc.setText( item.getCode()+" - "+item.getDescription());

		float unitprice = Util.parseNumber(productOnline.getUnitPrice());
		float finalprice = Util.parseNumber(productOnline.getFinalPrice());
		float discount = Util.parseNumber(productOnline.getDiscountPrice());
		float prezzoScontao = unitprice-(unitprice*discount/100);

		fieldItemCode.setText(item.getCode());
		fieldItemDesc.setText(item.getDescription());
		fieldPromoDesc.setText(productOnline.getCampDesc());
		fieldPromoCode.setText(productOffline.getPromoCode());
		fieldSellOutOne.setText(productOffline.getSellOutInzio());
		fieldSellOutTwo.setText(productOffline.getSellOutFine());
		fieldScontoOne.setText(Util.getAsElahNumFormatForDetails(productOnline.getScontoOne())+"%");
		fieldScontoTwo.setText(Util.getAsElahNumFormatForDetails(productOnline.getScontoTwo())+"%");
		fieldScontoThree.setText(Util.getAsElahNumFormatForDetails(productOnline.getScontoThree())+"%");
		fieldScontoFour.setText(Util.getAsElahNumFormatForDetails(productOnline.getScontoFour())+"%");
		fieldScontoMerce.setText(productOnline.getScontoMerce());
		String euro = "\u20ac";
		fieldPrezzo.setText(Util.getAsElahNumFormatForDetails(String.valueOf(unitprice))+" "+euro);
		fieldScontoCanvas.setText(Util.getAsElahNumFormatForDetails(String.valueOf(discount))+" %");
		fieldScontato.setText(Util.getAsElahNumFormatForDetails(String.valueOf(prezzoScontao))+" "+euro);
		fieldPromo.setText(Util.getAsElahNumFormatForDetails(String.valueOf(finalprice))+" "+euro);

		String status = productOffline.getPromoStatus();

		if(status!=null){
			if(status.equals("1")){
				fieldPromoStatus.setBackgroundResource(R.color.promo_active);
			}
			else if(status.equals("2")){
				fieldPromoStatus.setBackgroundResource(R.color.promo_expire);
			}
			else if(status.equals("3")){
				fieldPromoStatus.setBackgroundResource(R.color.promo_future);
			}
			else{
				fieldPromoStatus.setBackgroundResource(R.color.promo_inactive);
			}
		}
		else{
			fieldPromoStatus.setBackgroundResource(R.color.promo_inactive);
		}

	}

	public void setUpDiscountOnly(IDiscount discountItem) {
        String euro = "\u20ac";
		String itemCode = item.getCode();
		String desc		= item.getDescription();
		fieldTitleDesc.setText( itemCode+" - "+desc);

		mPromoLayout.setVisibility(View.GONE);
		fieldItemCode.setText(itemCode);
		fieldItemDesc.setText(desc);

		fieldPrezzo.setText(Util.getAsElahNumFormatForDetails(discountItem.getUItem())+" "+euro);
		fieldScontoCanvas.setText(Util.getAsElahNumFormatForDetails(discountItem.getDiscount())+" %");
		fieldScontato.setText(Util.getAsElahNumFormatForDetails(discountItem.getUItem())+" "+euro);
		fieldPromo.setText("");
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class ProductDetailLoadingTask extends AsyncTask<String, Integer, Integer>{

		private static final int SERVER_ERROR = -101;
		private static final int ITEM_LOADED = -102;
		private static final int JSON_EXCEPTION = -103;
		private static final int DISK_ERROR = -104;
		private static final int URL_SET_ERROR = -105;
		private static final int LOGIN_FAILED = -112;
		private static final int PERMISSION_NOT_ALLOWED = -113;
		private static final int LOGIN_SERVER_ERROR = -114;
		private static final int NO_DATA_EXCEPTION = 107;

		private ElahProgress mSyncProgress;
		private Context 	   mContext;

		private ProductDetailLoadingTask(Context context){
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
						Util.showToast(mToast, R.string.error_no_data);
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
					case LOGIN_FAILED:
						if(mContext!=null)
							Util.logout(mContext,R.string.msg_pw_changed);
						break;
					case PERMISSION_NOT_ALLOWED:
						if(mContext!=null)
							Util.logout(mContext,R.string.msg_permission_changed);
						break;
					case LOGIN_SERVER_ERROR:
						Util.showToast(mToast, R.string.msg_login_server_not_responding);
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
						ARG_REQUEST+"="+VALUE_REQUEST+"&"+ARG_CUST_CODE+"="+customerCode+"&"
						+ARG_ITEM_CODE+"="+item.getCode()+"&"+ARG_PROMO_CODE+"="+productOffline.getPromoCode()+"&"
						+ARG_CAMP_CODE+"="+productOffline.getCampCode()+"&"+ARG_LINK_CODE+"="+productOffline.getLinkCodeAsUrl()+"&"
						+ARG_PROMO_STATUS+"="+productOffline.getPromoStatus()
						+"&"+ARG_CURRENT_TIME+"="+Util.getCurrentTimeInMilliSeconds());

				String status = json.optString(Consts.JSON_KEY_SESSION_TIME_OUT);
				if(status!=null&&status.equals(Consts.RESPONSE_SESSION_TIMED_OUT)){
					LoginStatus loginStatus = LoginActivity.login(dbHelper,mPrefs);
					switch(loginStatus){
						case SUCCESS:
							json = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL+"/sync?"+
									ARG_REQUEST+"="+VALUE_REQUEST+"&"+ARG_CUST_CODE+"="+customerCode+"&"
									+ARG_ITEM_CODE+"="+item.getCode()+"&"+ARG_PROMO_CODE+"="+productOffline.getPromoCode()+"&"
									+ARG_CAMP_CODE+"="+productOffline.getCampCode()+"&"+ARG_LINK_CODE+"="+productOffline.getLinkCodeAsUrl()+"&"
									+ARG_PROMO_STATUS+"="+productOffline.getPromoStatus()
									+"&"+ARG_CURRENT_TIME+"="+Util.getCurrentTimeInMilliSeconds());
							break;
						case FAILED:
							return LOGIN_FAILED;
						case NO_PERMISSION:
							return PERMISSION_NOT_ALLOWED;
						case SERVER_ERROR:
							return LOGIN_SERVER_ERROR;
					}
				}
				productOnline = new IPromoItemOnline(json);

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
			catch (IllegalStateException e) {
				// TODO: handle exception
				return NO_DATA_EXCEPTION;
			}
		}

	}


	private class ProductOnlyLoading extends AsyncTask<String, Integer, Integer>{
		private static final int SERVER_ERROR = -101;
		private static final int ITEM_LOADED = -102;
		private static final int JSON_EXCEPTION = -103;
		private static final int DISK_ERROR = -104;
		private static final int URL_SET_ERROR = -105;
		private static final int LOGIN_FAILED = -112;
		private static final int PERMISSION_NOT_ALLOWED = -113;
		private static final int LOGIN_SERVER_ERROR = -114;

		private IDiscount discountItem;

		private ElahProgress mSyncProgress;
		private Context 	   mContext;

		public ProductOnlyLoading(Context context) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.discountItem = new IDiscount();
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
						setUpDiscountOnly(discountItem);
						break;
					case JSON_EXCEPTION:
						Util.showToast(mToast, R.string.error_no_data);
						finish();
						break;
					case DISK_ERROR:
						Util.showToast(mToast, R.string.error_server);
						finish();
						break;
					case URL_SET_ERROR:
						Util.showToast(mToast, R.string.error_server);
						finish();
						break;
					case LOGIN_FAILED:
						if(mContext!=null)
							Util.logout(mContext,R.string.msg_pw_changed);
						break;
					case PERMISSION_NOT_ALLOWED:
						if(mContext!=null)
							Util.logout(mContext,R.string.msg_permission_changed);
						break;
					case LOGIN_SERVER_ERROR:
						Util.showToast(mToast, R.string.msg_login_server_not_responding);
						break;
					default:
						break;
				}
			}
		}


		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{

				JSONObject json = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL+"/sync?"+
						ARG_REQUEST+"="+"itemDetail"+"&"+"cust_code"+"="+customerCode+"&"
						+"item_code"+"="+item.getCode()+"&"+"&"+ARG_CURRENT_TIME+Util.getCurrentTimeInMilliSeconds());

				String status = json.optString(Consts.JSON_KEY_SESSION_TIME_OUT);
				if(status!=null&&status.equals(Consts.RESPONSE_SESSION_TIMED_OUT)){
					LoginStatus loginStatus = LoginActivity.login(dbHelper,mPrefs);
					switch(loginStatus){
						case SUCCESS:
							json = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL+"/sync?"+
									ARG_REQUEST+"="+"itemDetail"+"&"+"cust_code"+"="+customerCode+"&"
									+"item_code"+"="+item.getCode()+"&"+"&"+ARG_CURRENT_TIME+Util.getCurrentTimeInMilliSeconds());
							break;

						case FAILED:
							return LOGIN_FAILED;

						case NO_PERMISSION:
							return PERMISSION_NOT_ALLOWED;

						case SERVER_ERROR:
							return LOGIN_SERVER_ERROR;
					}
				}
				discountItem.setDiscount(json.getString("D"));
				discountItem.setItemCode(json.getString("I"));
				discountItem.setUItem(json.getString("U"));

				return ITEM_LOADED;
			}catch (ClientProtocolException e) {
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
