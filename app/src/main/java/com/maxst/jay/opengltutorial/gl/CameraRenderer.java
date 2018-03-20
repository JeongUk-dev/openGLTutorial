package com.maxst.jay.opengltutorial.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.maxst.jay.opengltutorial.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class CameraRenderer extends MyRenderer implements SurfaceTexture.OnFrameAvailableListener {

    private int[] textureID;
    private SurfaceTexture surfaceTexture;
    private GLSurfaceView surfaceView;
    private boolean isRequiredUpdateTexture = false;
    private int programID;

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;

    private float[] VERTEX_BUF = {
             1.0f, -1.0f,
            -1.0f, -1.0f,
             1.0f,  1.0f,
            -1.0f,  1.0f
    };
    private float[] TEXTURE_BUF = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };
    private short[] INDEX_BUF = {};

    public CameraRenderer(GLSurfaceView view) {
        surfaceView = view;

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_BUF).position(0);

        textureBuffer = ByteBuffer.allocateDirect(TEXTURE_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(TEXTURE_BUF).position(0);

        indexBuffer = ByteBuffer.allocateDirect(INDEX_BUF.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(INDEX_BUF).position(0);

    }

    public void onPause() {

    }

    public void onResume() {

    }

    @Override
    public void close() {
        isRequiredUpdateTexture = false;
        surfaceTexture.release();
        CameraController.getInstance().closeCamera();
        deleteTexture();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        initCameraTexutre();
        CameraController.getInstance().openCamera(surfaceView.getContext(), surfaceTexture, surfaceView.getWidth(), surfaceView.getHeight());

        programID = ShaderUtil.createProgram(vertexShader, fragmentShader);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        synchronized (this) {
            if (isRequiredUpdateTexture) {
                surfaceTexture.updateTexImage();
                isRequiredUpdateTexture = false;
            }
        }

        GLES20.glUseProgram(programID);

        int ph = GLES20.glGetAttribLocation(programID, "vPosition");
        int tch = GLES20.glGetAttribLocation(programID, "vTexCoord");
        int th = GLES20.glGetUniformLocation(programID, "sTexture");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID[0]);
        GLES20.glUniform1i(th, 0);

        GLES20.glVertexAttribPointer(ph, 2, GLES20.GL_FLOAT, false, 4 * 2, vertexBuffer);
        GLES20.glVertexAttribPointer(tch, 2, GLES20.GL_FLOAT, false, 4 * 2, textureBuffer);
        GLES20.glEnableVertexAttribArray(ph);
        GLES20.glEnableVertexAttribArray(tch);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFlush();
    }

    private void initCameraTexutre() {
        textureID = new int[1];
        GLES20.glGenTextures(1, textureID, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        surfaceTexture = new SurfaceTexture(textureID[0]);
        surfaceTexture.setOnFrameAvailableListener(this);
    }

    private void deleteTexture() {
        GLES20.glDeleteTextures(1, textureID, 0);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        isRequiredUpdateTexture = true;
        surfaceView.requestRender();
    }
}
