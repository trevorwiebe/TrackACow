package com.trevorwiebe.trackacow.presentation.drugs_given_reports

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build.VERSION
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
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DrugsGivenReportActivity : AppCompatActivity() {

    private lateinit var timeDrugRvAdapter: TimeDrugRvAdapter
    private var mLotModel: LotModel? = null
    private val mStartCalendar = Calendar.getInstance()
    private val mEndCalendar = Calendar.getInstance()

    private lateinit var mStartDatePicker: OnDateSetListener
    private lateinit var mEndDatePicker: OnDateSetListener
    private lateinit var mNoDrugsGivenTv: TextView
    private lateinit var mDrugsReportRV: RecyclerView
    private lateinit var mProgressIndicator: LinearProgressIndicator
    private lateinit var mStartDateButton: Button
    private lateinit var mEndDateButton: Button
    private lateinit var mLast24HRs: Button
    private lateinit var mQuickMonth: Button
    private lateinit var mQuickAll: Button

    private val drugsGivenViewModel: DrugsGivenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drugs_given_report)

        mStartDateButton = findViewById(R.id.start_date_btn)
        mEndDateButton = findViewById(R.id.end_date_btn)
        mLast24HRs = findViewById(R.id.quick_yesterday)
        mQuickMonth = findViewById(R.id.quick_month)
        mQuickAll = findViewById(R.id.quick_all)
        mNoDrugsGivenTv = findViewById(R.id.no_drugs_given_report_tv)
        mProgressIndicator = findViewById(R.id.drugs_given_report_progress_indicator)
        mDrugsReportRV = findViewById(R.id.drug_report_rv)
        mDrugsReportRV.layoutManager = LinearLayoutManager(this)
        timeDrugRvAdapter = TimeDrugRvAdapter()
        mDrugsReportRV.adapter = timeDrugRvAdapter

        @Suppress("DEPRECATION")
        mLotModel = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("lotModel", LotModel::class.java)
        } else {
            intent.getParcelableExtra("lotModel")
        }

        title = resources.getString(R.string.drug_report_title, mLotModel?.lotName)

        if (savedInstanceState != null) {
            val startDate = savedInstanceState.getLong("startLong")
            val endDate = savedInstanceState.getLong("endLong")
            setSelectedButton(savedInstanceState.getInt("reportType"))
            mStartCalendar.timeInMillis = startDate
            mEndCalendar.timeInMillis = endDate
        } else {
            val millisInADay: Long = 86400000
            val today = System.currentTimeMillis()
            val yesterday = today - millisInADay
            mStartCalendar.timeInMillis = yesterday
            mEndCalendar.timeInMillis = System.currentTimeMillis()
            val startLong = mStartCalendar.timeInMillis
            val endLong = mEndCalendar.timeInMillis
            mStartDateButton.text = Utility.convertMillisToDate(startLong)
            mEndDateButton.text = Utility.convertMillisToDate(endLong)

            drugsGivenViewModel.onEvent(
                DrugsGivenUiEvent.CustomDateSelected(
                    mLotModel?.lotCloudDatabaseId ?: "", startLong, endLong
                )
            )

            setSelectedButton(Constants.YESTERDAY)
        }

        mStartDateButton.text = Utility.convertMillisToDate(mStartCalendar.timeInMillis)
        mEndDateButton.text = Utility.convertMillisToDate(mEndCalendar.timeInMillis)

        mStartDateButton.setOnClickListener {
            DatePickerDialog(
                this@DrugsGivenReportActivity,
                mStartDatePicker,
                mStartCalendar[Calendar.YEAR],
                mStartCalendar[Calendar.MONTH],
                mStartCalendar[Calendar.DAY_OF_MONTH]
            )
                .show()
        }

        mStartDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mStartCalendar[Calendar.YEAR] = year
            mStartCalendar[Calendar.MONTH] = month
            mStartCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            mStartDateButton.text = Utility.convertMillisToDate(mStartCalendar.timeInMillis)
            val selectedStartDate = mStartCalendar.timeInMillis
            val selectedEndDate = mEndCalendar.timeInMillis
            setSelectedButton(Constants.CUSTOM)

            drugsGivenViewModel.onEvent(
                DrugsGivenUiEvent.CustomDateSelected(
                    mLotModel?.lotCloudDatabaseId ?: "", selectedStartDate, selectedEndDate
                )
            )

        }

        mEndDateButton.setOnClickListener {
            DatePickerDialog(
                this@DrugsGivenReportActivity,
                mEndDatePicker,
                mEndCalendar[Calendar.YEAR],
                mEndCalendar[Calendar.MONTH],
                mEndCalendar[Calendar.DAY_OF_MONTH]
            )
                .show()
        }

        mEndDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mEndCalendar[Calendar.YEAR] = year
            mEndCalendar[Calendar.MONTH] = month
            mEndCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            mEndDateButton.text = Utility.convertMillisToDate(mEndCalendar.timeInMillis)
            val selectedStartDate = mStartCalendar.timeInMillis
            val selectedEndDate = mEndCalendar.timeInMillis
            setSelectedButton(Constants.CUSTOM)

            drugsGivenViewModel.onEvent(
                DrugsGivenUiEvent.CustomDateSelected(
                    mLotModel?.lotCloudDatabaseId ?: "", selectedStartDate, selectedEndDate
                )
            )
        }

        mQuickAll.setOnClickListener {
            if (mLotModel != null) {
                val lotId = mLotModel!!.lotCloudDatabaseId

                val lotStart = mLotModel!!.date
                val endDate = System.currentTimeMillis()

                mStartCalendar.timeInMillis = lotStart
                mEndCalendar.timeInMillis = endDate
                val friendlyStartDate = Utility.convertMillisToDate(lotStart)
                val friendlyEndDate = Utility.convertMillisToDate(endDate)
                mStartDateButton.text = friendlyStartDate
                mEndDateButton.text = friendlyEndDate

                drugsGivenViewModel.onEvent(
                    DrugsGivenUiEvent.CustomDateSelected(
                        lotId,
                        lotStart,
                        endDate
                    )
                )
            } else {
                // Archive Lot
            }
            setSelectedButton(Constants.ALL)
        }

        mQuickMonth.setOnClickListener {
            mStartCalendar.timeInMillis = System.currentTimeMillis()
            mStartCalendar.add(Calendar.MONTH, -1)
            mEndCalendar.timeInMillis = System.currentTimeMillis()
            val startLong = mStartCalendar.timeInMillis
            val endLong = mEndCalendar.timeInMillis
            val friendlyStartDate = Utility.convertMillisToDate(startLong)
            val friendlyEndDate = Utility.convertMillisToDate(endLong)
            mStartDateButton.text = friendlyStartDate
            mEndDateButton.text = friendlyEndDate

            drugsGivenViewModel.onEvent(
                DrugsGivenUiEvent.CustomDateSelected(
                    mLotModel?.lotCloudDatabaseId ?: "", startLong, endLong
                )
            )

            setSelectedButton(Constants.MONTH)
        }

        mLast24HRs.setOnClickListener {
            val millisInADay: Long = 86400000
            val today = System.currentTimeMillis()
            val yesterday = today - millisInADay
            mStartCalendar.timeInMillis = yesterday
            mEndCalendar.timeInMillis = System.currentTimeMillis()
            val startLong = mStartCalendar.timeInMillis
            val endLong = mEndCalendar.timeInMillis
            mStartDateButton.text = Utility.convertMillisToDate(startLong)
            mEndDateButton.text = Utility.convertMillisToDate(endLong)

            drugsGivenViewModel.onEvent(
                DrugsGivenUiEvent.CustomDateSelected(
                    mLotModel?.lotCloudDatabaseId ?: "", startLong, endLong
                )
            )

            setSelectedButton(Constants.YESTERDAY)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                drugsGivenViewModel.uiState.collect { drugsGivenUiState ->

                    if (drugsGivenUiState.isFetchingFromCloud && drugsGivenUiState.dataSource == DataSource.Local) {
                        mProgressIndicator.visibility = View.VISIBLE
                    } else {
                        mProgressIndicator.visibility = View.INVISIBLE
                    }

                    if (drugsGivenUiState.drugsGivenAndDrugList.isEmpty()) {
                        mNoDrugsGivenTv.visibility = View.VISIBLE
                        mDrugsReportRV.visibility = View.GONE
                    } else {
                        mNoDrugsGivenTv.visibility = View.GONE
                        mDrugsReportRV.visibility = View.VISIBLE
                        timeDrugRvAdapter.swapData(drugsGivenUiState.drugsGivenAndDrugList)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("startLong", mStartCalendar.timeInMillis)
        outState.putLong("endLong", mEndCalendar.timeInMillis)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    private fun setSelectedButton(reportType: Int) {
        when (reportType) {
            Constants.YESTERDAY -> {
                mLast24HRs.background =
                    AppCompatResources.getDrawable(this, R.drawable.accent_sign_in_btn)
                mLast24HRs.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                mQuickMonth.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickMonth.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickAll.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            Constants.MONTH -> {
                mLast24HRs.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mLast24HRs.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickMonth.background =
                    AppCompatResources.getDrawable(this, R.drawable.accent_sign_in_btn)
                mQuickMonth.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickAll.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            Constants.ALL -> {
                mLast24HRs.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mLast24HRs.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.accent_sign_in_btn)
                mQuickAll.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                mQuickMonth.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickMonth.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            Constants.CUSTOM -> {
                mLast24HRs.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mLast24HRs.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickAll.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickAll.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                mQuickMonth.background =
                    AppCompatResources.getDrawable(this, R.drawable.white_sign_in_btn_accent_border)
                mQuickMonth.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
        }
    }
}