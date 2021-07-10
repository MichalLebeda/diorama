#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_position;

uniform vec3 u_camera_pos;
uniform sampler2D u_texture;

const float EFFECT_DIST = 14.0;
void main(){
    vec4 texel = texture2D(u_texture, v_texCoords);
    if (texel.a == 0.0){
        discard;//discards transparent pixels
    }

    gl_FragColor = v_color * texel;

    float distance = distance(u_camera_pos.xy, v_position.xy);
    if (distance>EFFECT_DIST){
        gl_FragColor.a = mix(gl_FragColor.a, 0.0, pow((distance-EFFECT_DIST)/(2.0*EFFECT_DIST), 2.0));
    }
}
