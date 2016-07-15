package com.leegacy.sooji.africaradio.ViewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leegacy.sooji.africaradio.DataObjects.User;
import com.leegacy.sooji.africaradio.Listeners.OnHomeFragmentListener;
import com.leegacy.sooji.africaradio.Listeners.OnHomeRowListener;
import com.leegacy.sooji.africaradio.Models.HomeRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-07-07.
 */
public class HomeViewHolder extends RowViewHolder implements View.OnClickListener, OnHomeFragmentListener {

    private final ImageView playButton;
    private final ImageView pauseButton;
    private final TextView userName;
    private final ImageView profilePic;
    private final TextView title;
    private final TextView caption;
    private final TextView numHearts;
    private final ImageView heart;
    private final Firebase ref;
    private final Firebase savingDataRef;
    private final FirebaseStorage storage;
    private final TextView likesText;
    private boolean first = true;
    private String audioKey;
    private OnHomeRowListener onHomeRowListener;
    private boolean isHeartPressed=false;
    private String uid;
    private int heartCount;
    private String rowUserID;
    private Firebase heartGiverRef;

    public HomeViewHolder(View itemView) {
        super(itemView);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/");
        savingDataRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog");
        storage = FirebaseStorage.getInstance();
        authUid();
        likesText = (TextView) itemView.findViewById(R.id.home_row_likes_text);
        profilePic = (ImageView) itemView.findViewById(R.id.home_row_profile_image);
        userName = (TextView) itemView.findViewById(R.id.home_row_userID);
        playButton = (ImageView) itemView.findViewById(R.id.home_row_play);
        playButton.setOnClickListener(this);
        pauseButton = (ImageView) itemView.findViewById(R.id.home_row_pause);
        pauseButton.setOnClickListener(this);
        pauseButton.setEnabled(false);
        pauseButton.setVisibility(View.INVISIBLE);
        title = (TextView) itemView.findViewById(R.id.home_row_title);
        caption = (TextView) itemView.findViewById(R.id.home_row_caption);
        numHearts = (TextView) itemView.findViewById(R.id.home_num_likes);
        heart = (ImageView) itemView.findViewById(R.id.home_row_heart_empty);
        heart.setOnClickListener(this);
    }

    private void loadProfilePicture(String userid){
        StorageReference storageRef = storage.getReferenceFromUrl("gs://blazing-inferno-7470.appspot.com");


        StorageReference profileRef = storageRef.child("profile_pictures/" + userid + "/" + userid + ".png");


//        try {
//            localFile = File.createTempFile("profile_image", "jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        profileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(itemView.getResources(), bmp);
                roundDrawable.setCircular(true);
                bmp.recycle();
                // Apply it to an ImageView.

                profilePic.setImageDrawable(roundDrawable);

            }
        });
    }

    @Override
    public void update(RowModel rowModel) {
        HomeRowModel model = (HomeRowModel) rowModel;
//        userName.setText(model.getUserName());
        rowUserID = model.getUserID();
        title.setText(model.getTitle());
        caption.setText(model.getCaption());
        heartCount = model.getHeartCount();
        displayHearts();
        heartCount = model.getHeartCount();
        audioKey = model.getAudioKey();
//        if(isHeartPressed){
//            heart.setImageResource(R.drawable.heart_icon);
//        }
        loadProfilePicture(rowUserID);
        savingDataRef.child("users").child(rowUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.getFirstName() + " " + user.getLastName());
                //find if user pressed heart for this
                Query query = savingDataRef.child("heartGiver").child(rowUserID).child(audioKey).orderByKey().equalTo(uid);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            isHeartPressed = true;
                            heart.setImageResource(R.drawable.heart_icon);
                        } else {
                            isHeartPressed = false;
                            heart.setImageResource(R.drawable.heart_icon_empty);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void displayHearts() {
        if (heartCount < 1) {
            numHearts.setVisibility(View.GONE);
            likesText.setVisibility(View.GONE);
        } else if (heartCount == 1) {
            numHearts.setVisibility(View.VISIBLE);
            numHearts.setText(String.valueOf(heartCount));
            likesText.setText("like");
            likesText.setVisibility(View.VISIBLE);

        } else {

            numHearts.setVisibility(View.VISIBLE);
            numHearts.setText(String.valueOf(heartCount));
            likesText.setText("likes");
            likesText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_row_play:
                pauseButton.setEnabled(true);
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(false);
                playButton.setVisibility(View.INVISIBLE);
                if (first) {
                    onHomeRowListener.setHomeFragmentListener(this);
                    onHomeRowListener.initAudioRequested(audioKey);
                } else {
                    onHomeRowListener.playRequested();
                }

                break;
            case R.id.home_row_pause:
                first = false;
                onHomeRowListener.pauseRequested();
                playButton.setEnabled(true);
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setEnabled(false);
                pauseButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.home_row_heart_empty:
                if(!isHeartPressed) {
                    heart.setImageResource(R.drawable.heart_icon);
                    heartCount += 1;
                    savingDataRef.child("playlist").child(rowUserID).child(audioKey).child("numHearts").setValue(heartCount);
                    heartGiverRef = savingDataRef.child("heartGiver").child(rowUserID).child(audioKey).push();
                    heartGiverRef.setValue(uid);
                    displayHearts();
                    isHeartPressed = !isHeartPressed;
                }else{
                    heart.setImageResource(R.drawable.heart_icon_empty);
                    heartCount -= 1;
                    savingDataRef.child("playlist").child(rowUserID).child(audioKey).child("numHearts").setValue(heartCount);
                    heartGiverRef.removeValue(new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            //heart removed update UI
                            displayHearts();
                        }
                    });

                    isHeartPressed = !isHeartPressed;
                }
                break;
        }
    }

    @Override
    public void playFinished() {
        pauseButton.setEnabled(false);
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setEnabled(true);
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void playing() {
        pauseButton.setEnabled(true);
        pauseButton.setVisibility(View.VISIBLE);
        playButton.setEnabled(false);
        playButton.setVisibility(View.INVISIBLE);
    }

    public void setOnHomeRowListener(OnHomeRowListener onHomeRowListener) {
        this.onHomeRowListener = onHomeRowListener;
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
}
