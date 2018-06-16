package com.example.admin.friendconnection.friend;

/**
 * Created by Admin on 4/23/2018.
 */

public class FriendOnOff {
    private String id;
    private String onof;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOnof() {
        return onof;
    }

    public void setOnof(String onof) {
        this.onof = onof;
    }

    public FriendOnOff() {

    }

    public FriendOnOff(String id, String onof) {

        this.id = id;
        this.onof = onof;
    }
}
