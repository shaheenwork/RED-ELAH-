package com.eteam.dufour.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.customview.SegmentedRadioGroup;
import com.eteam.dufour.database.tables.TableItems;
import com.eteam.dufour.database.tables.TableItems.AssortimentoToLoad;
import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.dufour.database.tables.TableItems.SearchCriteria;
import com.eteam.dufour.database.tables.TableSurveyAssortimento.SurveyAssortimento;
import com.eteam.dufour.mobile.ConfirmCopyActivity;
import com.eteam.dufour.mobile.DetailProdottiActivity;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.newadpaters.AdapterAssortimento;
import com.eteam.dufour.newadpaters.AdapterAssortimento.OnAssortimentoSurveyActionListener;
import com.eteam.dufour.viewmodel.ModelSurveyAssortimento;
import com.eteam.utils.Util;
import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter.OnExpandClickListener;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;
import com.tjerkw.slideexpandable.library.SlideExpandableListView.OnLayoutChangeListener;

import org.apache.http.util.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

public class FragmentProddoti extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<ModelSurveyAssortimento>>, OnCheckedChangeListener {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TAG = "FragmentProdotti";

    private static final String EXTRA_SURVEY_ID = "com.eteam.dufour.fragments.FragmentAssortimento.INTENT_SURVEY_ID";
    private static final String EXTRA_CUSTOMER_NUMBER = "com.eteam.dufour.fragments.FragmentAssortimento.INTENT_CUSTOMER_NUMBER";
    private static final String EXTRA_SALES_PERSON_CODE = "com.eteam.dufour.fragments.FragmentAssortimento.INTENT_SALES_PERSON_CODE";

    private static final String VALUE_MARCHIO = "marchio";
    private static final String VALUE_LINEA = "linea";
    private static final String VALUE_MACROFAMIGLIA = "macrofamiglia";
    private static final String VALUE_SEARCH = "search";

    private static final int CALL_COPY_CONFIRM_ACTIVITY = 100;

    private ArrayList<ModelSurveyAssortimento> nonAssortitoItems;

    ArrayList<ModelSurveyAssortimento> newModels;
    private int firstIncompleteItemPosition = 0;

    // ===========================================================
    // Fields
    // ===========================================================

    private long survey_id;
    private String customerCode;
    private String salePersonCode;

    private TextView fieldListedNo;
    private TextView fieldTotalNo;
    private TextView fieldStatus;
    private TextView lblEmptyView;

    private SegmentedRadioGroup btnSegment;

    private Spinner spinnerMarchio;
    private Spinner spinnerLinea;
    private Spinner spinnerMacrofamiglia;

    private AdapterAssortimento mAdapter;


    private ActionSlideExpandableListView mList;

    private Bundle bundleArgs;

    private EditText fieldSearch;

    private ArrayAdapter<String> mAdapterMarchio, mAdapterLinea, mAdapterMacrofamiglia;
    private ArrayList<String> listMarchio, listLinea, listMacrofamiglia;

    private CopyProductTask mCopyTask;

    private Toast mToast;
    AssortimentoToLoad selectedTab = null;
    private static int highlightOrNot = 0;


    // ===========================================================
    // Constructors
    // ===========================================================

