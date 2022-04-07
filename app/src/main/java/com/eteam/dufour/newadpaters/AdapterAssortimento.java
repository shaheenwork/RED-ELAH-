package com.eteam.dufour.newadpaters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eteam.dufour.customview.ElahProgress;
import com.eteam.dufour.database.tables.TableItems.Item;
import com.eteam.dufour.database.tables.TableSurveyAssortimento.SurveyAssortimento;
import com.eteam.dufour.fragments.FragmentProddoti;
import com.eteam.dufour.mobile.R;
import com.eteam.dufour.mobile.R.color;
import com.eteam.dufour.viewmodel.ModelSurveyAssortimento;
import com.eteam.dufour.viewmodel.RelatedSpinners;
import com.eteam.utils.Consts;
import com.eteam.utils.Util;

public class AdapterAssortimento extends BaseAdapter {


    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TAG = "FragmentProdotti";

    public static final int NON_SELECTED = -1;
    public static final int FOCUS_PREZZO = 100;
    public static final int FOCUS_PREZZO_PROMO = 101;

    private static final String TAG_PREZZO = "fi_prezzo";
    private static final String TAG_PREZZO_PROMO = "fi_prezzo_promo";

    private static final String TAG_ASSORT = "sp_assort";
    private static final String TAG_FACING = "sp_facing";
    // private static final String TAG_FB = "sp_fb";

    private static final int KEY_TYPE = R.id.type;
    private static final int KEY_POSITION = R.id.position;
    private static final int KEY_ITEM_CODE = R.id.itemcode;

    private OnAssortimentoSurveyActionListener listener;

    // ===========================================================
    // Fields
    // ===========================================================


    private boolean filterSelected = false;
    private ArrayList<ModelSurveyAssortimento> surveys;
    private AssortAdapter mAdapterAssort;
    private int positionFocused;
    private int fieldFocused;
    private Toast mToast;

    private boolean prodottiRilevatiSelected;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AdapterAssortimento(Context context) {
        this(context, new ArrayList<ModelSurveyAssortimento>());
    }

    public AdapterAssortimento(Context context, ArrayList<ModelSurveyAssortimento> surveys) {
        this.surveys = surveys;

        mAdapterAssort = new AssortAdapter(context
                , android.R.layout.simple_spinner_item, Consts.ARRAY_ASSORT);
        mAdapterAssort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
       // Util.setUpToast(mToast);
    }
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void changeSource(ArrayList<ModelSurveyAssortimento> surveys) {
        this.surveys = new ArrayList<ModelSurveyAssortimento>(surveys);
        notifyDataSetChanged();
    }

    public boolean isFilterSelected() {
        return filterSelected;
    }

    public void setFilterSelected(boolean filterSelected) {
        this.filterSelected = filterSelected;
    }


