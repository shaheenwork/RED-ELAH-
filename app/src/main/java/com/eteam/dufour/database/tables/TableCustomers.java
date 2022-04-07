package com.eteam.dufour.database.tables;

import android.database.Cursor;

import com.eteam.db.DBTable;
import com.eteam.utils.Util;

public class TableCustomers extends DBTable{
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String TABLE_NAME = "customers";
	
	public static final String COLUMN_ID = "_id";
	
	//Unused Columns
	private static final String COLUMN_D  = "D";
	private static final String COLUMN_DT = "DT";
	private static final String COLUMN_ST = "ST";
	private static final String COLUMN_FG = "FG";
	private static final String COLUMN_SC = "SC";
	private static final String COLUMN_SN = "SN";
	private static final String COLUMN_SP = "SP";
	private static final String COLUMN_SR = "SR";
	private static final String COLUMN_AG = "AG";
	public static final String COLUMN_CL = "CL";
	private static final String COLUMN_SA = "SA";
	
	//Used Columns
	public static final String COLUMN_VT = "VT";
	public static final String COLUMN_BV = "BV";
	public static final String COLUMN_BX = "BX";
	public static final String COLUMN_BR = "BR";
	public static final String COLUMN_FX = "FX";
	public static final String COLUMN_NO = "NO";
	public static final String COLUMN_BC = "BC";
	public static final String COLUMN_ML = "ML";
	public static final String COLUMN_BN = "BN";
	public static final String COLUMN_BP = "BP";
	public static final String COLUMN_BL = "BL";
	public static final String COLUMN_CT = "CT";
	public static final String COLUMN_BA = "BA";
	public static final String COLUMN_AD = "AD";
	public static final String COLUMN_PV = "PV";
	public static final String COLUMN_PH = "PH";
	public static final String COLUMN_NM = "NM";
	public static final String COLUMN_CR = "CR";
	public static final String COLUMN_PC = "PC";
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
												+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
												+COLUMN_NM+" TEXT, "
												+COLUMN_NO+" TEXT, "
												+COLUMN_AD+" TEXT, "
												+COLUMN_CT+" TEXT, "
												+COLUMN_PC+" TEXT, "
												+COLUMN_PV+" TEXT, "
												+COLUMN_VT+" TEXT, "
												+COLUMN_CR+" TEXT, "
												+COLUMN_BC+" TEXT, "
												+COLUMN_PH+" TEXT, "
												+COLUMN_FX+" TEXT, "
												+COLUMN_ML+" TEXT, "
												+COLUMN_BA+" TEXT, "
												+COLUMN_BL+" TEXT, "
												+COLUMN_BP+" TEXT, "
												+COLUMN_BR+" TEXT, "
												+COLUMN_BV+" TEXT, "
												+COLUMN_BX+" TEXT, "
												+COLUMN_BN+" TEXT, "
												+COLUMN_D+" TEXT, "
												+COLUMN_DT+" TEXT, "	
												+COLUMN_ST+" TEXT, "
												+COLUMN_FG+" TEXT, "
												+COLUMN_SC+" TEXT, "
												+COLUMN_SN+" TEXT, "
												+COLUMN_SP+" TEXT, "
												+COLUMN_SR+" TEXT, "
												+COLUMN_AG+" TEXT, "
												+COLUMN_CL+" TEXT, "
												+COLUMN_SA+" TEXT)";
			
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

	// ===========================================================
	// Methods
	// ===========================================================
	
	public static final Customer getCustomer(String customerCode){
		Cursor c = getDB().query(TableCustomers.TABLE_NAME, 
				new String[]{TableCustomers.COLUMN_NO,TableCustomers.COLUMN_AD,TableCustomers.COLUMN_CT
				,TableCustomers.COLUMN_CT,TableCustomers.COLUMN_PC,TableCustomers.COLUMN_PV
				,TableCustomers.COLUMN_VT,TableCustomers.COLUMN_CR,TableCustomers.COLUMN_BC
				,TableCustomers.COLUMN_PH,TableCustomers.COLUMN_FX,TableCustomers.COLUMN_ML
				,TableCustomers.COLUMN_BA,TableCustomers.COLUMN_BL,TableCustomers.COLUMN_BP
				,TableCustomers.COLUMN_BX,TableCustomers.COLUMN_BR,TableCustomers.COLUMN_BV
				,TableCustomers.COLUMN_CL
				,TableCustomers.COLUMN_NM,TableCustomers.COLUMN_BN}
		, TableCustomers.COLUMN_NO+"= ?"
		, new String[]{customerCode}, null, null, null);
		
		Customer customer = null;
		if(c.moveToFirst()){
			customer = new Customer(c);
		}
		c.close();
		return customer;
	}
	
	
	

