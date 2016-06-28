package com.leegacy.sooji.africaradio.ViewHolder;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.leegacy.sooji.africaradio.Listeners.OnPlaylistListener;
import com.leegacy.sooji.africaradio.Models.PlaylistRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-04-23.
 */
public class PlaylistViewHolder extends RowViewHolder implements View.OnClickListener {
    private final ImageView playIcon;
    private final ImageView heartIcon;
    private final TextView heartsCount;
    private final ImageView commentIcon;
    private final TextView commentsCount;
    private final TextView titlePlaylist;
    private final ImageView pauseIcon;
    private final Firebase ref;
    private OnPlaylistListener onPlaylistListener;


    private PlaylistRowModel model;
    private MediaPlayer myMediaPlayer;
    private boolean first = true;
    private String audioFile;

    public PlaylistViewHolder(final View itemView) {
        super(itemView);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");

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
                if(first){
                    onPlaylistListener.initAudioRequested(audioFile);
                }
                onPlaylistListener.playRequested();
                playIcon.setEnabled(true);
                playIcon.setVisibility(View.VISIBLE);
                pauseIcon.setEnabled(false);
                pauseIcon.setVisibility(View.INVISIBLE);
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
                onPlaylistListener.addPlayDetailFragment();

//                Intent intent = new Intent(itemView.getContext(), PlayDetailActivity.class);
//                intent.putExtra("playlistKey", model.getAudioFile());
//                itemView.getContext().startActivity(intent);
        }
    }

    public void setOnPlaylistListener(OnPlaylistListener onPlaylistListener) {
        this.onPlaylistListener = onPlaylistListener;
    }
}
