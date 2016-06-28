package com.leegacy.sooji.africaradio.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.DataObjects.User;
import com.leegacy.sooji.africaradio.Listeners.OnPlayDetailListener;
import com.leegacy.sooji.africaradio.Listeners.OnPlaylistListener;
import com.leegacy.sooji.africaradio.Models.ProfileRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.MyAdapter;
import com.leegacy.sooji.africaradio.R;
import com.leegacy.sooji.africaradio.RowModelFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by soo-ji on 16-04-11.
 */
public class ProfileFragment extends Fragment implements OnPlaylistListener, OnPlayDetailListener{
    private View root;
    private String firstName;
    private String lastName;
    private TextView nameTextView;
    private FloatingActionButton recordButton;
    private String uid;
    private List<RowModel> rowModels;
    private Firebase ref;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private MediaPlayer myMediaPlayer;
    private boolean first = true;
    private PlayDetailFragment pdf;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.profile_fragment, null);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
//        nameTextView = (TextView) root.findViewById(R.id.userID);
//        recordButton = (FloatingActionButton) root.findViewById(R.id.recordButton);
//        recordButton.setOnClickListener(this);
        recyclerView = (RecyclerView) root.findViewById(R.id.profile_recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        //lm.setReverseLayout(true);
        recyclerView.setLayoutManager(lm);

        myAdapter = new MyAdapter();
        myAdapter.setOnPlaylistListener(this);
        rowModels = new ArrayList<RowModel>();


//        myAdapter.setRowModels(rowModels);
        //myAdapter.setOnSessionActivityListener(this);
        recyclerView.setAdapter(myAdapter);

        checkAuth();

        return root;
    }


    private void checkAuth() {
        Firebase authRef = new Firebase("https://blazing-inferno-7470.firebaseio.com");
        AuthData authData = authRef.getAuth();
        if (authData != null) {
            // user authenticated
            uid = authData.getUid();
            getUserInfo();
        } else {
            // no user authenticated
            Toast.makeText(getActivity(), "User Not Logged In", Toast.LENGTH_LONG);
        }
    }
    protected void decodeStringtoFile(String audioFile) {
        byte[] decoded = Base64.decode(audioFile, Base64.DEFAULT);
        File file = new File(Environment.getExternalStorageDirectory() + "/audio.3gp");
        if (file.exists()) {
            file.delete();
            file = null;
        }
        try {
            FileOutputStream os = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/audio.3gp"), true);
            os.write(decoded);
            os.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "file not decoded", Toast.LENGTH_SHORT);

        }
        Toast.makeText(getActivity(), "file not decoded", Toast.LENGTH_SHORT);

    }

    protected void setupAudio(String audioKey){
        //myMediaPlayer = MediaPlayer.create(this, outputFile);

        myMediaPlayer = new MediaPlayer();
        ref.child("audioFile").child(audioKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("audioFile fetched" + dataSnapshot.getValue());
                decodeStringtoFile(dataSnapshot.getValue(String.class));
                try {
                    myMediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/audio.3gp");
                    myMediaPlayer.prepare();
                    myMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "fetching audio data failed1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getActivity(), "fetching audio data failed", Toast.LENGTH_SHORT).show();
            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
    }


    protected void getUserInfo() {
        Firebase userRef = ref.child("users").child(uid);
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

                if (rowModels.size() < 1) {
                    rowModels.add(profileRowModel);
                    myAdapter.setRowModels(rowModels);
                } else {
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

    protected void getPlayList() {
        Firebase playlistRef = ref.child("playlist").child(uid);
        playlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playSnapshot : dataSnapshot.getChildren()) {
                    PlaylistItem play = playSnapshot.getValue(PlaylistItem.class);
                    rowModels.add(RowModelFactory.getPlaylistRowModel(play, playSnapshot.getKey()));
                    myAdapter.setRowModels(rowModels);
                    System.out.print("title " + play.getTitle());
                    //Toast toast = Toast.makeText(getContext(), "title is: "+play.getTitle(), Toast.LENGTH_LONG);
                    //toast.show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //    protected void populateInfo(){
//        nameTextView.setText(firstName+" "+lastName);
//        System.out.print(firstName);
//
//    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public List<RowModel> getRowModels() {
        return rowModels;
    }

    public void setRowModels(List<RowModel> rowModels) {
        this.rowModels = rowModels;
    }


    @Override
    public void playRequested() {
        myMediaPlayer.start();
    }

    @Override
    public void initAudioRequested(String audioKey) {
        if(myMediaPlayer!=null) {
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
        setupAudio(audioKey);
    }

    @Override
    public void pauseRequested() {
        myMediaPlayer.pause();
    }

    @Override
    public void addPlayDetailFragment() {
        pdf = new PlayDetailFragment();
        pdf.setOnPlayDetailListener(this);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.overlayFramelayout, pdf)
                .commit();
    }


    @Override
    public void removeFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .remove(pdf)
                .commit();
    }
}
