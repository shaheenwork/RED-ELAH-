package com.eteam.dufour.net.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.eteam.dufour.net.BaseResponse;

public class ResSaveCluster extends BaseResponse{

	
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String KEY_STATUS = "status";
	// ===========================================================
	// Fields
	// ===========================================================
	private int status;
	// ===========================================================
	// Constructors
	// ===========================================================
	public ResSaveCluster(JSONObject response) 
			throws JSONException {
		super(response);
		status = response.getInt(KEY_STATUS);
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public int getStatus() {
		return status;
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
