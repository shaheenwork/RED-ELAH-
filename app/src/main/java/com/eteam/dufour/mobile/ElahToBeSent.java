package com.eteam.dufour.mobile;

import android.annotation.SuppressLint;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask.Status;
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
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.eteam.dufour.async.SyncTask;
import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.customview.SegmentedRadioGroup;
import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableCustomers;
import com.eteam.dufour.database.tables.TableLogin;
import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.dufour.database.tables.TableSurveyCustomers;
import com.eteam.dufour.database.tables.TableSyncInfo;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

import androidx.fragment.app.FragmentActivity;

public class ElahToBeSent extends FragmentActivity implements
	OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor>, OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String INTENT_KEY = "intent_key";
	private static final int 	CALL_DRAFT_ACTIVITY = 105;
	// ===========================================================
	// Fields
	// ===========================================================
	private SegmentedRadioGroup mBtnSegmented;	
	
	private SQLiteCursorLoader loader;
	private ElahDBHelper dbHelper;
	
	private SimpleCursorAdapter mAdapter;
	
	private ListView mList;
	private View mBtnPointOfSale;	
	private View mBtnLogout;
	private TextView fieldListed, fieldTotalNo;
	private EditText mSearchField;
	private TextView lblTabSelection;
	
	private TextView lblToBeSentCount;
	private TextView lblDraftsCount;
	private TextView lblEmptyView;
	
	private TextView fieldDate;
	private TextView fieldTime;
	
	private String userName;
	private String passWord;
	private String versionId;
	
	private PopupWindow mPopUpEsc;
	private View btnEsc;
	private View btnSchema;
	private View btnSync;
		
	private Bundle mSearchBundle;
		
	private Toast mToast;
	
	private ToBeSentSyncTask mSyncTask;
	
	private SharedPreferences mPrefs;
	private boolean refreshAdapter;
	
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
		setContentView(R.layout.tobe_sent);
		intialize();
		setListeners();
		setEscPopUp();
		setDraftsMargin(getResources().getConfiguration());
		if(mSyncTask==null||(mSyncTask!=null&&mSyncTask.getStatus().equals(Status.FINISHED))){
			Util.checkAndPromptUpdate(this, mPrefs);
		}
		refreshAdapter = true;
		
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(mSyncTask==null||(mSyncTask!=null&&mSyncTask.getStatus().equals(Status.FINISHED))){
			Util.checkAndPromptUpdate(this, mPrefs);
		}
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
		if(mSyncTask!=null){
			if(mSyncTask.getStatus().equals(Status.RUNNING)){
				return;
			}
			else if(mSyncTask.getStatus()==Status.FINISHED){;
			}
		}
		
		if(refreshAdapter)
			setUpAdapters(mBtnSegmented.getCheckedRadioButtonId());
		else
			refreshAdapter = true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	};
	


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mSyncTask!=null)
			mSyncTask.cancel(true);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		setDraftsMargin(newConfig);
	}
	

	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		// TODO Auto-generated method stub
		
		if (mSyncTask != null){
			Log.d("Log", "Retainin AsynTask");
			return (mSyncTask);
		}
		return super.onRetainCustomNonConfigurationInstance();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode==CALL_DRAFT_ACTIVITY&&resultCode==RESULT_OK){
			if(intent!=null&&intent.getBooleanExtra(ConfirmDraftActivity.INTENT_CANCELLED, false)){
				refreshAdapter = false;
			}
		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId!=-1){
			switch (checkedId) {
				case R.id.btn_inviati:
					lblTabSelection.setText(R.string.msg_inviati_selected);
					lblEmptyView.setText(R.string.msg_empty_inviati);
					fieldListed.setText(" ");
					fieldTotalNo.setText(" ");
					break;
				case R.id.btn_da_inviare:
					lblTabSelection.setText(R.string.msg_da_inivare_selected);
					lblEmptyView.setText(R.string.msg_empty_da_inviare);
					fieldListed.setText(" ");
					fieldTotalNo.setText(" ");
					break;
				case R.id.btn_bozze:
					lblTabSelection.setText(R.string.msg_bozze_selected);
					lblEmptyView.setText(R.string.msg_empty_bozze);
					fieldListed.setText(" ");
					fieldTotalNo.setText(" ");
					break;
		
				default:
					break;
			}
			setUpAdapters(checkedId);
		}
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.d("Loading ", "ID - "+id);
		switch (id) {
	    	
			case R.id.btn_inviati:
				if(bundle==null){
					loader= new SQLiteCursorLoader(this, dbHelper, "SELECT " 
				    		+"b."+TableSurveyCustomers.COLUMN_SYNC_DATE+", a."+TableCustomers.COLUMN_NM
				    		+", a."+TableCustomers.COLUMN_NO+", a."+TableCustomers.COLUMN_AD+
				    		", a."+TableCustomers.COLUMN_CT+", a."+TableCustomers.COLUMN_ID
				    		+" FROM "+TableCustomers.TABLE_NAME+" a INNER JOIN "+TableSurveyCustomers.TABLE_NAME+" b"
				    		+" WHERE a."+TableCustomers.COLUMN_NO+"=b."+TableSurveyCustomers.COLUMN_CUSTOMER_CODE
				    		+" ORDER BY substr("+TableSurveyCustomers.COLUMN_SYNC_DATE+",7,4)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE
				    		+",4,2)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE+",1,2)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE
				    		+",14,2)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE+",17,2) DESC", null);
					Log.d("Log", "Query -"+"SELECT " 
				    		+"b."+TableSurveyCustomers.COLUMN_SYNC_DATE+", a."+TableCustomers.COLUMN_NM
				    		+", a."+TableCustomers.COLUMN_NO+", a."+TableCustomers.COLUMN_AD+
				    		", a."+TableCustomers.COLUMN_CT+", a."+TableCustomers.COLUMN_ID
				    		+" FROM "+TableCustomers.TABLE_NAME+" a INNER JOIN "+TableSurveyCustomers.TABLE_NAME+" b"
				    		+" WHERE a."+TableCustomers.COLUMN_NO+"=b."+TableSurveyCustomers.COLUMN_CUSTOMER_CODE
				    		+" ORDER BY strftime('%s',+b."+TableSurveyCustomers.COLUMN_SYNC_DATE+") DESC");
				}
				else{
					loader= new SQLiteCursorLoader(this, dbHelper, "SELECT " 
				    		+"b."+TableSurveyCustomers.COLUMN_SYNC_DATE+", a."+TableCustomers.COLUMN_NM
				    		+", a."+TableCustomers.COLUMN_NO+", a."+TableCustomers.COLUMN_AD+
				    		", a."+TableCustomers.COLUMN_CT+", a."+TableCustomers.COLUMN_ID
				    		+" FROM "+TableCustomers.TABLE_NAME+" a INNER JOIN "+TableSurveyCustomers.TABLE_NAME+" b"
				    		+" WHERE (a."+TableCustomers.COLUMN_NO+"=b."+TableSurveyCustomers.COLUMN_CUSTOMER_CODE
				    		+") AND (a."+TableCustomers.COLUMN_NM+" LIKE ? OR a."+TableCustomers.COLUMN_NO+" LIKE ?"
				    		+" OR a."+TableCustomers.COLUMN_AD+" LIKE ? OR a."+TableCustomers.COLUMN_CT+" LIKE ?)"
				    		+" ORDER BY substr("+TableSurveyCustomers.COLUMN_SYNC_DATE+",7,4)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE
				    		+",4,2)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE+",1,2)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE
				    		+",14,2)||substr("+TableSurveyCustomers.COLUMN_SYNC_DATE+",17,2) DESC", 
				    		new String[]{"%"+bundle.getString(INTENT_KEY)+"%","%"+bundle.getString(INTENT_KEY)+"%",
							"%"+bundle.getString(INTENT_KEY)+"%","%"+bundle.getString(INTENT_KEY)+"%"});
				}
				break;
			case R.id.btn_da_inviare:
				if(bundle==null){
					loader = new SQLiteCursorLoader(this, dbHelper,"SELECT " 
				    		+"b."+TableSurvey.COLUMN_SURVEY_SENT_DATE+", a."+TableCustomers.COLUMN_NM
				    		+", a."+TableCustomers.COLUMN_NO+", a."+TableCustomers.COLUMN_AD+
				    		", a."+TableCustomers.COLUMN_CT+", a."+TableCustomers.COLUMN_ID
				    		+" FROM "+TableCustomers.TABLE_NAME+" a INNER JOIN "+TableSurvey.TABLE_NAME+" b"
				    		+" WHERE a."+TableCustomers.COLUMN_NO+"=b."+TableSurvey.COLUMN_CUSTOMER_CODE
				    		+" AND b."+TableSurvey.COLUMN_FLAG+" IN ( "+TableSurvey.FLAG_TO_BE_SENT+","+TableSurvey.FLAG_TO_BE_SENT_OLD+") "
				    		+" ORDER BY substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",7,4)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",4,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",1,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",14,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",17,2) DESC", null);
				}
				else{
					loader= new SQLiteCursorLoader(this, dbHelper, "SELECT " 
				    		+"b."+TableSurvey.COLUMN_SURVEY_SENT_DATE+", a."+TableCustomers.COLUMN_NM
				    		+", a."+TableCustomers.COLUMN_NO+", a."+TableCustomers.COLUMN_AD+
				    		", a."+TableCustomers.COLUMN_CT+", a."+TableCustomers.COLUMN_ID
				    		+" FROM "+TableCustomers.TABLE_NAME+" a INNER JOIN "+TableSurvey.TABLE_NAME+" b"
				    		+" WHERE (a."+TableCustomers.COLUMN_NO+"=b."+TableSurvey.COLUMN_CUSTOMER_CODE
				    		+" AND b."+TableSurvey.COLUMN_FLAG+" IN ( "+TableSurvey.FLAG_TO_BE_SENT+","
						    +TableSurvey.FLAG_TO_BE_SENT_OLD+") "+") AND (a."+TableCustomers.COLUMN_NM
						    +" LIKE ? OR a."+TableCustomers.COLUMN_NO+" LIKE ?" 
						    +" OR a."+TableCustomers.COLUMN_AD+" LIKE ? OR a."+TableCustomers.COLUMN_CT+" LIKE ?)"
						    +" ORDER BY substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",7,4)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",4,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",1,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",14,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",17,2) DESC", 
				    		new String[]{"%"+bundle.getString(INTENT_KEY)+"%","%"+bundle.getString(INTENT_KEY)+"%",
				    				"%"+bundle.getString(INTENT_KEY)+"%","%"+bundle.getString(INTENT_KEY)+"%"});
				}
				break;
			case R.id.btn_bozze:
				if(bundle==null){
					loader = new SQLiteCursorLoader(this, dbHelper,"SELECT " 
				    		+"b."+TableSurvey.COLUMN_SURVEY_SENT_DATE+", b."+TableSurvey.COLUMN_ID
				    		+", a."+TableCustomers.COLUMN_NM+", a."+TableCustomers.COLUMN_NO
				    		+", a."+TableCustomers.COLUMN_AD+", a."+TableCustomers.COLUMN_CT
				    		+", a."+TableCustomers.COLUMN_ID+" FROM "+TableCustomers.TABLE_NAME
				    		+" a INNER JOIN "+TableSurvey.TABLE_NAME+" b"+" WHERE a."+TableCustomers.COLUMN_NO
				    		+"=b."+TableSurvey.COLUMN_CUSTOMER_CODE
				    		+" AND b."+TableSurvey.COLUMN_FLAG+"="+TableSurvey.FLAG_DRAFTS
				    		+" ORDER BY substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",7,4)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",4,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",1,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",14,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",17,2) DESC", null);
				}
				else{
					loader= new SQLiteCursorLoader(this, dbHelper, "SELECT " 
				    		+"b."+TableSurvey.COLUMN_SURVEY_SENT_DATE+", b."+TableSurvey.COLUMN_ID
				    		+", a."+TableCustomers.COLUMN_NM+", a."+TableCustomers.COLUMN_NO
				    		+", a."+TableCustomers.COLUMN_AD+", a."+TableCustomers.COLUMN_CT
				    		+", a."+TableCustomers.COLUMN_ID
				    		+" FROM "+TableCustomers.TABLE_NAME+" a INNER JOIN "+TableSurvey.TABLE_NAME+" b"
				    		+" WHERE (a."+TableCustomers.COLUMN_NO+"=b."+TableSurvey.COLUMN_CUSTOMER_CODE
				    		+" AND "+TableSurvey.COLUMN_FLAG+"="+TableSurvey.FLAG_DRAFTS
				    		+") AND (a."+TableCustomers.COLUMN_NM+" LIKE ? OR a."+TableCustomers.COLUMN_NO+" LIKE ?"
				    		+" OR a."+TableCustomers.COLUMN_AD+" LIKE ? OR a."+TableCustomers.COLUMN_CT+" LIKE ?)"
				    		+" ORDER BY substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",7,4)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",4,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",1,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE
				    		+",14,2)||substr("+TableSurvey.COLUMN_SURVEY_SENT_DATE+",17,2) DESC", 
				    		new String[]{"%"+bundle.getString(INTENT_KEY)+"%","%"+bundle.getString(INTENT_KEY)+"%"
							,"%"+bundle.getString(INTENT_KEY)+"%","%"+bundle.getString(INTENT_KEY)+"%"});
				}
				
				break;
			
			default:
				break;
		}
		

		return(loader);
	}

	public void onLoadFinished(Loader<Cursor> manager, Cursor cursor) {
		// TODO Auto-generated method stub
		if(mBtnSegmented.getCheckedRadioButtonId()==manager.getId()){
			mAdapter.changeCursor(cursor);
			fieldListed.setText(String.valueOf(mAdapter.getCount()));
			String count = "";
			switch (manager.getId()) {
				case R.id.btn_inviati:
					count = String.valueOf(TableSurveyCustomers.getSurveySendCount());
					break;
					
				case R.id.btn_da_inviare:
					count = String.valueOf(TableSurvey.getToBeSentCount());
					break;
				case R.id.btn_bozze:
					count = String.valueOf(TableSurvey.getDraftsCount());
					break;
			default:
				break;
			}
			
			fieldTotalNo.setText(count);
			
			if(lblDraftsCount!=null&&dbHelper!=null)
				setUpCountViews(lblDraftsCount, TableSurvey.getDraftsCount());
			if(lblToBeSentCount!=null&&dbHelper!=null)
				setUpCountViews(lblToBeSentCount, TableSurvey.getToBeSentCount());
		}
		
	}

	public void onLoaderReset(Loader<Cursor> cursor) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(null);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mBtnPointOfSale){
			Intent intent = new Intent(this,ElahPointOfSale.class);
			startActivity(intent);
		}
		else if(v==btnEsc){
			mPopUpEsc.dismiss();
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
		else if(v==btnSync){
			if(Util.haveNetworkConnection(this)){
				if(mSyncTask==null||mSyncTask.getStatus()==Status.FINISHED){
					ToBeSentSyncTask mSyncTask = new ToBeSentSyncTask(dbHelper,mPrefs,userName,passWord,versionId);
					this.mSyncTask = (ToBeSentSyncTask) mSyncTask.execute();
				}
			}
			else{
				Util.showToast(mToast, R.string.msg_intenet_not_connected);
			}
		}
	}
	
	
	// ===========================================================
	// Methods
	// ===========================================================
  
   
	private void setDraftsMargin(Configuration newConfig){
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			int margin = getResources().getDimensionPixelSize(R.dimen.drafts_count_margin_landscape);
			if(lblDraftsCount!=null){
				FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lblDraftsCount.getLayoutParams();
				params.rightMargin = margin;
				lblDraftsCount.setLayoutParams(params);
			}
		}
		else{
			int margin = getResources().getDimensionPixelSize(R.dimen.drafts_count_margin_port);
			if(lblDraftsCount!=null){
				FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lblDraftsCount.getLayoutParams();
				params.rightMargin = margin;
				lblDraftsCount.setLayoutParams(params);
			}
		}
	}
	

	@SuppressLint("ShowToast")
	private void intialize() {
		// TODO Auto-generated method stub
		mBtnSegmented = (SegmentedRadioGroup) findViewById(R.id.btn_segment);
		dbHelper = ElahDBHelper.getInstance(this);
		
		
		
		fieldListed = (TextView) findViewById(R.id.listed_no);
		fieldTotalNo = (TextView) findViewById(R.id.total_no);
		mSearchField = (EditText) findViewById(R.id.search_item);
		mSearchBundle = new Bundle();
		mBtnPointOfSale = findViewById(R.id.btn_point_of_sale);
		lblTabSelection = (TextView) findViewById(R.id.field_status);
		mBtnLogout = findViewById(R.id.btn_logout);
		mPrefs = getSharedPreferences(Consts.PREF_ELAH, MODE_PRIVATE);
		userName = mPrefs.getString(LoginActivity.PREF_USER_NAME, "");
		passWord = TableLogin.getPassword(userName);
		versionId = Util.getApplicationVersion(this);
		
		fieldDate = (TextView) findViewById(R.id.field_date);
		fieldTime =  (TextView) findViewById(R.id.field_time);
		
		lblToBeSentCount = (TextView) findViewById(R.id.lbl_to_be_sent_count);
		lblDraftsCount = (TextView) findViewById(R.id.lbl_drafts_count);
		lblEmptyView = (TextView) findViewById(R.id.empty_view);
		
		mList = (ListView) findViewById(R.id.list);
		mList.setEmptyView(lblEmptyView);
		
		btnSync = findViewById(R.id.btn_sync);
		

		
		mToast = Toast.makeText(this, "Sample", Toast.LENGTH_SHORT);
		//Util.setUpToast(mToast);
		
		setUpSyncTime();
	}
	
	@SuppressWarnings("deprecation")
	private void setEscPopUp() {
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

    private void setUpSyncTime() {
		
		String[] array = TableSyncInfo.getSyncDate().split("/");
		if(array.length==2){
			fieldDate.setText(array[0]);
			fieldTime.setText(array[1]);
		}
		else{
			Log.e("Log", "Check syncdate is set as date/time in sync table");
		}
	}
	


	private void startSyncActivity() {
		// TODO Auto-generated method stub
    	if(ElahToBeSent.this!=null){
    		Intent intent = new Intent(ElahToBeSent.this,ElahSyncPage.class);
    		startActivity(intent);
    	}
		
	}
	
	private void setListeners() {
		// TODO Auto-generated method stub
		mSearchField.addTextChangedListener(mSearchWatcher);
		mBtnSegmented.setOnCheckedChangeListener(this);
		mBtnPointOfSale.setOnClickListener(this);
		mBtnLogout.setOnClickListener(this);
		btnSync.setOnClickListener(this);
	}
	
	public void setUpAdapters(int checkId) {
		createAdapters(checkId);
		mList.setAdapter(mAdapter);
		if(checkId==R.id.btn_bozze){
			mList.setOnItemClickListener(bozzeItemClickListener);
			mList.setSelector(R.drawable.bk_list_item);
		}
		else{
			mList.setOnItemClickListener(null);
			mList.setSelector(R.drawable.bk_list_item_white);
		}
		
		getLoaderManager().restartLoader(checkId, null, this);
		mSearchField.setText("");
		mSearchField.clearFocus();
		setUpCountViews(lblDraftsCount, TableSurvey.getDraftsCount());
		setUpCountViews(lblToBeSentCount, TableSurvey.getToBeSentCount());
	}
	
	private void setUpCountViews(TextView view, int count) {
		// TODO Auto-generated method stub\
		if(count==0){
			view.setVisibility(View.GONE);
		}
		else{
			view.setVisibility(View.VISIBLE);
			view.setText(String.valueOf(count));
		}
	}

	@SuppressWarnings("deprecation")
	private void createAdapters(int checkId) {
		// TODO Auto-generated method stub
		switch (checkId) {
			case R.id.btn_inviati:
				mAdapter = new SimpleCursorAdapter(this,R.layout.row_to_be_sent, 
		                null, new String[] {TableSurveyCustomers.COLUMN_SYNC_DATE, TableCustomers.COLUMN_NM, 
						TableCustomers.COLUMN_NO, TableCustomers.COLUMN_AD, TableCustomers.COLUMN_CT},
		                new int[] { R.id.tobesent_date, R.id.tobesent_name, R.id.tobesent_no, R.id.tobesent_address, R.id.tobesent_city});
				break;
			case R.id.btn_da_inviare:
				mAdapter = new SimpleCursorAdapter(this,R.layout.row_to_be_sent, 
		                null, new String[] {TableSurvey.COLUMN_SURVEY_SENT_DATE, TableCustomers.COLUMN_NM, 
						TableCustomers.COLUMN_NO, TableCustomers.COLUMN_AD, TableCustomers.COLUMN_CT},
		                new int[] { R.id.tobesent_date, R.id.tobesent_name, R.id.tobesent_no, R.id.tobesent_address, R.id.tobesent_city });
				break;
			case R.id.btn_bozze:
				mAdapter = new SimpleCursorAdapter(this,R.layout.row_to_be_sent, 
		                null, new String[] {TableSurvey.COLUMN_SURVEY_SENT_DATE, TableCustomers.COLUMN_NM, 
						TableCustomers.COLUMN_NO, TableCustomers.COLUMN_AD, TableCustomers.COLUMN_CT},
		                new int[] { R.id.tobesent_date, R.id.tobesent_name, R.id.tobesent_no, R.id.tobesent_address, R.id.tobesent_city});
				break;
	
			default:
				break;
				
						
		}
		
		
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	private class ToBeSentSyncTask extends SyncTask{

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

		public ToBeSentSyncTask(ElahDBHelper dbHelper, SharedPreferences prefs,
				String userName, String password, String versionId) {
			super(dbHelper, prefs, userName, password, versionId);
			// TODO Auto-generated constructor stub
			mSyncProgress = new ElahProgress(ElahToBeSent.this);
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
			if(mSyncProgress!=null){
				 try{
	    			 mSyncProgress.show(R.string.senting);
				 }
				 catch(Exception e){
					 e.printStackTrace();
	    			 cancel(true);
				 }
			}
		}
		
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(mSyncProgress!=null){
	        	try{
	        		mSyncProgress.dismiss();
	        	}
	        	catch(Exception e){
	        		e.printStackTrace();
	        	}
	        }
	        switch (result) {
				case SYNC_FROM_SERVER_SUCCESSFUL:
					TableLogin.setSyncStatus(userName,TableLogin.SYNC_COMPLETED);
		        	startSyncActivity();
		        	Util.showToast(mToast, R.string.sync_success);
					break;
				case SYNC_FROM_SERVER_FAILED:
					TableLogin.setSyncStatus(userName,TableLogin.SYNC_INCOMPLETE);	        	
		        	startSyncActivity();
					Util.showToast(mToast, R.string.sync_failed);
					break;
				case SURVEY_SEND_FAILURE:
					Util.showToast(mToast, R.string.sync_to_server_failed);
					if(ElahToBeSent.this!=null)
						getLoaderManager().restartLoader(mBtnSegmented.getCheckedRadioButtonId(), null, ElahToBeSent.this);
					break;
				case NO_UPDATE_AVAILABLE:
					Util.showToast(mToast, R.string.msg_no_update_available);
					if(ElahToBeSent.this!=null)
						getLoaderManager().restartLoader(mBtnSegmented.getCheckedRadioButtonId(), null, ElahToBeSent.this);
					break;
				case LOGIN_FAILED:
					if(ElahToBeSent.this!=null)
						Util.logout(ElahToBeSent.this,R.string.msg_pw_changed);
					break;
				case PERMISSION_NOT_ALLOWED:
					if(ElahToBeSent.this!=null)
						Util.logout(ElahToBeSent.this,R.string.msg_permission_changed);
					break;
				case LOGIN_SERVER_ERROR:
					Util.showToast(mToast, R.string.msg_login_server_not_responding);
					if(ElahToBeSent.this!=null)
						getLoaderManager().restartLoader(mBtnSegmented.getCheckedRadioButtonId(), null, ElahToBeSent.this);
					break;
				default:
					break;
			}
		};
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
	    	if(mSyncProgress!=null){
	    		mSyncProgress.dismiss();
	    		mSyncProgress = null;
	    		
			}
	    	
	    	TableLogin.setSyncStatus(userName,TableLogin.SYNC_INCOMPLETE);
	    	
	    	Util.showToast(mToast, R.string.sync_failed);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if(mSyncProgress!=null){
	    		 if(values[0]==SURVEY_SEND_SUCCESS){
	    			 mSyncProgress.setMessage(R.string.senting_successful);
	    		 }
	    		 else if(values[0]==SYNC_STARTED){
						mSyncProgress.setMessage(R.string.syncing);
						mSyncProgress.setProgress(0);
		    		 }
	    		 else {
	    			 mSyncProgress.setMessage(R.string.syncing);
	    			 mSyncProgress.setProgress(values[0]);
	    		 }
		    }	
		}
		
		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
	
	
	// ===========================================================
	// Inner and Anonymous Interfaces
	// ===========================================================


	
	private TextWatcher mSearchWatcher = new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(s.toString()!=null){
				mSearchBundle.putString(INTENT_KEY, s.toString());
				int id =mBtnSegmented.getCheckedRadioButtonId();
				if(id!=-1)
					getLoaderManager().restartLoader(id
						,mSearchBundle,ElahToBeSent.this);
			}
		}
	};
	
	private OnItemClickListener bozzeItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> adpView, View view, int position,
				long id) {
			Cursor c = (Cursor) mList.getItemAtPosition(position);
			if(c!=null){
				int surveyId = c.getInt(c.getColumnIndex(TableSurvey.COLUMN_ID));
				String customerName = c.getString(c.getColumnIndex(TableCustomers.COLUMN_NM));
				String customerNo = c.getString(c.getColumnIndex(TableCustomers.COLUMN_NO));
				Intent intent = new Intent(ElahToBeSent.this,ConfirmDraftActivity.class);
				intent.putExtra(ConfirmDraftActivity.INTENT_SURVEY_ID, surveyId);
				intent.putExtra(ConfirmDraftActivity.INTENT_CUSTOMER_NUMBER, customerNo);
				intent.putExtra(ConfirmDraftActivity.INTENT_CUSTOMER_NAME, customerName);
				startActivityForResult(intent,CALL_DRAFT_ACTIVITY);
			}
		}
	};
	



}
