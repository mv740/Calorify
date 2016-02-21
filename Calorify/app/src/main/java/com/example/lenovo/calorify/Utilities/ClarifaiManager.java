package com.example.lenovo.calorify.Utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;
import com.example.lenovo.calorify.Authentication.Credential;
import com.example.lenovo.calorify.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by michal on 2/20/2016.
 */
public class ClarifaiManager {

    private ClarifaiClient client;
    private MainActivity mainActivity;

    public ClarifaiManager(MainActivity mainActivity) {
        client = new ClarifaiClient(Credential.CLIENT_ID, Credential.CLIENT_SECRET);
        this.mainActivity = mainActivity;
    }

    private static final String TAG_C = "Clarifai";

    public  Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }
        Log.d("getBitmapFromAsset", "processed");
        return bitmap;
    }

    public void sendImageToClarifai(Bitmap bitmapSelected) {
        mainActivity.panel.searchBegins();

        Log.d("Clarifai", "start");
        new AsyncTask<Bitmap,Void,RecognitionResult>() {
            @Override
            protected RecognitionResult doInBackground(Bitmap[] bitmaps) {
                Log.d(TAG_C, "doInBackground");
                return recognizeFile(bitmaps[0]);
            }
            @Override protected void onPostExecute(RecognitionResult result) {
                //

                ArrayList<Food> foodArrayList = new ArrayList<>();
                for (Tag tag :result.getTags())
                {
                    Food newFood = new Food(tag.getName());
                    newFood.index = foodArrayList.size();
                    foodArrayList.add(newFood);
                }


                Log.d(TAG_C,result.getTags().toString());

                mainActivity.initGoogleSearch(foodArrayList);

            }

        }.execute(bitmapSelected);
    }

    /** Sends the given bitmap to Clarifai for recognition and returns the result. */
    public  RecognitionResult recognizeFile(Bitmap bitmap) {
        try {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

            // Compress the image as a JPEG.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] jpeg = out.toByteArray();

            Log.d(TAG_C, "send msg");
            // Send the file to Clarifai and return the result.
            return client.recognize(new RecognitionRequest(jpeg)).get(0);
        } catch (ClarifaiException e) {
            Log.e(TAG_C, "Clarifai error", e);
            return null;
        }
    }
}
