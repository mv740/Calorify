package com.example.lenovo.calorify;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SurfaceView surface;
    SurfaceHolder holder;
    Button buttonClick;
    Camera camera;
    Activity activity;
    Context context;
    Preview preview;

    private final String TAG_C = "Clarifai";

    private ClarifaiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new ClarifaiClient(Credential.CLIENT_ID, Credential.CLIENT_SECRET);

        Bitmap bitmapSelected = getBitmapFromAsset(getApplicationContext(),"kitten.jpeg");

        context = getApplicationContext();
        surface = (SurfaceView) findViewById(R.id.camContainer);
        preview = new Preview(context, surface);

        int camId = findCameraId();
        safeCameraOpen(camId);



        //enable to send image
        //sendImageToClarifai(bitmapSelected);



 populateCaloriesList();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/brainflower.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

    }

    private void sendImageToClarifai(Bitmap bitmapSelected) {
        Log.d("Clarifai", "start");
        new AsyncTask<Bitmap,Void,RecognitionResult>() {
            @Override
            protected RecognitionResult doInBackground(Bitmap[] bitmaps) {
                Log.d(TAG_C, "doInBackground");
                return recognizeFile(bitmaps[0]);
            }
            @Override protected void onPostExecute(RecognitionResult result) {
                //
                Log.d(TAG_C,result.getTags().toString());
            }

        }.execute(bitmapSelected);
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
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

    /** Sends the given bitmap to Clarifai for recognition and returns the result. */
    private RecognitionResult recognizeFile(Bitmap bitmap) {
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
        ScrollView caloriesListContainer = (ScrollView) findViewById(R.id.caloriesListContainer);

        if (taskDescription.getVisibility() == View.VISIBLE) {
            taskDescription.setVisibility(View.GONE);
            caloriesListContainer.setVisibility(View.VISIBLE);
        }
        else{
            taskDescription.setVisibility(View.VISIBLE);
            caloriesListContainer.setVisibility(View.GONE);
        }
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
            qOpened = (camera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }
        return qOpened;
    }

    private void releaseCameraAndPreview() {
        preview.setCamera(camera);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private int findCameraId(){
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
            }
        } return cameraId;
    }



   /* public void startCamera(){

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
        //actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
    }*/
}
