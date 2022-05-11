package com.example.projectofmurad.groups;

import com.example.projectofmurad.Show;

public class UserGroupData {

    private String UID;
    private String groupKey;
    private boolean madrich;
    private int show;
    private boolean subscribedToAddEvent;
    private boolean subscribedToEditEvent;
    private boolean subscribedToDeleteEvent;

    public static final String KEY_UID = "uid";
    public static final String KEY_GROUP_KEY = "groupKey";
    public static final String KEY_MADRICH = "madrich";
    public static final String KEY_SHOW = "show";
    public static final String KEY_SUBSCRIBED_TO_ADD_EVENT = "subscribedToAddEvent";
    public static final String KEY_SUBSCRIBED_TO_EDIT_EVENT = "subscribedToEditEvent";
    public static final String KEY_SUBSCRIBED_TO_DELETE_EVENT = "subscribedToDeleteEvent";

    public UserGroupData(){}

    public UserGroupData(String UID, String groupKey, boolean madrich) {

        this.UID = UID;
        this.groupKey = groupKey;
        this.madrich = madrich;
        this.show = Show.Madrich.getValue();
    }

    public UserGroupData(String UID, String groupKey, boolean madrich, int show,
                         boolean subscribedToAddEvent, boolean subscribedToEditEvent,
                         boolean subscribedToDeleteEvent) {

        this.UID = UID;
        this.groupKey = groupKey;
        this.madrich = madrich;
        this.show = show;
        this.subscribedToAddEvent = subscribedToAddEvent;
        this.subscribedToEditEvent = subscribedToEditEvent;
        this.subscribedToDeleteEvent = subscribedToDeleteEvent;
    }

    public UserGroupData(boolean madrich, int show) {
        this.madrich = madrich;
        this.show = show;
    }

    public boolean isMadrich() {
        return madrich;
    }

    public void setMadrich(boolean madrich) {
        this.madrich = madrich;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public boolean isSubscribedToAddEvent() {
        return subscribedToAddEvent;
    }

    public void setSubscribedToAddEvent(boolean subscribedToAddEvent) {
        this.subscribedToAddEvent = subscribedToAddEvent;
    }

    public boolean isSubscribedToEditEvent() {
        return subscribedToEditEvent;
    }

    public void setSubscribedToEditEvent(boolean subscribedToEditEvent) {
        this.subscribedToEditEvent = subscribedToEditEvent;
    }

    public boolean isSubscribedToDeleteEvent() {
        return subscribedToDeleteEvent;
    }

    public void setSubscribedToDeleteEvent(boolean subscribedToDeleteEvent) {
        this.subscribedToDeleteEvent = subscribedToDeleteEvent;
    }
}
