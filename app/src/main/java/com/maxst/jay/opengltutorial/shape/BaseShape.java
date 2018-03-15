package com.maxst.jay.opengltutorial.shape;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public abstract class BaseShape {

    protected int programID = -1;
    protected int[] textureID = {-1};

    protected int positionHandle;
    protected int colorHandle;
    protected int mvpMatrixHandle;
    protected int textureCoordHandle;
    protected int textureHandle;

    protected float[] mvpMatrix;
    protected float[] projectionMatrix;
    protected float[] rotationMatrix;
    protected float[] scaleMatrix;
    protected float[] translateMatrix;
    protected float[] transformMatrix;


    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;
    FloatBuffer textureBuffer;
    ShortBuffer indexBuffer;

    public BaseShape() {
        mvpMatrix = new float[16];
        projectionMatrix = new float[16];
        rotationMatrix = new float[16];
        scaleMatrix = new float[16];
        translateMatrix = new float[16];
        transformMatrix = new float[16];


        Matrix.setIdentityM(mvpMatrix, 0);
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.setIdentityM(transformMatrix, 0);
        Matrix.setIdentityM(translateMatrix, 0);
    }

    public abstract void draw(float[] viewMatrix);

    public void setProjectionMatrix(float [] projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setScale(float x, float y, float z) {
//        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, x, y, z);
    }

    public void setTranslate(float x, float y, float z) {
//        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, x, y, z);
    }

    public void setRotation(float angle, float x, float y, float z) {
//        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setRotateM(rotationMatrix, 0, angle, x, y, z);
    }

    public void setTransform(float[] transform) {
        System.arraycopy(transform, 0, this.transformMatrix, 0, transform.length);
    }

    public int[] getTextureID() {
        return textureID;
    }




}
