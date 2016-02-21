package com.example.lenovo.calorify;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lenovo.calorify.Utilities.DBManager;
import com.example.lenovo.calorify.Utilities.Food;
import com.example.lenovo.calorify.Utilities.GoogleSearch;
import com.example.lenovo.calorify.Utilities.ClarifaiManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    DBManager dbMan;
    public SlideupPanel panel;
    public ArrayList<Food> foods;
    public static ClarifaiManager clarifaiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clarifaiManager = new ClarifaiManager(this);
        dbMan = new DBManager(this);
        panel = new SlideupPanel(this);

        updateTotalCals();

        test();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/brainflower.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        dbMan.getMostEatenFood();

    }


    public void test() {
        dbMan.persist(new Food("salut"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void displayMostEatenFood(String food) {

        setTooltipText(getString(R.string.tooltip_most_eaten) + " " + food);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        resetTooltipText();

                    }
                });
            }
        }, 10000);

    }



    private void setTooltipText(String text) {
        TextView toolTipTV = (TextView) findViewById(R.id.tooltip);
        toolTipTV.setText(text);
    }

    public void resetTooltipText() {
        TextView toolTipTV = (TextView) findViewById(R.id.tooltip);
        toolTipTV.setText(R.string.tooltip_text);
    }


    public void updateTotalCals() {
        int cals = dbMan.getCalorieCount();
        TextView totalCalsTV = (TextView) findViewById(R.id.total_cal_num);
        totalCalsTV.setText(String.valueOf(cals));
    }

    public void initGoogleSearch(ArrayList<Food> foods) {
        this.foods = foods;
        GoogleSearch search = new GoogleSearch(this);
        for (Food food : this.foods) {
            search.howManyCalories(food);
        }
    }


}
