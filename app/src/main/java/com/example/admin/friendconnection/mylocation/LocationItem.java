package com.example.admin.friendconnection.mylocation;

public class LocationItem {
    private String id;
    private String lat;
    private String lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public LocationItem() {

    }

    public LocationItem(String id, String lat, String lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }
}
