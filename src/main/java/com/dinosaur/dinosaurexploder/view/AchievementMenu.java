package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.achievements.Achievement;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class AchievementMenu {

    public Node create(AchievementManager manager) {

        var bg = new Rectangle(
            getAppWidth(),
            getAppHeight(),
            Color.color(0, 0, 0, 0.85)
        );

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        for (Achievement a : manager.getAllAchievements()) {
            Text text = new Text(
                a.getDescription() + " - " +
                (a.isCompleted() ? "Unlocked" : "Locked")
            );
            text.setFill(Color.WHITE);
            box.getChildren().add(text);
        }

        StackPane container = new StackPane(box);
        container.setPrefSize(getAppWidth(), getAppHeight());

        return new StackPane(bg, container);
    }
}