package com.example.lenovo.calorify.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.calorify.MainActivity;
import com.example.lenovo.calorify.R;

/**
 * Created by Lenovo on 2/21/2016.
 */
public class DBManager {

    private RequestQueue queue;
    private MainActivity mainActivity;

    public DBManager(MainActivity activity) {
        this.mainActivity = activity;
        queue = Volley.newRequestQueue((Activity) activity);
    }

    public void persist(Food food){

        String url = "http://calorify.azurewebsites.net/persistFood?foodName="+food.name+"&cals="+food.calories;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    Log.v("azure", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void addToCalorieCount(int cals){
        SharedPreferences sharedPref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(mainActivity.getString(R.string.shared_key_cal_label), getCalorieCount()+cals);
        editor.commit();

        //update view
        mainActivity.updateTotalCals();
    }

    public int getCalorieCount(){
        SharedPreferences sharedPref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        int cals = sharedPref.getInt(mainActivity.getString(R.string.shared_key_cal_label), 0);
        return cals;
    }

    public void resetCalorieCount(){
        SharedPreferences sharedPref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(mainActivity.getString(R.string.shared_key_cal_label), 0);
        editor.commit();

        //update view
        mainActivity.updateTotalCals();
    }

}
