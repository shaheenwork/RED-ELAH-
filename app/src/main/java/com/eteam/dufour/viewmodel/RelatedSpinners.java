package com.eteam.dufour.viewmodel;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.view.View;

public class RelatedSpinners{
		public Spinner mPosLnOne;
		public Spinner mPosLnTwo;
		
		private OnRelatedSpinnerSelectListener listener;
		
		public void setListener(OnRelatedSpinnerSelectListener listener) {
			this.listener = listener;
		}
		
				
		public RelatedSpinners(Spinner posLnOne,Spinner posLnTwo) {
			this.mPosLnOne = posLnOne;
			this.mPosLnTwo = posLnTwo;
			
			posLnOne.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(listener!=null)
						listener.onPosLnOneSelected(mPosLnOne, mPosLnTwo, position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
			
			posLnTwo.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(listener!=null)
						listener.onPosLnTwoSelected(mPosLnOne, mPosLnTwo, position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		public interface OnRelatedSpinnerSelectListener{
			public void onPosLnOneSelected(Spinner posLnOne,Spinner posLnTwo,int position);
			public void onPosLnTwoSelected(Spinner posLnOne,Spinner posLnTwo,int position);
		}
	}