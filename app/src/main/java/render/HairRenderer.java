package render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import objects.BufferView;
import objects.TriangleTest;
import utils.CameraPosition;
import utils.MatrixState;

/**
 * Created by Wujiqiang on 2017/3/2.
 */

public class HairRenderer implements GLSurfaceView.Renderer {
    private Context context;

    BufferView bufferView;

    TriangleTest triangle;

    private static final String TAG = "HairRenderer";

    public HairRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        MatrixState.initMMatrix();
        bufferView = new BufferView(context);
        // triangle = new TriangleTest(context);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface
        GLES20.glViewport(0, 0, width, height);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        float ratio = (float) width / height;

        // 设置投影矩阵
        MatrixState.perspectiveM(45, ratio, 0.1f, 100.0f);
        // 调用此方法尝试摄像机9参数位置矩阵
        MatrixState.setCamera(CameraPosition.getX(), CameraPosition.getY(), CameraPosition.getZ(), 0f, 0f, 0f, 0f, 1.0f, 0.0f);


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the rendering surface
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //Log.d(TAG, "onDrawFrame");

        //triangle.draw();
        MatrixState.setCamera(CameraPosition.getX(), CameraPosition.getY(), CameraPosition.getZ(), 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        bufferView.RenderHair();

    }


}
