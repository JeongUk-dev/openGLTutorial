package com.maxst.jay.opengltutorial.shape;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.maxst.jay.opengltutorial.gl.CameraController;
import com.maxst.jay.opengltutorial.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class CameraCube extends BaseShape implements SurfaceTexture.OnFrameAvailableListener {

    private SurfaceTexture surfaceTexture;
    private GLSurfaceView surfaceView;
    private CameraController mCameraController;
    private boolean isRequiredUpdateTexture = false;

    private final String vertexShader =
            "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texCoord;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform mat4 u_mvpMatrix;\n" +
                    "void main()							\n" +
                    "{										\n" +
                    "	gl_Position = u_mvpMatrix * a_position;\n" +
                    "	v_texCoord = a_texCoord; 			\n" +
                    "}										\n";

    private final String fragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform samplerExternalOES u_texture;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "	gl_FragColor = texture2D(u_texture, v_texCoord);\n" +
                    "}\n";


    private final float VERTEX_BUF[] = {
            1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1,
            1, 1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1,
            1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1,
            -1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1,
            1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1,
            1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1,
    };

    private final float TEXTURE_BUF[] = {
            0, 1, 1, 1, 1, 0, 0, 0,
            0, 1, 1, 1, 1, 0, 0, 0,
            0, 1, 1, 1, 1, 0, 0, 0,
            0, 1, 1, 1, 1, 0, 0, 0,
            0, 1, 1, 1, 1, 0, 0, 0,
            0, 1, 1, 1, 1, 0, 0, 0,
    };

    private final short INDEX_BUF[] = {
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19,
            20, 21, 22, 20, 22, 23,
    };

    public CameraCube(GLSurfaceView surfaceView) {
        super();
        this.surfaceView = surfaceView;

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_BUF).position(0);

        indexBuffer = ByteBuffer.allocateDirect(INDEX_BUF.length * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(INDEX_BUF).position(0);

        textureBuffer = ByteBuffer.allocateDirect(TEXTURE_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(TEXTURE_BUF).position(0);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
//        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        programID = ShaderUtil.createProgram(vertexShader, fragmentShader);

        positionHandle = GLES20.glGetAttribLocation(programID, "a_position");
        mvpMatrixHandle = GLES20.glGetUniformLocation(programID, "u_mvpMatrix");
        textureHandle = GLES20.glGetUniformLocation(programID, "u_texture");
        textureCoordHandle = GLES20.glGetAttribLocation(programID, "a_texCoord");


        createCubeTexture();

        mCameraController = new CameraController(surfaceView.getContext());
        mCameraController.openCamera(surfaceTexture, surfaceView.getWidth(), surfaceView.getHeight());
    }

    @Override
    public void draw(float[] viewMatrix) {
        synchronized (this) {
            if (isRequiredUpdateTexture) {
                surfaceTexture.updateTexImage();
                isRequiredUpdateTexture = false;
            }
        }

        GLES20.glUseProgram(programID);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID[0]);
        GLES20.glUniform1i(textureHandle, 0);

        float[] tmpMatrix = new float[16];

        Matrix.setIdentityM(tmpMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, rotationMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, tmpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDEX_BUF.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

//        GLES20.glDisableVertexAttribArray(positionHandle);
//        GLES20.glDisableVertexAttribArray(colorHandle);
//        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    public void createCubeTexture() {
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

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        isRequiredUpdateTexture = true;
        surfaceView.requestRender();
    }
}
