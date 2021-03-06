package com.example.test1.PlayUpload;

import android.graphics.Bitmap;

import com.google.firebase.Timestamp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VideoMember implements Serializable {
    private String name;
    private String Videourl;
    private String search;
    private Double Long;
    private Double Lat;
    private String UserId;
    private Timestamp uploadTime;
    private String placeName;
    //private Bitmap bitmap;

    public VideoMember() {}

    public VideoMember(String name, String videourl, String search, Double aLong, Double lat, String userId, Timestamp uploadTime, String placeName) {
        this.name = name;
        Videourl = videourl;
        this.search = search;
        Long = aLong;
        Lat = lat;
        UserId = userId;
        this.uploadTime = uploadTime;
        this.placeName = placeName;
        //this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideourl() {
        return Videourl;
    }

    public void setVideourl(String videourl) {
        Videourl = videourl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Double getLong() {
        return Long;
    }

    public void setLong(Double aLong) {
        Long = aLong;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    /*
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    */

}