package example.com.hairrenderer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import render.HairRenderer;
import utils.CameraPosition;
import utils.LogUtil;
import utils.MatrixState;

public class RenderActivity extends AppCompatActivity {

    /*
    * Hold a reference to our GLSurfaceView
    * */
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();

        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        final HairRenderer hairRenderer = new HairRenderer(this);

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer
            glSurfaceView.setRenderer(hairRenderer);
            //glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            rendererSet = true;
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since we're
             * not doing anything, the app will crash if the device doesn't
             * support OpenGL ES 2.0. If we publish on the market, we should
             * also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }


        setContentView(glSurfaceView);
        //setContentView(R.layout.activity_render);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(rendererSet){
            glSurfaceView.onResume();
        }
    }

    private float cx = CameraPosition.getX();
    private float cy = CameraPosition.getY();
    private float cz = CameraPosition.getZ();

    private final float r = 3.0f;

    /*private float alpha = 0.0f;
    private float theta = 0.0f;*/

    private float PreviousX;
    private float PreviousY;

    private float currentX;
    private float currentY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PreviousX = event.getX();
                PreviousY = event.getY();
                LogUtil.v("Action_Down ", " x = " +PreviousX + " y = " + PreviousY);
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                float dx = (currentX - PreviousX) / 500;
                float dy = (PreviousY - currentY) / 500;

                LogUtil.v("Action_Move ", "dx:" + dx + "  dy:" + dy);

                // alpha 表示在XZ平面的偏移量（弧度值）
                float alpha = (dx * 180.0f) / (r * 3.14f);
                MatrixState.setRotateY(alpha);
                // theta 表示在YZ平面的偏移量
                float theta = (dy * 180.0f) / (r * 3.14f);
                MatrixState.setRotateX(theta);

                /*LogUtil.v("Radius ", "alpha = " + alpha + " theta = " + theta );
                // 对于横向移动(在XZ平面)
                cx = cx + r * (float)Math.sin(alpha);
                cz = r * (float)Math.cos(alpha);
                // 对于纵向运动（在YZ平面）
                cy = cy + r * (float)Math.sin(theta);
                cz = r * (float) Math.cos(theta);
*/


                //CameraPosition.setX(cx);
                //CameraPosition.setY(cy);
               // CameraPosition.setZ(cz);

                //LogUtil.v("CameraPos", " x = " + CameraPosition.getX() + " y = " + CameraPosition.getY() + " z = " + CameraPosition.getZ());

                //glSurfaceView.requestRender();
        }
        /*mPreviousY = x;
        mPreviousY = y;
*/
        return true;
    }
}
