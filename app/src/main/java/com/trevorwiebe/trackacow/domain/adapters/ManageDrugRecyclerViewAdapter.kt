package com.trevorwiebe.trackacow.domain.adapters

import android.content.Context
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.domain.adapters.ManageDrugRecyclerViewAdapter.ManageDrugViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.trevorwiebe.trackacow.R
import android.widget.TextView

class ManageDrugRecyclerViewAdapter(
    private var mDrugList: List<DrugModel>,
    private val mContext: Context
) : RecyclerView.Adapter<ManageDrugViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ManageDrugViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_drug_rv, viewGroup, false)
        return ManageDrugViewHolder(view)
    }

    override fun onBindViewHolder(manageDrugViewHolder: ManageDrugViewHolder, i: Int) {
        val (_, defaultAmount, _, drugName) = mDrugList[i]
        manageDrugViewHolder.mDrugName.text = drugName
        manageDrugViewHolder.mDefaultAmount.text = defaultAmount.toString()
    }

    override fun getItemCount(): Int {
        return mDrugList.size
    }

    fun swapData(newDrugList: List<DrugModel>) {
        mDrugList = newDrugList
        notifyDataSetChanged()
    }

    inner class ManageDrugViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDrugName: TextView = view.findViewById(R.id.time_drug_report_name)
        val mDefaultAmount: TextView = view.findViewById(R.id.manage_default_given)
    }
}