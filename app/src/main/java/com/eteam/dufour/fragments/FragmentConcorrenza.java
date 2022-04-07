package com.eteam.dufour.fragments;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.eteam.dufour.database.tables.TableCompetitors;
import com.eteam.dufour.database.tables.TableCompetitors.SearchCriteria;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.newadpaters.AdapterCompetitors;
import com.eteam.dufour.newadpaters.AdapterCompetitors.OnCompetitorSurveyActionListener;
import com.eteam.dufour.viewmodel.ModelSurveyCompetitor;
import com.eteam.utils.Util;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

public class FragmentConcorrenza extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ModelSurveyCompetitor>> {
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final String TAG = FragmentConcorrenza.class.getName();
	
	private static final String VALUE_SEARCH = "search";
	private static final String VALUE_MARCHIO = "marchio";
	private static final String VALUE_LINEA = "linea";
	
	private static final String EXTRA_SURVEY_ID = "com.eteam.dufour.fragments.FragmentConconrrenza.EXTRA_SURVEY_ID";
	private static final String  EXTRA_SALES_PERSON_CODE = "com.eteam.dufour.fragments.FragmentConcorrenza.ETXRA_SALES_PERSON";
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Spinner mSpinnerMarchio;
	private Spinner mSpinnerLinea;
	
	private long   surveyId;
	private String salesPersonCode;
	
	private EditText mFieldSearch;
	private ActionSlideExpandableListView mListView;
	private TextView mFieldTotalNo;
	private TextView mFieldListedNo;
	
	private AdapterCompetitors mAdapter;
	
	private Bundle mBundle;
	
