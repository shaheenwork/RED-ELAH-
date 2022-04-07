package com.eteam.dufour.mobile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.database.tables.TableItems;
import com.eteam.dufour.database.tables.TableItems.BaseItem;
import com.eteam.dufour.database.tables.TablePromotions;
import com.eteam.dufour.database.tables.TablePromotions.Promotion;
import com.eteam.dufour.items.IPromoItemOnline;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.Util;

public class DetailPromozioniActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String INTENT_BUNDLE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_BUNDLE";
	public static final String INTENT_ITEM_CODE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_ITEM_CODE";
	public static final String INTENT_CUSTOMER_CODE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_CUSTOMER_CODE";
	public static final String INTENT_ITEM_DESC = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_ITEM_DESC";
	public static final String INTENT_CAMP_DESC = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_CAMP_DESC";
	public static final String INTENT_CAMP_CODE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_CAMP_CODE";
	public static final String INTENT_PROMO_CODE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_PROMO_CODE";
	public static final String INTENT_PROMO_DESC = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_PROMO_DESC";
	public static final String INTENT_SELL_IN =	"com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_SELL_IN";
	public static final String INTENT_SELL_OUT = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_SELL_OUT";
	public static final String INTENT_PERIODO_ORDINE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_PERIODO_ORDINE";
	public static final String INTENT_SCONTI_VALUTA = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_SCONTI_VALUTA";
	public static final String INTENT_SCONTO_MERCE = "com.eteam.dufour.release.backup.ProductDetailActivity.INTENT_SCONTO_MERCE";
	
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
	
	private Bundle mData;
	private String promoCode;
//	private String itemCode;
//	private String customerCode;
//	private String itemDesc;
	
	
	private Promotion		 productOffline;
	private IPromoItemOnline productOnline;
	
	private PromoDetailLoadingTask mTask;
	
	private Toast mToast;
	
	private LinearLayout mItemLayouts;
	
	private TextView fieldTitleDesc;
	
	private TextView fieldCampCodice;
	private TextView fieldCampDescrizione;
	
	private TextView fieldPromoCode;
	private TextView fieldPromoDescrizione;
	private TextView fieldSellIn;
	private TextView fieldSellOut;
	private TextView fieldPeriodoOrdine;
	private TextView fieldScontiValuta;
	private TextView fieldScontoMerce;
	private Button 	 mBtnBack;
	
	private String itemCode;
	private String customerCode;
	
	private ArrayList<BaseItem> promoItems;
	
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
		setContentView(R.layout.promotion_details);
		initialize();
		setDetailsFromIntent(savedInstanceState);
