package com.eteam.utils;

import android.os.Environment;
import android.text.InputFilter;

import java.io.File;

public class Consts {
    // ===========================================================
    // Constants
    // ===========================================================

    public static final boolean DEBUGGABLE = true;

    /*--------------------------------------------------------------------------------------------------------------------------------*/
    //ELAH PRE PRODUCTION (NEW)

	/*public static final String SERVER_URL = "http://preprodweb.elah-dufour.it/portal/mobile";
	public static final String ELAH_APK_URL = "http://preprodweb.elah-dufour.it/portal/dufourMobile/downloads/ElahAndroid.apk";
	public static final String ERROR_URL = "http://121.241.180.136:8082/elaherror/error.jsp";
	public static final String DB_NAME = "elahPreProd.db";*/

    /*--------------------------------------------------------------------------------------------------------------------------------*/
  /*  //ELAH TEST FROM (19-05-2014)
	//public static final String SERVER_URL 	= "http://test.elah-dufour.it/portal-test/mobile"; //old
     public static final String SERVER_URL 	= "http://test.elah-dufour.it/portal-test-2016/mobile"; //new
//	public static final String SERVER_URL 	= "http://192.168.0.84:8080/elah2016/mobile"; //manu local
	//public static final String SERVER_URL 	= "http://192.168.0.90:8081/elah/mobile";
	//public static final String ELAH_APK_URL = "http://test.elah-dufour.it/portal-test/dufourMobile/downloads/ElahAndroid.apk";//old
	public static final String ELAH_APK_URL = "http://test.elah-dufour.it/portal-test-2016/dufourMobile/downloads/ElahAndroid.apk";//new
	public static final String ERROR_URL 	= "https://121.241.180.136:8082/elaherror/error.jsp";
	public static final String DB_NAME 		= "elahCustTest.db";*/
    /*--------------------------------------------------------------------------------------------------------------------------------*/
    //ELAH PRODUCTION
    public static final String SERVER_URL = "https://web.elah-dufour.it/portal/mobile";
    public static final String ELAH_APK_URL = "https://web.elah-dufour.it/portal/dufourMobile/downloads/ElahAndroid.apk";
    public static final String ERROR_URL = "http://121.241.180.136:8082/elaherror/error.jsp";
    public static final String DB_NAME = "elah.db";
    /*--------------------------------------------------------------------------------------------------------------------------------*/

    //ELAH ANOOP
//	public static final String SERVER_URL = "http://192.168.0.31:8080/elah/mobile"; 

    //ELAH BIJU
//	public static final String SERVER_URL = "http://192.168.1.59:8080/elah/mobile"; 

    //ELAH SIBI
//	public static final String SERVER_URL = "http://192.168.1.9:8080/elah/mobile"; 

    //ELAH LOCAL
//	public static final String SERVER_URL = "http://192.168.0.1:8080/elah/mobile";
//	public static final String ELAH_APK_URL = "http://192.168.0.1:8082/elah-Real/dufourMobile/downloads/ElahAndroid.apk";
//	public static final String ERROR_URL = "http://192.168.0.1:8082/elaherror/error.jsp";
//	public static final String DB_NAME = "localelah.db";

    //ELAH PUBLIC
//	public static final String SERVER_URL = "http://121.241.180.136:8080/elah/mobile";      
//	public static final String ELAH_APK_URL ="http://121.241.180.136:8082/elah-Real/dufourMobile/downloads/ElahAndroid.apk";
//	public static final String ERROR_URL = "http://121.241.180.136:8082/elaherror/error.jsp";
//	public static final String DB_NAME = "localelah.db";

    /*--------------------------------------------------------------------------------------------------------------------------------*/

    //ELAH CUSTOMER TEST
//	public static final String SERVER_URL = "http://web.elah-dufour.it/portal-test/mobile";      
//	public static final String ELAH_APK_URL = "http://web.elah-dufour.it/portal-test/dufourMobile/downloads/ElahAndroid.apk";
//	public static final String ERROR_URL = "http://121.241.180.136:8082/elaherror/error.jsp";
//	public static final String DB_NAME = "dufour.db";

    /*--------------------------------------------------------------------------------------------------------------------------------*/

    //ELAH NEW CUSTOMER TEST PREPROD (NOT USED ANYMORE)

//	public static final String SERVER_URL = "http://portalnovi.preprod.gruppozenit.com/portal-test/mobile";      
//	public static final String ELAH_APK_URL = "http://portalnovi.preprod.gruppozenit.com/portal-test/dufourMobile/downloads/ElahAndroid.apk";
//	public static final String ERROR_URL = "http://121.241.180.136:8082/elaherror/error.jsp";
//	public static final String DB_NAME = "testelah.db";


