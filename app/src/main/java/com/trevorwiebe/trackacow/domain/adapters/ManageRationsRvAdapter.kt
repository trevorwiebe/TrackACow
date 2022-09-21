package com.trevorwiebe.trackacow.domain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.data.db.entities.RationEntity

class ManageRationsRvAdapter(private val rationsList: List<RationEntity>) :
    RecyclerView.Adapter<ManageRationsRvAdapter.ManageRationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageRationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_manage_rations, parent, false)
        return ManageRationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageRationViewHolder, position: Int) {
        val ration = rationsList.get(position)
        holder.ration.text = ration.rationName
    }

    override fun getItemCount(): Int {
        return rationsList.size
    }

    class ManageRationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ration: TextView
        init {
            ration = view.findViewById(R.id.item_manage_ration_tv)
        }
    }
}