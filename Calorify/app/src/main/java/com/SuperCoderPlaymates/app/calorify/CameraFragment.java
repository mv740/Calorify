package com.SuperCoderPlaymates.app.calorify;


import java.io.IOException;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.SuperCoderPlaymates.app.calorify.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class CameraFragment extends Fragment {

    public static final String TAG = "CameraFragment";

    private Camera camera;
    private SurfaceView surfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);
        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);

        prepareCamera();

        surfaceView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (camera == null)
                    return;
                camera.takePicture(new Camera.ShutterCallback() {

                    @Override
                    public void onShutter() {
                        // nothing to do
                        camera.enableShutterSound(true);


                    }

                }, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
                        layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        MainActivity.clarifaiManager.sendImageToClarifai(bitmap);
                    }

                });

            }
        });


        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // nothing here
            }

        });

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        prepareCamera();
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void prepareCamera(){

        if (camera == null) {
            try {
                camera = Camera.open();
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(p);
                surfaceView.setEnabled(true);
            } catch (Exception e) {
                Log.e(TAG, "No camera with exception: " + e.getMessage());
                surfaceView.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}