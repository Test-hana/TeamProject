package com.example.test1.PlayUpload;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.test1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PlayActivity extends AppCompatActivity {
    private static final String TAG = "PlayActivity";

    RecyclerView recyclerView;
    VideoMember videoMember;
    FirebaseFirestore firestore;
    CollectionReference collectionReference;
    Bundle bundle;
    String name, url;


    SwipeRefreshLayout swipeRefreshLayout;

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
        if(bundle!=null){
            videoMember.setLong(bundle.getDouble("Long",0));
            videoMember.setLat(bundle.getDouble("Lat",0));
            videoMember.setUserId(bundle.getString("UserId"));
//            Toast.makeText(this, videoMember.getUserId() +"\n" + videoMember.getLong() +"\n" + videoMember.getLat(), Toast.LENGTH_SHORT).show();

        }

        // Query (firestore)
        Query firestoreQuery = firestore.collection("videos")
                .whereEqualTo("long", videoMember.getLong())  // 경도 동일.
                .whereEqualTo("lat", videoMember.getLat())  // 위도 동일.  whereEqualTo는 다른 필드 가능.
                .orderBy("uploadTime", Query.Direction.DESCENDING); // Greater/LessThan과 동일 필드만 가능.

        // Recycler Options
        FirestoreRecyclerOptions<VideoMember> options = new FirestoreRecyclerOptions.Builder<VideoMember>()
                .setQuery(firestoreQuery, VideoMember.class)
                .build();

        FirestoreRecyclerAdapter<VideoMember, ViewHolder> firestoreRecyclerAdapter =
                new FirestoreRecyclerAdapter<VideoMember, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull VideoMember videoMember) {
                        viewHolder.setExoplayer(getApplication(), videoMember.getName(), videoMember.getVideourl());

                        viewHolder.setOnClicklistener(new ViewHolder.Clicklistener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                // todo
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                name = getItem(position).getName();
                                showDeleteDialog(name);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_video, parent, false);
                        return new ViewHolder(view);
                    }
                };


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        firestoreRecyclerAdapter.startListening();
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void showDeleteDialog(String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
        builder.setTitle("영상 삭제");
        builder.setMessage("정말로 영상을 삭제하시겠어요?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Query query = collectionReference.whereEqualTo("name", name).orderBy("uploadTime", Query.Direction.DESCENDING);
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) { // 이름이 같은 건 다 지원짐.

                                url = documentSnapshot.get("videourl").toString();
//                                url = documentSnapshot.getReference().getPath()+ "/videourl/";

                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

                                storageReference
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PlayActivity.this, "storage 영상 삭제 성공!", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onSuccess: Storage video file deleted!");

                                                documentSnapshot.getReference()
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                finish();
                                                                overridePendingTransition(0, 0);
                                                                startActivity(getIntent());
                                                                overridePendingTransition(0, 0);

                                                                Toast.makeText(PlayActivity.this, "Firestore 영상 삭제 성공!", Toast.LENGTH_SHORT).show();
                                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(PlayActivity.this, "Firestore 영상 삭제 오류!", Toast.LENGTH_SHORT).show();
                                                                Log.w(TAG, "Error deleting document", e);
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PlayActivity.this, "storage 영상 삭제 오류!", Toast.LENGTH_SHORT).show();
                                                Log.w(TAG, "onFailure: Error deleting Storage video file", e);
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}