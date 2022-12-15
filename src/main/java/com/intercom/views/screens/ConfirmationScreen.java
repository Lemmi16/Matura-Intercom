package com.intercom.views.screens;

import com.intercom.views.IntercomView;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ConfirmationScreen extends HBox {

    public ConfirmationScreen(IntercomView view) {
        getStyleClass().addAll("screen", "confirmation-screen");

        VBox vBox = new VBox();
        vBox.getStyleClass().add("vbox");
        HBox.setHgrow(vBox, Priority.ALWAYS);

        Label label = new Label();
        label.textProperty().bind(Bindings.createObjectBinding(() -> view.getRequest() != null ? view.getRequest().getAction().getName() : "", view.requestProperty()));
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox.setHgrow(label, Priority.ALWAYS);

        Button yesButton = new Button("Yes");
        yesButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        yesButton.getStyleClass().add("yes");
        yesButton.setOnAction(evt -> {
            view.call("http://" + view.getRequest().getRoom().getHostName() + ":" + view.getRequest().getRoom().getPort() + "/response?responding-room=" + view.getRoom().getId() + "&code=yes");
            view.setScreen(IntercomView.Screen.START);
        });

        VBox.setVgrow(yesButton, Priority.ALWAYS);

        Button noButton = new Button("No");
        noButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        noButton.getStyleClass().add("no");
        noButton.setOnAction(evt -> {
            view.call("http://" + view.getRequest().getRoom().getHostName() + ":" + view.getRequest().getRoom().getPort() + "/response?responding-room=" + view.getRoom().getId() + "&code=no");
            view.setScreen(IntercomView.Screen.START);
        });

        VBox.setVgrow(noButton, Priority.ALWAYS);

        VBox buttonBox = new VBox(yesButton, noButton);
        buttonBox.getStyleClass().add("button-box");

        getChildren().addAll(label, buttonBox);
    }
}
