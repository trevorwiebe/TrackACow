package com.trevorwiebe.trackacow.presentation.edit_drugs_given

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.presentation.edit_drugs_given.DrugsGivenRecyclerViewAdapter.DrugsGivenViewHolder
import java.text.NumberFormat
import java.util.*

class DrugsGivenRecyclerViewAdapter : RecyclerView.Adapter<DrugsGivenViewHolder>() {

    private val format = NumberFormat.getInstance(Locale.getDefault())
    private var drugsGivenList: List<DrugsGivenAndDrugModel> = emptyList()

    override fun getItemCount(): Int {
        return drugsGivenList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DrugsGivenViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_drug_given, viewGroup, false)
        return DrugsGivenViewHolder(view)
    }

    override fun onBindViewHolder(drugsGivenViewHolder: DrugsGivenViewHolder, position: Int) {
        val drugName = drugsGivenList[position].drugName
        val amountGivenStr = format.format(drugsGivenList[position].drugsGivenAmountGiven)
        val drugsGivenMessage = "$amountGivenStr units of $drugName"
        drugsGivenViewHolder.mDrugGiven.text = drugsGivenMessage
    }

    fun swapData(drugsGivenList: List<DrugsGivenAndDrugModel>) {
        this.drugsGivenList = drugsGivenList
        notifyDataSetChanged()
    }

    inner class DrugsGivenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDrugGiven: TextView

        init {
            mDrugGiven = view.findViewById(R.id.drug_given_name_and_amount)
        }
    }
}