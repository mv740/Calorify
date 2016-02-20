package com.example.lenovo.calorify.Utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lenovo.calorify.R;

/**
 * Created by NSPACE on 2016-02-09.
 */

public class CaloriesListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] foodNames;
    private final String[] calNums;
    public CaloriesListAdapter(Activity context,
                               String[] foodNames, String[] calNums) {
        super(context, R.layout.calorie_item_layout, foodNames);
        this.context = context;
        this.foodNames = foodNames;
        this.calNums = calNums;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.calorie_item_layout, null, true);

        TextView txtfoodName = (TextView) rowView.findViewById(R.id.food_name);
        TextView descriptField = (TextView) rowView.findViewById(R.id.cal_num);

        txtfoodName.setText(foodNames[position]);
        descriptField.setText(calNums[position]);
        return rowView;
    }
}
