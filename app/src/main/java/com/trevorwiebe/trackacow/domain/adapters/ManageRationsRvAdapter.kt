package com.trevorwiebe.trackacow.domain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

class ManageRationsRvAdapter() :
    RecyclerView.Adapter<ManageRationsRvAdapter.ManageRationViewHolder>() {

    private var rationsList: List<RationModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageRationViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_manage_rations, parent, false)
        return ManageRationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageRationViewHolder, position: Int) {
        holder.ration.text = rationsList[position].rationName
    }

    override fun getItemCount(): Int {
        return rationsList.size
    }

    fun setRationsList(rationsList: List<RationModel>){
        this.rationsList = rationsList
        notifyDataSetChanged()
    }

    class ManageRationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ration: TextView
        init {
            ration = view.findViewById(R.id.item_manage_ration_tv)
        }
    }
}