package com.eteam.dufour.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.eteam.dufour.database.tables.TableSurvey;
import com.eteam.model.ClusterModel;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

import java.util.ArrayList;

public class ClusterDialogActivity extends Activity implements OnClickListener {
    // ===========================================================
    // Constants
    // ===========================================================

    public final static String INTENT_SURVEY_ID = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_SURVEY_ID";
    public final static String INTENT_CLUSTER_VALUE = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE";
    public final static String INTENT_CLUSTER_VALUE2 = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE2";
    public final static String INTENT_CLUSTER_VALUE3 = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE3";
    public final static String INTENT_CLUSTER_VALUE_STATUS = "com.eteam.dufour.mobile.SurveySaveActivity.INTENT_CLUSTER_VALUE_STATUS";

    // ===========================================================
    // Fields
    // ===========================================================

    private long surveyId;
    private int clustStat;

    private LinearLayout main;


    private ImageView dummyView;

    private Spinner clusterCiaoccolato, clusterCaramelle, clusterDessert;
    public int clCiaoccolatoPosition = Consts.DEFAULT_CLUSTER_VALUE;
    public int clCaramellePosition =  Consts.DEFAULT_CLUSTER_VALUE;
    public int clDessertPosition =  Consts.DEFAULT_CLUSTER_VALUE;

    public int clustChangeValue =  Consts.DEFAULT_CLUSTER_VALUE;

    private Toast mToast;
    private ArrayList<ClusterModel> clusterList;


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
    public void onBackPressed() {
        goBackToSurveyActivity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clusterdialog);

        main = (LinearLayout) findViewById(R.id.main);
        main.setOnClickListener(this);

        surveyId = getIntent().getLongExtra(SurveySaveActivity.INTENT_SURVEY_ID, -1);
        clCiaoccolatoPosition = getIntent().getIntExtra(SurveySaveActivity.INTENT_CLUSTER_VALUE,  Consts.DEFAULT_CLUSTER_VALUE);
        clCaramellePosition = getIntent().getIntExtra(SurveySaveActivity.INTENT_CLUSTER_VALUE2,  Consts.DEFAULT_CLUSTER_VALUE);
        clDessertPosition = getIntent().getIntExtra(SurveySaveActivity.INTENT_CLUSTER_VALUE3,  Consts.DEFAULT_CLUSTER_VALUE);


        clusterCaramelle = (Spinner) findViewById(R.id.cl_caramelle);
        clusterCiaoccolato = (Spinner) findViewById(R.id.cl_ciaoccolato);
        clusterDessert = (Spinner) findViewById(R.id.cl_dessert);
        clusterList = new ArrayList<>();

        clusterList.add(new ClusterModel("A",1));
        clusterList.add(new ClusterModel("B",2));
        clusterList.add(new ClusterModel("C",3));
        clusterList.add(new ClusterModel("D",4));
        clusterList.add(new ClusterModel("E",5));
        clusterList.add(new ClusterModel("F",6));
        clusterList.add(new ClusterModel("NA",0));
        clusterList.add(new ClusterModel("NC",9));


        mToast = Toast.makeText(this, "Sample", Toast.LENGTH_SHORT);
       // Util.setUpToastBOTTOM(mToast);


        ArrayAdapter<ClusterModel> clusterAdapter = new ArrayAdapter<ClusterModel>(getApplicationContext(), R.layout.simple_spinner_item2, clusterList);
        clusterAdapter.setDropDownViewResource(R.layout.simple_spinner_item_cluster);

