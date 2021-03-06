package macbury.forge.shaders.uniforms;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import macbury.forge.level.LevelEnv;

/**
 * Created by macbury on 13.03.15.
 */
public class UniformSky extends BaseUniform {
  public final String UNIFORM_SKY_COLOR              = "u_skyColor";
  @Override
  public void bind(ShaderProgram shader, LevelEnv env, RenderContext context, Camera camera) {
    shader.setUniformf(UNIFORM_SKY_COLOR, env.skyColor);
  }

  @Override
  public void dispose() {

  }
}
