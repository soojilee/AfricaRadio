package com.leegacy.sooji.africaradio.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leegacy.sooji.africaradio.Listeners.OnPlayDetailListener;
import com.leegacy.sooji.africaradio.R;

/**
 * Created by soo-ji on 16-06-23.
 */
public class PlayDetailFragment extends Fragment implements View.OnClickListener{
    private View root;
    private TextView backButton;
    private OnPlayDetailListener onPlayDetailListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_playdetail, null);
        backButton = (TextView) root.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backButton:
                onPlayDetailListener.removeFragment();
                break;
        }
    }

    public void setOnPlayDetailListener(OnPlayDetailListener onPlayDetailListener) {
        this.onPlayDetailListener = onPlayDetailListener;
    }
}