    public void setListener(OnAssortimentoSurveyActionListener listener) {
        this.listener = listener;
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
    public ModelSurveyAssortimento getItem(int position) {
        // TODO Auto-generated method stub
        return surveys.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return surveys.get(position).getItem().getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(group.getContext());
            view = inflater.inflate(R.layout.row_prodotti, group, false);
            holder = new ViewHolder();

            holder.mItemCode = (TextView) view.findViewById(R.id.prodotti_no);
            holder.mItemDesc = (TextView) view.findViewById(R.id.prodotti_name);

            holder.mPrezzoEdit = (EditText) view.findViewById(R.id.prezzo_edit);
            holder.mPromoEdit = (EditText) view.findViewById(R.id.prezzo_promo_edit);
            holder.mDummyFocus = (EditText) view.findViewById(R.id.dummyFocus);

            holder.mSpinnerAssort = (Spinner) view.findViewById(R.id.assort_spinner);
            holder.mSpinnerFacing = (Spinner) view.findViewById(R.id.facing_spinner);
            //   holder.mSpinnerFB = (Spinner) view.findViewById(R.id.fb_spinner);
            Spinner posLnOne = (Spinner) view.findViewById(R.id.poslin_spinner1);
            Spinner posLnTwo = (Spinner) view.findViewById(R.id.poslin_spinner2);

            holder.relatedSpinners = new RelatedSpinners(posLnOne, posLnTwo);


            holder.mBtnInfo = view.findViewById(R.id.btn_info);
            holder.mBtnCopy = view.findViewById(R.id.btn_copy);

            holder.mExpandable = view.findViewById(R.id.expandable);
            holder.mLayoutRow = view.findViewById(R.id.item);
            holder.mPromoStatus = view.findViewById(R.id.promo_status);


            //setSelection on EditText
          /*  holder.mPrezzoEdit.setSelection(holder.mPrezzoEdit.length());
            holder.mPromoEdit.setSelection(holder.mPromoEdit.length());

            holder.mPrezzoEdit.setSelectAllOnFocus(true);
            holder.mPromoEdit.setSelectAllOnFocus(true);*/

            holder.relatedSpinners.setListener(new RelatedSpinners.OnRelatedSpinnerSelectListener() {

                @Override
                public void onPosLnOneSelected(Spinner posLnOne, Spinner posLnTwo,
                                               int position) {
                    int listPosition = (Integer) posLnOne.getTag(KEY_POSITION);
                    String itemCode = (String) posLnOne.getTag(KEY_ITEM_CODE);
                    if (listPosition < surveys.size()) {
                        ModelSurveyAssortimento model = surveys.get(listPosition);
                        SurveyAssortimento survey = model.getSurvey();
                        if (!survey.getItemCode().equals(itemCode)) {
                            return;
                        }
                        survey.setPrezzoLinea(posLnOne.getSelectedItem().toString() + "/"
                                + posLnTwo.getSelectedItem().toString());
                        if (position == posLnOne.getAdapter().getCount() - 1) {
                            posLnTwo.setSelection(0);
                            posLnTwo.setEnabled(false);
                            posLnTwo.setAlpha(0.2f);


                        } else {
                            posLnTwo.setEnabled(true);
                            posLnTwo.setAlpha(1.0f);
                        }
                        if (listener != null)
                            listener.onModified(model);

                    }
                }

                @Override
                public void onPosLnTwoSelected(Spinner posLnOne, Spinner posLnTwo, int position) {
                    int listPosition = (Integer) posLnTwo.getTag(KEY_POSITION);
                    String itemCode = (String) posLnOne.getTag(KEY_ITEM_CODE);
                    if (listPosition < surveys.size()) {
                        ModelSurveyAssortimento model = surveys.get(listPosition);
                        SurveyAssortimento survey = model.getSurvey();
                        if (!survey.getItemCode().equals(itemCode)) {
                            return;
                        }
                        surveys.get(listPosition)
                                .getSurvey()
                                .setPrezzoLinea(posLnOne.getSelectedItem().toString() + "/"
                                        + posLnTwo.getSelectedItem().toString());
                        if (listener != null)
                            listener.onModified(model);
                    }
                }


            });


            holder.mPrezzoEdit.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        holder.mPrezzoEdit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.mPrezzoEdit.clearFocus();
                                holder.mPrezzoEdit.requestFocus();
                                holder.mPrezzoEdit.selectAll();
                            }
                        }, 200);
                    }
                    return false;
                }
            });

            holder.mPromoEdit.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        holder.mPromoEdit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.mPromoEdit.clearFocus();
                                holder.mPromoEdit.requestFocus();
                                holder.mPromoEdit.selectAll();
                            }
                        }, 200);
                    }
                    return false;
                }
            });


            holder.mPrezzoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        holder.mPromoEdit.requestFocus();
                    }
                    return true;
                }
            });

