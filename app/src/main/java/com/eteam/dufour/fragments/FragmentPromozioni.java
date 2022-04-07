package com.eteam.dufour.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.eteam.dufour.customview.SegmentedRadioGroup;
import com.eteam.dufour.database.tables.TableItems.BaseItem;
import com.eteam.dufour.database.tables.TablePromotions;
import com.eteam.dufour.database.tables.TablePromotions.Promotion;
import com.eteam.dufour.database.tables.TablePromotions.PromotionType;
import com.eteam.dufour.database.tables.TablePromotions.SearchCriteria;
import com.eteam.dufour.mobile.DetailProdottiActivity;
import com.eteam.dufour.mobile.DetailPromozioniActivity;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.newadpaters.AdapterPromotions;
import com.eteam.dufour.newadpaters.AdapterPromotions.OnPromotionSurveyActionListener;
import com.eteam.dufour.viewmodel.ModelSurveyPromotionItems;
import com.eteam.dufour.viewmodel.ModelSurveyPromotions;
import com.eteam.utils.Util;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

public class FragmentPromozioni extends Fragment implements
		LoaderManager.LoaderCallbacks<ArrayList<ModelSurveyPromotions>>,OnCheckedChangeListener{
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final String EXTRA_SURVEY_ID = "com.eteam.dufour.fragments.FragmentAssortimento.EXTRA_SURVEY_ID";
	private static final String EXTRA_CUSTOMER_NUMBER = "com.eteam.dufour.fragments.FragmentAssortimento.EXTRA_CUSTOMER_NUMBER";
	private static final String EXTRA_SALES_PERSON = "com.eteam.dufour.fragments.FragmentAssortimento.EXTRA_SALES_PERSON";
	
	
	private static final String VALUE_SEARCH = "search";
	// ===========================================================
	// Fields
	// ===========================================================

	private TextView fieldListedNo;
	private TextView fieldTotalNo;
	private SegmentedRadioGroup btnSegment;
	private ExpandableListView mList;
	private EditText fieldSearch;
	private TextView mLblTabStat;
	
	private AdapterPromotions mAdapter;
		
	private String mCustomerCode;
	private String mSalesPerson;
	private long   survey_id;
		
	private Bundle mBundle;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public static FragmentPromozioni getInstance(long surveyId,String customerNumber,String salesPersonCode){
		FragmentPromozioni fragment = new FragmentPromozioni();
		
		Bundle args = new Bundle();
		
		args.putLong(EXTRA_SURVEY_ID, surveyId);
		args.putString(EXTRA_CUSTOMER_NUMBER, customerNumber);
		args.putString(EXTRA_SALES_PERSON, salesPersonCode);
		
		fragment.setArguments(args);
		return fragment;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mBundle = new Bundle();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			survey_id = getArguments().getLong(EXTRA_SURVEY_ID);
			mCustomerCode = getArguments().getString(EXTRA_CUSTOMER_NUMBER);
			mSalesPerson = getArguments().getString(EXTRA_SALES_PERSON);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.promotions, container, false);
		initialize(view);
		setListeners();		
		initLoader(btnSegment.getCheckedRadioButtonId());
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
	}
	
	public Loader<ArrayList<ModelSurveyPromotions>> onCreateLoader(int id, Bundle bundle) {
		
		
		PromotionType type = null;
		switch (id) {
			case R.id.btn_attive:
				type = PromotionType.ACTIVE;
				break;
			case R.id.btn_non_piu_attive:
				type = PromotionType.NON_ACTIVE;
				break;
			case R.id.btn_prossime:
				type = PromotionType.FUTURE;
				break;
	
			default:
				break;
		}
				
		return new PromotionsLoader(getActivity(), survey_id, mCustomerCode, mSalesPerson, type,convertToCriteria(bundle));
		
	}
	
	private static final String TAG = FragmentPromozioni.class.getName();
	
	public void onLoadFinished(Loader<ArrayList<ModelSurveyPromotions>> loader, ArrayList<ModelSurveyPromotions> surveys) {
		Log.d(TAG, "Promotions available before "+mAdapter.getGroupCount());
		if(surveys!=null){
			mAdapter.changeSource(surveys);
			fieldListedNo.setText(String.valueOf(surveys.size()));
			switch (loader.getId()) {
				case R.id.btn_attive:
					fieldTotalNo.setText(TablePromotions.getPromoAttiveCount(survey_id, mCustomerCode));
					break;
				
				case R.id.btn_non_piu_attive:
					fieldTotalNo.setText(TablePromotions.getPromoNonAttiveCount( survey_id, mCustomerCode));
					break;
				
				case R.id.btn_prossime:
					fieldTotalNo.setText(TablePromotions.getPromoProssimeCount(survey_id, mCustomerCode));
					break;
	
				default:
					break;
			}
			Log.d(TAG, "Promotions available "+mAdapter.getGroupCount());
			mAdapter.notifyDataSetChanged();
		}
	}

	public void onLoaderReset(Loader<ArrayList<ModelSurveyPromotions>> loader) {
		// TODO Auto-generated method stub
		mAdapter.clear();
	}
	
	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		mList.col
		switch (checkedId) {
			case R.id.btn_attive:
				mLblTabStat.setText(R.string.msg_attive_selected);
//				mList.enableItemClick();
				break;
			case R.id.btn_non_piu_attive:
//				mList.disableItemClick();
				mLblTabStat.setText(R.string.msg_non_piu_attive_selected);
				
				break;
			case R.id.btn_prossime:
//				mList.disableItemClick();
				mLblTabStat.setText(R.string.msg_prossime_selected);
				
				break;
			default:
				break;
		}
		initLoader(checkedId);
	}
		
	public void onBtnClicked(BaseItem item) {
		if(Util.haveNetworkConnection(getActivity())){
			if(item!=null){
				DetailProdottiActivity.startActivity(getActivity(), mCustomerCode, item);
			}
		}
		else{
			Util.startAlertMsgActivity(getActivity(),R.string.msg_no_net_connection
					,R.string.chiudi);
		}
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	private void initialize(View view) {
		// TODO Auto-generated method stub
		fieldListedNo = (TextView) view.findViewById (R.id.listed_no);
		fieldTotalNo = (TextView) view.findViewById (R.id.total_no);
		
		btnSegment = (SegmentedRadioGroup) view.findViewById(R.id.segment_text);
        
		
        
        mList = (ExpandableListView) view.findViewById (R.id.list);
        mList.setEmptyView(view.findViewById(R.id.empty_view));
        
        mAdapter = new AdapterPromotions(getActivity());
        
        mAdapter.setListener(new OnPromotionSurveyActionListener() {
			
			@Override
			public void onMinimiseClicked() {
				collapseAll();
			}
			
			@Override
			public void onInfoButtonClicked(ModelSurveyPromotions model) {
				if(Util.haveNetworkConnection(getActivity())){
					Promotion promotion = model.getPromotion();
					if(promotion!=null){
//						String promoCode = c.getString(c.getColumnIndex(TablePromotions.COLUMN_PROMO_CODE));
			//			String itemDesc = c.getString(c.getColumnIndex(TableItems.COLUMN_DESCRIPTION));
						startPromoDetailActivity(promotion.getPromoCode(),mCustomerCode);
					}
					Log.d("Log", "Promo code is - "+promotion.getPromoCode());
				}
				else{
					Util.startAlertMsgActivity(getActivity(),R.string.msg_no_net_connection
							,R.string.chiudi);
				}
			}

			@Override
			public void onClickView(int position) {
				if(mList.isGroupExpanded(position))
					mList.collapseGroup(position);
				else
					mList.expandGroup(position);
			}

			@Override
			public void OnInfoButtonClicked(ModelSurveyPromotionItems itemModel) {
				if(Util.haveNetworkConnection(getActivity())){
					if(itemModel!=null){
						DetailProdottiActivity.startActivity(getActivity(), mCustomerCode, itemModel.getItem());
					}
				}
				else{
					Util.startAlertMsgActivity(getActivity(),R.string.msg_no_net_connection
							,R.string.chiudi);
				}
			}
		});
        
      
        
        mList.setAdapter(mAdapter);
        mList.setOnGroupExpandListener(new OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    mList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        
        fieldSearch = (EditText)view.findViewById(R.id.search_promotions);
		
		
		mLblTabStat = (TextView) view.findViewById(R.id.field_status);
    }
	
	private void setListeners() {
		// TODO Auto-generated method stub
		btnSegment.setOnCheckedChangeListener(this);
		fieldSearch.addTextChangedListener(searchWatcher);
	}

	//Initializes the loader with the given Id
	private void initLoader(int id){
		AsyncTaskLoader<?> loader = (AsyncTaskLoader<Object>) getLoaderManager().getLoader(id);
		if(loader!=null){
			PromotionsLoader assort = (PromotionsLoader)loader;
			assort.setCriteria(convertToCriteria(mBundle));
		}
		
		if(loader!=null&&loader.isReset()){
			Log.d(TAG, "Restarting");
			getLoaderManager().restartLoader(id,mBundle, this);
		}
		else{
			Log.d(TAG, "Init loader");
			getLoaderManager().initLoader(id, mBundle, this);
		}
	}

	//Convert the given bundle to search criteria
	public SearchCriteria convertToCriteria(Bundle bundle){
		if(bundle==null||bundle.isEmpty()){
			return null;
		}
		else{
			String argSearch = bundle.getString(VALUE_SEARCH);
			return new SearchCriteria(argSearch);
		}
		
	}
	

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	// ===========================================================
	// Inner and Anonymous Interface
	// ===========================================================

	private TextWatcher searchWatcher = new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			mBundle.putString(VALUE_SEARCH, s.toString());
			initLoader(btnSegment.getCheckedRadioButtonId());
			collapseAll();
		}
	};
	
	private void collapseAll(){
		int count =  mAdapter.getGroupCount();
		for (int i = 0; i <count ; i++)
		  mList.collapseGroup(i);
	}
	
	
	
	protected void startPromoDetailActivity(String promoCode,
			String mCustomerCode2) {
		Intent intent = new Intent(getActivity(),DetailPromozioniActivity.class);
		intent.putExtra(DetailPromozioniActivity.INTENT_PROMO_CODE, promoCode);
		intent.putExtra(DetailPromozioniActivity.INTENT_CUSTOMER_CODE, mCustomerCode2);
		startActivity(intent);
	}
	
	public static class PromotionsLoader 
		extends AsyncTaskLoader<ArrayList<ModelSurveyPromotions>> {
		
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================
		
		private ArrayList<ModelSurveyPromotions> promotions;
		private long surveyId;
		private SearchCriteria criteria;
		private String salesPersonCode;
		private String customerCode;
		private PromotionType type;
		
		// ===========================================================
		// Constructors
		// ===========================================================
		
		public PromotionsLoader(Context context,long surveyId,String customerCode,String salesPersonCode,PromotionType type,SearchCriteria criteria) {
			super(context);
			this.surveyId = surveyId;
			this.criteria = criteria;
			this.salesPersonCode = salesPersonCode;
			this.customerCode = customerCode;
			this.type 	  = type;
		}
		
		public void setCriteria(SearchCriteria criteria) {
			boolean criteriaChanged = false;
			if(criteria!=null){
				criteriaChanged = !criteria.equals(this.criteria);
			}
			else if(this.criteria!=null){
				criteriaChanged = true;
			}
			this.criteria = criteria;
			if(criteriaChanged){
				forceLoad();
				criteriaChanged = false;
			}
		}

		public PromotionsLoader(Context context,long surveyId,String customerCode,String salesPersonCode,PromotionType type) {
			this(context, surveyId, customerCode,salesPersonCode, type,null);
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================
		@Override
		public ArrayList<ModelSurveyPromotions> loadInBackground() {
			return TablePromotions.getSurveyPromotions(surveyId,
					customerCode, salesPersonCode, type, criteria);
		}
		
		
		
		/**
	     * Handles a request to start the Loader.
	     */
	    @Override protected void onStartLoading() {
	        if (promotions != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            deliverResult(promotions);
	        }

	       
	        if (takeContentChanged() || promotions == null) {
	            // If the data has changed since the last time it was loaded
	            // or is not currently available, start a load.
	            forceLoad();
//	            criteriaChanged = false;
	        }
	    }
	    
	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override protected void onReset() {
	        super.onReset();

	        // Ensure the loader is stopped
	        onStopLoading();

	        // At this point we can release the resources associated with 'apps'
	        // if needed.
	        if (promotions != null) {
	            promotions = null;
	        }
	    }
		
				
			
		
		@Override
		public void deliverResult(ArrayList<ModelSurveyPromotions> data) {
			super.deliverResult(data);
			if (isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (data != null) {
	            }
	        }
	        promotions = data;

	        if (isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(data);
	        }

	     
		}
		
		 /**
	     * Handles a request to stop the Loader.
	     */
	    @Override protected void onStopLoading() {
	        // Attempt to cancel the current load task if possible.
	        cancelLoad();
	    }
		// ===========================================================
		// Methods
		// ===========================================================
		
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}