    public static FragmentProddoti getInstance(long surveyId,
                                               String customerNo, String salesPersonCode, int highlightIncompleteRow) {
        FragmentProddoti fragment = new FragmentProddoti();
        Bundle args = new Bundle();
        args.putLong(EXTRA_SURVEY_ID, surveyId);
        args.putString(EXTRA_CUSTOMER_NUMBER, customerNo);
        args.putString(EXTRA_SALES_PERSON_CODE, salesPersonCode);
        fragment.setArguments(args);
        highlightOrNot = highlightIncompleteRow;
		/*if (highlightIncompleteRow==1) {
			newModels.get(2).setAllMandatoryFieldsFilled(0);
			mAdapter.changeSource(newModels);
		}*/
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @SuppressLint("ShowToast")
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        bundleArgs = new Bundle();


        listMarchio = new ArrayList<String>();
        listLinea = new ArrayList<String>();
        listMacrofamiglia = new ArrayList<String>();

        mAdapterLinea = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item);
        mAdapterMarchio = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item);
        mAdapterMacrofamiglia = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item);

        mAdapterMarchio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAdapterLinea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAdapterMacrofamiglia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mToast = Toast.makeText(activity, "Sample", Toast.LENGTH_SHORT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            survey_id = getArguments().getLong(EXTRA_SURVEY_ID);
            customerCode = getArguments().getString(EXTRA_CUSTOMER_NUMBER);
            salePersonCode = getArguments().getString(EXTRA_SALES_PERSON_CODE);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        clearLastFocus();
        if (mCopyTask != null && mCopyTask.getStatus().equals(Status.RUNNING)) {
            return;
        }
        initialize(getView());
        setAdapters(btnSegment.getCheckedRadioButtonId());

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
//	        Toast.makeText(getActivity(), "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
//	        Toast.makeText(getActivity(), "keyboard hidden", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prodotti, container, false);
        initialize(view);

        setUpSpinners();
        setListeners();
        setSpinnerAdapters(btnSegment.getCheckedRadioButtonId());
        setAdapters(btnSegment.getCheckedRadioButtonId());

        return view;
    }


    public Loader<ArrayList<ModelSurveyAssortimento>> onCreateLoader(int id, Bundle bundle) {


        AssortimentoLoader loader = new AssortimentoLoader(getActivity(), survey_id
                , getToLoad(id), customerCode, salePersonCode, convertToCriteria(bundle));


        Log.d(TAG, "Starting loader " + id);

        return loader;
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<ModelSurveyAssortimento>> loader,
                               ArrayList<ModelSurveyAssortimento> models) {
        Log.d(TAG, "Loading finished with number " + models.size());
        if (models != null && btnSegment.getCheckedRadioButtonId() == loader.getId()) {

            Log.d(TAG, "Loading finished with number " + models.size());


            // NonAssortito items need not be shown in Prodotti rilevati tab. (but should be sent to backend)
            if (selectedTab == AssortimentoToLoad.PRODOTTI_RILEVATI) {

                nonAssortitoItems = new ArrayList<ModelSurveyAssortimento>();
                for (ModelSurveyAssortimento modelSurveyAssortimento : models) {

                    if (modelSurveyAssortimento.getSurvey().getAssorti() == 2) {
                        nonAssortitoItems.add(modelSurveyAssortimento);
                    }

                }
                models.removeAll(nonAssortitoItems);

            }

            newModels = models;
            mAdapter.changeSource(models);

            if (models.size() != 0) {
                mList.setSelection(0);
            }

            updateCount(loader.getId());


            if (spinnerMarchio != null)
                if (spinnerMarchio.getSelectedItemPosition() != 0 && spinnerLinea.getSelectedItemPosition() != 0
                        && spinnerMacrofamiglia.getSelectedItemPosition() != 0) {
                    mAdapter.setFilterSelected(true);
//					mAdapterProdotto.setFilterSelected(true);
                } else {
                    mAdapter.setFilterSelected(false);
//					mAdapterProdotto.setFilterSelected(false);
                }
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ModelSurveyAssortimento>> models) {

        mAdapter.clear();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int id) {

        Log.d(TAG, "Number of items " + mAdapter.getCount());
        clearLastFocus();

        selectedTab = getToLoad(id);


        mList.setSelectionAfterHeaderView();
        switch (id) {
            case R.id.btn_assortimento:
                fieldStatus.setText(R.string.msg_assortimento_selected);
                lblEmptyView.setText(R.string.msg_empty_prodotti);
//				mList.setOnExpandListener(this);
                mList.restoreToExpandableList();
                mList.setOnExpandListener(new OnExpandClickListener() {

                    @Override
                    public void onItemExpand(View parent, int position, ViewGroup viewgroup) {
                        if (btnSegment.getCheckedRadioButtonId() != R.id.btn_prodotti_rilevati) {
                            EditText fieldPrezzo = (EditText) parent.findViewById(R.id.prezzo_edit);
                            fieldPrezzo.requestFocus();
                            Log.d(TAG, "Expanded position at " + position);
                        }

                    }

                    @Override
                    public void onItemCollapsed(View parent, int position,
                                                ViewGroup viewgroup) {
                        if (btnSegment.getCheckedRadioButtonId() != R.id.btn_prodotti_rilevati) {
                            EditText fieldDummy = (EditText) parent.findViewById(R.id.dummyFocus);
                            ;
                            Log.d(TAG, "Dummy field gained focus " + fieldDummy.requestFocus());
                        }
                    }
                });
                mList.collapse();
                mAdapter.setProdottiRilevatiSelected(false);
                break;
            case R.id.btn_listino_completo:
                fieldStatus.setText(R.string.msg_listino_completo_selected);
                lblEmptyView.setText(R.string.msg_empty_prodotti);
//				mList.setOnExpandListener(this);
                mList.restoreToExpandableList();
                mList.setOnExpandListener(new OnExpandClickListener() {

                    @Override
                    public void onItemExpand(View parent, int position, ViewGroup viewgroup) {
                        if (btnSegment.getCheckedRadioButtonId() != R.id.btn_prodotti_rilevati) {
                            EditText fieldPrezzo = (EditText) parent.findViewById(R.id.prezzo_edit);
                            fieldPrezzo.requestFocus();
                            Log.d(TAG, "Expanded position at " + fieldPrezzo.requestFocus());
                        }
                    }

                    @Override
                    public void onItemCollapsed(View parent, int position,
                                                ViewGroup viewgroup) {
                        if (btnSegment.getCheckedRadioButtonId() != R.id.btn_prodotti_rilevati) {
                            EditText fieldDummy = (EditText) parent.findViewById(R.id.dummyFocus);
                            fieldDummy.requestFocus();
                            Log.d(TAG, "Dummy field gained focus " + fieldDummy.requestFocus());
                        }
                    }


                });
                mList.collapse();
                mAdapter.setProdottiRilevatiSelected(false);
                break;
            case R.id.btn_prodotti_rilevati:
                mAdapter.setProdottiRilevatiSelected(true);
                fieldStatus.setText(R.string.msg_prodotti_rilevati_selected);
                lblEmptyView.setText(R.string.msg_empty_prodotti_rivealti);
                mList.setOnExpandListener(null);
                mList.openAllViews();

                break;

            default:
                break;
        }
        initLoader(id, true);

//		getLoaderManager().initLoader(id, bundleArgs, this).forceLoad();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_COPY_CONFIRM_ACTIVITY && resultCode == Activity.RESULT_OK) {
            SurveyAssortimento survey = ConfirmCopyActivity.getSurvey(data);
            if (survey != null) {
                if (mCopyTask != null) {
                    if (mCopyTask.getStatus() == Status.FINISHED) {

                        mCopyTask = new CopyProductTask(getActivity(), survey);
                        mCopyTask.execute();
                    }
                } else {
                    mCopyTask = new CopyProductTask(getActivity(), survey);
                    mCopyTask.execute();
                }

            }
        }
    }


    // ===========================================================
    // Methods
    // ===========================================================
    private void initialize(View view) {
        // TODO Auto-generated method stub
        fieldListedNo = (TextView) view.findViewById(R.id.listed_no);
        fieldTotalNo = (TextView) view.findViewById(R.id.total_no);

        fieldStatus = (TextView) view.findViewById(R.id.field_status);

        btnSegment = (SegmentedRadioGroup) view.findViewById(R.id.segment_text);

        spinnerMarchio = (Spinner) view.findViewById(R.id.marchio);
        spinnerLinea = (Spinner) view.findViewById(R.id.linea);
        spinnerMacrofamiglia = (Spinner) view.findViewById(R.id.macrofamiglia);

        lblEmptyView = (TextView) view.findViewById(R.id.empty_view);


        mList = (ActionSlideExpandableListView) view.findViewById(R.id.list);
        mList.setEmptyView(lblEmptyView);

        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState != 0) {

                    View focusedView = getActivity().getCurrentFocus();
                    if (focusedView != null) {

                        InputMethodManager inputMethodManager = (InputMethodManager)
                                getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }
        });

        mList.setLayoutChangeListener(new OnLayoutChangeListener() {

            @Override
            public void beforeLayoutChanged() {
                mAdapter.saveFocus();
            }

            @Override
            public void afterLayoutChanged() {

                mAdapter.restoreFocus();
            }
        });

        mList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 1) {
                    // listView.setItemsCanFocus(true);

                    // Use afterDescendants, because I don't want the ListView to steal focus
                    mList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//			        myEditText.requestFocus();
                } else {
                    if (!mList.isFocused()) {
                        // listView.setItemsCanFocus(false);

                        // Use beforeDescendants so that the EditText doesn't re-take focus
                        mList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                        mList.requestFocus();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        fieldSearch = (EditText) view.findViewById(R.id.field_search);


    }

    private void updateCount(int id) {
        fieldListedNo.setText(String.valueOf(mAdapter.getCount()));

        switch (id) {
            case R.id.btn_assortimento:
                fieldTotalNo.setText(String.valueOf(TableItems.getAssortimentoTotalCount(survey_id, customerCode)));
                break;

            case R.id.btn_listino_completo:
                fieldTotalNo.setText(TableItems.getListinoCompletoCount(survey_id, customerCode));
                break;

            case R.id.btn_prodotti_rilevati:
                fieldTotalNo.setText(TableItems.getProdottiRivelatiCount(survey_id, customerCode));
                break;

            default:
                break;
        }
    }

    private AssortimentoToLoad getToLoad(int id) {
        AssortimentoToLoad toLoad = null;
        switch (id) {
            case R.id.btn_assortimento:
                toLoad = AssortimentoToLoad.ASSORTIMENTO;
                break;
            case R.id.btn_listino_completo:
                toLoad = AssortimentoToLoad.COMPLETE;
                break;
            case R.id.btn_prodotti_rilevati:
                toLoad = AssortimentoToLoad.PRODOTTI_RILEVATI;
                break;
            default:
                break;
        }
        return toLoad;
    }

    private void initLoader(int id) {
        initLoader(id, false);
    }

    //Initializes the loader with the given Id
    private void initLoader(int id, boolean forceLoad) {
        AsyncTaskLoader<?> loader = (AsyncTaskLoader<Object>) getLoaderManager().getLoader(id);
        if (loader != null) {
            AssortimentoLoader assort = (AssortimentoLoader) loader;
            assort.setCriteria(convertToCriteria(bundleArgs));
            if (forceLoad) {
                loader.forceLoad();
            }

        }

        if (loader != null && loader.isReset()) {
            Log.d(TAG, "Restarting");
            getLoaderManager().restartLoader(id, bundleArgs, this);
        } else {
            Log.d(TAG, "Init loader");
            getLoaderManager().initLoader(id, bundleArgs, this);
        }
    }

    //Convert the given bundle to search criteria
    public SearchCriteria convertToCriteria(Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return null;
        } else {
            String marchio = bundle.getString(VALUE_MARCHIO);
            String linea = bundle.getString(VALUE_LINEA);
            String macroFamiglia = bundle.getString(VALUE_MACROFAMIGLIA);
            String argSearch = bundle.getString(VALUE_SEARCH);
            return new SearchCriteria(marchio, linea, macroFamiglia, argSearch);
        }

    }

    private void setUpSpinners() {
        // TODO Auto-generated method stub
        spinnerMarchio.setAdapter(mAdapterMarchio);
        spinnerLinea.setAdapter(mAdapterLinea);
        spinnerMacrofamiglia.setAdapter(mAdapterMacrofamiglia);
    }


    private void setListeners() {
        // TODO Auto-generated method stub
        btnSegment.setOnCheckedChangeListener(this);
        fieldSearch.removeTextChangedListener(searchWatcher);
        fieldSearch.addTextChangedListener(searchWatcher);
        fieldSearch.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAdapter.clearFocus();
                } else {
                    fieldSearch.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }

            }

        });

        fieldSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fieldSearch.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        spinnerLinea.setOnItemSelectedListener(mSelectionListener);
        spinnerMarchio.setOnItemSelectedListener(mSelectionListener);
        spinnerMacrofamiglia.setOnItemSelectedListener(mSelectionListener);
    }

    private void setSpinnerAdapters(int chkdBtn) {
        populateMarchio(chkdBtn);
        populateLinea(chkdBtn);
        populateMacrogfamiglia(chkdBtn);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void populateLinea(int chckdBtn) {
        mAdapterLinea.clear();
        switch (chckdBtn) {
            case R.id.btn_assortimento:
                TableItems.populateLinea(listLinea, spinnerMarchio.getSelectedItem().toString(), "1");
                break;
            case R.id.btn_prodotti_rilevati:
            case R.id.btn_listino_completo:
                TableItems.populateLinea(listLinea, spinnerMarchio.getSelectedItem().toString());
                break;

            default:
                break;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            for (String value : listLinea) {
                mAdapterLinea.add(value);
            }

        } else
            mAdapterLinea.addAll(listLinea);
        spinnerLinea.setSelection(0);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void populateMarchio(int chckdBtn) {
        mAdapterMarchio.clear();
        switch (chckdBtn) {
            case R.id.btn_assortimento:
                TableItems.populateMarchio(listMarchio, "1");
                break;
            case R.id.btn_prodotti_rilevati:
                TableItems.populateMarchio(listMarchio);
                break;

            default:
                break;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            for (String value : listMarchio) {
                mAdapterMarchio.add(value);
            }

        } else
            mAdapterMarchio.addAll(listMarchio);

        spinnerMarchio.setSelection(0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void populateMacrogfamiglia(int chckdBtn) {
        mAdapterMacrofamiglia.clear();
        switch (chckdBtn) {
            case R.id.btn_assortimento:
                TableItems.populateMacrofamiglia(listMacrofamiglia, spinnerMarchio.getSelectedItem().toString()
                        , spinnerLinea.getSelectedItem().toString(), "1");
                break;
            case R.id.btn_prodotti_rilevati:
            case R.id.btn_listino_completo:
                TableItems.populateMacrofamiglia(listMacrofamiglia, spinnerMarchio.getSelectedItem().toString()
                        , spinnerLinea.getSelectedItem().toString());
                break;

            default:
                break;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            for (String value : listMacrofamiglia) {
                mAdapterMacrofamiglia.add(value);
            }

        } else
            mAdapterMacrofamiglia.addAll(listMacrofamiglia);


        spinnerMacrofamiglia.setSelection(0);
    }

    private void setAdapters(int checkId) {
        mAdapter = new AdapterAssortimento(getActivity());
        mList.setAdapter(mAdapter);
        if (btnSegment.getCheckedRadioButtonId() == R.id.btn_prodotti_rilevati) {
            mList.openAllViews();
        } else {
            mList.restoreToExpandableList();
        }
        mAdapter.setListener(new OnAssortimentoSurveyActionListener() {

            @Override
            public void onInfoClicked(Item item) {
                startProductDetailActivity(customerCode, item);
            }

            @Override
            public void onCopyClicked(ModelSurveyAssortimento model) {
                ConfirmCopyActivity.startActivity(FragmentProddoti.this, CALL_COPY_CONFIRM_ACTIVITY, mAdapter.getCount()
                        , model);
            }

            @Override
            public void onModified(ModelSurveyAssortimento item) {


                Util.refreshListRow(mList, checkIfMandatoryFieldsFilled(item));
			/*	newModels.get(2).setAllMandatoryFieldsFilled(0);
				mAdapter.changeSource(newModels);
*/
            }

            @Override
            public void onCountChanged() {
                // TODO Auto-generated method stub
                updateCount(btnSegment.getCheckedRadioButtonId());
            }

        });

        setSpinnerArgBundles(bundleArgs);
        initLoader(checkId);
//		getLoaderManager().initLoader(checkId, bundleArgs, this).forceLoad();
    }

    public static ModelSurveyAssortimento checkIfMandatoryFieldsFilled(ModelSurveyAssortimento item) {

        if (highlightOrNot == 1) {

            if (item.getSurvey().getAssorti() == 0 && item.getSurvey().getLineaOne() == 0 && item.getSurvey().getLineaTwo() == 0 && item.getSurvey().getFacing() == 0 && (TextUtils.isEmpty(item.getSurvey().getPrezzo()) || Double.parseDouble(item.getSurvey().getPrezzo().replace(',', '.')) == 0)) {
                item.setAllMandatoryFieldsFilled(1);

            } else if (item.getSurvey().getAssorti() != 2) {

                if (TextUtils.isEmpty(item.getSurvey().getPrezzo())) {
                    item.setAllMandatoryFieldsFilled(0);
                } else if (item.getSurvey().getAssorti() != 0 && item.getSurvey().getLineaOne() != 0 && item.getSurvey().getLineaTwo() != 0 && item.getSurvey().getFacing() != 0 && Double.parseDouble(item.getSurvey().getPrezzo().replace(',', '.')) != 0) {
                    item.setAllMandatoryFieldsFilled(1);

                } else {
                    item.setAllMandatoryFieldsFilled(0);
                }

            } else if (item.getSurvey().getAssorti() == 2) {

                item.setAllMandatoryFieldsFilled(1);
            }
        }
        return item;
    }

    public void changeBgColorAccordingToMandatoryFields(ArrayList<String> incompleteItemIds) {

        highlightOrNot = 1;

        mAdapter.changeSource(changeMandatoryFieldsFilledStatus(incompleteItemIds, newModels));

        mList.setSelection(firstIncompleteItemPosition);

    }

    private ArrayList<ModelSurveyAssortimento> changeMandatoryFieldsFilledStatus(ArrayList<String> incompleteItemIds, ArrayList<ModelSurveyAssortimento> newModels) {

        firstIncompleteItemPosition = 0;

        for (int i = 0; i < newModels.size(); i++) {
            if (incompleteItemIds.contains(newModels.get(i).getItem().getCode())) {
                newModels.get(i).setAllMandatoryFieldsFilled(0);
                if (firstIncompleteItemPosition == 0) {
                    firstIncompleteItemPosition = i;
                }
            } else {
                newModels.get(i).setAllMandatoryFieldsFilled(1);
            }
        }
        return newModels;

    }

    private void setSpinnerArgBundles(Bundle bundle) {
        bundle.clear();
        String mMarchioValue = spinnerMarchio.getSelectedItem().toString();
        String mLineaValue = spinnerLinea.getSelectedItem().toString();
        String mMacrofamigliaValue = spinnerMacrofamiglia.getSelectedItem().toString();
        String mSearch = fieldSearch.getText().toString();
        if (!mMarchioValue.equals(TableItems.MARCHIO_UNSELECTED)) {
            bundle.putString(VALUE_MARCHIO, mMarchioValue);
        }
        if (!mLineaValue.equals(TableItems.LINEA_UNSELECTED)) {
            bundle.putString(VALUE_LINEA, mLineaValue);
        }
        if (!mMacrofamigliaValue.equals(TableItems.MACROFAMIGLIA_UNSELECTED)) {
            bundle.putString(VALUE_MACROFAMIGLIA, mMacrofamigliaValue);
        }
        if (!mSearch.trim().equals("")) {
            bundle.putString(VALUE_SEARCH, mSearch);
        }
    }


    protected void startProductDetailActivity(String customerCode, Item item) {
        DetailProdottiActivity.startActivity(getActivity(), customerCode, item);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class CopyProductTask extends AsyncTask<Void, Void, Integer> {


        private static final int COPY_SUCCESS = 0;
        private static final int EMPTY_COPY = 1;

        private Context context;
        private ElahProgress dialog;

        private SurveyAssortimento item;

        public CopyProductTask(Context context, SurveyAssortimento item) {
            this.item = item;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ElahProgress(context);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
//			dialog.setIndeterminate(false);

            dialog.show(R.string.msg_copying);
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (dialog != null)
                dialog.dismiss();
            if (result == COPY_SUCCESS) {
//				mAdapter.notifyDataSetChanged();
                initLoader(btnSegment.getCheckedRadioButtonId());
//				getLoaderManager().initLoader(btnSegment.getCheckedRadioButtonId()
//						,bundleArgs,FragmentProddoti.this).forceLoad();
            } else if (result == EMPTY_COPY) {
                Util.showToast(mToast, R.string.msg_empty_copy);
            }

        }

        @Override
        protected Integer doInBackground(Void... params) {

            if (item != null) {
                mAdapter.copyValue(item);
                return COPY_SUCCESS;
            } else {
                return EMPTY_COPY;
            }

        }

    }

    public static class AssortimentoLoader
            extends AsyncTaskLoader<ArrayList<ModelSurveyAssortimento>> {

        // ===========================================================
        // Constants
        // ===========================================================
        // ===========================================================
        // Fields
        // ===========================================================


        private long surveyId;
        private SearchCriteria criteria;
        private String salesPersonCode;
        private ArrayList<ModelSurveyAssortimento> items;
        private String customerCode;
        private AssortimentoToLoad toLoad;


        // ===========================================================
        // Constructors
        // ===========================================================

        public AssortimentoLoader(Context context, long surveyId, AssortimentoToLoad toLoad,
                                  String customerCode, String salesPersonCode, SearchCriteria criteria) {
            super(context);
            this.surveyId = surveyId;
            this.toLoad = toLoad;
            this.criteria = criteria;
            this.salesPersonCode = salesPersonCode;
            this.customerCode = customerCode;

        }

        public AssortimentoLoader(Context context, long surveyId,
                                  AssortimentoToLoad toLoad, String customerCode, String salesPersonCode) {
            this(context, surveyId, toLoad, customerCode, salesPersonCode, null);
        }


        // ===========================================================
        // Getter & Setter
        // ===========================================================


        /***
         * If the criteria is different the loader is reloaded
         * @param criteria The search criteria to be set
         * */
        public void setCriteria(SearchCriteria criteria) {
            boolean criteriaChanged = false;
            if (criteria != null) {
                criteriaChanged = !criteria.equals(this.criteria);
            } else if (this.criteria != null) {
                criteriaChanged = true;
            }
            this.criteria = criteria;
            if (criteriaChanged) {
                forceLoad();
                criteriaChanged = false;
            }
        }

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        @Override
        public ArrayList<ModelSurveyAssortimento> loadInBackground() {
            return TableItems.getItems(surveyId, customerCode, salesPersonCode, toLoad, criteria);
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (items != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(items);
            }


            if (takeContentChanged() || items == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
//	            criteriaChanged = false;
            }
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (items != null) {
                items = null;
            }
        }


        @Override
        public void deliverResult(ArrayList<ModelSurveyAssortimento> data) {
            super.deliverResult(data);
            Log.d(TAG, "Delivering results " + data.size());
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (data != null) {
                }
            }
            items = data;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(data);
            }


        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
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

    // ===========================================================
    // Inner and Anonymous Interface
    // ===========================================================

    private TextWatcher searchWatcher = new TextWatcher() {

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {


        }

        public void afterTextChanged(Editable s) {
            bundleArgs.putString(VALUE_SEARCH, s.toString().trim());
            initLoader(btnSegment.getCheckedRadioButtonId());
//			getLoaderManager().initLoader(btnSegment.getCheckedRadioButtonId(),
//					bundleArgs, FragmentProddoti.this).forceLoad();
        }
    };

    private AdapterView.OnItemSelectedListener mSelectionListener = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> adpView, View view, int position,
                                   long id) {
            // TODO Auto-generated method stub
            if (adpView == spinnerMarchio) {
                populateLinea(btnSegment.getCheckedRadioButtonId());
            } else if (adpView == spinnerLinea) {
                populateMacrogfamiglia(btnSegment.getCheckedRadioButtonId());
            } else if (adpView == spinnerMacrofamiglia) {
                setSpinnerArgBundles(bundleArgs);
            }
            setSpinnerArgBundles(bundleArgs);
            initLoader(btnSegment.getCheckedRadioButtonId());
//			getLoaderManager().initLoader(btnSegment.getCheckedRadioButtonId()
//					,bundleArgs,FragmentProddoti.this).forceLoad();
            mList.collapse();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    private void clearLastFocus() {
        if (getView() != null)
            getView().clearFocus();
//		resetContentChanged();
    }


}
