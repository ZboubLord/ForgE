package macbury.forge.editor.windows;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import macbury.forge.Config;
import macbury.forge.ForgE;
import macbury.forge.ForgEBootListener;
import macbury.forge.editor.controllers.ProjectController;
import macbury.forge.editor.views.MainMenu;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame implements ForgEBootListener {
  private final LwjglAWTCanvas openGLCanvas;
  private final ForgE engine;
  private final ProjectController projectController;
  private final MainMenu mainMenu;
  private JPanel contentPane;
  private JButton wireframeButton;
  private JPanel openGlContainer;
  private JButton texturedButton;
  private JPanel statusBarPanel;

  public MainWindow() {
    setContentPane(contentPane);
    setSize(1360, 768);
    setVisible(true);
    setExtendedState(JFrame.MAXIMIZED_BOTH);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Config config            = new Config();
    config.generateWireframe = true;
    engine                   = new ForgE(config);

    engine.setBootListener(this);

    mainMenu     = new MainMenu();

    openGLCanvas = new LwjglAWTCanvas(engine);
    openGlContainer.add(openGLCanvas.getCanvas(), BorderLayout.CENTER);

    projectController = new ProjectController();
    projectController.setMainWindow(this);
    projectController.setWireframeButton(wireframeButton);
    projectController.setTextureButton(texturedButton);
    setJMenuBar(mainMenu);

    pack();
  }

  @Override
  public void afterEngineCreate(ForgE engine) {
    projectController.newMap();
  }

}