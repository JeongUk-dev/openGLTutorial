package com.maxst.jay.opengltutorial;

import android.Manifest;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.maxst.jay.opengltutorial.databinding.ActivityMainBinding;
import com.maxst.jay.opengltutorial.gl.MyGLSurfaceView;
import com.maxst.jay.opengltutorial.gl.RendererType;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding mainBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainBinding.triangleBtn.setOnClickListener(this);
        mainBinding.squareBtn.setOnClickListener(this);
        mainBinding.cubeBtn.setOnClickListener(this);
        mainBinding.cameraBtn.setOnClickListener(this);
        mainBinding.cameraCubeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MyGLSurfaceView surfaceView = null;
        switch (v.getId()) {
            case R.id.triangleBtn:
                surfaceView = new MyGLSurfaceView(this, RendererType.triangle);
                break;
            case R.id.squareBtn:
                surfaceView = new MyGLSurfaceView(this, RendererType.square);
                break;
            case R.id.cubeBtn:
                surfaceView = new MyGLSurfaceView(this, RendererType.cube);
                break;
            case R.id.cameraBtn:
                MainActivityPermissionsDispatcher.showCameraWithPermissionCheck(this);
                surfaceView = new MyGLSurfaceView(this, RendererType.camera);
                break;
            case R.id.cameraCubeBtn:
                surfaceView = new MyGLSurfaceView(this, RendererType.cameraCube);
                break;
        }

        mainBinding.content.removeAllViews();
        mainBinding.content.addView(surfaceView);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void showCamera() {

    }

    @OnShowRationale(Manifest.permission.CAMERA)
    public void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("카메라 써?")
                .setPositiveButton("ㅇㅋ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("ㄴㄴ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    public void showDeniedForCamera() {
        Toast.makeText(this, "카메라 안씀", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void showNeverAskForCamera() {
        Toast.makeText(this, "카메라 못씀", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
