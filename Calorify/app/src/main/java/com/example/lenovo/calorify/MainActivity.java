package com.example.lenovo.calorify;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

public class MainActivity extends AppCompatActivity {

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
    }
}
