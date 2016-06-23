package com.leegacy.sooji.africaradio.Activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by soo-ji on 16-04-28.
 */
public class PlayDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private Firebase ref;
    private String uid;
    private String playlistKey;
    private TextView title;
    private TextView description;
    private ImageView playIcon;
    private ImageView pauseIcon;
    private MediaPlayer myMediaPlayer;
    private boolean first = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playdetail);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        title = (TextView) findViewById(R.id.titlePlayDetail);
        description = (TextView) findViewById(R.id.descriptionPlayDetail);
        playIcon = (ImageView) findViewById(R.id.playButtonPlayDetail);
        playIcon.setOnClickListener(this);
        pauseIcon = (ImageView) findViewById(R.id.pauseButtonPlayDetail);
        pauseIcon.setOnClickListener(this);
        pauseIcon.setEnabled(false);
        pauseIcon.setVisibility(View.INVISIBLE);


    }

    private void init() {
        ref.child("playlist").child(uid).child(playlistKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlaylistItem play = dataSnapshot.getValue(PlaylistItem.class);
                updateUI(play);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void updateUI(PlaylistItem play) {
        title.setText(play.getTitle());
        description.setText(play.getDescription());
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
            Toast.makeText(this, "file not decoded", Toast.LENGTH_SHORT);

        }
        Toast.makeText(this, "file not decoded", Toast.LENGTH_SHORT);

    }

    protected void setupAudio(){
        //myMediaPlayer = MediaPlayer.create(this, outputFile);

        myMediaPlayer = new MediaPlayer();
        ref.child("audioFile").child(playlistKey).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Toast.makeText(getBaseContext(), "fetching audio data failed1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getBaseContext(), "fetching audio data failed", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        playlistKey = getIntent().getStringExtra("playlistKey");
        Firebase authRef = new Firebase("https://blazing-inferno-7470.firebaseio.com");
        authRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                    uid = authData.getUid();
                    init();
                } else {
                    // user is not logged in
                    Toast.makeText(getBaseContext(), "User Not Logged In", Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButtonPlayDetail:
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
            case R.id.pauseButtonPlayDetail:
                myMediaPlayer.pause();
                first = false;
                playIcon.setEnabled(true);
                playIcon.setVisibility(View.VISIBLE);
                pauseIcon.setEnabled(false);
                pauseIcon.setVisibility(View.INVISIBLE);
                break;

        }
    }
}
