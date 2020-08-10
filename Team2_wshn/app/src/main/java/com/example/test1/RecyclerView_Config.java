package com.example.test1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.security.spec.KeySpec;
import java.util.List;


public class RecyclerView_Config {
    private Context mContext;
    private DBAdpater mDBAdapter;
    public void setConfig(RecyclerView recyclerView, Context context,
                          List<DB> DBs, List<String> keys) {
        mContext = context;
        mDBAdapter = new DBAdpater(DBs, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mDBAdapter);
    }

    public class DBItemView extends RecyclerView.ViewHolder {
        private ImageView mlist_profile;
        private TextView mlist_userId;
        private TextView mlist_uploadDate;

        private String key;

        public DBItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item, parent, false));

            mlist_profile = (ImageView) itemView.findViewById(R.id.list_profile);
            mlist_userId = (TextView) itemView.findViewById(R.id.list_userId);
            mlist_uploadDate = (TextView) itemView.findViewById(R.id.list_uploadDate);
        }
        public void bind(DB db, String key) {
            Glide.with(mContext).load(db.getProfile()).into(mlist_profile);
            mlist_userId.setText(db.getUserId());
            mlist_uploadDate.setText(db.getUploadTime());
            this.key = key;
        }
    }

    class DBAdpater extends RecyclerView.Adapter<DBItemView> {
        private List<DB> mDBList;
        private List<String> mKeys;

        public DBAdpater(List<DB> mDBList, List<String> mKeys) {
            this.mDBList = mDBList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public DBItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DBItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DBItemView holder, int position) {
            holder.bind(mDBList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mDBList.size(); // DB 리스트의 사이즈 리턴.
        }
    }

    private List<DB> mDBList;
    private List<String> mKeys;
   // private Context context;

//    public RecyclerView_Config(List<DB> List/*, Context context*/) {
//        mDBList = List;
//       // this.context = context;
//    }


//    @NonNull
//    @Override
//    public DBItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
//       DBItemView holder = new DBItemView(view);
//       return holder;
//    }

//    @Override
//    public void onBindViewHolder(@NonNull DBItemView holder, int position) {
//        Glide.with(holder.itemView)
//                .load(List<DB>.get(position).P)
//                .into(holder.list_profile);     // 프로필 이미지를 로드
//
//        holder.list_userId.setText(DB.Place.Video.getUserId());
//        holder.list_uploadDate.setText(DB.Place.Video.getDateTime());
//
//    }

//    @Override
//    public int getItemCount() {
//        // 삼항 연산자
//        return (null != arrayList ? arrayList.size() : 0);
//        //        ㄴ 이 구문이              ㄴ참일 때        ㄴ거짓일 때
//    }


}
