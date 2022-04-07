package com.eteam.dufour.newadpaters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eteam.utils.Util;

public class AssortAdapter extends ArrayAdapter<String> {


    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    private String[] objects;

    // ===========================================================
    // Constructors
    // ===========================================================
    public AssortAdapter(Context context, int textViewResourceId,
                         String[] objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        // TODO Auto-generated constructor stub
    }
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    @Override
    public View getDropDownView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = super.getDropDownView(position, convertView, parent);
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setGravity(Gravity.CENTER);
        if (position != 0)
            v.setText(objects[position].substring(0, 1));
       // Util.aaaaa(v, position);

        return v;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
