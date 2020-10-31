package com.example.test1.PlayUpload;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class PlayActivity extends AppCompatActivity {
    private static final String TAG = "PlayActivity";

    RecyclerView recyclerView;
    VideoMember videoMember;
    FirebaseFirestore firestore;
    CollectionReference collectionReference;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        recyclerView = findViewById(R.id.recyclerview_ShowVideo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("videos");

        videoMember = new VideoMember(); /** 이거 빼면 null pointer exception.*/

        Intent intent = getIntent();
        bundle = intent.getExtras();
        videoMember.setLong(bundle.getDouble("Long",0));
        videoMember.setLat(bundle.getDouble("Lat",0));
        videoMember.setUserId(bundle.getString("UserId"));
        Toast.makeText(this, videoMember.getUserId() +"\n" + videoMember.getLong() +"\n" + videoMember.getLat(), Toast.LENGTH_SHORT).show();


    }


    @Override
    protected void onStart() {
        super.onStart();



//        videoMember.setLong(bundle.getDouble("Long",0));
//        videoMember.setLat(bundle.getDouble("Lat",0));
//        videoMember.setUserId(bundle.getString("UserId"));

//        if (intent.hasExtra("myBundle")) {
//
  //         Toast.makeText(this, videoMember.getUserId() +" "+ videoMember.getLong() +" "+ videoMember.getLat(), Toast.LENGTH_SHORT).show();
//
//    /* "nameKey"라는 이름의 key에 저장된 값이 있다면
//       textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
//
//        } else {
//            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show();
//        }



        // Query (firestore)
        Query firestoreQuery = firestore.collection("videos").orderBy("uploadTime");

        // Recycler Options
        FirestoreRecyclerOptions<VideoMember> options = new FirestoreRecyclerOptions.Builder<VideoMember>()
                .setQuery(firestoreQuery, VideoMember.class)
                .build();

        FirestoreRecyclerAdapter<VideoMember, ViewHolder> firestoreRecyclerAdapter =
                new FirestoreRecyclerAdapter<VideoMember, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull VideoMember videoMember) {
                        viewHolder.setExoplayer(getApplication(), videoMember.getName(), videoMember.getVideourl());
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_video, parent, false);
                        return new ViewHolder(view);
                    }
                };
        firestoreRecyclerAdapter.startListening();
        recyclerView.setAdapter(firestoreRecyclerAdapter);

    }
}