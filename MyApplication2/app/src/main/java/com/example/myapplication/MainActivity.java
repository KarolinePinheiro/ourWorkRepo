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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private MediaPlayer mediaPlayer;

    private int[] songs;

    private int currentSongIndex = 0;

    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textTotalTime;
    private TextView textSongName;

    private ImageView buttonPlay;
    private ImageView buttonPause;
    private ImageView buttonStop;
    private ImageView buttonNext;
    private ImageView buttonPrevious;
    private ImageView buttonShuffle;

    private final Random random = new Random();

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Tilt configuration

    private static final float TILT_THRESHOLD = 6.0f;

    private static final long TILT_COOLDOWN_MS = 1500;

    private long lastTiltTime = 0;

    // =========================
    // ADD — Stable Shake Fields
    // =========================

    private static final float SHAKE_THRESHOLD = 2.7f;

    private static final int SHAKE_SLOP_TIME_MS = 500;

    private static final int SHAKE_RESET_TIME_MS = 3000;

    private long lastShakeTimestamp = 0;

    private float lastX;
    private float lastY;
    private float lastZ;

    private boolean shakeInitialized = false;

    private final Runnable updateSeekBar =
            new Runnable() {

                @Override
                public void run() {

                    if (mediaPlayer != null
                            && mediaPlayer.isPlaying()) {

                        seekBar.setProgress(
                                mediaPlayer
                                        .getCurrentPosition()
                        );

                        textCurrentTime.setText(
                                formatTime(
                                        mediaPlayer
                                                .getCurrentPosition()
                                )
                        );

                        handler.postDelayed(
                                this,
                                1000
                        );
                    }
                }
            };

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        seekBar =
                findViewById(R.id.seekBar);

        textCurrentTime =
                findViewById(
                        R.id.textCurrentTime
                );

        textTotalTime =
                findViewById(
                        R.id.textTotalTime
                );

        textSongName =
                findViewById(
                        R.id.textSongName
                );

        buttonPlay =
                findViewById(
                        R.id.buttonPlay
                );

        buttonPause =
                findViewById(
                        R.id.buttonPause
                );

        buttonStop =
                findViewById(
                        R.id.buttonStop
                );

        buttonNext =
                findViewById(
                        R.id.buttonNext
                );

        buttonPrevious =
                findViewById(
                        R.id.buttonPrevious
                );

        buttonShuffle =
                findViewById(
                        R.id.buttonShuffle
                );

        sensorManager =
                (SensorManager)
                        getSystemService(
                                SENSOR_SERVICE
                        );

        if (sensorManager != null) {

            accelerometer =
                    sensorManager
                            .getDefaultSensor(
                                    Sensor.TYPE_ACCELEROMETER
                            );
        }

        songs = loadSongsFromRaw();

        if (songs.length == 0) {
            return;
        }

        loadSong(currentSongIndex, true);

        buttonPlay.setOnClickListener(v -> {

            if (mediaPlayer != null
                    && !mediaPlayer.isPlaying()) {

                mediaPlayer.start();

                handler.post(
                        updateSeekBar
                );
            }
        });

        buttonPause.setOnClickListener(v -> {

            if (mediaPlayer != null
                    && mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
            }
        });

        buttonStop.setOnClickListener(v -> {

            if (mediaPlayer != null) {

                mediaPlayer.stop();

                mediaPlayer.release();

                mediaPlayer =
                        MediaPlayer.create(
                                this,
                                songs[
                                        currentSongIndex
                                        ]
                        );

                seekBar.setProgress(0);

                textCurrentTime.setText(
                        "0:00"
                );

                seekBar.setMax(
                        mediaPlayer
                                .getDuration()
                );

                textTotalTime.setText(
                        formatTime(
                                mediaPlayer
                                        .getDuration()
                        )
                );
            }
        });

        buttonNext.setOnClickListener(v -> {

            currentSongIndex++;

            if (currentSongIndex
                    >= songs.length) {

                currentSongIndex = 0;
            }

            loadSong(
                    currentSongIndex,
                    true
            );
        });

        buttonPrevious.setOnClickListener(v -> {

            currentSongIndex--;

            if (currentSongIndex < 0) {

                currentSongIndex =
                        songs.length - 1;
            }

            loadSong(
                    currentSongIndex,
                    true
            );
        });

        buttonShuffle.setOnClickListener(v -> {

            int newIndex;

            do {

                newIndex =
                        random.nextInt(
                                songs.length
                        );

            } while (
                    newIndex
                            == currentSongIndex
                            && songs.length > 1
            );

            currentSongIndex = newIndex;

            loadSong(
                    currentSongIndex,
                    true
            );
        });

        seekBar.setOnSeekBarChangeListener(
                new SeekBar
                        .OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser
                                && mediaPlayer != null) {

                            mediaPlayer.seekTo(
                                    progress
                            );

                            textCurrentTime.setText(
                                    formatTime(
                                            progress
                                    )
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

    @Override
    protected void onResume() {

        super.onResume();

        if (sensorManager != null
                && accelerometer != null) {

            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    public void onSensorChanged(
            SensorEvent event) {

        if (event.sensor.getType()
                != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = event.values[0];

        long currentTime =
                System.currentTimeMillis();

        if (currentTime - lastTiltTime
                < TILT_COOLDOWN_MS) {
            return;
        }

        // Tilt RIGHT → Next

        if (x < -TILT_THRESHOLD) {

            lastTiltTime = currentTime;

            currentSongIndex++;

            if (currentSongIndex
                    >= songs.length) {

                currentSongIndex = 0;
            }

            loadSong(
                    currentSongIndex,
                    true
            );
        }

        // Tilt LEFT → Previous

        else if (x > TILT_THRESHOLD) {

            lastTiltTime = currentTime;

            currentSongIndex--;

            if (currentSongIndex < 0) {

                currentSongIndex =
                        songs.length - 1;
            }

            loadSong(
                    currentSongIndex,
                    true
            );
        }

        // =========================
        // NEW — Stable Shake Logic
        // =========================

        float y = event.values[1];
        float z = event.values[2];

        if (!shakeInitialized) {

            lastX = x;
            lastY = y;
            lastZ = z;

            shakeInitialized = true;

            return;
        }

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;

        float acceleration =
                Math.abs(deltaX)
                        + Math.abs(deltaY)
                        + Math.abs(deltaZ);

        long now =
                System.currentTimeMillis();

        if (acceleration
                > SHAKE_THRESHOLD) {

            if (lastShakeTimestamp
                    + SHAKE_SLOP_TIME_MS
                    > now) {
                return;
            }

            if (lastShakeTimestamp
                    + SHAKE_RESET_TIME_MS
                    < now) {

                lastShakeTimestamp = 0;
            }

            lastShakeTimestamp = now;

            int newIndex;

            do {

                newIndex =
                        random.nextInt(
                                songs.length
                        );

            } while (
                    newIndex
                            == currentSongIndex
                            && songs.length > 1
            );

            currentSongIndex = newIndex;

            loadSong(
                    currentSongIndex,
                    true
            );
        }
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy) {
    }

    private int[] loadSongsFromRaw() {

        Field[] fields =
                R.raw.class.getFields();

        Arrays.sort(
                fields,
                (a, b) ->
                        a.getName()
                                .compareTo(
                                        b.getName()
                                )
        );

        int[] tempSongs =
                new int[fields.length];

        for (int i = 0;
             i < fields.length;
             i++) {

            try {

                tempSongs[i] =
                        fields[i]
                                .getInt(null);

            } catch (
                    IllegalAccessException e) {

                e.printStackTrace();
            }
        }

        return tempSongs;
    }

    private void loadSong(
            int index,
            boolean autoPlay) {

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer =
                MediaPlayer.create(
                        this,
                        songs[index]
                );

        seekBar.setProgress(0);

        textCurrentTime.setText("0:00");

        seekBar.setMax(
                mediaPlayer.getDuration()
        );

        textTotalTime.setText(
                formatTime(
                        mediaPlayer
                                .getDuration()
                )
        );

        textSongName.setText(
                getSongName(index)
        );

        if (autoPlay) {

            mediaPlayer.start();

            handler.post(
                    updateSeekBar
            );
        }

        mediaPlayer.setOnCompletionListener(
                mp ->
                        playNextSongAuto()
        );
    }

    private String getSongName(
            int index) {

        try {

            Field[] fields =
                    R.raw.class.getFields();

            Arrays.sort(
                    fields,
                    (a, b) ->
                            a.getName()
                                    .compareTo(
                                            b.getName()
                                    )
            );

            String rawName =
                    fields[index]
                            .getName();

            return rawName.replace(
                    "_",
                    " "
            );
        }

        catch (Exception e) {

            return "Unknown song";
        }
    }

    private void playNextSongAuto() {

        currentSongIndex++;

        if (currentSongIndex
                >= songs.length) {

            currentSongIndex = 0;
        }

        loadSong(
                currentSongIndex,
                true
        );
    }

    private String formatTime(
            int milliseconds) {

        long minutes =
                TimeUnit.MILLISECONDS
                        .toMinutes(
                                milliseconds
                        );

        long seconds =
                TimeUnit.MILLISECONDS
                        .toSeconds(
                                milliseconds
                        )
                        % 60;

        return String.format(
                "%d:%02d",
                minutes,
                seconds
        );
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacks(
                updateSeekBar
        );

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (sensorManager != null) {
            sensorManager.unregisterListener(
                    this
            );
        }
    }
}