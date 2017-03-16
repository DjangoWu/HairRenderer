package utils;

/**
 * Created by Wujiqiang on 2017/3/2.
 */

public class Vector3 {
    private float x, y, z;
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3() {}

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3 minus(Vector3 others) {
        return new Vector3(
                this.x - others.x,
                this.y - others.y,
                this.z - others.z);
    }

    public float length() {
        return (float)Math.sqrt(
                x * x + y * y + z * z);
    }

    public Vector3 Normalzied() {
        return new Vector3(
                this.x / this.length(),
                this.y / this.length(),
                this.z / this.length());
    }

}
