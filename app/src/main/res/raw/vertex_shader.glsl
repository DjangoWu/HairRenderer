uniform mat4 u_Matrix;
attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;

void main() {
    gl_Position = u_Matrix * vec4(a_Position.x * 3 , a_Position.y * 3 , a_Position.z * 3 , 1.0f);//a_Position * 10;
    v_TextureCoordinates = a_TextureCoordinates;
}
