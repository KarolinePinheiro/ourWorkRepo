# Support Links
https://chatgpt.com/share/69e1f165-1088-83eb-99f0-9a4e4eaf2708

Foundations of User Experience (UX) Design - https://www.coursera.org/learn/foundations-user-experience-design/

# Layout creation sequence
New Project -> Empty Views Activity -> Language -> Java - Finish
Leave all other fields untouched

# For git commit -> git push -> RPC errors
https://chatgpt.com/share/69e1f1bb-4c4c-83eb-ac8e-9e537744a8b3

- Open terminal and run:
git config --global http.version HTTP/1.1
git config --global http.postBuffer 524288000
git push


## in the Code

- Music starts immediatly after app launch

Solved with:
loadSong(currentSongIndex, false);

        // Removed automatic playback
        // The song is now only prepared
        // mediaPlayer.start();   <-- REMOVED
        // handler.post(updateSeekBar); <-- REMOVED

- Prevent pressing Play twice

Solved with:
if (mediaPlayer != null && !mediaPlayer.isPlaying())



- Dynamic music track list
We have moved from a static - hardcoded list to a dynamic list

private int[] songs = {
        R.raw.song1,
        R.raw.song2,
        R.raw.song3
};

private int currentSongIndex = 0;

Solved with :
private int[] loadSongsFromRaw() {

    java.lang.reflect.Field[] fields = R.raw.class.getFields();

    int[] tempSongs = new int[fields.length];

    for (int i = 0; i < fields.length; i++) {

        try {
            tempSongs[i] = fields[i].getInt(null);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    return tempSongs;
}

and after sort songs alphabetically updating the function as:


private int[] loadSongsFromRaw() {

    java.lang.reflect.Field[] fields = R.raw.class.getFields();

    java.util.Arrays.sort(fields, (a, b) ->
            a.getName().compareTo(b.getName())
    );

    int[] tempSongs = new int[fields.length];

    for (int i = 0; i < fields.length; i++) {

        try {
            tempSongs[i] = fields[i].getInt(null);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    return tempSongs;
}

- Display Song Name Dynamically

Solved with:
private String getSongName(int index) {

    Field[] fields = R.raw.class.getFields();

    Arrays.sort(fields,
            (a, b) ->
                    a.getName().compareTo(b.getName())
    );

    return fields[index].getName();
}

- Issues with music labels

Solved with:
lowercase letters (a–z)
numbers (0–9)
underscore (_)

- Issues in Tilt operation sensivity

Solved with:
private static final float TILT_THRESHOLD = 6.0f;

4.0  very sensitive
6.0  balanced (default)
8.0  less sensitive

- Issues in Shuffle (shake) operation sensivity and stability

Solved with:
private static final float SHAKE_THRESHOLD = 12.0f;

10.0  sensitive
12.0  balanced
15.0  less sensitive


// =========================
// IMPROVED SHAKE DETECTION
// =========================

private static final float SHAKE_THRESHOLD = 2.7f;

private static final int SHAKE_SLOP_TIME_MS = 500;

private static final int SHAKE_RESET_TIME_MS = 3000;

private long lastShakeTimestamp = 0;

private float lastX;
private float lastY;
private float lastZ;

private boolean shakeInitialized = false;

See also full function, lines 388 -> 435

This implementation uses:
Delta acceleration
Event gating
Noise filtering
Single-trigger logic

- Small error in sound volume (coordinates)

Solved with:
if (y < -VOLUME_TILT_THRESHOLD) {

    audioManager.adjustVolume(
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
    );

    lastVolumeTime = nowVolume;
}
else if (y > VOLUME_TILT_THRESHOLD) {

    audioManager.adjustVolume(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
    );

    lastVolumeTime = nowVolume;
}

replaced with:

if (y > VOLUME_TILT_THRESHOLD) {

    audioManager.adjustVolume(
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
    );

    lastVolumeTime = nowVolume;
}

// Tilt TOP AWAY → Volume DOWN
else if (y < -VOLUME_TILT_THRESHOLD) {

    audioManager.adjustVolume(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
    );

    lastVolumeTime = nowVolume;
}