//		productOffline = PromoDBOperation.getPromoDetails(dbHelper, customerCode, itemCode);
		if(mData==null){
			productOffline = TablePromotions.getPromotionForPromo(customerCode, promoCode);
			setProductDetails();
			restoreAsyncTask();
		}
		else{
			setView(mData);
		}
		setUpPromoItemRows();
	}	
	






	private void setView(Bundle mData) {
		// TODO Auto-generated method stub
		 mBtnBack.setText(R.string.lbl_dett__prodotto);
		 mBtnBack.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_5));
		 mBtnBack.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.left_arrow),null, null, null);
		 
		 fieldTitleDesc.setText(mData.getString(INTENT_PROMO_CODE)+" - "
				 	+mData.getString(INTENT_PROMO_DESC));
		 
		 fieldCampCodice.setText(mData.getString(INTENT_CAMP_CODE));
		 fieldCampDescrizione.setText(mData.getString(INTENT_CAMP_DESC));
		
		 fieldPromoCode.setText(mData.getString(INTENT_PROMO_CODE));
		 fieldPromoDescrizione.setText(mData.getString(INTENT_PROMO_DESC));
		 fieldSellIn.setText(mData.getString(INTENT_SELL_IN));
		 fieldSellOut.setText(mData.getString(INTENT_SELL_OUT));
		 fieldPeriodoOrdine.setText(mData.getString(INTENT_PERIODO_ORDINE));
		 fieldScontiValuta.setText(mData.getString(INTENT_SCONTI_VALUTA));
		 fieldScontoMerce.setText(mData.getString(INTENT_SCONTO_MERCE));
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		if(mData!=null)
			outState.putBundle(INTENT_BUNDLE, mData);
		
		outState.putString(INTENT_CUSTOMER_CODE, customerCode);
		outState.putString(INTENT_ITEM_CODE, itemCode);
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
	// ===========================================================
	// Methods
	// ===========================================================
	
	@SuppressWarnings("deprecation")
	private void restoreAsyncTask() {
        if (getLastNonConfigurationInstance() != null) {
            mTask = (PromoDetailLoadingTask) getLastNonConfigurationInstance();
            if (mTask != null) {
                if (!(mTask.getStatus()
                        .equals(AsyncTask.Status.FINISHED))) {
                    mTask.setContext(DetailPromozioniActivity.this);
                }
            }
        }
    }

	@SuppressLint("ShowToast")
	private void initialize() {
		mToast = Toast.makeText(this, "Sample", Toast.LENGTH_SHORT);
		
		 fieldCampCodice= (TextView) findViewById(R.id.field_camp_code);
		 fieldCampDescrizione= (TextView) findViewById(R.id.field_camp_desc);
		
		 fieldPromoCode= (TextView) findViewById(R.id.field_promo_code);
		 fieldPromoDescrizione= (TextView) findViewById(R.id.field_promo_desc);
		 fieldSellIn= (TextView) findViewById(R.id.field_sell_in);
		 fieldSellOut= (TextView) findViewById(R.id.field_sell_out);
		 fieldPeriodoOrdine= (TextView) findViewById(R.id.field_periodo_oridine);
		 fieldScontiValuta= (TextView) findViewById(R.id.field_sconto_valuta);
		 fieldScontoMerce = (TextView) findViewById(R.id.field_sconto_merce);
		 
		 fieldTitleDesc = (TextView) findViewById(R.id.field_title_desc);
		 
		 mItemLayouts = (LinearLayout) findViewById(R.id.layout_items);
		 
		 mBtnBack = (Button) findViewById(R.id.btn_back);
		 
		 promoItems = new ArrayList<BaseItem>();
		
		 mBtnBack.setOnClickListener(this);
		
	}

	private void setDetailsFromIntent(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			mData = savedInstanceState.getBundle(INTENT_BUNDLE);
			promoCode = savedInstanceState.getString(INTENT_PROMO_CODE);
			customerCode = savedInstanceState.getString(INTENT_CUSTOMER_CODE);
		}
		else{
			mData = getIntent().getBundleExtra(INTENT_BUNDLE);
			promoCode = getIntent().getStringExtra(INTENT_PROMO_CODE);
			customerCode = getIntent().getStringExtra(INTENT_CUSTOMER_CODE);
		}
	}
	
	private void setProductDetails() {
		// TODO Auto-generated method stub
		if(mTask!=null&&mTask.getStatus().equals(Status.FINISHED)){
			mTask = new PromoDetailLoadingTask(this);
			mTask.execute();
		}
		else if(mTask==null){
			mTask = new PromoDetailLoadingTask(this);
			mTask.execute();
		}
		
	}
	
	

	public void setUpView() {
		 fieldTitleDesc.setText(promoCode+" - "+productOffline.getPromoDesc());
		
		 fieldCampCodice.setText(productOffline.getCampCode());
		 fieldCampDescrizione.setText(productOnline.getCampDesc());
		
		 fieldPromoCode.setText(productOffline.getPromoCode());
		 fieldPromoDescrizione.setText(productOffline.getPromoDesc());
		 fieldSellIn.setText(productOffline.getSellInInzio()+" - "+productOffline.getSellInFine());
		 fieldSellOut.setText(productOffline.getSellOutInzio()+" - "+productOffline.getSellOutFine());
		 fieldPeriodoOrdine.setText(productOffline.getOrderInzio()+" - "+productOffline.getOrderFine());
		 fieldScontiValuta.setText(productOnline.getScontoOne()+" + "+productOnline.getScontoTwo()
				 +" + "+productOnline.getScontoThree()+" + "+productOnline.getScontoFour());
		 fieldScontoMerce.setText(productOnline.getScontoMerce());
		
		// TODO Auto-generated method stub
		
		
	}
	
	private void setUpPromoItemRows() {
		// TODO Auto-generated method stub
//		mListDetails.clear();
		
		if(mData!=null)
			TableItems.populateItems(customerCode, mData.getString(INTENT_PROMO_CODE),promoItems);
		else
			TableItems.populateItems( customerCode, productOffline.getPromoCode(), promoItems);
		LayoutInflater inflater = LayoutInflater.from(this);
		
		mItemLayouts.removeAllViews();
		 
		Log.d("Log", "Item size - "+promoItems.size());
		
		for(BaseItem item:promoItems){
			View v = inflater.inflate(R.layout.promo_pro_detail, null);
			TextView fieldItemCode = (TextView) v.findViewById(R.id.field_item_code);
			TextView fieldItemDesc = (TextView) v.findViewById(R.id.field_item_desc);
			
			
			fieldItemCode.setText(item.getCode());
			fieldItemDesc.setText(item.getDescription());
			mItemLayouts.addView(v);
		}
		
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	private class PromoDetailLoadingTask extends AsyncTask<String, Integer, Integer>{
		
		private static final int SERVER_ERROR = -101;
		private static final int ITEM_LOADED = -102;
		private static final int JSON_EXCEPTION = -103;
		private static final int DISK_ERROR = -104;
		private static final int URL_SET_ERROR = -105;
		
		private ElahProgress mSyncProgress;
		private Context 	   mContext;
		
		private PromoDetailLoadingTask(Context context){
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
						ARG_REQUEST+"="+VALUE_REQUEST+"&"+ARG_CUST_CODE+"="+customerCode+"&"
						+ARG_ITEM_CODE+"="+itemCode+"&"+ARG_PROMO_CODE+"="+productOffline.getPromoCode()+"&"
						+ARG_CAMP_CODE+"="+productOffline.getCampCode()+"&"+ARG_LINK_CODE+"="+productOffline.getLinkCodeAsUrl()+"&"
						+ARG_PROMO_STATUS+"="+productOffline.getPromoStatus()
						+"&"+ARG_CURRENT_TIME+"="+Util.getCurrentTimeInMilliSeconds());
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
//			return null;
		}
		
	}

	public void onClick(View v) {
		if(v==mBtnBack){
			finish();
		}
		
	}
}
