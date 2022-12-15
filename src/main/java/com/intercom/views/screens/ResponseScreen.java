package com.intercom.views.screens;

import com.intercom.model.Response;
import com.intercom.views.IntercomView;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

public class ResponseScreen extends HBox {

    public ResponseScreen(IntercomView view) {
        getStyleClass().addAll("screen", "response-screen");

        VBox vBox = new VBox();
        vBox.getStyleClass().add("vbox");
        HBox.setHgrow(vBox, Priority.ALWAYS);

        view.getResponses().addListener((Observable it) -> {
            Platform.runLater(() -> {
                vBox.getChildren().clear();

                view.getResponses().forEach(response -> {
                    Label label = new Label(response.getRoom().getName() + ": " + response.getCode());
                    label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    switch(response.getCode()){

                        case SENT -> {
                            label.getStyleClass() .add("sent");
                            label.setGraphic(new FontIcon(MaterialDesign.MDI_ARROW_RIGHT_BOLD));
                        }
                        case NOT_SENT -> {
                            label.getStyleClass() .add("not-sent");
                            label.setGraphic(new FontIcon(MaterialDesign.MDI_ACCOUNT_ALERT));
                        }
                        case YES -> {
                            label.getStyleClass() .add("yes");
                            label.setGraphic(new FontIcon(MaterialDesign.MDI_CHECK));
                        }
                        case NO -> {
                            label.getStyleClass() .add("no");
                            label.setGraphic(new FontIcon(MaterialDesign.MDI_CLOSE));
                        }
                    }

                    VBox.setVgrow(label, Priority.ALWAYS);
                    vBox.getChildren().add(label);
                });

            });
        });

        Button button = new Button("CLOSE");
        button.getStyleClass().add("close-button");
        button.setMaxHeight(Double.MAX_VALUE);
        button.setOnAction(evt -> {
            view.getResponses().clear();
            view.setScreen(IntercomView.Screen.START);
        });

        getChildren().addAll(vBox, button);
    }
}
