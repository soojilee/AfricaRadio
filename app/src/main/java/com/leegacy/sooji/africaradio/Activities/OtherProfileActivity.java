package com.leegacy.sooji.africaradio.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.DataObjects.User;
import com.leegacy.sooji.africaradio.Models.ProfileRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.MyAdapter;
import com.leegacy.sooji.africaradio.R;
import com.leegacy.sooji.africaradio.RowModelFactory;

import java.util.ArrayList;

/**
 * Created by soo-ji on 16-06-04.
 */
public class OtherProfileActivity extends AppCompatActivity{
    private Firebase ref;
    private RecyclerView other_recycler;
    private ArrayList<RowModel> rowModels;
    private MyAdapter myAdapter;
    private String uid;
    private String otherUid;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherpeople_profile_fragment);
        otherUid = getIntent().getStringExtra("OTHER_UID");
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        rowModels = new ArrayList<RowModel>();
        other_recycler = (RecyclerView) findViewById(R.id.other_profile_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        other_recycler.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        other_recycler.setAdapter(myAdapter);
        myAdapter.setRowModels(rowModels);
        checkAuth();


    }

    protected void getUserInfo(){
        Firebase userRef = ref.child("users").child(otherUid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                firstName = user.getFirstName();
                lastName = user.getLastName();

//                populateInfo();
                ProfileRowModel profileRowModel = new ProfileRowModel();
                profileRowModel.setFirstName(firstName);
                profileRowModel.setLastName(lastName);
                profileRowModel.setOtherUid(otherUid);
                if(rowModels.size()<1) {
                    rowModels.add(profileRowModel);
                    myAdapter.setRowModels(rowModels);
                }else{
                    rowModels.remove(0);
                    rowModels.add(0, profileRowModel);
                    myAdapter.setRowModels(rowModels);
                }
                getPlayList();
//                playListID = user.getPlayListID();


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    protected void getPlayList(){
        Firebase playlistRef = ref.child("playlist").child(otherUid);
        playlistRef.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PlaylistItem play = dataSnapshot.getValue(PlaylistItem.class);
                rowModels.add(RowModelFactory.getPlaylistRowModel(play, dataSnapshot.getKey()));
                System.out.print("title " + play.getTitle());
                myAdapter.setRowModels(rowModels);
                //Toast toast = Toast.makeText(getContext(), "title is: "+play.getTitle(), Toast.LENGTH_LONG);
                //toast.show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void checkAuth() {
        Firebase authRef = new Firebase("https://blazing-inferno-7470.firebaseio.com");
        authRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                    uid = authData.getUid();
                    getUserInfo();
                } else {
                    // user is not logged in
                    Toast.makeText(getBaseContext(), "User Not Logged In", Toast.LENGTH_LONG);
                }
            }
        });
    }

}
