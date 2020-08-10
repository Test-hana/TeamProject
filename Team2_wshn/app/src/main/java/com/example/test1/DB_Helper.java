package com.example.test1;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DB_Helper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceBooks;
    private List<DB> DBs = new ArrayList<>();

    // all of the interactions with RealTime DataBase are asynchronous.
    public interface  DataStatus{ // interface를 사용한 객체는 반드시 interface의 메소드를 구현해야한다는 규제.
        void DataIsLoaded(List<DB> DBs, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }
    public DB_Helper() {
        mDatabase = FirebaseDatabase.getInstance();  // 디비 갖고옴
        mReferenceBooks = mDatabase.getReference("DB"); // 디비의 특정 노드 갖고옴(하위항목 포함).
    }

    public void readDB(final DataStatus dataStatus) { // final 클래스 선언으로 상속 받을 수 없게 함.
        mReferenceBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터 입력,수정,삭제 일어날 때마다 호출.
                // addValueEventListener()가 호출 될때마다 아님(asynchronous).
                // DataSnapshot이 특정 노드의 키와 값을 가짐. 여기서는 "DB"노드의 키,값을 가짐.
                DBs.clear(); // DB 초기화
                List<String> keys = new ArrayList<>(); // 키 리스트 생성.

                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    // keyNode라는 데이터스냅샷 변수에 받아온 dataSnapshot의 자식들을 하나씩 반복 저장.
                    keys.add(keyNode.getKey());
                    // 이번 반복에 keyNode에 저장된 key(String타입)를 키 리스트(keys)에 추가.
                    DB db = keyNode.getValue(DB.class);
                    // DB 타입의 변수 db를 생성하고
                    // 이번 반복에 keyNode에 저장돼있는 값을 저장시킴(DB클래스의 타입을 참조하여)
                    DBs.add(db); // DBs에 db 추가.
                }  // 이로써 DBs에는 onDataChange에 변경된 DB의 스냅샷이 저장되고
                   // keys에는 그 DB의 키(String)들이 저장됨.
                dataStatus.DataIsLoaded(DBs, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SearchActivity", String.valueOf(databaseError.toException()));

            }
        });
    }

}
