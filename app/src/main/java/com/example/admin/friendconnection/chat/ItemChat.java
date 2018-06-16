package com.example.admin.friendconnection.chat;

/**
 * Created by Admin on 4/24/2018.
 */

public class ItemChat {
    private String id;
    private String value;
    private String person;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public ItemChat() {

    }

    public ItemChat(String id, String value, String person) {
        this.id = id;
        this.value = value;
        this.person = person;
    }
}
