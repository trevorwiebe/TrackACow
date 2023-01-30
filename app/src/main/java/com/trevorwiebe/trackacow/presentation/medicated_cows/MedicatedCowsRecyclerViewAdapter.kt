package com.trevorwiebe.trackacow.presentation.medicated_cows

import android.content.Context
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.presentation.medicated_cows.MedicatedCowsRecyclerViewAdapter.TrackCowViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.trevorwiebe.trackacow.R
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel

class MedicatedCowsRecyclerViewAdapter(
    private var cowUiModelList: List<CowUiModel> = emptyList(),
    private val mContext: Context

) : RecyclerView.Adapter<TrackCowViewHolder>() {

    override fun getItemCount(): Int {
        return cowUiModelList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TrackCowViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.list_medicated_cows, viewGroup, false)
        return TrackCowViewHolder(view)
    }

    override fun onBindViewHolder(trackCowViewHolder: TrackCowViewHolder, position: Int) {

        val cowUiModel = cowUiModelList[position]

        val tagNumber = cowUiModel.cowModel.tagNumber.toString()
        trackCowViewHolder.mDate.text = Utility.convertMillisToFriendlyDate(cowUiModel.cowModel.date)
        trackCowViewHolder.mTagNumber.text = tagNumber

        if (cowUiModel.cowModel.notes.isNullOrEmpty()) {
            trackCowViewHolder.mNotes.visibility = View.GONE
        } else {
            trackCowViewHolder.mNotes.visibility = View.VISIBLE
            trackCowViewHolder.mNotes.text =
                mContext.resources.getString(R.string.notes_more, cowUiModel.cowModel.notes)
        }
        if (cowUiModel.cowModel.isAlive == 1) {

            trackCowViewHolder.mTagNumber.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    android.R.color.black
                )
            )
            var message = ""

            if (cowUiModel.drugsGivenAndDrugModelList.isEmpty()) {
                trackCowViewHolder.mDrugsGiven.text =
                    mContext.resources.getString(R.string.no_drugs_given)
                trackCowViewHolder.mDrugsGiven.typeface =
                    Typeface.defaultFromStyle(Typeface.ITALIC)
            } else {
                trackCowViewHolder.mDrugsGiven.typeface = Typeface.DEFAULT
                for (q in cowUiModel.drugsGivenAndDrugModelList.indices) {
                    val drugsGivenAndDrugModel = cowUiModel.drugsGivenAndDrugModelList[q]
                    val drugName = drugsGivenAndDrugModel.drugName
                    val amountGiven = drugsGivenAndDrugModel.drugsGivenAmountGiven
                    message += mContext.resources.getQuantityString(
                        R.plurals.drug_unit,
                        amountGiven,
                        amountGiven,
                        drugName
                    ) + "\n"
                }
                message = message.trimIndent()
                trackCowViewHolder.mDrugsGiven.text = message
            }
        } else {
            trackCowViewHolder.mTagNumber.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.redText
                )
            )
            trackCowViewHolder.mDrugsGiven.text =
                mContext.resources.getString(R.string.this_cow_is_dead)
        }
    }

    fun swapData(newCowUiModelList: List<CowUiModel>) {
        cowUiModelList = newCowUiModelList
        notifyDataSetChanged()
    }

    inner class TrackCowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mTagNumber: TextView = view.findViewById(R.id.medicated_cow_tag_number)
        val mDate: TextView = view.findViewById(R.id.date_treated_on)
        val mDrugsGiven: TextView = view.findViewById(R.id.medication_given)
        val mNotes: TextView = view.findViewById(R.id.notes)
    }

}