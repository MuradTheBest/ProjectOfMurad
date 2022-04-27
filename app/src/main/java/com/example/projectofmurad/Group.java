package com.example.projectofmurad;

import java.io.Serializable;

public class Group implements Serializable {

    private String name;
    private String key;
    private int color;
    private int madrichCode;
    private int limit;
    private int usersNumber;

    public Group() {}

    public Group(String name, String key, int madrichCode, int color, int limit) {
        this.name = name;
        this.key = key;
        this.madrichCode = madrichCode;
        this.limit = limit;
        this.color = color;
    }

    public int getMadrichCode() {
        return madrichCode;
    }

    public void setMadrichCode(int madrichCode) {
        this.madrichCode = madrichCode;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getUsersNumber() {
        return usersNumber;
    }

    public void setUsersNumber(int usersNumber) {
        this.usersNumber = usersNumber;
    }
}
