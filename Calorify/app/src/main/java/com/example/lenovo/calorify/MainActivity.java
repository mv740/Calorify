package com.example.lenovo.calorify;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;
import com.example.lenovo.calorify.Authentication.Credential;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;


import com.example.lenovo.calorify.Utilities.CaloriesListAdapter;
import com.example.lenovo.calorify.Utilities.Food;
import com.example.lenovo.calorify.Utilities.GoogleSearch;
import com.example.lenovo.calorify.Utilities.ClarifaiManager;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.IOException;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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


        //dispatchTakePictureIntent();

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
        MaterialProgressBar progressBar = (MaterialProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(visibility);
    }
}
