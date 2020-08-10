package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PlayActivity extends AppCompatActivity {

    private Button btn_search;

    private StorageReference mStorageRef;
    private VideoView mainVideoView;
    private ImageView playBtn;
    private TextView currentTimer;
    private TextView durationTimer;
    private ProgressBar currentProgress;
    private ProgressBar bufferProgress;

    private boolean isPlaying;

    private Uri videoUri;

    private int current = 0;
    private int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        btn_search = findViewById(R.id.btn_search);//리뷰 목록 버튼
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //리뷰 영상 목록화면으로 이동
                Intent intent = new Intent(PlayActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });


        isPlaying = false;

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mainVideoView = (VideoView) findViewById(R.id.mainVideoView);
        playBtn = (ImageView) findViewById(R.id.playBtn);
        currentProgress = (ProgressBar) findViewById(R.id.videoProgress);
        currentProgress.setMax(100);

        currentTimer = (TextView) findViewById(R.id.currentTimer);
        durationTimer = (TextView) findViewById(R.id.durationTimer);
        bufferProgress = (ProgressBar) findViewById(R.id.bufferProgress);

        videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/loginex-67236.appspot.com/o/%EC%83%98%ED%94%8C%20%EC%98%81%EC%83%81.mp4?alt=media&token=5ca0c338-4ba3-4d89-a910-2b2f2c0d3126");
        //하나 스토리지 영상 토큰 값

        mainVideoView.setVideoURI(videoUri);
        mainVideoView.requestFocus();

        mainVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {

                if(i == mediaPlayer.MEDIA_INFO_BUFFERING_START){

                    bufferProgress.setVisibility(View.VISIBLE);

                } else if( i == mediaPlayer.MEDIA_INFO_BUFFERING_END){

                    bufferProgress.setVisibility(View.INVISIBLE);

                }

                return false;
            }
        });

        mainVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                duration = mediaPlayer.getDuration()/1000;
                String durationString = String.format("%02d:%02d", duration / 60, duration % 60);
                //영상 재생 시, 영상 전체 시간 감소 안됨, 수정하기
                durationTimer.setText(durationString);
            }
        });

        mainVideoView.start();
        isPlaying = true;
        playBtn.setImageResource(R.drawable.pause_action);

        new VideoProgress().execute();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPlaying) {

                    mainVideoView.pause();
                    bufferProgress.setVisibility(View.INVISIBLE);
                    isPlaying = false;
                    playBtn.setImageResource(R.drawable.play_action);

                } else {

                    mainVideoView.start();
                    isPlaying = true;
                    playBtn.setImageResource(R.drawable.pause_action);


                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        isPlaying = false;
    }

    public class VideoProgress extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            do {

                if(isPlaying){

                    current = mainVideoView.getCurrentPosition() / 1000;
                    publishProgress(current);

                }

            } while(currentProgress.getProgress() <= 100);


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            try {

                int currentPercent = values[0] * 100/duration;
                currentProgress.setProgress(currentPercent);

                String currentString = String.format("%02d:%02d", values[0] / 60, values[0] % 60);

                currentTimer.setText(currentString);

            } catch (Exception e) {

            }
        }
    }




}
