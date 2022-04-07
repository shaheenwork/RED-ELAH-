package com.eteam.utils.downloader;

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

import android.os.AsyncTask;
//import android.util.Log;
import android.util.Log;

public class DownloadTask extends AsyncTask<DownloadFile, Integer, Boolean>{
		private String targetpath;
		
		private static FileInputStream fis;
		private boolean cancelled;
		private boolean skiperrorfiles;
		
		public static final int CONNECTING_SERVER = -1;
		public static final int CONNECTION_SUCCESS = -2;
		public static final int STARTING_DOWNLOAD = -3;
		
		public DownloadTask(String targetpath) {
			// TODO Auto-generated constructor stub
			this.targetpath = targetpath;
			createFoldersIfNotExists(targetpath);
			setSkiperrorfiles(false);
		}
		
		public DownloadTask() {
			// TODO Auto-generated constructor stub
			this.targetpath = null;
			setSkiperrorfiles(false);
		}
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			cancelled = false;
		}
		


		@Override
		protected void onPostExecute(Boolean downloadsucess) {
		// TODO Auto-generated method stub
			super.onPostExecute(downloadsucess);
//			Log.d("Log", "Sending download status "+downloadsucess);
			
		}
		


		@Override
		protected Boolean doInBackground(DownloadFile... files) {
			
				int fileLength = 0;
				long total = 0;
				boolean stat = true;
								
				HttpURLConnection connection;
				URL url;
//				Log.d("Log", "No of files to be downloaded = "+files.length);
				for(DownloadFile file:files){
					try {
						publishProgress(CONNECTING_SERVER);
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
						if(!isSkiperrorfiles()){
							break;
						}
						else{
							continue;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						stat = false;
						e.printStackTrace();
						if(!isSkiperrorfiles()){
							break;
						}
						else{
							continue;
						}
					}
				}
//				Log.d("Log", "Total size to be downloaded in mb= "+fileLength/1048576);
				for(DownloadFile file:files){
					try {
						if(file.isTargetpathSet()){
			            	targetpath = file.getTargetpath();   	        	
			            }
			            createFoldersIfNotExists(file.getTargetpath()+File.separator);
						
			            url = new URL(file.getUrl());
			           	connection = (HttpURLConnection) url.openConnection();
			           	connection.setChunkedStreamingMode(0);
			           	connection.connect();
						
			           	publishProgress(CONNECTION_SUCCESS,fileLength);
			            // this will be useful so that you can show a typical 0-100% progress bar
			           
			            // download the file
			           	publishProgress(CONNECTING_SERVER);          
			            InputStream input = new BufferedInputStream(url.openStream());
			            deleteFileIfExists(targetpath+File.separator+file.getFileName());
			            OutputStream output = new FileOutputStream(targetpath+File.separator+file.getFileName());
			           
//			            Log.d("Log", "target path = "+targetpath+File.separator+file.getFileName());
			            
			            publishProgress(STARTING_DOWNLOAD);
			            byte data[] = new byte[1024];
			            int percentage_global=-1;
			            int count;
			            while (!cancelled&&(count = input.read(data)) != -1) {
			                total += count;
			                // publishing the progress....	
			                int percentage = (int) (total * 100 / fileLength);
			                if(percentage_global!=percentage){
			                	publishProgress(percentage);
//			                	Log.d("Log", "Download Percentage = "+percentage);
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
			            		if(!isSkiperrorfiles()){
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
						if(!isSkiperrorfiles()){
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
						if(!isSkiperrorfiles()){
							break;
						}
						else{
							continue;
						}
					}
				
				}
//				Log.d("Log", "Status = "+stat);
				return stat;
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
			    fis = new FileInputStream(filepath);
			    byte[] dataBytes = new byte[1024];
			 
			    int nread = 0; 
			 
			    while ((nread = fis.read(dataBytes)) != -1) {
			      md.update(dataBytes, 0, nread);
			    };
			 
			    byte[] mdbytes = md.digest();
			 
			    //convert the byte to hex format
			    StringBuffer sb = new StringBuffer("");
			    for (int i = 0; i < mdbytes.length; i++) {
			    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			    }
			 
			    return sb.toString().toUpperCase();
			 
		}
		
		private void createFoldersIfNotExists(String path){
//			Log.d("Log", "Making the folder at = "+path);
			File file = new File(path);
			if(!file.exists())
				file.mkdirs();
		}
		
		public boolean getCancelStatus(){
			return cancelled;
		}
		
		public void cancelDownload(){
			cancelled = true;
		}

		public boolean isSkiperrorfiles() {
			return skiperrorfiles;
		}

		public void setSkiperrorfiles(boolean skiperrorfiles) {
			this.skiperrorfiles = skiperrorfiles;
		}
		
		private void deleteFileIfExists(String path){
			File file = new File(path);
			if(file.exists())
				file.delete();
		}
		
			
	}