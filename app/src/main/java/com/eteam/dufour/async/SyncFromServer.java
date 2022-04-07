package com.eteam.dufour.async;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.eteam.dufour.database.ElahDBHelper;
import com.eteam.dufour.database.tables.TableList;
import com.eteam.dufour.database.tables.TableLogin;
import com.eteam.dufour.database.tables.TableSyncInfo;
import com.eteam.dufour.database.tables.TableSyncInfo.Session;
import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;

public class SyncFromServer extends IntentService{


	// ===========================================================
	// Constants
	// ===========================================================
	private static final String KEY_REQ_FOR = "reqfor";
	private static final String KEY_USER_NAME = "user";
	private static final String KEY_PASSWORD = "pwd";
	private static final String KEY_VERSION_ID = "versionId";
	private static final String KEY_CURRENT_TIME = "ct";
	
	private static final String KEY_SYNC_STATUS = "syncTypeStatus";
	
	private static final String KEY_RESPONSE = "errorcode";
	
	private static final String RESPONSE_SUCCESS = "1";
	private static final String RESPONSE_UPDATE_AVAILABLE = "1";
	
	private static final int SENDING_CURRENT_SURVEY = 4;
	
	public static final int SURVEY_SEND_FAILURE = 101;
	public static final int SURVEY_SEND_SUCCESS = 102;
	public static final int SURVEY_EMPTY = 103;
	
	
	
	public static final int SYNC_FROM_SERVER_SUCCESSFUL = 104;
	public static final int SYNC_FROM_SERVER_FAILED = 105;
	
	private static final int SEND_SERVER_ERROR = 106;
	private static final int RECEIVE_SERVER_ERROR = 107;
	public static final int SESSION_TIMED_OUT = 108; 
	public static final int NO_UPDATE_AVAILABLE = 109;
	
	public static final String ACTION_PROGRESS = "com.eteam.dufour.async.SyncFromServer.ACTION_PROGRESS";
	public static final String ACTION_RESULT = "com.eteam.dufour.async.SyncFromServer.ACTION_RESULT";
	
	public static final String INTENT_RESULT = "com.eteam.dufour.async.SyncFromServer.INTENT_RESULT";
	public static final String INTENT_PROGRESS = "com.eteam.dufour.async.SyncFromServer.INTENT_PROGRESS";
	public static final String INTENT_USER_NAME = "com.eteam.dufour.async.SyncFromServer.INTENT_USER_NAME";
	
