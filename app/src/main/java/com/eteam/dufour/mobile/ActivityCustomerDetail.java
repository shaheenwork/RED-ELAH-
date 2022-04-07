package com.eteam.dufour.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.database.tables.TableCustomers;
import com.eteam.dufour.database.tables.TableCustomers.Address;
import com.eteam.dufour.database.tables.TableCustomers.Customer;
import com.eteam.utils.Util;

public class ActivityCustomerDetail extends Activity{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String EXTRA_CUSTOMER_CODE = "com.eteam.dufour.mobile.ActivityCustomerDetail.EXTRA_CUSTOMER_CODE";
	// ===========================================================
	// Fields
	// ===========================================================
	private String mCustomerCode;
	private Toast mToast;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public static void startActivity(Context context,String customerCode){
		Intent intent = new Intent(context
				,ActivityCustomerDetail.class);
		intent.putExtra(EXTRA_CUSTOMER_CODE, customerCode);
		context.startActivity(intent);
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_customer_info);
		initialize();
		setCustomerCodeFromIntent(savedInstanceState);
		populateView(mCustomerCode);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_CUSTOMER_CODE, mCustomerCode);
		super.onSaveInstanceState(outState);
	}




	private void initialize() {
		mToast = Toast.makeText(this, "sample", Toast.LENGTH_SHORT);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	private void setCustomerCodeFromIntent(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			mCustomerCode = savedInstanceState.getString(EXTRA_CUSTOMER_CODE);
		}
		else{
			mCustomerCode = getIntent().getStringExtra(EXTRA_CUSTOMER_CODE);
		}
	}
	
	private void populateView(String customerCode) {
				
		Customer customer = TableCustomers.getCustomer(customerCode);
		if(customer!=null){
			TextView mLblCustDetails = (TextView) findViewById(R.id.lbl_cust_details);
				
			TextView mFieldName = (TextView) findViewById(R.id.field_ragione_sociale);
			TextView mFieldAddress = (TextView) findViewById(R.id.field_indirizzo);
			TextView mFieldCustCity = (TextView) findViewById(R.id.field_citta);
			TextView mFieldCustCap = (TextView) findViewById(R.id.field_cap);
			TextView mFieldCustCounty = (TextView) findViewById(R.id.field_pv);
			TextView mFieldRegNo = (TextView) findViewById(R.id.field_partita_iva);
			TextView mFieldClientRef = (TextView) findViewById(R.id.field_riferimento);
			TextView mFieldBillName = (TextView) findViewById(R.id.field_fat_ragione_sociale);
			TextView mFieldCustTel = (TextView) findViewById(R.id.field_telefono);
			TextView mFieldCustFax = (TextView) findViewById(R.id.field_fax);
			TextView mFieldCustEmail = (TextView) findViewById(R.id.field_email);
			TextView mFieldBillAddress = (TextView) findViewById(R.id.field_fat_indirizzo);
			
			TextView mFieldBillCity = (TextView) findViewById(R.id.field_fat_citta);
			TextView mFieldBillPost = (TextView) findViewById(R.id.field_fat_cap);
			TextView mFieldBillCounty = (TextView) findViewById(R.id.field_fat_pv);
			TextView mFieldBillRef = (TextView) findViewById(R.id.field_fat_riferimento);
			TextView mFieldBillVat = (TextView) findViewById(R.id.field_fat_partita_iva);
			
			Address address = customer.getAddress();
			
			mLblCustDetails.setText(mCustomerCode+" - "+customer.getName());
			if(Util.isStringValid(mCustomerCode)){		
				mFieldName.setText("("+mCustomerCode+") "+customer.getName());
			}
			else{
				mFieldName.setText("-");
			}
			mFieldAddress.setText(address.getAddress());
			mFieldCustCity.setText(address.getCity());
			mFieldCustCap.setText(address.getCap());
			mFieldCustCounty.setText(address.getCounty());
			mFieldCustTel.setText(address.getTelephone());
			mFieldCustFax.setText(address.getFax());
			mFieldCustEmail.setText(address.getEmail());
			
			mFieldRegNo.setText(customer.getRegNo());
			mFieldClientRef.setText(customer.getClientRef());
			mFieldBillName.setText(customer.getBillName());
			mFieldBillAddress.setText(customer.getBillAdress());
			mFieldBillCity.setText(customer.getBillCity());
			mFieldBillCounty.setText(customer.getBillCounty());
			mFieldBillPost.setText(customer.getBillPost());
			mFieldBillRef.setText(customer.getBillRef());
			mFieldBillVat.setText(customer.getBillVat());
			
			View btnBack	= findViewById(R.id.btn_chiudi);
			btnBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			
		}
		else{
			Util.showToast(mToast, R.string.msg_no_customer_details);
		}
		
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
