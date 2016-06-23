package com.leegacy.sooji.africaradio.ViewHolder;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.leegacy.sooji.africaradio.Models.ProfileRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-04-11.
 */
public class ProfileViewHolder extends RowViewHolder implements View.OnClickListener{
    private final TextView name;
//    private final ImageView profilePicture;
    private final TextView bio;
    private final RelativeLayout followButtonContainer;
    private final Button followButton;
    private final Button messageButton;
    private final Firebase ref;
    private Firebase followingRef;
    private ProfileRowModel model;
    private String uid;
    private String otherUid;
    private boolean refset = false;

    public ProfileViewHolder(View itemView) {
        super(itemView);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/");
        followingRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog/following");
        authUid();
        name = (TextView) itemView.findViewById(R.id.userID);
//        profilePicture = (ImageView) itemView.findViewById(R.id.profilePicture);
        followButtonContainer = (RelativeLayout) itemView.findViewById(R.id.followButtonContainer);
        followButton = (Button) itemView.findViewById(R.id.followButton);
        followButton.setActivated(false);
        followButton.setOnClickListener(this);
        messageButton = (Button) itemView.findViewById(R.id.messageButton);
        messageButton.setOnClickListener(this);
        bio = (TextView) itemView.findViewById(R.id.bio);
    }

    private void authUid() {
        AuthData authData = ref.getAuth();
        if (authData != null) {
            // user authenticated
            uid = authData.getUid();
        } else {
            // no user authenticated
            Toast.makeText(itemView.getContext(), "User Login Required", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void update(RowModel rowModel) {
        model = (ProfileRowModel) rowModel;
        name.setText(model.getFirstName()+model.getLastName());
        if(model.getProfilePicture() == null){
            Resources res = itemView.getResources();
//            Drawable drawable = res.getDrawable(R.drawable.profile);
//            profilePicture.setImageDrawable(drawable);
        }else{
            //retrieve image from firebase
        }
        bio.setText(model.getBio());
        if(model.getOtherUid() != null){
            followButtonContainer.setVisibility(View.VISIBLE);
            otherUid = model.getOtherUid();
        }
    }

    public ProfileRowModel getModel() {
        return model;
    }

    public void setModel(ProfileRowModel model) {
        this.model = model;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.messageButton:
                Toast.makeText(itemView.getContext(), "Messaging not supported yet.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.followButton:
                //if user clicks follow on this user, then update database

                if(followButton.isActivated()){
                    followingRef.removeValue(new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            followButton.setActivated(false);
                            followButton.setText("follow");
                        }
                    });

                }else{
                    followingRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog/following").child(uid).push();
                    followingRef.setValue(otherUid, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            followButton.setActivated(true);
                            followButton.setText("following");
                        }
                    });
                }
                break;
        }
    }

}
