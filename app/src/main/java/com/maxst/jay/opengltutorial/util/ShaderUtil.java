package com.maxst.jay.opengltutorial.util;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class ShaderUtil {

    public static int createProgram(String vertexSrc, String fragmentSrc) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSrc);
        if (vertexShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSrc);
        if (fragmentShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
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
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return shaderProgramId;
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
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(shader));
            return 0;
        }
        return shader;
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(ShaderUtil.class.getName(), glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
