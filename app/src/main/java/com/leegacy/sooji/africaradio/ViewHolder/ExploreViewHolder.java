package com.leegacy.sooji.africaradio.ViewHolder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leegacy.sooji.africaradio.Activities.OtherProfileActivity;
import com.leegacy.sooji.africaradio.DataObjects.User;
import com.leegacy.sooji.africaradio.Models.ExploreRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by soo-ji on 16-06-02.
 */
public class ExploreViewHolder extends RowViewHolder implements View.OnClickListener{


    private static final String TAG = "EXPLORE_VIEW_HOLDER";
    private final TextView exploreUserID;
    private final TextView exploreDescription;
//    private final ImageView exploreProfile;
    private final ImageView playButton;
    private final ImageView pauseButton;
    private final Firebase ref;
    private MediaPlayer myMediaPlayer;
    private boolean first = true;
    private String audioFile;
    private String otherUid;
    private File localFile;


    public ExploreViewHolder(View itemView) {
        super(itemView);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        exploreUserID = (TextView) itemView.findViewById(R.id.explore_userID);
        exploreDescription = (TextView) itemView.findViewById(R.id.explore_description);
//        exploreProfile = (ImageView) itemView.findViewById(R.id.explore_profile_picture);
        playButton = (ImageView) itemView.findViewById(R.id.explore_play);
        pauseButton = (ImageView) itemView.findViewById(R.id.explore_pause);
        exploreUserID.setOnClickListener(this);
//        exploreProfile.setOnClickListener(this);
        itemView.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        pauseButton.setClickable(false);
        playButton.setOnClickListener(this);
        pauseButton.setVisibility(View.INVISIBLE);
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference audioStorageRef = storage.getReferenceFromUrl("gs://blazing-inferno-7470.appspot.com/audioFile");

        myMediaPlayer = new MediaPlayer();


        localFile = null;
        try {
            localFile = File.createTempFile("audio", "3gp");
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioStorageRef.child(audioFile).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
                pauseButton.setClickable(false);
                pauseButton.setVisibility(View.INVISIBLE);
                playButton.setClickable(true);
                playButton.setVisibility(View.VISIBLE);
                first = true;
            }
        });
    }

    private void startPlayer() {
        try {
            myMediaPlayer.setDataSource(localFile.getAbsolutePath());
            myMediaPlayer.prepare();
//            seekBar.setMax(myMediaPlayer.getDuration());
//            seekUpdation();
            myMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(itemView.getContext(), "fetching audio data failed1", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
            if(v.getId()== R.id.explore_play) {
                if (first) {

                    setupAudio();

                } else {

                    myMediaPlayer.start();
                }
                playButton.setClickable(false);
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setClickable(true);
                pauseButton.setVisibility(View.VISIBLE);
            }
            else if(v.getId() == R.id.explore_pause) {
                myMediaPlayer.pause();
                first = false;
                playButton.setClickable(true);
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setClickable(false);
                pauseButton.setVisibility(View.INVISIBLE);
            }else if(v == itemView) {
                gotoProfile();
            }
//            case R.id.explore_userID:
//                gotoProfile();
//                break;

//            case R.id.explore_profile_picture:
//                gotoProfile();
//                break;


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
