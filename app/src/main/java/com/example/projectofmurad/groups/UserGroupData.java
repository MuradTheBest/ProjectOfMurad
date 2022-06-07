package com.example.projectofmurad.groups;

/**
 * The type User group data.
 */
public class UserGroupData {

    private String UID;
    private String groupKey;
    private boolean madrich;
    private boolean subscribedToAddEvent;
    private boolean subscribedToEditEvent;
    private boolean subscribedToDeleteEvent;

    /**
     * The constant KEY_MADRICH.
     */
    public static final String KEY_MADRICH = "madrich";

    /**
     * Instantiates a new User group data.
     */
    public UserGroupData(){}

    /**
     * Instantiates a new User group data.
     *
     * @param UID      the uid
     * @param groupKey the group key
     * @param madrich  the madrich
     */
    public UserGroupData(String UID, String groupKey, boolean madrich) {
        this.UID = UID;
        this.groupKey = groupKey;
        this.madrich = madrich;
    }

    /**
     * Is madrich boolean.
     *
     * @return the boolean
     */
    public boolean isMadrich() {
        return madrich;
    }

    /**
     * Sets madrich.
     *
     * @param madrich the madrich
     */
    public void setMadrich(boolean madrich) {
        this.madrich = madrich;
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
     * Gets group key.
     *
     * @return the group key
     */
    public String getGroupKey() {
        return groupKey;
    }

    /**
     * Sets group key.
     *
     * @param groupKey the group key
     */
    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    /**
     * Is subscribed to add event boolean.
     *
     * @return the boolean
     */
    public boolean isSubscribedToAddEvent() {
        return subscribedToAddEvent;
    }

    /**
     * Sets subscribed to add event.
     *
     * @param subscribedToAddEvent the subscribed to add event
     */
    public void setSubscribedToAddEvent(boolean subscribedToAddEvent) {
        this.subscribedToAddEvent = subscribedToAddEvent;
    }

    /**
     * Is subscribed to edit event boolean.
     *
     * @return the boolean
     */
    public boolean isSubscribedToEditEvent() {
        return subscribedToEditEvent;
    }

    /**
     * Sets subscribed to edit event.
     *
     * @param subscribedToEditEvent the subscribed to edit event
     */
    public void setSubscribedToEditEvent(boolean subscribedToEditEvent) {
        this.subscribedToEditEvent = subscribedToEditEvent;
    }

    /**
     * Is subscribed to delete event boolean.
     *
     * @return the boolean
     */
    public boolean isSubscribedToDeleteEvent() {
        return subscribedToDeleteEvent;
    }

    /**
     * Sets subscribed to delete event.
     *
     * @param subscribedToDeleteEvent the subscribed to delete event
     */
    public void setSubscribedToDeleteEvent(boolean subscribedToDeleteEvent) {
        this.subscribedToDeleteEvent = subscribedToDeleteEvent;
    }
}
