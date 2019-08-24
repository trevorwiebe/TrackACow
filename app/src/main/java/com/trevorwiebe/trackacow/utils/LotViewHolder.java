package com.trevorwiebe.trackacow.utils;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;


public class LotViewHolder extends RecyclerView.ViewHolder {

    public TextView lotName;
    public ImageView reorder;
    public View lotMoveView;

    public LotViewHolder(View view) {
        super(view);

        lotName = view.findViewById(R.id.item_lot_name);
        reorder = view.findViewById(R.id.lot_reorder);
        lotMoveView = view.findViewById(R.id.lot_move_view);
    }
}
