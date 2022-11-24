package com.intercom.views;

import animatefx.animation.Flash;
import com.intercom.model.Config;
import com.intercom.model.Config.Action;
import com.intercom.model.Config.Room;
import com.intercom.model.Request;
import com.intercom.model.Response;
import com.intercom.model.Response.Status;
import io.helidon.common.reactive.Single;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class IntercomView extends StackPane {

    private final Single<WebServer> server;
    private final Config config;

    private Node startScreen;
    private Node actionsScreen;
    private Node roomsScreen;
    private Node responseScreen;
    private Node confirmationScreen;
    private Room room;

    public IntercomView(Room room, Config config) {
        this.room = room;
        this.config = config;

        getStyleClass().add("intercom-view");

        setPrefSize(320, 230); // resolution on the monitors attached to the PI

        startScreen = createStartScreen();
        actionsScreen = createActionsScreen();
        roomsScreen = createRoomsScreen();
        responseScreen = createResponseScreen();
        confirmationScreen = createConfirmationScreen();

        startScreen.visibleProperty().bind(screenProperty().isEqualTo(Screen.START));
        actionsScreen.visibleProperty().bind(screenProperty().isEqualTo(Screen.ACTIONS));
        roomsScreen.visibleProperty().bind(screenProperty().isEqualTo(Screen.ROOMS));
        responseScreen.visibleProperty().bind(screenProperty().isEqualTo(Screen.RESPONSE));
        confirmationScreen.visibleProperty().bind(screenProperty().isEqualTo(Screen.CONFIRMATION));

        getChildren().setAll(startScreen, actionsScreen, roomsScreen, responseScreen, confirmationScreen);

        screenProperty().addListener(it -> {
            switch (getScreen()) {
                case START -> startScreen.toFront();
                case ACTIONS -> {
                    setSelectedAction(null);
                    actionsScreen.toFront();
                }
                case ROOMS -> {
                    getSelectedRooms().clear();
                    roomsScreen.toFront();
                }
                case RESPONSE -> responseScreen.toFront();
                case CONFIRMATION -> confirmationScreen.toFront();
            }
        });

        setScreen(Screen.START);

        requestProperty().addListener(it -> setScreen(Screen.CONFIRMATION));
        getResponses().addListener((Observable it) -> setScreen(Screen.RESPONSE));

        Routing.Builder builder = Routing.builder();
        for (Action action : config.getActions()) {
            builder = builder.get("/" + action.getId(), (req, res) -> {
                Optional<String> caller = req.queryParams().first("calling-room-id");
                if (caller.isPresent()) {
                    String roomId = caller.get();
                    res.send("action received: " + action.getId() + ", calling room id: " + roomId);
                    Platform.runLater(() -> {
                        setRequest(new Request(findRoomInConfiguration(roomId), action));
                        Flash flash = new Flash(confirmationScreen);
                        flash.setCycleCount(3);
                        flash.play();
                    });
                }
            });

            builder = builder.get("/response", (req, res) -> {
                Optional<String> respondingRoomIdOptional = req.queryParams().first("responding-room");
                if (respondingRoomIdOptional.isPresent()) {
                    String respondingRoomId = respondingRoomIdOptional.get();
                    Room responseRoom = findRoomInConfiguration(respondingRoomId);
                    Optional<String> codeParameter = req.queryParams().first("code");

                    if (codeParameter.isPresent()) {
                        String code = codeParameter.get();
                        Status responseCode = code.equals("yes") ? Status.YES : Status.NO;
                        Response response = new Response(responseRoom, responseCode);

                        getResponses().replaceAll(r -> {
                            if (r.getRoom().getId() == response.getRoom().getId()) {
                                return response;
                            }

                            return r;
                        });
                    }
                    res.send("success");
                } else {
                    res.send("failure");
                }

                req.closeConnection();
            });
        }

        Routing routing = builder.build();

        server = WebServer.builder(routing).port(Integer.parseInt(room.getPort())).build().start();
    }

    public void shutdownServer() {
        try {
            WebServer webServer = server.get();
            if (webServer.isRunning()) {
                webServer.shutdown();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Room findRoomInConfiguration(String id) {
        for (Room room : config.getRooms()) {
            if (room.getId().equalsIgnoreCase(id)) {
                return room;
            }
        }

        return null;
    }

    private final ObjectProperty<Request> request = new SimpleObjectProperty<>();

    public Request getRequest() {
        return request.get();
    }

    public ObjectProperty<Request> requestProperty() {
        return request;
    }

    public void setRequest(Request request) {
        this.request.set(request);
    }

    private final ObservableList<Response> responses = FXCollections.observableArrayList();

    private ObservableList<Response> getResponses() {
        return responses;
    }

    private final ObjectProperty<Action> selectedAction = new SimpleObjectProperty<>();

    private Action getSelectedAction() {
        return selectedAction.get();
    }

    private void setSelectedAction(Action selectedAction) {
        this.selectedAction.set(selectedAction);
    }

    private final ObservableList<Room> selectedRooms = FXCollections.observableArrayList();

    private ObservableList<Room> getSelectedRooms() {
        return selectedRooms;
    }

    private enum Screen {
        START,
        ACTIONS,
        ROOMS,
        CONFIRMATION,
        RESPONSE
    }

    private final ObjectProperty<Screen> screen = new SimpleObjectProperty<>();

    public final Screen getScreen() {
        return screen.get();
    }

    public final ObjectProperty<Screen> screenProperty() {
        return screen;
    }

    public final void setScreen(Screen screen) {
        this.screen.set(screen);
    }

    private Node createStartScreen() {
        Label label = new Label("Intercom - " + room.getName());
        label.setGraphic(new FontIcon(MaterialDesign.MDI_WIFI));
        label.setContentDisplay(ContentDisplay.TOP);

        StackPane pane = new StackPane(label);
        pane.getStyleClass().add("start-screen");
        pane.setOnMouseClicked(evt -> setScreen(Screen.ACTIONS));
        return pane;
    }

    private Node createConfirmationScreen() {
        Label label = new Label();
        label.textProperty().bind(Bindings.createObjectBinding(() -> getRequest() != null ? getRequest().getAction().getName() : "", requestProperty()));
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox.setVgrow(label, Priority.ALWAYS);

        Button yesButton = new Button("Yes");
        yesButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        yesButton.getStyleClass().add("yes");
        yesButton.setPrefWidth(1);
        yesButton.setOnAction(evt -> {
            call("http://" + getRequest().getRoom().getHostName() + ":" + getRequest().getRoom().getPort() + "/response?responding-room=" + room.getId() + "&code=yes");
            setScreen(Screen.START);
        });

        HBox.setHgrow(yesButton, Priority.ALWAYS);

        Button noButton = new Button("No");
        noButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        noButton.getStyleClass().add("no");
        noButton.setPrefWidth(1);
        noButton.setOnAction(evt -> {
            call("http://" + getRequest().getRoom().getHostName() + ":" + getRequest().getRoom().getPort() + "/response?responding-room=" + room.getId() + "&code=no");
            setScreen(Screen.START);
        });

        HBox.setHgrow(noButton, Priority.ALWAYS);

        HBox buttonBox = new HBox(yesButton, noButton);
        buttonBox.getStyleClass().add("button-box");

        VBox box = new VBox(label, buttonBox);
        box.getStyleClass().add("confirmation-screen");

        return box;
    }

    private Node createResponseScreen() {
        VBox box = new VBox();
        box.getStyleClass().add("response-screen");

        getResponses().addListener((Observable it) -> {
            Platform.runLater(() -> {
                box.getChildren().clear();

                getResponses().forEach(response -> {
                    Label label = new Label(response.getRoom().getName() + ": " + response.getCode());
                    label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    VBox.setVgrow(label, Priority.ALWAYS);
                    box.getChildren().add(label);
                });

                Button button = new Button("CLOSE");
                button.getStyleClass().add("close-button");
                button.setMaxWidth(Double.MAX_VALUE);
                button.setOnAction(evt -> {
                    getResponses().clear();
                    setScreen(Screen.START);
                });
                box.getChildren().add(button);
            });
        });

        return box;
    }

    private Node createActionsScreen() {
        VBox box = new VBox();
        box.getStyleClass().add("action-screen");

        config.getActions().forEach(action -> {
            Button button = new Button(action.getName());
            button.getStyleClass().add("action-button");
            button.setOnAction(evt -> {
                setSelectedAction(action);
                setScreen(Screen.ROOMS);
            });
            box.getChildren().add(button);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            VBox.setVgrow(button, Priority.ALWAYS);
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(evt -> setScreen(Screen.START));
        cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(cancelButton, Priority.ALWAYS);

        box.getChildren().add(cancelButton);
        return box;
    }

    private Node createRoomsScreen() {
        VBox box = new VBox();
        box.getStyleClass().add("room-screen");

        config.getRooms().forEach(r -> {
            // do not add own room
            if (!r.equals(room)) {
                ToggleButton button = new ToggleButton(r.getName());
                getSelectedRooms().addListener((Observable it) -> button.setSelected(getSelectedRooms().contains(r)));
                button.getStyleClass().add("room-button");
                button.selectedProperty().addListener(it -> {
                    if (button.isSelected()) {
                        getSelectedRooms().add(r);
                    } else {
                        getSelectedRooms().remove(r);
                    }
                });
                box.getChildren().add(button);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                VBox.setVgrow(button, Priority.ALWAYS);
            }
        });

        Separator separator = new Separator();
        separator.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(separator, Priority.NEVER);

        Button cancelButton = new Button("CANCEL");
        cancelButton.setPrefWidth(1);
        cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(evt -> setScreen(Screen.START));

        Button sendButton = new Button("SENDEN");
        sendButton.setPrefWidth(1);
        sendButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(evt -> sendMessage());

        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        HBox.setHgrow(sendButton, Priority.ALWAYS);

        HBox buttonBox = new HBox(cancelButton, sendButton);
        buttonBox.getStyleClass().add("button-box");
        VBox.setVgrow(buttonBox, Priority.ALWAYS);

        box.getChildren().add(buttonBox);

        return box;
    }

    private void sendMessage() {
        Action action = getSelectedAction();

        getSelectedRooms().forEach(selectedRoom -> {
            boolean success = call("http://" + selectedRoom.getHostName() + ":" + selectedRoom.getPort() + "/" + action.getId() + "?calling-room-id=" + this.room.getId());
            if (success) {
                getResponses().add(new Response(selectedRoom, Status.SENT));
            } else {
                getResponses().add(new Response(selectedRoom, Status.NOT_SENT));
            }
        });
    }

    private boolean call(String s) {
        System.out.println("calling: " + s);
        try {
            URL url = new URL(s);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(2);
            urlConnection.connect();
            urlConnection.getContent(); // to really send the request across the wire
            return true;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        return false;
    }
}

