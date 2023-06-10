package com.trevorwiebe.trackacow.presentation.fragment_move

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.DragHelper.ActionCompletionContract
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.LotViewHolder
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.PenViewHolder

class ShufflePenAndLotsAdapter : RecyclerView.Adapter<ViewHolder>(),
    ActionCompletionContract {

    private var objectList: MutableList<Any> = mutableListOf()
    private lateinit var context: Context
    private lateinit var touchHelper: ItemTouchHelper

    private lateinit var onItemShuffledCallback: (lotId: String, penId: String) -> Unit
    private lateinit var onLotsMerged: (lotIdList: List<String>) -> Unit

    fun onItemShuffled(callback: (lotId: String, penId: String) -> Unit) {
        onItemShuffledCallback = callback
    }

    fun onLotsMerged(callback: (lotIdList: List<String>) -> Unit) {
        onLotsMerged = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        return when (viewType) {
            LOT_NAME -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_shuffle_lot, parent, false)
                LotViewHolder(view)
            }

            PEN_NAME -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_shuffle_pen, parent, false)
                PenViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_shuffle_lot, parent, false)
                LotViewHolder(view)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modelObject = objectList[position]
        when (modelObject) {
            is LotModel -> {
                val lotName = modelObject.lotName
                val lotViewHolder = holder as LotViewHolder
                lotViewHolder.lotModel = modelObject
                lotViewHolder.lotName.text = lotName
                lotViewHolder.reorder.setOnTouchListener { v, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder)
                    }
                    true
                }
            }

            is PenModel -> {
                val penName = modelObject.penName
                val penText = "Pen: $penName"
                val penViewHolder = holder as PenViewHolder
                penViewHolder.penName.text = penText
                penViewHolder.twoLotsUnderOnePen.visibility = View.INVISIBLE
                penViewHolder.mergeLotsBtn.visibility = View.GONE
                penViewHolder.mergeLotsBtn.setOnClickListener {
                    val lotNameList =
                        penViewHolder.lotsToMergeList.joinToString(", ") { it.lotName }
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Caution")
                        .setMessage("Are you sure you want to merge these lots? - $lotNameList -  This action cannot be undone.")
                        .setPositiveButton("Merge Lots") { dialog, which ->
                            val lotIdList =
                                penViewHolder.lotsToMergeList.map { it.lotCloudDatabaseId }
                            onLotsMerged(lotIdList)
                        }
                        .setNegativeButton("Cancel") { _, _ -> }
                        .show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return objectList.size
    }

    override fun getItemViewType(position: Int): Int {
        val modelObject = objectList[position]
        if (modelObject is LotModel) return LOT_NAME
        return PEN_NAME
    }

    fun setShuffleObjectList(objectList: MutableList<Any>, context: Context) {
        this.objectList = objectList
        this.context = context
        notifyDataSetChanged()
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int, viewHolder: ViewHolder) {
        if (newPosition >= 1) {
            val lotShuffleObject = objectList[oldPosition]
            objectList.removeAt(oldPosition)
            objectList.add(newPosition, lotShuffleObject)
            notifyItemMoved(oldPosition, newPosition)
        }
    }

    override fun onClearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        val selectedLotPosition = viewHolder.adapterPosition
        val lotModel = objectList[selectedLotPosition] as LotModel
        val lotId = lotModel.lotCloudDatabaseId
        val shuffleObjectNeighbors = getShuffleObjectsBeside(selectedLotPosition)

        if (shuffleObjectNeighbors[0] is PenModel && shuffleObjectNeighbors[1] is PenModel) {
            val penModel = shuffleObjectNeighbors[0] as PenModel
            if (penModel.penCloudDatabaseId != "") {
                onItemShuffledCallback(lotId, penModel.penCloudDatabaseId!!)
            }
        }
        setMergeButtons(recyclerView)
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    private fun getShuffleObjectsBeside(position: Int): List<Any?> {
        val topShuffleObject: Any? =
            if (position == 0) null else objectList[position - 1]
        val bottomShuffleObject: Any? =
            if (position + 1 == objectList.size) null else objectList[position + 1]
        return listOf(topShuffleObject, bottomShuffleObject)
    }

    private fun setMergeButtons(recyclerView: RecyclerView) {
        var lastPenViewHolder: PenViewHolder? = null
        val lotViewHolderList = mutableListOf<LotViewHolder>()
        for (i in 0 until recyclerView.childCount) {
            val selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(i)
            if (selectedViewHolder is PenViewHolder) {
                lastPenViewHolder = selectedViewHolder
                lotViewHolderList.clear()
            } else if (selectedViewHolder is LotViewHolder) {

                lotViewHolderList.add(selectedViewHolder)

                // need to show a merge button
                if (lotViewHolderList.size > 1) {
                    lastPenViewHolder?.twoLotsUnderOnePen?.visibility = View.VISIBLE
//                    lastPenViewHolder?.mergeLotsBtn?.visibility = View.VISIBLE
                    lastPenViewHolder?.setLotsToMerge(lotViewHolderList.map { it.lotModel })
                } else {
//                    lastPenViewHolder?.mergeLotsBtn?.visibility = View.INVISIBLE
                    lastPenViewHolder?.twoLotsUnderOnePen?.visibility = View.INVISIBLE
                }
            }
        }
    }

    companion object {
        const val LOT_NAME = 1
        const val PEN_NAME = 2
    }
}