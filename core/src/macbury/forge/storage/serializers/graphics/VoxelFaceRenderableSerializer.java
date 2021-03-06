package macbury.forge.storage.serializers.graphics;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import macbury.forge.graphics.batch.renderable.VoxelFaceRenderable;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by macbury on 21.03.15.
 */
public class VoxelFaceRenderableSerializer extends Serializer<VoxelFaceRenderable> {
  @Override
  public void write(Kryo kryo, Output output, VoxelFaceRenderable face) {
    kryo.writeObject(output, face.direction);
    kryo.writeObject(output, face.boundingBox);
    output.writeBoolean(face.ssao);
    output.writeBoolean(face.reflect);
    output.writeBoolean(face.haveTransparency);
    output.writeInt(face.primitiveType);
    output.writeInt(face.triangleCount);
    kryo.writeObject(output, face.worldTransform);

    int max              = face.mesh.getNumVertices() * face.mesh.getVertexSize() / 4;
    FloatBuffer vertBuff = face.mesh.getVerticesBuffer();
    output.writeInt(max);
    int oldPosition      = vertBuff.position();
    for (int pos = 0; pos < max; pos++) {
      vertBuff.position(pos);
      output.writeFloat(vertBuff.get());
    }
    vertBuff.position(oldPosition);

    ShortBuffer indiBuff = face.mesh.getIndicesBuffer();
    oldPosition          = indiBuff.position();
    output.writeInt(face.mesh.getNumIndices());
    for (int pos = 0; pos < face.mesh.getNumIndices(); pos++) {
      indiBuff.position(pos);
      output.writeShort(indiBuff.get());
    }

    indiBuff.position(oldPosition);

  }

  @Override
  public VoxelFaceRenderable read(Kryo kryo, Input input, Class<VoxelFaceRenderable> type) {
    return null;
  }
}
