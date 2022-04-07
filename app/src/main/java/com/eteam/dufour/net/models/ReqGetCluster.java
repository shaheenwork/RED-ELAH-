package com.eteam.dufour.net.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.eteam.dufour.net.BaseRequest;

public class ReqGetCluster extends BaseRequest{
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	private String customerCode;
	// ===========================================================
	// Constructors
	// ===========================================================
	public ReqGetCluster(String customerCode) {
		// TODO Auto-generated constructor stub
		this.customerCode = customerCode;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public JSONObject createRequest() 
			throws JSONException {
		JSONObject request = new JSONObject();
		request.put("cust_code", customerCode);
		return request;
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
