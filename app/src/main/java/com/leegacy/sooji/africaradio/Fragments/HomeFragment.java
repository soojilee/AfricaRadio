package com.leegacy.sooji.africaradio.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.Listeners.OnHomeFragmentListener;
import com.leegacy.sooji.africaradio.Listeners.OnHomeRowListener;
import com.leegacy.sooji.africaradio.Models.HomeRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.MyAdapter;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by soo-ji on 16-07-06.
 */
public class HomeFragment extends Fragment implements OnHomeRowListener {
    private static final String TAG = "HOME_FRAGMENT";
    private View root;
    private Firebase ref;
    private RecyclerView home_recycler;
    private TextView loadingText;
    private MyAdapter adapter;
    private ArrayList<RowModel> rowModels;
    private String uid;
    private MediaPlayer myMediaPlayer;
    private File localFile;
    private OnHomeFragmentListener onHomeFragmentListener;
    private HashMap<String, PlaylistItem> hm;
    private HomeRowModel homeRowModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, null);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        home_recycler = (RecyclerView) root.findViewById(R.id.home_recycler);
        loadingText = (TextView) root.findViewById(R.id.homeloadingText);
        loadingText.setVisibility(View.INVISIBLE);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        home_recycler.setLayoutManager(lm);
        adapter = new MyAdapter();
        adapter.setOnHomeRowListener(this);
        home_recycler.setAdapter(adapter);
        rowModels = new ArrayList<RowModel>();
        checkAuth();
        return root;
    }

    private void loadFeed() {
        hm = new HashMap<String, PlaylistItem>();

        //TODO: limit query to certain number so you only pull limited number of data at a time
        ref.child("following").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot followingSnapshot : dataSnapshot.getChildren()) {

                    Log.e(TAG, followingSnapshot.getValue(String.class));
                    ref.child("playlist").child(followingSnapshot.getValue(String.class)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot playlistSnapshot : dataSnapshot.getChildren()) {
                                PlaylistItem value = playlistSnapshot.getValue(PlaylistItem.class);
                                value.setUserID(followingSnapshot.getValue(String.class));
                                String key = playlistSnapshot.getKey();
                                //hm.put(key, value);
                                homeRowModel = new HomeRowModel();
                                homeRowModel.setAudioKey(key);
                                homeRowModel.setTitle(value.getTitle());
                                homeRowModel.setCaption(value.getDescription());
                                homeRowModel.setHeartCount(value.getNumHearts());
                                homeRowModel.setUserID(value.getUserID());
                                rowModels.add(homeRowModel);
                            }


                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                //Map<String, PlaylistItem> treeMap = new TreeMap<>(hm);
//                generateHomeRowModels(treeMap);
                loadingText.setVisibility(View.INVISIBLE);
                adapter.setRowModels(rowModels);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    private void generateHomeRowModels(Map<String, PlaylistItem> treeMap) {
        Set s = treeMap.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            PlaylistItem value = (PlaylistItem) entry.getValue();
            homeRowModel = new HomeRowModel();
            homeRowModel.setAudioKey(key);
            homeRowModel.setTitle(value.getTitle());
            homeRowModel.setCaption(value.getDescription());
            homeRowModel.setHeartCount(value.getNumHearts());
            homeRowModel.setUserID(value.getUserID());
            rowModels.add(homeRowModel);
//            ref.child("users").child(value.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    User user = dataSnapshot.getValue(User.class);
//                    homeRowModel.setUserName(user.getFirstName() + " " + user.getLastName());
//                    //find if user pressed heart for this
//                    Query query = ref.child("heartGiver").child(homeRowModel.getUserID()).child(homeRowModel.getAudioKey()).equalTo(uid);
//                    if (query != null) {
//                        homeRowModel.setHeartPressed(true);
//                    } else {
//                        homeRowModel.setHeartPressed(false);
//                    }
//
//
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//
//                }
//            });
//            System.out.println(key + " => " + value);
        }

    }

    private void checkAuth() {
        Firebase authRef = new Firebase("https://blazing-inferno-7470.firebaseio.com");
        authRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                    uid = authData.getUid();
                    loadFeed();
                } else {
                    // user is not logged in

                }
            }
        });
    }

    protected void setupAudio(String audioKey) {
        //myMediaPlayer = MediaPlayer.create(this, outputFile);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference audioStorageRef = storage.getReferenceFromUrl("gs://blazing-inferno-7470.appspot.com/audioFile");

        myMediaPlayer = new MediaPlayer();


        localFile = null;
        try {
            localFile = File.createTempFile("audio", "3gp");
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioStorageRef.child(audioKey).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                startPlayer();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO: handle errors
            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onHomeFragmentListener.playFinished();
                myMediaPlayer.release();
                myMediaPlayer = null;
            }
        });

    }

    private void startPlayer() {
        try {
            myMediaPlayer.setDataSource(localFile.getAbsolutePath());
            myMediaPlayer.prepare();
//            seekBar.setMax(myMediaPlayer.getDuration());
//            seekUpdation();
            myMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "fetching audio data failed1", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void playRequested() {
        myMediaPlayer.start();
        onHomeFragmentListener.playing();
    }

    @Override
    public void initAudioRequested(String audioKey) {
        if (myMediaPlayer != null) {
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
        setupAudio(audioKey);
    }

    @Override
    public void setHomeFragmentListener(OnHomeFragmentListener v) {
        this.onHomeFragmentListener = v;
    }

    @Override
    public void pauseRequested() {
        myMediaPlayer.pause();
        onHomeFragmentListener.playFinished(); //updating pauseIcon to to playIcon
    }
}
