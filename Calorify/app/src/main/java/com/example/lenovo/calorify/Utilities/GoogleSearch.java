package com.example.lenovo.calorify.Utilities;

import android.app.Activity;
import android.util.Log;
import android.view.View;

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

    private int searchCount;

    private RequestQueue queue;
    private MainActivity activity;

    public GoogleSearch(MainActivity activity){
        queue = Volley.newRequestQueue((Activity)activity);
        this.activity = activity;
        searchCount = 0;
    }



    public void howManyCalories(final Food food) {

        activity.setLoadingVisibility(View.VISIBLE);

        //to make it compatible with web parameters
        String toSearch = food.name.replaceAll(" ", "+");
            String url = "https://www.google.ca/search?site=webhp&source=hp&q=how+many+calories+in+" + toSearch;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            food.calories = findCalsInSearchResponse(response);
                            food.grams = findGramsInSearchResponse(response);
                            activity.updateFoods(food);

                            if (isSearchComplete()){
                                //reset search count
                                searchCount = 0;
                                activity.searchIsDone();
                            }

                            searchCount++;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    private boolean isSearchComplete() {
        return searchCount+1 == activity.foods.size();
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
    private int findGramsInSearchResponse(String response){
        int grams = 0;
        String token = "</option><option selected=\"selected\" value=\"";
        int index = response.indexOf(token) + token.length();
        String numOfCals = response.substring(index, index+11);
        if (numOfCals.contains("grams")){
            //then it is food
            String numOfCalsInInt = numOfCals.replaceAll("[^0-9]", "");
            grams = Integer.valueOf(numOfCalsInInt);
        }

        return grams;
    }

}
