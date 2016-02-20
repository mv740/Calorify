package com.example.lenovo.calorify;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lenovo.calorify.Utilities.CaloriesListAdapter;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    EasyCamera camera;
    SurfaceView surface;
    SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*EasyCamera camera = DefaultEasyCamera.open();
        EasyCamera.CameraActions actions = camera.startPreview(surface);
        Camera.PictureCallback callback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, EasyCamera.CameraActions actions) {
                // store picture
            }
        };
        actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));*/
        //setTitleTypeface();
        populateCaloriesList();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/brainflower.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        startCamera();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void buildCaloriesList(String[] foodNames, String[] calNums){
        CaloriesListAdapter adapter = new CaloriesListAdapter(MainActivity.this, foodNames, calNums);
        ListView list = (ListView)findViewById(R.id.caloriesList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //do something
            }
        });

        togglePanelContent();
    }

    private void populateCaloriesList(){
        String[] foodNames = {"salut1", "salut2", "salut3"};
        String[] calNums = {"100", "200", "300"};
        buildCaloriesList(foodNames, calNums);
    }

    private void togglePanelContent(){
        TextView taskDescription = (TextView) findViewById(R.id.sliding_layout_task_description);
        ListView caloriesList = (ListView) findViewById(R.id.caloriesList);

        if (taskDescription.getVisibility() == View.VISIBLE) {
            taskDescription.setVisibility(View.GONE);
            caloriesList.setVisibility(View.VISIBLE);
        }
        else{
            taskDescription.setVisibility(View.VISIBLE);
            caloriesList.setVisibility(View.GONE);
        }
    }

    public void startCamera(){

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
}
