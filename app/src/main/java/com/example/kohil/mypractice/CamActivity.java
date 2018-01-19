package com.example.kohil.mypractice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.Toast;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;

public class CamActivity extends AppCompatActivity {

    TextureView textureView;
    ImageView photo, video;
    RxPermissions rxPermission;
    CameraManager cameraManager;
    private static final String TAG = "CamActivity:";
    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    Size size;
    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            connectCamera();
        }


        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    private String cameraId;
    private CameraDevice cameraDevice;
    String fileName;
    File camera2VideoImagesDirectory;

    CameraDevice.StateCallback cameraDeviceCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    CaptureRequest.Builder captureRequestBuilder;
    HandlerThread backgroundHandlerThread;
    Handler backgroungHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        handlePermission();
        createFolder();

        Log.d(TAG, "onCreate: "+fileName(true).getAbsolutePath());
        Log.d(TAG, "onCreate: "+fileName(false).getAbsolutePath());
        textureView = (TextureView) findViewById(R.id.texture_view);
        photo = (ImageView) findViewById(R.id.photo);
        video = (ImageView) findViewById(R.id.video);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    void handlePermission() {
        rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(perms)
                .subscribe(permission -> {
                    if (permission.granted) {
                        Log.d(TAG, "onCreate: Done");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        rxPermission.shouldShowRequestPermissionRationale(this, permission.name);
                    } else {
                        Log.d(TAG, "onCreate: Nothing");
                    }
                });
    }


    void closeCamera() {

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startThread();
        if (textureView.isAvailable()) {
            setupCamera(textureView.getWidth(), textureView.getHeight());
            connectCamera();
        } else
            textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        stopThread();

    }

    void setupCamera(int width, int height) {
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String cameId : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraCharacteristics.LENS_FACING_FRONT)
                    continue;
                cameraId = cameId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void setupMediaRecorder(){


        MediaRecorder recorder = null;
        try {
            recorder = new MediaRecorder();
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            recorder.setOutputFile(fileName(true).getAbsolutePath());
            recorder.prepare();
            recorder.start();   // Recording is now started
        } catch (Exception e) {
            e.printStackTrace();
        }





    }
    void connectCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            cameraManager.openCamera(cameraId, cameraDeviceCallBack, backgroungHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void startThread() {

        backgroundHandlerThread = new HandlerThread("Camera2Thread");
        backgroundHandlerThread.start();
        backgroungHandler = new Handler(backgroundHandlerThread.getLooper());

    }

    void stopThread() {
        if (backgroundHandlerThread != null) {

            backgroundHandlerThread.quitSafely();
            try {
                backgroundHandlerThread.join();
                backgroundHandlerThread = null;
                backgroungHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void startPreview() {
        try {

            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());

            Surface surfacePreview = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surfacePreview);
            cameraDevice.createCaptureSession(Arrays.asList(surfacePreview), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Toast.makeText(CamActivity.this, "Configured", Toast.LENGTH_SHORT).show();
                    try {
                        session.setRepeatingRequest(captureRequestBuilder.build(), null, backgroungHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(CamActivity.this, "Configure failed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    void createFolder(){

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        camera2VideoImagesDirectory = new File(file, "camera2VideoImagesDirectory");

        if(camera2VideoImagesDirectory.exists()){

        }else{
            camera2VideoImagesDirectory.mkdir();
        }
    }

    File fileName(boolean isImage){

        String timeStamp = new SimpleDateFormat("yyyyDDmm_HHmmss").format(new Date());
        String prefix = isImage ? "Image_":"Video_";
        File tempFile = null;
        try {
            tempFile = File.createTempFile(prefix, timeStamp, camera2VideoImagesDirectory);
            fileName = tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile;
    }
}
