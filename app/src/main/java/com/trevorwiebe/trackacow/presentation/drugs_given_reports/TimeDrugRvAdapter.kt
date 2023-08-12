package com.trevorwiebe.trackacow.presentation.drugs_given_reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.presentation.drugs_given_reports.TimeDrugRvAdapter.DayViewHolder

class TimeDrugRvAdapter : RecyclerView.Adapter<DayViewHolder>() {

    private var drugsGivenList: List<DrugsGivenAndDrugModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_drug_report, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val drugModel = drugsGivenList[position]
        var drugName = drugModel.drugName
        val amountGiven = drugModel.drugsGivenAmountGiven
        if (drugName == "") drugName = "[drug_unavailable]"
        holder.mDrugName.text = drugName
        holder.mDrugAmountGiven.text = "$amountGiven units"
    }

    override fun getItemCount(): Int {
        return drugsGivenList.size
    }

    fun swapData(drugsGivenModels: List<DrugsGivenAndDrugModel>) {
        drugsGivenList = drugsGivenModels
        notifyDataSetChanged()
    }

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDrugName: TextView
        val mDrugAmountGiven: TextView

        init {
            mDrugName = view.findViewById(R.id.time_drug_report_name)
            mDrugAmountGiven = view.findViewById(R.id.time_drug_report_amount_given)
        }
    }
}