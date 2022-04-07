package com.eteam.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Application;
import android.util.Log;


public class HttpClientHelper {
	
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int HTTP_TIMEOUT = 10*1000;
	private static final boolean DEBUGGABLE = true;
	
	private static final String CONTENT_TYPE_APPLICATION 		 = "application/json";
	private static final String CONTENT_TYPE_MULTIPART_FORM_DATA = "application/x-www-form-urlencoded";
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

	   public static JSONObject executeHttpGetAsJSON(String url)
	    		throws ClientProtocolException, URISyntaxException
	    		, IOException, JSONException,IllegalStateException{
	    	String response = executeHttpGet(url);
	    	
	    	if(response.startsWith("(")&&response.endsWith("")){
	        	response =  response.substring(1).replace(");","");
	         }
	    	
	    	JSONObject object = new JSONObject(response);
	    	
	    	return object;
	    }
	    
	    
	    public static String executeHttpGet(String url) 
	    		throws URISyntaxException, ClientProtocolException, IOException  {
	    	
	    	BufferedReader bf = null;
	        try {
	        	if(DEBUGGABLE)
	        		Log.d("Log", "Sending GET request : "+url);
		        HttpURLConnection urlConnection = (HttpURLConnection)new URL(url)
		                .openConnection();
		        urlConnection.setUseCaches(false);
		        urlConnection.setConnectTimeout(HTTP_TIMEOUT);
		        urlConnection.setReadTimeout(HTTP_TIMEOUT);
		        urlConnection.addRequestProperty("Cache-Control", "no-cache");
//		        urlConnection.setDoOutput(true);
		        urlConnection.setDoInput(true);
		        
		        InputStream in = urlConnection.getInputStream();
		
		        InputStreamReader isw = new InputStreamReader(in);
		        bf = new BufferedReader(isw);
		        StringBuffer sb = new StringBuffer("");
		        String line = "";
		        String NL = System.getProperty("line.separator");
		        while ((line = bf.readLine()) != null) {
		          sb.append(line + NL);
		        }
		        in.close();
		        
		        if(DEBUGGABLE)
		        	Log.d("Log", "From URL : "+url+" response is : "+sb.toString());   
		        
		        return sb.toString();
	        } finally {
		        if (bf != null) {
		        try {
		           bf.close();
		        } catch (IOException e) {
		           e.printStackTrace();
		        }
	        }
	      }
	    	
	   }
	 
	    
	    public static String executeHttpPost(String strURL,List<NameValuePair> nameValuePairs) 
	    		throws ClientProtocolException, IOException{
	    	return executeHttpPost(strURL, getQuery(nameValuePairs),CONTENT_TYPE_MULTIPART_FORM_DATA);
	    }
	    
	    public static String executeHttpPost(String url,JSONObject content) 
	    		throws ClientProtocolException, IOException{
	    	return executeHttpPost(url, content.toString(),CONTENT_TYPE_APPLICATION);
	    }
	    
	    public static String executeHttpPost(String url,String content,String contentType) 
	    		throws ClientProtocolException, IOException{
	    	
	    	if(DEBUGGABLE)
	    		Log.d("Log", "Sending POST request : "+url);
	    	
	    	
	    	HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
	    	conn.setUseCaches(false);
	    	conn.setReadTimeout(HTTP_TIMEOUT);
	    	conn.setConnectTimeout(HTTP_TIMEOUT);
	    	conn.setRequestMethod("POST");
	    	conn.setRequestProperty("Content-Type", contentType); 
	    	conn.addRequestProperty("Cache-Control", "no-cache");
	    	conn.setDoInput(true);
	    	conn.setDoOutput(true);
	    	OutputStream os = conn.getOutputStream();
	    	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
	    	Log.d("Content JSON", content);
	    	writer.write(content);
	    	writer.close();
	    	os.close();
	    	conn.connect();
	    	
	    	String response= "";

	    	//start listening to the stream
	    	Scanner inStream = new Scanner(conn.getInputStream());

	    	//process the stream and store it in StringBuilder
	    	while(inStream.hasNextLine())
	    		response+=(inStream.nextLine());
	    	
	    	conn.disconnect();
	    	inStream.close();
	    	
	    	 if(DEBUGGABLE)
		        	Log.d("Log", "From URL : "+url+" response is : "+response);   
	    	
	        return response;
	    }
	    
