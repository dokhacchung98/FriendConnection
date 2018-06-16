package com.example.admin.friendconnection.friend;

/**
 * Created by Admin on 4/22/2018.
 */

public class Friend {
    private String id;
    private String person;
    private String mode;

    public Friend() {
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Friend(String id, String person, String mode) {
        this.id = id;
        this.person = person;
        this.mode = mode;
    }
}
