package com.eteam.dufour.customview;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

public class CustomSpinner extends Spinner {
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public CustomSpinner(Context context) {
	    super(context);
	    // TODO Auto-generated constructor stub
	}
	
	public CustomSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomSpinner(Context context, AttributeSet attrs, int defStyle,
			int mode) {
		super(context, attrs, defStyle, mode);
		// TODO Auto-generated constructor stub
	}

	public CustomSpinner(Context context, int mode) {
		super(context, mode);
		// TODO Auto-generated constructor stub
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
		
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	public void setSelectionWithoutFiring(int position){
		ignoreNewSelectionByReflection(position, getItemIdAtPosition(position));
		setSelection(position);
	}

	private void ignoreNewSelectionByReflection(int position,long selectedRowId) {
	    try {
	        Class<?> c = this.getClass().getSuperclass().getSuperclass().getSuperclass();
	        Field oldPosField = c.getDeclaredField("mOldSelectedPosition");
	        oldPosField.setAccessible(true);
	        oldPosField.setInt(this, position);
	        
	        Field oldRowId = c.getDeclaredField("mOldSelectedRowId");
	        oldRowId.setAccessible(true);
	        oldRowId.setLong(this, selectedRowId);
	    } catch (Exception e) {
	        Log.e("Exception", "Db is slow using "+getClass().getName()
	        		+" modify ignoreNewSelection Function",e);
	        // TODO: handle exception
	        
	    }
	}



}