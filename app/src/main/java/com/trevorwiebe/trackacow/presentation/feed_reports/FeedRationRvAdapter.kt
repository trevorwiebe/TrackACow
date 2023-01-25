package com.trevorwiebe.trackacow.presentation.feed_reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

class FeedRationRvAdapter :
    RecyclerView.Adapter<FeedRationRvAdapter.FeedRationViewHolder>() {

    private var rationsList: List<RationModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRationViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_feed_ration_item, parent, false)
        return FeedRationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rationsList.size
    }

    override fun onBindViewHolder(holder: FeedRationViewHolder, position: Int) {
        holder.ration.text = rationsList[position].rationName
    }

    fun setRationsList(rationsList: List<RationModel>) {
        this.rationsList = rationsList
        notifyDataSetChanged()
    }

    class FeedRationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ration: Button

        init {
            ration = view.findViewById(R.id.feed_ration_tv)
        }
    }
}