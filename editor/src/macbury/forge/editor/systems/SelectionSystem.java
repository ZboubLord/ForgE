package macbury.forge.editor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import macbury.forge.components.Cursor;
import macbury.forge.components.Position;
import macbury.forge.editor.selection.AbstractSelection;
import macbury.forge.editor.selection.SelectionInterface;
import macbury.forge.editor.selection.SingleBlockSelection;
import macbury.forge.editor.utils.MousePosition;
import macbury.forge.graphics.DebugShape;
import macbury.forge.graphics.camera.GameCamera;
import macbury.forge.level.Level;
import macbury.forge.level.map.ChunkMap;
import macbury.forge.ui.Overlay;
import macbury.forge.utils.VoxelCursor;
import macbury.forge.utils.VoxelPicker;

/**
 * Created by macbury on 19.10.14.
 * handles editor ui and editor input like selecting voxels, appending voxels, deleting voxels, clicking on enityt etc
 */
public class SelectionSystem extends EntitySystem {
  private static final String TAG = "EditorSysten";
  private final GameCamera camera;
  private final ChunkMap map;
  private final VoxelPicker voxelPicker;
  private final Cursor cursorComponent;
  private final ShapeRenderer shapeRenderer;
  private final RenderContext renderContext;
  private Overlay overlay;
  private final MousePosition mousePosition;
  public final VoxelCursor voxelCursor = new VoxelCursor();
  private AbstractSelection selection;
  private Array<SelectionInterface> listeners;

  public SelectionSystem(Level level) {
    super();
    this.listeners               = new Array<SelectionInterface>();
    this.voxelPicker             = new VoxelPicker(level.terrainMap);
    this.camera                  = level.camera;
    this.mousePosition           = new MousePosition(camera);
    this.map                     = level.terrainMap;
    this.shapeRenderer           = level.batch.shapeRenderer;
    this.renderContext           = level.renderContext;
    Entity cursorEntity          = level.entities.createEntity();

    this.cursorComponent         = level.entities.createComponent(Cursor.class);

    cursorEntity.add(level.entities.createComponent(Position.class));
    cursorEntity.add(cursorComponent);
    level.entities.addEntity(cursorEntity);

    this.selection = new SingleBlockSelection(this.map);
  }

  public void addListener(SelectionInterface selectionInterface) {
    listeners.add(selectionInterface);
  }

  @Override
  public void update(float deltaTime) {
    cursorComponent.set(selection.getBoundingBox());
    renderContext.begin(); {
      renderContext.setDepthMask(true);
      renderContext.setCullFace(GL30.GL_BACK);
      renderContext.setDepthTest(GL20.GL_LEQUAL);
      shapeRenderer.setProjectionMatrix(camera.combined);
      shapeRenderer.begin(ShapeRenderer.ShapeType.Line); {
        shapeRenderer.setColor(cursorComponent.color);
        DebugShape.draw(shapeRenderer, cursorComponent.cursorBox);
      }
      shapeRenderer.end();
    } renderContext.end();
  }

  private boolean getCurrentVoxelCursor(float screenX, float screenY) {
    mousePosition.set(screenX, screenY);
    Ray pickRay              = camera.getPickRay(mousePosition.x, mousePosition.y);
    return voxelPicker.getVoxelPositionForPickRay(pickRay, camera.far, voxelCursor);
  }

  public void setOverlay(Overlay overlay) {
    this.overlay = overlay;
    overlay.addCaptureListener(new InputListener() {

      @Override
      public boolean mouseMoved(InputEvent event, float x, float y) {
        if (getCurrentVoxelCursor(x,y)) {
          selection.reset(voxelCursor);
          return true;
        }
        return super.mouseMoved(event,x,y);
      }

      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT && getCurrentVoxelCursor(x,y)) {
          selection.start(voxelCursor);
          for (SelectionInterface listener : listeners) {
            listener.onSelectionStart(selection);
          }
          return true;
        }

        return super.touchDown(event,x,y,pointer,button);
      }

      @Override
      public void touchDragged(InputEvent event, float x, float y, int pointer) {
        if (getCurrentVoxelCursor(x,y)) {
          selection.update(voxelCursor);
          for (SelectionInterface listener : listeners) {
            listener.onSelectionChange(selection);
          }
        }
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT && getCurrentVoxelCursor(x,y)) {
          selection.end(voxelCursor);
          for (SelectionInterface listener : listeners) {
            listener.onSelectionEnd(selection);
          }
          selection.reset(voxelCursor);
        }
        super.touchUp(event, x, y, pointer, button);
      }
    });
  }

}