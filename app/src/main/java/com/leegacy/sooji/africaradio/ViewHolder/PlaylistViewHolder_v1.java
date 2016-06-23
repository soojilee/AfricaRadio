package com.leegacy.sooji.africaradio.ViewHolder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.leegacy.sooji.africaradio.Activities.PlayDetailActivity;
import com.leegacy.sooji.africaradio.Listeners.OnPlaylistListener;
import com.leegacy.sooji.africaradio.Models.PlaylistRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by soo-ji on 16-04-23.
 */
public class PlaylistViewHolder_v1 extends RowViewHolder implements View.OnClickListener {
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

    public PlaylistViewHolder_v1(final View itemView) {
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

    protected void decodeStringtoFile(String audioFile) {
        byte[] decoded = Base64.decode(audioFile, Base64.DEFAULT);
        File file = new File(Environment.getExternalStorageDirectory() + "/audio.3gp");
        if (file.exists()) {
            file.delete();
            file = null;
        }
        try {
            FileOutputStream os = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/audio.3gp"), true);
            os.write(decoded);
            os.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(itemView.getContext(), "file not decoded", Toast.LENGTH_SHORT);

        }
        Toast.makeText(itemView.getContext(), "file not decoded", Toast.LENGTH_SHORT);

    }

    protected void setupAudio(){
        //myMediaPlayer = MediaPlayer.create(this, outputFile);

        myMediaPlayer = new MediaPlayer();
        ref.child("audioFile").child(audioFile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("audioFile fetched" + dataSnapshot.getValue());
                decodeStringtoFile(dataSnapshot.getValue(String.class));
                try {
                    myMediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/audio.3gp");
                    myMediaPlayer.prepare();
                    myMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(itemView.getContext(), "fetching audio data failed1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(itemView.getContext(), "fetching audio data failed", Toast.LENGTH_SHORT).show();
            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pauseIcon.setEnabled(false);
                pauseIcon.setVisibility(View.INVISIBLE);
                playIcon.setEnabled(true);
                playIcon.setVisibility(View.VISIBLE);
                first = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playView:
                if (first) {

                    setupAudio();

                }else {

                    myMediaPlayer.start();
                }
                playIcon.setEnabled(false);
                playIcon.setVisibility(View.INVISIBLE);
                pauseIcon.setEnabled(true);
                pauseIcon.setVisibility(View.VISIBLE);

                break;
            case R.id.pauseView:
                myMediaPlayer.pause();
                first = false;
                playIcon.setEnabled(true);
                playIcon.setVisibility(View.VISIBLE);
                pauseIcon.setEnabled(false);
                pauseIcon.setVisibility(View.INVISIBLE);
                break;
            case View.NO_ID:
                Intent intent = new Intent(itemView.getContext(), PlayDetailActivity.class);
                intent.putExtra("playlistKey", model.getAudioFile());
                itemView.getContext().startActivity(intent);
        }
    }

    public void setOnPlaylistListener(OnPlaylistListener onPlaylistListener) {
        this.onPlaylistListener = onPlaylistListener;
    }
}
