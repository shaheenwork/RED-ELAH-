package com.eteam.dufour.fragments;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eteam.dufour.async.SyncTask;
import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.database.tables.TableCustomers;
import com.eteam.dufour.database.tables.TableCustomers.BaseAddress;
import com.eteam.dufour.database.tables.TableCustomers.BaseCustomer;
import com.eteam.dufour.database.tables.TableItems;
import com.eteam.dufour.database.tables.TableLogin;
import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.dufour.database.tables.TableSurvey.*;
import com.eteam.dufour.database.tables.TableSurveyAssortimento;
import com.eteam.dufour.database.tables.TableSurveyCompetitor;
import com.eteam.dufour.database.tables.TableSurveyCustomers;
import com.eteam.dufour.database.tables.TableSurveyPromotions;
import com.eteam.dufour.database.tables.TableSyncInfo;
import com.eteam.dufour.mobile.BuildConfig;
import com.eteam.dufour.mobile.LoginActivity;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.newadpaters.DashboardAdapter;
import com.eteam.dufour.viewmodel.ModelDashboardItem;
import com.eteam.dufour.viewmodel.ModelDashboardRow;
import com.eteam.dufour.viewmodel.ModelProductForDashboard;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.Util;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class FragmentChiusuraAttivita extends Fragment implements OnClickListener {
    // ===========================================================
    // Constants
    // ===========================================================
;
    public static final String TAG = "com.eteam.dufour.fragmentsFragmentChiusuraArrivita";

    private static final String EXTRA_USERNAME = "com.eteam.dufour.fragments.FragmentChiusuraAttivita.EXTRA_USERNAME";
    private static final String EXTRA_CUSTOMER_CODE = "com.eteam.dufour.fragments.FragmentChiusuraAttivita.EXTRA_CUSTOMER_CODE";
    private static final String EXTRA_SURVEY_ID = "com.eteam.dufour.fragments.FragmentChiusuraAttivita.EXTRA_SURVEY_ID";
    private static final String EXTRA_CLUSTER_VALUE = "com.eteam.dufour.fragments.FragmentChiusuraAttivita.EXTRA_CLUSTER_VALUE";
    private static final String EXTRA_CLUSTER_VALUE2 = "com.eteam.dufour.fragments.FragmentChiusuraAttivita.EXTRA_CLUSTER_VALUE2";
    private static final String EXTRA_CLUSTER_VALUE3 = "com.eteam.dufour.fragments.FragmentChiusuraAttivita.EXTRA_CLUSTER_VALUE3";
    private static final String EXTRA_CLUSTER_VALUE_STAT = "com.eteam.dufour.fragments.FragmentChiusuraAttivita.EXTRA_CLUSTER_VALUE_STAT";
    public static final int CURRENT_SURVEY_SEND_FAILED = 1;
    public static final int SURVEY_SEND_SUCCESS_ONLINE = 2;
    public static final int SURVEY_SEND_SUCCESS_OFFLINE = 4;
    public static final int SURVEY_EMPTY = 3;
    public static final int INVIA_DATI_SURVEY_FAILED = 5;


    // ===========================================================
    // Fields
    // ===========================================================

    private TextView mFieldSync;
    private TextView mFieldCustName;
    private TextView mFieldCustCode;
    private TextView mFieldAddress1;
    private TextView mFieldAddress2;

    private View mBtnInviaDati;

    private String userName;
    private String passWord;
    private String customerCode;
    private long surveyId;
    private int clusterValue;
    private int clusterValue2;
    private int clusterValue3;
    private int clusterStatus;
    private String versionId;

    private OnSurveySentListener mSurveySentListener;

    private SurveySendTask task;


    //dashboard
    private ArrayList<String> listLinea;
    private List<String> listCategories;
    private List<String> listPresenzaProdotto;
    private List<String> listRottDiStock;
    private List<String> listFacing;
    private List<String> listQualitaEspositiva;
    private SharedPreferences mPrefs;
    ArrayList<ModelProductForDashboard> products_new;
    ArrayList<ModelProductForDashboard> products_old;
    public String salesPersonCode;
    private ListView RV_dashboard;
    private ScrollView scrollDashBoard;
    TextView tv_no_dashboard;
    private DashboardAdapter dashboardAdapter;
    private List<ModelDashboardRow> valuesList;
    GenerateDashboardTask generateDashboardTask;
    private ElahProgress mDashboardProgress;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static FragmentChiusuraAttivita getInstance(long surveyId, String userName, String customerCode, int clusterValue, int clusterValue2, int clusterValue3, int clusterStatus) {
        FragmentChiusuraAttivita fragment = new FragmentChiusuraAttivita();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, userName);
        bundle.putString(EXTRA_CUSTOMER_CODE, customerCode);
        bundle.putLong(EXTRA_SURVEY_ID, surveyId);
        bundle.putInt(EXTRA_CLUSTER_VALUE, clusterValue);
        bundle.putInt(EXTRA_CLUSTER_VALUE2, clusterValue2);
        bundle.putInt(EXTRA_CLUSTER_VALUE3, clusterValue3);
        bundle.putInt(EXTRA_CLUSTER_VALUE_STAT, clusterStatus);
        fragment.setArguments(bundle);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

//	public OnSurveySentListener getSurveySentListener() {
//		return mSurveySentListener;
//	}
//
//	public void setSurveySentListener(OnSurveySentListener mSurveySentListener) {
//		this.mSurveySentListener = mSurveySentListener;
//	}

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        versionId = Util.getApplicationVersion(getActivity());
        if (activity instanceof OnSurveySentListener) {
            mSurveySentListener = (OnSurveySentListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(EXTRA_USERNAME);
            customerCode = getArguments().getString(EXTRA_CUSTOMER_CODE);
            surveyId = getArguments().getLong(EXTRA_SURVEY_ID);
            Log.e("surv-id", String.valueOf(surveyId));
            clusterValue = getArguments().getInt(EXTRA_CLUSTER_VALUE);
            clusterValue2 = getArguments().getInt(EXTRA_CLUSTER_VALUE2);
            clusterValue3 = getArguments().getInt(EXTRA_CLUSTER_VALUE3);
            clusterStatus = getArguments().getInt(EXTRA_CLUSTER_VALUE_STAT);
        }
        listLinea = new ArrayList<String>();
        listCategories = new ArrayList<String>();
        listPresenzaProdotto = new ArrayList<String>();
        listRottDiStock = new ArrayList<String>();
        listFacing = new ArrayList<String>();
        listQualitaEspositiva = new ArrayList<String>();
        products_new = new ArrayList<ModelProductForDashboard>();
        products_old = new ArrayList<ModelProductForDashboard>();
        valuesList = new ArrayList<ModelDashboardRow>();
        mPrefs = getActivity().getSharedPreferences(Consts.PREF_ELAH, MODE_PRIVATE);
        salesPersonCode = mPrefs.getString(LoginActivity.PREF_SALESPERSON, "");

        generateDashboardTask = new GenerateDashboardTask();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        passWord = TableLogin.getPassword(userName);

        View view = inflater.inflate(R.layout.chiusura_attivita, container, false);

        mFieldSync = (TextView) view.findViewById(R.id.field_sync_date);
        mFieldCustName = (TextView) view.findViewById(R.id.field_cust_name);
        mFieldCustCode = (TextView) view.findViewById(R.id.field_cust_code);
        mFieldAddress1 = (TextView) view.findViewById(R.id.field_address_1);
        mFieldAddress2 = (TextView) view.findViewById(R.id.field_address_2);

        mBtnInviaDati = view.findViewById(R.id.btn_invia_dati);

        mFieldSync.setText(TableSyncInfo.getSyncDate());
        BaseCustomer customer = TableCustomers.getBaseCustomer(customerCode);
        BaseAddress address = customer.getAddress();
        mFieldCustName.setText(customer.getName());
        mFieldCustCode.setText(customer.getCode());
        mFieldAddress1.setText(address.getAddress());
        mFieldAddress2.setText(address.getPostalCode() + "-" + address.getCity());

        mBtnInviaDati.setOnClickListener(this);

        RV_dashboard = (ListView) view.findViewById(R.id.rv_dashboard);
        scrollDashBoard = (ScrollView) view.findViewById(R.id.lyt_dashboard);
        tv_no_dashboard = (TextView) view.findViewById(R.id.tv_no_dashboard);

       /* RV_dashboard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollDashBoard.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
*/
        //  generateDashBoard();
        scrollDashBoard.setVisibility(View.GONE);
        tv_no_dashboard.setVisibility(View.GONE);
        if (TableItems.isOldSurveyPresent(surveyId, customerCode)) {
            generateDashboardTask.execute();
        } else {
            // scrollDashBoard.setVisibility(View.GONE);
            tv_no_dashboard.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += 50;
        }
        Log.e("h-dp", "" + totalHeight);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int pixels = (int) (totalHeight * getResources().getDisplayMetrics().density);
        Log.e("h-px", "" + pixels);
        params.height = (int) (pixels
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) * getResources().getDisplayMetrics().density);
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void generateDashBoard() {


        //getting all Linea
        listLinea.clear();
        TableItems.populateLinea(listLinea, null);
        listLinea.remove(0); //remove "Linea" item


        //Categories (static)
        listCategories = Arrays.asList(Consts.ARRAY_CATEGORY);


        //calculations

        // - Presenza prodotto
        listPresenzaProdotto.clear();
        listRottDiStock.clear();
        listFacing.clear();
        listQualitaEspositiva.clear();
        calculateValuesForDashBoard(listLinea, surveyId);
        dashboardAdapter = new DashboardAdapter(valuesList, getActivity());
    }

    private void calculateValuesForDashBoard(ArrayList<String> listLinea, long surveyId) {

        valuesList.clear();
        ModelDashboardItem modelDashboardItem;
        ModelDashboardRow modelDashboardRow;

        mAddHeadings(valuesList);

        for (String linea : listLinea) {

            double disponibile_num_new = 0;  // total number of products set as disponibile in new survey
            double disponibile_num_old = 0;  // total number of products set as disponibile in old survey
            double total_facing_new = 0; //sum of facing in new survey
            double total_facing_old = 0; //sum of facing in old survey
            double goodPlacedproducts_num_new = 0; //number of good placed products in new survey
            double goodPlacedproducts_num_old = 0; //number of good placed products in old survey
            products_new.clear();
            double rottDiStock_num_new = 0;  // total number of products set as rottDiStock in new survey
            double rottDiStock_num_old = 0;  // total number of products set as rottDiStock in old survey
            products_old.clear();

            products_new = TableItems.getProductsInNewSurveyByLinea(surveyId, customerCode, linea, salesPersonCode); //all products under a linea in new survey
            products_old = TableItems.getProductsInOldSurveyByLinea(surveyId, customerCode, linea, salesPersonCode); //all products under a linea in old survey


            //new survey

            for (ModelProductForDashboard item : products_new) {
                total_facing_new += item.getFacing();
                if (item.getSurveyLevel() == Consts.SURVEY_LEVEL_DISPONIBILE) {
                    disponibile_num_new++;
                } else if (item.getSurveyLevel() == Consts.SURVEY_LEVEL_ROTTI_DI_STOCK) {
                    rottDiStock_num_new++;
                }
                if (isGoodPlaced(item.getPosLin())) {
                    goodPlacedproducts_num_new++;
                }

            }

            //old survey
            for (ModelProductForDashboard item : products_old) {
                total_facing_old += item.getFacing();
                if (item.getSurveyLevel() == Consts.SURVEY_LEVEL_DISPONIBILE) {
                    disponibile_num_old++;
                } else if (item.getSurveyLevel() == Consts.SURVEY_LEVEL_ROTTI_DI_STOCK) {
                    rottDiStock_num_old++;
                }

                if (isGoodPlaced(item.getPosLin())) {

                    goodPlacedproducts_num_old++;
                }
            }

          /*  listPresenzaProdotto.add(getPercentageString(mCalculatePercentage(disponibile_num_new, products_new.size()),mCalculatePercentage(disponibile_num_old, products_old.size())));
            listRottDiStock.add(getPercentageString(mCalculatePercentage(rottDiStock_num_new, products_new.size()),mCalculatePercentage(rottDiStock_num_old,products_old.size())));
            listQualitaEspositiva.add(getPercentageString(mCalculatePercentage(goodPlacedproducts_num_new,products_new.size()),mCalculatePercentage(goodPlacedproducts_num_old,products_old.size())));
            listFacing.add(getFacingString(mCalculateAverqage(total_facing_new, products_new.size()),mCalculateAverqage(total_facing_old,products_old.size())));
*/

            modelDashboardRow = new ModelDashboardRow();

            modelDashboardItem = new ModelDashboardItem(Consts.TYPE_VALUE, getPercentageString(mCalculatePercentage(disponibile_num_new, products_new.size()), mCalculatePercentage(disponibile_num_old, products_old.size())), Consts.NO_CIRCLE, mPlusOrMinus(mCalculatePercentage(disponibile_num_new, products_new.size()), mCalculatePercentage(disponibile_num_old, products_old.size()), false));
            modelDashboardRow.setVal2(modelDashboardItem);

            modelDashboardItem = new ModelDashboardItem(Consts.TYPE_VALUE, getPercentageString(mCalculatePercentage(rottDiStock_num_new, products_new.size()), mCalculatePercentage(rottDiStock_num_old, products_old.size())), Consts.NO_CIRCLE, mPlusOrMinus(mCalculatePercentage(rottDiStock_num_new, products_new.size()), mCalculatePercentage(rottDiStock_num_old, products_old.size()), true));
            modelDashboardRow.setVal3(modelDashboardItem);

            modelDashboardItem = new ModelDashboardItem(Consts.TYPE_VALUE, getFacingString(mCalculateAverqage(total_facing_new, products_new.size()), mCalculateAverqage(total_facing_old, products_old.size())), Consts.NO_CIRCLE, mPlusOrMinus(mCalculateAverqage(total_facing_new, products_new.size()), mCalculateAverqage(total_facing_old, products_old.size()), false));
            modelDashboardRow.setVal4(modelDashboardItem);

            modelDashboardItem = new ModelDashboardItem(Consts.TYPE_VALUE, getPercentageString(mCalculatePercentage(goodPlacedproducts_num_new, products_new.size()), mCalculatePercentage(goodPlacedproducts_num_old, products_old.size())), Consts.NO_CIRCLE, mPlusOrMinus(mCalculatePercentage(goodPlacedproducts_num_new, products_new.size()), mCalculatePercentage(goodPlacedproducts_num_old, products_old.size()), false));
            modelDashboardRow.setVal5(modelDashboardItem);

            modelDashboardItem = new ModelDashboardItem(Consts.TYPE_HEADING2, linea, mCircleColor(modelDashboardRow), Consts.NA);
            modelDashboardRow.setVal1(modelDashboardItem);

            valuesList.add(modelDashboardRow);
        }

      /*  Log.e("listPresenzaProdotto", listPresenzaProdotto.toString());
        Log.e("listRottDiStock", listRottDiStock.toString());
        Log.e("listFacing", listFacing.toString());
        Log.e("listQualitaEspositiva", listQualitaEspositiva.toString());
*/
    }

    private int mCircleColor(ModelDashboardRow modelDashboardRow) {
        int plusNumber = 0;
        int minusNumber = 0;
        int noChangeNumber = 0;
        if (modelDashboardRow.getVal2().getPlusOrMinus() == Consts.PLUS) {
            plusNumber++;
        } else if (modelDashboardRow.getVal2().getPlusOrMinus() == Consts.NO_CHANGE) {
            noChangeNumber++;
        } else {
            minusNumber++;
        }

        if (modelDashboardRow.getVal3().getPlusOrMinus() == Consts.PLUS) {
            plusNumber++;
        } else if (modelDashboardRow.getVal3().getPlusOrMinus() == Consts.NO_CHANGE) {
            noChangeNumber++;
        } else {
            minusNumber++;
        }

        if (modelDashboardRow.getVal4().getPlusOrMinus() == Consts.PLUS) {
            plusNumber++;
        } else if (modelDashboardRow.getVal4().getPlusOrMinus() == Consts.NO_CHANGE) {
            noChangeNumber++;
        } else {
            minusNumber++;
        }

        if (modelDashboardRow.getVal5().getPlusOrMinus() == Consts.PLUS) {
            plusNumber++;
        } else if (modelDashboardRow.getVal5().getPlusOrMinus() == Consts.NO_CHANGE) {
            noChangeNumber++;
        } else {
            minusNumber++;
        }


        if (noChangeNumber == 4) {
            return Consts.NO_CIRCLE;
        } else if (plusNumber == minusNumber) {
            return Consts.YELLOW_CIRCLE;
        } else if (plusNumber > minusNumber) {
            return Consts.GREEN_CIRCLE;
        } else {
            return Consts.RED_CIRCLE;
        }

    }

    private int mPlusOrMinus(double val1, double val2, boolean inverseColor) {

        if (inverseColor) {
            if (val1 == val2) {
                return Consts.NO_CHANGE;
            } else if (val1 > val2) {
                return Consts.MINUS;
            } else {
                return Consts.PLUS;
            }

        } else {
            if (val1 == val2) {
                return Consts.NO_CHANGE;
            } else if (val1 > val2) {
                return Consts.PLUS;
            } else {
                return Consts.MINUS;
            }

        }

    }

    private void mAddHeadings(List<ModelDashboardRow> valuesList) {
        ModelDashboardRow modelDashboardRow;
        ModelDashboardItem modelDashboardItem;
        modelDashboardRow = new ModelDashboardRow();

        modelDashboardItem = new ModelDashboardItem(Consts.TYPE_HEADING1, "Categoria", Consts.NO_CIRCLE, Consts.NA);
        modelDashboardRow.setVal1(modelDashboardItem);

        modelDashboardItem = new ModelDashboardItem(Consts.TYPE_HEADING2, "Presenza Pt. (%)", Consts.NO_CIRCLE, Consts.NA);
        modelDashboardRow.setVal2(modelDashboardItem);

        modelDashboardItem = new ModelDashboardItem(Consts.TYPE_HEADING2, "Rott. Di Stock (%)", Consts.NO_CIRCLE, Consts.NA);
        modelDashboardRow.setVal3(modelDashboardItem);

        modelDashboardItem = new ModelDashboardItem(Consts.TYPE_HEADING2, "Facing Medio", Consts.NO_CIRCLE, Consts.NA);
        modelDashboardRow.setVal4(modelDashboardItem);

        modelDashboardItem = new ModelDashboardItem(Consts.TYPE_HEADING2, getResources().getString(R.string.qualita_espositiva), Consts.NO_CIRCLE, Consts.NA);
        modelDashboardRow.setVal5(modelDashboardItem);

        valuesList.add(modelDashboardRow);
    }

    private boolean isGoodPlaced(String posLin) {
        if (posLin == null || TextUtils.isEmpty(posLin) || posLin.equals("/")) {
            return false;
        } else {
            int v1end = posLin.indexOf("/");
            if (v1end != -1) {
                int val1 = 0;
                int val2 = 0;
                try {
                    val1 = Integer.parseInt(posLin.substring(0, posLin.indexOf("/")));
                    val2 = Integer.parseInt(posLin.substring(posLin.indexOf("/") + 1));
                } catch (NumberFormatException e) {
                    return false;
                }

                if (val1 >= 5 && val1 <= 8 && val2 >= 5 && val2 <= 8) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        }

    }

    private double mCalculateAverqage(double total_facing_new, double size) {

        double average = 0;


        if (size != 0) {
            average = (total_facing_new) / size;
        }


        return average;
    }

    private String getFacingString(double new_average, double old_average) {
        String final_value;
        if (new_average >= old_average) {
            final_value = Util.decimalFormat(new_average) + " (+" + Util.decimalFormat(new_average - old_average) + ")";
        } else {
            final_value = Util.decimalFormat(new_average) + " (" + Util.decimalFormat(new_average - old_average) + ")";
        }

        return final_value;
    }

    private double mCalculatePercentage(double value, double size) {

        double percent = 0;
        if (size != 0) {
            percent = (value * 100) / size;
        }

        return percent;
    }

    private String getPercentageString(double new_percent, double old_percent) {
        String final_value;
        if (new_percent >= old_percent) {
            final_value = Util.decimalFormat(new_percent) + "% (+" + Util.decimalFormat(new_percent - old_percent) + "%)";
        } else {
            final_value = Util.decimalFormat(new_percent) + "% (" + Util.decimalFormat(new_percent - old_percent) + "%)";
        }

        return final_value;
    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == mBtnInviaDati) {
            sendCurrentSurvey();
        }
    }

    public void sendCurrentSurvey() {
        if (Util.haveNetworkConnection(getActivity())) {

            if (task != null && task.getStatus() == Status.FINISHED) {
                task = new SurveySendTask(getActivity());
                task.execute();
            } else if (task == null) {
                task = new SurveySendTask(getActivity());
                task.execute();
            }
        } else {
            JSONObject currentSurvey;

            try {


                currentSurvey = TableSurvey.getSurveyAsJSON(surveyId, true);
                if (isJSONArrayEmpty(currentSurvey, TableSurveyPromotions.KEY_JSON_ARRAYNAME)
                        && isJSONArrayEmpty(currentSurvey, TableSurveyAssortimento.KEY_JSON_ARRAYNAME)
                        && isJSONArrayEmpty(currentSurvey, TableSurveyCompetitor.KEY_JSON_ARRAYNAME)) {
                    mSurveySentListener.onSurveySent(SURVEY_EMPTY);
                    return;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mSurveySentListener.onSurveySent(CURRENT_SURVEY_SEND_FAILED);
                return;
            }

            TableSurvey.updateSurveySentDate(surveyId, Util.getCurrentDateTime(), clusterValue, clusterValue2, clusterValue3, clusterStatus);
            TableSurvey.updateFlag(surveyId, TableSurvey.FLAG_TO_BE_SENT);

            mSurveySentListener.onSurveySent(SURVEY_SEND_SUCCESS_OFFLINE);
        }
    }


    // ===========================================================
    // Methods
    // ===========================================================
    private boolean isJSONArrayEmpty(JSONObject object, String arrayKey) throws JSONException {

        if (object == null || object.getJSONArray(arrayKey).length() == 0)
            return true;
        else
            return false;
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================


    private class SurveySendTask extends AsyncTask<Void, Integer, Integer> {
        private static final String KEY_PAGE_NO = "page_no";

        private static final String KEY_POST_JSON = "syncdata";
        private static final String KEY_REQ_FOR = "reqfor";
        private static final String KEY_USER_NAME = "user";
        private static final String KEY_PASSWORD = "pwd";
        private static final String KEY_VERSION_ID = "versionId";
        private static final String KEY_CURRENT_TIME = "ct";

        private static final String KEY_RESPONSE = "errorcode";

        private static final String RESPONSE_SUCCESS = "1";

        private static final int SENDING_CURRENT_SURVEY = -4;
        private static final int CURRENT_SURVEY_SUCCESS = -5;
        private static final int SENDING_TO_BE_SENT = -6;
        private static final int TO_BE_SENT_SUCCESS = -7;

        private ElahProgress mSyncProgress;
        private ElahProgress mDashboardProgress;
        private Context mContext;


        public SurveySendTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mSyncProgress = new ElahProgress(mContext);
            mSyncProgress.setCanceledOnTouchOutside(false);
            mSyncProgress.setCancelable(false);
            if (mSyncProgress != null) {
                mSyncProgress.show(R.string.senting);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            if (mSyncProgress != null) {
                if (values[0] == SENDING_CURRENT_SURVEY) {
                    mSyncProgress.setMessage(R.string.senting);
                } else if (values[0] == CURRENT_SURVEY_SUCCESS) {
                    mSyncProgress.setMessage(R.string.senting);
                } else if (values[0] == SENDING_TO_BE_SENT) {
                    mSyncProgress.setMessage(R.string.senting);
                } else if (values[0] == TO_BE_SENT_SUCCESS) {
                    mSyncProgress.setMessage(R.string.senting_successful);
                }
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mSyncProgress != null)
                mSyncProgress.dismiss();
            mSurveySentListener.onSurveySent(result);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Integer doInBackground(Void... params) {
            int count = 0;
//			Cluster cluster = TableCluster.getCluster1(customerCode);
//			if(cluster!=null){
//			
//				try {
//					ResSaveCluster response = (ResSaveCluster) ElahNetHelper.executeRequestResponse(
//							new ReqSaveCluster(cluster), ResSaveCluster.class);
//					TableCluster.udpateClusterStatus(customerCode, response.getStatus());
//					
//				} catch (ClientProtocolException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//					
//				
//			}


            try {
                publishProgress(SENDING_CURRENT_SURVEY);
                TableSurvey.updateSurveySentDate(surveyId, Util.getCurrentDateTime(), clusterValue, clusterValue2, clusterValue3, clusterStatus);

                JSONObject currentSurvey = TableSurvey.getSurveyAsJSON(surveyId, true);
                JSONObject currentSurveyJSON = null;
                JSONObject response = null;

                if (currentSurvey != null) {

                    if (isJSONArrayEmpty(currentSurvey, TableSurveyPromotions.KEY_JSON_ARRAYNAME)
                            && isJSONArrayEmpty(currentSurvey, TableSurveyAssortimento.KEY_JSON_ARRAYNAME)
                            && isJSONArrayEmpty(currentSurvey, TableSurveyCompetitor.KEY_JSON_ARRAYNAME)) {
                        return SURVEY_EMPTY;
                    }

                    currentSurveyJSON = getCompleteSurvey(currentSurvey, count);
                    if (Consts.DEBUGGABLE)


                    for(int i = 0; i <= currentSurvey.toString().length() / 2000; i++) {
                        int start = i * 2000;
                        int end = (i+1) * 2000;
                        end = end > currentSurvey.toString().length() ? currentSurvey.toString().length() : end;
                        Log.v(TAG, currentSurvey.toString().substring(start, end));
                    }

                     //   Log.d(TAG, "Sending information - " + currentSurvey.toString());
                    Log.d(TAG, "Surv url - " + Consts.SERVER_URL + "/" + "SurveySave");
                    response = ElahHttpClient.getJSONPost(Consts.SERVER_URL + "/" + "SurveySave", getPostData(currentSurveyJSON, Util.getCurrentDateTime()));

                    if (response.get(KEY_RESPONSE).equals(RESPONSE_SUCCESS)) {
                        Survey survey = TableSurvey.getSurvey(surveyId);

                        TableSurveyCustomers.create(Util.convertToCustomer(survey));
                        TableSurvey.delete(survey);
                        count++;

                    } else {
                        Log.e(TAG, "Survey Failure " + response.toString());

                        if (!BuildConfig.DEBUG) {
                       /*nwwd     Crashlytics.log(Log.ERROR, FragmentChiusuraAttivita.class.getName()
                                    , "User Name is " + userName + "\n" + "The survey send is " + currentSurveyJSON.toString(4) + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);
                     */


                            FirebaseCrashlytics.getInstance().log(FragmentChiusuraAttivita.class.getName() + ": " +
                                    "User Name is " + userName + "\n" + "The survey send is " + currentSurveyJSON.toString(4) + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);


                        }
                        return CURRENT_SURVEY_SEND_FAILED;

                    }


                }
                publishProgress(CURRENT_SURVEY_SUCCESS);


                ArrayList<Survey> list = TableSurvey.getToBeSentSurveys();
                publishProgress(SENDING_TO_BE_SENT);
                for (Survey survey : list) {
                    JSONObject toBeSentSurvey = null;
                    switch (survey.getFlag()) {
                        case TableSurvey.FLAG_TO_BE_SENT:
                            toBeSentSurvey = TableSurvey.getSurveyAsJSON(survey.getId(), true);
                            break;
                        case TableSurvey.FLAG_TO_BE_SENT_OLD:
                            toBeSentSurvey = TableSurvey.getSurveyAsJSON(survey.getId(), false);
                            break;
                    }
                    currentSurveyJSON = getCompleteSurvey(toBeSentSurvey, count);
                    response = ElahHttpClient.getJSONPost(Consts.SERVER_URL + "/" + "SurveySave", getPostData(currentSurveyJSON, survey.getSentDateTime()));
                    if (response.get(KEY_RESPONSE).equals(RESPONSE_SUCCESS)) {


                        TableSurveyCustomers.create(Util.convertToCustomer(survey));
                        TableSurvey.delete(survey);
                        count++;
                    } else {
                        if (!BuildConfig.DEBUG) {
                       /*  nwwd   Crashlytics.log(Log.ERROR, FragmentChiusuraAttivita.class.getName()
                                    , "User Name is " + userName + "\n" + "The survey send is " + currentSurveyJSON.toString(4) + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);
                      */


                            FirebaseCrashlytics.getInstance().log(FragmentChiusuraAttivita.class.getName() + ": " +
                                    "User Name is " + userName + "\n" + "The survey send is " + currentSurveyJSON.toString(4) + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);

                        }
                        return INVIA_DATI_SURVEY_FAILED;
                    }

                }
                publishProgress(TO_BE_SENT_SUCCESS);


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                if (!BuildConfig.DEBUG) {
               /* nwwd    Crashlytics.log(Log.ERROR, FragmentChiusuraAttivita.class.getName()
                            , "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);
                */


                    FirebaseCrashlytics.getInstance().log(FragmentChiusuraAttivita.class.getName() + ": " +
                            "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);

                    FirebaseCrashlytics.getInstance().recordException(e);


                }

                if (count > 0)
                    return INVIA_DATI_SURVEY_FAILED;
                else
                    return CURRENT_SURVEY_SEND_FAILED;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                if (!BuildConfig.DEBUG) {
               /* nwwd    Crashlytics.log(Log.ERROR, FragmentChiusuraAttivita.class.getName()
                            , "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);
             */

                    FirebaseCrashlytics.getInstance().log(FragmentChiusuraAttivita.class.getName() + ": " +
                            "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);

                    FirebaseCrashlytics.getInstance().recordException(e);


                }

                if (count > 0)
                    return INVIA_DATI_SURVEY_FAILED;
                else
                    return CURRENT_SURVEY_SEND_FAILED;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                if (!BuildConfig.DEBUG) {
              /*nwwd      Crashlytics.log(Log.ERROR, FragmentChiusuraAttivita.class.getName()
                            , "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);
              */

                    FirebaseCrashlytics.getInstance().log(FragmentChiusuraAttivita.class.getName() + ": " +
                            "User Name is " + userName + "\n" + "Error is " + e.getMessage() + "\nTime is " + getCurrentDateAndTime() + "\nAndroid version is " + Build.VERSION.RELEASE);

                    FirebaseCrashlytics.getInstance().recordException(e);


                }
                if (count > 0)
                    return INVIA_DATI_SURVEY_FAILED;
                else
                    return CURRENT_SURVEY_SEND_FAILED;
            }
            return SURVEY_SEND_SUCCESS_ONLINE;
        }


        private JSONObject getCompleteSurvey(JSONObject survey, int page) throws JSONException {

            JSONObject object = new JSONObject();
            object.put(Survey.KEY_JSON_ARRAY_NAME, survey);
            object.put(KEY_PAGE_NO, page);
            return object;
        }

        private boolean isJSONArrayEmpty(JSONObject object, String arrayKey) throws JSONException {
            if (object.getJSONArray(arrayKey).length() == 0)
                return true;
            else
                return false;
        }

        private List<NameValuePair> getPostData(JSONObject json, String sentTime) {
            return getPostData(json.toString(), sentTime);
        }

        private List<NameValuePair> getPostData(String json, String sentTime) {
            ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

            BasicNameValuePair request = new BasicNameValuePair(KEY_REQ_FOR, "survey");
            BasicNameValuePair syncdata = new BasicNameValuePair(KEY_POST_JSON, json);
            BasicNameValuePair user = new BasicNameValuePair(KEY_USER_NAME, userName);
            BasicNameValuePair pwd = new BasicNameValuePair(KEY_PASSWORD, passWord);
            BasicNameValuePair version = new BasicNameValuePair(KEY_VERSION_ID, versionId);
            BasicNameValuePair ct = new BasicNameValuePair(KEY_CURRENT_TIME, sentTime);

            list.add(request);
            list.add(syncdata);
            list.add(user);
            list.add(pwd);
            list.add(version);
            list.add(ct);

            return list;
        }

    }

    // ===========================================================
    // Interfaces
    // ===========================================================

    public interface OnSurveySentListener {
        public void onSurveySent(int status);
    }


    @SuppressLint("SimpleDateFormat")
    private String getCurrentDateAndTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyyHH:mm:ss:SSS
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }


    private class GenerateDashboardTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            scrollDashBoard.setVisibility(View.GONE);
            mDashboardProgress = new ElahProgress(getActivity());
            mDashboardProgress.setCanceledOnTouchOutside(false);
            mDashboardProgress.setCancelable(false);
            if (mDashboardProgress != null) {
                mDashboardProgress.show(R.string.please_wait);
            }
            super.onPreExecute();
        }

       /* protected void onPostExecute() {



        }*/

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RV_dashboard.setAdapter(dashboardAdapter);
            setListViewHeightBasedOnChildren(RV_dashboard);
            scrollDashBoard.setVisibility(View.VISIBLE);
            mDashboardProgress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            generateDashBoard();


            return null;
        }
    }


    @Override
    public void onDetach() {
        if (mDashboardProgress != null && mDashboardProgress.isShowing())
            mDashboardProgress.dismiss();
        super.onDetach();
    }


}
