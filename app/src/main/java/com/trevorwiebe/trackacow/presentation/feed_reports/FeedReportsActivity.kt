package com.trevorwiebe.trackacow.presentation.feed_reports

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FeedReportsActivity : AppCompatActivity() {

    private lateinit var mProgressBar: LinearProgressIndicator
    private lateinit var mStartBtn: Button
    private lateinit var mEndBtn: Button
    private lateinit var mQuickLast24Hrs: Button
    private lateinit var mQuickLast30Days: Button
    private lateinit var mQuickAll: Button
    private lateinit var mNoFeed: TextView

    private var mStartCalender = Calendar.getInstance()
    private var mEndCalender = Calendar.getInstance()
    private var mDateCriteriaType = Constants.ALL

    @Inject
    lateinit var feedReportsViewModelFactory: FeedReportsViewModel.FeedReportsViewModelFactory

    private val feedReportViewModel: FeedReportsViewModel by viewModels {
        @Suppress("DEPRECATION")
        val lotModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("lotModel", LotModel::class.java)
        } else {
            intent.getParcelableExtra("lotModel")
        }
        FeedReportsViewModel.providesFactory(
            assistedFactory = feedReportsViewModelFactory,
            lotId = lotModel?.lotCloudDatabaseId ?: ""
        )
    }

    // TODO: remove ration horizontal scroll

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_reports)

        mProgressBar = findViewById(R.id.feed_reports_progress_bar)
        mStartBtn = findViewById(R.id.start_feed_date_btn)
        mEndBtn = findViewById(R.id.end_feed_date_btn)
        mQuickLast24Hrs = findViewById(R.id.quick_feed_yesterday)
        mQuickLast30Days = findViewById(R.id.quick_feed_month)
        mQuickAll = findViewById(R.id.quick_feed_all)
        mNoFeed = findViewById(R.id.feed_no_feeds)

        @Suppress("DEPRECATION")
        val lotModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("lotModel", LotModel::class.java)
        } else {
            intent.getParcelableExtra("lotModel")
        }
        val lotId = lotModel?.lotCloudDatabaseId ?: ""

        title = "Feed Reports: ${lotModel?.lotName}"

        if (savedInstanceState != null) {
            val startDate = savedInstanceState.getLong("startLong")
            val endDate = savedInstanceState.getLong("endLong")
            mStartCalender.timeInMillis = startDate
            mEndCalender.timeInMillis = endDate
            mStartBtn.text = Utility.convertMillisToDate(startDate)
            mEndBtn.text = Utility.convertMillisToDate(endDate)
            mDateCriteriaType = savedInstanceState.getInt("dateCriteriaType")
            setSelectedButton(mDateCriteriaType)
        } else {
            val millisInDay: Long = 86400000
            val today = System.currentTimeMillis()
            val yesterday = today - millisInDay
            mStartCalender.timeInMillis = yesterday
            mEndCalender.timeInMillis = System.currentTimeMillis()
            val startLong = mStartCalender.timeInMillis
            val endLong = mEndCalender.timeInMillis
            mStartBtn.text = Utility.convertMillisToDate(startLong)
            mEndBtn.text = Utility.convertMillisToDate(endLong)
            setSelectedButton(Constants.YESTERDAY)

            feedReportViewModel.onEvent(
                FeedReportsUiEvent.OnDateSelected(
                    lotId, startLong, endLong
                )
            )
        }

        val startDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mStartCalender[Calendar.YEAR] = year
            mStartCalender[Calendar.MONTH] = month
            mStartCalender[Calendar.DAY_OF_MONTH] = dayOfMonth
            mStartBtn.text = Utility.convertMillisToDate(mStartCalender.timeInMillis)
            setSelectedButton(Constants.CUSTOM)
            feedReportViewModel.onEvent(
                FeedReportsUiEvent.OnDateSelected(
                    lotId, mStartCalender.timeInMillis, mEndCalender.timeInMillis
                )
            )
        }

        mStartBtn.setOnClickListener {
            DatePickerDialog(
                this@FeedReportsActivity,
                startDatePicker,
                mStartCalender[Calendar.YEAR],
                mStartCalender[Calendar.MONTH],
                mStartCalender[Calendar.DAY_OF_MONTH]
            ).show()
        }

        val endDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mEndCalender[Calendar.YEAR] = year
            mEndCalender[Calendar.MONTH] = month
            mEndCalender[Calendar.DAY_OF_MONTH] = dayOfMonth
            mEndBtn.text = Utility.convertMillisToDate(mEndCalender.timeInMillis)
            setSelectedButton(Constants.CUSTOM)
            feedReportViewModel.onEvent(
                FeedReportsUiEvent.OnDateSelected(
                    lotId, mStartCalender.timeInMillis, mEndCalender.timeInMillis
                )
            )
        }

        mEndBtn.setOnClickListener {
            DatePickerDialog(
                this@FeedReportsActivity,
                endDatePicker,
                mEndCalender[Calendar.YEAR],
                mEndCalender[Calendar.MONTH],
                mEndCalender[Calendar.DAY_OF_MONTH]
            ).show()
        }

        mQuickLast24Hrs.setOnClickListener {
            val millisInDay: Long = 86400000
            val today = System.currentTimeMillis()
            val yesterday = today - millisInDay
            mStartCalender.timeInMillis = yesterday
            mEndCalender.timeInMillis = System.currentTimeMillis()
            val startLong = mStartCalender.timeInMillis
            val endLong = mEndCalender.timeInMillis
            mStartBtn.text = Utility.convertMillisToDate(startLong)
            mEndBtn.text = Utility.convertMillisToDate(endLong)
            setSelectedButton(Constants.YESTERDAY)

            feedReportViewModel.onEvent(
                FeedReportsUiEvent.OnDateSelected(
                    lotId, startLong, endLong
                )
            )
        }

        mQuickLast30Days.setOnClickListener {
            mStartCalender.timeInMillis = System.currentTimeMillis()
            mStartCalender.add(Calendar.MONTH, -1)
            mEndCalender.timeInMillis = System.currentTimeMillis()
            val startLong = mStartCalender.timeInMillis
            val endLong = mEndCalender.timeInMillis
            mStartBtn.text = Utility.convertMillisToDate(startLong)
            mEndBtn.text = Utility.convertMillisToDate(endLong)
            setSelectedButton(Constants.MONTH)
            feedReportViewModel.onEvent(
                FeedReportsUiEvent.OnDateSelected(
                    lotId, startLong, endLong
                )
            )
        }

        mQuickAll.setOnClickListener {
            if (lotModel != null) {

                mStartCalender.timeInMillis = lotModel.date
                mStartCalender.set(Calendar.MILLISECOND, 0)
                mStartCalender.set(Calendar.SECOND, 0)
                mStartCalender.set(Calendar.MINUTE, 0)
                mStartCalender.set(Calendar.HOUR, 0)

                val startDate = mStartCalender.timeInMillis
                val endDate = System.currentTimeMillis()

                mEndCalender.timeInMillis = endDate

                mStartBtn.text = Utility.convertMillisToDate(startDate)
                mEndBtn.text = Utility.convertMillisToDate(endDate)

                feedReportViewModel.onEvent(
                    FeedReportsUiEvent.OnDateSelected(
                        lotId,
                        startDate,
                        endDate
                    )
                )
            }
            setSelectedButton(Constants.ALL)
        }

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

                    if ((it.feedIsFetchingFromCloud && it.feedDataSource == DataSource.Local) ||
                        (it.rationIsFetchingFromCloud && it.feedDataSource == DataSource.Local)
                    ) {
                        mProgressBar.visibility = View.VISIBLE
                    } else {
                        mProgressBar.visibility = View.INVISIBLE
                    }

                    // rations
                    val allRation = RationModel(
                        0,
                        "",
                        getString(R.string.all)
                    )
                    val allRationList = it.rationList.toMutableList()
                    allRationList.add(0, allRation)
                    feedRationRvAdapter.setRationsList(allRationList.toList())

                    // feeds
                    feedReportRvAdapter.setFeedList(it.feedList)
                    if (it.feedList.isEmpty()) {
                        mNoFeed.visibility = View.VISIBLE
                    } else {
                        mNoFeed.visibility = View.GONE
                    }
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("startLong", mStartCalender.timeInMillis)
        outState.putLong("endLong", mEndCalender.timeInMillis)
        outState.putInt("dateCriteriaType", mDateCriteriaType)
        super.onSaveInstanceState(outState)
    }

    private fun setSelectedButton(reportType: Int) {
        mDateCriteriaType = reportType
        when (reportType) {
            Constants.YESTERDAY -> {
                mQuickLast24Hrs.background =
                    AppCompatResources.getDrawable(this, R.drawable.accent_sign_in_btn)
                mQuickLast24Hrs.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                mQuickLast30Days.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickLast30Days.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickAll.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }

            Constants.MONTH -> {
                mQuickLast24Hrs.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickLast24Hrs.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickLast30Days.background =
                    AppCompatResources.getDrawable(this, R.drawable.accent_sign_in_btn)
                mQuickLast30Days.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickAll.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }

            Constants.ALL -> {
                mQuickLast24Hrs.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickLast24Hrs.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.accent_sign_in_btn)
                mQuickAll.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                mQuickLast30Days.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickLast30Days.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }

            Constants.CUSTOM -> {
                mQuickLast24Hrs.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickLast24Hrs.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickAll.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickLast30Days.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickLast30Days.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}