package com.leegacy.sooji.africaradio;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leegacy.sooji.africaradio.Listeners.OnHomeRowListener;
import com.leegacy.sooji.africaradio.Listeners.OnPlaylistListener;
import com.leegacy.sooji.africaradio.Models.CommentRowModel;
import com.leegacy.sooji.africaradio.Models.ExploreRowModel;
import com.leegacy.sooji.africaradio.Models.HomeRowModel;
import com.leegacy.sooji.africaradio.Models.PlaylistRowModel;
import com.leegacy.sooji.africaradio.Models.ProfileRowModel;
import com.leegacy.sooji.africaradio.Models.RowModel;
import com.leegacy.sooji.africaradio.ViewHolder.CommentViewHolder;
import com.leegacy.sooji.africaradio.ViewHolder.ExploreViewHolder;
import com.leegacy.sooji.africaradio.ViewHolder.HomeViewHolder;
import com.leegacy.sooji.africaradio.ViewHolder.PlaylistViewHolder;
import com.leegacy.sooji.africaradio.ViewHolder.ProfileViewHolder;
import com.leegacy.sooji.africaradio.ViewHolder.RowViewHolder;

import java.util.List;

/**
 * Created by soo-ji on 16-04-23.
 */
public class MyAdapter extends RecyclerView.Adapter{
    private static final String TAG = "MY ADAPTER";
    private List<RowModel> rowModels;
    private OnPlaylistListener onPlaylistListener;
    private OnHomeRowListener onHomeRowListener;

    public void setRowModels(List<RowModel> rowModels) {
        this.rowModels = rowModels;
        notifyDataSetChanged();
    }

    public void setOnPlaylistListener(OnPlaylistListener onPlaylistListener) {
        this.onPlaylistListener = onPlaylistListener;
    }

    public void setOnHomeRowListener(OnHomeRowListener onHomeRowListener) {
        this.onHomeRowListener = onHomeRowListener;
    }

    enum ViewTypes{
        PROFILE_ROW_MODEL,
        COMMENT_ROW_MODEL,
        PLAYLIST_ROW_MODEL,
        EXPLORE_ROW_MODEL,
        HOME_ROW_MODEL
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ViewTypes.PROFILE_ROW_MODEL.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_row_view,parent, false);
            return new ProfileViewHolder(v);
        }else if(viewType == ViewTypes.COMMENT_ROW_MODEL.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_view,parent,false);
            return new CommentViewHolder(v);
        }else if(viewType == ViewTypes.PLAYLIST_ROW_MODEL.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_row_view,parent,false);
            PlaylistViewHolder playlistViewHolder = new PlaylistViewHolder(v);
            playlistViewHolder.setOnPlaylistListener(onPlaylistListener);
            return playlistViewHolder;
        }else if(viewType == ViewTypes.EXPLORE_ROW_MODEL.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_row_view,parent,false);
            return new ExploreViewHolder(v);
        }else if(viewType == ViewTypes.HOME_ROW_MODEL.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_view,parent,false);
            HomeViewHolder homeViewHolder = new HomeViewHolder(v);
            homeViewHolder.setOnHomeRowListener(onHomeRowListener);
            return homeViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowViewHolder) holder).update(rowModels.get(position));
    }

    @Override
    public int getItemCount() {
        if(rowModels ==null){
            return 0;
        }
        return rowModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        RowModel rm = rowModels.get(position);
        if(rm instanceof ProfileRowModel){
            return ViewTypes.PROFILE_ROW_MODEL.ordinal();
        }else if(rm instanceof CommentRowModel){
            return ViewTypes.COMMENT_ROW_MODEL.ordinal();
        }else if(rm instanceof PlaylistRowModel){
            return ViewTypes.PLAYLIST_ROW_MODEL.ordinal();
        }else if(rm instanceof ExploreRowModel) {
            return ViewTypes.EXPLORE_ROW_MODEL.ordinal();
        }else if(rm instanceof HomeRowModel){
            return ViewTypes.HOME_ROW_MODEL.ordinal();
        }

        Log.e(TAG, "Critical Error: Row item view type was not recognized");
        return -1;
    }
}
