package com.eteam.dufour.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.dufour.database.tables.TableSurveyAssortimento.SurveyAssortimento;
import com.eteam.dufour.viewmodel.ModelSurveyAssortimento;

import androidx.fragment.app.Fragment;

public class ConfirmCopyActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String EXTRA_ITEM = "EXTRA_ITEM";
	private static final String EXTRA_COUNT = "EXTRA_COUNT";
	private static final String EXTRA_SURVEY = "EXTRA_SURVEY";
	
	// ===========================================================
	// Fields
	// ===========================================================
	private TextView mFieldInfo;
	private View 	 mBtnAnnulla;
	private View 	 mBtnProsegui;
	
	private Item 	 			item;
	private SurveyAssortimento 	survey;
	private int		 			count;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	
	
	public static final void startActivity(Fragment activity, int requestCode,
										   int count, ModelSurveyAssortimento model){
					
		Intent intent = new Intent(activity.getActivity(),ConfirmCopyActivity.class);
		intent.putExtra(ConfirmCopyActivity.EXTRA_COUNT, count);
		intent.putExtra(ConfirmCopyActivity.EXTRA_ITEM, model.getItem());
		intent.putExtra(ConfirmCopyActivity.EXTRA_SURVEY, model.getSurvey());
		
		activity.startActivityForResult(intent,requestCode);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.copyconfirm);
		getIntentAndSetData(savedInstanceState);
		mFieldInfo = (TextView) findViewById(R.id.field_msg);
		mBtnAnnulla  = findViewById(R.id.btn_annulla);
		mBtnProsegui = findViewById(R.id.btn_prosegui);
		
		mFieldInfo.setText(getString(R.string.msg_copy_starting)+" "+item.getCode()+" "+item.getDescription()+" "
				+getString(R.string.msg_copy_middle)+" "+count
						+" "+getString(R.string.msg_copy_ending));
		mBtnAnnulla.setOnClickListener(this);
		mBtnProsegui.setOnClickListener(this);
	}
	


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(EXTRA_SURVEY, survey);
		outState.putParcelable(EXTRA_ITEM, item);
		outState.putInt(EXTRA_COUNT, count);
		super.onSaveInstanceState(outState);

	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mBtnAnnulla){
			finish();
		}
		else if(v==mBtnProsegui){
			Intent data = new Intent();
			data.putExtra(EXTRA_SURVEY,survey);
			setResult(RESULT_OK, data);
			finish();
		}
	}
	
	public static SurveyAssortimento getSurvey(Intent data){
		return data.getParcelableExtra(EXTRA_SURVEY);
	}
	// ===========================================================
	// Methods
	// ===========================================================
	private void getIntentAndSetData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			item	  = savedInstanceState.getParcelable(EXTRA_ITEM);
			survey 	  = savedInstanceState.getParcelable(EXTRA_SURVEY);
			count 	  = savedInstanceState.getInt(EXTRA_COUNT);
		}
		else{
			item	  = getIntent().getParcelableExtra(EXTRA_ITEM);
			survey	  = getIntent().getParcelableExtra(EXTRA_SURVEY);
			count 	  = getIntent().getIntExtra(EXTRA_COUNT, -1);
		}
	}
	

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
