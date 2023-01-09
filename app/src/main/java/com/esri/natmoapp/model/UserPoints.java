package com.esri.natmoapp.model;

import java.io.Serializable;

public class UserPoints implements Serializable {

    private String UserId;

    private String Username;

    private String User_Organization;

    private String Points_Scored;

    private boolean isActive;

    private String CreatedBy;

    private String CreatedDate;

    private String ModifiedBy;

    private String ModifiedDate;

    private Float Latitude;

    private Float Longitude;

    private String DeviceId;

    private String AppVersion;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPoints_Scored() {
        return Points_Scored;
    }

    public void setPoints_Scored(String points_Scored) {
        Points_Scored = points_Scored;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public String getUser_Organization() {
        return User_Organization;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setUser_Organization(String user_Organization) {
        User_Organization = user_Organization;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getModifiedBy() {
        return ModifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        ModifiedBy = modifiedBy;
    }

    public Float getLatitude() {
        return Latitude;
    }

    public void setLatitude(Float latitude) {
        Latitude = latitude;
    }

    public Float getLongitude() {
        return Longitude;
    }

    public void setLongitude(Float longitude) {
        Longitude = longitude;
    }

    public String getModifiedDate() {
        return ModifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        ModifiedDate = modifiedDate;
    }

}
