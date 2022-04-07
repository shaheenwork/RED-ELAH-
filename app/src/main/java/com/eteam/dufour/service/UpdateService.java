package com.eteam.dufour.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.util.Log;

import com.eteam.utils.Consts;
import com.eteam.utils.ElahHttpClient;
import com.eteam.utils.downloader.DownloadFile;

public class UpdateService extends IntentService{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "UpdateService";
	private static final String SERVICE_NAME = "elahupdateservice";
	
	private static final String KEY_APK_VERSION = "android_version";
	
	private static final int APK_CORRUPTED = -213;
		
	public static final String ACTION_UPDATION = "com.eteam.dufour.service.UpdateService.ACTION_UPDATION";
	
	public static final String PREF_UPDATE_STATUS = "com.eteam.dufour.mobile.SplashScreen.PREF_UPDATE_STATUS";
	
	public static final int UPDATE_DEFAULT_VALUE = -4;
	public static final int UPDATE_AVAILABLE = -1;
	public static final int UP_TO_DATE = -2;
	public static final int SERVER_ERROR = -3;
	
	
	public static final String INTENT_UPDATE_STATUS = "com.eteam.dufour.mobile.UpdateService.INTENT_UPDATE_STATUS";
	
	private final static boolean SKIP_DOWNLOAD_ON_ERROR = true;
	
