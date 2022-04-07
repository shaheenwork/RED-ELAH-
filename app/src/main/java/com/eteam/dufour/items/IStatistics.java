package com.eteam.dufour.items;

import com.eteam.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class IStatistics {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String KEY_LOD = "LOD";
	public static final String KEY_LON = "LON";
	public static final String KEY_LPR = "LPR";
	public static final String KEY_LSD = "LSD";
	public static final String KEY_LSN = "LSN";
	public static final String KEY_PCD = "PCD";
	public static final String KEY_PCP = "PCP";
	public static final String KEY_PCQ = "PCQ";
	public static final String KEY_PMD = "PMD";
	public static final String KEY_PMP = "PMP";
	public static final String KEY_PMQ = "PMQ";
	public static final String KEY_PND = "PND";
	public static final String KEY_PNP = "PNP";
	public static final String KEY_PNQ = "PNQ";
	public static final String KEY_PPD = "PPD";
	public static final String KEY_PPP = "PPP";
	public static final String KEY_PPQ = "PPQ";

	private static final String[] ITALIAN_MONTH = {"GENNAIO","FEBBRAIO","MARZO","APRILE","MAGGIO","GIUGNO","LUGLIO"
			,"AGOSTO","SETTEMBRE","OTTOBRE","NOVEMBRE","DICEMBRE"};
	// ===========================================================
	// Fields
	// ===========================================================

	private String ppd;
	private String ppq;
	private String ppp;
	private String pcd;
	private String pcq;
	private String pcp;
	private String pnd;
	private String pnq;
	private String pnp;
	private String pmd;
	private String pmq;
	private String pmp;
	private String lqn;
	private String lod;
	private String lsn;
	private String lsd;

	private String lpr;




	// ===========================================================
	// Constructors
	// ===========================================================
	public IStatistics(JSONObject json) throws JSONException{
		ppd = json.optString(KEY_PPD,"");
		ppq = json.optString(KEY_PPQ,"");
		ppp = json.optString(KEY_PPP,"");
		pcd = json.optString(KEY_PCD,"");
		pcq = json.optString(KEY_PCQ,"");
		pcp = json.optString(KEY_PCP,"");
		pnd = json.optString(KEY_PND,"");
		pnq = json.optString(KEY_PNQ,"");
		pnp = json.optString(KEY_PNP,"");
		pmd = json.optString(KEY_PMD,"");
		pmq = json.optString(KEY_PMQ,"");
		pmp = json.optString(KEY_PMP,"");
		lqn = json.optString(KEY_LSN,"");
		lod = json.optString(KEY_LOD,"");
		lsn = json.optString(KEY_LSN,"");
		lsd = json.optString(KEY_LSD,"");
		lpr = json.optString(KEY_LPR,"");
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public String getPPDAsDate(){
		return getItalianMonth(ppd);
	}

	public String getPCDAsDate(){
		return getItalianMonth(pcd);
	}

	public String getPNDAsDate(){
		return getItalianMonth(pnd);
	}

	public String getPMDAsDate(){
		return getItalianMonth(pmd);
	}

	public String getPpd() {
		return ppd;
	}

	public void setPpd(String ppd) {
		this.ppd = ppd;
	}

	public String getPpq() {
		if(!ppq.trim().equals("")){
			ppq = "Q.t?"+ppq;
		}
		return ppq;
	}

	public void setPpq(String ppq) {
		this.ppq = ppq;
	}

	public String getPpp() {
		if(!ppp.trim().equals("")){
			ppp = " Valore "+ppp;
		}
		return ppp;
	}

	public void setPpp(String ppp) {
		this.ppp = ppp;
	}

	public String getPcd() {
		return pcd;
	}

	public void setPcd(String pcd) {
		this.pcd = pcd;
	}

	public String getPcq() {
		if(!pcq.trim().equals("")){
			pcq = "Q.t?"+pcq;
		}

		return pcq;
	}

	public void setPcq(String pcq) {
		this.pcq = pcq;
	}

	public String getPcp() {
		if(!pcp.trim().equals("")){
			pcp = " Valore "+pcp;
		}
		return pcp;
	}

	public void setPcp(String pcp) {
		this.pcp = pcp;
	}

	public String getPnd() {
		return pnd;
	}

	public void setPnd(String pnd) {
		this.pnd = pnd;
	}

	public String getPnq() {
		return pnq;
	}

	public void setPnq(String pnq) {
		this.pnq = pnq;
	}

	public String getPnp() {
		return pnp;
	}

	public void setPnp(String pnp) {
		this.pnp = pnp;
	}

	public String getPmd() {
		return pmd;
	}

	public void setPmd(String pmd) {
		this.pmd = pmd;
	}

	public String getPmq() {
		if(!pmq.trim().equals("")){
			pmq = "Q.t?"+pmq;
		}
		return pmq;
	}

	public void setPmq(String pmq) {
		this.pmq = pmq;
	}

	public String getPmp() {
		if(!pmp.trim().equals("")){
			pmp = " Valore "+pmp;
		}
		return pmp;
	}

	public void setPmp(String pmp) {
		this.pmp = pmp;
	}

	public String getLqn() {
		return lqn;
	}

	public void setLqn(String lqn) {
		this.lqn = lqn;
	}

	public String getLod() {
		return lod;
	}

	public void setLod(String lod) {
		this.lod = lod;
	}

	public String getLsn() {
		return lsn;
	}

	public void setLsn(String lsn) {
		this.lsn = lsn;
	}

	public String getLsd() {
		return lsd;
	}

	public void setLsd(String lsd) {
		this.lsd = lsd;
	}

	public String getLpr() {
		return lpr;
	}

	public String getLprAsNum(){
		return Util.getAsElahNumFormatForDetails(lpr);
	}


	public void setLpr(String lpr) {
		this.lpr = lpr;
	}

	public String getOrdine(){
		if(!lqn.trim().equals("")&&!lod.trim().equals("")){
			return lqn+" del "+lod;
		}
		return "";
	}

	public String getCosegna(){
		if(!lsn.trim().equals("")&&!lsd.trim().equals("")){
			return lsn+" del "+lsd;
		}
		return "";
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	private String getItalianMonth(String date){
		String out  = "";
		if(date.contains("/")){
			String[] dateArray = date.split("/");
			int i;
			try{
				i = Integer.parseInt(dateArray[0]);
			}
			catch(NumberFormatException e){
				return out;
			}
			out = ITALIAN_MONTH[i-1]+" "+dateArray[1];
		}
		return out;
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================


}
