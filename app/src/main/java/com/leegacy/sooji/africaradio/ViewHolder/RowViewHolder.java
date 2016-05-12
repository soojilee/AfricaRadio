package com.leegacy.sooji.africaradio.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.leegacy.sooji.africaradio.Models.RowModel;

/**
 * Created by soo-ji on 16-04-11.
 */
public abstract class RowViewHolder extends RecyclerView.ViewHolder{
    public RowViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void update(RowModel rowModel);
}
