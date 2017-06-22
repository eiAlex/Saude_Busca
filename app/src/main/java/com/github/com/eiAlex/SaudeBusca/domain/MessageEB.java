package com.github.com.eiAlex.SaudeBusca.domain;

import android.location.Location;


public class MessageEB {
    private String resultMessage;
    private User user;
    private Location location;
    private String className;

    public String getResultMessage() {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
}
