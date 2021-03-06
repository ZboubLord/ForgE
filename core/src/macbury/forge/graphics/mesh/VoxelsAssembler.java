package macbury.forge.graphics.mesh;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import macbury.forge.blocks.Block;
import macbury.forge.blocks.BlockShapePart;
import macbury.forge.blocks.BlockShapeTriangle;
import macbury.forge.graphics.builders.TerrainPart;
import macbury.forge.graphics.builders.VoxelDef;

/**
 * Created by macbury on 16.10.14.
 */
public class VoxelsAssembler extends MeshAssembler {
  private Matrix4 transformMat = new Matrix4();
  private Vector3 tempVec      = new Vector3();
  private Vector2 tempVec2     = new Vector2();

  private MeshVertexInfo vertex(VoxelDef voxelDef, BlockShapePart part, int index, TextureAtlas.AtlasRegion sideRegion, TerrainPart terrainPart) {
    MeshVertexInfo vert = this.vertex().ao(voxelDef.ao).transparent(voxelDef.block.transparent);

    // Part first calculate position of vertex and rotate in the algiment path
    transformMat.idt();

    transformMat.translate(voxelDef.position.x, voxelDef.position.y, voxelDef.position.z);
    transformMat.scl(terrainPart.voxelSize.x, terrainPart.voxelSize.y, terrainPart.voxelSize.z);
    transformMat.translate(voxelDef.center);

    if (voxelDef.voxel.alginTo != null) {
      switch (voxelDef.block.rotation) {
        case horizontal:
          transformMat.rotate(voxelDef.voxel.alginTo.rotationHorizontal);
          break;

        case alignToSurface:
          transformMat.rotate(voxelDef.voxel.alginTo.rotationAllSides);
          break;
      }
    }

    transformMat.translate(part.verticies.get(index));
    transformMat.getTranslation(vert.position);

    // Recalculate aligment normals too :P

    transformMat.idt();
    if (voxelDef.voxel.alginTo != null) {
      switch(voxelDef.block.rotation) {
        case horizontal:
          transformMat.rotate(voxelDef.voxel.alginTo.rotationHorizontal);
        break;

        case alignToSurface:
          transformMat.rotate(voxelDef.voxel.alginTo.rotationAllSides);
        break;
      }
    }
    
    transformMat.translate(part.normals.get(index));
    transformMat.getTranslation(vert.normal);

    Vector2 uv = part.uvs.get(index);
    terrainPart.getUVScaling(tempVec2);
    tempVec2.scl(uv);

    vert.uv.set(tempVec2);
    vert.textureFullCords(sideRegion.getU(), sideRegion.getV(), sideRegion.getU2(), sideRegion.getV2());

    if (part.waviness != null) {
      vert.material.setWaviness(part.waviness[index]);
    }

    return vert;
  }

  public void face(VoxelDef voxelDef, Block.Side side, TerrainPart part) {
    BlockShapePart blockShapePart       = voxelDef.block.blockShape.get(side);

    if (blockShapePart != null) {
      TextureAtlas.AtlasRegion sideRegion = voxelDef.block.getRegionForSide(side);

      for(BlockShapeTriangle triangle : blockShapePart.triangles) {
        MeshVertexInfo vert1          = vertex(voxelDef, blockShapePart, triangle.index1, sideRegion, part);
        MeshVertexInfo vert2          = vertex(voxelDef, blockShapePart, triangle.index2, sideRegion, part);
        MeshVertexInfo vert3          = vertex(voxelDef, blockShapePart, triangle.index3, sideRegion, part);

        triangle(vert1, vert2, vert3);
      }
    }
  }
/*
  public void top(VoxelDef voxelDef) {
    face(voxelDef, Block.Side.top, part);
  }

  public void bottom(VoxelDef voxelDef) {
    face(voxelDef, Block.Side.bottom, part);
  }

  public void front(VoxelDef voxelDef) {
    face(voxelDef, Block.Side.front, part);
  }

  public void back(VoxelDef voxelDef) {
    face(voxelDef, Block.Side.back, part);
  }

  public void left(VoxelDef voxelDef) {
    face(voxelDef, Block.Side.left, part);
  }

  public void right(VoxelDef voxelDef) {
    face(voxelDef, Block.Side.right, part);
  }
*/
}
