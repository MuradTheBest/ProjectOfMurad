package com.example.projectofmurad.groups;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * The type Group.
 */
public class Group implements Serializable {

    private String name;
    private String key;
    private String description;
    private int color;
    private int madrichCode;
    private int limit;
    private int usersNumber;
    private String picture;

    /**
     * The constant KEY_GROUP.
     */
    public static final String KEY_GROUP = "key_group";
    /**
     * The constant KEY_NAME.
     */
    public static final String KEY_NAME = "name";
    /**
     * The constant KEY_GROUP_KEY.
     */
    public static final String KEY_GROUP_KEY = "key";
    /**
     * The constant KEY_MADRICH_CODE.
     */
    public static final String KEY_MADRICH_CODE = "madrichCode";
    /**
     * The constant KEY_USERS_NUMBER.
     */
    public static final String KEY_USERS_NUMBER = "usersNumber";
    /**
     * The constant KEY_PICTURE.
     */
    public static final String KEY_PICTURE = "picture";

    /**
     * Instantiates a new Group.
     */
    public Group() {}

    /**
     * Instantiates a new Group.
     *
     * @param name        the name
     * @param key         the key
     * @param description the description
     * @param madrichCode the madrich code
     * @param color       the color
     * @param usersNumber the users number
     * @param limit       the limit
     */
    public Group(String name, String key, String description, int madrichCode, int color, int usersNumber, int limit) {
        this.name = name;
        this.key = key;
        this.description = description;
        this.madrichCode = madrichCode;
        this.limit = limit;
        this.usersNumber = usersNumber;
        this.color = color;
    }

    /**
     * Gets madrich code.
     *
     * @return the madrich code
     */
    public int getMadrichCode() {
        return madrichCode;
    }

    /**
     * Sets madrich code.
     *
     * @param madrichCode the madrich code
     */
    public void setMadrichCode(int madrichCode) {
        this.madrichCode = madrichCode;
    }

    /**
     * Gets limit.
     *
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets limit.
     *
     * @param limit the limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Gets users number.
     *
     * @return the users number
     */
    public int getUsersNumber() {
        return usersNumber;
    }

    /**
     * Sets users number.
     *
     * @param usersNumber the users number
     */
    public void setUsersNumber(int usersNumber) {
        this.usersNumber = usersNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", color=" + color +
                ", madrichCode=" + madrichCode +
                ", limit=" + limit +
                ", usersNumber=" + usersNumber +
                ", picture='" + picture + '\'' +
                '}';
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets picture.
     *
     * @return the picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Sets picture.
     *
     * @param picture the picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }
}
