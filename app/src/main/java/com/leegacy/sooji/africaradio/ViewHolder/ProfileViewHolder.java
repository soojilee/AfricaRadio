package com.leegacy.sooji.africaradio.ViewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leegacy.sooji.africaradio.Models.ProfileRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

import java.io.File;

/**
 * Created by soo-ji on 16-04-11.
 */
public class ProfileViewHolder extends RowViewHolder implements View.OnClickListener {
    private static final String PROFILE_PIC = "profile picture";
    private final TextView name;
    //    private final ImageView profilePicture;
    private final TextView bio;
    private final RelativeLayout followButtonContainer;
    private final FrameLayout followButton;
    private final FrameLayout messageButton;
    private final Firebase ref;
    private final FirebaseStorage storage;
    private final ImageView profilePicImage;
    private final Firebase rootfollowingRef;
    private final Firebase rootfollowerRef;
    private final TextView numFollowers;
    private final TextView numFollowing;
    private Firebase followingRef;
    private ProfileRowModel model;
    private String uid;
    private String otherUid;
    private boolean refset = false;
    private TextView followButtonText;
    private File localFile;
    private LruCache<String, Bitmap> mMemoryCache;
    private Firebase followerRef;


    public ProfileViewHolder(View itemView) {
        super(itemView);
        ref = new Firebase("https://blazing-inferno-7470.firebaseio.com/");
        rootfollowingRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog/following");
        rootfollowerRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog/follower");
        storage = FirebaseStorage.getInstance();
        authUid();
        name = (TextView) itemView.findViewById(R.id.userID);
//        profilePicture = (ImageView) itemView.findViewById(R.id.profilePicture);
        followButtonContainer = (RelativeLayout) itemView.findViewById(R.id.followButtonContainer);
        followButton = (FrameLayout) itemView.findViewById(R.id.followButton);
        followButtonText = (TextView) itemView.findViewById(R.id.followButtonText);
        followButton.setEnabled(false);
        followButton.setOnClickListener(this);

        numFollowers = (TextView) itemView.findViewById(R.id.numFollowers);
        numFollowing = (TextView) itemView.findViewById(R.id.numFollowing);
        messageButton = (FrameLayout) itemView.findViewById(R.id.messageButton);
        messageButton.setOnClickListener(this);
        bio = (TextView) itemView.findViewById(R.id.bio);
        profilePicImage = (ImageView) itemView.findViewById(R.id.profilePicture);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
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

//    private void uploadImage(){
//        // Get the data from an ImageView as bytes
//        profilePicImage.setDrawingCacheEnabled(true);
//        profilePicImage.buildDrawingCache();
//        Bitmap bitmap = profilePicImage.getDrawingCache();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        UploadTask uploadTask = mountainsRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                downloadImage();
//            }
//        });
//    }

    private void getMyProfilePicture(){
        Bitmap myBitmap = getBitmapFromMemCache(PROFILE_PIC);
        if(myBitmap!=null){
            // Create the RoundedBitmapDrawable.
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(itemView.getResources(), myBitmap);
            roundDrawable.setCircular(true);

            // Apply it to an ImageView.

            profilePicImage.setImageDrawable(roundDrawable);
        }else{
            downloadImageToCache(uid);
        }
    }

    @Override
    public void update(RowModel rowModel) {
        model = (ProfileRowModel) rowModel;
        name.setText(model.getFirstName() + " "+ model.getLastName());
        if(model.getOtherUid()!=null){
            otherUid = model.getOtherUid();
            numFollowUpdate(otherUid);
            isFollowing(otherUid);

            downloadImageToCache(otherUid);
        }else{
            followButtonContainer.setVisibility(View.GONE);
            getMyProfilePicture();
            numFollowUpdate(uid);
        }


//        if (localFile != null){
//            if (localFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//
////                profilePicImage.setImageBitmap(myBitmap);
//
//
//                // Create the RoundedBitmapDrawable.
//                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(itemView.getResources(), myBitmap);
//                roundDrawable.setCircular(true);
//
//                // Apply it to an ImageView.
//
//                profilePicImage.setImageDrawable(roundDrawable);
//
//
//            }
//

        // Create a storage reference from our app


//        bio.setText(model.getBio());   //TODO: uncomment it later

    }

    private void numFollowUpdate(String id) {
        rootfollowingRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numFollowing.setText(String.valueOf((int) dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        rootfollowerRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numFollowers.setText(String.valueOf((int) dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void isFollowing(final String otherID) {
        rootfollowingRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot followingSnapshot : dataSnapshot.getChildren()) {

                    if (followingSnapshot.getValue().toString().equals(otherID)) {
                        Log.e("ppl I'm following ", followingSnapshot.getValue().toString());
                        //follow button is set to following
                        followButtonText.setText("following");
                        followButton.setEnabled(true);
                        followButton.setActivated(true);
                        followButtonContainer.setVisibility(View.VISIBLE);
                        return;
                    }

                }
                //follow button is set to follow
                followButton.setEnabled(true);
                followButton.setActivated(false);
                followButtonContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void downloadImageToCache(String user) {
        StorageReference storageRef = storage.getReferenceFromUrl("gs://blazing-inferno-7470.appspot.com");


        StorageReference profileRef = storageRef.child("profile_pictures/" + user + "/" + user + ".png");


//        try {
//            localFile = File.createTempFile("profile_image", "jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        saveMyProfile();
        profileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                addBitmapToMemoryCache(PROFILE_PIC, bmp);
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(itemView.getResources(), bmp);
                roundDrawable.setCircular(true);
                bmp.recycle();
                // Apply it to an ImageView.

                profilePicImage.setImageDrawable(roundDrawable);

            }
        });
//                // Local temp file has been created
//                if (localFile.exists()) {
//
//                    Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//
////                profilePicImage.setImageBitmap(myBitmap);
//
//
//                    // Create the RoundedBitmapDrawable.
//                    RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(itemView.getResources(), myBitmap);
//                    roundDrawable.setCircular(true);
//
//                    // Apply it to an ImageView.
//
//                    profilePicImage.setImageDrawable(roundDrawable);
//
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
    }

    private void saveMyProfile() {

    }


    public ProfileRowModel getModel() {
        return model;
    }

    public void setModel(ProfileRowModel model) {
        this.model = model;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageButton:
                Toast.makeText(itemView.getContext(), "Messaging not supported yet.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.followButton:
                //if user clicks follow on this user, then update database

                if (followButton.isActivated()) {
                    followingRef.removeValue(new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            followButton.setActivated(false);
                            followButtonText.setText("follow");
                        }
                    });
                    followerRef.removeValue(new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            //follower list updated
                        }
                    });

                } else {
                    followingRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog/following").child(uid).push();
                    followingRef.setValue(otherUid, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            followButton.setActivated(true);
                            followButtonText.setText("following");
                        }
                    });
                    followerRef = new Firebase("https://blazing-inferno-7470.firebaseio.com/android/saving-data/fireblog/follower").child(otherUid).push();
                    followerRef.setValue(uid, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            //follower list updated for the other user
                        }
                    });
                }
                break;
        }
    }

}
