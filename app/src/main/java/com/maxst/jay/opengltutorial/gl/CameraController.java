package com.maxst.jay.opengltutorial.gl;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.maxst.jay.opengltutorial.util.CameraUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by jeonguk on 2018. 3. 14..
 */

public class CameraController {
    private static volatile CameraController sInstance;

    private final String TAG = CameraController.class.getName();

    private Camera mCamera = null;

    public static CameraController getInstance() {
        if (sInstance == null) {
            synchronized (CameraController.class) {
                if (sInstance == null) {
                    sInstance = new CameraController();
                }
            }
        }
        return sInstance;
    }

    public CameraController() {}

    public void openCamera(Context context, SurfaceTexture surfaceTexture, int width, int height) {
        Log.i(TAG, "openCamera");
        final Activity activity = (Activity) context;
        if (null == activity || activity.isFinishing()) return;
        try {
            mCamera = android.hardware.Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
            if (mCamera != null) {
                Log.i(TAG, "mCamera : " + mCamera.toString());
            } else {
                Log.w(TAG, "mCamera is null");
            }
            android.hardware.Camera.Parameters parameters = mCamera.getParameters();
            List<android.hardware.Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

            android.hardware.Camera.Size size = CameraUtil.chooseOptimalSize(previewSizes, width, height);

            parameters.setPreviewSize(size.width, size.height);
            CameraUtil.setCameraDisplayOrientation(activity, mCamera, android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK, parameters);
            mCamera.setParameters(parameters);

            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        try {
            if (mCamera != null) {
                try {
                    mCamera.lock();
                } catch (Throwable ignored) {
                }
                mCamera.release();
                mCamera = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
