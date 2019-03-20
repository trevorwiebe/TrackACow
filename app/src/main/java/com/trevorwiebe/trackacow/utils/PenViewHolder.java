package com.trevorwiebe.trackacow.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;


public class PenViewHolder extends RecyclerView.ViewHolder {

    public TextView penName;
    public ImageView reorder;

    public PenViewHolder(View itemView) {
        super(itemView);
        penName = itemView.findViewById(R.id.shuffle_pen_tv);
        reorder = itemView.findViewById(R.id.pen_reorder);
    }

}