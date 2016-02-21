package com.example.lenovo.calorify;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lenovo.calorify.Utilities.DBManager;
import com.example.lenovo.calorify.Utilities.Food;
import com.example.lenovo.calorify.Utilities.GoogleSearch;
import com.example.lenovo.calorify.Utilities.ClarifaiManager;

import org.w3c.dom.Text;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.ArrayList;

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

    }


    public void test(){
        dbMan.persist(new Food("salut"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void updateTotalCals(){
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
