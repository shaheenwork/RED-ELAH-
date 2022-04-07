package com.tjerkw.slideexpandable.library;

import java.util.BitSet;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

/**
 * Wraps a ListAdapter to give it expandable list view functionality.
 * The main thing it does is add a listener to the getToggleButton
 * which expands the getExpandableView for each list item.
 *
 * @author tjerk
 * @date 6/9/12 4:41 PM
 */
public abstract class AbstractSlideExpandableListAdapter extends WrapperListAdapterImpl {
	/**
	 * Reference to the last expanded list item.
	 * Since lists are recycled this might be null if
	 * though there is an expanded list item
	 */
	private View lastOpen = null;
	
	/**
	 * The position of the last expanded list item.
	 * If -1 there is no list item expanded.
	 * Otherwise it points to the position of the last expanded list item
	 */
	private int lastOpenPosition = -1;
	/**
	 * A list of positions of all list items that are expanded.
	 * Normally only one is expanded. But a mode to expand
	 * multiple will be added soon.
	 *
	 * If an item onj position x is open, its bit is set
	 */
	private BitSet openItems = new BitSet();
	/**
	 * We remember, for each collapsable view its height.
	 * So we dont need to recalculate.
	 * The height is calculated just before the view is drawn.
	 */
	
	private boolean itemClick = true;
	private boolean openAllViews = false;
	
	private final SparseIntArray viewHeights = new SparseIntArray(10);
	
	private OnExpandClickListener mItemClickListener;
	
	private OnViewExpandedListener mViewExpanded;
	

	public AbstractSlideExpandableListAdapter(ListAdapter wrapped) {
		super(wrapped);
	}
	
	public void setOnItemClickListener(OnExpandClickListener clickListener){
		this.mItemClickListener = clickListener;
	}
	
	public OnExpandClickListener getItemClickListener(){
		return mItemClickListener;
	}
	

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = wrapped.getView(position, view, viewGroup);
		enableFor(view, position,viewGroup);
		
		return view;
	}

	/**
	 * This method is used to get the Button view that should
	 * expand or collapse the Expandable View.
	 * <br/>
	 * Normally it will be implemented as:
	 * <pre>
	 * return parent.findViewById(R.id.expand_toggle_button)
	 * </pre>
	 *
	 * A listener will be attached to the button which will
	 * either expand or collapse the expandable view
	 *
	 * @see #getExpandableView(View)
	 * @param parent the list view item
	 * @ensure return!=null
	 * @return a child of parent which is a button
	 */
	public abstract View getExpandToggleButton(View parent);

	/**
	 * This method is used to get the view that will be hidden
	 * initially and expands or collapse when the ExpandToggleButton
	 * is pressed @see getExpandToggleButton
	 * <br/>
	 * Normally it will be implemented as:
	 * <pre>
	 * return parent.findViewById(R.id.expandable)
	 * </pre>
	 *
	 * @see #getExpandToggleButton(View)
	 * @param parent the list view item
	 * @ensure return!=null
	 * @return a child of parent which is a view (or often ViewGroup)
	 *  that can be collapsed and expanded
	 */
	public abstract View getExpandableView(View parent);

	/**
	 * Gets the duration of the collapse animation in ms.
	 * Default is 330ms. Override this method to change the default.
	 *
	 * @return the duration of the anim in ms
	 */
	protected int getAnimationDuration() {
		return 330;
	}
	

	public OnViewExpandedListener getViewExpanded() {
		return mViewExpanded;
	}

	public void setViewExpanded(OnViewExpandedListener mViewExpanded) {
		this.mViewExpanded = mViewExpanded;
	}

	public int getLastOpenPosition() {
		return lastOpenPosition;
	}