	public static BaseCustomer getBaseCustomer(String customerCode){
		Cursor c = getDB().query(TableCustomers.TABLE_NAME, 
				new String[]{TableCustomers.COLUMN_NM,TableCustomers.COLUMN_NO,
				TableCustomers.COLUMN_CL,
				TableCustomers.COLUMN_AD,TableCustomers.COLUMN_PC,TableCustomers.COLUMN_CT}, 
				TableCustomers.COLUMN_NO+"= ?", new String[]{customerCode}, null, null, null);
		BaseCustomer customer = null;
					
		if(c.moveToFirst()){
			customer = new BaseCustomer(c);
		}
		c.close();
			
		
		return customer;
	}
	
	public static BaseAddress getAddress(String customerCode){
		
		Cursor c = getDB().query(TableCustomers.TABLE_NAME, 
				new String[]{COLUMN_CT,COLUMN_PC,COLUMN_AD}, 
				COLUMN_NO+"= ?",
				new String[]{customerCode}, null, null, null);
			
		BaseAddress address = null;
		
		if(c.moveToFirst()){
			address = new BaseAddress(c);
		}
		c.close();			
		
		return address;
	}
	
	public static final int getCount(){
		
		Cursor c = getDB().query(TABLE_NAME, new String[]{COLUMN_ID}
						, null, null, null, null, null);
		int count = 0;
		if(c.moveToFirst()){
			count = c.getCount();
		}
		c.close();
		
		return count;
		
	}
	
//	public static final String get(ElahDBHelper dbHelper,String customerCode){
//		Cursor c = dbHelper.getReadableDatabase().query(TableCustomers.TABLE_NAME, 
//				new String[]{COLUMN_CT,COLUMN_PC,COLUMN_AD}, 
//				COLUMN_NO+"= ?",
//				new String[]{customerCode}, null, null, null);
//		String city = "";
//			
//		if(c.moveToFirst()){;
//			city = c.getString(c.getColumnIndex(TableCustomers.COLUMN_CT));
//		}
//		c.close();			
//		
//		return city;
//	}
//	
//	public static final String getAddress(ElahDBHelper dbHelper,String customerCode){
//		Cursor c = dbHelper.getReadableDatabase().query(TableCustomers.TABLE_NAME, 
//				new String[]{TableCustomers.COLUMN_AD}, 
//				TableCustomers.COLUMN_NO+"='"+customerCode+"'", null, null, null, null);
//		String address = null;
//		
//		if(c.moveToFirst()){
//			address = c.getString(c.getColumnIndex(TableCustomers.COLUMN_AD));
//		}
//		c.close();
//			
//		
//		return address;
//	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class Customer extends BaseCustomer {

		// ===========================================================
		// Constants
		// ===========================================================
		
		// ===========================================================
		// Fields
		// ===========================================================
		
		
		private String regNo;
		private String clientRef;
		private String billName;
		
	
		private String billAdress;
		private String billCity;
		private String billPost;
		private String billCounty;
		private String billRef;
		private String billVat;
		
		
		private Address fullAddress;
		
		// ===========================================================
		// Constructors
		// ===========================================================
		
