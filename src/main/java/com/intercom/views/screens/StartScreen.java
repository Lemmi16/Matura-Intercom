package com.intercom.views.screens;

import com.intercom.model.Config;
import com.intercom.views.IntercomView;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

public class StartScreen extends StackPane {

    public StartScreen(IntercomView view) {
        getStyleClass().add("start-screen");

        Label label = new Label(view.getRoom().getName());
        label.setGraphic(new FontIcon(MaterialDesign.MDI_WIFI));
        label.setContentDisplay(ContentDisplay.TOP);
        getChildren().add(label);

        setOnMouseClicked(evt -> view.setScreen(IntercomView.Screen.ACTIONS));
    }
}
