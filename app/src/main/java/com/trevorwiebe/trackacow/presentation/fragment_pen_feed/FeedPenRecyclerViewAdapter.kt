package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.FeedPenRecyclerViewAdapter.FeedPenViewHolder
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.data.entities.FeedEntity
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

        // get date
        val date = feedPenListUiModel.date
        val friendlyDate = Utility.convertMillisToFriendlyDate(date)
        feedPenViewHolder.mDate.text = friendlyDate

        // get call model
        val callModel = feedPenListUiModel.callModel
        val call = callModel.callAmount.toLong()
        feedPenViewHolder.mCall.text = numberFormat.format(call)

        // get feed entities
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
        val mDate: TextView
        val mCall: TextView
        val mFed: TextView
        val mLeftToFeed: TextView
        val mTotalFed: TextView

        init {
            mDate = view.findViewById(R.id.feed_pen_date)
            mCall = view.findViewById(R.id.feed_lot_call)
            mFed = view.findViewById(R.id.feed_lot_fed)
            mLeftToFeed = view.findViewById(R.id.feed_lot_left_to_feed)
            mTotalFed = view.findViewById(R.id.feed_lot_total_fed)
        }
    }
}