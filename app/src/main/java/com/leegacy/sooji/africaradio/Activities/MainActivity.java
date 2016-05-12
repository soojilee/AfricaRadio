package com.leegacy.sooji.africaradio.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.leegacy.sooji.africaradio.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView signInTextView;
    private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInTextView = (TextView) findViewById(R.id.signIn);
        signInTextView.setOnClickListener(this);
        registerTextView = (TextView) findViewById(R.id.register);
        registerTextView.setOnClickListener(this);
        //startActivity(new Intent(this, TabsActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn:
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }
}
