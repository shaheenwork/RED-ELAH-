package com.eteam.dufour.customview;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eteam.dufour.mobile.R;

public class ElahProgress extends AlertDialog{




	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	private TextView fieldMsg;
	private TextView fieldProgress;
	// ===========================================================
	// Constructors
	// ===========================================================
//	public ElahProgress(Context context) {
//		
//		super(context);
//	}
	
	public ElahProgress(Context context) {
//		super(context);
		super(context,R.style.CustomDialogTheme);
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState); 
		LayoutInflater inflater = LayoutInflater.from(getContext());
	     View view = inflater.inflate(R.layout.progress_sync, null);
	     fieldMsg = (TextView) view.findViewById(R.id.field_msg);
	     fieldProgress = (TextView) view.findViewById(R.id.field_progress);
	     setView(view,0,0,0,0);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	     super.onCreate(savedInstanceState);
	     
	     
	}
	
	
	public void setUpView(){
	}
	
	
	public void setProgress(int value) {
		// TODO Auto-generated method stub
		//super.setProgress(value);
		if(fieldProgress!=null){
			try{
				fieldProgress.setText(String.valueOf(value)+"%");
			}
			catch (Exception e) {
				e.printStackTrace();// TODO: handle exception
			}
		}
	}
    
	
	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		if(fieldMsg!=null){
			try{
				fieldMsg.setText(message);
			}
			catch (Exception e) {
				e.printStackTrace();// TODO: handle exception
			}
		}
	}
	
	public void setMessage(int msgId){
		setMessage(getContext().getResources().getString(msgId));
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	
	public void show(int msgId) {
		// TODO Auto-generated method stub
		show();
		setMessage(msgId);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
