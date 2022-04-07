package com.eteam.dufour.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AlertMsgActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String INTENT_BTN_TEXT_ID = "com.eteam.dufour.mobile.INTENT_BTN_TXT_ID";
	public static final String INTENT_MSG_TEXT_ID = "com.eteam.dufour.mobile.INTENT_MSG_TXT_ID";
	// ===========================================================
	// Fields
	// ===========================================================
	private Button mBtnChiudi;
	private TextView mTextView;
	private int msgId;
	private int btnLabelId;
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
		setContentView(R.layout.nonet);
		restoreInstance(savedInstanceState);
		
		mTextView = (TextView) findViewById(R.id.field_msg);
		mTextView.setText(msgId);
		
		mBtnChiudi = (Button) findViewById(R.id.btn_chiudi);
		mBtnChiudi.setText(btnLabelId);
		mBtnChiudi.setOnClickListener(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt(INTENT_MSG_TEXT_ID, msgId);
		outState.putInt(INTENT_BTN_TEXT_ID, btnLabelId);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mBtnChiudi){
			setResult(RESULT_OK);
			finish();
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void restoreInstance(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			msgId = savedInstanceState.getInt(INTENT_MSG_TEXT_ID);
			btnLabelId = savedInstanceState.getInt(INTENT_BTN_TEXT_ID);
		}
		else{
			msgId = getIntent().getIntExtra(INTENT_MSG_TEXT_ID, -1);
			btnLabelId = getIntent().getIntExtra(INTENT_BTN_TEXT_ID, -1);
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================




}
