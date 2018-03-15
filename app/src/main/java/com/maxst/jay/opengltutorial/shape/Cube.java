package com.maxst.jay.opengltutorial.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.maxst.jay.opengltutorial.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class Cube extends BaseShape {

    private final String vertexShader =
            "attribute vec4 a_position;" +
                    "attribute vec4 a_color;" +
                    "attribute vec3 a_normal;" +
                    "uniform mat4 u_VPMatrix;" +
                    "uniform vec3 u_LightPos;" +
                    "varying vec3 v_texCoords;" +
                    "attribute vec3 a_texCoords;" +
                    "void main()" +
                    "{" +
                    "v_texCoords = a_texCoords;" +
                    "gl_Position = u_VPMatrix * a_position;" +
                    "}";

    private final String fragmentShader =
            "precision mediump float;" +
                    "uniform samplerCube u_texId;" +
                    "varying vec3 v_texCoords;" +
                    "void main()" +
                    "{" +
                    "gl_FragColor = textureCube(u_texId, v_texCoords);" +
                    "}";


    private float[] VERTEX_BUF = {
            1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1,
            1, 1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1,
            1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1,
            -1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1,
            1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1,
            1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1,
    };

    private short[] INDEX_BUF = {
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19,
            20, 21, 22, 20, 22, 23,
    };

    private float[] TEXTURE_BUF = {
            1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, //0-1-2-3 front
            1, 1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1,//0-3-4-5 right
            1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1,//4-7-6-5 back
            -1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1,//1-6-7-2 left
            1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, //top
            1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1,//bottom
    };

    public Cube() {
        super();

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_BUF).position(0);

        indexBuffer = ByteBuffer.allocateDirect(INDEX_BUF.length * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(INDEX_BUF).position(0);

//        colorBuffer = ByteBuffer.allocateDirect(COLOR_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        colorBuffer.put(COLOR_BUF).position(0);

        textureBuffer = ByteBuffer.allocateDirect(TEXTURE_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(TEXTURE_BUF).position(0);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        programID = ShaderUtil.createProgram(vertexShader, fragmentShader);

        positionHandle = GLES20.glGetAttribLocation(programID, "a_position");
        mvpMatrixHandle = GLES20.glGetUniformLocation(programID, "u_VPMatrix");
        textureHandle = GLES20.glGetUniformLocation(programID, "u_texId");
        textureCoordHandle = GLES20.glGetAttribLocation(programID, "a_texCoords");


        textureID[0] = createCubeTexture();
    }

    @Override
    public void draw(float[] viewMatrix) {


        GLES20.glUseProgram(programID);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(textureCoordHandle, 3, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);




        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureID[0]);
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

    public int createCubeTexture() {
        int[] textureId = new int[1];

        // Face 0 - Red
        byte[] cubePixels0 = {127, 0, 0};
        // Face 1 - Green
        byte[] cubePixels1 = {0, 127, 0};
        // Face 2 - Blue
        byte[] cubePixels2 = {0, 0, 127};
        // Face 3 - Yellow
        byte[] cubePixels3 = {127, 127, 0};
        // Face 4 - Purple
        byte[] cubePixels4 = {127, 0, 127};
        // Face 5 - White
        byte[] cubePixels5 = {127, 127, 127};

        ByteBuffer cubePixels = ByteBuffer.allocateDirect(3);

        // Generate a texture object
        GLES20.glGenTextures(1, textureId, 0);

        // Bind the texture object
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId[0]);

        // Load the cube face - Positive X
        cubePixels.put(cubePixels0).position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLES20.GL_RGB, 1, 1, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, cubePixels);

        // Load the cube face - Negative X
        cubePixels.put(cubePixels1).position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLES20.GL_RGB, 1, 1, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, cubePixels);

        // Load the cube face - Positive Y
        cubePixels.put(cubePixels2).position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLES20.GL_RGB, 1, 1, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, cubePixels);

        // Load the cube face - Negative Y
        cubePixels.put(cubePixels3).position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLES20.GL_RGB, 1, 1, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, cubePixels);

        // Load the cube face - Positive Z
        cubePixels.put(cubePixels4).position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLES20.GL_RGB, 1, 1, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, cubePixels);

        // Load the cube face - Negative Z
        cubePixels.put(cubePixels5).position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLES20.GL_RGB, 1, 1, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, cubePixels);

        // Set the filtering mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        return textureId[0];
    }
}
