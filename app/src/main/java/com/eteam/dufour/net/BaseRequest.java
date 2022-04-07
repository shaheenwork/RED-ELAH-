package com.eteam.dufour.net;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class  BaseRequest {
	
	
	public BaseRequest() {
	}
	
	public abstract JSONObject createRequest() throws JSONException;
}
