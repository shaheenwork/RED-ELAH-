package com.eteam.utils;

import android.text.InputType;
import android.text.method.NumberKeyListener;

public class ElahNumListener extends NumberKeyListener{

	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public int getInputType() {
		   return InputType.TYPE_CLASS_NUMBER 
		          | InputType.TYPE_NUMBER_FLAG_DECIMAL 
		          | InputType.TYPE_NUMBER_FLAG_SIGNED;
		
	}

	@Override
	protected char[] getAcceptedChars() {
		// TODO Auto-generated method stub
		return new char[]{'1','2','3','4','5','6','7','8','9','0',','};
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
