package com.example.test1;

public class UploadMember {
    private String VideoName;
    private String VideoUri;

    private UploadMember() {}
    public UploadMember(String name, String videoUri){

        if(name.trim().equals("")){
            name = "not available";
        }

        VideoName = name;
        VideoUri = videoUri;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public String getVideoUri() {
        return VideoUri;
    }

    public void setVideoUri(String videoUri) {
        VideoUri = videoUri;
    }
}