//			holder.mPromoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//					if(actionId==EditorInfo.IME_ACTION_DONE){
////						holder.mPromoEdit.clearFocus();
//					}
//					return true;
//				}
//			});
            SpinnerInteractionListener listenerAssort = new SpinnerInteractionListener();
            SpinnerInteractionListener listenerFacing = new SpinnerInteractionListener();
            //  SpinnerInteractionListener listenerFB = new SpinnerInteractionListener();
            SpinnerInteractionListener listenerPosLine = new SpinnerInteractionListener();


            posLnOne.setOnTouchListener(listenerPosLine);
            posLnTwo.setOnTouchListener(listenerPosLine);

            holder.mSpinnerAssort.setOnTouchListener(listenerAssort);
            holder.mSpinnerAssort.setOnItemSelectedListener(listenerAssort);

            holder.mSpinnerFacing.setOnTouchListener(listenerFacing);
            holder.mSpinnerFacing.setOnItemSelectedListener(listenerFacing);

           /* holder.mSpinnerFB.setOnTouchListener(listenerFB);
            holder.mSpinnerFB.setOnItemSelectedListener(listenerFB);
*/
            holder.mPrezzoEdit.setOnFocusChangeListener(focusListener);
            holder.mPromoEdit.setOnFocusChangeListener(focusListener);

            holder.mPrezzoEdit.removeTextChangedListener(holder.prezzoTextWatcher);
            holder.mPromoEdit.removeTextChangedListener(holder.prezzoPromoTextWatcher);
            holder.mPrezzoEdit.addTextChangedListener(holder.prezzoTextWatcher);
            holder.mPromoEdit.addTextChangedListener(holder.prezzoPromoTextWatcher);

            holder.mPrezzoEdit.setKeyListener(Consts.ELAH_KEY_LISTENER);
            holder.mPrezzoEdit.setFilters(Consts.COMMA_FILTER);

            holder.mPromoEdit.setKeyListener(Consts.ELAH_KEY_LISTENER);
            holder.mPromoEdit.setFilters(Consts.COMMA_FILTER);

            holder.mPrezzoEdit.setTag(KEY_TYPE, TAG_PREZZO);
            holder.mPromoEdit.setTag(KEY_TYPE, TAG_PREZZO_PROMO);
            holder.mSpinnerAssort.setTag(KEY_TYPE, TAG_ASSORT);
            holder.mSpinnerFacing.setTag(KEY_TYPE, TAG_FACING);
            //   holder.mSpinnerFB.setTag(KEY_TYPE, TAG_FB);


            holder.mSpinnerAssort.setAdapter(mAdapterAssort);


            holder.mBtnInfo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag(KEY_POSITION);

                    if (listener != null)
                        listener.onInfoClicked(surveys.get(position).getItem());
                }
            });

            holder.mBtnCopy.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag(KEY_POSITION);
                    if (listener != null)
                        listener.onCopyClicked(surveys.get(position));
                }
            });
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ModelSurveyAssortimento modelView = surveys.get(position);
        holder.position = position;
        Item item = modelView.getItem();
        SurveyAssortimento survey = modelView.getSurvey();

        holder.mItemCode.setText(item.getCode());
        holder.mItemDesc.setText(item.getDescription());


        holder.setupRow(modelView);

        View promoView = holder.mPromoStatus;
        String status = item.getPromoStatus();
        if (!TextUtils.isEmpty(status)) {
            if (status.equals("1")) {
                promoView.setBackgroundResource(R.color.promo_active);
            } else if (status.equals("2")) {
                promoView.setBackgroundResource(R.color.promo_expire);
            } else if (status.equals("3")) {
                promoView.setBackgroundResource(R.color.promo_future);
            } else {
                promoView.setBackgroundResource(R.color.promo_inactive);
            }
        } else {
            promoView.setBackgroundResource(R.color.promo_inactive);
        }

        holder.mPrezzoEdit.setText(survey.getPrezzo());
        holder.mPromoEdit.setText(survey.getPrezzoPromo());
        holder.mSpinnerAssort.setSelection(survey.getAssorti());
        holder.mSpinnerFacing.setSelection(survey.getFacing());
        //  holder.mSpinnerFB.setSelection(survey.getFb());

        holder.relatedSpinners.mPosLnOne.setSelection(survey.getLineaOne());
        holder.relatedSpinners.mPosLnTwo.setSelection(survey.getLineaTwo());

        holder.mPrezzoEdit.setTag(KEY_POSITION, position);
        holder.mPromoEdit.setTag(KEY_POSITION, position);
        holder.mSpinnerAssort.setTag(KEY_POSITION, position);
        holder.mSpinnerFacing.setTag(KEY_POSITION, position);
        //    holder.mSpinnerFB.setTag(KEY_POSITION, position);

        holder.relatedSpinners.mPosLnOne.setTag(KEY_POSITION, position);
        holder.relatedSpinners.mPosLnTwo.setTag(KEY_POSITION, position);
        holder.mBtnInfo.setTag(KEY_POSITION, position);
        holder.mBtnCopy.setTag(KEY_POSITION, position);

        String itemCode = survey.getItemCode();


        holder.mPrezzoEdit.setTag(KEY_ITEM_CODE, itemCode);
        holder.mPromoEdit.setTag(KEY_ITEM_CODE, itemCode);
        holder.mSpinnerAssort.setTag(KEY_ITEM_CODE, itemCode);
        holder.mSpinnerFacing.setTag(KEY_ITEM_CODE, itemCode);
        //   holder.mSpinnerFB.setTag(KEY_ITEM_CODE, itemCode);

        holder.relatedSpinners.mPosLnOne.setTag(KEY_ITEM_CODE, itemCode);
        holder.relatedSpinners.mPosLnTwo.setTag(KEY_ITEM_CODE, itemCode);
        holder.mBtnInfo.setTag(KEY_ITEM_CODE, itemCode);
        holder.mBtnCopy.setTag(KEY_ITEM_CODE, itemCode);

        if (positionFocused == position) {
            holder.mDummyFocus.clearFocus();
            switch (fieldFocused) {
                case R.id.prezzo_edit:
                    holder.mPrezzoEdit.requestFocus();
                    break;
                case R.id.prezzo_promo_edit:
                    holder.mPromoEdit.requestFocus();
                    break;

                default:
                    break;
            }
        } else {
            holder.mPrezzoEdit.clearFocus();
            holder.mPromoEdit.clearFocus();
            if (positionFocused != -1 && group.getFocusedChild() == null) {
                holder.mDummyFocus.requestFocus();
            }

        }

        return view;
    }

    // ===========================================================
    // Methods
    // ===========================================================


    //	private OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
