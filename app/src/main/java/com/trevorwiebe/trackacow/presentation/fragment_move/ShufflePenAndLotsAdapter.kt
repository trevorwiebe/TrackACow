package com.trevorwiebe.trackacow.presentation.fragment_move

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.DragHelper.ActionCompletionContract
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.ShuffleObject
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.ViewGroup
import android.view.LayoutInflater
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.utils.LotViewHolder
import android.view.MotionEvent
import android.view.View
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot.UpdateHoldingLot
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.UpdateLotWithNewPenId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.PenViewHolder
import com.trevorwiebe.trackacow.domain.utils.Utility
import java.util.ArrayList

class ShufflePenAndLotsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ActionCompletionContract {

    private var shuffleObjects: ArrayList<ShuffleObject> = ArrayList()
    private lateinit var context: Context
    private lateinit var touchHelper: ItemTouchHelper

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        val shuffleObject = shuffleObjects[position]
        if (itemViewType == LOT_NAME) {
            val lotName = shuffleObject.name
            val lotViewHolder = holder as LotViewHolder
            lotViewHolder.lotName.text = lotName
            lotViewHolder.reorder.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchHelper!!.startDrag(holder)
                    lotViewHolder.lotMoveView.visibility = View.VISIBLE
                    true
                } else {
                    lotViewHolder.lotMoveView.visibility = View.INVISIBLE
                    true
                }
            }
        } else {
            val penName = shuffleObject.name
            val penText = "Pen: $penName"
            val penViewHolder = holder as PenViewHolder
            penViewHolder.penName.text = penText
        }
    }

    override fun getItemCount(): Int {
        return shuffleObjects.size
    }

    override fun getItemViewType(position: Int): Int {
        val shuffleObject = shuffleObjects[position]
        return shuffleObject.type
    }

    fun setAdapterVariables(shuffleObjects: MutableList<ShuffleObject>, context: Context) {
        this.shuffleObjects = ArrayList(shuffleObjects)
        this.context = context
        notifyDataSetChanged()
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        if (newPosition != 0) {
            val lotShuffleObject = shuffleObjects[oldPosition]
            val lotId = lotShuffleObject.id
            shuffleObjects.removeAt(oldPosition)
            shuffleObjects.add(newPosition, lotShuffleObject)
            notifyItemMoved(oldPosition, newPosition)
            val penShuffleObject = findNearestPen(newPosition)
            if (penShuffleObject != null) {
                val penId = penShuffleObject.id
                if (Utility.haveNetworkConnection(context)) {
                    val baseRef = Constants.BASE_REFERENCE.child(Constants.LOTS).child(lotId)
                        .child("lotPenId")
                    baseRef.setValue(penId)
                } else {
                    Utility.setNewDataToUpload(context, true)
                    UpdateHoldingLot(lotId, penId).execute(context)
                }
                UpdateLotWithNewPenId(lotId, penId).execute(context)
            }
        }
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    private fun findNearestPen(position: Int): ShuffleObject? {
        var i = position
        while (i < shuffleObjects.size) {
            val shuffleObject = shuffleObjects[i]
            if (shuffleObject.type == PEN_NAME) {
                return shuffleObject
            }
            i--
        }
        return null
    }

    companion object {
        private const val TAG = "ShufflePenAndLotsAdapte"
        const val LOT_NAME = 1
        const val PEN_NAME = 2
    }
}