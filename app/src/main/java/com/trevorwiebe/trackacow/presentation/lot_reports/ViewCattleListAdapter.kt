package com.trevorwiebe.trackacow.presentation.lot_reports

import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.presentation.lot_reports.ViewCattleListAdapter.ViewCattleViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.trevorwiebe.trackacow.R
import android.widget.TextView
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import java.text.NumberFormat
import java.util.*

class ViewCattleListAdapter : RecyclerView.Adapter<ViewCattleViewHolder>() {

    private var loadModels: List<LoadModel> = emptyList()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    override fun getItemCount(): Int {
        return loadModels.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewCattleViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_load_of_cattle, viewGroup, false)
        return ViewCattleViewHolder(view)
    }

    override fun onBindViewHolder(viewCattleViewHolder: ViewCattleViewHolder, i: Int) {

        val (_, headAdded, date, notes) = loadModels[i]

        val strDate = "Date: " + Utility.convertMillisToDate(date)
        val strHeadAddedStr = "Head added: " + numberFormat.format(headAdded.toLong())
        viewCattleViewHolder.mHeadAdded.text = strHeadAddedStr
        viewCattleViewHolder.mDate.text = strDate

        if (notes == null || notes.isEmpty()) {
            viewCattleViewHolder.mNotes.visibility = View.GONE
        } else {
            viewCattleViewHolder.mNotes.visibility = View.VISIBLE
            viewCattleViewHolder.mNotes.text = "Memo: $notes"
        }
    }

    fun setData(loadList: List<LoadModel>) {
        this.loadModels = loadList
        notifyDataSetChanged()
    }

    inner class ViewCattleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mHeadAdded: TextView = view.findViewById(R.id.item_head_count)
        val mDate: TextView = view.findViewById(R.id.item_date)
        val mNotes: TextView = view.findViewById(R.id.item_notes)
    }
}