	private ArrayAdapter<String> mAdapterMarchio,mAdapterLinea;
	private ArrayList<String> listMarchio,listLinea;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public static final FragmentConcorrenza getInstance(long surveyId, String salesPersonCode){
		FragmentConcorrenza fragment = new FragmentConcorrenza();
		
		Bundle args = new Bundle();
		
		args.putLong(EXTRA_SURVEY_ID, surveyId);
		args.putString(EXTRA_SALES_PERSON_CODE, salesPersonCode);
		
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
		listMarchio = new ArrayList<String>();
		listLinea = new ArrayList<String>();
		
		
		mAdapterLinea = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item);
		mAdapterMarchio = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item);
		
			
		mAdapterMarchio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAdapterLinea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		initView(getView());
		setAdapters();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.compitition, container, false);
		initView(mView);
		setListeners();
		setUpSpinners();
		setSpinnerAdapters();
		setAdapters();
		return mView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			surveyId 		= getArguments().getLong(EXTRA_SURVEY_ID, -1);
			salesPersonCode = getArguments().getString(EXTRA_SALES_PERSON_CODE);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	public Loader<ArrayList<ModelSurveyCompetitor>> onCreateLoader(int id, Bundle mBundle) {
				
		return new CompetitorLoader(getActivity(), surveyId,salesPersonCode,convertToCriteria(mBundle));
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<ModelSurveyCompetitor>> loader
			, ArrayList<ModelSurveyCompetitor> competitors) {
		if(competitors!=null){
			mAdapter.changeSource(competitors);
			mFieldTotalNo.setText(String.valueOf(TableCompetitors.getCount(surveyId)));
			mFieldListedNo.setText(String.valueOf(mAdapter.getCount()));
		}
		
	}

	public void onLoaderReset(Loader<ArrayList<ModelSurveyCompetitor>> loader) {
		mAdapter.clearSource();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	private void initView(View mView) {
		mSpinnerLinea = (Spinner) mView.findViewById(R.id.linea_competition);
		mSpinnerMarchio = (Spinner) mView.findViewById(R.id.marchio_competition);
		
		mFieldSearch = (EditText) mView.findViewById(R.id.search_compitition);
		mFieldSearch.removeTextChangedListener(mSearchWatcher);
		mFieldSearch.addTextChangedListener(mSearchWatcher);
		mFieldSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mAdapter.clearFocus();
			}
		});
		
		mListView = (ActionSlideExpandableListView) mView.findViewById(R.id.list);
		mListView.setEmptyView(mView.findViewById(R.id.empty_view));
		
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 1)
			    {
			        // listView.setItemsCanFocus(true);

			        // Use afterDescendants, because I don't want the ListView to steal focus
					mListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//			        myEditText.requestFocus();
			    }
			    else
			    {
			        if (!mListView.isFocused())
			        {
			            // listView.setItemsCanFocus(false);

			            // Use beforeDescendants so that the EditText doesn't re-take focus
			        	mListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
			        	mListView.requestFocus();
			        }
			    }
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});	
		
		mFieldListedNo = (TextView) mView.findViewById(R.id.listed_no);
		mFieldTotalNo = (TextView) mView.findViewById(R.id.total_no);
		
		mAdapter = new AdapterCompetitors(getActivity());
		mAdapter.setListener(new OnCompetitorSurveyActionListener() {
			
			@Override
			public void onModified(ModelSurveyCompetitor item) {
				Util.refreshListRow(mListView, item);
			}
			
			@Override
			public void onMinimiseClicked() {
				mListView.collapse();
			}
		});
	}
	
	private void setAdapters(){
		mListView.setAdapter(mAdapter);
		setSpinnerArgBundles(mBundle);
		initLoader();
	}
	
	private void setSpinnerAdapters(){
		populateMarchio();
		populateLinea();
	}
	
	//Initializes the loader with the given Id
	private void initLoader(){
		AsyncTaskLoader<?> loader = (AsyncTaskLoader<Object>) getLoaderManager().getLoader(0);
		if(loader!=null){
			CompetitorLoader assort = (CompetitorLoader)loader;
			assort.setCriteria(convertToCriteria(mBundle));
		}
		
		if(loader!=null&&loader.isReset()){
			Log.d(TAG, "Restarting");
			getLoaderManager().restartLoader(0,mBundle, this);
		}
		else{
			Log.d(TAG, "Init loader");
			getLoaderManager().initLoader(0, mBundle, this);
		}
	}
	
	//Convert the given bundle to search criteria
	public SearchCriteria convertToCriteria(Bundle bundle){
		if(bundle==null||bundle.isEmpty()){
			return null;
		}
		else{
			String marchio 	= bundle.getString(VALUE_MARCHIO);
			String linea	= bundle.getString(VALUE_LINEA);
			String argSearch = bundle.getString(VALUE_SEARCH);
			return new SearchCriteria(marchio, linea, argSearch);
		}
		
	}
	
	private void setSpinnerArgBundles(Bundle bundle){
		bundle.clear();
		String mMarchioValue = mSpinnerMarchio.getSelectedItem().toString();
		String mLineaValue = mSpinnerLinea.getSelectedItem().toString();
		String mSearch = mFieldSearch.getText().toString();
		if(!mMarchioValue.equals(TableCompetitors.MARCHIO_UNSELECTED)){
			bundle.putString(VALUE_MARCHIO, mMarchioValue);
		}
		if(!mLineaValue.equals(TableCompetitors.LINEA_UNSELECTED)){
			bundle.putString(VALUE_LINEA, mLineaValue);
		}
		if(!mSearch.trim().equals("")){
			bundle.putString(VALUE_SEARCH, mSearch);
		}
	}
	
	private void setListeners() {
		// TODO Auto-generated method stub
		mSpinnerMarchio.setOnItemSelectedListener(topSpinnerListener);
		mSpinnerLinea.setOnItemSelectedListener(topSpinnerListener);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void populateLinea(){
		mAdapterLinea.clear();
		TableCompetitors.populateLinea(listLinea,mSpinnerMarchio.getSelectedItem().toString());
		mAdapterLinea.addAll(listLinea);
		mSpinnerLinea.setSelection(0);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void populateMarchio(){
		mAdapterMarchio.clear();
		TableCompetitors.populateMarchio(listMarchio);
		mAdapterMarchio.addAll(listMarchio);
		mSpinnerMarchio.setSelection(0);
	}
	
	private void setUpSpinners() {
		// TODO Auto-generated method stub
		mSpinnerMarchio.setAdapter(mAdapterMarchio);
		mSpinnerLinea.setAdapter(mAdapterLinea);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================


	// ===========================================================
	// Inner and Anonymous Interfaces
	// ===========================================================

	private OnItemSelectedListener topSpinnerListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> adpView, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(adpView==mSpinnerMarchio){
				populateLinea();
			}
			setSpinnerArgBundles(mBundle);
			initLoader();
			if(mListView!=null){
				mListView.collapse();
				Util.hideKeyPad(getActivity(), mListView);
			}
			
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
		
	private TextWatcher mSearchWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			mBundle.putString(VALUE_SEARCH, s.toString().trim());
			initLoader();
			mListView.collapse();
		}
	};
	
	
	public static class CompetitorLoader 
				extends AsyncTaskLoader<ArrayList<ModelSurveyCompetitor>> {
		
		
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================

		private ArrayList<ModelSurveyCompetitor> competitors;
		private long surveyId;
		private SearchCriteria criteria;
		private String salesPersonCode;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public CompetitorLoader(Context context,long surveyId,String salesPersonCode) {
            this(context, surveyId, salesPersonCode, null);
        }
		
        public CompetitorLoader(Context context,long surveyId,String salesPersonCode,SearchCriteria criteria) {
            super(context);
            this.surveyId = surveyId;
            this.criteria = criteria;
            this.salesPersonCode = salesPersonCode;
        }
        
		// ===========================================================
		// Getter & Setter
		// ===========================================================

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
		
		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================


        @Override
        public ArrayList<ModelSurveyCompetitor> loadInBackground() {
            return TableCompetitors.getCompetitors(surveyId, salesPersonCode, criteria);
        }
        
        /**
	     * Handles a request to start the Loader.
	     */
	    @Override protected void onStartLoading() {
	        if (competitors != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            deliverResult(competitors);
	        }

	       
	        if (takeContentChanged() || competitors == null) {
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
	        if (competitors != null) {
	            competitors = null;
	        }
	    }
		
				
			
		
		@Override
		public void deliverResult(ArrayList<ModelSurveyCompetitor> data) {
			super.deliverResult(data);
			Log.d(TAG, "Delivering results "+data.size());
			if (isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (data != null) {
	            }
	        }
	        competitors = data;

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