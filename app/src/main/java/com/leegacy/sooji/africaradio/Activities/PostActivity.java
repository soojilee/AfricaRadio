package com.leegacy.sooji.africaradio.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.R;

import java.io.File;

/**
 * Created by soo-ji on 16-04-22.
 */
public class PostActivity extends AppCompatActivity implements View.OnClickListener{
    private FrameLayout postButton;
    private EditText addTitle;
    private EditText descriptionBox;
    private String audioFile;
    private SeekBar seekBar;
    private Firebase ref;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postButton = (FrameLayout) findViewById(R.id.postButton);
        postButton.setOnClickListener(this);
        addTitle = (EditText) findViewById(R.id.addTitle);
        descriptionBox = (EditText) findViewById(R.id.description);
//        seekBar = (SeekBar) findViewById(R.id.postSeekBar);
        audioFile = getIntent().getStringExtra("audioFile");

        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");



    }
    @Override
    public void onResume() {
        super.onResume();
        Firebase authRef = new Firebase("https://blazing-inferno-7470.firebaseio.com");
        authRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                    uid = authData.getUid();
                } else {
                    // user is not logged in
                    Toast.makeText(getBaseContext(), "User Not Logged In", Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference audioStorageRef = storage.getReferenceFromUrl("gs://blazing-inferno-7470.appspot.com/audioFile");

        switch(v.getId()){
            case R.id.postButton:
                String title = addTitle.getText().toString();
                if(title.length() == 0){
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                String description = descriptionBox.getText().toString();
                //save this entry to this user's database
                Firebase playlistRef = ref.child("playlist").child(uid);
                PlaylistItem recording = new PlaylistItem();
//                recording.setAudioFile("");
                recording.setDescription(description);
                recording.setTitle(title);
                Firebase newplayref = playlistRef.push();
                newplayref.setValue(recording);
//                newplayref.child("audioFile").setValue(newplayref.getKey());

                Uri file = Uri.fromFile(new File(audioFile));
                StorageReference audioFileRef = audioStorageRef.child(newplayref.getKey());
                UploadTask uploadTask = audioFileRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Intent intent = new Intent(getBaseContext(),TabsActivity.class);

                        finish();
                        startActivity(intent);
                    }
                });

                break;
        }
    }


    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

}
