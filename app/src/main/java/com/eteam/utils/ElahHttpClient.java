package com.eteam.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


public class ElahHttpClient {

	private static final String TAG = ElahHttpClient.class.getName();

	public static final String SERVER_ERROR = "-error";

	private static final int HTTP_TIMEOUT = 2*60*1000;
	private static final String ENCODING = "UTF-8";

    private static final boolean DEBUGGABLE = false;


    /*****
     * Run {@link HttpGet} method on the given url 
     * and return the response
     * @param url URL in which the get is to be executed
     * @return response as {@link JSONObject}
     * *****/
    public static JSONObject executeGetAsJSON(String url)
    		throws ClientProtocolException,
    		URISyntaxException, IOException,
    		JSONException,IllegalStateException{

    	System.out.println("Url : "+url);

    	String response = executeGet(url);

    	if(response.startsWith("(")&&response.endsWith("")){
        	response =  response.substring(1).replace(");","");
         }

    	JSONObject object = new JSONObject(response);

    	return object;


    }

    /*****
     * Run {@link HttpGet} method on the given url 
     * and return the response
     * @param url URL in which the get is to be executed
     * @return response as string
     * *****/
    public static String executeGet(String url)
    		throws URISyntaxException, ClientProtocolException, IOException  {

    	BufferedReader bf = null;
    	String response = null;
    	HttpURLConnection urlConnection = null;
    	InputStreamReader isw = null;

        try {

	        urlConnection = (HttpURLConnection)new URL(url)
	                .openConnection();
	        urlConnection.setConnectTimeout(HTTP_TIMEOUT);
	        urlConnection.setReadTimeout(HTTP_TIMEOUT);
	        urlConnection.setDoInput(true);
	        urlConnection.setRequestMethod(HttpGet.METHOD_NAME);
	        urlConnection.setChunkedStreamingMode(0);
	        if (Build.VERSION.SDK_INT > 13) {
	        	urlConnection.setRequestProperty("http.KeepAlive", "false");
	        }
	        urlConnection.setRequestProperty("http.maxConnections", "200");
	        urlConnection.setRequestProperty("Accept-Encoding", "identity");
	        urlConnection.connect();
	        InputStream in = urlConnection.getInputStream();

	        isw = new InputStreamReader(in,ENCODING);
	        bf = new BufferedReader(isw);
	        StringBuffer sb = new StringBuffer("");
	        String line = "";

	        String NL = System.getProperty("line.separator");
	        while ((line = bf.readLine()) != null) {
	          sb.append(line + NL);
	        }
	        in.close();

	        if(DEBUGGABLE){
	        	writeToFile(sb.toString());
	        }
	        response = sb.toString();

        }
		finally
		{
		    if (bf != null)
		       bf.close();
		    if(isw!=null)
		    	isw.close();
		    if(urlConnection!=null)
		    	urlConnection.disconnect();
		}
		if(DEBUGGABLE){
			Log.i(TAG, "URL is - "+url);
			Log.i(TAG, "Get response is : "+response);
		}
		return response;

   }



    private static void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(Environment.getExternalStorageDirectory()+File.separator+"elahjson.txt", true));
            outputStreamWriter.write("\n*****************\n"+data+"\n*****************\n");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String sendPost(String strURL,List<NameValuePair> nameValuePairs)
    		throws ClientProtocolException, IOException{
    	HttpURLConnection conn = null;
    	Scanner inStream =null;
    	String response= "";
    	try{
	    	URL url = new URL(strURL);
	    	conn = (HttpURLConnection) url.openConnection();
	    	conn.setReadTimeout(HTTP_TIMEOUT);
	    	conn.setConnectTimeout(HTTP_TIMEOUT);
	    	conn.setRequestMethod("POST");
	    	conn.setDoInput(true);
	    	conn.setDoOutput(true);
	    	conn.setRequestProperty("Accept-Encoding", "identity");
	    	if (Build.VERSION.SDK_INT > 13) {
	    		conn.setRequestProperty("http.KeepAlive", "true");
	        }

	    	OutputStream os = conn.getOutputStream();
	    	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,ENCODING));
	    	writer.write(getQuery(nameValuePairs));
	    	writer.close();
	    	os.close();
	    	conn.connect();

	    	//start listening to the stream
	    	inStream = new Scanner(conn.getInputStream(),ENCODING);

	    	//process the stream and store it in StringBuilder
	    	while(inStream.hasNextLine())
	    		response+=(inStream.nextLine());


    	}
    	finally{
	    	if(conn!=null)
	    		conn.disconnect();
	    	if(inStream!=null)
	    		inStream.close();
    	}

    	if(DEBUGGABLE)
    		Log.d("Log", "Post response is - "+response);
        return response;
    }

    public static JSONObject getJSONPost(String url,List<NameValuePair> nameValuePairs) throws ClientProtocolException, JSONException, IOException{
    	String jsonSendPost = sendPost(url, nameValuePairs);
    	return new JSONObject(jsonSendPost);
    }


    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair.getName(),ENCODING));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(),ENCODING));
        }
        return result.toString();
    }

    /**
     * Enable cookie service for HTTP URL connection.
     * Note: While using Elah HTTPClient if sessions are to be saved
     * use this function
     * */
    public static final void enableCookie(){
		CookieManager cookieManager = new CookieManager();
	    CookieHandler.setDefault(cookieManager);
    }

    public static void enableHttpResponseCache(Context context) {
        try {
	        long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
	        File httpCacheDir = new File(context.getCacheDir(), "http");
	        Class.forName("android.net.http.HttpResponseCache")
	            .getMethod("install", File.class, long.class)
	            .invoke(null, httpCacheDir, httpCacheSize);
        }
        catch (Exception e) {

        }
    }

	public static void disableConnectionReuseIfNecessary() {
	        System.setProperty("http.keepAlive", "false");
	}

}    
    