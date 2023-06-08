package com.trevorwiebe.trackacow.presentation.fragment_move.utils

import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

class PenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
    var penName: TextView
    var reorder: ImageView
    var mergeLotsBtn: Button

    private lateinit var lotsToMerge: List<LotModel>

    init {
        penName = itemView.findViewById(R.id.shuffle_pen_tv)
        reorder = itemView.findViewById(R.id.pen_reorder)
        mergeLotsBtn = itemView.findViewById(R.id.merge_lots_btn)
        mergeLotsBtn.setOnClickListener(this)
    }

    fun setLotsToMerge(lotList: List<LotModel>) {
        lotsToMerge = lotList
    }

    override fun onClick(view: View) {
        MaterialAlertDialogBuilder(view.context)
            .setTitle("Caution")
            .setMessage("Are you sure you want to merge these lots?.  This action cannot be undone.")
            .setPositiveButton("Merge Lots") { dialog, which ->

            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }
}