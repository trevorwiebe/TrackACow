package com.trevorwiebe.trackacow.domain.utils;

import androidx.recyclerview.widget.RecyclerView;
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