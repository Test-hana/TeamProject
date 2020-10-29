package com.example.test1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberInfoActivity extends AppCompatActivity {

    private static final String TAG = "MemberInfoActivity";
    public ImageView iv_profile;
    private Button btn_ok, picture, gallery;

    public EditText tv_nickname;
    private String profilePath;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);


        tv_nickname = (EditText) findViewById(R.id.tv_nickname);

        iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardView cardView = findViewById(R.id.btns_CardView);
                if (cardView.getVisibility() == View.VISIBLE) {
                    cardView.setVisibility(View.GONE);
                } else {
                    cardView.setVisibility(View.VISIBLE);
                }
            }
        });



        picture = findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberInfoActivity.this, CameraActivity.class);
                startActivityForResult(intent, 0);

            }
        });

        gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //갤러리 권한요청
                if (ContextCompat.checkSelfPermission(MemberInfoActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MemberInfoActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInfoActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(MemberInfoActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        startToast("권한을 허용해주세요.");
                    }
                } else {
                    Intent intent = new Intent(MemberInfoActivity.this, GalleryActivity.class);
                    startActivityForResult(intent, 0);
                }

            }
        });


        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoSetting();

            }
        });






    }//onCreate 끝

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MemberInfoActivity.this, GalleryActivity.class);
                    startActivityForResult(intent, 0);
                }  else {
                    startToast("권한을 허용해주세요.");
                }

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:{
                if(resultCode == Activity.RESULT_OK){
                    profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(iv_profile);
                    Log.e("로그", "profilePath:"+profilePath);


                }
            }
        }
    }

    private void InfoSetting(){
        final String nickname = tv_nickname.getText().toString();

        //프로필 설정하기
        if(nickname.length()>0){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");


            if(profilePath == null){
                startToast("프로필을 설정해주세요.");
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Log.e("실패1","실패");
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final Uri downloadUri = task.getResult();
                                Log.e("성공","성공 : "+downloadUri);

                                mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                                final UserInfo userInfo = new UserInfo(nickname,downloadUri.toString());

                                mDatabase.child(user.getUid()).setValue(userInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startToast("회원정보 등록을 성공하였습니다.");
                                                Log.d(TAG, "DocumentSnapshot successfully written!");

                                                Intent intent = new Intent(MemberInfoActivity.this, MainActivity.class);
                                                Bundle bundle = new Bundle(); //번들 객체 생성

                                                bundle.putString("자체닉네임", nickname);
                                                bundle.putString("자체프로필uri", downloadUri.toString());
                                                intent.putExtras(bundle);
                                                startActivity(intent);


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                startToast("회원정보 등록을 실패하였습니다.");
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });

                            } else {
                                Log.e("로그","실패");
                            }
                        }
                    });
                }catch (FileNotFoundException e){
                    Log.e("로그","에러"+e.toString());
                }
            }

        }else {
            startToast("회원정보를 설정해주세요.");
        }
    }


    private void startToast(String msg){ //리스너에서 Toast msg가 불가해서
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }





}
