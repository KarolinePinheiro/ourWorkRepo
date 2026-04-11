package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // MediaPlayer instance
    private MediaPlayer mediaPlayer;

    // Playlist
    private int[] songs = {
            R.raw.song1,
            R.raw.song2,
            R.raw.song3,
            R.raw.song4
    };

    private int currentSongIndex = 0;

    // UI
    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textTotalTime;

    private ImageView buttonPlay;
    private ImageView buttonPause;
    private ImageView buttonStop;
    private ImageView buttonNext;
    private ImageView buttonPrevious;

    // Handler for SeekBar updates
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

        // Init UI
        seekBar = findViewById(R.id.seekBar);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPause = findViewById(R.id.buttonPause);
        buttonStop = findViewById(R.id.buttonStop);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);

        // FIX #1:
        // App starts with NO music playing
        loadSong(currentSongIndex, false);

        // PLAY
        buttonPlay.setOnClickListener(v -> {

            // FIX #2:
            // Prevent double play
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {

                mediaPlayer.start();
                handler.post(updateSeekBar);
            }
        });

        // PAUSE
        buttonPause.setOnClickListener(v -> {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        // STOP
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
                textTotalTime.setText(
                        formatTime(mediaPlayer.getDuration())
                );
            }
        });

        // NEXT
        buttonNext.setOnClickListener(v -> {

            currentSongIndex++;

            if (currentSongIndex >= songs.length) {
                currentSongIndex = 0;
            }

            // FIX #3:
            // Next song auto-plays immediately
            loadSong(currentSongIndex, true);
        });

        // PREVIOUS
        buttonPrevious.setOnClickListener(v -> {

            currentSongIndex--;

            if (currentSongIndex < 0) {
                currentSongIndex = songs.length - 1;
            }

            // FIX #3:
            // Previous song auto-plays immediately
            loadSong(currentSongIndex, true);
        });

        // SeekBar
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser && mediaPlayer != null) {

                            mediaPlayer.seekTo(progress);

                            textCurrentTime.setText(
                                    formatTime(progress)
                            );
                        }
                    }

                    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
                });
    }

    // =========================
    // CORE LOADING FUNCTION
    // =========================

    private void loadSong(int index, boolean autoPlay) {

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(
                this,
                songs[index]
        );

        // Reset UI
        seekBar.setProgress(0);
        textCurrentTime.setText("0:00");

        seekBar.setMax(mediaPlayer.getDuration());
        textTotalTime.setText(
                formatTime(mediaPlayer.getDuration())
        );

        // FIX #4:
        // Auto-play controlled externally (Next/Previous = true, startup = false)
        if (autoPlay) {

            mediaPlayer.start();
            handler.post(updateSeekBar);
        }

        mediaPlayer.setOnCompletionListener(
                mp -> playNextSongAuto()
        );
    }

    // Auto-next when song ends
    private void playNextSongAuto() {

        currentSongIndex++;

        if (currentSongIndex >= songs.length) {
            currentSongIndex = 0;
        }

        loadSong(currentSongIndex, true);
    }

    // Time formatting
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