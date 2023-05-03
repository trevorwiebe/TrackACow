package com.trevorwiebe.trackacow.presentation.lot_reports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.lot_reports.ViewCattleListAdapter.ViewCattleViewHolder
import java.text.NumberFormat
import java.util.*

class ViewCattleListAdapter : RecyclerView.Adapter<ViewCattleViewHolder>() {

    private var loadModels: List<LoadModel> = emptyList()
    private lateinit var context: Context
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

        val strDate = context.getString(R.string.date) + Utility.convertMillisToDate(date)
        val strHeadAddedStr = "Head added: " + numberFormat.format(headAdded.toLong())
        viewCattleViewHolder.mHeadAdded.text = strHeadAddedStr
        viewCattleViewHolder.mDate.text = strDate

        if (notes.isNullOrEmpty()) {
            viewCattleViewHolder.mNotes.visibility = View.GONE
        } else {
            viewCattleViewHolder.mNotes.visibility = View.VISIBLE
            viewCattleViewHolder.mNotes.text = context.getString(R.string.notes_more, notes)
        }
    }

    fun setData(loadList: List<LoadModel>, context: Context) {
        this.loadModels = loadList
        this.context = context
        notifyDataSetChanged()
    }

    inner class ViewCattleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mHeadAdded: TextView = view.findViewById(R.id.item_head_count)
        val mDate: TextView = view.findViewById(R.id.item_date)
        val mNotes: TextView = view.findViewById(R.id.item_notes)
    }
}