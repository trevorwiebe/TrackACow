package com.trevorwiebe.trackacow.presentation.fragment_move.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

class PenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var penName: TextView
    var reorder: ImageView
    var twoLotsUnderOnePen: TextView
    var mergeLotsBtn: Button

    lateinit var lotsToMergeList: List<LotModel>

    init {
        penName = itemView.findViewById(R.id.shuffle_pen_tv)
        reorder = itemView.findViewById(R.id.pen_reorder)
        twoLotsUnderOnePen = itemView.findViewById(R.id.two_lots_under_one_pen_error)
        mergeLotsBtn = itemView.findViewById(R.id.merge_lots_btn)
    }

    fun setLotsToMerge(lotList: List<LotModel>) {
        lotsToMergeList = lotList
    }
}