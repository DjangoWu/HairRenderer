uniform mat4 u_Matrix;

uniform vec3 g_vCameraPos;
uniform vec3 g_vLightDir;
uniform float g_fKd;

uniform mat4 g_mLightViewProj;

attribute vec4 a_Position;
attribute vec3 a_Tangent;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;

varying vec4 vColor;
varying vec3 vMarCoord;
varying vec3 vDomCoord;



void main() {

    gl_Position = u_Matrix * vec4(a_Position.x * 3 , a_Position.y * 3 , a_Position.z * 3 , 1.0f);//a_Position * 10;

    // vector for lighting
    vec3 vCameraDir = normalize(g_vCameraPos - a_Position.xyz);
    float fSinThetaI = dot(g_vLightDir, a_Tangent);
    float fSinThetaO = dot(vCameraDir, a_Tangent);
    vec3 vLightPerp = g_vLightDir - fSinThetaI * a_Tangent;
    vec3 vCameraPerp = vCameraDir - fSinThetaO * a_Tangent;
    float fCosPhiD = dot(vCameraPerp, vLightPerp) * rsqrt(dot(vCameraPerp, vCameraPerp) * dot(vLightPerp, vLightPerp));

    // Compute diffuse and pass to fragment shader
    vColor.r = DiffuseFactor(vLightPerp, fCosPhiD);
    vColor.gb = a_TextureCoordinates.gr;

    // Also compute a HACK factor
    vColor.a = HackFactor(a_TextureCoordinates.g, a_TextureCoordinates.r);
    // Compue texcoord for marschner model
    vMarCoord.x = (fSinThetaI + 1.0) * 0.5;
    vMarCoord.y = (fSinThetaO + 1.0) * 0.5;
    vMarCoord.z = (fCosPhiD + 1.0) * 0.5;

    // shadow coordinates
    vec4 vPosShadow = mul(a_Position, g_mLightViewProj);
    vDOMCoord.x = (vPosShadow.x + 1.0f) / 2.0f + 0.0009765625;
    vDOMCoord.y = (1.0f - vPosShadow.y) / 2.0f + 0.0009765625;
    vDOMCoord.z = vPosShadow.z;

    v_TextureCoordinates = a_TextureCoordinates;
}

//----------------------------------------------------------------------
// FUNCTION: return the diffuse factor.
//----------------------------------------------------------------------
float DiffuseFactor(vec3 vLightPerp, float fCosPhiD) {
    return g_fKd * saturate(dot(normalzie(vLightPerp), g_vLightDir)) * (fCosPhiD + 1) * 0.5;
}
//----------------------------------------------------------------------
// FUNCTION: compute a per-vertex hack darkening factor.
//----------------------------------------------------------------------
float HackFactor(float fRand, float fRTCoord)
{
	return (1 - g_fDiffuseRand * fRand) *	// random strand darkening
		lerp(1 - g_fRootOcc, 1, fRTCoord);	// root darkening
}