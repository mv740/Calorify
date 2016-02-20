package com.example.lenovo.calorify.Utilities;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.calorify.MainActivity;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2/20/2016.
 */
public class GoogleSearch {

    private RequestQueue queue;
    private MainActivity activity;

    public GoogleSearch(MainActivity activity){
        queue = Volley.newRequestQueue((Activity)activity);
        this.activity = activity;
    }



    public void howManyCalories(final Food food) {

            String url = "https://www.google.ca/search?site=webhp&source=hp&q=how+many+calories+in+" + food.name;
        Log.v("FOODNAME", food.name);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            int cals = findCalsInSearchResponse(response);
                            food.calories = Integer.toString(cals);

                            activity.updateFoods(food);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }


    private int findCalsInSearchResponse(String response){
        int cals = 0;
        String token = "<div class=\"_eF an_fna\" aria-live=\"polite\">";
        int index = response.indexOf(token) + token.length();
        String numOfCals = response.substring(index, index+15);
        if (numOfCals.contains("calories")){
            //then it is food
            String numOfCalsInInt = numOfCals.replaceAll("[^0-9]", "");
            cals = Integer.valueOf(numOfCalsInInt);
        }

        return cals;
    }

}
