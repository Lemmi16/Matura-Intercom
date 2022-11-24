package com.intercom.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private List<Room> rooms = new ArrayList<>();

    private List<Action> actions = new ArrayList<>();

    public Config() {
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Action> getActions() {
        return actions;
    }

    public class Element {
        private String id;

        public Element() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public class Room extends Element {
        private String name;
        private String hostName;
        private String port;
        private List<String> availableActions = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getPort() {
            return port;
        }

        public List<String> getAvailableActions() {
            return availableActions;
        }
    }

    public class Action extends Element {
        private String name;

        public Action() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static Config load(boolean testing) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(new InputStreamReader(Config.class.getResourceAsStream(testing ? "config-testing.json" : "config.json"), StandardCharsets.UTF_8), Config.class);
    }
}
