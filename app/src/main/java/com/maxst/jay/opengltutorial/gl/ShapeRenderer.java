package com.maxst.jay.opengltutorial.gl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.maxst.jay.opengltutorial.shape.BaseShape;
import com.maxst.jay.opengltutorial.shape.CameraCube;
import com.maxst.jay.opengltutorial.shape.Cube;
import com.maxst.jay.opengltutorial.shape.Square;
import com.maxst.jay.opengltutorial.shape.Triangle;

import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class ShapeRenderer extends MyRenderer {

    RendererType type;
    BaseShape shape;
    GLSurfaceView glSurfaceView;

    public ShapeRenderer(GLSurfaceView view, RendererType type) {
        this.type = type;
        this.glSurfaceView = view;
    }
    public ShapeRenderer(RendererType type) {
        this.type = type;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        switch (type) {
            case triangle:
                shape = new Triangle();
                break;
            case square:
                shape = new Square();
                break;
            case cube:
                shape = new Cube();
                break;
            case cameraCube:
                shape = new CameraCube(glSurfaceView);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float[] projectionMatrix = new float[16];
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);

        shape.setProjectionMatrix(projectionMatrix);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] mViewMatrix = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        shape.setRotation(cubeRotationAngle, 1, 1, 1);
        shape.draw(mViewMatrix);
        updateCubeRotation();
    }

    @Override
    public void close() {
//        GLES20.glDeleteTextures(1, shape.getTextureID(), 0);
    }

    private static final float CUBE_ROTATION_INCREMENT = 0.6f;
    private static final int REFRESH_RATE_FPS = 60;
    private static final float FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;
    private float cubeRotationAngle;
    private long mLastUpdateMillis;
    private void updateCubeRotation() {
        if (mLastUpdateMillis != 0) {
            float factor = (SystemClock.elapsedRealtime() - mLastUpdateMillis) / FRAME_TIME_MILLIS;
            cubeRotationAngle += CUBE_ROTATION_INCREMENT * factor;
        }
        mLastUpdateMillis = SystemClock.elapsedRealtime();
    }
}
