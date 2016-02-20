package com.example.lenovo.calorify.Utilities;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Lenovo on 2/20/2016.
 */
public class GoogleSearch {

    public void search(Activity activity){
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url ="https://www.google.ca/search?site=webhp&source=hp&q=how+many+calories+in+orange";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        findCalsInSearchResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private String findCalsInSearchResponse(String response){
        String token = "<div class=\"_eF an_fna\" aria-live=\"polite\">";
        int index = response.indexOf(token) + token.length();
        String numOfCals = response.substring(index, index+10);
        return numOfCals;
    }

}
