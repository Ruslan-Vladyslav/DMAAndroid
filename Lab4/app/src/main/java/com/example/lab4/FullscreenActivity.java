package com.example.lab4;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

public class FullscreenActivity extends AppCompatActivity {

    VideoView videoView;
    Button btnPlay, btnPause, btnStop, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_fullscreen);

        videoView = findViewById(R.id.videoViewFullscreen);

        btnBack = findViewById(R.id.btnBackFullscreen);
        btnPlay = findViewById(R.id.btnPlayFullscreen);
        btnPause = findViewById(R.id.btnPauseFullscreen);
        btnStop = findViewById(R.id.btnStopFullscreen);

        Uri videoUri = getIntent().getParcelableExtra("videoUri");

        if (videoUri != null) {
            videoView.setVideoURI(videoUri);
            videoView.start();
            updateButtonsColor("play");
        } else {
            finish();
        }

        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            if (!videoView.isPlaying()) {
                videoView.start();
            }
            updateButtonsColor("play");
        });

        btnPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            }
            updateButtonsColor("pause");
        });

        btnStop.setOnClickListener(v -> {
            videoView.stopPlayback();
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                mp.setVolume(1f, 1f);
            });
            updateButtonsColor("stop");
        });
    }

    private void updateButtonsColor(String active) {
        int defaultColor = Color.parseColor("#616161"); // сірий
        int activeColor = Color.parseColor("#BDBDBD");

        btnPlay.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        btnPause.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        btnStop.setBackgroundTintList(ColorStateList.valueOf(defaultColor));

        switch (active) {
            case "play":
                btnPlay.setBackgroundTintList(ColorStateList.valueOf(activeColor));
                break;
            case "pause":
                btnPause.setBackgroundTintList(ColorStateList.valueOf(activeColor));
                break;
            case "stop":
                btnStop.setBackgroundTintList(ColorStateList.valueOf(activeColor));
                break;
        }
    }
}