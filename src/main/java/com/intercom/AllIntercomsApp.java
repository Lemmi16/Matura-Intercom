package com.intercom;

import com.intercom.model.Config;
import com.intercom.views.IntercomView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AllIntercomsApp extends Application {

    // load the configuration from config.json
    private final Config config = Config.load(true);

    private final List<IntercomView> views = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(50));
        tilePane.setHgap(50);
        tilePane.setVgap(50);

        tilePane.setPrefColumns(3);

        config.getRooms().forEach(room -> {
            // create the actual user interface
            IntercomView intercomView = new IntercomView(room, config);
            intercomView.getStyleClass().add("tile");
            views.add(intercomView);

            Label label = new Label("Room ID: \"" + room.getId() + "\", Port: " + room.getPort());

            VBox box = new VBox(5, label, intercomView);
            tilePane.getChildren().add(box);
        });

        // boilerplate code to make something visible in JavaFX
        Scene scene = new Scene(tilePane);
        scene.getStylesheets().add(AllIntercomsApp.class.getResource("styles.css").toExternalForm());

        //stage.setOnCloseRequest(evt -> views.forEach(view -> view.shutdownServer()));
        stage.setScene(scene);
        stage.setTitle("Intercom");
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        views.forEach(view -> view.shutdownServer());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
