package com.maxst.jay.opengltutorial.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by jeonguk on 2018. 3. 13..
 */

public class MyGLSurfaceView extends GLSurfaceView {

    RendererType type;
    MyRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    public MyGLSurfaceView(Context context, RendererType type) {
        super(context);
        this.type = type;
        setEGLContextClientVersion(2);

        switch (type) {
            case triangle:
            case square:
            case cube:
            case cameraCube:
                renderer = new ShapeRenderer(this, type);
                break;
            case camera:
                renderer = new CameraRenderer(this);
                break;
        }

        setRenderer(renderer);

        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        renderer.close();
    }
}
