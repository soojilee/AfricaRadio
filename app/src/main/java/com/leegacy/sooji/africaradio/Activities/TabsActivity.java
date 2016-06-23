package com.leegacy.sooji.africaradio.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.leegacy.sooji.africaradio.Fragments.ExploreFragment;
import com.leegacy.sooji.africaradio.Fragments.ProfileFragment;
import com.leegacy.sooji.africaradio.Fragments.RecordFragment;
import com.leegacy.sooji.africaradio.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soo-ji on 16-04-10.
 */
public class TabsActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private String firstName;
    private String lastName;
    private String playListID;
    private String uid;
    private Firebase ref;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
//        firstName = getIntent().getStringExtra(SignInActivity.FIRST_NAME);
//        Toast toast = Toast.makeText(this, "first name is: "+firstName, Toast.LENGTH_LONG);
//        lastName = getIntent().getStringExtra(SignInActivity.LAST_NAME);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
//        uid = getIntent().getStringExtra(SignInActivity.UID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
//        addFragment();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ProfileFragment pf = new ProfileFragment();

        RecordFragment rf = new RecordFragment();
        ExploreFragment ef = new ExploreFragment();

        adapter.addFragment(ef, "EXPLORE");
        adapter.addFragment(rf, "RECORD");
        adapter.addFragment(pf, "PROFILE");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {
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

    protected void addFragment(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
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


                return tabFragment;
            }
        });
    }


}
