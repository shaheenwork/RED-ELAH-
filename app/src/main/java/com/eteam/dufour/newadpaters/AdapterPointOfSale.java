package com.eteam.dufour.newadpaters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.eteam.dufour.mobile.R;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class AdapterPointOfSale extends SimpleCursorAdapter {


	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	private OnClickListener infoClickListener;
	// ===========================================================
	// Constructors
	// ===========================================================
	@SuppressWarnings("deprecation")
	public AdapterPointOfSale(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public OnClickListener getInfoClickListener() {
		return infoClickListener;
	}

	public void setInfoClickListener(OnClickListener infoClickListener) {
		this.infoClickListener = infoClickListener;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		View root = super.getView(position, view, parent);
		View infoBtn = root.findViewById(R.id.btn_info);
		infoBtn.setOnClickListener(infoClickListener);
		return root;
	}
	// ===========================================================
	// Methods
	// ===========================================================



	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
