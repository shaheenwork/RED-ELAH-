package com.eteam.utils.downloader;

public class DownloadFile {
	
	private String url;
	private String checksum;
	private String filename;
	private String targetpath;

	public DownloadFile(String url){
		setUrl(url);
	}
	
	public DownloadFile(String url,String checksum){
		setUrl(url);
		setChecksum(checksum);
	}
	
	public DownloadFile(String url, String checksum,
			String filename){
		setUrl(url);
		setChecksum(checksum);
		setFileName(filename);
	}

	public String getUrl() {
		url = url.replace(" ", "%20");
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getFileName() {
		if(!isFileNameSet()){
			String[] split = url.split("/");
			filename = split[split.length-1];
		}			
		return filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}
	
	public boolean isFileNameSet(){
		return filename!=null;
	}
	
	public boolean isCheckSumSet(){
		return checksum!=null;
	}
	
	public boolean isTargetpathSet(){
		return targetpath!=null;
	}

	public String getTargetpath() {
		return targetpath;
	}

	public void setTargetpath(String targetpath) {
		this.targetpath = targetpath;
	}
}
