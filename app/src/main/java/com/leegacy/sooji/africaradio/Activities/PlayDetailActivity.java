package com.leegacy.sooji.africaradio.Activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-04-28.
 */
public class PlayDetailActivity extends AppCompatActivity {
    private Firebase ref;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_playdetail);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        String playlistKey = getIntent().getStringExtra("playlistKey");
        

    }
}
