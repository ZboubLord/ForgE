package macbury.forge.graphics.batch.renderable;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import macbury.forge.graphics.batch.Wireframe;

/**
 * Created by macbury on 18.10.14.
 */
public class BaseRenderable {
  public boolean ssao;
  public boolean reflect;
  public Wireframe wireframe;
  public Mesh mesh;
  public int primitiveType;
  public Matrix4 worldTransform = new Matrix4();
  public int triangleCount = 0;
  public boolean haveTransparency;
}
