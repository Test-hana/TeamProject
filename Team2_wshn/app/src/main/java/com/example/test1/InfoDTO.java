package com.example.test1;

import android.net.Uri;

public class InfoDTO {

    public String place;
    public double Lat;
    public double Long;
    public String userId; //사용자 id
    public String uploadTime; //업로드 날짜
    public Uri profile; //사용자 프로필 스토리지 저장 url
    //public String videoUri;

    public InfoDTO(){
        //생성자
        this.place = place;
        this.Lat = Lat;
        this.Long = Long;
        this.userId = userId;
        this.uploadTime = uploadTime;
        this.profile = profile;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Uri getProfile() {
        return profile;
    }

    public void setProfile(Uri profile) {
        this.profile = profile;
    }
}
