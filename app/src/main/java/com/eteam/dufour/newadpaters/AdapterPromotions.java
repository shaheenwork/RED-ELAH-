package com.eteam.dufour.newadpaters;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.dufour.database.tables.TablePromotions.Promotion;
import com.eteam.dufour.database.tables.TableSurveyPromotions;
import com.eteam.dufour.database.tables.TableSurveyPromotions.SurveyPromotion;
import com.eteam.dufour.database.tables.TableSurveyPromotionsItem.SurveyPromotionItem;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.mobile.R.color;
import com.eteam.dufour.viewmodel.ModelSurveyPromotionItems;
import com.eteam.dufour.viewmodel.ModelSurveyPromotions;
import com.eteam.utils.Consts;
//import android.util.Log;

public class AdapterPromotions extends BaseExpandableListAdapter{


	// ===========================================================
	// Constants
	// ===========================================================
	public final static int NON_SELECTED = -1;
		
	private static final int KEY_POSITION = R.id.position;
	private static final int KEY_CHILD_POSITION = R.id.childposition;
	private static final int KEY_ITEM_CODE = R.id.itemcode;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private ArrayList<ModelSurveyPromotions> surveys;
	
	private ArrayAdapter<String> mAdapterVisibility;
	
	private OnPromotionSurveyActionListener listener;
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public AdapterPromotions(Context context,ArrayList<ModelSurveyPromotions> surveys) {
		this.surveys = surveys;
		mAdapterVisibility = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Consts.ARRAY_VISIBILITY);
		mAdapterVisibility.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	public AdapterPromotions(Context context) {
		this(context,new ArrayList<ModelSurveyPromotions>());
	}
	
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return surveys.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return surveys.get(groupPosition).getSurvey().getItems().size();
		
	}

	@Override
	public ModelSurveyPromotions getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return surveys.get(groupPosition);
	}
	
	
	

	@Override
	public ModelSurveyPromotionItems getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return surveys.get(groupPosition).getSurvey().getItems().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return surveys.get(groupPosition).hashCode();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return surveys.get(groupPosition).getSurvey().getItems().get(childPosition).hashCode();
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	
//	new String[]{TablePromotions.COLUMN_PROMO_DESC,TablePromotions.COLUMN_PROMO_CODE,TablePromotions.COLUMN_SELL_OUT_INIZIO
//	,TablePromotions.COLUMN_SELL_OUT_FINE,TablePromotions.COLUMN_ID}, 
//	new int[]{R.id.promotion_name, R.id.promotion_code, R.id.promotion_startingdate, R.id.promotion_endingdate

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder holder;
		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.row_promizioni, parent,false);
			
			holder = new GroupViewHolder();
			
			holder.fieldPromotionName = (TextView) convertView.findViewById(R.id.promotion_name);
			holder.fieldPromotionCode = (TextView) convertView.findViewById(R.id.promotion_code);
			holder.fieldStartingDate  = (TextView) convertView.findViewById(R.id.promotion_startingdate);
			holder.fieldEndingDate	  = (TextView) convertView.findViewById(R.id.promotion_endingdate);
			
			holder.layoutVisibility	  = convertView.findViewById(R.id.expandable);
			
			holder.itemBg			  = convertView.findViewById(R.id.item);
			
			holder.spinnerVisibility  = (Spinner) convertView.findViewById(R.id.promovisibility);
			holder.spinnerVisibility.setAdapter(mAdapterVisibility);
			
			holder.spinnerVisibility.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					Integer groupPosition = (Integer) parent.getTag(KEY_POSITION);
					
											
					if(groupPosition!=null&&groupPosition<getGroupCount()){
						SurveyPromotion surveyPromotion 	= surveys.get(groupPosition).getSurvey();
						surveyPromotion.setVisibility(position);	
						notifyDataSetChanged();
					}
				}
					

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
			
			holder.btnInfo = convertView.findViewById(R.id.btn_info);
			holder.btnInfo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Integer position  = (Integer) v.getTag(KEY_POSITION);
					if(position==null)
						return;
					String itemCode = (String) v.getTag(KEY_ITEM_CODE);
					ModelSurveyPromotions model = surveys.get(position);
					SurveyPromotion survey = model.getSurvey();
					if(!survey.getPromoCode().equals(itemCode)){
						return;
					}
					if(listener!=null)
						listener.onInfoButtonClicked(model);
				}
			});
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					GroupViewHolder holder = (GroupViewHolder) v.getTag();
					if(listener!=null){
						listener.onClickView(holder.position);
					}
				}
			});
			
			convertView.setTag(holder);
			
		}
		else{
			holder = (GroupViewHolder) convertView.getTag();
		}
		
		ModelSurveyPromotions model = surveys.get(groupPosition);
		holder.position = groupPosition;
		
		Promotion	promotion = model.getPromotion();
		SurveyPromotion survey = model.getSurvey();
		holder.fieldPromotionName.setText(promotion.getPromoDesc());
		holder.fieldPromotionCode.setText(promotion.getPromoCode());
		holder.fieldStartingDate.setText(promotion.getSellOutInzio());
		holder.fieldEndingDate.setText(promotion.getSellOutFine());
		
		
		holder.layoutVisibility.setVisibility(isExpanded?View.VISIBLE:View.GONE);
		
		holder.spinnerVisibility.setSelection(survey.getVisibility());
		
		holder.spinnerVisibility.setTag(KEY_POSITION,groupPosition);
	
		holder.btnInfo.setTag(KEY_POSITION,groupPosition);
		holder.btnInfo.setTag(KEY_ITEM_CODE,survey.getPromoCode());
		
		if(survey.getId()==0||survey.getId()==-1){
			holder.itemBg.setBackgroundResource(color.item_unselected);
		}
		else{
			holder.itemBg.setBackgroundResource(color.item_selected);
		}
		
		
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
	
		ChildViewHolder holder;
		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.itemdetail_new, parent,false);
			holder = new ChildViewHolder();
			
			holder.fieldNo			= (TextView) convertView.findViewById(R.id.field_number);
			holder.fieldDescription = (TextView) convertView.findViewById(R.id.field_description);
			
			holder.spinnerVolantino = (Spinner) convertView.findViewById(R.id.field_volantino);
			holder.spinnerTaglioPz	= (Spinner) convertView.findViewById(R.id.field_taglio_pz);
			
			holder.spinnerVolantino.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					Integer groupPosition = (Integer) parent.getTag(KEY_POSITION);
					Integer childPosition = (Integer) parent.getTag(KEY_CHILD_POSITION);
					
					if(groupPosition!=null&&childPosition!=null){
						
						if(childPosition<getChildrenCount(groupPosition)){
							SurveyPromotion surveyPromotion 	= surveys.get(groupPosition).getSurvey();
							SurveyPromotionItem promotionItem 	= surveyPromotion.getItems().get(childPosition).getSurvey();
							
							String itemCode = (String) parent.getTag(KEY_ITEM_CODE);
							if(TextUtils.isEmpty(itemCode)||!itemCode.equals(promotionItem.getItemCode()))
								return;
							
							if(surveyPromotion.getId()==0
									||surveyPromotion.getId()==-1){
								TableSurveyPromotions.createNew(surveyPromotion);
	//							Log.i(TAG, "New survey promotion is created : "+surveyPromotion.getId());
							}
	//						Log.i(TAG, "Updating survey promotion : "+surveyPromotion.getId()+" with volantino : "+position);
							promotionItem.setVolantino(surveyPromotion.getId(), position);
							if(!surveyPromotion.isValid()){
								TableSurveyPromotions.delete(surveyPromotion);
							}
							notifyDataSetChanged();
						}
					}
						
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			
			});
			
			holder.spinnerTaglioPz.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					Integer groupPosition = (Integer) parent.getTag(KEY_POSITION);
					Integer childPosition = (Integer) parent.getTag(KEY_CHILD_POSITION);
															
					if(groupPosition != null && childPosition<getChildrenCount(groupPosition)){
						
						
						SurveyPromotion surveyPromotion 	= surveys.get(groupPosition).getSurvey();
						SurveyPromotionItem promotionItem 	= surveyPromotion.getItems().get(childPosition).getSurvey();
						
						String itemCode = (String) parent.getTag(KEY_ITEM_CODE);
						if(TextUtils.isEmpty(itemCode)||!itemCode.equals(promotionItem.getItemCode()))
							return;

						if(surveyPromotion.getId()==0
								||surveyPromotion.getId()==-1){							
							TableSurveyPromotions.createNew(surveyPromotion);
//							Log.i(TAG, "New survey promotion is created : "+surveyPromotion.getId());
						}
//						Log.i(TAG, "Updating survey promotion : "+surveyPromotion.getId()+" with prezzo : "+position);						
						promotionItem.setPrezzo(surveyPromotion.getId(), position);
						
						if(!surveyPromotion.isValid()){
							TableSurveyPromotions.delete(surveyPromotion);
							
						}
						notifyDataSetChanged();
					}
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
			
			holder.layoutMinimise = convertView.findViewById(R.id.layoutMinimise);
			
			holder.btnMinimise = convertView.findViewById(R.id.btn_minimise);
			
			holder.btnMinimise.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(listener!=null)
						listener.onMinimiseClicked();
				}
			});
			
			holder.btnInfo = convertView.findViewById(R.id.btn_info);
			holder.btnInfo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Integer groupPosition = (Integer) v.getTag(KEY_POSITION);
					Integer childPosition = (Integer) v.getTag(KEY_CHILD_POSITION);
															
					if(groupPosition != null && childPosition != null&&childPosition<getChildrenCount(groupPosition)){
						
						
						SurveyPromotion surveyPromotion 	= surveys.get(groupPosition).getSurvey();
						ModelSurveyPromotionItems itemModel 	= surveyPromotion.getItems().get(childPosition);
						
						String itemCode = (String) v.getTag(KEY_ITEM_CODE);
						if(TextUtils.isEmpty(itemCode)||!itemCode.equals(itemModel.getItem().getCode()))
							return;
						if(listener!=null)
							listener.OnInfoButtonClicked(itemModel);
					}
					
				}
			});
			
			convertView.setTag(holder);
			
		}
		else{
			
			holder = (ChildViewHolder) convertView.getTag();
		}
		SurveyPromotion surveyPromotions = surveys.get(groupPosition).getSurvey();
		
		ModelSurveyPromotionItems model = surveyPromotions.getItems().get(childPosition);
		Item item 						= model.getItem();
		SurveyPromotionItem  survey		= model.getSurvey();
		
		holder.spinnerVolantino.setTag(KEY_POSITION, groupPosition);
		holder.spinnerVolantino.setTag(KEY_CHILD_POSITION, childPosition);
		holder.spinnerVolantino.setTag(KEY_ITEM_CODE,item.getCode());
		
		
		holder.spinnerTaglioPz.setTag(KEY_POSITION, groupPosition);
		holder.spinnerTaglioPz.setTag(KEY_CHILD_POSITION, childPosition);
		holder.spinnerTaglioPz.setTag(KEY_ITEM_CODE,item.getCode());
		
		holder.btnInfo.setTag(KEY_POSITION,groupPosition);
		holder.btnInfo.setTag(KEY_CHILD_POSITION,childPosition);
		holder.btnInfo.setTag(KEY_ITEM_CODE,item.getCode());
		
		holder.fieldNo.setText(item.getCode());
		holder.fieldDescription.setText(item.getDescription());
		holder.spinnerVolantino.setSelection(survey.getVolantino());
		holder.spinnerTaglioPz.setSelection(survey.getPrezzo());
		
		holder.layoutMinimise.setVisibility(isLastChild?View.VISIBLE:View.GONE);
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void changeSource(ArrayList<ModelSurveyPromotions> model){
		this.surveys = model;
		notifyDataSetChanged();
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public void setListener(OnPromotionSurveyActionListener listener) {
		this.listener = listener;
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
	
	class ChildViewHolder{
		TextView fieldNo;
		TextView fieldDescription;
		Spinner	 spinnerVolantino;
		Spinner  spinnerTaglioPz;
		View	 layoutMinimise;
		View 	 btnMinimise;
		View 	 btnInfo;
	}
	
	

	
	class GroupViewHolder{
		TextView fieldPromotionName;
		TextView fieldPromotionCode;
		TextView fieldStartingDate;
		TextView fieldEndingDate;
		View 	 layoutVisibility;
		View	 itemBg;
		Spinner  spinnerVisibility;
		View 	 btnInfo;
		
		int 	 position;
	}


	public void clear() {
		surveys.clear();
		notifyDataSetChanged();
	}
	
	public interface OnPromotionSurveyActionListener{
		public void onMinimiseClicked();
		public void onInfoButtonClicked(ModelSurveyPromotions model);
		public void onClickView(int position);
		public void OnInfoButtonClicked(ModelSurveyPromotionItems itemModel);
	}
}
