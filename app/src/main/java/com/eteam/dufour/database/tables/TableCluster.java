package com.eteam.dufour.database.tables;

//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.eteam.db.DBTable;
//import com.eteam.dufour.database.tables.TableCluster.Cluster;

//public class TableCluster extends DBTable {
//	// ===========================================================
//	// Constants
//	// ===========================================================
//
//	public static enum ClusterStatus{
//		PENDING(2),
//		ERROR(0),
//		SUCCESS(1);
//		
//		private int status;
//		
//		private ClusterStatus(int status) {
//			// TODO Auto-generated constructor stub
//			this.status = status;
//		}
//		
//		public int getStatus() {
//			return status;
//		}
//	}
//
//	private static final String TABLE_NAME			= "cluster";
//	
//	private static final String COLUMN_ID 		 	= "_id";
//	private static final String COLUMN_CUSTOMER_ID 	= "cust_code";
//	private static final String COLUMN_CLUSTER		= "cluster";
//	private static final String COLUMN_STATUS		= "status";
//	
//	public static final String CREATE_TABLE 		= "CREATE TABLE IF NOT EXISTS "
//													+TABLE_NAME
//													+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
//													+COLUMN_CUSTOMER_ID+" TEXT, "
//													+COLUMN_STATUS+" INTEGER)";
//	
//	
//	
//	// ===========================================================
//	// Fields
//	// ===========================================================
//	
//	// ===========================================================
//	// Constructors
//	// ===========================================================
//
//	// ===========================================================
//	// Getter & Setter
//	// ===========================================================
//
//	// ===========================================================
//	// Methods for/from SuperClass/Interfaces
//	// ===========================================================
//
//	// ===========================================================
//	// Methods
//	// ===========================================================
//	public static void onCreate(SQLiteDatabase db){
//		db.execSQL(CREATE_TABLE);
//	}
//	
//	public static void createOrUpdate(Cluster cluster){
//		
//		String[] projection = new String[]{COLUMN_CLUSTER,
//				COLUMN_CUSTOMER_ID,COLUMN_ID,COLUMN_STATUS};
//		String selection    = COLUMN_CUSTOMER_ID+"=?";
//		String[] selectionArgs = new String[]{cluster.customerId};
//		DBTable.createOrUpdate(cluster, TABLE_NAME, projection, selection, selectionArgs);
//		
//		
//	}
//	
//	public static void udpateClusterStatus(String customerId,int status){
//		String selection    = COLUMN_CUSTOMER_ID+"=?";
//		String[] selectionArgs = new String[]{customerId};
//		ContentValues values = new ContentValues();
//		values.put(COLUMN_STATUS, status);
//		getDB().update(TABLE_NAME, values, selection, selectionArgs);
//		
//	}
//	
//
//	public static Cluster getCluster1(String customerId) {
//		String[] projection = new String[]{COLUMN_CLUSTER,
//				COLUMN_CUSTOMER_ID,COLUMN_ID,COLUMN_STATUS};
//		String selection    = COLUMN_CUSTOMER_ID+"=?";
//		String[] selectionArgs = new String[]{customerId};
//		
//		Cursor c = getDB().query(TABLE_NAME, projection, selection,
//				selectionArgs, null, null, null);
//		Cluster cluster = null;
//		if(c.moveToFirst()){
//			cluster = new Cluster(customerId,c);
//		}
//		c.close();
//		return cluster;
//	}
//	// ===========================================================
//	// Inner and Anonymous Classes
//	// ===========================================================
//	
//	public static class Cluster extends DBModel{
//		// ===========================================================
//		// Constants
//		// ===========================================================
//		private static final ContentValues values = new ContentValues();
//		// ===========================================================
//		// Fields
//		// ===========================================================
//		private String customerId;
//		private String cluster;
//		private int    status;
//		// ===========================================================
//		// Constructors
//		// ===========================================================
//		
//		public Cluster(String customerId,String cluster,int status){
//			this.customerId = customerId;
//			this.cluster    = cluster;
//			this.status		= status;
//		}
//		
//		public Cluster(String customerId,String cluster){
//			this(customerId, cluster, ClusterStatus.PENDING.status);
//		}
//		
//		public Cluster(String customerId,JSONObject json) throws JSONException {
//			this(customerId, 
//					json.getString(COLUMN_CLUSTER), 
//					json.getInt(COLUMN_STATUS));
//			
//		}
//		
//		public Cluster(String customerId,Cursor c) {
//			this(customerId, 
//					c.getString(c.getColumnIndex(COLUMN_CLUSTER)), 
//					c.getInt(c.getColumnIndex(COLUMN_STATUS)));
//		}
//
//		// ===========================================================
//		// Getter & Setter
//		// ===========================================================
//		public String getCustomerId() {
//			return customerId;
//		}
//		
//		public String getCluster1() {
//			return cluster;
//		}
//		
//		public int getStatus() {
//			return status;
//		}
//		
//		public void setError(){
//			status = ClusterStatus.ERROR.status;
//		}
//		// ===========================================================
//		// Methods for/from SuperClass/Interfaces
//		// ===========================================================
//
//		@Override
//		public ContentValues getContentValues() {
//			values.clear();
//			values.put(COLUMN_CUSTOMER_ID, customerId);
//			values.put(COLUMN_STATUS, status);
//			values.put(COLUMN_CLUSTER, cluster);
//			return values;
//		}
//		
//		// ===========================================================
//		// Methods
//		// ===========================================================
//		
//		// ===========================================================
//		// Inner and Anonymous Classes
//		// ===========================================================
//	}
//
//	
//}
