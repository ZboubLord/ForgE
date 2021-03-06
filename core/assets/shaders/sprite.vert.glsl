uniform mat4   u_projectionMatrix;
uniform mat4   u_worldTransform;

attribute vec2 a_texCoord0;
attribute vec4 a_position;

varying   vec2 v_texCoord;

void main() {
  v_texCoord        = a_texCoord0;
  gl_Position       = u_projectionMatrix * u_worldTransform * a_position;
}
