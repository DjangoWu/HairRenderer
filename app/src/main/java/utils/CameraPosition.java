package utils;

/**
 * Created by Wujiqiang on 2017/3/8.
 */

public  class CameraPosition {
    private static float x = 3.0f;
    private static float y = 0.0f;
    private static float z = 0.0f;

    public static float getX() {
        return x;
    }

    public static void setX(float x) {
        CameraPosition.x = x;
    }

    public static float getY() {
        return y;
    }

    public static void setY(float y) {
        CameraPosition.y = y;
    }

    public static float getZ() {
        return z;
    }

    public static void setZ(float z) {
        CameraPosition.z = z;
    }
}