		public Customer(Cursor c) {
			super(c);
			
			String billNo = c.getString(c.getColumnIndex(COLUMN_BC));
			if(Util.isStringValid(billNo)){
				billName = "("+billNo+") "+(c.getString(c.getColumnIndex(TableCustomers.COLUMN_BN)));
			}
			else{
				billName = "-";
			}
							
			regNo 		= c.getString(c.getColumnIndex(COLUMN_VT));
			clientRef 	= c.getString(c.getColumnIndex(COLUMN_CR));
			
			billAdress  = c.getString(c.getColumnIndex(COLUMN_BA));
			billCity	= c.getString(c.getColumnIndex(COLUMN_BL));
			billPost	= c.getString(c.getColumnIndex(COLUMN_BP));
			billCounty  = c.getString(c.getColumnIndex(COLUMN_BX));
			billRef		= c.getString(c.getColumnIndex(COLUMN_BR));
			billVat		= c.getString(c.getColumnIndex(COLUMN_BV));
			
			
			fullAddress 	= new Address(address,c);
			
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================
		
		public Address getAddress() {
			return fullAddress;
		};

		public String getRegNo() {
			return Util.filterDetails(regNo);
		}
		public void setRegNo(String regNo) {
			this.regNo = regNo;
		}
		public String getClientRef() {
			return Util.filterDetails(clientRef);
		}
		public void setClientRef(String clientRef) {
			this.clientRef = clientRef;
		}
		public String getBillName() {
			return Util.filterDetails(billName);
		}
		public void setBillName(String billName) {
			this.billName = billName;
		}
	
		public String getBillAdress() {
			return Util.filterDetails(billAdress);
		}
		public void setBillAdress(String billAdress) {
			this.billAdress = billAdress;
		}
		public String getBillCity() {
			return Util.filterDetails(billCity);
		}
		public void setBillCity(String billCity) {
			this.billCity = billCity;
		}
		public String getBillPost() {
			return Util.filterDetails(billPost);
		}
		public void setBillPost(String billPost) {
			this.billPost = billPost;
		}
		public String getBillCounty() {
			return Util.filterDetails(billCounty);
		}
		public void setBillCounty(String billCounty) {
			this.billCounty = billCounty;
		}
		public String getBillRef() {
			return Util.filterDetails(billRef);
		}
		public void setBillRef(String billRef) {
			this.billRef = billRef;
		}
		public String getBillVat() {
			return Util.filterDetails(billVat);
		}
		public void setBillVat(String billVat) {
			this.billVat = billVat;
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
	
	public static class BaseCustomer {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================

		protected String name;
		protected String code;
		protected String cluster;
		protected BaseAddress address;		
		
		// ===========================================================
		// Constructors
		// ===========================================================
		
		public BaseCustomer(Cursor c){
			name		 	= c.getString(c.getColumnIndex(TableCustomers.COLUMN_NM));
			code		 	= c.getString(c.getColumnIndex(TableCustomers.COLUMN_NO));
			cluster 		= c.getString(c.getColumnIndex(TableCustomers.COLUMN_CL));
			address			= new BaseAddress(c);
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public String getName() {
			return name;
		}
	
		public String getCode() {
			return code;
		}
	
		public BaseAddress getAddress() {
			return address;
		}
		public String getCluster() {
			return cluster;
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
	
	public static class Address extends BaseAddress{
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================

		private String cap;
		private String county;
		private String telephone;
		private String fax;
		private String email;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public Address(Cursor c){
			super(c);
			init(c);
		}
		
		public Address(BaseAddress base,Cursor c) {
			super(base);
			init(c);
			
		}
		
				
		// ===========================================================
		// Getter & Setter
		// ===========================================================
		
		public String getCap() {
			return cap;
		}
		
		public String getCounty() {
			return county;
		}
		
		public String getTelephone() {
			return telephone;
		}
		
		public String getFax() {
			return fax;
		}
		
		public String getEmail() {
			return email;
		}
		
		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================
		private void init(Cursor c){
			cap 		= c.getString(c.getColumnIndex(COLUMN_PC));
			county 		= c.getString(c.getColumnIndex(COLUMN_PV));
			telephone 	= c.getString(c.getColumnIndex(COLUMN_PH));
			fax 		= c.getString(c.getColumnIndex(COLUMN_FX));
			email 		= c.getString(c.getColumnIndex(COLUMN_ML));
		}
		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
	

	public static class BaseAddress {
		// ===========================================================
		// Constants
		// ===========================================================
		// ===========================================================
		// Fields
		// ===========================================================

		private String address;
		private String city;
		private String postalCode;
		
		// ===========================================================
		// Constructors
		// ===========================================================

		public BaseAddress(BaseAddress baseAddress) {
			this.address 		= baseAddress.address;
			this.city			= baseAddress.city;
			this.postalCode		= baseAddress.postalCode;
		}
		
		public BaseAddress(Cursor c){
			this.address 		= c.getString(c.getColumnIndex(TableCustomers.COLUMN_AD));
			this.postalCode 	= c.getString(c.getColumnIndex(TableCustomers.COLUMN_PC));
			this.city 			= c.getString(c.getColumnIndex(TableCustomers.COLUMN_CT));
		
		}
		
		// ===========================================================
		// Getter & Setter
		// ===========================================================
		public String getAddress() {
			return address;
		}
		
		
		public String getPostalCode() {
			return postalCode;
		}
		
		public String getCity() {
			return city;
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

	
}
