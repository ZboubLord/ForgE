uniform sampler2D u_spriteTexture;
varying vec2      v_texCoord;

void main() {

  gl_FragColor = texture2D(u_spriteTexture, v_texCoord);
}
