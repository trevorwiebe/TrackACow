package com.trevorwiebe.trackacow.presentation.fragment_move

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.DragHelper.ActionCompletionContract
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.ShuffleObject
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.ViewGroup
import android.view.LayoutInflater
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.LotViewHolder
import android.view.MotionEvent
import android.view.View
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.PenViewHolder
import java.util.ArrayList

class ShufflePenAndLotsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ActionCompletionContract {

    private var shuffleObjects: ArrayList<ShuffleObject> = ArrayList()
    private lateinit var context: Context
    private lateinit var touchHelper: ItemTouchHelper

    private lateinit var onItemShuffledCallback: (lotModel: LotModel) -> Unit
    private lateinit var onDragStartCallback: () -> Unit
    private lateinit var onDragStopCallback: () -> Unit

    fun onItemShuffled(callback: (lotModel: LotModel) -> Unit ){
        onItemShuffledCallback = callback
    }

    fun onDragStart(dragStartCallback: () -> Unit){
        onDragStartCallback = dragStartCallback
    }

    fun onDragStop(dragStopCallback: () -> Unit){
        onDragStopCallback = dragStopCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val shuffleObject = shuffleObjects[position]
        when(shuffleObject.type){
            LOT_NAME -> {
                val lotName = shuffleObject.name
                val lotViewHolder = holder as LotViewHolder
                lotViewHolder.lotName.text = lotName
                lotViewHolder.reorder.setOnTouchListener { v, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder)
                        onDragStartCallback()
                    }
                    true
                }
            }
            PEN_NAME -> {
                val penName = shuffleObject.name
                val penText = "Pen: $penName"
                val penViewHolder = holder as PenViewHolder
                penViewHolder.penName.text = penText
                penViewHolder.tooManyLots.visibility = View.GONE
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

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        if (newPosition >= 1) {
            val lotShuffleObject = shuffleObjects[oldPosition]
            val lotId = lotShuffleObject.id
            shuffleObjects.removeAt(oldPosition)
            shuffleObjects.add(newPosition, lotShuffleObject)
            notifyItemMoved(oldPosition, newPosition)

            val shuffleObjectNeighbors = getShuffleObjectsBeside(newPosition)

            if (shuffleObjectNeighbors[0] != null && shuffleObjectNeighbors[0]!!.type == LOT_NAME) {
                Log.d(TAG, "onViewMoved: two items under one pen (below lot)")
            }

            if(shuffleObjectNeighbors[1] != null && shuffleObjectNeighbors[1]!!.type == LOT_NAME){
                Log.d(TAG, "onViewMoved: two items under one pen (above lot)")
            }

            if (shuffleObjectNeighbors[0] != null && shuffleObjectNeighbors[0]!!.type == PEN_NAME) {
//                 update database with new order
                val penId = shuffleObjectNeighbors[0]!!.id
                onItemShuffledCallback(LotModel(
                    0,
                    "",
                    lotId,
                    "",
                    "",
                    0L,
                    0,
                    0,
                    penId
                )
                )
            }
        }
    }

    override fun onClearView(viewHolder: RecyclerView.ViewHolder) {
        if(viewHolder.itemViewType == LOT_NAME) {
            onDragStopCallback()
        }
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    private fun getShuffleObjectsBeside(position: Int): List<ShuffleObject?> {
        val topShuffleObject: ShuffleObject? = if(position == 0) null else shuffleObjects[position-1]
        val bottomShuffleObject: ShuffleObject? = if(position+1 == shuffleObjects.size) null else shuffleObjects[position+1]
        return listOf(topShuffleObject, bottomShuffleObject)
    }

    companion object {
        private const val TAG = "ShufflePenAndLotsAdapte"
        const val LOT_NAME = 1
        const val PEN_NAME = 2
    }
}