package com.example.test1.PlayUpload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

    private static final int PICK_VIDEO = 1, RECORD_VIDEO = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    VideoView videoView;
    Button button, button2, button3;
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
        button3 = findViewById(R.id.button_recordVideo);
        button = findViewById(R.id.button_upload_main);
        button2 = findViewById(R.id.button_chooseVideo);
        progressBar = findViewById(R.id.progressBar_main);
        editText = findViewById(R.id.et_video_name);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
//        videoView.start();

        /** 위도 경도 UserId 받음(Main -> Fragment -> this). **/
        videoMember = new VideoMember();
        Intent intent = getIntent();
        bundle = intent.getExtras(); //MainActivity에서 전달한 str값 bundle에 저장

        videoMember.setLong(bundle.getDouble("Long",0));
        videoMember.setLat(bundle.getDouble("Lat",0));
        videoMember.setUserId(bundle.getString("UserId"));
        videoMember.setPlaceName(bundle.getString("장소이름"));
        //videoMember.setBitmap(bundle.getParcelable("장소사진"));
        Toast.makeText(this,
                videoMember.getPlaceName()
                + "\nLong : "
                + videoMember.getLong()
                + "\nLat : " + videoMember.getLat()
                , Toast.LENGTH_LONG).show();

        editText.setHint(videoMember.getPlaceName());


        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RecordVideo();
            }
        });

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
    public void onBackPressed() {
        super.onBackPressed();

        videoView.stopPlayback();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RECORD_VIDEO && resultCode == RESULT_OK) {

            try {
                videoUri = data.getData();
                videoView.setVideoURI(videoUri);
            } catch (Exception e){
                Toast.makeText(this,"No file selected", Toast.LENGTH_SHORT).show();
            }
//            videoView.start();
        }

        else if (requestCode == PICK_VIDEO && resultCode == RESULT_OK &&
                data != null && data.getData() != null ){
            try {
                videoUri = data.getData();
                videoView.setVideoURI(videoUri);
            } catch (Exception e){
                Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();
            }
//            videoView.start();
        }
    }

    public void RecordVideo() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //권한이 부여되면 PERMISSION_GRANTED 거부되면 PERMISSION_DENIED 리턴

//권한 요청 할 필요가 있는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                //권한 요청을 해야할 필요가 있는 경우(사용자가 DONT ASK ME AGIAN CHECK + DENY 선택)

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                //requestPermissions 메소드는 비동기적으로 동작한다. 왜냐면 이 권한 검사 및 요청 메소드는
                //메인 액티비티에서 동작하기떄문에(메인쓰레드) 사용자 반응성이 굉장히 중요한 파트이다. 여기서 시간을
                //오래 끌어버리면 사람들이 답답함을 느끼게 된다. requestPermissions의 결과로 콜백 메소드인
                //onRequestPermissionsResult()가 호출된다. 오버라이딩 메소드이다. Ctrl+O

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 : 저화질  1: 고화질
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 104857600L); // 100mb Limit
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60); // 영상 녹화 제한 시간 30초.
            startActivityForResult(intent, RECORD_VIDEO);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
                                 videoMember_firestore.put("Lng",bundle.getDouble("Long",0)); */

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

