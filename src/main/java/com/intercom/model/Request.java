package com.intercom.model;

import com.intercom.model.Config.Action;
import com.intercom.model.Config.Room;

public class Request {

    private Room room;
    private Action action;

    public Request(Room room, Action action) {
        this.room = room;
        this.action = action;
    }

    public Room getRoom() {
        return room;
    }

    public Action getAction() {
        return action;
    }
}