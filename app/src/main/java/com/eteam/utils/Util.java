package com.eteam.utils;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.database.tables.TableSurvey.Survey;
import com.eteam.dufour.database.tables.TableSurveyCustomers.SurveyCustomer;
import com.eteam.dufour.mobile.AlertMsgActivity;
import com.eteam.dufour.mobile.AlertUpdateActivity;
import com.eteam.dufour.mobile.LoginActivity;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.service.UpdateService;

@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class Util {
    // ===========================================================
    // Constants
    // ===========================================================
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
    public static String getCurrentTimeInMilliSeconds() {
        return String.valueOf(new Date().getTime());
    }

    public static final String getCurrentDate() {
        SimpleDateFormat dfDate_day = new SimpleDateFormat("yyyy-MM-dd");
//	    String dt="";
        Calendar c = Calendar.getInstance();
//	    Log.d("Log","Current date time - "+dfDate_day.format(c.getTime()));
        return dfDate_day.format(c.getTime());

    }

    public static final String getCurrentDateTime() {
    	/*SimpleDateFormat dfDate_day= new SimpleDateFormat("dd-MM-yyyy / HH:mm");
//	    String dt="";
	    Calendar c = Calendar.getInstance(); 
//	    Log.d("Log","Current date time - "+dfDate_day.format(c.getTime()));
	    return dfDate_day.format(c.getTime());*/
        // SimpleDateFormat dfDate_day = new SimpleDateFormat("dd-MM-yyyy / HH:mm");


        SimpleDateFormat dfDate_day = new SimpleDateFormat("dd-MM-yyyy / HH:mm:ss.SSS");
        Calendar c = Calendar.getInstance();
        System.currentTimeMillis();
        c.setTimeInMillis(System.currentTimeMillis());
        return dfDate_day.format(c.getTime());

    }

    ///by bibn new addtion for  updation old apk..in new version not need this
    public static final String getNewChangedTime(long newTime) {
        SimpleDateFormat dfDate_day = new SimpleDateFormat("dd-MM-yyyy / HH:mm:ss.SSS");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(newTime);
        return dfDate_day.format(newTime);
    }

    public static final String getCurrentDateTimeWithoutSpace() {
        SimpleDateFormat dfDate_day = new SimpleDateFormat("dd-MM-yyyy/HH:mm");
//	    String dt="";
        Calendar c = Calendar.getInstance();
//	    Log.d("Log","Current date time - "+dfDate_day.format(c.getTime()));
        return dfDate_day.format(c.getTime());

    }

    public static final String getDateFromDateTimeFormat(String dateTime) {
        int seperatorIndex = dateTime.indexOf("/");
        if (seperatorIndex != -1)
            dateTime = dateTime.substring(0, seperatorIndex).trim();
        return dateTime;
    }


    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void setUpToast(Toast mStatToast) {
        mStatToast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) mStatToast.getView();
        toastView.setBackgroundResource(R.drawable.bk_toast);
        toastView.setGravity(Gravity.CENTER);
        TextView text = (TextView) toastView.findViewById(android.R.id.message);
        text.setTextColor(Color.BLACK);
        text.setShadowLayer(0, 0, 0, Color.BLACK);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setGravity(Gravity.CENTER);
    }
    public static void setUpToastBOTTOM(Toast mStatToast) {
        mStatToast.setGravity(Gravity.BOTTOM, 0, 150);
        LinearLayout toastView = (LinearLayout) mStatToast.getView();
        toastView.setBackgroundResource(R.drawable.bk_toast);
        toastView.setGravity(Gravity.CENTER);
        TextView text = (TextView) toastView.findViewById(android.R.id.message);
        text.setTextColor(Color.BLACK);
        text.setShadowLayer(0, 0, 0, Color.BLACK);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setGravity(Gravity.CENTER);
    }

    public static final void showToast(Toast mToast, int msgId) {
        // TODO Auto-generated method stub
        if (mToast != null) {
            mToast.setText(msgId);
            try {
                mToast.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final void showToast(Toast mToast, String msg) {
        // TODO Auto-generated method stub
        if (mToast != null) {
            mToast.setText(msg);
            try {
                mToast.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final String getUniqueId(String salesPerson) {
        return (BigDecimal.valueOf(Math.floor(Math.random() * 900000) + 100000)).stripTrailingZeros().toPlainString()
                + salesPerson + getCurrentTimeInMilliSeconds();
    }

    public static boolean isSpinnerEmpty(Spinner spinner) {
        if (spinner.getSelectedItemPosition() == 0)
            return true;
        else return false;
    }

    public static void showKeyPad(Context context, EditText textEdit) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textEdit, InputMethodManager.SHOW_IMPLICIT);
        textEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    public static void hideKeyPad(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static final boolean installApplication(Context context, String apkPath) {
        // TODO Auto-generated method stub
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static final String getAppFullVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            return versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static final String getApplicationVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            int bracketIndex = versionName.indexOf("(");
            versionName = versionName.substring(0, bracketIndex);
            return versionName.trim();
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static final void logout(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static final void logout(Context context, int errorMsgId) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(LoginActivity.INTENT_ERROR_MSG_ID, errorMsgId);
        context.startActivity(intent);
    }

    public static final void logout(Context context, String errorMsg) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(LoginActivity.INTENT_ERROR_MSG, errorMsg);
        context.startActivity(intent);
    }

    public static final void startAlertMsgActivity(Context context, int msgId, int btnLblId) {
        Intent intent = new Intent(context, AlertMsgActivity.class);
        intent.putExtra(AlertMsgActivity.INTENT_BTN_TEXT_ID, btnLblId);
        intent.putExtra(AlertMsgActivity.INTENT_MSG_TEXT_ID, msgId);
        context.startActivity(intent);
    }

    public static final void startAlertMsgActivity(Activity activity, int msgId, int btnLblId, int requestCode) {
        Intent intent = new Intent(activity, AlertMsgActivity.class);
        intent.putExtra(AlertMsgActivity.INTENT_BTN_TEXT_ID, btnLblId);
        intent.putExtra(AlertMsgActivity.INTENT_MSG_TEXT_ID, msgId);
        activity.startActivityForResult(intent, requestCode);
    }

    public static final void checkAndPromptUpdate(Context activity, SharedPreferences mPref) {
        if (UpdateService.isUpdateAvailble(mPref)) {
            File file = new File(Consts.ELAH_DOWNLOAD_DIRECTORY + File.separator + Consts.ELAH_APK_FILENAME);
            int currentversion = 0;
            try {
                currentversion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (file.exists()) {
                PackageInfo info = activity.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0);

                if (info != null) {
                    if (info.versionCode > currentversion) {
                        Intent intent = new Intent(activity, AlertUpdateActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activity.startActivity(intent);
                    } else {
                        Intent service = new Intent(activity, UpdateService.class);
                        activity.startService(service);
                    }
                } else {
                    Intent service = new Intent(activity, UpdateService.class);
                    activity.startService(service);

                }
            }

        }
    }


    public static final String getAsElahDBNumFormat(String surveyValue) {
        if (surveyValue != null) {

            if (surveyValue.equals("0E-20")) {
                surveyValue = "";
            }
            if (surveyValue.trim().equals("")) {
                return "";
            }
            surveyValue = surveyValue.replace(",", ".");
            try {
                if (Double.valueOf(surveyValue) == 0) {
                    surveyValue = "";
                }

            } catch (NumberFormatException e) {
//				e.printStackTrace();
                return surveyValue = "";

            }

            NumberFormat format = NumberFormat.getInstance(Locale.UK);
            format.setMinimumFractionDigits(2);
            format.setMaximumFractionDigits(15);
            try {
                String number = format.format(Double.parseDouble(surveyValue));
                number = number.replace(",", "");

                surveyValue = number.replace(".", ",");
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
//				e.printStackTrace();
                surveyValue = "";
            }
            return surveyValue;
        }
        return "";
    }

    public static final String getComboBoxValue(String surveyValue) {

        if (surveyValue != null) {

            if (surveyValue.equals("0E-20")) {
                surveyValue = "";
            }
            if (surveyValue.trim().equals("")) {
                return "";
            }
            surveyValue = surveyValue.replace(",", ".");

            try {
                double valuedouble = (int) Double.parseDouble(surveyValue);
                if (valuedouble == 0) {
                    return "";
                }
                int value = (int) (Math.round(valuedouble / 10) * 10);
                if (value > 50) {
                    value = 50;
                }
                surveyValue = String.valueOf(value);

            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
//				e.printStackTrace();
                surveyValue = "";
            }
            return surveyValue;
        }
        return "";
    }

    public static final String getAsElahNumFormatForDetails(String surveyValue) {

        if (surveyValue != null) {

            if (surveyValue.equals("0E-20")) {
                surveyValue = "";
            }
            if (surveyValue.trim().equals("")) {
                return "0,00";
            }
            try {
                if (Double.valueOf(surveyValue) == 0) {
                    surveyValue = "0,00";
                }

            } catch (NumberFormatException e) {
                return surveyValue = "0,00";
            }
//			BigDecimal prezzo = new BigDecimal(surveyValue);
//			prezzo.stripTrailingZeros().toPlainString();
            NumberFormat format = NumberFormat.getInstance(Locale.UK);
            format.setMinimumFractionDigits(2);
            try {
                String number = format.format(Double.parseDouble(surveyValue));

                surveyValue = number.replace(".", ",");

            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
//				e.printStackTrace();
                surveyValue = "0,00";
            }
            return surveyValue;
        }
        return "0,00";
    }

    public static final String isNumberValid(String filtered) {
        try {
            if (!filtered.trim().equals("")) {
                filtered = filtered.replace(",", ".");
                Double number = Double.parseDouble(filtered);
                if (number != 0) {

                } else {
                    filtered = "";
                }
            }
        } catch (NumberFormatException e) {
            // TODO: handle exception
            e.printStackTrace();
            filtered = "";
        }
        return filtered;
    }

    public static boolean isStringValid(String value) {
        return !(TextUtils.isEmpty(value)
                || value.trim().toLowerCase().equals("undefined"));
    }

    public static final String filterDetails(String value) {
        if (TextUtils.isEmpty(value)
                || value.trim().toLowerCase(Locale.getDefault())
                .equals("undefined")) {
            return "-";
        } else
            return value;
    }

    public static final String filterUrlDetails(String value) {
        if (value == null || value.trim().equals("")
                || value.trim().toLowerCase().equals("undefined")) {
            return "";
        } else
            return value;
    }

    public static float parseNumber(String value) {
        float number = 0;
        try {
            number = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return number;
    }

    public static void setAssortSpinnerColor(View adpView, int position) {
        // TODO Auto-generated method stub
        switch (position) {
            case 0:
                adpView.setBackgroundResource(R.drawable.edit_box_tr_bk);
                break;
            case 1:
                adpView.setBackgroundResource(R.drawable.bk_assort_green);
                break;
            case 2:
                adpView.setBackgroundResource(R.drawable.bk_assort_red);
                break;
            case 3:
                adpView.setBackgroundResource(R.drawable.bk_assort_blue);
                break;
            case 4:
                adpView.setBackgroundResource(R.drawable.bk_assort_grey);
                break;
            default:
                break;
        }

    }

    public static void setPromoStatusColor(String status, View promoView) {
        if (status != null) {
            if (status.equals("1")) {
                promoView.setBackgroundResource(R.color.promo_active);
            } else if (status.equals("2")) {
                promoView.setBackgroundResource(R.color.promo_expire);
            } else if (status.equals("3")) {
                promoView.setBackgroundResource(R.color.promo_future);
            } else {
                promoView.setBackgroundResource(R.color.promo_inactive);
            }
        } else {
            promoView.setBackgroundResource(R.color.promo_inactive);
        }
    }

    public static String getItalianNum(String number) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ITALY);
        nf.setMinimumFractionDigits(2);
        String filtered = isNumberValid(number);
        if (!filtered.trim().equals("")) {
            String num = nf.format(Double.parseDouble(filtered));
            return num;
        }
        return "";
    }

    public static String convertToItalianCurrency(String value) {
        value = value.replace(",", ".");
        value = value.replace(".", "");
        return convertToItalianCurrency(Double.parseDouble(value));
    }

    public static String convertToItalianCurrency(double value) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.ITALIAN);
        String currencySymbol = formatter.getCurrency().getSymbol();
        String moneyString = formatter.format(value);
        return moneyString.replace(currencySymbol, "");
    }

    public static int getIndexOf(String[] array, String value) {
        value = Util.getComboBoxValue(value);
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static SurveyCustomer convertToCustomer(Survey survey) {

        String sentDateTime = survey.getSentDateTime();

        SurveyCustomer surveyCustomer = new SurveyCustomer();
        surveyCustomer.setCustomerCode(survey.getCustomerCode());
        surveyCustomer.setPostStatus(0);
        surveyCustomer.setSalesPersonCode(survey.getSalesPersonCode());
        surveyCustomer.setSurveyId(survey.getId());
        surveyCustomer.setSyncDate(Util.getDateFromDateTimeFormat(sentDateTime));
        surveyCustomer.setSurveyDate(sentDateTime);

        return surveyCustomer;
    }

    /**
     * Hide keyboard on touch of UI
     */
    public static void hideKeyboard(final Activity activity, View view) {

        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                hideKeyboard(activity, innerView);
            }
        }
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity, v);
                    return false;
                }

            });
        }

    }

    /**
     * Hide keyboard while focus is moved
     */
    public static void hideSoftKeyboard(Activity activity, View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                if (android.os.Build.VERSION.SDK_INT < 11) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                            0);
                } else {
                    if (activity.getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(activity
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    view.clearFocus();
                }
                view.clearFocus();
            }
        }
    }

    public static void refreshListRow(ListView list, Object target) {
        int start = list.getFirstVisiblePosition();
        for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
            if (target == list.getItemAtPosition(i)) {
                View view = list.getChildAt(i - start);
                list.getAdapter().getView(i, view, list);
                break;
            }
    }

    public static View getListRow(ListView list, int position) {
        int start = list.getFirstVisiblePosition();
        for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
            if (position == i) {
                View view = list.getChildAt(i - start);
                return list.getAdapter().getView(i, view, list);
            }

        return null;
    }

    public static String decimalFormat(double value){

        DecimalFormat df = new DecimalFormat("0.0");

        return df.format(value).replace('.',',');

    }




    /****
     * Compares if given two strings are equal
     * @param str1 String one
     * @param str2 String two
     * @return true if both strings are equal, else false
     * ******/
    public static boolean stringEquals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
