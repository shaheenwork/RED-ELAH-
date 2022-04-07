package com.eteam.dufour.customview;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class PannableEdit extends EditText{
	// ===========================================================
	// Constants
	// ===========================================================
	
	
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public PannableEdit(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PannableEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PannableEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================



	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event)
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        clearFocus();
	    }
	    return super.onKeyPreIme(keyCode, event);
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if(focused){
			postDelayed(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					setCursorVisible(true);
				}
			}, 300);
		}
		else{
			
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================
	private int stateToSave;


	 @Override
	  public Parcelable onSaveInstanceState() {
	    //begin boilerplate code that allows parent classes to save state
	    Parcelable superState = super.onSaveInstanceState();

	    SavedState ss = new SavedState(superState);
	    //end

	    ss.stateToSave = this.stateToSave;
	    return ss;
	  }

	  @Override
	  public void onRestoreInstanceState(Parcelable state) {
	    //begin boilerplate code so parent classes can restore state
	    if(!(state instanceof SavedState)) {
	      super.onRestoreInstanceState(state);
	      return;
	    }

	    SavedState ss = (SavedState)state;
	    super.onRestoreInstanceState(ss.getSuperState());
	    //end

	    this.stateToSave = ss.stateToSave;
	  }





	static class SavedState extends BaseSavedState {
	   int stateToSave;
	   int position;
	   
	    SavedState(Parcelable superState) {
	      super(superState);
	    }

	    private SavedState(Parcel in) {
	      super(in);
	      this.stateToSave = in.readInt();
	    }

	    @Override
	    public void writeToParcel(Parcel out, int flags) {
	      super.writeToParcel(out, flags);
	      out.writeInt(this.stateToSave);
	    }

	    //required field that makes Parcelables from a Parcel
	    public static final Parcelable.Creator<SavedState> CREATOR =
	        new Parcelable.Creator<SavedState>() {
	          public SavedState createFromParcel(Parcel in) {
	            return new SavedState(in);
	          }
	          public SavedState[] newArray(int size) {
	            return new SavedState[size];
	          }
	    };
	  }
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	
}
