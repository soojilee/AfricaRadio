package com.leegacy.sooji.africaradio.ViewHolder;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leegacy.sooji.africaradio.Models.ProfileRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-04-11.
 */
public class ProfileViewHolder extends RowViewHolder {
    private final TextView name;
    private final ImageView profilePicture;
    private final TextView bio;
    private ProfileRowModel model;
    public ProfileViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.userID);
        profilePicture = (ImageView) itemView.findViewById(R.id.profilePicture);
        bio = (TextView) itemView.findViewById(R.id.bio);
    }

    @Override
    public void update(RowModel rowModel) {
        model = (ProfileRowModel) rowModel;
        name.setText(model.getFirstName()+model.getLastName());
        if(model.getProfilePicture() == null){
            Resources res = itemView.getResources();
            Drawable drawable = res.getDrawable(R.drawable.profile);
            profilePicture.setImageDrawable(drawable);
        }else{
            //retrieve image from firebase
        }
        bio.setText(model.getBio());
    }

    public ProfileRowModel getModel() {
        return model;
    }

    public void setModel(ProfileRowModel model) {
        this.model = model;
    }
}