    /*--------------------------------------------------------------------------------------------------------------------------------*/

    //ELAH NEW CUSTOMER TEST 14-11-2013

//	public static final String SERVER_URL = "http://newweb.elah-dufour.it/portal-test/mobile";      
//	public static final String ELAH_APK_URL = "http://newweb.elah-dufour.it/portal-test/dufourMobile/downloads/ElahAndroid.apk";
//	public static final String ERROR_URL = "http://121.241.180.136:8082/elaherror/error.jsp";
//	public static final String DB_NAME = "newtestelah.db";


    /*--------------------------------------------------------------------------------------------------------------------------------*/
    //ELAH REAL LOCAL

//	public static final String SERVER_URL = "http://192.168.0.1:8082/elah-Real/mobile";
//	public static final String ELAH_APK_URL = "http://192.168.0.1:8082/elah-Real/dufourMobile/downloads/ElahAndroid.apk";
//	public static final String ERROR_URL = "http://192.168.0.1:8082/elaherror/error.jsp";
//	public static final String DB_NAME = "realelah.db";


    //ELAH-REAL PUBLIC
//	public static final String SERVER_URL = "http://121.241.180.136:8082/elah-Real/mobile";
//	public static final String ELAH_APK_URL = "http://121.241.180.136:8082/elah-Real/dufourMobile/downloads/ElahAndroid.apk";
//	public static final String ERROR_URL = "http://121.241.180.136:8082/elaherror/error.jsp";
//	public static final String DB_NAME 	 = "realelah.db";

    /*--------------------------------------------------------------------------------------------------------------------------------*/

    public static final String PREF_ELAH = "com.eteam.utils.AppPreferences.elah_pref";


    /*--------------------------------------------------------------------------------------------------------------------------------*/

    //Static Arrays
    public static final String[] ARRAY_SI_NO = new String[]{"", "Si", "No"};
    public static final String[] ARRAY_VISIBILITY = new String[]{"", "Isola", "Testata", "Fuori banco"};
    public static final String[] ARRAY_FACING = new String[]{"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    public static final String[] ARRAY_POSLNONE = new String[]{"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    public static final String[] ARRAY_POSLNTWO = new String[]{"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    public static final String[] ARRAY_PREZZO_PROMO = new String[]{"", "10", "20", "30", "40", "50"};
    public static final String[] ARRAY_ASSORT = new String[]{"", "Disponibile", "NonAssortito", "Rottura di stock", "Sospensione"};


    /*--------------------------------------------------------------------------------------------------------------------------------*/

    public static final String JSON_KEY_ID = "ID";

    public static final String JSON_KEY_SESSION_TIME_OUT = "conStatus";
    public static final String RESPONSE_SESSION_TIMED_OUT = "1";

    /*--------------------------------------------------------------------------------------------------------------------------------*/

    public static final String ELAH_APK_FILENAME = "ElahAndroid.apk";
    public static final String ELAH_DOWNLOAD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "ELAH_DOWNLOADS";

    /*--------------------------------------------------------------------------------------------------------------------------------*/

    public static final ElahKeyListener ELAH_KEY_LISTENER = new ElahKeyListener(false, true);
    public static final InputFilter[] COMMA_FILTER = new InputFilter[]{new CommaInputFilter(), new InputFilter.LengthFilter(15)};
    /*--------------------------------------------------------------------------------------------------------------------------------*/

    public static final String KEY_ANDROID_VERSION="android_version";
    public static final String KEY_FAILURE_TYPE="failure_type";
    public static final String KEY_SYNC_TIME="sync_time";
    public static final String KEY_USERNAME="user_name";
    public static final String KEY_SYNC_EVENT="Sync";
    public static final String KEY_SYNC_STATUS="Sync_status";
    public static final String EVENT_LOGIN = "login";
    public static final int DEFAULT_CLUSTER_VALUE = 6;


    //Dashboard

    public static String CATEGORIA = "Categoria";
    public static int SURVEY_LEVEL_DISPONIBILE = 1;
    public static int SURVEY_LEVEL_ROTTI_DI_STOCK= 3;
    public static final String[] ARRAY_CATEGORY = new String[]{"Presenza prodotto", "Rott. Di Stock", "Facing medio", "Qualità espositiva"};


    public  static int TYPE_HEADING1=1;
    public  static int TYPE_HEADING2=2;
    public  static int TYPE_VALUE=3;

    public static int NO_CIRCLE=0;
    public static int NA=0;
    public static int RED_CIRCLE=1;
    public static int GREEN_CIRCLE=2;
    public static int YELLOW_CIRCLE=3;

    public static int PLUS=1;
    public static int MINUS=0;
    public static int NO_CHANGE=2;

    public static int COPY_SURVEY=1;
    public static int NEW_SURVEY=2;



}
