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
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.DragHelper.ActionCompletionContract
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.LotViewHolder
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.PenViewHolder
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.ShuffleObject

class ShufflePenAndLotsAdapter : RecyclerView.Adapter<ViewHolder>(),
    ActionCompletionContract {

    private var shuffleObjects: ArrayList<ShuffleObject> = ArrayList()
    private lateinit var context: Context
    private lateinit var touchHelper: ItemTouchHelper

    private lateinit var onItemShuffledCallback: (lotId: String, penId: String) -> Unit

    fun onItemShuffled(callback: (lotId: String, penId: String) -> Unit) {
        onItemShuffledCallback = callback
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
        val shuffleObject = shuffleObjects[position]
        when (shuffleObject.type) {
            LOT_NAME -> {
                val lotName = shuffleObject.name
                val lotViewHolder = holder as LotViewHolder
                lotViewHolder.lotName.text = lotName
                lotViewHolder.reorder.setOnTouchListener { v, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder)
                    }
                    true
                }
            }
            PEN_NAME -> {
                val penName = shuffleObject.name
                val penText = "Pen: $penName"
                val penViewHolder = holder as PenViewHolder
                penViewHolder.penName.text = penText
                penViewHolder.mergeLotsBtn.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return shuffleObjects.size
    }

    override fun getItemViewType(position: Int): Int {
        val shuffleObject = shuffleObjects[position]
        return shuffleObject.type
    }

    fun setShuffleObjectList(shuffleObjects: MutableList<ShuffleObject>, context: Context) {
        this.shuffleObjects = ArrayList(shuffleObjects)
        this.context = context
        notifyDataSetChanged()
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int, viewHolder: ViewHolder) {
        if (newPosition >= 1) {
            val lotShuffleObject = shuffleObjects[oldPosition]
            shuffleObjects.removeAt(oldPosition)
            shuffleObjects.add(newPosition, lotShuffleObject)
            notifyItemMoved(oldPosition, newPosition)
        }
    }

    override fun onClearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        val selectedLotPosition = viewHolder.adapterPosition
        val lotId = shuffleObjects[selectedLotPosition].id
        val shuffleObjectNeighbors = getShuffleObjectsBeside(selectedLotPosition)

        if (shuffleObjectNeighbors[0]?.type == PEN_NAME && shuffleObjectNeighbors[1]?.type == PEN_NAME) {
            val penId = shuffleObjectNeighbors[0]?.id
            if (penId != null) {
                onItemShuffledCallback(lotId, penId)
            }
        }
        setMergeButtons(recyclerView)
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    private fun getShuffleObjectsBeside(position: Int): List<ShuffleObject?> {
        val topShuffleObject: ShuffleObject? =
            if (position == 0) null else shuffleObjects[position - 1]
        val bottomShuffleObject: ShuffleObject? =
            if (position + 1 == shuffleObjects.size) null else shuffleObjects[position + 1]
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
                // need to show a merge button
                if (lotViewHolderList.isNotEmpty()) {
                    lastPenViewHolder?.mergeLotsBtn?.visibility = View.VISIBLE
                } else {
                    lastPenViewHolder?.mergeLotsBtn?.visibility = View.INVISIBLE
                }
                lotViewHolderList.add(selectedViewHolder)
            }
        }
    }

    companion object {
        const val LOT_NAME = 1
        const val PEN_NAME = 2
    }
}