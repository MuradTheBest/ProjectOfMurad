package com.example.projectofmurad.groups;

public class UserGroupData {

    private String UID;
    private String groupKey;
    private boolean madrich;
    private boolean subscribedToAddEvent;
    private boolean subscribedToEditEvent;
    private boolean subscribedToDeleteEvent;

    public static final String KEY_MADRICH = "madrich";

    public UserGroupData(){}

    public UserGroupData(String UID, String groupKey, boolean madrich) {
        this.UID = UID;
        this.groupKey = groupKey;
        this.madrich = madrich;
    }

    public boolean isMadrich() {
        return madrich;
    }

    public void setMadrich(boolean madrich) {
        this.madrich = madrich;
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
