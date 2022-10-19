package com.trevorwiebe.trackacow.domain.adapters

import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.domain.adapters.ReportsRecyclerViewAdapter.ReportsViewHolder
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.trevorwiebe.trackacow.R
import android.widget.TextView

class ReportsRecyclerViewAdapter : RecyclerView.Adapter<ReportsViewHolder>() {

    private var mLotModel: List<LotModel> = emptyList()

    override fun getItemCount(): Int { return mLotModel.size }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ReportsViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_reports, viewGroup, false)
        return ReportsViewHolder(view)
    }

    override fun onBindViewHolder(reportsViewHolder: ReportsViewHolder, i: Int) {
        val lotEntity: LotModel = mLotModel[i]
        val lotName = lotEntity.lotName
        reportsViewHolder.mLotName.text = lotName
    }

    fun swapLotData(lotModels: List<LotModel>) {
        mLotModel = lotModels
        notifyDataSetChanged()
    }

    inner class ReportsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mLotName: TextView = view.findViewById(R.id.reports_lot_name)
    }
}