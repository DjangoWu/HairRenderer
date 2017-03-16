precision mediump float;

uniform sampler2D u_TextureUnit;
uniform sampler2D g_samMarschner1;
uniform sampler2D g_samMarschner2;

uniform float g_fKa;        // ambient
uniform float g_fKr;


varying vec2 v_TextureCoordinates;
varying vec4 vColor;
varying vec3 vMarCoord;
varying vec3 vDomCoord;
void main() {
    // Get values in precomputed textures
    vec4 vTex1 = texture2D(g_samMarschner1, vMarCoord.xy);
    vec4 vTex2 = texture2D(g_samMarschner2, vec2(vMarCoord.z, vTex1.a));

    // Get color in hack texture
    vec4 vTexColor = texture2D(u_TextureUnit, vColor.gb);

    // Compose different terms
    vec4 fColor;
    fColor.rgb = g_fKa * vTexColor +    // ambient
        (vColor.r * vTexColor +         // diffuse
        g_fKr * vtex1.r * vtex2.a);     // reflection

    vColor.rgb *= vColor.a;
    vColor.a = 1.0;

    gl_FragColor = vColor;
    //gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}
