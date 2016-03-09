package com.SuperCoderPlaymates.app.calorify;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.graphics.Bitmap;


import com.clarifai.api.ClarifaiClient;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import com.SuperCoderPlaymates.app.calorify.Utilities.CaloriesListAdapter;
import com.SuperCoderPlaymates.app.calorify.Utilities.Food;
import com.SuperCoderPlaymates.app.calorify.Utilities.GoogleSearch;
import com.SuperCoderPlaymates.app.calorify.Utilities.ClarifaiManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    EasyCamera camera;
    SurfaceView surface;
    SurfaceHolder holder;
    public ArrayList<Food> foods;
    public boolean doneLoading = false;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ClarifaiClient client;
    public static ClarifaiManager clarifaiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clarifaiManager = new ClarifaiManager(this);

        //for testing witout camer
        //Bitmap bitmapSelected = clarifaiManager.getBitmapFromAsset(getApplicationContext(), "kitten.jpeg");

        //populateCaloriesList();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/brainflower.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        Fragment camera =  getSupportFragmentManager().findFragmentById(R.id.camera_preview);


        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();
        AdRequest adRequest = new AdRequest.Builder()     // All emulators
                .addTestDevice(deviceId)  // An example device ID
                .build();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.v(e.getMessage(), e.getLocalizedMessage());
        }
        return "";
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            clarifaiManager.sendImageToClarifai(imageBitmap);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void buildCaloriesList(String[] foodNames, String[] calNums, String[] gramNums) {
        CaloriesListAdapter adapter = new CaloriesListAdapter(MainActivity.this, foodNames, calNums, gramNums);
        ListView list = (ListView) findViewById(R.id.caloriesList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //do something
            }
        });

        setCaloriesListVisibility(View.VISIBLE);
    }

    private void setCaloriesListVisibility(int visibility) {
        TextView taskDescription = (TextView) findViewById(R.id.sliding_layout_task_description);
        ListView caloriesListContainer = (ListView) findViewById(R.id.caloriesList);
        if (visibility == View.VISIBLE) {
            taskDescription.setVisibility(View.GONE);
            caloriesListContainer.setVisibility(View.VISIBLE);
        }
        else{
            taskDescription.setVisibility(View.VISIBLE);
            caloriesListContainer.setVisibility(View.GONE);
        }
    }

    private void updateCaloriesList() {
        ArrayList<String> foodNamesAL = new ArrayList<>();
        ArrayList<String> calNumsAL = new ArrayList<>();
        ArrayList<String> gramNumsAL = new ArrayList<>();

        for (Food food : foods) {
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

        this.foods.set(food.index, food);
        updateCaloriesList();
    }

    public void initGoogleSearch(ArrayList<Food> foods) {
        this.foods = foods;
        GoogleSearch search = new GoogleSearch(this);
        for (Food food : this.foods) {
            search.howManyCalories(food);
        }
    }


    public void startCamera() {

        RelativeLayout v = (RelativeLayout) findViewById(R.id.camContainer);
        surface = new SurfaceView(getApplicationContext());
        holder = surface.getHolder();

        v.addView(surface);


        camera = DefaultEasyCamera.open();
        EasyCamera.CameraActions actions = null;
        try {
            actions = camera.startPreview(holder);
        } catch (IOException e) {
            System.out.println("HEEEEERRRE");
            e.printStackTrace();
        }

        EasyCamera.PictureCallback callback = new EasyCamera.PictureCallback() {
            public void onPictureTaken(byte[] data, EasyCamera.CameraActions actions) {
                // store picture
            }
        };
        actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
    }


    public void searchBegins(){
        setLoadingVisibility(View.VISIBLE);
        setCaloriesListVisibility(View.VISIBLE);
    }

    public void searchIsDone(){
        boolean foodFound = false;
        for (Food food: foods){
            if (food.calories>0)
                foodFound=true;
        }

        if (!foodFound){
            setCaloriesListVisibility(View.GONE);
            TextView alert = (TextView) findViewById(R.id.sliding_layout_task_description);
            alert.setText(R.string.no_result_slideup_text);
        }
        setLoadingVisibility(View.GONE);
    }

    public void setLoadingVisibility(int visibility){
        RelativeLayout progressBarContainer = (RelativeLayout) findViewById(R.id.progress_bar_container);
        progressBarContainer.setVisibility(visibility);
    }
}
