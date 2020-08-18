#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
void main()
{
   vec4 texel = texture2D(u_texture, v_texCoords);
   if(texel.a == 0.0){
        discard; //discards transparent pixels
   }

   gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
}
