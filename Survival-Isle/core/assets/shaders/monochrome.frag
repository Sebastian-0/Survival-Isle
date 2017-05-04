#version 120

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform float colorIntensity;

void main()
{
    vec4 originalColor = texture2D(u_texture, v_texCoord);
    vec3 lum = vec3(0.299, 0.587, 0.114);
    gl_FragColor = vec4(mix(vec3(dot(originalColor.rgb, lum)), originalColor.rgb, colorIntensity), originalColor.a * v_color.a);
    
}