	    public static InputStream getPostResponseAsStream(String strURL,String content) 
	    		throws ClientProtocolException, IOException{
	    	URL url = new URL(strURL);
	    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	conn.setUseCaches(false);
	    	conn.setReadTimeout(HTTP_TIMEOUT);
	    	conn.setConnectTimeout(HTTP_TIMEOUT);
	    	conn.setRequestMethod("POST");
	    	conn.setRequestProperty("Content-Type", "application/json"); 
	    	conn.addRequestProperty("Cache-Control", "no-cache");
	    	
	    	conn.setDoInput(true);
	    	conn.setDoOutput(true);
	    	OutputStream os = conn.getOutputStream();
	    	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
	    	writer.write(content);
	    	writer.close();
	    	os.close();
	    	conn.connect();
	    	

	    	return conn.getInputStream();
	    }
	    
	    public static HttpURLConnection getHttpUrlConnection(String url,String reqMethod) 
	    		throws ClientProtocolException, IOException{
	    	
	    	HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	    	conn.setUseCaches(false);
	    	conn.setReadTimeout(HTTP_TIMEOUT);
	    	conn.setConnectTimeout(HTTP_TIMEOUT);
	    	conn.setRequestMethod(reqMethod);
	    	
	    	conn.setDoInput(true);
	    	conn.setDoOutput(false);
	    
	    	

	    	return conn;
	    }
	    
	    public static int getFileSize(String... fileUrlPath) 
	    		throws IOException{
	    	int length = 0;
	    	for(String file:fileUrlPath){
	    		length += getFileSize(file);
	    	}
	    	return length;
	    }
	    
	    
	    public static float getFileSize(String fileUrlPath) 
	    		throws IOException{
	    	URL url = new URL(fileUrlPath);
	    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	conn.setUseCaches(false);
	    	float length = 0;
	    	try{
	    		final String contentLengthStr=conn.getHeaderField("content-length");
	    		length = Float.parseFloat(contentLengthStr);
	    	}
	    	catch(NumberFormatException e){
	    		
	    	}
	    	conn.disconnect();
	    	return length;
	    }
	    

	    public static JSONObject getJSONPost(String url,List<NameValuePair> nameValuePairs) 
	    		throws ClientProtocolException, JSONException, IOException{
	    	String response = executeHttpPost(url, nameValuePairs);
	    	return new JSONObject(response);
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
	            result.append(URLEncoder.encode(pair.getName(),"ISO-8859-1"));
	            result.append("=");
	            result.append(URLEncoder.encode(pair.getValue(),"ISO-8859-1"));
	        }
	        return result.toString();
	    }
	    
	    /**
	     * Enable cookie service for HTTP URL connection.
	     * <p>Note: While using HttpClient if sessions are to be saved
	     * use this function in {@link Application} onCreate</p>
	     * */
	    public static final void enableCookie(){
			CookieManager cookieManager = new CookieManager();  
		    CookieHandler.setDefault(cookieManager);
	    }
	    
	    public static void parseXML(String url,DefaultHandler handler)
	    		throws ClientProtocolException, ParserConfigurationException
	    		, SAXException, IOException{
	    	HttpURLConnection connection = getHttpUrlConnection(url,HttpGet.METHOD_NAME);
	    	connection.setUseCaches(false);
	    	
	    	connection.connect();
	    	InputStream is = connection.getInputStream();
	    	parseXML(is, handler);
	    	is.close();
	    	connection.disconnect();
	    }
	    
	    public static void parseXML(InputStream is,DefaultHandler handler) 
	    		throws ParserConfigurationException, SAXException, IOException{
	    	parseXML(new InputSource(is), handler);
	    }
	    
	    
	    public static void parseXML(InputSource source,DefaultHandler handler) 
				throws ParserConfigurationException, SAXException, IOException{
			SAXParserFactory factory=SAXParserFactory.newInstance();
	        SAXParser sp=factory.newSAXParser();
	        XMLReader reader=sp.getXMLReader();
	        reader.setContentHandler(handler);
	        reader.parse(source);
		}
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

    
 
    
 

}    
    