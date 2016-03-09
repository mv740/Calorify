package com.SuperCoderPlaymates.app.calorify.Utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.SuperCoderPlaymates.app.calorify.R;

/**
 * Created by NSPACE on 2016-02-09.
 */

public class CaloriesListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] foodNames;
    private final String[] calNums;
    private final String[] gramNums;
    public CaloriesListAdapter(Activity context,
                               String[] foodNames, String[] calNums, String[] gramNums) {
        super(context, R.layout.calorie_item_layout, foodNames);
        this.context = context;
        this.foodNames = foodNames;
        this.calNums = calNums;
        this.gramNums = gramNums;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.calorie_item_layout, null, true);

        TextView txtfoodName = (TextView) rowView.findViewById(R.id.food_name);
        TextView calNum = (TextView) rowView.findViewById(R.id.cal_num);
        TextView gramNum = (TextView) rowView.findViewById(R.id.grams_num);

        txtfoodName.setText(foodNames[position]);
        calNum.setText(calNums[position]);

        if (gramNums[position].equals("0")){
            gramNum.setVisibility(View.GONE);
            TextView gramLabel = (TextView) rowView.findViewById(R.id.gram_label);
            gramLabel.setVisibility(View.GONE);
            TextView inLabel = (TextView) rowView.findViewById(R.id.in_label);
            inLabel.setVisibility(View.GONE);
        }
        else {
            gramNum.setText(gramNums[position]);
        }
        return rowView;
    }
}
