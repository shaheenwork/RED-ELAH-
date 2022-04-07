package com.eteam.dufour.mobile;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableCustomers;
import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.dufour.newadpaters.AdapterPointOfSale;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

import androidx.fragment.app.FragmentActivity;

public class ElahPointOfSale extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener{


	// ===========================================================
	// Constants
	// ===========================================================
	
	private final static String SEARCH_KEY = "com.eteam.dufour.release.backup.ElahPointOfSale.SEARCH_KEY";
	private static final int CALL_SURVEY_COPY_ACTIVITY = 101;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private SQLiteCursorLoader loader;
	private ElahDBHelper dbHelper;
	
	private TextView fieldListedNo;
	private TextView fieldTotalNo;
	
	private View mBtnAnnula;
	private View mBtnLogout;

	private PopupWindow mPopUpEsc;
	private View btnEsc;
	private View btnSchema;
	
	private ListView mList;
	private AdapterPointOfSale mAdapter;
	
	private EditText mFieldSearch;
	
	private Bundle mSearchBundle;
	
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
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.point_of_sale);
		Util.checkAndPromptUpdate(this, getSharedPreferences(Consts.PREF_ELAH, 
				MODE_PRIVATE));
		initialize();
		setListeners();
		getLoaderManager().initLoader(0, null, this);
		setEscPopUp();
		TableSurvey.deleteCorruptedData();
		
	}
	


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		dbHelper.close();
		loader = null;
	//	mAdapter = null;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		// TODO Auto-generated method stub
		//"NM", "NO", "AD","CT", "_id"
		if(bundle==null){
			loader = new SQLiteCursorLoader(this,dbHelper, "SELECT "+TableCustomers.COLUMN_NM
					+","+TableCustomers.COLUMN_NO+","+TableCustomers.COLUMN_AD
					+","+TableCustomers.COLUMN_CT+","+TableCustomers.COLUMN_ID
					+" FROM "+TableCustomers.TABLE_NAME
					+" ORDER BY "+TableCustomers.COLUMN_NM+" COLLATE NOCASE,"
					+TableCustomers.COLUMN_NO+","+TableCustomers.COLUMN_CT+" COLLATE NOCASE,"
					+TableCustomers.COLUMN_AD+" COLLATE NOCASE",null);
		}
		else{
			loader = new SQLiteCursorLoader(this, dbHelper, "SELECT "+TableCustomers.COLUMN_NM
					+","+TableCustomers.COLUMN_NO+","+TableCustomers.COLUMN_AD
					+","+TableCustomers.COLUMN_CT+","+TableCustomers.COLUMN_ID
					+" FROM "+TableCustomers.TABLE_NAME+" WHERE "+TableCustomers.COLUMN_NM+" LIKE ?"
					+" OR "	+TableCustomers.COLUMN_NO+ " LIKE ?"
					+" OR "+TableCustomers.COLUMN_AD+" LIKE ? OR "+TableCustomers.COLUMN_CT+" LIKE ?"
					+" ORDER BY "+TableCustomers.COLUMN_NM+" COLLATE NOCASE,"
					+TableCustomers.COLUMN_NO+","+TableCustomers.COLUMN_CT+" COLLATE NOCASE,"
					+TableCustomers.COLUMN_AD+" COLLATE NOCASE", 
					new String[]{"%"+bundle.getString(SEARCH_KEY)+"%","%"+bundle.getString(SEARCH_KEY)+"%"
					,"%"+bundle.getString(SEARCH_KEY)+"%","%"+bundle.getString(SEARCH_KEY)+"%"});
		}
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(cursor);
		fieldListedNo.setText(String.valueOf(mAdapter.getCount()));
		fieldTotalNo.setText(String.valueOf(TableCustomers.getCount()));
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(null);
	}
	
	public void onItemClick(AdapterView<?> adpView, View view, int position, long id) {
		// TODO Auto-generated method stub
		Cursor c = (Cursor) mList.getItemAtPosition(position);
		if(c!=null){
//			Intent intent = new Intent(this,SurveyCreateActivity.class);
//			intent.putExtra(SurveyCreateActivity.INTENT_CUSTOMER_CODE, 
//					c.getString(c.getColumnIndex(TableCustomers.COLUMN_NO)));
//			intent.putExtra(SurveyCreateActivity.INTENT_CUSTOMER_NAME, 
//					c.getString(c.getColumnIndex(TableCustomers.COLUMN_NM)));
//			startActivityForResult(intent,CALL_SURVEY_COPY_ACTIVITY);
			SurveyCreateActivity.startActivityForResult(this
					, c.getString(c.getColumnIndex(TableCustomers.COLUMN_NM))
					, c.getString(c.getColumnIndex(TableCustomers.COLUMN_NO))
					, CALL_SURVEY_COPY_ACTIVITY);
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==CALL_SURVEY_COPY_ACTIVITY&&resultCode==RESULT_OK){
			finish();
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mBtnAnnula){
			finish();
		}
		else if(v==mBtnLogout){
			mBtnLogout.post(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					mPopUpEsc.showAsDropDown(mBtnLogout);
				}
			});
		}		
		else if(v==btnSchema){
			mPopUpEsc.dismiss();
			finish();
			Intent intent = new Intent(this,ElahSyncPage.class);
			startActivity(intent);
			
		}
		else if(v==btnEsc){
			mPopUpEsc.dismiss();
			Util.logout(this);
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	@SuppressWarnings("deprecation")
	private void setEscPopUp() {
		// TODO Auto-generated method stub
//		int deviceWidth = getWindowManager().getDefaultDisplay().getWidth();
		
//		int width = (int) (deviceWidth*PERCENTAGE_ESC_POPUP);
//		int height = (int) (width/POP_UP_ESC_RATIO);
		
		mPopUpEsc = new PopupWindow(this);
		mPopUpEsc.setBackgroundDrawable(new BitmapDrawable());
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.escpopup, null);
		
		btnEsc = v.findViewById(R.id.btn_esci);
		btnSchema = v.findViewById(R.id.btn_schema);
		
		mPopUpEsc.setContentView(v);
		mPopUpEsc.setHeight(ListPopupWindow.WRAP_CONTENT);
		mPopUpEsc.setWidth(ListPopupWindow.WRAP_CONTENT);
		mPopUpEsc.setOutsideTouchable(true);
		
		btnEsc.setOnClickListener(this);
		btnSchema.setOnClickListener(this);
		
		
		
	}
	
	private void initialize() {
		// TODO Auto-generated method stub
		dbHelper = ElahDBHelper.getInstance(this);
		
		mList = (ListView) findViewById(R.id.pointofsale_list);
		mList.setEmptyView(findViewById(R.id.empty_view));
		
		mAdapter = new AdapterPointOfSale(this, R.layout.row_customer, null, 
				 new String[]{TableCustomers.COLUMN_NM, TableCustomers.COLUMN_NO, 
				 TableCustomers.COLUMN_AD ,TableCustomers.COLUMN_CT, TableCustomers.COLUMN_ID}, 
				 new int[] { R.id.lbl_name, R.id.lbl_no, R.id.lbl_address,
				 R.id.lbl_city});
		mAdapter.setInfoClickListener(infoClickListener);
		mFieldSearch = (EditText) findViewById(R.id.search_customer);
		mSearchBundle = new Bundle();
		mBtnAnnula = findViewById(R.id.annulla);
		mBtnLogout = findViewById(R.id.btn_logout);
		
        mList.setAdapter(mAdapter);
		fieldListedNo = (TextView) findViewById(R.id.listed_no);
        fieldTotalNo = (TextView) findViewById(R.id.total_no);
        
	}
	
	private void setListeners() {
		// TODO Auto-generated method stub
		mList.setOnItemClickListener(this);
		mBtnAnnula.setOnClickListener(this);
		mBtnLogout.setOnClickListener(this);
		mFieldSearch.addTextChangedListener(searchWatcher);
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	// ===========================================================
	// Inner and Anonymous Interfaces
	// ===========================================================
	
	private TextWatcher searchWatcher = new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			mSearchBundle.putString(SEARCH_KEY, s.toString());
			getLoaderManager().restartLoader(0, mSearchBundle, ElahPointOfSale.this);
		}
	};

	private OnClickListener infoClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			int position = mList.getPositionForView(v);
			Log.d("Log", "Clicked at -"+position);
			Cursor c = (Cursor) mList.getItemAtPosition(position);
			if(c!=null){
				String customerCode = c.getString(c.getColumnIndex(TableCustomers.COLUMN_NO));
				ActivityCustomerDetail.startActivity(ElahPointOfSale.this, customerCode);
			}
			
		}
	};
	


	

}
