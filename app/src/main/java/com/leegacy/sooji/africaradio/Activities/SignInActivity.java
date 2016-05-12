package com.leegacy.sooji.africaradio.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.leegacy.sooji.africaradio.DataObjects.User;
import com.leegacy.sooji.africaradio.R;

import java.util.Map;

/**
 * Created by soo-ji on 16-04-09.
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String UID = "userID";
    private Button signInButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(this);
        emailEditText = (EditText) findViewById(R.id.email_signin);
        passwordEditText = (EditText) findViewById(R.id.singin_password);
        myFirebaseRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                //TODO: uncomment this later
//                String email = emailEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
                    String email = "si-hun@hotmail.com";
                    String password = "Cotlgns12";
                if (!email.contains("@")) {
                    Toast toast = Toast.makeText(this, "Invalid E-mail", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (password.length() < 8) {
                    Toast toast = Toast.makeText(this, "Password should be at least 8 characters", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
//                myFirebaseRef.createUser(email, password, new MyValueResultHandler());
                //TODO: monitor authentication

                myFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(final AuthData authData) {
                        System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                        Firebase ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog/users");
                        System.out.println("UID------------------------: " + authData.getUid());
                        Query queryRef = ref.child(authData.getUid());
//                        Query queryRef = ref.child("users");
                        final Intent intent = new Intent(getBaseContext(), TabsActivity.class);

                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                // do some stuff once
//                                String firstName = (String) snapshot.getValue().

                                System.out.println("queried------------: " + snapshot);
                                User user = snapshot.getValue(User.class);
//                                intent.putExtra(FIRST_NAME, user.getFirstName());
//                                intent.putExtra(LAST_NAME, user.getLastName());
                                intent.putExtra(UID, authData.getUid());
                                System.out.println(user.getFirstName() + " - " + user.getLastName());
                                finish();
                                startActivity(intent);
                            }


                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Toast toast = Toast.makeText(getBaseContext(), "querying name of user failed: " + firebaseError, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });

                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // there was an error
                        Toast toast = Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                });
                break;
        }
    }

    public class MyValueResultHandler implements Firebase.ValueResultHandler<Map<String, Object>> {

        @Override
        public void onSuccess(Map<String, Object> stringObjectMap) {
            Toast toast = Toast.makeText(getBaseContext(), "user created", Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("Successfully created user account with uid: " + stringObjectMap.get("uid"));
        }

        @Override
        public void onError(FirebaseError firebaseError) {
            Toast toast = Toast.makeText(getBaseContext(), "ERROR: " + firebaseError, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
