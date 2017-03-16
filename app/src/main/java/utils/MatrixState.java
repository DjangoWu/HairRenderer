package utils;

import android.opengl.Matrix;

/**
 * Created by Wujiqiang on 2017/2/13.
 */

// 存储系统矩阵状态的类
public class MatrixState {
    private static float[] mProjMatrix = new float[16];
    private static float[] mVMatrix = new float[16];
    private static float[] mMMatrix = new float[16];
    // 绕X轴旋转矩阵
    private static float[] mRotateXMatrix = new float[16];
    // 绕Y轴旋转矩阵
    private static float[] mRotateYMatrix = new float[16];
    private static float[] mRotateMatrix = new float[16];
    // 缩放矩阵
    private static float[] mScaleMatrix = new float[16];
    //private static float[]

    public static void setScale(float x, float y, float z) {
        Matrix.scaleM(mScaleMatrix, 0, x, y, z);
    }
    public static void setRotateX(float angle) {
        Matrix.rotateM(mRotateXMatrix, 0, angle, 1, 0, 0);
    }
    public static void setRotateY(float angle) {
        Matrix.rotateM(mRotateYMatrix, 0, angle, 0, 1, 0);
    }

    public static void initMMatrix() {
        Matrix.setIdentityM(mRotateXMatrix, 0);
        Matrix.setIdentityM(mRotateYMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
    }
    // 设置摄像机
    public static void setCamera(float cx, // 摄像机位置x
                                 float cy, // 摄像机位置y
                                 float cz, // 摄像机位置z
                                 float tx, // 摄像机目标点x
                                 float ty, // 摄像机目标点y
                                 float tz, // 摄像机目标点z
                                 float upx, // 摄像机UP向量X分量
                                 float upy, // 摄像机UP向量Y分量
                                 float upz // 摄像机UP向量Z分量
    )
    {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
    }

    // 设置透视投影参数
    public static void setProjectFrustum(float left, // near面的left
                                         float right, // near面的right
                                         float bottom, // near面的bottom
                                         float top, // near面的top
                                         float near, // near面距离
                                         float far // far面距离
    )
    {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f)
    public static void perspectiveM(float yFovInDegrees, float aspect,
                                    float n, float f) {
        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);

        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));
        mProjMatrix[0] = a / aspect;
        mProjMatrix[1] = 0f;
        mProjMatrix[2] = 0f;
        mProjMatrix[3] = 0f;

        mProjMatrix[4] = 0f;
        mProjMatrix[5] = a;
        mProjMatrix[6] = 0f;
        mProjMatrix[7] = 0f;

        mProjMatrix[8] = 0f;
        mProjMatrix[9] = 0f;
        mProjMatrix[10] = -((f + n) / (f - n));
        mProjMatrix[11] = -1f;

        mProjMatrix[12] = 0f;
        mProjMatrix[13] = 0f;
        mProjMatrix[14] = -((2f * f * n) / (f - n));
        mProjMatrix[15] = 0f;
    }

    static float[] mMVPMatrix = new float[16];
    static float[] mMVMatrix = new float[16];

    public static float[] getFinalMatrix()
    {
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.setIdentityM(mRotateMatrix, 0);

        Matrix.multiplyMM(mRotateMatrix, 0, mRotateXMatrix, 0, mRotateYMatrix, 0);

        Matrix.multiplyMM(mMMatrix, 0, mRotateMatrix, 0, mScaleMatrix, 0);

        Matrix.multiplyMM(mMVMatrix, 0, mVMatrix, 0, mMMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVMatrix, 0);
        return mMVPMatrix;
    }
}
