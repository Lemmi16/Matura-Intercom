package com.intercom.views.screens;

import com.intercom.views.IntercomView;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Material;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

public class RoomsScreen extends HBox {

    public RoomsScreen(IntercomView view) {
        getStyleClass().add("screen");

        VBox vbox= new VBox();
        vbox.getStyleClass().add("vbox");
        HBox.setHgrow(vbox, Priority.ALWAYS);

        view.getConfig().getRooms().forEach(r -> {
            // do not add own room
            if (!r.equals(view.getRoom())) {
                ToggleButton button = new ToggleButton(r.getName());
                button.setPrefHeight(1);
                view.getSelectedRooms().addListener((Observable it) -> button.setSelected(view.getSelectedRooms().contains(r)));
                button.getStyleClass().add("room-button");
                button.selectedProperty().addListener(it -> {
                    if (button.isSelected()) {
                        view.getSelectedRooms().add(r);
                        button.setGraphic(new FontIcon(MaterialDesign.MDI_CHECK));
                    } else {
                        view.getSelectedRooms().remove(r);
                        button.setGraphic(null);
                    }
                });
                vbox.getChildren().add(button);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                VBox.setVgrow(button, Priority.ALWAYS);
            }
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(evt -> view.setScreen(IntercomView.Screen.START));

        Button sendButton = new Button("SENDEN");
        sendButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(evt -> view.sendMessage());

        VBox.setVgrow(cancelButton, Priority.ALWAYS);
        VBox.setVgrow(sendButton, Priority.ALWAYS);

        VBox buttonBox = new VBox(cancelButton, sendButton);
        buttonBox.getStyleClass().add("button-box");
        VBox.setVgrow(buttonBox, Priority.ALWAYS);

        getChildren().addAll(vbox, buttonBox);
    }
}
