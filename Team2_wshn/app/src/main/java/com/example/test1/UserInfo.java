package com.example.test1;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public String nickname; //드로우 메뉴_자체회원 닉네임
    public String photoUrl; //드로우 메뉴_자체회원 프로필

    public UserInfo(String nickname, String photoUrl) {
        //생성자
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }

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
