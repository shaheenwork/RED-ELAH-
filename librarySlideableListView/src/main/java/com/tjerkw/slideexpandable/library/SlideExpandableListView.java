package com.tjerkw.slideexpandable.library;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter.OnExpandClickListener;
import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter.OnViewExpandedListener;

/**
 * Simple subclass of listview which does nothing more than wrap
 * any ListAdapter in a SlideExpandalbeListAdapter
 */
public class SlideExpandableListView extends ListView implements OnViewExpandedListener {
	private SlideExpandableListAdapter adapter;
	private OnExpandClickListener mClickListener;
	private OnLayoutChangeListener layoutChangeListener;
	
	public SlideExpandableListView(Context context) {
		super(context);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Collapses the currently open view.
	 *
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
		if(adapter!=null) {
				return adapter.collapseLastOpen();
		}
		return false;
	}

	public void setAdapter(ListAdapter adapter) {
			this.adapter = new SlideExpandableListAdapter(adapter);
			this.adapter.setViewExpanded(this);
			super.setAdapter(this.adapter);
	}
	
	public void setLayoutChangeListener(
			OnLayoutChangeListener layoutChangeListener) {
		this.layoutChangeListener = layoutChangeListener;
	}

//	/**
//	 * Registers a OnItemClickListener for this listview which will
//	 * expand the item by default. Any other OnItemClickListener will be overriden.
//	 *
//	 * To undo call setOnItemClickListener(null)
//	 *
//	 * Important: This method call setOnItemClickListener, so the value will be reset
//	 */
//	public void enableExpandOnItemClick() {
//		this.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//				SlideExpandableListAdapter adapter = (SlideExpandableListAdapter)getAdapter();
//				adapter.getExpandToggleButton(view).performClick();
//			}
//		});
//	}


	@Override
	public Parcelable onSaveInstanceState() {
		if(adapter!=null){
			return adapter.onSaveInstanceState(super.onSaveInstanceState());
		}
		else{
			return super.onSaveInstanceState();
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if(adapter!=null){
			if(!(state instanceof AbstractSlideExpandableListAdapter.SavedState)) {
				super.onRestoreInstanceState(state);
				return;
			}
	
			AbstractSlideExpandableListAdapter.SavedState ss = (AbstractSlideExpandableListAdapter.SavedState)state;
			super.onRestoreInstanceState(ss.getSuperState());
	
			adapter.onRestoreInstanceState(ss);
		}
		else{
			super.onRestoreInstanceState(state);
		}
		
	}
	
	public int getLastOpenedPosition(){
		if(adapter!=null)
			return adapter.getLastOpenPosition();
		else
			return -1;
	}

	public OnExpandClickListener getClickListener() {
		return mClickListener;
	}

	public void setOnExpandListener(OnExpandClickListener mClickListener) {
		if(adapter!=null)
			this.adapter.setOnItemClickListener(mClickListener);
	}
	
	public void enableItemClick(){
		if(adapter!=null)
			this.adapter.enableItemClick();
	}
	
	public void disableItemClick(){
		if(adapter!=null)
			this.adapter.disableItemClick();
	}
	
	public void openAllViews(){
		if(adapter!=null)
			this.adapter.openAllViews();
	}
	
	public void restoreToExpandableList(){
		if(adapter!=null)
			this.adapter.restoreToExpandableList();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed){
			if(layoutChangeListener!=null)
				layoutChangeListener.beforeLayoutChanged();
		}
		super.onLayout(changed, l, t, r, b);
		if(changed){
			if(layoutChangeListener!=null)
				layoutChangeListener.afterLayoutChanged();
		}
	}

	@Override
	public void onViewExpanded(View target,int position) {
		// TODO Auto-generated method stub
		Rect scrollBounds = new Rect();
		this.getHitRect(scrollBounds);
		if (target.getLocalVisibleRect(scrollBounds)) {
		    Log.d("Log", "Target is visible");
		} else {
			smoothScrollToPosition(position);
		}
	}
	
	public interface OnLayoutChangeListener{
		public void beforeLayoutChanged();
		public void afterLayoutChanged();
	}
}