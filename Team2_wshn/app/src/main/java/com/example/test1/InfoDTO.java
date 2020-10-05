package com.example.test1;

import android.net.Uri;

import java.io.Serializable;

public class InfoDTO implements Serializable {

    public String nickname;
    public String photoUrl;
    /*
    public String place;
    public double Lat;
    public double Long;
    public String userId; //사용자 id
    public String uploadTime; //업로드 날짜
    //public Uri profile; //사용자 프로필 스토리지 저장 url
    //public String videoUri;
    */


    public InfoDTO(String nickname, String photoUrl){
        //생성자
        /*
        this.place = place;
        this.Lat = Lat;
        this.Long = Long;
        this.userId = userId;
        this.uploadTime = uploadTime;
         */
        //this.profile = profile;
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }
/*
    public String getPlace() {
        return this.place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public double getLat() {
        return this.Lat;
    }

    public void setLat(double lat) {
       this.Lat = lat;
    }

    public double getLong() {
        return this.Long;
    }

    public void setLong(double aLong) {
        this.Long = aLong;
    }

    public String getUserId() {
            return this.userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUploadTime() {
        return this.uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }


    */
    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getphotoUrl() {
        return this.photoUrl;
    }

    public void setphotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