//
//		@Override
//		public void onItemSelected(AdapterView<?> parent, View view,
//				int position, long id) {
//			String type = (String) parent.getTag(KEY_TYPE);
//			int listposition = (Integer) parent.getTag(KEY_POSITION);
//			String itemCode  = (String) parent.getTag(KEY_ITEM_CODE);
////			Log.d(TAG, "Type is "+type+" position is "+listposition);
//			if(listposition<surveys.size()){
//				ModelSurveyAssortimento model = surveys.get(listposition);
//				if(model!=null){
//					SurveyAssortimento survey = model.getSurvey();
//					if(!survey.getItemCode().equals(itemCode)){
//						return;
//					}
//	//					Log.d(TAG, "Type is "+type);
//					if(type.equals(TAG_ASSORT)){
//						Util.setAssortSpinnerColor(parent, position);
//						survey.setAssorti(position);
//					}
//					else if(type.equals(TAG_FACING)){
//						survey.setFacing(position);
//					}
//					else if(type.equals(TAG_FB)){
//						survey.setFb(position);
//					}
//				}
//				if(listener!=null)
//					listener.onModified(model);
//			}
//
//		}
//
//		@Override
//		public void onNothingSelected(AdapterView<?> parent) {
//
//		}
//	};

//hide keyboard in pressing other

    public void hideKeyboard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;
        private String type;
        private int listposition;
        private String itemCode;
        private ModelSurveyAssortimento model;
        private SurveyAssortimento survey;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            hideKeyboard(v, v.getContext());
            return false;

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                type = (String) parent.getTag(KEY_TYPE);
                listposition = (Integer) parent.getTag(KEY_POSITION);
                itemCode = (String) parent.getTag(KEY_ITEM_CODE);
//				Log.d(TAG, "Type is "+type+" position is "+listposition);
                if (listposition < surveys.size()) {
                    model = surveys.get(listposition);
                    if (model != null) {
                        survey = model.getSurvey();
                        if (!survey.getItemCode().equals(itemCode)) {
                            return;
                        }
                        //					Log.d(TAG, "Type is "+type);
                        if (type.equals(TAG_ASSORT)) {
                            Util.setAssortSpinnerColor(parent, position);
                            survey.setAssorti(position);
                        } else if (type.equals(TAG_FACING)) {
                            survey.setFacing(position);
                        } /*else if (type.equals(TAG_FB)) {
                            survey.setFb(position);
                        }*/
                    }
                    if (listener != null)
                        listener.onModified(model);
                }
                // Your selection handling code here
                userSelect = false;
            } else {
                //else condition added by bibin for promotion color

                type = (String) parent.getTag(KEY_TYPE);
                listposition = (Integer) parent.getTag(KEY_POSITION);
                itemCode = (String) parent.getTag(KEY_ITEM_CODE);
                if (listposition < surveys.size()) {
                    model = surveys.get(listposition);
                    if (model != null) {
                        survey = model.getSurvey();
                        if (!survey.getItemCode().equals(itemCode)) {
                            return;
                        }
                        if (type.equals(TAG_ASSORT)) {
                            Util.setAssortSpinnerColor(parent, position);
                        }
                    }
                }

            }
        }

