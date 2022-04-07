package com.eteam.utils.downloader;

import com.eteam.utils.ElahHttpClient;

import android.os.AsyncTask;

public class GetResponseTask extends AsyncTask<String, Integer, String>{
	protected static final int CONNECTING_SERVER = -100;
	protected static final int RECEIVING_DATA = -101;
	protected static final int WAITING_RESPONSE = -102;
	protected static final int RESPONSE_FAILED = -103;
	
	@Override
	protected String doInBackground(String... urls) {
		// TODO Auto-generated method stub
		try {
			return ElahHttpClient.executeGet(urls[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			publishProgress(RESPONSE_FAILED);
			e.printStackTrace();
		}
		return null;
	}

	
}
