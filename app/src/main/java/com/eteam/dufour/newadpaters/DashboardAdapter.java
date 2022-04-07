package com.eteam.dufour.newadpaters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eteam.dufour.mobile.R;
import com.eteam.dufour.viewmodel.ModelDashboardRow;
import com.eteam.utils.Consts;

import java.util.ArrayList;
import java.util.List;


public class DashboardAdapter extends ArrayAdapter<ModelDashboardRow> implements View.OnClickListener {

    private List<ModelDashboardRow> itemsList;
    private Context context;

    // View lookup cache
    private static class ViewHolder {
        TextView TV_val1, TV_val2, TV_val3, TV_val4, TV_val5;
        RelativeLayout LYT_val1, LYT_val2, LYT_val3, LYT_val4, LYT_val5;
        GradientDrawable BG_circle;
        View circle;
    }

    public DashboardAdapter(List<ModelDashboardRow> itemsList, Context context) {
        super(context, R.layout.row_dashboard, itemsList);
        this.itemsList = itemsList;
        this.context = context;
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            /*case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;*/
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ModelDashboardRow dataModel = getItem(position);
        // Check if an existing view i s being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_dashboard, parent, false);

            viewHolder.TV_val1 = (TextView) convertView.findViewById(R.id.tv_val1);
            viewHolder.TV_val2 = (TextView) convertView.findViewById(R.id.tv_val2);
            viewHolder.TV_val3 = (TextView) convertView.findViewById(R.id.tv_val3);
            viewHolder.TV_val4 = (TextView) convertView.findViewById(R.id.tv_val4);
            viewHolder.TV_val5 = (TextView) convertView.findViewById(R.id.tv_val5);

            viewHolder.LYT_val1 = (RelativeLayout) convertView.findViewById(R.id.lyt_val1);
            viewHolder.LYT_val2 = (RelativeLayout) convertView.findViewById(R.id.lyt_val2);
            viewHolder.LYT_val3 = (RelativeLayout) convertView.findViewById(R.id.lyt_val3);
            viewHolder.LYT_val4 = (RelativeLayout) convertView.findViewById(R.id.lyt_val4);
            viewHolder.LYT_val5 = (RelativeLayout) convertView.findViewById(R.id.lyt_val5);

            viewHolder.circle = convertView.findViewById(R.id.circle);
            viewHolder.BG_circle = (GradientDrawable) viewHolder.circle.getBackground();
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

        ModelDashboardRow item = itemsList.get(position);
        viewHolder.TV_val1.setText(item.getVal1().getValue());
        if (item.getVal2().getType() == Consts.TYPE_VALUE) {
            viewHolder.TV_val2.setText(Html.fromHtml(item.getVal2().getValue()));

        } else {
            viewHolder.TV_val2.setText(item.getVal2().getValue());
        }

        if (item.getVal3().getType() == Consts.TYPE_VALUE) {
            viewHolder.TV_val3.setText(Html.fromHtml(item.getVal3().getValue()));

        } else {
            viewHolder.TV_val3.setText(item.getVal3().getValue());
        }
        if (item.getVal4().getType() == Consts.TYPE_VALUE) {
            viewHolder.TV_val4.setText(Html.fromHtml(item.getVal4().getValue()));

        } else {
            viewHolder.TV_val4.setText(item.getVal4().getValue());
        }
        if (item.getVal5().getType() == Consts.TYPE_VALUE) {
            viewHolder.TV_val5.setText(Html.fromHtml(item.getVal5().getValue()));
        } else {
            viewHolder.TV_val5.setText(item.getVal5().getValue());
        }

        if (item.getVal1().getType() == Consts.TYPE_HEADING1) {
            viewHolder.LYT_val1.setBackgroundColor(context.getResources().getColor(R.color.d_light_grey));
            viewHolder.TV_val1.setTextColor(Color.BLACK);
            viewHolder.circle.setVisibility(View.GONE);
        } else {
            viewHolder.LYT_val1.setBackgroundColor(context.getResources().getColor(R.color.d_grey));
            viewHolder.circle.setVisibility(View.VISIBLE);
            viewHolder.TV_val1.setTextColor(Color.WHITE);
        }


        //background color
        if (item.getVal2().getType() == Consts.TYPE_VALUE) {
            if (item.getVal2().getPlusOrMinus() == Consts.NO_CHANGE) {
                viewHolder.LYT_val2.setBackgroundResource(R.drawable.nochange_grey_roundedcorner);
            } else if (item.getVal2().getPlusOrMinus() == Consts.PLUS) {
                viewHolder.LYT_val2.setBackgroundResource(R.drawable.green_roundedcorner);
            } else if (item.getVal2().getPlusOrMinus() == Consts.MINUS) {
                viewHolder.LYT_val2.setBackgroundResource(R.drawable.red_roundedcorner);
            }
        } else {
            viewHolder.LYT_val2.setBackgroundColor(context.getResources().getColor(R.color.d_grey));
        }

        if (item.getVal3().getType() == Consts.TYPE_VALUE) {
            if (item.getVal3().getPlusOrMinus() == Consts.NO_CHANGE) {
                viewHolder.LYT_val3.setBackgroundResource(R.drawable.nochange_grey_roundedcorner);
            } else if (item.getVal3().getPlusOrMinus() == Consts.PLUS) {
                viewHolder.LYT_val3.setBackgroundResource(R.drawable.green_roundedcorner);
            } else if (item.getVal3().getPlusOrMinus() == Consts.MINUS) {
                viewHolder.LYT_val3.setBackgroundResource(R.drawable.red_roundedcorner);
            }
        } else {
            viewHolder.LYT_val3.setBackgroundColor(context.getResources().getColor(R.color.d_grey));
        }

        if (item.getVal4().getType() == Consts.TYPE_VALUE) {
            if (item.getVal4().getPlusOrMinus() == Consts.NO_CHANGE) {
                viewHolder.LYT_val4.setBackgroundResource(R.drawable.nochange_grey_roundedcorner);
            } else if (item.getVal4().getPlusOrMinus() == Consts.PLUS) {
                viewHolder.LYT_val4.setBackgroundResource(R.drawable.green_roundedcorner);
            } else if (item.getVal4().getPlusOrMinus() == Consts.MINUS) {
                viewHolder.LYT_val4.setBackgroundResource(R.drawable.red_roundedcorner);
            }
        } else {
            viewHolder.LYT_val4.setBackgroundColor(context.getResources().getColor(R.color.d_grey));
        }

        if (item.getVal5().getType() == Consts.TYPE_VALUE) {
            if (item.getVal5().getPlusOrMinus() == Consts.NO_CHANGE) {
                viewHolder.LYT_val5.setBackgroundResource(R.drawable.nochange_grey_roundedcorner);
            } else if (item.getVal5().getPlusOrMinus() == Consts.PLUS) {
                viewHolder.LYT_val5.setBackgroundResource(R.drawable.green_roundedcorner);
            } else if (item.getVal5().getPlusOrMinus() == Consts.MINUS) {
                viewHolder.LYT_val5.setBackgroundResource(R.drawable.red_roundedcorner);
            }
        } else {
            viewHolder.LYT_val5.setBackgroundColor(context.getResources().getColor(R.color.d_grey));

        }


//circle color and visibility
        if (item.getVal1().getCircleColor() == Consts.NO_CIRCLE) {
            viewHolder.circle.setVisibility(View.GONE);
        } else if (item.getVal1().getCircleColor() == Consts.RED_CIRCLE) {
            viewHolder.circle.setVisibility(View.VISIBLE);
            viewHolder.BG_circle.setColor(context.getResources().getColor(R.color.d_circle_red));
        } else if (item.getVal1().getCircleColor() == Consts.YELLOW_CIRCLE) {
            viewHolder.circle.setVisibility(View.VISIBLE);
            viewHolder.BG_circle.setColor(context.getResources().getColor(R.color.d_circle_yellow));
        } else {
            viewHolder.circle.setVisibility(View.VISIBLE);
            viewHolder.BG_circle.setColor(context.getResources().getColor(R.color.d_circle_green));
        }
        return convertView;
    }
}
