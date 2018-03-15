package com.maxst.jay.opengltutorial.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.maxst.jay.opengltutorial.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class Triangle extends BaseShape {

    private String vertexShader =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private String fragmentShader =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private float[] VERTEX_BUF = {
            0.0f, 0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };

    private float[] COLOR_BUF = {
            0.63671875f, 0.76953125f, 0.22265625f, 0.0f
    };

    public Triangle() {
        super();

        ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX_BUF.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(VERTEX_BUF);
        vertexBuffer.position(0);

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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_BUF.length / 3);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
