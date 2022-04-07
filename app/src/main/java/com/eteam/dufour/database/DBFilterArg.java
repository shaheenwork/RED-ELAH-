package com.eteam.dufour.database;

import java.util.ArrayList;

public class DBFilterArg {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	private String selection;
	private ArrayList<String> selectionArg;
	// ===========================================================
	// Constructors
	// ===========================================================

	public DBFilterArg(String selection,ArrayList<String> selectionArg) {
		this.selection = selection;
		this.selectionArg = selectionArg;
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getSelection() {
		return selection;
	}
	
	public ArrayList<String> getSelectionArg() {
		return selectionArg;
	}
	
	public String[] getSelectionArgAsArray() {
		String[] array = new String[selectionArg.size()]; 
		selectionArg.toArray(array);
		return array;
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
