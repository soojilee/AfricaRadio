package com.leegacy.sooji.africaradio;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by soo-ji on 16-04-11.
 */
public class PineApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        // other setup code
    }
}
