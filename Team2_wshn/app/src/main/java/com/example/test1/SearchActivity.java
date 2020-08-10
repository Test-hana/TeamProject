
package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DB> arrayList; // 어댑터와 통신 매개체 역
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView); // 아이디 연결

        new DB_Helper().readDB(new DB_Helper.DataStatus() {
            @Override
            public void DataIsLoaded(List<DB> DBs, List<String> keys) {
                findViewById(R.id.loading_DB_pb).setVisibility(View.GONE);
                new RecyclerView_Config().setConfig(mRecyclerView,
                        SearchActivity.this, DBs, keys);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}

//       // recyclerView.setHasFixedSize(true);     // 리싸클러뷰 기존성능 강화
//        layoutManager = new LinearLayoutManager(this);     //  리싸이클러뷰에 레이아웃 매니저 설정
//        mRecyclerView.setLayoutManager(layoutManager);
//        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)
//
//        database = FirebaseDatabase.getInstance();  // 파이어베이스 데이터베이스 연동
//
//        databaseReference = database.getReference("DB"); // DB 테이블 연결
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // 파이어베이스 데이터베이의 데이터를 받아오는 곳
//                arrayList.clear(); // 기존 배열리스트가 존재하지 않게 초기화
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {  // 반복문으로 데이터리스트 추출
//                    DB db = snapshot.getValue(DB.class);  // *만들어뒀던 Video 객체에 데이터를 담는다.*
//                    arrayList.add(db);  // 담은 데이터들을 배열리스트에 넣고 리사 이클러뷰로 보낼 준비
//                }
//                adapter.notifyDataSetChanged();  // 리스트 저장 및 새로고침
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // 디비를 가져오던 중 에러 발생 시
//                Log.e("SearchActivity", String.valueOf(databaseError.toException()));
//            }
//        });
//
//        adapter = new RecyclerView_Config(arrayList/*, this*/);
//        mRecyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
//
//    }
//}
