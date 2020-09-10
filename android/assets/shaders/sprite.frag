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

void main(){
    vec4 texel = texture2D(u_texture, v_texCoords);
    if (texel.a == 0.0){
        discard;//discards transparent pixels
    }

    gl_FragColor = v_color * texel;

    float distance = distance(u_camera_pos.xy, v_position.xy);
    if (distance>20.0){
        gl_FragColor.a = mix(gl_FragColor.a, 0.0, pow((distance-20.0)/40.0, 2.0));
    }
}