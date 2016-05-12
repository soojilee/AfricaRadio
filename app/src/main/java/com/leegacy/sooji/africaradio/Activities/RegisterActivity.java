package com.leegacy.sooji.africaradio.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.leegacy.sooji.africaradio.DataObjects.User;
import com.leegacy.sooji.africaradio.R;

import java.util.Map;

/**
 * Created by soo-ji on 16-04-11.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText emailRegister;
    private EditText passwordRegister;
    private EditText passwordAgain;

    private EditText firstName;
    private EditText lastName;
    private Button emailRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailRegister = (EditText) findViewById(R.id.email_register);
        emailRegisterButton = (Button) findViewById(R.id.email_register_button);
        emailRegisterButton.setOnClickListener(this);

        passwordRegister = (EditText) findViewById(R.id.register_password);
        passwordAgain = (EditText) findViewById(R.id.register_password_again);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.email_register_button:
                String email = emailRegister.getText().toString();
                String password = passwordRegister.getText().toString();
                String password_again = passwordAgain.getText().toString();
                if(!password.equals(password_again)){
                    Toast toast = Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Firebase ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/");
                ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        System.out.println("whatlkaj");
                        Firebase ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
                        Firebase userRef = ref.child("users").child(result.get("uid").toString());

                        User user = new User(firstName.getText().toString(), lastName.getText().toString());
                        userRef.setValue(user, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                                    Toast toast = Toast.makeText(getBaseContext(), "User Not Created"+firebaseError, Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    System.out.println("Data saved successfully.");
                                    //System.out.println("Successfully created user account with uid: " + result.get("uid"));
                                    Toast toast = Toast.makeText(getBaseContext(), "Registration Successful", Toast.LENGTH_SHORT);
                                    toast.show();
                                    startActivity(new Intent(getBaseContext(), SignInActivity.class));
                                    finish();
                                }
                            }
                        });


                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Toast toast = Toast.makeText(getBaseContext(), "Registration Failed"+firebaseError, Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });

        }
    }
}
