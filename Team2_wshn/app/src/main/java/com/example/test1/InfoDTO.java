package com.example.test1;

import java.io.Serializable;

public class InfoDTO implements Serializable {

    public String place;
    public double Lat;
    public double Long;
    public String userId; //사용자 id
    public String uploadTime; //업로드 날짜
    //public String videoUri;

    public InfoDTO(String place, double Lat, double Long, String userId, String uploadTime){
        //생성자
        this.place = place;
        this.Lat = Lat;
        this.Long = Long;
        this.userId = userId;
        this.uploadTime = uploadTime;

    }
    /*
    public double getLong() {
        return this.Long;
    }

    public void setLong(double Long) {
        this.Long = Long;
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

    public String getPlace() {
        return this.place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public double getLat() {
        return this.Lat;
    }

    public void setLat(double Lat) {
       this.Lat = Lat;
    }

*/

}

