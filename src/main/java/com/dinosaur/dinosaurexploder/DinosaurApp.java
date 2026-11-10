/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.DinosaurController;
import com.dinosaur.dinosaurexploder.model.GameEntityFactory;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import javafx.application.Platform;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Summary : The Factory handles the DinosaurApp,Physics,Settings and Input of all entities in the
 * game
 */
public class DinosaurApp extends GameApplication {
  DinosaurGUI gui = new DinosaurGUI();
  DinosaurController controller = new DinosaurController();
  private AchievementManager achievementManager;

  // --- Latenz-Tracking Status ---
  private boolean awaitingProcessing = false;

  @Override
  protected void initSettings(GameSettings settings) {
    gui.initSettings(settings);
    settings.setAppIcon(GameConstants.GAME_ICON_DINOSAUR);
    settings.setTitle(GameConstants.GAME_NAME);
  }

  /**
   * Wird aufgerufen, sobald die Stage existiert, aber VOR dem eigentlichen Spielstart.
   * Perfekt für Menü- und globale Event-Filter.
   */
  @Override
  protected void onPreInit() {
    // EventFilter fängt Klicks und Tasten ab, BEVOR Menü-Buttons sie konsumieren
    FXGL.getPrimaryStage().addEventFilter(InputEvent.ANY, event -> {
      if (awaitingProcessing) {
        return;
      }

      String actionName = null;

      if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
        MouseEvent me = (MouseEvent) event;
        if (me.getButton() == MouseButton.PRIMARY) {
          actionName = "Maus: Linksklick (PRIMARY)";
        } else if (me.getButton() == MouseButton.SECONDARY) {
          actionName = "Maus: Rechtsklick (SECONDARY)";
        } else {
          actionName = "Maus: " + me.getButton();
        }
      } else if (event.getEventType() == KeyEvent.KEY_PRESSED) {
        KeyEvent ke = (KeyEvent) event;
        actionName = "Taste: " + ke.getCode();
      }

      if (actionName != null) {
        final long startTimeNanos = System.nanoTime();
        final String finalAction = actionName;
        awaitingProcessing = true;

        // Platform.runLater läuft nach dem Event-Dispatch und Button-Klick ab,
        // funktioniert auch bei pausierter Game-Engine / im Menü
        Platform.runLater(() -> {
          double latencyMs = (System.nanoTime() - startTimeNanos) / 1_000_000.0;
          System.out.printf("[Latenz] %s -> Verarbeitet in: %.2f ms%n", finalAction, latencyMs);
          awaitingProcessing = false;
        });
      }
    });
  }

  @Override
  protected void initInput() {
    controller.initInput();
  }

  @Override
  protected void initGame() {
    FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());
    achievementManager = new AchievementManager();
    achievementManager.init();
    FXGL.getWorldProperties().setValue("achievementManager", achievementManager);
    controller.initGame(achievementManager);
  }

  @Override
  protected void initPhysics() {
    controller.initPhysics();
  }

  @Override
  protected void onUpdate(double tpf) {
    if (achievementManager != null) {
      achievementManager.update(tpf);
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}