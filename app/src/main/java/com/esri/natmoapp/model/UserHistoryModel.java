package com.esri.natmoapp.model;

public class UserHistoryModel {

    private RedeemHistory redeemHistory;

    private String redeemUser;

    private String  createdBy;

    private String organizationName;

    public RedeemHistory getRedeemHistory() {
        return redeemHistory;
    }

    public void setRedeemHistory(RedeemHistory redeemHistory) {
        this.redeemHistory = redeemHistory;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRedeemUser() {
        return redeemUser;
    }

    public void setRedeemUser(String redeemUser) {
        this.redeemUser = redeemUser;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
