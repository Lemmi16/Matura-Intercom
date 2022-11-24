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

    public void createDefaults() {
        getRooms().clear();
        getActions().clear();

        Room philip = new Room();
        philip.setId("philip");
        philip.setName("Philip");
        philip.setHostName("pi-philip.local");
        getRooms().add(philip);

        Room jule = new Room();
        jule.setId("jule");
        jule.setName("Jule");
        jule.setHostName("pi-jule.local");
        getRooms().add(jule);

        Room armin = new Room();
        armin.setId("armin");
        armin.setName("Armin");
        armin.setHostName("pi-armin.local");
        getRooms().add(armin);

        Room kitchen = new Room();
        kitchen.setId("kitchen");
        kitchen.setName("Küche");
        kitchen.setHostName("pi-kitchen.local");
        getRooms().add(kitchen);

        Room office = new Room();
        office.setId("office");
        office.setName("Büro");
        office.setHostName("pi-office.local");
        getRooms().add(office);

        Action duty = new Action();
        duty.setId("duty");
        duty.setName("Ämtli");
        getActions().add(duty);

        Action sleep = new Action();
        sleep.setId("sleep");
        sleep.setName("Schlafen");
        getActions().add(sleep);

        Action come = new Action();
        come.setId("come");
        come.setName("Bitte kommen");
        getActions().add(come);

        Action homework = new Action();
        homework.setId("homework");
        homework.setName("Hausaufgaben");
        getActions().add(homework);

        Action teeth = new Action();
        teeth.setId("teeth");
        teeth.setName("Zähneputzen");
        getActions().add(teeth);

        Action shower = new Action();
        shower.setId("shower");
        shower.setName("Duschen");
        getActions().add(shower);

        philip.getAvailableActions().add(come.getId());
        jule.getAvailableActions().add(come.getId());
        armin.getAvailableActions().add(come.getId());

        kitchen.getAvailableActions().add(duty.getId());
        kitchen.getAvailableActions().add(sleep.getId());
        kitchen.getAvailableActions().add(come.getId());
        kitchen.getAvailableActions().add(shower.getId());
        kitchen.getAvailableActions().add(teeth.getId());
        kitchen.getAvailableActions().add(homework.getId());

        office.getAvailableActions().add(duty.getId());
        office.getAvailableActions().add(sleep.getId());
        office.getAvailableActions().add(come.getId());
        office.getAvailableActions().add(shower.getId());
        office.getAvailableActions().add(teeth.getId());
        office.getAvailableActions().add(homework.getId());
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

    private void save() {
        createDefaults();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(this));
    }

    public static void main(String[] args) {
        Config config = new Config();
        config.save();
    }
}
