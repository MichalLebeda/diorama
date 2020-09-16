#ifdef GL_ES
precision mediump float;
#endif

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform float time;
uniform mat4 u_projectionViewMatrix;
// uniform float time;
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_position;

vec3 permute(vec3 x) { return mod(((x*34.0)+1.0)*x, 289.0); }

float snoise(vec2 v){
    const vec4 C = vec4(0.211324865405187, 0.366025403784439,
    -0.577350269189626, 0.024390243902439);
    vec2 i  = floor(v + dot(v, C.yy));
    vec2 x0 = v -   i + dot(i, C.xx);
    vec2 i1;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod(i, 289.0);
    vec3 p = permute(permute(i.y + vec3(0.0, i1.y, 1.0))
    + i.x + vec3(0.0, i1.x, 1.0));
    vec3 m = max(0.5 - vec3(dot(x0, x0), dot(x12.xy, x12.xy),
    dot(x12.zw, x12.zw)), 0.0);
    m = m*m;
    m = m*m;
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * (a0*a0 + h*h);
    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

void main()
{
    v_color = a_color;
    v_position = a_position;
    v_color.a = v_color.a * (255.0/254.0);
    v_texCoords = a_texCoord0;
    //gl_Position = u_projectionViewMatrix * a_position;
    //NOISE
    vec2 pos = vec2(a_position.x*0.1+time, floor(a_position.y*0.1));
    float noise = snoise(pos);
//    v_color.rgb = v_color.rgb + noise/3.0;
    v_position.z = v_position.z + noise/3.0;

    gl_Position =  u_projectionViewMatrix * v_position;
}
