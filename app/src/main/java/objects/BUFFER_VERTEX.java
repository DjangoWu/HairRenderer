package objects;

import utils.Vector2;
import utils.Vector3;

/**
 * Created by Wujiqiang on 2017/3/2.
 */

public class BUFFER_VERTEX {
    public Vector3 position;
    public Vector3 tangent;
    public Vector2 reserved;

    public BUFFER_VERTEX() {
        position = new Vector3();
        tangent = new Vector3();
        reserved = new Vector2();
    }

    /*public BUFFER_VERTEX(Vector.Vector3 position, Vector.Vector3 tangent, Vector.Vector2 reserved) {
        this.position = position;
        this.tangent = tangent;
        this.reserved = reserved;
    }*/
}