	public static final String NAME = "com.eteam.dufour.SyncFromServer";
	// ===========================================================
	// Fields
	// ===========================================================
	private ElahDBHelper dbHelper;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public SyncFromServer() {
		super(NAME);
		// TODO Auto-generated constructor stub
		Log.d("Log", "Creating service at "+NAME);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		dbHelper = ElahDBHelper.getInstance(this);
		Log.d("Log", "Creating service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String userName = intent.getStringExtra(INTENT_USER_NAME);
		String response = null;
		Cursor c1 = null;
		SQLiteDatabase xdb = dbHelper.getWritableDatabase();
		Log.d("Log", "Started");
		try{
	    	   
//		     String url = Consts.SERVER_URL+"/sync?reqfor=tablelist&Starting_date="+getCurrentDate()+"&ct="+getMilliSeconds();     	           	   
//	      	 Log.d("Log", "Url is "+url);
		     response = ElahHttpClient.executeGet(Consts.SERVER_URL+"/sync?reqfor=tablelist&Starting_date="+getCurrentDate()+"&ct="+getMilliSeconds());
//	         Log.d("___","response: "+response.intern());
	         response = response.replace("(", "").replace(");","");
	   	    
	   	     JSONObject obj = new JSONObject(response);
	   	     
	   	     String status = obj.optString(Consts.JSON_KEY_SESSION_TIME_OUT);
	   	     if(status!=null&&status.equals(Consts.JSON_KEY_SESSION_TIME_OUT)){
	   	    	notifyResult(SESSION_TIMED_OUT);
	   	     }	   	     
	   	     
	   	     JSONObject syncObj = obj.getJSONObject("syncdata");
	   	     
	   	     String syncStatus = syncObj.getString(KEY_SYNC_STATUS);
	   	     
	   	     if(syncStatus.equals(RESPONSE_UPDATE_AVAILABLE)){
	   	      	     
//		   	     Log.d("Project","sync data... session = "+session+" date  "+date);
		   	     TableLogin.setSyncStatus(userName, TableLogin.SYNC_INCOMPLETE);
		   	     xdb.execSQL("CREATE TABLE IF NOT EXISTS SYNCDATA (_id INTEGER PRIMARY KEY, session VARCHAR, date VARCHAR)");
		   	     //newDB.execSQL("INSERT INTO SYNCDATA (session, date) VALUES ("+session+","+date+")");
		   	     
		   	     TableSyncInfo.createSession(new Session(syncObj));
		   	       
		   	     JSONArray tableListArray = syncObj.getJSONArray(TableList.TABLE_NAME); 
	//	   	     xdb.beginTransaction();
		   	     TableList.createAndPopulateTableList(tableListArray);
	//	   	     xdb.setTransactionSuccessful();
	   	     }
	   	     else{
	   	    	notifyResult(NO_UPDATE_AVAILABLE);
	   	     }
	     
	       }
	       catch(Exception e){
	    	   e.printStackTrace();
	    	   notifyResult(SYNC_FROM_SERVER_FAILED);
	       }
	       finally{
//	    	   xdb.endTransaction();
	       }
	       try{
	   	     String[] selection = new String[] {TableList.COLUMN_TABLE_ID,TableList.COLUMN_TOTAL_PAGES};
	   	     c1 = dbHelper.getReadableDatabase().query(TableList.TABLE_NAME,selection,null,null,null,null,null);
	   	     dbHelper.getWritableDatabase().beginTransaction();
	   	     long pagenumber = TableList.getTotalPages();
	   	     int tableNumber = 0;
	   	     if( c1.moveToFirst())  {
		        do {
		        	String tablename=c1.getString(c1.getColumnIndex("tableid"));
		            int totalpages=c1.getInt(c1.getColumnIndex("totalpages"));
		            if(ElahDBHelper.doesTableExist(dbHelper, tablename))
		               	ElahDBHelper.dropTable(dbHelper, tablename);
		            SQLiteStatement insert = null;	 
		            for(int q=0; q<totalpages; q++)  {
	            		  String currentDate = getCurrentDate();
	            		  
	            		  response = ElahHttpClient.executeGet(Consts.SERVER_URL+"/sync?reqfor="+tablename+"&pageno="+(q+1)
	            				  +"&Starting_date="+currentDate+"&ct="+getMilliSeconds());
	                        
	                      response = response.replace("(", "").replace(");","");
	                      
	                      JSONObject table = new JSONObject(response);         	     
	                      JSONArray  tableInfoArray = table.getJSONArray(tablename);
	                   	  
	                      for(int i=0;i<tableInfoArray.length();i++){
	                    	  int oldInfoKeyLength = -1;
	                   		  JSONObject tableInfo = tableInfoArray.getJSONObject(i);
	                   		  Iterator<?> iterator;
	            	    	  String  tablelist_keys = "";
	               	    	  String paramts = "";
	               	    	  ArrayList<String> type = new ArrayList<String>();
	                   		  boolean first = true;
	                   		  int index = 1;
	                   		  if(q==0&&i==0||oldInfoKeyLength!=tableInfo.length()){
	                   			  oldInfoKeyLength = tableInfo.length();
	                   			  iterator = tableInfo.keys();
	                   			  while(iterator.hasNext()){
	                   				
		       	    				String key = String.valueOf(iterator.next());
		       	   	          		//Log.d("Project","Key values = "+key);
		       	   	          			if(first) {
		       	   	          				tablelist_keys += key;
		       	   	          				paramts += "?";
		       	   	          				first = false;
		       	   	          				type.add("VARCHAR");
		       	   	          			}  else {
		       	   	          				tablelist_keys += ","+key;
		       	   	          				paramts += ",?"; 
		       	   	          				type.add("VARCHAR");
		       	   	                	     
		       	   	          			}
		       	   	          			
		       	   	          		}
		       	    			 	xdb.execSQL("create table if not exists "+tablename+"("
		      	          					+TableList.COLUMN_ID+" integer primary key autoincrement, "
		      	          					+tablelist_keys+" "+type+")");
		      	    				insert = xdb.compileStatement("INSERT INTO "+ tablename+"("+tablelist_keys+") "  + "VALUES("+paramts+")");
		      	    				
	                   		  }
	                   		  iterator = tableInfo.keys();
	             	    	  while(iterator.hasNext()){
	               	    		String key = String.valueOf(iterator.next());
	               	          	insert.bindString(index, tableInfo.getString(key));
	               	          	index++; 
	               	    	  }
//	             	    	  Log.d("Log", "key_numbers"+index);
	               	    	  insert.executeInsert();
	                   	  }
	                   	  tableNumber++;  	
	                      publishProgress((int)(tableNumber*100/pagenumber));  	 
       		   }
		          	              		              
		         }while(c1.moveToNext());
		         xdb.setTransactionSuccessful();
		     }
	       }
	       catch (Exception e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
				notifyResult(SYNC_FROM_SERVER_FAILED);
	       }      	          
        finally{
     	   
     	   if(xdb!=null){
     		   xdb.endTransaction();
//       		   xdb.close();
     	   }
     	   if(c1!=null)
     		   c1.close();
        }
		notifyResult(SYNC_FROM_SERVER_SUCCESSFUL);
	}
	// ===========================================================
	// Methods
	// ===========================================================
    private String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyyHH:mm:ss:SSS
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    
    private long getMilliSeconds() {
		// TODO Auto-generated method stub
		Date now = new Date();
        return now.getTime();
	}
    
    private void notifyResult(int result){
        Intent i = new Intent(ACTION_RESULT);
        i.putExtra(INTENT_RESULT, result);
        SyncFromServer.this.sendBroadcast(i);
    }
    
    private void publishProgress(int result){
        Intent i = new Intent(ACTION_PROGRESS);
        i.putExtra(INTENT_PROGRESS, result);
        SyncFromServer.this.sendBroadcast(i);
    }
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
