package com.trevorwiebe.trackacow.presentation.lot_reports

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.LinearLayout
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.presentation.edit_load.EditLoadActivity
import com.trevorwiebe.trackacow.presentation.drugs_given_reports.DrugsGivenReportActivity
import com.trevorwiebe.trackacow.presentation.edit_lot.EditLotActivity
import android.view.*
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.feed_reports.FeedReportsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class LotReportActivity : AppCompatActivity() {

    private var mTotalHeadInt = 0
    private var mLotId: String? = null
    private var mSelectedLotModel: LotModel? = null
    private var reportType = 0
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    private val cattleListAdapter = ViewCattleListAdapter()
    private var mCurrentHeadDays = 0
    private var mReportType: Int? = null
    private var mLoadModelList: List<LoadModel> = emptyList()
    private lateinit var mProgressBar: LinearProgressIndicator
    private lateinit var mCustomerName: TextView
    private lateinit var mTotalHead: TextView
    private lateinit var mCurrentHead: TextView
    private lateinit var mDate: TextView
    private lateinit var mNotes: TextView
    private lateinit var mTotalDeathLoss: TextView
    private lateinit var mDeathLossPercentage: TextView
    private lateinit var mFeedReports: TextView
    private lateinit var mFeedReportsBtn: Button
    private lateinit var mDrugsUsedLayout: LinearLayout
    private lateinit var mNoDrugReports: TextView
    private lateinit var mDrugReports: Button
    private lateinit var mHeadDays: TextView
    private lateinit var mNoCattleReceived: TextView

    @Inject lateinit var lotReportViewModelFactory: LotReportViewModel.LotReportViewModelFactory

    private val lotReportViewModel: LotReportViewModel by viewModels{
        LotReportViewModel.providesFactory(
            assistedFactory = lotReportViewModelFactory,
            lotCloudDatabaseId = intent.getStringExtra("lotCloudDatabaseId") ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lot_reports)

        reportType = intent.getIntExtra("reportType", 0)
        if (reportType == Constants.LOT) {
            mReportType = Constants.LOT
        } else if (reportType == Constants.ARCHIVE) {
            mReportType = Constants.ARCHIVE
        }

        mProgressBar = findViewById(R.id.lot_report_progress_bar)
        mNoDrugReports = findViewById(R.id.no_drug_reports)
        val resetLotBtn = findViewById<Button>(R.id.archive_this_lot)
        resetLotBtn.setOnClickListener(archiveLotListener)
        if (reportType == Constants.ARCHIVE) {
            resetLotBtn.visibility = View.GONE
        }
        mDrugsUsedLayout = findViewById(R.id.drugs_used_layout)
        mTotalDeathLoss = findViewById(R.id.reports_death_loss)
        mDeathLossPercentage = findViewById(R.id.reports_death_loss_percentage)
        mFeedReports = findViewById(R.id.feed_reports)
        mCustomerName = findViewById(R.id.reports_customer_name)
        mTotalHead = findViewById(R.id.reports_total_head)
        mCurrentHead = findViewById(R.id.reports_current_head)
        mDate = findViewById(R.id.reports_date)
        mNotes = findViewById(R.id.reports_notes)
        mHeadDays = findViewById(R.id.reports_head_days)
        mDrugReports = findViewById(R.id.drug_reports_button)
        mFeedReportsBtn = findViewById(R.id.feed_reports_btn)

        val viewLoadsOfCattle = findViewById<RecyclerView>(R.id.view_loads_of_cattle)
        mNoCattleReceived = findViewById(R.id.no_cattle_received_tv)
        viewLoadsOfCattle.layoutManager = LinearLayoutManager(this)
        viewLoadsOfCattle.adapter = cattleListAdapter
        viewLoadsOfCattle.addOnItemTouchListener(
            ItemClickListener(
                this,
                viewLoadsOfCattle,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (reportType == Constants.LOT) {
                            val editLoadIntent = Intent(this@LotReportActivity, EditLoadActivity::class.java)
                            editLoadIntent.putExtra("loadModel", mLoadModelList[position])
                            startActivity(editLoadIntent)
                        }
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        mDrugReports.setOnClickListener {
            val drugReports = Intent(this@LotReportActivity, DrugsGivenReportActivity::class.java)
            drugReports.putExtra("lotModel", mSelectedLotModel)
            drugReports.putExtra("reportType", mReportType)
            startActivity(drugReports)
        }

        mFeedReportsBtn.setOnClickListener {
            val feedReports = Intent(this@LotReportActivity, FeedReportsActivity::class.java)
            feedReports.putExtra("lotModel", mSelectedLotModel)
            startActivity(feedReports)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                lotReportViewModel.uiState.collect {

                    if ((it.lotIsFetchingFromCloud && it.lotDataSource == DataSource.Local) ||
                        (it.drugIsFetchingFromCloud && it.drugDataSource == DataSource.Local) ||
                        (it.loadIsFetchingFromCloud && it.loadDataSource == DataSource.Local) ||
                        (it.deadCowIsFetchingFromCloud && it.deadCowDataSource == DataSource.Local) ||
                        (it.feedIsFetchingFromCloud && it.feedDataSource == DataSource.Local)
                    ) {
                        mProgressBar.visibility = View.VISIBLE
                    } else {
                        mProgressBar.visibility = View.INVISIBLE
                    }

                    mSelectedLotModel = it.lotModel
                    mLotId = mSelectedLotModel?.lotCloudDatabaseId

                    title = mSelectedLotModel?.lotName ?: ""
                    mCustomerName.text = mSelectedLotModel?.customerName ?: ""
                    mDate.text = Utility.convertMillisToDate(mSelectedLotModel?.date ?: 0)
                    mNotes.text = mSelectedLotModel?.notes ?: ""

                    if (it.drugsGivenAndDrugList.isNotEmpty()) {

                        // Prepare drugs used layout to add drugs given reporting
                        mDrugsUsedLayout.removeAllViews()
                        mNoDrugReports.visibility = View.GONE

                        val drugsGivenAndDrugsList = it.drugsGivenAndDrugList

                        for (i in drugsGivenAndDrugsList.indices) {
                            val scale = resources.displayMetrics.density
                            val pixels16 = (16 * scale + 0.5f).toInt()
                            val pixels4 = (4 * scale + 0.5f).toInt()

                            val drugsGivenAndDrugModel = drugsGivenAndDrugsList[i]
                            val drugName: String = drugsGivenAndDrugModel.drugName
                            val amountGiven: Int = drugsGivenAndDrugModel.drugsGivenAmountGiven
                            val textToSet = "$amountGiven units of $drugName"
                            val textView = TextView(this@LotReportActivity)
                            val textViewParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            textViewParams.setMargins(pixels16, pixels4, pixels16, pixels4)
                            textView.textSize = 16f
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    baseContext,
                                    android.R.color.black
                                )
                            )
                            textView.layoutParams = textViewParams
                            textView.text = textToSet
                            mDrugsUsedLayout.addView(textView)
                        }
                    } else {
                        mDrugsUsedLayout.removeAllViews()
                        mNoDrugReports.visibility = View.VISIBLE
                    }

                    mLoadModelList = it.loadList
                    if (mLoadModelList.isNotEmpty()) {

                        mNoCattleReceived.visibility = View.GONE

                        cattleListAdapter.setData(mLoadModelList, this@LotReportActivity)

                        mTotalHeadInt = 0
                        mCurrentHeadDays = 0
                        for (a in mLoadModelList.indices) {
                            val (_, numberOfHead, date) = mLoadModelList[a]
                            mTotalHeadInt += numberOfHead
                            val daysHadLoad = getDaysSinceFromMillis(date)
                            val thisLoadsHeadDays = daysHadLoad * numberOfHead
                            mCurrentHeadDays += thisLoadsHeadDays
                        }

                        val totalHeadStr = numberFormat.format(mTotalHeadInt.toLong())
                        mTotalHead.text = totalHeadStr
                        val lotIds = ArrayList<String?>()
                        lotIds.add(mLotId)
                    } else {
                        mNoCattleReceived.visibility = View.VISIBLE
                    }

                    val cowEntities: List<CowModel> = it.deadCowList
                    var numberOfHeadDaysToSubtract = 0
                    for (r in cowEntities.indices) {
                        val (_, _, _, _, date) = cowEntities[r]
                        val daysSinceDied = getDaysSinceFromMillis(date)
                        numberOfHeadDaysToSubtract += daysSinceDied
                    }
                    var currentHeadDays = mCurrentHeadDays - numberOfHeadDaysToSubtract
                    if (currentHeadDays < 0) {
                        currentHeadDays = 0
                    }
                    val headDaysStr = numberFormat.format(currentHeadDays.toLong())
                    mHeadDays.text = headDaysStr
                    val numberDead = cowEntities.size
                    val decimalFormat = DecimalFormat("#.##")
                    val percent = numberDead * 100f / mTotalHeadInt
                    val deadText =
                        numberFormat.format(numberDead.toLong()) + " " + resources.getString(R.string.dead)
                    val percentDeadText = decimalFormat.format(percent.toDouble()) + "%"
                    mTotalDeathLoss.text = deadText
                    mDeathLossPercentage.text = percentDeadText
                    var currentHead = mTotalHeadInt - numberDead
                    if (currentHead < 0) {
                        currentHead = 0
                    }
                    val currentHeadStr = numberFormat.format(currentHead.toLong())
                    mCurrentHead.text = currentHeadStr

                    mFeedReports.text = it.feedAmount
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.pen_reports_menu, menu)
        val item = menu.findItem(R.id.reports_action_edit)
        if (reportType == Constants.ARCHIVE) {
            item.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.reports_action_edit) {
            val editLotIntent = Intent(this@LotReportActivity, EditLotActivity::class.java)
            editLotIntent.putExtra("lotModel", mSelectedLotModel)
            startActivity(editLotIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private val archiveLotListener = View.OnClickListener {
        val lotArchived = AlertDialog.Builder(this@LotReportActivity)
        lotArchived.setTitle(resources.getString(R.string.confirm_archive_title))
        lotArchived.setMessage(resources.getString(R.string.confirm_archive_body))
        lotArchived.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            lotReportViewModel.onEvent(LotReportEvents.OnArchiveLot(mSelectedLotModel))
            finish()
        }
        lotArchived.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        lotArchived.show()
    }


    private fun getDaysSinceFromMillis(startDate: Long): Int {
        val millisInOnDay = TimeUnit.DAYS.toMillis(1)
        val currentTime = System.currentTimeMillis()
        val timeElapsed = currentTime - startDate
        return if (timeElapsed < 0) {
            0
        } else if (millisInOnDay >= timeElapsed) {
            1
        } else {
            val daysElapsed = timeElapsed / millisInOnDay
            daysElapsed.toInt() + 1
        }
    }
}