package com.eteam.dufour.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.eteam.dufour.mobile.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentTab extends Fragment implements OnTabChangeListener{
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final String TAB_PRODOTTI = "prodotti";
	private static final String TAB_PROMOZIONI = "promozioni";
	private static final String TAB_CONCORRENZA = "concorrenza";
	private static final String TAB_CHIUSURA = "chiusura";
	
	
	// ===========================================================
	// Fields
	// ===========================================================
	private TabHost mTabHost;
	
	
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_survey, null);
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		setupTabs();
		mTabHost.setOnTabChangedListener(this);
		return view;
	}



	public void onTabChanged(String tabId) {
		if(tabId.equals(TAB_PRODOTTI)){
			updateTab(TAB_PRODOTTI, new FragmentProddoti(), R.id.tab_prodotti);
		}
		if(tabId.equals(TAB_PROMOZIONI)){
		}
		if(tabId.equals(TAB_CONCORRENZA)){
		}
		if(tabId.equals(TAB_CHIUSURA)){
		}
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	 private void setupTabs() {
	        mTabHost.setup(); // you must call this before adding your tabs!
	        
	        mTabHost.addTab(newTab(TAB_PRODOTTI, R.string.prodotti, R.id.tab_prodotti));
	        mTabHost.addTab(newTab(TAB_PROMOZIONI, R.string.promozioni, R.id.tab_promozioni));
	        mTabHost.addTab(newTab(TAB_CONCORRENZA, R.string.concorrenza, R.id.tab_concorrenza));
	        mTabHost.addTab(newTab(TAB_CHIUSURA, R.string.chiusura_attivita, R.id.tab_chiusura_attivita));
	 }
	 
	 private TabSpec newTab(String tag, int labelId, int tabContentId) {
	        Log.d("Log", "buildTab(): tag=" + tag);
	 
//	        View indicator = LayoutInflater.from(getActivity()).inflate(
//	                R.layout.tab,
//	                (ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
//	        ((TextView) indicator.findViewById(R.id.text)).setText(labelId);
	 
	        TabSpec tabSpec = mTabHost.newTabSpec(tag);
	        tabSpec.setIndicator(getResources().getString(labelId));
	        tabSpec.setContent(tabContentId);
	        return tabSpec;
	    }
	 
	    private void updateTab(String tabId,Fragment fragment,int placeholder) {
	        FragmentManager fm = getFragmentManager();
	        if (fm.findFragmentByTag(tabId) == null) {
	            fm.beginTransaction()
	                    .replace(placeholder,fragment ,tabId)
	                    .commit();
	        }
	    }
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