        clusterCiaoccolato.setAdapter(clusterAdapter);
        clusterDessert.setAdapter(clusterAdapter);
        clusterCaramelle.setAdapter(clusterAdapter);
        clusterDessert.setSelection(getclusterListIndex(clDessertPosition));
        clusterCaramelle.setSelection(getclusterListIndex(clCaramellePosition));
        clusterCiaoccolato.setSelection(getclusterListIndex(clCiaoccolatoPosition));
        clustChangeValue = 0;
        clusterCiaoccolato.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                clCiaoccolatoPosition = clusterList.get(position).getId();
                clusterCiaoccolato.setSelection(position);
				/*if (position != clusterList.indexOf(TableSurvey.getCluster1(surveyId))) {
					clustChangeValue = 1;
				}*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        clusterCaramelle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                clCaramellePosition = clusterList.get(position).getId();

                clusterCaramelle.setSelection(position);
				/*if (position != clusterList.indexOf(TableSurvey.getCluster1(surveyId))) {
					clustChangeValue = 1;
				}*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        clusterDessert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                clDessertPosition = clusterList.get(position).getId();
                clusterDessert.setSelection(position);
				/*if (position != clusterList.indexOf(TableSurvey.getCluster1(surveyId))) {
					clustChangeValue = 1;
				}*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        getIntentAndSetData(savedInstanceState);

				
		/*getIntentAndSetData(savedInstanceState);
		
		btnAnnula = findViewById(R.id.btn_cancel);
		btnEliminaBozza = findViewById(R.id.btn_elimina_bozza);
		btnSalva = findViewById(R.id.btn_save_drafts);
		dummyView =(ImageView) findViewById(R.id.dummyview);
		
		btnAnnula.setOnClickListener(this);
		btnEliminaBozza.setOnClickListener(this);
		btnSalva.setOnClickListener(this);
		dummyView.setOnClickListener(this);*/
        dummyView = (ImageView) findViewById(R.id.dummyview);
        dummyView.setOnClickListener(this);

    }

    private int getclusterListIndex(int value) {
        for (int i = 0; i<clusterList.size();i++){
            if (clusterList.get(i).getId()==value){
                return i;
            }
        }
        return Consts.DEFAULT_CLUSTER_VALUE;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(INTENT_SURVEY_ID, surveyId);
        outState.putInt(INTENT_CLUSTER_VALUE, clCiaoccolatoPosition);
        outState.putInt(INTENT_CLUSTER_VALUE2, clCaramellePosition);
        outState.putInt(INTENT_CLUSTER_VALUE3, clDessertPosition);
        outState.putInt(INTENT_CLUSTER_VALUE_STATUS, clustStat);
        super.onSaveInstanceState(outState);

    }


    public void onClick(View v) {
        if (v == dummyView) {

            goBackToSurveyActivity();

        }

    }

    private void goBackToSurveyActivity() {
        // if (clusterFilledCorrectly()) {


        Intent resultIntent = new Intent();
        resultIntent.putExtra(INTENT_CLUSTER_VALUE, clCiaoccolatoPosition);
        resultIntent.putExtra(INTENT_CLUSTER_VALUE2, clCaramellePosition);
        resultIntent.putExtra(INTENT_CLUSTER_VALUE3, clDessertPosition);
        resultIntent.putExtra(INTENT_CLUSTER_VALUE3, clDessertPosition);
        resultIntent.putExtra(INTENT_CLUSTER_VALUE_STATUS, decideSurveyStatus());


        setResult(Activity.RESULT_OK, resultIntent);
        finish();
      /*  } else {
            Util.showToast(mToast, R.string.msg_fill_all_or_empty_all);
        }*/
    }

    private int decideSurveyStatus() {

        if (clCiaoccolatoPosition != TableSurvey.getCluster1(surveyId)) {
            return 1;
        } else if (clCaramellePosition != TableSurvey.getCluster2(surveyId)) {
            return 1;
        } else if (clDessertPosition != TableSurvey.getCluster3(surveyId)) {
            return 1;
        } else {
            return 0;
        }

    }

    private boolean clusterFilledCorrectly() {
        return clCiaoccolatoPosition == 0 && clCaramellePosition == 0 && clDessertPosition == 0 || clCiaoccolatoPosition != 0 && clCaramellePosition != 0 && clDessertPosition != 0;

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void getIntentAndSetData(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (savedInstanceState != null) {
            surveyId = savedInstanceState.getLong(INTENT_SURVEY_ID);
            clCiaoccolatoPosition = savedInstanceState.getInt(INTENT_CLUSTER_VALUE);
            clCiaoccolatoPosition = savedInstanceState.getInt(INTENT_CLUSTER_VALUE2);
            clDessertPosition = savedInstanceState.getInt(INTENT_CLUSTER_VALUE3);
            clustStat = savedInstanceState.getInt(INTENT_CLUSTER_VALUE_STATUS);
        } else {
            surveyId = getIntent().getLongExtra(INTENT_SURVEY_ID, -1);
            clCiaoccolatoPosition = getIntent().getIntExtra(INTENT_CLUSTER_VALUE,  Consts.DEFAULT_CLUSTER_VALUE);
            clCaramellePosition = getIntent().getIntExtra(INTENT_CLUSTER_VALUE2,  Consts.DEFAULT_CLUSTER_VALUE);
            clDessertPosition = getIntent().getIntExtra(INTENT_CLUSTER_VALUE3,  Consts.DEFAULT_CLUSTER_VALUE);
            clustStat = getIntent().getIntExtra(INTENT_CLUSTER_VALUE_STATUS, 0);
        }
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
