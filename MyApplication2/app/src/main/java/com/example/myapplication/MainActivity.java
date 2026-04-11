package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    // =========================
    // CHANGE #1: NOW 4 TRACKS
    // =========================
    private int[] songs = {
            R.raw.song1,
            R.raw.song2,
            R.raw.song3,
            R.raw.song4
    };

    private int currentSongIndex = 0;

    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textTotalTime;

    private ImageView buttonPlay;
    private ImageView buttonPause;
    private ImageView buttonStop;
    private ImageView buttonNext;
    private ImageView buttonPrevious;

    // =========================
    // CHANGE #2: SHUFFLE BUTTON
    // =========================
    private ImageView buttonShuffle;

    private final Random random = new Random();

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                seekBar.setProgress(mediaPlayer.getCurrentPosition());

                textCurrentTime.setText(
                        formatTime(mediaPlayer.getCurrentPosition())
                );

                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPause = findViewById(R.id.buttonPause);
        buttonStop = findViewById(R.id.buttonStop);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);

        // CHANGE #3: NEW SHUFFLE BUTTON
        buttonShuffle = findViewById(R.id.buttonShuffle);

        // App starts WITHOUT auto-play
        loadSong(currentSongIndex, false);

        // =========================
        // PLAY
        // =========================
        buttonPlay.setOnClickListener(v -> {

            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                handler.post(updateSeekBar);
            }
        });

        // =========================
        // PAUSE
        // =========================
        buttonPause.setOnClickListener(v -> {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        // =========================
        // STOP
        // =========================
        buttonStop.setOnClickListener(v -> {

            if (mediaPlayer != null) {

                mediaPlayer.stop();
                mediaPlayer.release();

                mediaPlayer = MediaPlayer.create(
                        this,
                        songs[currentSongIndex]
                );

                seekBar.setProgress(0);
                textCurrentTime.setText("0:00");

                seekBar.setMax(mediaPlayer.getDuration());
                textTotalTime.setText(formatTime(mediaPlayer.getDuration()));
            }
        });

        // =========================
        // NEXT (auto-play)
        // =========================
        buttonNext.setOnClickListener(v -> {

            currentSongIndex++;

            if (currentSongIndex >= songs.length) {
                currentSongIndex = 0;
            }

            loadSong(currentSongIndex, true); // CHANGE: auto-play enabled
        });

        // =========================
        // PREVIOUS (auto-play)
        // =========================
        buttonPrevious.setOnClickListener(v -> {

            currentSongIndex--;

            if (currentSongIndex < 0) {
                currentSongIndex = songs.length - 1;
            }

            loadSong(currentSongIndex, true); // CHANGE: auto-play enabled
        });

        // =========================
        // CHANGE #4: SHUFFLE LOGIC (4 TRACKS SAFE)
        // =========================
        buttonShuffle.setOnClickListener(v -> {

            int newIndex;

            // ensures we do not repeat same song immediately
            do {
                newIndex = random.nextInt(songs.length);
            } while (newIndex == currentSongIndex && songs.length > 1);

            currentSongIndex = newIndex;

            // CHANGE: shuffle ALWAYS auto-plays
            loadSong(currentSongIndex, true);
        });

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser && mediaPlayer != null) {
                            mediaPlayer.seekTo(progress);
                            textCurrentTime.setText(formatTime(progress));
                        }
                    }

                    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
                });
    }

    // =========================
    // CORE LOAD FUNCTION
    // =========================
    private void loadSong(int index, boolean autoPlay) {

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, songs[index]);

        seekBar.setProgress(0);
        textCurrentTime.setText("0:00");

        seekBar.setMax(mediaPlayer.getDuration());
        textTotalTime.setText(formatTime(mediaPlayer.getDuration()));

        // CHANGE: controlled playback behavior
        if (autoPlay) {
            mediaPlayer.start();
            handler.post(updateSeekBar);
        }

        mediaPlayer.setOnCompletionListener(mp -> playNextSongAuto());
    }

    // AUTO NEXT
    private void playNextSongAuto() {

        currentSongIndex++;

        if (currentSongIndex >= songs.length) {
            currentSongIndex = 0;
        }

        loadSong(currentSongIndex, true);
    }

    // TIME FORMATTER
    private String formatTime(int milliseconds) {

        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacks(updateSeekBar);

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}