	private final static int DEFAULT_REFRESH_HOUR = 2;
	private final static int ERROR_REFRESH_MINUTE = 10;
	private UpdateReceiver updateReceiver;
	// ===========================================================
	// Fields
	// ===========================================================
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public UpdateService() {
		super(SERVICE_NAME);
		// TODO Auto-generated constructor stub
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {

	    registerInstallReceiver();

		Log.d(TAG, "Update service is starting");
		int installedAPKVersion = getInstalledAPKVersion();
		File downloadedAPK = new File(Consts.ELAH_DOWNLOAD_DIRECTORY+File.separator+Consts.ELAH_APK_FILENAME);
		if(downloadedAPK.exists()){
			Log.d(TAG, "dowloadedAPK exists" );
			PackageInfo downloadedAPKInfo = getPackageManager().getPackageArchiveInfo(downloadedAPK.getAbsolutePath(), 0);
			
			if(downloadedAPKInfo!=null){
				int downloadedVersion = downloadedAPKInfo.versionCode;
				if(installedAPKVersion<downloadedVersion){
					switch (getUdpateStatus(downloadedVersion)) {
						case UPDATE_AVAILABLE:
							downloadAndInstall();
							break;
						case UP_TO_DATE:
							sendUpdateStatus(UPDATE_AVAILABLE);
							scheduleService(0, DEFAULT_REFRESH_HOUR, 0);
							break;
						case SERVER_ERROR:
							sendUpdateStatus(UP_TO_DATE);
							scheduleService(0, 0, ERROR_REFRESH_MINUTE);
							break;
	
						case APK_CORRUPTED:
							downloadedAPK.delete();
							downloadAndInstall();
							break;
					}
				}
				else{
					Log.d(TAG, "dowloadedAPK version is lower "+installedAPKVersion+" > "+downloadedVersion );
					downloadedAPK.delete();
					sendUpdateStatus(UP_TO_DATE);
					scheduleService(0, DEFAULT_REFRESH_HOUR, 0);
				}
				
			}
			else{
				downloadedAPK.delete();
				scheduleService(0, 0, ERROR_REFRESH_MINUTE);
			}
			
		}
		else{
			switch (getUdpateStatus(installedAPKVersion)) {
				case UPDATE_AVAILABLE:
					downloadAndInstall();
					break;
				case UP_TO_DATE:
					sendUpdateStatus(UP_TO_DATE);
					scheduleService(0, DEFAULT_REFRESH_HOUR, 0);
					break;
				case SERVER_ERROR:
					sendUpdateStatus(UP_TO_DATE);
					scheduleService(0, 0, ERROR_REFRESH_MINUTE);
					break;
				
				case APK_CORRUPTED:
					break;
				
				default:
					break;
			}
			
		}
		
	}

    private void registerInstallReceiver() {

        try {
            updateReceiver= new UpdateReceiver();
            IntentFilter intentFilter = new IntentFilter(UpdateService.ACTION_UPDATION);
            registerReceiver(updateReceiver,intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // ===========================================================
	// Methods
	// ===========================================================
	
	private int getInstalledAPKVersion() {
		int version =  APK_CORRUPTED;
		try {
			PackageInfo mAppInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = mAppInfo.versionCode;
		} catch (NameNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	
	private int getUdpateStatus(int currentVersion){
		int status = SERVER_ERROR;
		try {
			JSONObject result = ElahHttpClient.executeGetAsJSON(Consts.SERVER_URL+"/SynchronizationVersionServlet?version=0");
			if(result!=null){
					if(currentVersion!=APK_CORRUPTED){
						if(result.optInt(KEY_APK_VERSION)>currentVersion){
							//update button enable;
							status = UPDATE_AVAILABLE;
						}
						else{
							status = UP_TO_DATE;
						}
					}
					else{
						status = APK_CORRUPTED;
					}
					
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	private void downloadAndInstall() {
		if(downloadElahUpdate()){
			sendUpdateStatus(UPDATE_AVAILABLE);
			scheduleService(0, DEFAULT_REFRESH_HOUR, 0);
		}
		else{
			sendUpdateStatus(UP_TO_DATE);
			scheduleService(0, 0, ERROR_REFRESH_MINUTE);
		}
	}
	
	public static final boolean isUpdateAvailble(SharedPreferences mPrefs){
		int status = mPrefs.getInt(PREF_UPDATE_STATUS, UP_TO_DATE);
		if(status==UPDATE_AVAILABLE){
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean downloadElahUpdate(){
		DownloadFile file = new DownloadFile(Consts.ELAH_APK_URL);
		return download(Consts.ELAH_DOWNLOAD_DIRECTORY,file);
	}
	
	private void sendUpdateStatus(int status){
		Intent intent = new Intent();
		intent.setAction(ACTION_UPDATION);
		intent.putExtra(INTENT_UPDATE_STATUS, status);
		sendBroadcast(intent);
		Log.d("UpdateService", "Sending status "+status);
	}
	
	private boolean download(String targetpath,DownloadFile... files){
		 		
		int fileLength = 0;
		long total = 0;
		boolean stat = true;
						
		HttpURLConnection connection;
		URL url;
//				Log.d("Log", "No of files to be downloaded = "+files.length);
		for(DownloadFile file:files){
			try {
				url = new URL(file.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setChunkedStreamingMode(0);
				connection.connect();
				
				fileLength = fileLength + connection.getContentLength();
				
//						Log.d("Log", "Url = "+file.getUrl());
//						Log.d("Log", "File size = "+file.getFileName()+" "+fileLength);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				stat = false;
				e.printStackTrace();
				if(SKIP_DOWNLOAD_ON_ERROR){
					break;
				}
				else{
					continue;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				stat = false;
				e.printStackTrace();
				if(SKIP_DOWNLOAD_ON_ERROR){
					break;
				}
				else{
					continue;
				}
			}
		}
//				Log.d("Log", "Total size to be downloaded in mb= "+fileLength/1048576);
		if(fileLength==0){
			return false;
		}
			
		for(DownloadFile file:files){
			try {
				if(file.isTargetpathSet()){
	            	targetpath = file.getTargetpath();   	        	
	            }
	            createFoldersIfNotExists(targetpath+File.separator);
				
	            url = new URL(file.getUrl());
	           	connection = (HttpURLConnection) url.openConnection();
	           	connection.setChunkedStreamingMode(0);
	           	connection.connect();
				
	            // this will be useful so that you can show a typical 0-100% progress bar
	           
	            // download the file     
	            InputStream input = new BufferedInputStream(url.openStream());
	            deleteFileIfExists(targetpath+File.separator+file.getFileName());
	            OutputStream output = new FileOutputStream(targetpath+File.separator+file.getFileName());
	           
//			            Log.d("Log", "target path = "+targetpath+File.separator+file.getFileName());
	            
	            byte data[] = new byte[1024];
	            int percentage_global=-1;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....	
	                int percentage = (int) (total * 100 / fileLength);
	                if(percentage_global!=percentage){
			                	Log.d("UpdateService", "Download Percentage = "+percentage);
	                  	percentage_global = percentage;
	                }
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
	            if(file.isCheckSumSet()){
	            	if(!compareCheckSum(file, targetpath+File.separator+file.getFileName())){
	            		stat = false;	
//			            		Log.d("Log", "Setting checksum value = "+stat);
	            		if(SKIP_DOWNLOAD_ON_ERROR){
							break;
						}
						else{
							continue;
						}
	            	}
	            }
	            
	        
			}
			catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				stat = false;
				e.printStackTrace();
				if(SKIP_DOWNLOAD_ON_ERROR){
					break;
				}
				else{
					continue;
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				stat = false;
				e.printStackTrace();
				if(SKIP_DOWNLOAD_ON_ERROR){
					break;
				}
				else{
					continue;
				}
			}
		}
		
		 return stat;
	}
	

	
	private void createFoldersIfNotExists(String path){
		Log.d("Log", "Making the folder at = "+path);
		File file = new File(path);
		if(!file.exists())
			file.mkdirs();
	}
	
	
	
	private void deleteFileIfExists(String path){
		File file = new File(path);
		if(file.exists())
			file.delete();
	}
	
	private boolean compareCheckSum(DownloadFile file, String downloadedpath){
		boolean stats = false;
		try {
			Log.e("Log","File checksum = "+file.getChecksum());
			Log.e("Log", "Md5 Checksum = "+getMD5CheckSum(downloadedpath));
			if(file.getChecksum().equals(getMD5CheckSum(downloadedpath)))
				stats = true;
        } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stats;
	}
	
	 public static String getMD5CheckSum(String filepath) throws IOException, NoSuchAlgorithmException {
		 
		    MessageDigest md = MessageDigest.getInstance("MD5");
		    FileInputStream fis = new FileInputStream(filepath);
		    byte[] dataBytes = new byte[1024];
		 
		    int nread = 0; 
		 
		    while ((nread = fis.read(dataBytes)) != -1) {
		      md.update(dataBytes, 0, nread);
		    };
		    fis.close();
		    
		    byte[] mdbytes = md.digest();
		 
		    //convert the byte to hex format
		    StringBuffer sb = new StringBuffer("");
		    for (int i = 0; i < mdbytes.length; i++) {
		    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		 
		    return sb.toString().toUpperCase(Locale.getDefault());
		 
	 }
	
	 private void scheduleService(int date,int hours,int minutes){
	 	Intent serviceIntent = new Intent(this,UpdateService.class);
	    PendingIntent restartServiceIntent = PendingIntent.getService(this, 0, serviceIntent,0);
	    AlarmManager alarms = (AlarmManager)getSystemService(ALARM_SERVICE);
	    // cancel previous alarm
	    alarms.cancel(restartServiceIntent);
	    // schedule alarm for today + 1 day
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DATE, date);
	    calendar.add(Calendar.HOUR_OF_DAY, hours);
	    calendar.add(Calendar.MINUTE, minutes);
	    calendar.add(Calendar.SECOND, 0);
	    // schedule the alarm
	    alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), restartServiceIntent);
	 }
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(updateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
