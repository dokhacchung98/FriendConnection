package com.example.admin.friendconnection.friend;

public class AddFriend {
    private String key;
    private String id;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AddFriend(String key, String id) {
        this.key = key;
        this.id = id;
    }

    public AddFriend() {

    }
}
