package com.leegacy.sooji.africaradio.Listeners;

/**
 * Created by soo-ji on 16-07-07.
 */
public interface OnHomeRowListener {
    void playRequested();
    void initAudioRequested(String audioKey);
    void setHomeFragmentListener(OnHomeFragmentListener v);
    void pauseRequested();

}
