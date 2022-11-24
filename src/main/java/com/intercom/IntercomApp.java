package com.intercom;

import com.intercom.model.Config;
import com.intercom.model.Config.Room;
import com.intercom.views.IntercomView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IntercomApp extends Application {

    // load the configuration from config.json
    private final Config config = Config.load(false);

    private Room room;

    @Override
    public void start(Stage stage) {
        // in which room are we? Depends on the hostname, e.g. pi-philip.local -> Philip's room
        room = determineRoom();

        // create the actual user interface
        IntercomView intercomView = new IntercomView(room, config);

        // boilerplate code to make something visible in JavaFX
        Scene scene = new Scene(intercomView);
        scene.getStylesheets().add(IntercomApp.class.getResource("styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Intercom " + room.getName());
        stage.sizeToScene();
        stage.setFullScreenExitHint("");

        // make this application full screen when running on the Raspberry Pi
        // the operating system on Raspberry Pi is Linux
        stage.setFullScreen(System.getProperty("os.name").equals("Linux"));

        stage.show();
    }

    private Room determineRoom() {
        // first we check if the room ID was passed to the program ... then use that room
        String roomId = System.getProperty("room");
        if (roomId != null) {
            System.out.println("room id = " + roomId);
            for (Room room : config.getRooms()) {
                if (room.getId().equalsIgnoreCase(roomId)) {
                    return room;
                }
            }
        } else {
            // if no room has been passed then we find the room by matching the computer's name to the room
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                System.out.println("local host name: " + hostName);
                for (Room room : config.getRooms()) {
                    if (room.getHostName().toLowerCase().startsWith(hostName.toLowerCase())) {
                        return room;
                    }
                }
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
