package com.trevorwiebe.trackacow.presentation.fragment_move.utils

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.trevorwiebe.trackacow.R

class PenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var penName: TextView
    var reorder: ImageView
    var tooManyLots: TextView

    init {
        penName = itemView.findViewById(R.id.shuffle_pen_tv)
        reorder = itemView.findViewById(R.id.pen_reorder)
        tooManyLots = itemView.findViewById(R.id.too_many_lots_tv)
    }
}