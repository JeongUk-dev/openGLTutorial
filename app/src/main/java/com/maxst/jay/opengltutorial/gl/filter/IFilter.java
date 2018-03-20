package com.maxst.jay.opengltutorial.gl.filter;

import java.nio.FloatBuffer;

/**
 * Created by jeonguk on 2018. 3. 20..
 */

public interface IFilter {
    int getTextureTarget();

    void setTextureSize(int width, int height);

    void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride);

    void releaseProgram();
}
