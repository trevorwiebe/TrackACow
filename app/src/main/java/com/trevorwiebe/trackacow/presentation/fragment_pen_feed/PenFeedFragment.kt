package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotsByPenId.OnLotsByPenIdLoaded
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.QueryFeedsByLotId.OnFeedsByLotIdReturned
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.domain.adapters.FeedPenRecyclerViewAdapter
import com.trevorwiebe.trackacow.data.entities.LotEntity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.presentation.feedlot.FeedLotActivity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotsByPenId
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.QueryFeedsByLotId
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PenFeedFragment : Fragment(),
    OnLotsByPenIdLoaded,
    OnFeedsByLotIdReturned {

    private var mPenId: String? = null
    private lateinit var mEmptyPen: TextView
    private lateinit var mFeedPenRv: RecyclerView
    private lateinit var feedPenRecyclerViewAdapter: FeedPenRecyclerViewAdapter
    private lateinit var mSelectedLotEntity: LotEntity
    private lateinit var mDatesList: ArrayList<Long>

    private val penFeedViewModel: PenFeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPenId = arguments?.getString("fragment_pen_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_pen_feed, container, false)

        mEmptyPen = rootView.findViewById(R.id.feed_pen_empty)
        mFeedPenRv = rootView.findViewById(R.id.feed_pen_rv)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        mFeedPenRv.layoutManager = linearLayoutManager

        feedPenRecyclerViewAdapter = FeedPenRecyclerViewAdapter()
        mFeedPenRv.adapter = feedPenRecyclerViewAdapter

        mFeedPenRv.addOnItemTouchListener(
            ItemClickListener(
                context,
                mFeedPenRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val date = mDatesList[position]
                        val lotId = mSelectedLotEntity.lotId
                        val feedLotIntent = Intent(activity, FeedLotActivity::class.java)
                        feedLotIntent.putExtra("date", date)
                        feedLotIntent.putExtra("lotId", lotId)
                        startActivity(feedLotIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                penFeedViewModel.uiState.collect{
                    feedPenRecyclerViewAdapter.setCallList(it.callList)
                }
            }
        }

        QueryLotsByPenId(mPenId, this).execute(context)

        return rootView
    }

    override fun onLotsByPenIdLoaded(lotEntities: ArrayList<LotEntity>) {
        if (lotEntities.size == 0) {
            mEmptyPen.visibility = View.VISIBLE
        } else {
            mEmptyPen.visibility = View.INVISIBLE
            mSelectedLotEntity = lotEntities[0]
            val lotId = mSelectedLotEntity!!.lotId
            QueryFeedsByLotId(lotId, this@PenFeedFragment).execute(context)

            val dateStarted = mSelectedLotEntity!!.date
            mDatesList = getDaysList(dateStarted)
            mDatesList.reverse()
            feedPenRecyclerViewAdapter!!.setDateList(mDatesList)
        }
    }

    private fun getDaysList(roughDateStarted: Long): ArrayList<Long> {
        val days = ArrayList<Long>()
        val c = Calendar.getInstance()
        c.timeInMillis = roughDateStarted
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0
        var dateStarted = c.timeInMillis
        days.add(dateStarted)
        val oneDay = TimeUnit.DAYS.toMillis(1)
        val currentTime = System.currentTimeMillis()
        while (dateStarted + oneDay < currentTime) {
            dateStarted = dateStarted + oneDay
            days.add(dateStarted)
        }
        return days
    }

    override fun onFeedsByLotIdReturned(feedEntities: ArrayList<FeedEntity>) {
        Log.d(TAG, "onFeedsByLotIdReturned: $feedEntities")
        feedPenRecyclerViewAdapter!!.setFeedList(feedEntities)
    }

    companion object {
        private const val TAG = "PenFeedFragment"
        @JvmStatic
        fun newInstance(penId: String?): PenFeedFragment {
            val args = Bundle()
            args.putString("fragment_pen_id", penId)
            val fragment = PenFeedFragment()
            fragment.arguments = args
            return fragment
        }
    }
}