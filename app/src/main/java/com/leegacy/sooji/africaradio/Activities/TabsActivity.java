package com.leegacy.sooji.africaradio.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.leegacy.sooji.africaradio.Fragments.ProfileFragment;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-04-10.
 */
public class TabsActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private String firstName;
    private String lastName;
    private String playListID;
    private String uid;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
//        firstName = getIntent().getStringExtra(SignInActivity.FIRST_NAME);
//        Toast toast = Toast.makeText(this, "first name is: "+firstName, Toast.LENGTH_LONG);
//        lastName = getIntent().getStringExtra(SignInActivity.LAST_NAME);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");

        uid = getIntent().getStringExtra(SignInActivity.UID);
        addFragment();





    }



    protected void addFragment(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()){
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Fragment getItem(int position) {
                ProfileFragment tabFragment = new ProfileFragment();
//                tabFragment.setFirstName(firstName);
//                tabFragment.setLastName(lastName);

                tabFragment.setUid(uid);
                return tabFragment;
            }
        });
    }


}
