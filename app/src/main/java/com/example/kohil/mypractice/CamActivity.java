package com.example.kohil.mypractice;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Collections;

public class CamActivity extends AppCompatActivity {

    TextureView textureView;
    Button button;
    RxPermissions rxPermission;
    CameraManager cameraManager;
    private static final String TAG = "CamActivity:";
    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    Size size;
    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                setupCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
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
    private CameraCaptureSession cameraCaptureSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        textureView = (TextureView) findViewById(R.id.texture_view);
        button = (Button) findViewById(R.id.button);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        handlePermission();


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

    private void setupCamera() throws CameraAccessException {


        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        for (String camId : cameraManager.getCameraIdList()) {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(camId);
            if (cameraCharacteristics.LENS_FACING_BACK == cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)) {

                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                size = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
            }
            cameraId = camId;
        }
    }

    void openCamera() throws CameraAccessException {

        CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {

                try {
                    cameraDevice = camera;
                    createCamSession();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                camera.close();
                cameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                camera.close();
                cameraDevice = null;
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cameraManager.openCamera(cameraId, stateCallback, null);
    }

    private void createCamSession() throws CameraAccessException {

        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureRequestBuilder.addTarget(previewSurface);
        cameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                if (cameraDevice == null)
                    return;

                try {
                    CaptureRequest captureRequest = captureRequestBuilder.build();
                    cameraCaptureSession = session;
                    cameraCaptureSession.setRepeatingRequest(captureRequest, null, null);

                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        }, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (textureView.isAvailable()) {
            try {
                setupCamera();
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else
            textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }
}
