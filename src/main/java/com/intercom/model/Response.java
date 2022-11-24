package com.intercom.model;

import com.intercom.model.Config.Room;

public class Response {

    public enum Status {
        SENT,
        NOT_SENT,
        YES,
        NO
    }

    private Room room;
    private Status code;

    public Response(Room room, Status code) {
        this.room = room;
        this.code = code;
    }

    public Room getRoom() {
        return room;
    }

    public Status getCode() {
        return code;
    }
}
