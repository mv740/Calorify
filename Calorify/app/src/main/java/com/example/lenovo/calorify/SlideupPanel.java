package com.example.lenovo.calorify;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lenovo.calorify.Utilities.CaloriesListAdapter;
import com.example.lenovo.calorify.Utilities.Food;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2/21/2016.
 */
public class SlideupPanel {

    MainActivity mainActivity;

    public SlideupPanel(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }



    private void buildCaloriesList(String[] foodNames, String[] calNums, String[] gramNums) {
        CaloriesListAdapter adapter = new CaloriesListAdapter(mainActivity, foodNames, calNums, gramNums);
        ListView list = (ListView) mainActivity.findViewById(R.id.caloriesList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView calNumsTV = (TextView) view.findViewById(R.id.cal_num);
                TextView foodNameTV = (TextView) view.findViewById(R.id.food_name);
                TextView gramNumsTV = (TextView) view.findViewById(R.id.grams_num);

                Food newFood = new Food(foodNameTV.getText().toString());
                newFood.calories = Integer.valueOf(calNumsTV.getText().toString());
                newFood.grams = Integer.valueOf(gramNumsTV.getText().toString());

                mainActivity.dbMan.addToCalorieCount(newFood.calories);
                //mainActivity.dbMan.persist(newFood);
                mainActivity.dbMan.addToCalorieCount(newFood.calories);

            }
        });

    }


    private void updateCaloriesList() {
        ArrayList<String> foodNamesAL = new ArrayList<>();
        ArrayList<String> calNumsAL = new ArrayList<>();
        ArrayList<String> gramNumsAL = new ArrayList<>();

        for (Food food : mainActivity.foods) {
            if (food.calories > 0) {
                foodNamesAL.add(food.name);
                calNumsAL.add(String.valueOf(food.calories));
                gramNumsAL.add(String.valueOf(food.grams));
            }
        }

        String[] foodNames = foodNamesAL.toArray(new String[0]);
        String[] calNums = calNumsAL.toArray(new String[0]);
        String[] gramNums = gramNumsAL.toArray(new String[0]);

        buildCaloriesList(foodNames, calNums, gramNums);
    }

    public void updateFoods(Food food) {

        mainActivity.foods.set(food.index, food);
        updateCaloriesList();
    }

    public void searchBegins() {
        setLoadingVisibility(View.VISIBLE);
        hideSlideupAlert();
    }

    public void searchIsDone() {
        boolean foodFound = false;
        for (Food food : mainActivity.foods) {
            if (food.calories > 0)
                foodFound = true;
        }

        if (!foodFound) {
            setSlideupAlert(R.string.no_result_slideup_text);
        }
        setLoadingVisibility(View.GONE);
        setSlideupAlert(R.string.search_slideup_text);
    }

    public void setLoadingVisibility(int visibility) {
        RelativeLayout progressBarContainer = (RelativeLayout) mainActivity.findViewById(R.id.progress_bar_container);
        progressBarContainer.setVisibility(visibility);
    }

    public void setSlideupAlert(int alertText){
        TextView alert = (TextView) mainActivity.findViewById(R.id.sliding_layout_alert);
        alert.setText(alertText);
        alert.setVisibility(View.VISIBLE);
    }

    public void hideSlideupAlert(){
        TextView alert = (TextView) mainActivity.findViewById(R.id.sliding_layout_alert);
        alert.setVisibility(View.GONE);
        
    }
}
