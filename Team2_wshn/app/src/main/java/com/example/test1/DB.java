package com.example.test1;

import com.google.android.gms.maps.model.LatLng;

public class DB {   // Database에 있는 비디오 객체


    private String place;
    private double Lat;
    private double Long;
    private String uploadTime;
    private String videoUri;
    private String profile;
    private String userId;

    public DB() {
    }

    public DB(String place, double lat, double aLong, String uploadTime, String videoUri, String profile, String userId) {
        this.place = place;
        Lat = lat;
        Long = aLong;
        this.uploadTime = uploadTime;
        this.videoUri = videoUri;
        this.profile = profile;
        this.userId = userId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}


