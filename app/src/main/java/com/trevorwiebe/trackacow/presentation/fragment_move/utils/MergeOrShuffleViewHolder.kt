package com.trevorwiebe.trackacow.presentation.fragment_move.utils

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R

class MergeOrShuffleViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.item_shuffle_merge_or_split)
    val mergeOrSplit: Button = view.findViewById(R.id.merge_or_split_btn)
}