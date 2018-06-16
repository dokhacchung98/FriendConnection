package com.example.admin.friendconnection.schedule;

import java.io.Serializable;

public class ScheduleItem implements Serializable{
    private String id;
    private String title;
    private String value;
    private String time;
    private String calendar;
    private String location;

    public ScheduleItem(String id, String title, String value, String time, String calendar, String location, String lat, String lng) {
        this.id = id;
        this.title = title;
        this.value = value;
        this.time = time;
        this.calendar = calendar;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
    }

    private String lat;
    private String lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public ScheduleItem() {

    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
