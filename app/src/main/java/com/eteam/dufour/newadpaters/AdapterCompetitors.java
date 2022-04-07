package com.eteam.dufour.newadpaters;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.eteam.dufour.database.tables.TableCompetitors.BaseCompetitor;
import com.eteam.dufour.database.tables.TableSurveyCompetitor.SurveyCompetitor;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.viewmodel.ModelSurveyCompetitor;
import com.eteam.dufour.viewmodel.RelatedSpinners;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class AdapterCompetitors extends BaseAdapter{


	// ===========================================================
	// Constants
	// ===========================================================
		
	
	public static final int FOCUS_TAGLIO 		= 10;
	public static final int FOCUS_PREZZO 		= 11;
	public static final int FOCUS_NOTE 			= 12;
	public static final int NON_SELECTED 		= -1;
	
	private static final String TAG_PREZZO		= "fi_prezzo";
	private static final String TAG_TAGLIO		= "fi_taglio";
	private static final String TAG_NOTE		= "fi_note";
	
	private static final String TAG_FACING 		= "sp_facing";
	private static final String TAG_VISIBILITY 	= "sp_visibility";
	private static final String TAG_VOLANTINO 	= "sp_volantino";
	
	private static final int KEY_TYPE		= R.id.type;
	private static final int KEY_POSITION 	= R.id.position;
	private static final int KEY_ITEM_CODE  = R.id.itemcode;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	
	private ArrayList<ModelSurveyCompetitor> surveys;
	
	private ArrayAdapter<String> mAdapterVisibility;
	private ArrayAdapter<String> mAdapterVolantino;
	private ArrayAdapter<String> mAdapterFacing;
	private ArrayAdapter<String> mAdapterPosLnOne;
	private ArrayAdapter<String> mAdapterPosLnTwo;
	
	private int positionFocused;
	private int fieldFocused;
	
	private OnCompetitorSurveyActionListener listener;
	// ===========================================================
	// Constructors
	// ===========================================================
		
	public AdapterCompetitors(Context context) {
		this(context,new ArrayList<ModelSurveyCompetitor>());
	}
	
	public AdapterCompetitors(Context context,ArrayList<ModelSurveyCompetitor> surveys) {
		this.surveys = surveys;
		
		mAdapterVisibility = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item
					, Consts.ARRAY_VISIBILITY);
		mAdapterVolantino = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item
					, Consts.ARRAY_SI_NO);
		mAdapterFacing = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item
					, Consts.ARRAY_FACING);
		mAdapterPosLnOne = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item
					, Consts.ARRAY_POSLNONE);
		mAdapterPosLnTwo = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item
				, Consts.ARRAY_POSLNTWO);
		
		mAdapterVisibility.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAdapterVolantino.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAdapterFacing.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAdapterPosLnOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAdapterPosLnTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public void setListener(OnCompetitorSurveyActionListener listener) {
		this.listener = listener;
	}
	
	public OnCompetitorSurveyActionListener getListener() {
		return listener;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return surveys.size();
	}

	@Override
	public ModelSurveyCompetitor getItem(int position) {
		// TODO Auto-generated method stub
		return surveys.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return surveys.get(position).getCompetitor().getId();
	}
	
	public void changeSource(ArrayList<ModelSurveyCompetitor> surveys){
		this.surveys = surveys;
		notifyDataSetChanged();
	}
	
	public void clearSource(){
		surveys.clear();
		notifyDataSetChanged();
	}	

	
	@Override
	public View getView(int listposition, View view, ViewGroup group) {
		ViewHolder holder = null;
		if(view==null){
			LayoutInflater inflater = LayoutInflater.from(group.getContext());
			view = inflater.inflate(R.layout.row_conconrenza, group,false);
			
			holder = new ViewHolder();
			holder.lblName 			= (TextView) view.findViewById(R.id.lbl_competitor_name);
			holder.lblDescription 	= (TextView) view.findViewById(R.id.lbl_product_description);
			
			holder.setFieldNote((EditText) view.findViewById(R.id.note_edit));
			holder.setFieldTaglio((EditText) view.findViewById(R.id.taglio_edit));
			holder.setFieldPrezzo((EditText) view.findViewById(R.id.prezzo_edit));
			
			holder.fieldTaglio.setTag(KEY_TYPE, TAG_TAGLIO);
			holder.fieldPrezzo.setTag(KEY_TYPE, TAG_PREZZO);
			holder.fieldNote.setTag(KEY_TYPE, TAG_NOTE);
			
			
			holder.fieldPrezzo.setOnFocusChangeListener(focusListener);
			holder.fieldTaglio.setOnFocusChangeListener(focusListener);
			holder.fieldNote.setOnFocusChangeListener(focusListener);
			
			holder.fieldPrezzo.setKeyListener(Consts.ELAH_KEY_LISTENER);
			holder.fieldTaglio.setKeyListener(Consts.ELAH_KEY_LISTENER);
			
			holder.fieldPrezzo.setFilters(Consts.COMMA_FILTER);
			holder.fieldTaglio.setFilters(Consts.COMMA_FILTER);
			
			holder.spinnerFacing 	= (Spinner) view.findViewById(R.id.facing_spinner);
			holder.spinnerVolantino = (Spinner) view.findViewById(R.id.volantino_spinner);
			holder.spinnerVisibility = (Spinner) view.findViewById(R.id.competitor_visibility);
						
			
			
			holder.relatedSpinners  = new RelatedSpinners((Spinner) view.findViewById(R.id.poslin_spinner1), 
					(Spinner) view.findViewById(R.id.poslin_spinner2));
			
			holder.spinnerFacing.setAdapter(mAdapterFacing);
			holder.spinnerVolantino.setAdapter(mAdapterVolantino);
			holder.spinnerVisibility.setAdapter(mAdapterVisibility);
			holder.relatedSpinners.mPosLnOne.setAdapter(mAdapterPosLnOne);
			holder.relatedSpinners.mPosLnTwo.setAdapter(mAdapterPosLnTwo);
			
			
			holder.childBackGround	= view.findViewById(R.id.item);
			
			holder.spinnerFacing.setTag(KEY_TYPE, TAG_FACING);
			holder.spinnerVolantino.setTag(KEY_TYPE,TAG_VOLANTINO);
			holder.spinnerVisibility.setTag(KEY_TYPE,TAG_VISIBILITY);
			
			holder.spinnerFacing.setOnItemSelectedListener(spinnerSelector);
			holder.spinnerVolantino.setOnItemSelectedListener(spinnerSelector);
			holder.spinnerVisibility.setOnItemSelectedListener(spinnerSelector);

			holder.relatedSpinners.setListener(new RelatedSpinners.OnRelatedSpinnerSelectListener() {
				
				@Override
				public void onPosLnOneSelected(Spinner posLnOne, Spinner posLnTwo,
						int position) {
					Integer listPosition = (Integer) posLnOne.getTag(KEY_POSITION);
					if(listPosition==null)
						listPosition = 0;
					if(listPosition<surveys.size()){
						ModelSurveyCompetitor competitor = surveys.get(listPosition);
						competitor.getSurvey().setPrezzoLinea(
								posLnOne.getSelectedItemPosition()+"/"+posLnTwo.getSelectedItemPosition());
						if(position==mAdapterPosLnOne.getCount()-1){
							posLnTwo.setSelection(0);
							posLnTwo.setEnabled(false);
							posLnTwo.setAlpha(0.2f);
						}
						else{
							posLnTwo.setEnabled(true);
							posLnTwo.setAlpha(1.0f);
						}
						if(listener!=null)
							listener.onModified(competitor);
					}
				}
				
				@Override
				public void onPosLnTwoSelected(Spinner posLnOne, Spinner posLnTwo,
						int position) {
					Integer listPosition = (Integer) posLnTwo.getTag(KEY_POSITION);
					if(listPosition==null)
						listPosition = 0;
					if(listPosition<surveys.size()){
						ModelSurveyCompetitor competitor = surveys.get(listPosition);
						competitor.getSurvey().setPrezzoLinea(
								posLnOne.getSelectedItemPosition()+"/"+posLnTwo.getSelectedItemPosition());
						if(listener!=null)
							listener.onModified(competitor);
					}
				}
				
				
			});
			
			holder.btnMinimise = view.findViewById(R.id.btn_minimise);
			holder.btnMinimise.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(listener!=null)
						listener.onMinimiseClicked();
				}
			});
			
			holder.dummyFocusNumber = (EditText) view.findViewById(R.id.dummyFocusNumber);
			holder.dummyFocusText   = (EditText) view.findViewById(R.id.dummyFocusText);
						
			view.setTag(holder);
		}
		else{
			holder = (ViewHolder) view.getTag();
		}
		
		holder.position = listposition;
		ModelSurveyCompetitor model = surveys.get(listposition);
		
		BaseCompetitor competitor = model.getCompetitor();
		SurveyCompetitor survey   = model.getSurvey();
		
		holder.lblName.setText(competitor.getName());
		holder.lblDescription.setText(competitor.getProductDescription());
		
		holder.fieldTaglio.setText(survey.getTaglioPZ());
		holder.fieldPrezzo.setText(survey.getPrezzo());
		holder.fieldNote.setText(survey.getNote());
		
		holder.fieldTaglio.setTag(KEY_POSITION, listposition);
		holder.fieldPrezzo.setTag(KEY_POSITION, listposition);
		holder.fieldNote.setTag(KEY_POSITION, listposition);
		
		holder.fieldTaglio.setTag(KEY_ITEM_CODE, competitor.getProductId());
		holder.fieldPrezzo.setTag(KEY_ITEM_CODE, competitor.getProductId());
		holder.fieldNote.setTag(KEY_ITEM_CODE, competitor.getProductId());
		
		
		holder.spinnerFacing.setSelection(survey.getFacing());
		holder.spinnerVisibility.setSelection(survey.getVisibilty());
		holder.spinnerVolantino.setSelection(survey.getVolantino());
		
		holder.relatedSpinners.mPosLnOne.setSelection(survey.getLineaOne());
		holder.relatedSpinners.mPosLnTwo.setSelection(survey.getLineaTwo());
					
		holder.spinnerFacing.setTag(KEY_POSITION, listposition);
		holder.spinnerVolantino.setTag(KEY_POSITION,listposition);
		holder.spinnerVisibility.setTag(KEY_POSITION,listposition);
		
		holder.relatedSpinners.mPosLnOne.setTag(KEY_POSITION,listposition);
		holder.relatedSpinners.mPosLnTwo.setTag(KEY_POSITION,listposition);
		
		
		
		holder.setupRow(model);
		
		if(positionFocused==listposition){
			holder.dummyFocusNumber.clearFocus();
			holder.dummyFocusText.clearFocus();
			switch (fieldFocused) {
				case R.id.prezzo_edit:
					holder.fieldPrezzo.requestFocus();
					break;
				case R.id.taglio_edit:
					holder.fieldTaglio.requestFocus();
					break;
				case R.id.note_edit:
					holder.fieldNote.requestFocus();
					break;
	
				default:
					break;
			}
		}
		else{
			if(positionFocused!=-1&&group.getFocusedChild()==null){
				switch (fieldFocused) {
				case R.id.prezzo_edit:
				case R.id.taglio_edit:
					holder.dummyFocusNumber.requestFocus();
					break;
				case R.id.note_edit:
					holder.dummyFocusText.requestFocus();
					break;
	
				default:
					break;
			}
			}
			
			holder.fieldPrezzo.clearFocus();
			holder.fieldTaglio.clearFocus();
			holder.fieldNote.clearFocus();
		}

		return view;
	}
	
	public void clearFocus() {
		positionFocused = -1;
		
	}
	
	// ===========================================================
	// Methods
	// ===========================================================


	
	

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	

	
	class ViewHolder{
		TextView lblName;
		TextView lblDescription;
		
		EditText fieldTaglio;
		EditText fieldNote;
		EditText fieldPrezzo;
		EditText dummyFocusText;
		EditText dummyFocusNumber;
		
		Spinner  spinnerFacing;
		Spinner  spinnerVolantino;
		Spinner  spinnerVisibility;
		RelatedSpinners relatedSpinners;
		
		View 	 childBackGround;
		View	 btnMinimise;
		
		int		 position;
		
		
		public void setFieldTaglio(EditText fieldTaglio) {
			this.fieldTaglio = fieldTaglio;
			this.fieldTaglio.removeTextChangedListener(taglioTextWatcher);
			this.fieldTaglio.addTextChangedListener(taglioTextWatcher);
		}
		
		public void setFieldNote(EditText fieldNote) {
			this.fieldNote = fieldNote;
			this.fieldNote.removeTextChangedListener(noteTextWatcher);
			this.fieldNote.addTextChangedListener(noteTextWatcher);
		}
		
		public void setFieldPrezzo(EditText fieldPrezzo) {
			this.fieldPrezzo = fieldPrezzo;
			this.fieldPrezzo.removeTextChangedListener(prezzoTextWatcher);
			this.fieldPrezzo.addTextChangedListener(prezzoTextWatcher);
		}
		
		
		
		private TextWatcher prezzoTextWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				if(position<surveys.size()){
					ModelSurveyCompetitor model = surveys.get(position);
					SurveyCompetitor survey = model.getSurvey();
					survey.setPrezzo(Util.getAsElahDBNumFormat(s.toString()));
					setupRow(model);
				}
			}
		};
		
		private TextWatcher taglioTextWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				if(position<surveys.size()){
					ModelSurveyCompetitor model = surveys.get(position);
					SurveyCompetitor survey = model.getSurvey();
					survey.setTaglioPZ(Util.getAsElahDBNumFormat(s.toString()));
					setupRow(model);
				}
			}
		};
		
		private TextWatcher noteTextWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(position<surveys.size()){
					ModelSurveyCompetitor model = surveys.get(position);
					SurveyCompetitor survey = model.getSurvey();
					survey.setNote(s.toString());
					setupRow(model);
				}
			}
		};

		protected void setupRow(ModelSurveyCompetitor model) {
			long id = model.getSurvey().getId();
			childBackGround.setBackgroundResource((id==0||id==-1)?R.color.item_unselected:R.color.item_selected);
		}
		
	}
	
		
	private OnItemSelectedListener spinnerSelector = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String type = (String) parent.getTag(KEY_TYPE);
			Integer    listPosition =  (Integer) parent.getTag(KEY_POSITION);
			
			if(listPosition==null)
				listPosition = 0;
			if(listPosition<surveys.size()){
				ModelSurveyCompetitor model = surveys.get(listPosition);
				SurveyCompetitor survey = model.getSurvey();
				if(type.equals(TAG_FACING)){
					survey.setFacing(position);
				}
				else if(type.equals(TAG_VISIBILITY)){
					survey.setVisibilty(position);
				}
				else if(type.equals(TAG_VOLANTINO)){
					survey.setVolantino(position);
				}
				if(listener!=null)
					listener.onModified(model);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(v!=null){
				String type = (String) v.getTag(KEY_TYPE);
				Integer listposition = (Integer) v.getTag(KEY_POSITION);
				
				if(listposition==null)
					listposition = 0;
				
				if(!hasFocus&&listposition<surveys.size()){
									
					String productId = (String)v.getTag(KEY_ITEM_CODE);
					if(listposition!=null&&!TextUtils.isEmpty(productId)){
					
						SurveyCompetitor competitor = surveys.get(listposition).getSurvey();
						
						if(!competitor.getProductId().equals(productId))
							return;
						EditText field = (EditText) v;
						
						String text = field.getText().toString();
						if(type.equals(TAG_PREZZO)){
							String mValue = Util.getAsElahDBNumFormat(text);
							competitor.setPrezzo(mValue);
							field.setTextKeepState(mValue);
						}
						else if(type.equals(TAG_TAGLIO)){
							String mValue = Util.getAsElahDBNumFormat(text);
							competitor.setTaglioPZ(mValue);
							field.setTextKeepState(mValue);
						}
						else if(type.equals(TAG_NOTE)){
							competitor.setNote(text);
						}
						
					}
				}
				else{
					positionFocused = listposition;
					if(type.equals(TAG_PREZZO)){
						fieldFocused = R.id.prezzo_edit;
					}
					else if(type.equals(TAG_TAGLIO)){
						fieldFocused = R.id.taglio_edit;
					}
					else if(type.equals(TAG_NOTE)){
						fieldFocused = R.id.note_edit;
					}
					else
						fieldFocused = -1;
					
				}
			
			}
		}
	};
	
	
	public interface OnCompetitorSurveyActionListener{
		public void onMinimiseClicked();
		public void onModified(ModelSurveyCompetitor item);
	}

	
}
