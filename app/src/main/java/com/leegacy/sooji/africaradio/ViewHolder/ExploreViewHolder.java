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
import com.leegacy.sooji.africaradio.Activities.OtherProfileActivity;
import com.leegacy.sooji.africaradio.DataObjects.User;
import com.leegacy.sooji.africaradio.Models.ExploreRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by soo-ji on 16-06-02.
 */
public class ExploreViewHolder extends RowViewHolder implements View.OnClickListener{


    private static final String TAG = "EXPLORE_VIEW_HOLDER";
    private final TextView exploreUserID;
    private final TextView exploreDescription;
    private final ImageView exploreProfile;
    private final ImageView playButton;
    private final ImageView pauseButton;
    private final Firebase ref;
    private MediaPlayer myMediaPlayer;
    private boolean first = true;
    private String audioFile;
    private String otherUid;


    public ExploreViewHolder(View itemView) {
        super(itemView);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        exploreUserID = (TextView) itemView.findViewById(R.id.explore_userID);
        exploreDescription = (TextView) itemView.findViewById(R.id.explore_description);
        exploreProfile = (ImageView) itemView.findViewById(R.id.explore_profile_picture);
        playButton = (ImageView) itemView.findViewById(R.id.explore_play);
        pauseButton = (ImageView) itemView.findViewById(R.id.explore_pause);
        exploreUserID.setOnClickListener(this);
        exploreProfile.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        pauseButton.setClickable(false);
        playButton.setOnClickListener(this);
        pauseButton.setVisibility(View.INVISIBLE);
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

    @Override
    public void update(RowModel rowModel) {
        ExploreRowModel model = (ExploreRowModel) rowModel;
        otherUid = model.getUserID();
        audioFile = model.getRecordingID();
        exploreDescription.setText(model.getRecordingTitle());

        ref.child("users").child(model.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                exploreUserID.setText(user.getFirstName() + " " + user.getLastName());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    protected void setupAudio(){
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
            case R.id.explore_play:
                if (first) {

                    setupAudio();

                } else {

                    myMediaPlayer.start();
                }
                playButton.setClickable(false);
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setClickable(true);
                pauseButton.setVisibility(View.VISIBLE);

                break;
            case R.id.explore_pause:
                myMediaPlayer.pause();
                first = false;
                playButton.setClickable(true);
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setClickable(false);
                pauseButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.explore_userID:
                gotoProfile();
                break;
            case R.id.explore_profile_picture:
                gotoProfile();
                break;
        }
    }

    private void gotoProfile() {
        Intent intent = new Intent(itemView.getContext(), OtherProfileActivity.class);
        intent.putExtra("OTHER_UID", otherUid);
        itemView.getContext().startActivity(intent);
//        ref.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                Intent intent = new Intent(itemView.getContext(), OtherProfileActivity.class);
//                intent.putExtra("OTHER_UID", uid);
//                itemView.getContext().startActivity(intent);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
    }

}
