package com.esri.natmoapp.model;

public class LoginResponse {

    private String token;

    private Registration userProfile;

    public Registration getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(Registration userProfile) {
        this.userProfile = userProfile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
