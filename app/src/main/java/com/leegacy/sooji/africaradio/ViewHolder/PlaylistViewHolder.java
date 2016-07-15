package com.leegacy.sooji.africaradio.ViewHolder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.leegacy.sooji.africaradio.Activities.PlayDetailActivity;
import com.leegacy.sooji.africaradio.Listeners.OnPlaylistListener;
import com.leegacy.sooji.africaradio.Listeners.OnProfileFragmentListener;
import com.leegacy.sooji.africaradio.Models.PlaylistRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-04-23.
 */
public class PlaylistViewHolder extends RowViewHolder implements View.OnClickListener, OnProfileFragmentListener {
    private ImageView playIcon;
    private ImageView heartIcon;
    private TextView heartsCount;
    private ImageView commentIcon;
    private TextView commentsCount;
    private TextView titlePlaylist;
    private ImageView pauseIcon;
    private Firebase ref;
    private OnPlaylistListener onPlaylistListener;


    private PlaylistRowModel model;
    private MediaPlayer myMediaPlayer;
    private boolean first = true;
    private String audioFile;

    public PlaylistViewHolder(final View itemView) {
        super(itemView);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");

        init();

    }

    private void init(){
        playIcon = (ImageView) itemView.findViewById(R.id.playView);
        pauseIcon = (ImageView) itemView.findViewById(R.id.pauseView);
        pauseIcon.setEnabled(false);
        heartIcon = (ImageView) itemView.findViewById(R.id.heartIcon);
        heartsCount = (TextView) itemView.findViewById(R.id.heartsCount);
        commentIcon = (ImageView) itemView.findViewById(R.id.commentIcon);
        commentsCount = (TextView) itemView.findViewById(R.id.commentsCount);
        titlePlaylist = (TextView) itemView.findViewById(R.id.titlePlaylist);



        playIcon.setOnClickListener(this);
        pauseIcon.setOnClickListener(this);
        pauseIcon.setEnabled(false);
        pauseIcon.setVisibility(View.INVISIBLE);
        itemView.setId(View.NO_ID);
        itemView.setOnClickListener(this);
    }

    @Override
    public void update(RowModel rowModel) {
        model = (PlaylistRowModel) rowModel;
        titlePlaylist.setText(model.getTitle());
        heartsCount.setText(model.getNumHearts() + "");
        commentsCount.setText(model.getNumComments() + "");
        audioFile = model.getAudioFile(); //reference

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playView:
                playIcon.setEnabled(true);
                playIcon.setVisibility(View.INVISIBLE);
                pauseIcon.setEnabled(false);
                pauseIcon.setVisibility(View.VISIBLE);
                if(first){
                    onPlaylistListener.setProfileFragmentListener(this);
                    onPlaylistListener.initAudioRequested(audioFile);
                }else {
                    onPlaylistListener.playRequested();
                }

                break;
            case R.id.pauseView:
                first = false;
                onPlaylistListener.pauseRequested();
                playIcon.setEnabled(true);
                playIcon.setVisibility(View.VISIBLE);
                pauseIcon.setEnabled(false);
                pauseIcon.setVisibility(View.INVISIBLE);
                break;
            case View.NO_ID:
//                onPlaylistListener.addPlayDetailFragment(model);

                Intent intent = new Intent(itemView.getContext(), PlayDetailActivity.class);
                intent.putExtra("playlistKey", model.getAudioFile());
                itemView.getContext().startActivity(intent);
        }
    }

    public void setOnPlaylistListener(OnPlaylistListener onPlaylistListener) {
        this.onPlaylistListener = onPlaylistListener;
    }

    @Override
    public void playFinished() {
        pauseIcon.setEnabled(false);
        pauseIcon.setVisibility(View.INVISIBLE);
        playIcon.setEnabled(true);
        playIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void playing() {
        pauseIcon.setEnabled(true);
        pauseIcon.setVisibility(View.VISIBLE);
        playIcon.setEnabled(false);
        playIcon.setVisibility(View.INVISIBLE);
    }


}
