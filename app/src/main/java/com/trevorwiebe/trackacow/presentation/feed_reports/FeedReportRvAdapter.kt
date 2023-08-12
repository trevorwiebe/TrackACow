package com.trevorwiebe.trackacow.presentation.feed_reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel

class FeedReportRvAdapter :
    RecyclerView.Adapter<FeedReportRvAdapter.FeedReportViewHolder>() {

    private var feedList: List<FeedModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedReportViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_feed_report_item, parent, false)
        return FeedReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    override fun onBindViewHolder(holder: FeedReportViewHolder, position: Int) {
        holder.amount.text = feedList[position].feed.toString()
    }

    fun setFeedList(feedList: List<FeedModel>){
        this.feedList = feedList
        notifyDataSetChanged()
    }

    class FeedReportViewHolder(view: View): RecyclerView.ViewHolder(view){
        val ration: TextView
        val amount: TextView

        init{
            ration = view.findViewById(R.id.item_feed_ration)
            amount = view.findViewById(R.id.item_feed_amount)
        }
    }
}