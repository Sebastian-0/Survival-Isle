#version 120

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform int enabled;
uniform vec3 tint;

void main()
{
  vec4 color = texture2D(u_texture, v_texCoord);
  vec4 newColor = vec4(tint, color.a);
  gl_FragColor = (v_color * (color + newColor) / 2) * enabled + (v_color * color) * (1 - enabled);
}