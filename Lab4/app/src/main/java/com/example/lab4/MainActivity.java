package com.example.lab4;

import static android.net.Uri.fromFile;
import android.provider.OpenableColumns;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import android.os.Environment;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    MediaPlayer mediaPlayer;
    TextView textTitle;
    ActivityResultLauncher<String> filePicker;
    Uri selectedUri;

    boolean isVideo = true;

    LinearLayout audioLayout;
    TextView textPlaceholder;
    SeekBar seekBar;
    Button btnFullscreen;
    Button btnPlay;
    Button btnPause;
    Button btnStop;

    TextView textCurrentTime;
    TextView textTotalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        audioLayout = findViewById(R.id.audioLayout);
        textPlaceholder = findViewById(R.id.textPlaceholder);
        seekBar = findViewById(R.id.seekBar);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        updateButtonsColor(btnPlay, btnPause, btnStop, null);
        btnPlay.setEnabled(false);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);

        Button btnPick = findViewById(R.id.btnPick);
        Button btnDownload = findViewById(R.id.btnDownload);
        btnFullscreen = findViewById(R.id.btnFullscreen);

        EditText editUrl = findViewById(R.id.editUrl);
        textTitle = findViewById(R.id.textTitle);

        videoView.setVisibility(View.GONE);
        audioLayout.setVisibility(View.GONE);
        textPlaceholder.setVisibility(View.VISIBLE);

        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);

        // вибір файлу
        filePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedUri = uri;

                        btnPlay.setEnabled(true);
                        btnPause.setEnabled(true);
                        btnStop.setEnabled(true);

                        updateButtonsColor(btnPlay, btnPause, btnStop, null);

                        String name = "File";

                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null) {
                            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            if (cursor.moveToFirst()) {
                                name = cursor.getString(nameIndex);
                            }
                            cursor.close();
                        }
                        textTitle.setText(name);

                        String type = getContentResolver().getType(uri);

                        if (type != null && type.startsWith("video")) {
                            isVideo = true;

                            videoView.setVisibility(View.VISIBLE);
                            audioLayout.setVisibility(View.GONE);
                            textPlaceholder.setVisibility(View.GONE);
                            btnFullscreen.setVisibility(View.VISIBLE);

                            videoView.setVideoURI(uri);
                            videoView.seekTo(0);

                            textCurrentTime.setText("00:00");
                            videoView.setOnPreparedListener(mp -> {
                                textTotalTime.setText(formatTime(videoView.getDuration()));
                            });

                        } else { // аудіо
                            isVideo = false;

                            videoView.setVisibility(View.GONE);
                            audioLayout.setVisibility(View.VISIBLE);
                            textPlaceholder.setVisibility(View.GONE);
                            btnFullscreen.setVisibility(View.GONE);

                            if (mediaPlayer != null) mediaPlayer.release();
                            mediaPlayer = MediaPlayer.create(this, uri);

                            seekBar.setProgress(0);
                            textCurrentTime.setText("00:00");
                            textTotalTime.setText(formatTime(mediaPlayer.getDuration()));

                            startSeekBar();
                        }
                    }
                }
        );

        btnFullscreen.setVisibility(View.GONE);
        btnFullscreen.setOnClickListener(v -> {
            if (isVideo && selectedUri != null) {
                Intent intent = new Intent(this, FullscreenActivity.class);
                intent.putExtra("videoUri", selectedUri);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Choose video for fullscreen", Toast.LENGTH_SHORT).show();
            }
        });

        btnPick.setOnClickListener(v -> filePicker.launch("*/*"));

        editUrl.setOnClickListener(v -> {
            editUrl.setFocusable(true);
            editUrl.setFocusableInTouchMode(true);
            editUrl.requestFocus();
        });

        editUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                editUrl.setFocusable(false);
                editUrl.setFocusableInTouchMode(false);
            }
        });

        btnDownload.setOnClickListener(v -> {
            String url = editUrl.getText().toString().trim();

            if (url.isEmpty()) {
                Toast.makeText(this, "Enter URL", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadFile(url);
        });

        btnPlay.setOnClickListener(v -> {
            if (selectedUri == null) return;

            if (isVideo) {
                videoView.start();
                startVideoTimer();
            } else if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            updateButtonsColor(btnPlay, btnPause, btnStop, "play");
        });

        btnPause.setOnClickListener(v -> {
            if (isVideo) {
                videoView.pause();
            } else if (mediaPlayer != null) {
                mediaPlayer.pause();
            }

            updateButtonsColor(btnPlay, btnPause, btnStop, "pause");
        });

        btnStop.setOnClickListener(v -> {
            if (isVideo) {
                videoView.stopPlayback();
                videoView.setVideoURI(selectedUri);
                seekBar.setProgress(0);
                textCurrentTime.setText("00:00");
            } else if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
                textCurrentTime.setText("00:00");
            }

            updateButtonsColor(btnPlay, btnPause, btnStop, "stop");
        });

        videoView.setOnPreparedListener(mp -> {
            seekBar.setMax(videoView.getDuration());
            textTotalTime.setText(formatTime(videoView.getDuration()));

            new Thread(() -> {
                while (videoView.isPlaying()) {
                    runOnUiThread(() -> {
                        seekBar.setProgress(videoView.getCurrentPosition());
                        textCurrentTime.setText(formatTime(videoView.getCurrentPosition()));
                    });
                    try { Thread.sleep(500); } catch (InterruptedException e) { break; }
                }
            }).start();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (isVideo) {
                        videoView.seekTo(progress);
                    } else if (mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);
                    }
                    textCurrentTime.setText(formatTime(progress));
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startSeekBar() {
        new Thread(() -> {
            while (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        runOnUiThread(() -> {
                            int current = mediaPlayer.getCurrentPosition();
                            int total = mediaPlayer.getDuration();

                            seekBar.setMax(total);
                            seekBar.setProgress(current);

                            textCurrentTime.setText(formatTime(current));
                            textTotalTime.setText(formatTime(total));
                        });
                    }
                    Thread.sleep(500);
                } catch (Exception e) {
                    break;
                }
            }
        }).start();
    }

    private void downloadFile(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationInExternalFilesDir(
                    this,
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
            );

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            long id = dm.enqueue(request);

            new Thread(() -> {
                boolean downloading = true;

                while (downloading) {
                    Cursor c = dm.query(new DownloadManager.Query().setFilterById(id));

                    if (c.moveToFirst()) {
                        int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;

                            Uri fileUri = dm.getUriForDownloadedFile(id);

                            runOnUiThread(() -> {
                                selectedUri = fileUri;
                                textPlaceholder.setVisibility(View.GONE);

                                btnPlay.setEnabled(true);
                                btnPause.setEnabled(true);
                                btnStop.setEnabled(true);

                                updateButtonsColor(btnPlay, btnPause, btnStop, null);

                                String path = fileName.toLowerCase();

                                if (path.endsWith(".mp4") || path.endsWith(".3gp") || path.endsWith(".mkv")) {
                                    isVideo = true;

                                    videoView.setVisibility(View.VISIBLE);
                                    audioLayout.setVisibility(View.GONE);
                                    btnFullscreen.setVisibility(View.VISIBLE);

                                    videoView.setVideoURI(selectedUri);

                                    textCurrentTime.setText("00:00");
                                    videoView.setOnPreparedListener(mp -> {
                                        textTotalTime.setText(formatTime(videoView.getDuration()));
                                    });

                                } else {
                                    isVideo = false;

                                    videoView.setVisibility(View.GONE);
                                    audioLayout.setVisibility(View.VISIBLE);
                                    btnFullscreen.setVisibility(View.GONE);

                                    if (mediaPlayer != null) mediaPlayer.release();
                                    mediaPlayer = MediaPlayer.create(MainActivity.this, selectedUri);

                                    seekBar.setProgress(0);
                                    textCurrentTime.setText("00:00");
                                    textTotalTime.setText(formatTime(mediaPlayer.getDuration()));

                                    startSeekBar();
                                }

                                textTitle.setText(fileName);
                                Toast.makeText(MainActivity.this, "Downloaded!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                    c.close();
                }
            }).start();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateButtonsColor(Button play, Button pause, Button stop, String active) {
        play.setBackgroundColor(Color.LTGRAY);
        pause.setBackgroundColor(Color.LTGRAY);
        stop.setBackgroundColor(Color.LTGRAY);

        if (active == null) return;

        switch (active) {
            case "play":
                play.setBackgroundColor(Color.GRAY);
                break;
            case "pause":
                pause.setBackgroundColor(Color.GRAY);
                break;
            case "stop":
                stop.setBackgroundColor(Color.GRAY);
                break;
        }
    }

    private String formatTime(int milliseconds) {
        int totalSeconds = milliseconds / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void startVideoTimer() {
        new Thread(() -> {
            while (videoView != null && videoView.isPlaying()) {
                try {
                    int currentPos = videoView.getCurrentPosition();
                    runOnUiThread(() -> {
                        seekBar.setMax(videoView.getDuration());
                        seekBar.setProgress(currentPos);
                        textCurrentTime.setText(formatTime(currentPos));
                    });
                    Thread.sleep(500);
                } catch (Exception e) {
                    break;
                }
            }
        }).start();
    }
}