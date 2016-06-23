package com.leegacy.sooji.africaradio.Fragments;

import android.os.Bundle;
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
import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.Listeners.OnExploreViewClickedListener;
import com.leegacy.sooji.africaradio.Models.ExploreRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.MyAdapter;
import com.leegacy.sooji.africaradio.R;

import java.util.ArrayList;

/**
 * Created by soo-ji on 16-06-02.
 */
public class ExploreFragment extends Fragment {
    private static final String TAG = "EXPLORE_FRAGMENT";
    private View root;
    private RecyclerView explore_recycler;
    private Firebase ref;
    private ArrayList<RowModel> rowModels;
    private MyAdapter adapter;
    private TextView loadingText;
    private OnExploreViewClickedListener onExploreViewClickedListener;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.explore_fragment, null);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        explore_recycler = (RecyclerView) root.findViewById(R.id.explore_recycler);
        loadingText = (TextView) root.findViewById(R.id.loadingText);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        explore_recycler.setLayoutManager(lm);
        adapter = new MyAdapter();

        explore_recycler.setAdapter(adapter);
        rowModels = new ArrayList<RowModel>();
        checkAuth();
        getPlaylists();
        return root;
    }

    private void checkAuth() {
        Firebase authRef = new Firebase("https://blazing-inferno-7470.firebaseio.com");
        authRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                    uid = authData.getUid();
                } else {
                    // user is not logged in
                    Toast.makeText(getActivity(), "User Not Logged In", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void getPlaylistHelper(final String otherUid) {

        ref.child("playlist").child(otherUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playSnapshot : dataSnapshot.getChildren()) {
                    ExploreRowModel model = new ExploreRowModel();

                    model.setUserID(otherUid);
                    model.setRecordingID(playSnapshot.getKey());
                    PlaylistItem item = playSnapshot.getValue(PlaylistItem.class);
                    Log.e(TAG, "playlist item: " + item);
                    model.setRecordingTitle(item.getTitle());
                    rowModels.add(model);
                }
                loadingText.setVisibility(View.INVISIBLE);
                adapter.setRowModels(rowModels);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "unable to retrieve user playlists");
            }
        });
    }

    private void getPlaylists() {
        //TODO: make this pull with respect to the most recent updates
        ref.child("users").addValueEventListener(new ValueEventListener() {

            String otherUid;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot uidSnapshot : dataSnapshot.getChildren()) {

                    otherUid = uidSnapshot.getKey();

                    getPlaylistHelper(otherUid);
                }

                Log.e(TAG, "setRowmodels");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "unable to retrieve users");
            }
        });

    }

}
