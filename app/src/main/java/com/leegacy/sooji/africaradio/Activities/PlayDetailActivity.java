package com.leegacy.sooji.africaradio.Activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private Handler seekHandler;
    private MediaPlayer myMediaPlayer;
    private boolean first = true;
    private SeekBar seekBar;
    private File localFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playdetail);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        playlistKey = getIntent().getStringExtra("playlistKey");
        title = (TextView) findViewById(R.id.titlePlayDetail);
        description = (TextView) findViewById(R.id.descriptionPlayDetail);
        playIcon = (ImageView) findViewById(R.id.playButtonPlayDetail);
        playIcon.setOnClickListener(this);
        pauseIcon = (ImageView) findViewById(R.id.pauseButtonPlayDetail);
        pauseIcon.setOnClickListener(this);
        pauseIcon.setEnabled(false);
        pauseIcon.setVisibility(View.INVISIBLE);
        seekBar = (SeekBar) findViewById(R.id.seekbarPlayDetail);
        seekHandler = new Handler();


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(myMediaPlayer!=null) {
            myMediaPlayer.release();
            myMediaPlayer = null;
            seekHandler.removeCallbacks(run);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myMediaPlayer!=null) {
            myMediaPlayer.release();
            myMediaPlayer = null;
            seekHandler.removeCallbacks(run);
        }
    }


    private void updateUI(PlaylistItem play) {
        title.setText(play.getTitle());
        description.setText(play.getDescription());
    }

    protected void seekUpdation(){
        seekBar.setProgress(myMediaPlayer.getCurrentPosition());
        if(myMediaPlayer.getCurrentPosition() >= seekBar.getMax()){ //play is finished
            pauseIcon.setVisibility(View.INVISIBLE);
            playIcon.setVisibility(View.VISIBLE);
            pauseIcon.setEnabled(false);
            playIcon.setEnabled(true);
            myMediaPlayer.release();
            myMediaPlayer = null;
            first = true;

        }

        seekHandler.postDelayed(run, 100); //update seek handle every 0.5 seconds
    }

    Runnable run = new Runnable() { @Override public void run() { seekUpdation(); } };
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference audioStorageRef = storage.getReferenceFromUrl("gs://blazing-inferno-7470.appspot.com/audioFile");

        myMediaPlayer = new MediaPlayer();


        localFile = null;
        try {
            localFile = File.createTempFile("audio", "3gp");
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioStorageRef.child(playlistKey).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                startPlayer();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO: handle errors
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

    private void startPlayer() {
        try {
            myMediaPlayer.setDataSource(localFile.getAbsolutePath());
            myMediaPlayer.prepare();
            seekBar.setMax(myMediaPlayer.getDuration());
            seekUpdation();
            myMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "fetching audio data failed1", Toast.LENGTH_SHORT).show();
        }
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

                    seekBar.setMax(myMediaPlayer.getDuration());
                    seekUpdation();
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
