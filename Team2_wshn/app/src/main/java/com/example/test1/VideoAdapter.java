package com.example.test1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private ArrayList<Video> arrayList;
    private Context context;

    public VideoAdapter(ArrayList<Video> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
       VideoViewHolder holder = new VideoViewHolder(view);
       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .into(holder.list_profile);                     // 프로필 이미지를 로드

        holder.list_user.setText(arrayList.get(position).getUser());
        holder.list_uploadDate.setText(String.valueOf(arrayList.get(position).getUploadDate()));

    }

    @Override
    public int getItemCount() {
        // 삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
        //        ㄴ 이 구문이              ㄴ참일 때        ㄴ거짓일 때
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView list_profile;
        TextView list_user;
        TextView list_uploadDate;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.list_profile = itemView.findViewById(R.id.list_profile);
            this.list_user = itemView.findViewById(R.id.list_userId);
            this.list_uploadDate = itemView.findViewById(R.id.list_uploadDate);

        }
    }
}
