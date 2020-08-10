package com.example.test1;

public class Video {   // DB에 있는 비디오 객
    private String User;
    private String Uri;
    private String Profile;
    private double LatLng;
    private int UploadDate;


    public Video() {
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public double getLatLng() {
        return LatLng;
    }

    public void setLatLng(double latLng) {
        LatLng = latLng;
    }

    public int getUploadDate() {
        return UploadDate;
    }

    public void setUploadDate(int uploadDate) {
        UploadDate = uploadDate;
    }
}
