package com.trevorwiebe.trackacow.presentation.fragment_medicate

import android.content.Context
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.trevorwiebe.trackacow.R
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel

class PenRecyclerViewAdapter(
    var mPenAndLotModel: List<PenAndLotModel>,
    var mShouldShowLots: Boolean = true,
    private val mContext: Context
) : RecyclerView.Adapter<PenRecyclerViewAdapter.PenViewHolder>() {

    override fun getItemCount(): Int {
        return mPenAndLotModel.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PenViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_pen, viewGroup, false)
        return PenViewHolder(view)
    }

    override fun onBindViewHolder(penViewHolder: PenViewHolder, i: Int) {
        val penAndLotModel = mPenAndLotModel[i]
        penViewHolder.mPen.text = penAndLotModel.penName

        if(mShouldShowLots) {
            val lotName = penAndLotModel.lotName

            if (lotName != null) {
                penViewHolder.mLotNames.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.colorAccent
                    )
                )
                penViewHolder.mLotNames.setTypeface(null, Typeface.BOLD)
                penViewHolder.mLotNames.text = lotName
            } else {
                penViewHolder.mLotNames.setTypeface(null, Typeface.NORMAL)
                penViewHolder.mLotNames.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        android.R.color.darker_gray
                    )
                )
                penViewHolder.mLotNames.text = mContext.getString(R.string.work_no_cattle_in_this_pen)
            }
        }else{
            penViewHolder.mLotNames.visibility = View.INVISIBLE
        }
    }

    fun swapData(penAndLotModel: List<PenAndLotModel>) {
        mPenAndLotModel = penAndLotModel
        notifyDataSetChanged()
    }

    inner class PenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mPen: TextView = view.findViewById(R.id.pen)
        val mLotNames: TextView = view.findViewById(R.id.lot_names)
    }
}