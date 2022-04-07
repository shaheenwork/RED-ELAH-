package com.eteam.dufour.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class UnpressableLinearLayout extends LinearLayout
{


	public UnpressableLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public UnpressableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressLint("NewApi")
	public UnpressableLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
  
	@Override
    public void setPressed(boolean pressed)
    {
        // Do nothing here. Specifically, do not propagate this message along
        // to our children so they do not incorrectly display a pressed state
        // just because one of their ancestors got pressed.
    }
}