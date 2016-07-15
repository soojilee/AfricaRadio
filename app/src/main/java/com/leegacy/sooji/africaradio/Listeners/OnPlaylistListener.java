package com.leegacy.sooji.africaradio.Listeners;

import com.leegacy.sooji.africaradio.Models.PlaylistRowModel;

/**
 * Created by soo-ji on 16-06-23.
 */
public interface OnPlaylistListener {

    void playRequested();
    void initAudioRequested(String audioKey);
    void setProfileFragmentListener(OnProfileFragmentListener v);
    void pauseRequested();
    void addPlayDetailFragment(PlaylistRowModel model);
}