///old code

/*
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                String type = (String) parent.getTag(KEY_TYPE);
                int listposition = (Integer) parent.getTag(KEY_POSITION);
                String itemCode = (String) parent.getTag(KEY_ITEM_CODE);
//				Log.d(TAG, "Type is "+type+" position is "+listposition);
                if (listposition < surveys.size()) {
                    ModelSurveyAssortimento model = surveys.get(listposition);
                    if (model != null) {
                        SurveyAssortimento survey = model.getSurvey();
                        if (!survey.getItemCode().equals(itemCode)) {
                            return;
                        }
                        //					Log.d(TAG, "Type is "+type);
                        if (type.equals(TAG_ASSORT)) {
                            Util.setAssortSpinnerColor(parent, position);
                            survey.setAssorti(position);
                        } else if (type.equals(TAG_FACING)) {
                            survey.setFacing(position);
                        } else if (type.equals(TAG_FB)) {
                            survey.setFb(position);
                        }
                    }
                    if (listener != null)
                        listener.onModified(model);
                }
                // Your selection handling code here
                userSelect = false;
            }
        }*/


        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }

    }


    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v != null) {
                String type = (String) v.getTag(KEY_TYPE);
                Integer listposition = (Integer) v.getTag(KEY_POSITION);
                String itemCode = (String) v.getTag(KEY_ITEM_CODE);


                if (listposition == null)
                    listposition = 0;

                if (!hasFocus && listposition < surveys.size()) {

                    SurveyAssortimento survey = surveys.get(listposition).getSurvey();
                    if (!survey.getItemCode().equals(itemCode)) {
                        return;
                    }
                    EditText field = (EditText) v;
                    if (type.equals(TAG_PREZZO)) {
                        String mValue = Util.getAsElahDBNumFormat(field.getText().toString());
                        survey.setPrezzo(mValue);
                        field.setTextKeepState(mValue);
                    } else if (type.equals(TAG_PREZZO_PROMO)) {
                        String mValue = Util.getAsElahDBNumFormat(field.getText().toString());
                        survey.setPrezzoPromo(mValue);
                        field.setTextKeepState(mValue);
                    }


//					Log.d(TAG, "Focus lost at position :"+listposition);
//					Log.d(TAG, "Last selection position :"+positionFocused);

                } else {


                    positionFocused = listposition;
                    fieldFocused = type.equals(TAG_PREZZO) ? R.id.prezzo_edit : R.id.prezzo_promo_edit;

                }

            }
        }
    };


    public void setProdottiRilevatiSelected(boolean prodottiRilevatiSelected) {
        this.prodottiRilevatiSelected = prodottiRilevatiSelected;
    }

    public int position;
    public int field;

    public void saveFocus() {
        position = positionFocused;
        field = fieldFocused;
    }

    public void restoreFocus() {
        positionFocused = position;
        fieldFocused = field;

        position = -1;
        field = -1;
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    class ViewHolder {

        TextView mItemCode;
        TextView mItemDesc;

        EditText mPrezzoEdit;
        EditText mPromoEdit;
        EditText mDummyFocus;

        Spinner mSpinnerFacing;
        //  Spinner mSpinnerFB;
        RelatedSpinners relatedSpinners;
        Spinner mSpinnerAssort;

        View mBtnCopy;
        View mBtnInfo;

        View mLayoutRow;
        View mPromoStatus;
        View mExpandable;

        int position;


        OnTextWatchListener listener;


        public void setListener(OnTextWatchListener listener) {
            this.listener = listener;
        }

        private TextWatcher prezzoTextWatcher = new TextWatcher() {
            boolean mToggle = false;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (position < surveys.size()) {

                    if (!TextUtils.isEmpty(s.toString())) {
                        if (s.toString().equals(".")){
                            s.replace(0,s.length(),"0.");
                        }
                        else if (s.toString().equals(",")){
                            s.replace(0,s.length(),"0,");
                        }
                        if (Double.parseDouble(s.toString().replace(',', '.')) > 30) {
                            if (!mToggle) {

                                Util.showToast(mToast, R.string.prezzo_promo_limit);
                                mToggle = !mToggle;
                                s.replace(0, s.length(), "");
                            }
                        } else if (mToggle) {
                            mToggle = !mToggle;
                        }

                    }

                    ModelSurveyAssortimento model = surveys.get(position);
                    if (mItemCode.getText().equals(model.getItem().getCode())) {
                        SurveyAssortimento survey = model.getSurvey();
                        survey.setPrezzo(Util.getAsElahDBNumFormat(s.toString()));
                        setupRow(FragmentProddoti.checkIfMandatoryFieldsFilled(model));
                    }

                }
            }
        };

        private TextWatcher prezzoPromoTextWatcher = new TextWatcher() {

            boolean mToggle = false;

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


                if (!TextUtils.isEmpty(s.toString())) {
                    if (s.toString().equals(".")){
                        s.replace(0,s.length(),"0.");
                    }
                    else if (s.toString().equals(",")){
                        s.replace(0,s.length(),"0,");
                    }
                    if (Double.parseDouble(s.toString().replace(',', '.')) > 30) {
                        if (!mToggle) {
                            Util.showToast(mToast, R.string.prezzo_promo_limit);
                            s.replace(0, s.length(), "");
                            mToggle = !mToggle;
                        }
                    } else if (mToggle) {
                        mToggle = !mToggle;
                    }

                }

                if (position < surveys.size()) {
                    ModelSurveyAssortimento model = surveys.get(position);
                    if (mItemCode.getText().equals(model.getItem().getCode())) {
                        SurveyAssortimento survey = model.getSurvey();
                        survey.setPrezzoPromo(Util.getAsElahDBNumFormat(s.toString()));
                        setupRow(model);
                    }


                }

            }
        };


        public void setupRow(ModelSurveyAssortimento model) {
            SurveyAssortimento survey = model.getSurvey();
            View copyBtn = mBtnCopy;
            long id = survey.getId();
            if (id != 0 && id != -1) {

                if (model.getAllMandatoryFieldsFilled() == 0) {
                    mLayoutRow.setBackgroundResource(color.item_incomplete);
                } else {
                    mLayoutRow.setBackgroundResource(color.item_selected);
                }
                if (filterSelected && surveys.size() > 1)
                    copyBtn.setVisibility(View.VISIBLE);
                else
                    copyBtn.setVisibility(View.GONE);
            } else {

                if (model.getAllMandatoryFieldsFilled() == 0) {
                    mLayoutRow.setBackgroundResource(color.item_incomplete);
                } else {

                    mLayoutRow.setBackgroundResource(color.item_unselected);
                }
                copyBtn.setVisibility(View.GONE);
                if (prodottiRilevatiSelected) {

                    if (surveys.remove(model)) {
                        positionFocused = -1;
                        notifyDataSetChanged();
                        if (AdapterAssortimento.this.listener != null)
                            AdapterAssortimento.this.listener.onCountChanged();
                    }

                }
            }

        }

        public void setupRow2(ModelSurveyAssortimento model) {
            SurveyAssortimento survey = model.getSurvey();
            View copyBtn = mBtnCopy;
            long id = survey.getId();
            if (id != 0 && id != -1) {
                mLayoutRow.setBackgroundResource(color.item_incomplete);
                if (filterSelected && surveys.size() > 1)
                    copyBtn.setVisibility(View.VISIBLE);
                else
                    copyBtn.setVisibility(View.GONE);
            } else {
                mLayoutRow.setBackgroundResource(color.item_incomplete);
                copyBtn.setVisibility(View.GONE);
                if (prodottiRilevatiSelected) {

                    if (surveys.remove(model)) {
                        positionFocused = -1;
                        notifyDataSetChanged();
                        if (AdapterAssortimento.this.listener != null)
                            AdapterAssortimento.this.listener.onCountChanged();
                    }

                }
            }

        }


    }

    public interface OnAssortimentoSurveyActionListener {
        public void onInfoClicked(Item item);

        public void onCopyClicked(ModelSurveyAssortimento item);

        public void onModified(ModelSurveyAssortimento item);

        public void onCountChanged();
    }

    public void clear() {
        surveys.clear();
        notifyDataSetChanged();

    }

    public void copyValue(SurveyAssortimento surveyToCopy) {
        for (ModelSurveyAssortimento model : surveys) {
            model.getSurvey().copy(surveyToCopy);
        }
    }

    public interface OnTextWatchListener {
        public void onModified(ModelSurveyAssortimento model);
    }

    public void clearFocus() {
        positionFocused = -1;

    }

    public int getPositionFocused() {
        return positionFocused;
    }

}
