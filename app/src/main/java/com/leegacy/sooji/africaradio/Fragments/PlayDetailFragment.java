package com.leegacy.sooji.africaradio.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.leegacy.sooji.africaradio.Listeners.OnPlayDetailListener;
import com.leegacy.sooji.africaradio.Models.PlaylistRowModel;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by soo-ji on 16-06-23.
 */
public class PlayDetailFragment extends Fragment implements View.OnClickListener {
    private View root;
    private TextView backButton;
    private OnPlayDetailListener onPlayDetailListener;
    private PlaylistRowModel model;
    private TextView title;
    private TextView description;
    private ImageView playButton;
    private ImageView pauseButton;
    private SeekBar seekBar;
    private boolean first = true;
    private MediaPlayer myMediaPlayer;
    private String audioFile;
    private Firebase ref;
    private Handler seekHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_playdetail, null);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        audioFile = model.getAudioFile();
//        backButton = (TextView) root.findViewById(R.id.backutton);
//        backButton.setOnClickListener(this);
        title = (TextView) root.findViewById(R.id.titlePlayDetail);
        title.setText(model.getTitle());
        description = (TextView) root.findViewById(R.id.descriptionPlayDetail);
        description.setText(model.getDescription());
        playButton = (ImageView) root.findViewById(R.id.playButtonPlayDetail);
        pauseButton = (ImageView) root.findViewById(R.id.pauseButtonPlayDetail);
        seekBar = (SeekBar) root.findViewById(R.id.seekbarPlayDetail);
        seekHandler = new Handler();
        return root;
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
            Toast.makeText(getActivity(), "file not decoded", Toast.LENGTH_SHORT);
        }
        Toast.makeText(getActivity(), "file not decoded", Toast.LENGTH_SHORT);

    }

    protected void setupAudio() {
        //myMediaPlayer = MediaPlayer.create(this, outputFile);

        myMediaPlayer = new MediaPlayer();

        ref.child("audioFile").child(audioFile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e(TAG,"audioFile fetched" + dataSnapshot.getValue());
                decodeStringtoFile(dataSnapshot.getValue(String.class));
                try {
                    myMediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/audio.3gp");
                    myMediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "fetching audio data failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getActivity(), "fetching audio data failed", Toast.LENGTH_SHORT).show();
            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pauseButton.setClickable(false);
                pauseButton.setVisibility(View.INVISIBLE);
                playButton.setClickable(true);
                playButton.setVisibility(View.VISIBLE);
                first = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.backButton:
//                onPlayDetailListener.removeFragment();
//                break;
            case R.id.playButtonPlayDetail:
                if (first) {

                    setupAudio();

                }

                seekBar.setMax(myMediaPlayer.getDuration());
                seekUpdation();
                myMediaPlayer.start();

                playButton.setClickable(false);
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setClickable(true);
                pauseButton.setVisibility(View.VISIBLE);

                break;
            case R.id.pauseButtonPlayDetail:
                myMediaPlayer.pause();
                first = false;
                playButton.setClickable(true);
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setClickable(false);
                pauseButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

    protected void seekUpdation() {
        seekBar.setProgress(myMediaPlayer.getCurrentPosition());
        if (myMediaPlayer.getCurrentPosition() >= seekBar.getMax()) { //play is finished
            pauseButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setEnabled(false);
            playButton.setEnabled(true);
            myMediaPlayer.release();
            myMediaPlayer = null;
            first = true;

        }

        seekHandler.postDelayed(run, 100); //update seek handle every 0.5 seconds
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void setOnPlayDetailListener(OnPlayDetailListener onPlayDetailListener) {
        this.onPlayDetailListener = onPlayDetailListener;
    }

    public void setModel(PlaylistRowModel model) {
        this.model = model;
    }
}
