package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.FeedPenRecyclerViewAdapter.FeedPenViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.trevorwiebe.trackacow.R
import android.widget.TextView
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.ui_model.FeedPenListUiModel
import java.text.NumberFormat
import java.util.*

class FeedPenRecyclerViewAdapter : RecyclerView.Adapter<FeedPenViewHolder>() {

    private var feedPenUiModelList: List<FeedPenListUiModel> = emptyList()

    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    override fun getItemCount(): Int {
        return feedPenUiModelList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FeedPenViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.list_feed_pen, viewGroup, false)
        return FeedPenViewHolder(view)
    }

    override fun onBindViewHolder(feedPenViewHolder: FeedPenViewHolder, i: Int) {

        val feedPenListUiModel = feedPenUiModelList[i]

        // set date
        val date = feedPenListUiModel.date
        val friendlyDate = Utility.convertMillisToFriendlyDate(date)
        feedPenViewHolder.mDate.text = friendlyDate

        // set call model
        val callModel = feedPenListUiModel.callAndRationModel
        val call = callModel.callAmount.toLong()
        feedPenViewHolder.mCall.text = numberFormat.format(call)

        // set ration
        val rationName = feedPenListUiModel.callAndRationModel.rationName
        if (rationName.isNullOrEmpty()) {
            feedPenViewHolder.mRation.text = "-"
        } else {
            feedPenViewHolder.mRation.text = rationName
        }

        // set feed entities
        val feedEntities = feedPenListUiModel.feedList
        var totalFed = 0
        feedPenViewHolder.mFed.text = ""
        if (feedEntities.isEmpty()) {
            feedPenViewHolder.mFed.text = "0"
        } else {
            for (t in feedEntities.indices) {
                val feedEntity = feedEntities[t]
                val amountFed = feedEntity.feed
                totalFed += amountFed
                val amountFedStr = numberFormat.format(amountFed.toLong())
                feedPenViewHolder.mFed.append(amountFedStr)
                if (t < (feedEntities.size - 1)) {
                    feedPenViewHolder.mFed.append("\n")
                }
            }
        }

        // calculate feed and left to feed
        val totalFedStr = numberFormat.format(totalFed.toLong())
        feedPenViewHolder.mTotalFed.text = totalFedStr
        val leftToFeed = call - totalFed
        val leftToFeedStr = numberFormat.format(leftToFeed)
        feedPenViewHolder.mLeftToFeed.text = leftToFeedStr
    }

    fun setFeedPenList(feedPenListUiList: List<FeedPenListUiModel>){
        feedPenUiModelList = feedPenListUiList
        notifyDataSetChanged()
    }

    inner class FeedPenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDate: TextView = view.findViewById(R.id.feed_pen_date)
        val mCall: TextView = view.findViewById(R.id.feed_lot_call)
        val mRation: TextView = view.findViewById(R.id.feed_lot_ration)
        val mFed: TextView = view.findViewById(R.id.feed_lot_fed)
        val mLeftToFeed: TextView = view.findViewById(R.id.feed_lot_left_to_feed)
        val mTotalFed: TextView = view.findViewById(R.id.feed_lot_total_fed)
    }
}