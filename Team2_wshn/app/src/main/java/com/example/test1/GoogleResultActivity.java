package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class GoogleResultActivity extends AppCompatActivity {

    private TextView tv_id;
    private ImageView iv_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_result);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");//MainActivity로 부터 닉네임 전달받음
        String photourl = intent.getStringExtra("photourl");//MainActivity로 부터 프로필사진 전달받음

        tv_id = findViewById(R.id.tv_id);
        tv_id.setText(nickname);//닉네임 text를 텍스트 뷰에 세팅

        iv_profile = findViewById(R.id.iv_profile);
        Glide.with(this).load(photourl).into(iv_profile);//프로필 url를 이미지 뷰에 세팅

    }
}
