package com.maxst.jay.opengltutorial.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.maxst.jay.opengltutorial.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class Square extends BaseShape {

    private final String vertexShader =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShader =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private static float VERTEX_BUF[] = {
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f // top right
    };

    private final short INDEX_BUF[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices

    private float COLOR_BUF[] = {0.2f, 0.709803922f, 0.898039216f, 1.0f};

    public Square() {
        super();

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_BUF.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_BUF).position(0);

        indexBuffer = ByteBuffer.allocateDirect(INDEX_BUF.length * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(INDEX_BUF).position(0);

        programID = ShaderUtil.createProgram(vertexShader, fragmentShader);

        positionHandle = GLES20.glGetAttribLocation(programID, "vPosition");
        mvpMatrixHandle = GLES20.glGetUniformLocation(programID, "uMVPMatrix");
        colorHandle = GLES20.glGetUniformLocation(programID, "vColor");
    }

    @Override
    public void draw(float[] viewMatrix) {
        GLES20.glUseProgram(programID);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);


        GLES20.glUniform4fv(colorHandle, 1, COLOR_BUF, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDEX_BUF.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
