package com.trevorwiebe.trackacow.presentation.fragment_move.utils

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.trevorwiebe.trackacow.R

class LotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lotName: TextView
    var reorder: ImageView
    var lotMoveViewBottom: View

    init {
        lotName = view.findViewById(R.id.item_lot_name)
        reorder = view.findViewById(R.id.lot_reorder)
        lotMoveViewBottom = view.findViewById(R.id.lot_move_view)
    }
}