package com.leegacy.sooji.africaradio.Listeners;

/**
 * Created by soo-ji on 16-06-23.
 */
public interface OnPlaylistListener {

    void playRequested();
    void initAudioRequested(String audioKey);
    void pauseRequested();
    void addPlayDetailFragment();
}
