package com.maxst.jay.opengltutorial.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class ShaderUtil {

    public static int createProgram(String vertexSrc, String fragmentSrc) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSrc);
        if (vertexShader == 0) {
            Log.e("Load Program", "Vertex Shader Failed");
            return 0;
        }
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSrc);
        if (fragmentShader == 0) {
            Log.e("Load Program", "Fragment Shader Failed");
            return 0;
        }

        int[] link = new int[1];
        int shaderProgramId = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        GLES20.glAttachShader(shaderProgramId, vertexShader);
        checkGlError("glAttachShader vertext");
        GLES20.glAttachShader(shaderProgramId, fragmentShader);
        checkGlError("glAttachShader fragment");
        GLES20.glLinkProgram(shaderProgramId);

        GLES20.glGetProgramiv(shaderProgramId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.e("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return shaderProgramId;
    }

    public static int createProgram(Context context, @RawRes int vertexSrcRawId, @RawRes int fragmentSrcRawId) {
        return createProgram(getStringFromRaw(context, vertexSrcRawId), getStringFromRaw(context, fragmentSrcRawId));
    }

    private static int loadShader(int type, String shaderSrc) {
        int[] compiled = new int[1];
        int shader;
        shader = GLES20.glCreateShader(type);
        checkGlError("glCreateShader");
        GLES20.glShaderSource(shader, shaderSrc);
        checkGlError("glShaderSource");
        GLES20.glCompileShader(shader);
        checkGlError("glCompileShader");
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(shader));
            return 0;
        }
        return shader;
    }

    private static String getStringFromRaw(Context context, int id) {
        final InputStream inputStream = context.getResources().openRawResource(id);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String nextLine;
        final StringBuilder body = new StringBuilder();
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(ShaderUtil.class.getName(), glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


    public static int createTexture(int textureTarget, @Nullable Bitmap bitmap, int minFilter, int magFilter, int wrapS, int wrapT) {
        int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        checkGlError("glGenTextures");
        GLES20.glBindTexture(textureTarget, textureHandle[0]);
        checkGlError("glBindTexture " + textureHandle[0]);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter); //线性插值
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT);

        if (bitmap != null) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        checkGlError("glTexParameter");
        return textureHandle[0];
    }

    public static int createTexture(int textureTarget) {
        return createTexture(textureTarget, null, GLES20.GL_LINEAR, GLES20.GL_LINEAR,
                GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
    }
}