//	public void setLastOpenPosition(int lastOpenPosition) {
//		this.lastOpenPosition = lastOpenPosition;
//	}

	public void enableFor(View parent, int position,ViewGroup viewgroup) {
//		View more = getExpandToggleButton(parent);
		View itemToolbar = getExpandableView(parent);
		itemToolbar.measure(parent.getWidth(), parent.getHeight());
		enableFor(parent, itemToolbar, position,viewgroup);
	}


	private void enableFor(final View button, final View target, final int position,final ViewGroup group) {
			
			if(target == lastOpen && position!=lastOpenPosition) {
				// lastOpen is recycled, so its reference is false
				lastOpen = null;
			}
			if(position == lastOpenPosition) {
				// re reference to the last view
				// so when can animate it when collapsed
				if(mItemClickListener!=null&&lastOpen==null)
					mItemClickListener.onItemExpand(button, position, group);
				lastOpen = target;
				
			}
			int height = viewHeights.get(position, -1);
			if(height == -1) {
				viewHeights.put(position, target.getMeasuredHeight());
				updateExpandable(target,position);
			} else {
				updateExpandable(target, position);
			}
			
			if(itemClick){
				button.findViewById(R.id.item).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if(itemClick){
							view.setAnimation(null);
							// check wether we need to expand or collapse
							int type =
								target.getVisibility() == View.VISIBLE
								? ExpandCollapseAnimation.COLLAPSE
								: ExpandCollapseAnimation.EXPAND;
			
							// remember the state
							if(type == ExpandCollapseAnimation.EXPAND) {
								openItems.set(position, true);
							} else {
								openItems.set(position, false);
							}
							// check if we need to collapse a different view
							if(type == ExpandCollapseAnimation.EXPAND) {
								if(lastOpenPosition != -1 && lastOpenPosition != position) {
									if(lastOpen!=null) {
										animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE,position);
									}
									openItems.set(lastOpenPosition, false);
								}
								lastOpen = target;
								lastOpenPosition = position;
								if(mItemClickListener!=null)
									mItemClickListener.onItemExpand(button, position, group);
							} else if(lastOpenPosition == position) {
								if(mItemClickListener!=null)
									mItemClickListener.onItemCollapsed(button, position, group);
								lastOpenPosition = -1;
							}
			
							animateView(target, type,position);
							
							if(position==getCount()-1){
			//					ActionSlideExpandableListView list = (ActionSlideExpandableListView) group;
			//					list.scrollBy(0, target.getHeight());
								
							}
							
						}
					}
				});
			}
			else{
				button.setOnClickListener(null);
			}
		
		
	}

	private void updateExpandable(View target, int position) {
		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)target.getLayoutParams();
		if(!openAllViews){
			
			if(openItems.get(position)) {
				target.setVisibility(View.VISIBLE);
				params.bottomMargin = 0;
			} else {
				target.setVisibility(View.GONE);
				params.bottomMargin = 0-viewHeights.get(position);
			}
		}
		else{
			
			target.setVisibility(View.VISIBLE);
			params.bottomMargin = 0;
		}
	}
	

	/**
	 * Performs either COLLAPSE or EXPAND animation on the target view
	 * @param target the view to animate
	 * @param type the animation type, either ExpandCollapseAnimation.COLLAPSE
	 *			 or ExpandCollapseAnimation.EXPAND
	 */
	private void animateView(final View target, final int type,final int position) {
		Animation anim = new ExpandCollapseAnimation(
				target,
				type
		);
		anim.setDuration(getAnimationDuration());
		target.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if(mViewExpanded!=null&&type==ExpandCollapseAnimation.EXPAND)
					mViewExpanded.onViewExpanded(target,position);
			}
		});
	}
	

	/**
	 * Closes the current open item.
	 * If it is current visible it will be closed with an animation.
	 *
	 * @return true if an item was closed, false otherwise
	 */
	public boolean collapseLastOpen() {
		if(!openAllViews){
			if(lastOpenPosition != -1) {
				// if visible animate it out
				if(lastOpen != null) {
					animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE,-1);
				}
				openItems.set(lastOpenPosition, false);
				lastOpenPosition = -1;
				return true;
			}
		}
		return false;
	}

	public Parcelable onSaveInstanceState(Parcelable parcelable) {

		SavedState ss = new SavedState(parcelable);
		ss.lastOpenPosition = this.lastOpenPosition;
		ss.openItems = this.openItems;
		return ss;
	}

	public void onRestoreInstanceState(SavedState state) {

		this.lastOpenPosition = state.lastOpenPosition;
		this.openItems = state.openItems;
	}

	/**
	 * Utility methods to read and write a bitset from and to a Parcel
	 */
	private static BitSet readBitSet(Parcel src) {
		int cardinality = src.readInt();

		BitSet set = new BitSet();
		for (int i = 0; i < cardinality; i++) {
			set.set(src.readInt());
		}

		return set;
	}

	private static void writeBitSet(Parcel dest, BitSet set) {
		int nextSetBit = -1;

		dest.writeInt(set.cardinality());

		while ((nextSetBit = set.nextSetBit(nextSetBit + 1)) != -1) {
			dest.writeInt(nextSetBit);
		}
	}


	/**
	 * The actual state class
	 */
	static class SavedState extends View.BaseSavedState {
		public BitSet openItems = null;
		public int lastOpenPosition = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			lastOpenPosition = in.readInt();
			openItems = readBitSet(in);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			 out.writeInt(lastOpenPosition);
			 writeBitSet(out, openItems);
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
	
	public interface OnExpandClickListener{
		public void onItemExpand(View parent, int position,ViewGroup viewgroup);
		public void onItemCollapsed(View parent, int position,ViewGroup viewgroup);
	}
	
	public interface OnViewExpandedListener{
		public void onViewExpanded(View target,int position);
	}
	
	public void enableItemClick(){
		itemClick = true;
	}
	
	public void disableItemClick(){
		itemClick = false;
	}
	
	public void openAllViews(){
		openAllViews = true;
		itemClick = false;
	}
	
	public void restoreToExpandableList(){
		openAllViews = false;
		itemClick = true;
	}
}
