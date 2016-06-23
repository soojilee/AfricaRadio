package com.leegacy.sooji.africaradio.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by soo-ji on 16-04-22.
 */
public class PostActivity extends AppCompatActivity implements View.OnClickListener{
    private Button postButton;
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
        postButton = (Button) findViewById(R.id.postButton);
        postButton.setOnClickListener(this);
        addTitle = (EditText) findViewById(R.id.addTitle);
        descriptionBox = (EditText) findViewById(R.id.description);
        seekBar = (SeekBar) findViewById(R.id.postSeekBar);
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

                byte[] soundBytes;

                try{
                    InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(audioFile)));
                    soundBytes = new byte[inputStream.available()];
                    soundBytes = toByteArray(inputStream);
                    String audioFileString = Base64.encodeToString(soundBytes, Base64.DEFAULT);
                    ref.child("audioFile").child(newplayref.getKey()).setValue(audioFileString);
                    inputStream.close();
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "ERROR, SOUND NOT CONVERTED TO STRING", Toast.LENGTH_SHORT).show();
                }
//
                Intent intent = new Intent(this,TabsActivity.class);

                finish();
                startActivity(intent);

                break;
        }
    }



    public byte[] toByteArray(InputStream in) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while(read != -1){
            read = in.read(buffer);
            if(read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

}
