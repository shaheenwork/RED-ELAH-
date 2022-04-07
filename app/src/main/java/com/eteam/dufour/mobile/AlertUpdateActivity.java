package com.eteam.dufour.mobile;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class AlertUpdateActivity extends Activity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private Button mBtnChiudi;
	private TextView mTextView;
	// ===========================================================
	// Constructors
	// ===========================================================

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
		setContentView(R.layout.nonet);
		
		mTextView = (TextView) findViewById(R.id.field_msg);
		mTextView.setText(R.string.msg_app_update_available);
		
		mBtnChiudi = (Button) findViewById(R.id.btn_chiudi);
		mBtnChiudi.setText(android.R.string.ok);
		mBtnChiudi.setOnClickListener(this);
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mBtnChiudi){

			File file = new File(Consts.ELAH_DOWNLOAD_DIRECTORY
					+File.separator+Consts.ELAH_APK_FILENAME);
			if(file.exists()){
				if(Util.installApplication(this, Consts.ELAH_DOWNLOAD_DIRECTORY+File.separator
						+Consts.ELAH_APK_FILENAME)){
					return;
				}
			}
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
