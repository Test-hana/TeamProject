package com.example.test1.PlayUpload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.test1.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class PlayUploadActivity extends AppCompatActivity {
    private static final String TAG = "PlayUploadActivity";

    private static final int PICK_VIDEO = 1;
    VideoView videoView;
    Button button, button2;
    ProgressBar progressBar;
    EditText editText;
    private Uri videoUri;
    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;
    VideoMember videoMember;
    UploadTask uploadTask;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_upload);

        storageReference = FirebaseStorage.getInstance().getReference("Videos");
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");
        firestore = FirebaseFirestore.getInstance();

        videoView = findViewById(R.id.videoView_main);
        button = findViewById(R.id.button_upload_main);
        button2 = findViewById(R.id.button_chooseVideo);
        progressBar = findViewById(R.id.progressBar_main);
        editText = findViewById(R.id.et_video_name);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        /** 위도 경도 UserId 받음(Main -> Fragment -> this). **/
        videoMember = new VideoMember();
        Intent intent = getIntent();
        bundle = intent.getExtras(); //MainActivity에서 전달한 str값 bundle에 저장

        videoMember.setLong(bundle.getDouble("Long",0));
        videoMember.setLat(bundle.getDouble("Lat",0));
        videoMember.setUserId(bundle.getString("UserId"));
        Toast.makeText(this,"Long : " + videoMember.getLong() + " Lat : " + videoMember.getLat(), Toast.LENGTH_LONG).show();

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ChooseVideo();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadVideo();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO || resultCode == RESULT_OK ||
                data != null || data.getData() != null ){
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    /** 업로드할 파일 선택 **/
    public void ChooseVideo() {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_VIDEO);
    }

    private String getExt (Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

//    public void ShowVideo(Bundle bundle) {
//
//        Intent intent = new Intent(PlayUploadActivity.this, ShowVideo.class);
//        intent.putExtra("myBundle",bundle);
//        startActivity(intent);
//        Toast.makeText(this,"PlayUpload액티비티에서 "+ bundle.getString("UserId"), Toast.LENGTH_LONG).show();
//
//    }

    private void UploadVideo(){
        String videoName = editText.getText().toString();
        String search = editText.getText().toString().toLowerCase();
        if(videoUri != null || !TextUtils.isEmpty(videoName)){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri); /** 비디오 업로드 **/

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException(); //Returns the exception that caused the Task to fail. Returns null if the Task is not yet complete, or completed successfully.

                    }
                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(PlayUploadActivity.this,"Data uploaded", Toast.LENGTH_SHORT).show();

                                videoMember.setName(videoName);
                                videoMember.setVideourl(downloadUrl.toString());
                                videoMember.setSearch(search);
                                videoMember.setUploadTime(Timestamp.now());

                                String i = databaseReference.push().getKey();
                                databaseReference.child(i).setValue(videoMember);

                                /** firestore 데이터 컬렉션 도큐먼트 생성.
                                 Map<String, Object> videoMember_firestore = new HashMap<>();
                                 videoMember_firestore.put("videoName", videoName);
                                 videoMember_firestore.put("VideoUrl", downloadUrl.toString());
                                 videoMember_firestore.put("userId", bundle.getString("UserId"));
                                 videoMember_firestore.put("Lat",bundle.getDouble("Lat",0));
                                 videoMember_firestore.put("Lng",bundle.getDouble("Long",0));
                                 */
                                /** Add a new document with a generated ID */
                                firestore.collection("videos").document()
                                        .set(videoMember)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PlayUploadActivity.this, "VideoMember Saved", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PlayUploadActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, e.toString());
                                            }
                                        });

                            } else {
                                Toast.makeText(PlayUploadActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this,"All Fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}

