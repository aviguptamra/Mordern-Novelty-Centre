package com.esri.natmoapp.model;

import java.io.Serializable;

public class AddProductSeries implements Serializable {

    private String productSeriesUniqueId ;

    private String pointsScored ;

    private String active;

    private String createdBy;

    private String createdDate;

    private String modifiedBy;

    private String latitude;

    private String longitude;

    private String remarks;

    private String deviceId ;

    private String appVersion ;

    public String getProductSeriesUniqueId() {
        return productSeriesUniqueId;
    }

    public void setProductSeriesUniqueId(String productSeriesUniqueId) {
        this.productSeriesUniqueId = productSeriesUniqueId;
    }

    public String getPointsScored() {
        return pointsScored;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setPointsScored(String pointsScored) {
        this.pointsScored = pointsScored;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}