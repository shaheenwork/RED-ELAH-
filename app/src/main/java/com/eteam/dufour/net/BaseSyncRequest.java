package com.eteam.dufour.net;

import android.text.TextUtils;

public abstract class BaseSyncRequest extends BaseRequest{
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	private String methodName;
	// ===========================================================
	// Constructors
	// ===========================================================
	public BaseSyncRequest(String methodName) {
		// TODO Auto-generated constructor stub
		this.methodName = methodName;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getMethodName() {
		return methodName;
	}
	
	public boolean isToBeAppendedToUrl(){
		return !TextUtils.isEmpty(methodName);
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
