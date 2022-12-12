package com.intercom.views.screens;

import com.intercom.views.IntercomView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActionScreen extends HBox {

    public ActionScreen(IntercomView view) {
        getStyleClass().add("action-screen");

        VBox vbox= new VBox();
        vbox.getStyleClass().add("vbox");
        HBox.setHgrow(vbox, Priority.ALWAYS);

        view.getConfig().getActions().forEach(action -> {
            Button button = new Button(action.getName());
            button.getStyleClass().add("action-button");
            button.setOnAction(evt -> {
                view.setSelectedAction(action);
                view.setScreen(IntercomView.Screen.ROOMS);
            });
            vbox.getChildren().add(button);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            VBox.setVgrow(button, Priority.ALWAYS);
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(evt -> view.setScreen(IntercomView.Screen.START));
        cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(cancelButton, Priority.ALWAYS);

       getChildren().addAll(vbox, cancelButton);
    }
}
