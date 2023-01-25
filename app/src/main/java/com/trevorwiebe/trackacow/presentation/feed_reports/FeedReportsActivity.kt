package com.trevorwiebe.trackacow.presentation.feed_reports

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FeedReportsActivity : AppCompatActivity() {

    private lateinit var mStartBtn: Button
    private lateinit var mEndBtn: Button
    private lateinit var mYesterday: Button
    private lateinit var mThisMonth: Button
    private lateinit var mAll: Button
    private lateinit var mNoFeed: TextView

    private lateinit var mStartCalender: Calendar
    private lateinit var mEndCalendar: Calendar

    @Inject
    lateinit var feedReportsViewModelFactory: FeedReportsViewModel.FeedReportsViewModelFactory

    private val feedReportViewModel: FeedReportsViewModel by viewModels {
        FeedReportsViewModel.providesFactory(
            assistedFactory = feedReportsViewModelFactory,
            lotId = intent.getStringExtra("lotId") ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_reports)

        mStartBtn = findViewById(R.id.start_feed_date_btn)
        mEndBtn = findViewById(R.id.end_feed_date_btn)
        mYesterday = findViewById(R.id.quick_feed_yesterday)
        mThisMonth = findViewById(R.id.quick_feed_month)
        mAll = findViewById(R.id.quick_feed_all)

        mStartBtn.text = Utility.convertMillisToDate(System.currentTimeMillis())
        mEndBtn.text = Utility.convertMillisToDate(System.currentTimeMillis())

        val rationRv: RecyclerView = findViewById(R.id.feed_ration_rv)
        rationRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val feedRationRvAdapter = FeedRationRvAdapter()
        rationRv.adapter = feedRationRvAdapter

        val feedRv: RecyclerView = findViewById(R.id.feed_report_rv)
        feedRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val feedReportRvAdapter = FeedReportRvAdapter()
        feedRv.adapter = feedReportRvAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                feedReportViewModel.uiState.collect {

                    // rations
                    val allRation = RationModel(0, "", "All")
                    val allRationList = it.rationList.toMutableList()
                    allRationList.add(0, allRation)
                    feedRationRvAdapter.setRationsList(allRationList.toList())

                    // feeds
                    feedReportRvAdapter.setFeedList(it.feedList)
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}