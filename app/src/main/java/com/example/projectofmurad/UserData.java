package com.example.projectofmurad;

import androidx.annotation.NonNull;

/**
 * The type User data.
 */
public class UserData {

    private String UID;
    private String email;
    private String username;
    private String phone;
    private String picture;
    private String currentGroup;

    /**
     * The constant KEY_UID.
     */
    public final static String KEY_UID = "uid";
    /**
     * The constant KEY_USERNAME.
     */
    public final static String KEY_USERNAME = "username";
    /**
     * The constant KEY_PHONE.
     */
    public final static String KEY_PHONE = "phone";
    /**
     * The constant KEY_CURRENT_GROUP.
     */
    public static final String KEY_CURRENT_GROUP = "currentGroup";

    /**
     * Instantiates a new User data.
     */
    public UserData(){}

    /**
     * Instantiates a new User data.
     *
     * @param UID      the uid
     * @param email    the email
     * @param username the username
     * @param phone    the phone
     */
    public UserData(String UID, String email, String username, String phone) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }

    /**
     * Instantiates a new User data.
     *
     * @param UID      the uid
     * @param email    the email
     * @param username the username
     */
    public UserData(String UID, String email, String username) {
        this.UID = UID;
        this.email = email;
        this.username = username;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public String getUID() {
        return UID;
    }

    /**
     * Sets uid.
     *
     * @param UID the uid
     */
    public void setUID(String UID) {
        this.UID = UID;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserData{" +
                "UID='" + UID + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", picture='" + picture + '\'' +
                ", currentGroup='" + currentGroup + '\'' +
                '}';
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

    /**
     * Gets current group.
     *
     * @return the current group
     */
    public String getCurrentGroup() {
        return currentGroup;
    }

    /**
     * Sets current group.
     *
     * @param currentGroup the current group
     */
    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }

}