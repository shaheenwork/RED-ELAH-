package com.eteam.dufour.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class ElahRadioButton extends RadioButton{

	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	  private float mX;
	  private Paint paint;
	  private Rect rect = new Rect();
	  private Paint textPaint;
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ElahRadioButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public ElahRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public ElahRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	  
	    @Override
	    public void onDraw(Canvas canvas) {
	    	super.onDraw(canvas);
	        String text = this.getText().toString();
	        
	        float currentHeight = textPaint.measureText("x");
	 
	        // final float scale =
	        // getContext().getResources().getDisplayMetrics().density;
	        // float textSize = (int) (TEXT_SIZE * scale + 0.5f);
	        textPaint.setTextSize(this.getTextSize());
	        textPaint.setTextAlign(Paint.Align.CENTER);
	 
//	        float canvasWidth = canvas.getWidth();
//	        float textWidth = textPaint.measureText(text);
	 
	        if (isChecked()) {
//	            GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xffdcdcdc, 0xff111111 });
//	            grad.setBounds(0, 0, this.getWidth(), this.getHeight());
//	            grad.draw(canvas);
	            textPaint.setColor(Color.WHITE);
	        } else {
//	            GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xffa5a5a5, 0xff000000 });
//	            grad.setBounds(0, 0, this.getWidth(), this.getHeight());
//	            grad.draw(canvas);
	            textPaint.setColor(0xffcccccc);
	        }
	 
	        float h = (this.getHeight() / 2) + currentHeight;
	        canvas.drawText(text, mX, h, textPaint);
	 
	        
	        canvas.drawRect(rect, paint);
	 
	    }
	 
	    @Override
	    protected void onSizeChanged(int w, int h, int ow, int oh) {
	        super.onSizeChanged(w, h, ow, oh);
	        
	        mX = w * 0.5f; // remember the center of the screen
	    }
	    
	    @Override
	    protected void onLayout(boolean changed, int left, int top, int right,
	    		int bottom) {
	    	// TODO Auto-generated method stub
	    	super.onLayout(changed, left, top, right, bottom);
	        if(changed)
	        	rect.set(left, top, right, bottom);
	    }
	// ===========================================================
	// Methods
	// ===========================================================
	    
	    private void init(){
	    	paint = new Paint();
	        paint.setColor(Color.BLACK);
	        paint.setStyle(Style.STROKE);
	        
	        textPaint = new Paint();
	        textPaint.setAntiAlias(true);
	    }
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
