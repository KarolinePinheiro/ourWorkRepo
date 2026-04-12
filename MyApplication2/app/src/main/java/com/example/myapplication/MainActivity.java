package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private int[] songs;

    private int currentSongIndex = 0;

    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textTotalTime;

    // =========================
    // ADD #1:
    // TextView to display
    // the current song name
    // =========================
    private TextView textSongName;

    private ImageView buttonPlay;
    private ImageView buttonPause;
    private ImageView buttonStop;
    private ImageView buttonNext;
    private ImageView buttonPrevious;
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

        // =========================
        // ADD #2:
        // Bind song name TextView
        // =========================
        textSongName = findViewById(R.id.textSongName);

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPause = findViewById(R.id.buttonPause);
        buttonStop = findViewById(R.id.buttonStop);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonShuffle = findViewById(R.id.buttonShuffle);

        songs = loadSongsFromRaw();

        if (songs.length == 0) {
            return;
        }

        loadSong(currentSongIndex, false);

        buttonPlay.setOnClickListener(v -> {

            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                handler.post(updateSeekBar);
            }
        });

        buttonPause.setOnClickListener(v -> {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

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

        buttonNext.setOnClickListener(v -> {

            currentSongIndex++;

            if (currentSongIndex >= songs.length) {
                currentSongIndex = 0;
            }

            loadSong(currentSongIndex, true);
        });

        buttonPrevious.setOnClickListener(v -> {

            currentSongIndex--;

            if (currentSongIndex < 0) {
                currentSongIndex = songs.length - 1;
            }

            loadSong(currentSongIndex, true);
        });

        buttonShuffle.setOnClickListener(v -> {

            int newIndex;

            do {
                newIndex = random.nextInt(songs.length);
            }
            while (newIndex == currentSongIndex
                    && songs.length > 1);

            currentSongIndex = newIndex;

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

                            textCurrentTime.setText(
                                    formatTime(progress)
                            );
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {
                    }
                });
    }

    private int[] loadSongsFromRaw() {

        Field[] fields = R.raw.class.getFields();

        Arrays.sort(fields,
                (a, b) ->
                        a.getName().compareTo(b.getName())
        );

        int[] tempSongs = new int[fields.length];

        for (int i = 0; i < fields.length; i++) {

            try {
                tempSongs[i] = fields[i].getInt(null);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return tempSongs;
    }

    private void loadSong(int index, boolean autoPlay) {

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(
                this,
                songs[index]
        );

        seekBar.setProgress(0);
        textCurrentTime.setText("0:00");

        seekBar.setMax(mediaPlayer.getDuration());

        textTotalTime.setText(
                formatTime(mediaPlayer.getDuration())
        );

        // =========================
        // ADD #4:
        // Update song name display
        // =========================
        textSongName.setText(
                getSongName(index)
        );

        if (autoPlay) {

            mediaPlayer.start();

            handler.post(updateSeekBar);
        }

        mediaPlayer.setOnCompletionListener(
                mp -> playNextSongAuto()
        );
    }

    // =========================
    // ADD #3:
    // Extract song name dynamically
    // from res/raw resource name
    // =========================
    private String getSongName(int index) {

        try {

            Field[] fields = R.raw.class.getFields();

            Arrays.sort(fields,
                    (a, b) ->
                            a.getName().compareTo(b.getName())
            );

            String rawName =
                    fields[index].getName();

            // Convert:
            // song_name_example
            // ->
            // song name example

            return rawName.replace("_", " ");

        }
        catch (Exception e) {

            return "Unknown song";
        }
    }

    private void playNextSongAuto() {

        currentSongIndex++;

        if (currentSongIndex >= songs.length) {
            currentSongIndex = 0;
        }

        loadSong(currentSongIndex, true);
    }

    private String formatTime(int milliseconds) {

        long minutes =
                TimeUnit.MILLISECONDS.toMinutes(milliseconds);

        long seconds =
                TimeUnit.MILLISECONDS
                        .toSeconds(milliseconds) % 60;

        return String.format("%d:%02d",
                minutes,
                seconds);
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