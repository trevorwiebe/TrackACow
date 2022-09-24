package com.trevorwiebe.trackacow.domain.adapters

import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.domain.adapters.FeedPenRecyclerViewAdapter.FeedPenViewHolder
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.data.db.entities.FeedEntity
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.trevorwiebe.trackacow.R
import android.widget.TextView
import com.trevorwiebe.trackacow.domain.utils.Utility
import java.text.NumberFormat
import java.util.*

class FeedPenRecyclerViewAdapter : RecyclerView.Adapter<FeedPenViewHolder>() {

    private var mDateList: List<Long> = emptyList()
    private var mCallModels: List<CallModel> = emptyList()
    private var mFeedEntities: List<FeedEntity> = emptyList()

    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    override fun getItemCount(): Int {
        return mDateList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FeedPenViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.list_feed_pen, viewGroup, false)
        return FeedPenViewHolder(view)
    }

    override fun onBindViewHolder(feedPenViewHolder: FeedPenViewHolder, i: Int) {
        val date = mDateList[i]
        val friendlyDate = Utility.convertMillisToFriendlyDate(date)
        feedPenViewHolder.mDate.text = friendlyDate
        val callModel = getCallByDate(date)
        var call = 0
        if (callModel != null) {
            call = callModel.callAmount
            val callStr = numberFormat.format(call.toLong())
            feedPenViewHolder.mCall.text = callStr
        }
        val feedEntities = getFeedEntitiesByDate(date)
        var totalFed = 0
        feedPenViewHolder.mFed.text = ""
        if (feedEntities.size == 0) {
            feedPenViewHolder.mFed.text = "0"
        } else {
            for (t in feedEntities.indices) {
                val feedEntity = feedEntities[t]
                val amountFed = feedEntity.feed
                totalFed = totalFed + amountFed
                val amountFedStr = numberFormat.format(amountFed.toLong())
                feedPenViewHolder.mFed.append(amountFedStr)
                if (t < (feedEntities.size - 1)) {
                    feedPenViewHolder.mFed.append("\n")
                }
            }
        }
        val totalFedStr = numberFormat.format(totalFed.toLong())
        feedPenViewHolder.mTotalFed.text = totalFedStr
        val leftToFeed = call - totalFed
        val leftToFeedStr = numberFormat.format(leftToFeed.toLong())
        feedPenViewHolder.mLeftToFeed.text = leftToFeedStr
    }

    fun setDateList(dateList: List<Long>) {
        mDateList = dateList
        notifyDataSetChanged()
    }

    fun setCallList(callList: List<CallModel>) {
        mCallModels = callList
        notifyDataSetChanged()
    }

    fun setFeedList(feedList: ArrayList<FeedEntity>) {
        mFeedEntities = feedList
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

    private fun getCallByDate(date: Long): CallModel? {
        for (r in mCallModels.indices) {
            val callModel = mCallModels[r]
            if (callModel.date == date) {
                return callModel
            }
        }
        return null
    }

    private fun getFeedEntitiesByDate(date: Long): ArrayList<FeedEntity> {
        val feedEntities = ArrayList<FeedEntity>()
        for (q in mFeedEntities.indices) {
            val feedEntity = mFeedEntities[q]
            if (feedEntity.date == date) {
                feedEntities.add(feedEntity)
            }
        }
        return feedEntities
    }
}