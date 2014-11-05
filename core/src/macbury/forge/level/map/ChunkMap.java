package macbury.forge.level.map;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import macbury.forge.graphics.VoxelMap;
import macbury.forge.graphics.VoxelMaterial;
import macbury.forge.graphics.builders.Chunk;
import macbury.forge.utils.Vector3i;

/**
 * Created by macbury on 19.10.14.
 */
public class ChunkMap extends VoxelMap {
  public static final int CHUNK_SIZE         = 10;
  public static final int CHUNK_ARRAY_SIZE    = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
  public static final Vector3 TERRAIN_TILE_SIZE = new Vector3(1,1,1);
  private static final String TAG = "ChunkMap";
  public final Array<Chunk> chunks;
  public final Array<Chunk> chunkToRebuild;

  private int countChunksX;
  private int countChunksY;
  private int countChunksZ;

  private Vector3i tempA = new Vector3i();
  private Vector3i tempB = new Vector3i();
  public ChunkMap(Vector3 tileSize) {
    super(tileSize);
    chunks         = new Array<Chunk>();
    chunkToRebuild = new Array<Chunk>();
  }

  @Override
  public void initialize(int width, int height, int depth) {
    super.initialize(width, height, depth);
    splitIntoChunks();
  }

  public void buildFloor() {
    VoxelMaterial grass1 = new VoxelMaterial(44f/255f,159f/255f,93f/255f,1);
    VoxelMaterial grass2 = new VoxelMaterial(82f/255f,198f/255f,152f/255f,1);
    VoxelMaterial grass3 = new VoxelMaterial(14f/255f,123f/255f,34f/255f,1);

    Array<VoxelMaterial> m = new Array<VoxelMaterial>();
    m.add(grass1);
    m.add(grass2);
    m.add(grass3);

    for(int y = 0; y < 1; y++) {
      for(int x = 0; x < width; x++) {
        for(int z = 0; z < depth; z++) {
          setMaterialForPosition(grass1, x,y,z);
        }
      }
    }

    materials.addAll(m);
    VoxelMaterial rock = new VoxelMaterial(186f/255f,191f/255f,186f/255f,1);
    materials.add(rock);
  }

  public Vector3i voxelPositionToChunkPosition(int x, int y, int z){
    return tempA.set(x/CHUNK_SIZE,y/CHUNK_SIZE,z/CHUNK_SIZE);
  }

  public Chunk findForChunkPosition(Vector3i position) {
    for (Chunk chunk : chunks) {
      if (chunk.position.equals(position))
        return chunk;
    }
    return null;
  }

  @Override
  public void setMaterialForPosition(VoxelMaterial color, int x, int y, int z) {
    super.setMaterialForPosition(color, x, y, z);
    rebuildChunkAroundPosition(x, y, z);
  }

  private void rebuildChunkAroundPosition(int x, int y, int z) {
    Vector3i chunkPosition = voxelPositionToChunkPosition(x, y, z);
    Chunk    centerChunk   = findForChunkPosition(chunkPosition);
    addToRebuild(centerChunk);

    if (centerChunk.start.x == x) {
      //Gdx.app.log(TAG, "X left border!");
      tempB.set(chunkPosition).x -= 1;
      rebuildChunkForChunkPositionIfExists(tempB);
    }

    if (centerChunk.end.x - 1 == x) {
      //Gdx.app.log(TAG, "X right border!");
      tempB.set(chunkPosition).x += 1;
      rebuildChunkForChunkPositionIfExists(tempB);
    }

    if (centerChunk.start.y == y) {
      //Gdx.app.log(TAG, "Y top border!");
      tempB.set(chunkPosition).y += 1;
      rebuildChunkForChunkPositionIfExists(tempB);
    }

    if (centerChunk.end.y - 1 == y) {
      //Gdx.app.log(TAG, "Y bottom border!");
      tempB.set(chunkPosition).y += 1;
      rebuildChunkForChunkPositionIfExists(tempB);
    }

    if (centerChunk.start.z == z) {
      //Gdx.app.log(TAG, "Z Front border!");
      tempB.set(chunkPosition).z -= 1;
      rebuildChunkForChunkPositionIfExists(tempB);
    }

    if (centerChunk.end.z - 1 == z) {
      //Gdx.app.log(TAG, "Z back border!");
      tempB.set(chunkPosition).z += 1;
      rebuildChunkForChunkPositionIfExists(tempB);
    }
  }

  private void rebuildChunkForChunkPositionIfExists(Vector3i chunkPosition) {
    Chunk chunk            = findForChunkPosition(chunkPosition);
    if (chunk != null) {
      addToRebuild(chunk);
    }
  }

  private void rebuildChunkForChunkPosition(Vector3i chunkPosition) {
    Chunk chunk            = findForChunkPosition(chunkPosition);
    if (chunk == null) {
      throw new GdxRuntimeException("Chunk is null!!");
    } else {
      addToRebuild(chunk);
    }
  }

  private void rebuildChunkForPosition(int x, int y, int z) {
    Vector3i chunkPosition = voxelPositionToChunkPosition(x,y,z);
    rebuildChunkForChunkPosition(chunkPosition);
  }

  @Override
  public void setEmptyForPosition(int x, int y, int z) {
    super.setEmptyForPosition(x, y, z);
    rebuildChunkAroundPosition(x, y, z);
  }

  private void splitIntoChunks() {
    this.countChunksX = width / CHUNK_SIZE;
    this.countChunksY = height / CHUNK_SIZE;
    this.countChunksZ = depth / CHUNK_SIZE;
    for(int chunkX = 0; chunkX < countChunksX; chunkX++) {
      for(int chunkY = 0; chunkY < countChunksY; chunkY++) {
        for(int chunkZ = 0; chunkZ < countChunksZ; chunkZ++) {
          Chunk chunk  = new Chunk();
          chunk.position.set(chunkX, chunkY, chunkZ);
          chunk.worldPosition.set(chunkX * CHUNK_SIZE, chunkY * CHUNK_SIZE, chunkZ * CHUNK_SIZE).scl(voxelSize);
          chunk.start.set(chunkX * CHUNK_SIZE, chunkY * CHUNK_SIZE, chunkZ * CHUNK_SIZE);
          chunk.end.set(chunk.start).add(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
          chunk.size.set(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE).scl(voxelSize);
          chunks.add(chunk);
        }
      }
    }
  }

  private void addToRebuild(Chunk chunk) {
    if (chunkToRebuild.indexOf(chunk, true) == -1){
      chunkToRebuild.add(chunk);
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    for(Chunk chunk : chunks) {
      chunk.dispose();
    }
    chunks.clear();
    chunkToRebuild.clear();
  }

}
