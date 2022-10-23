package com.trevorwiebe.trackacow.presentation.lot_reports

import androidx.appcompat.app.AppCompatActivity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.QueryDeadCowsByLotIds.OnDeadCowsLoaded
import com.trevorwiebe.trackacow.domain.dataLoaders.main.archivedLot.QueryArchivedLotsByLotId.OnArchivedLotLoaded
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.QueryFeedsByLotId.OnFeedsByLotIdReturned
import com.trevorwiebe.trackacow.domain.dataLoaders.main.load.QueryLoadsByLotId.OnLoadsByLotIdLoaded
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import android.widget.TextView
import android.widget.LinearLayout
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.presentation.activities.EditLoadActivity
import com.trevorwiebe.trackacow.presentation.activities.DrugsGivenReportActivity
import com.trevorwiebe.trackacow.presentation.edit_lot.EditLotActivity
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.QueryDeadCowsByLotIds
import com.trevorwiebe.trackacow.data.entities.CowEntity
import android.view.*
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot.InsertHoldingLot
import com.trevorwiebe.trackacow.data.cacheEntities.CacheArchivedLotEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingArchivedLot.InsertHoldingArchivedLot
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.DeleteLotEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.archivedLot.InsertArchivedLotEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.QueryFeedsByLotId
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class LotReportActivity : AppCompatActivity(),
    OnDeadCowsLoaded, OnArchivedLotLoaded, OnFeedsByLotIdReturned {

    private var mTotalHeadInt = 0
    private var mLotId: String? = null
    private var mSelectedLotModel: LotModel? = null
    private var reportType = 0
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    private val cattleListAdapter = ViewCattleListAdapter()
    private var mCurrentHeadDays = 0
    private var mReportType: Int? = null
    private var mLoadModelList: List<LoadModel> = emptyList()

    private lateinit var mCustomerName: TextView
    private lateinit var mTotalHead: TextView
    private lateinit var mCurrentHead: TextView
    private lateinit var mDate: TextView
    private lateinit var mNotes: TextView
    private lateinit var mTotalDeathLoss: TextView
    private lateinit var mDeathLossPercentage: TextView
    private lateinit var mFeedReports: TextView
    private lateinit var mDrugsUsedLayout: LinearLayout
    private lateinit var mNoDrugReports: TextView
    private lateinit var mDrugReports: Button
    private lateinit var mHeadDays: TextView
    private lateinit var mNoCattleReceived: TextView

    @Inject lateinit var lotReportViewModelFactory: LotReportViewModel.LotReportViewModelFactory

    private val lotReportViewModel: LotReportViewModel by viewModels{
        LotReportViewModel.providesFactory(
            assistedFactory = lotReportViewModelFactory,
            reportType = intent.getIntExtra("reportType", 0),
            lotId = intent.getIntExtra("lotId", -1),
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
                            editLoadIntent.putExtra("loadId", mLoadModelList[position].loadId)
                            startActivityForResult(editLoadIntent, EDIT_LOAD_CODE)
                        }
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        mDrugReports.setOnClickListener {
            val drugReports = Intent(this@LotReportActivity, DrugsGivenReportActivity::class.java)
            drugReports.putExtra("lotId", mLotId)
            drugReports.putExtra("reportType", mReportType)
            startActivity(drugReports)
        }

        lifecycleScope.launch{
            lotReportViewModel.uiState.collect{ lotReportUiState ->

                mSelectedLotModel = lotReportUiState.lotModel
                lotEntityLoaded(mSelectedLotModel)

                if(lotReportUiState.drugsGivenAndDrugList.isNotEmpty()){

                    // Prepare drugs used layout to add drugs given reporting
                    mDrugsUsedLayout.removeAllViews()
                    mNoDrugReports.visibility = View.GONE

                    val drugsGivenAndDrugsList = lotReportUiState
                        .drugsGivenAndDrugList.distinctBy {
                            it.drugCloudDatabaseId to it.drugCloudDatabaseId
                        }

                    for(i in drugsGivenAndDrugsList.indices) {
                        val scale = resources.displayMetrics.density
                        val pixels16 = (16 * scale + 0.5f).toInt()
                        val pixels8 = (8 * scale + 0.5f).toInt()
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
                }else{
                    mNoDrugReports.visibility = View.VISIBLE
                }

                mLoadModelList = lotReportUiState.loadList
                if(mLoadModelList.isNotEmpty()){

                    mNoCattleReceived.visibility = View.GONE

                    cattleListAdapter.setData(mLoadModelList)

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
                    QueryDeadCowsByLotIds(this@LotReportActivity, lotIds).execute(this@LotReportActivity)
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
            startActivityForResult(editLotIntent, EDIT_PEN_CODE)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onArchivedLotLoaded(archivedLotEntity: ArchivedLotEntity) {
        mSelectedLotModel = LotModel(
            archivedLotEntity.primaryKey,
            archivedLotEntity.lotName!!,
            archivedLotEntity.lotId ?: "",
            archivedLotEntity.customerName,
            archivedLotEntity.notes,
            archivedLotEntity.dateEnded,
            ""
        )
        lotEntityLoaded(mSelectedLotModel)
    }

    override fun onFeedsByLotIdReturned(feedEntities: ArrayList<FeedEntity>) {
        var totalAmountFed = 0
        for (y in feedEntities.indices) {
            val feedEntity = feedEntities[y]
            val amountFed = feedEntity.feed
            totalAmountFed += amountFed
        }
        val amountFedStr = numberFormat.format(totalAmountFed.toLong())
        val amountFedText = "$amountFedStr lbs"
        mFeedReports.text = amountFedText
    }

    override fun onDeadCowsLoaded(cowEntities: ArrayList<CowEntity>) {
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
        val deadText = numberFormat.format(numberDead.toLong()) + " dead"
        val percentDeadText = decimalFormat.format(percent.toDouble()) + "%"
        mTotalDeathLoss.text = deadText
        mDeathLossPercentage.text = percentDeadText
        var currentHead = mTotalHeadInt - numberDead
        if (currentHead < 0) {
            currentHead = 0
        }
        val currentHeadStr = numberFormat.format(currentHead.toLong())
        mCurrentHead.text = currentHeadStr
    }

    private val archiveLotListener = View.OnClickListener {
        val lotArchived = AlertDialog.Builder(this@LotReportActivity)
        lotArchived.setTitle("Are you sure you want to archive lot?")
        lotArchived.setMessage("This action cannot be undone.  You will not be able to edit these reports after they are archived.  You will be able to view this lot's reports under Archives.")
        lotArchived.setPositiveButton("Yes") { dialog, which ->
            val archivedLotEntity = ArchivedLotEntity(
                0,
                mSelectedLotModel!!.lotName,
                mSelectedLotModel!!.lotCloudDatabaseId,
                mSelectedLotModel!!.customerName,
                mSelectedLotModel!!.notes,
                mSelectedLotModel!!.date,
                System.currentTimeMillis()
            )
            if (Utility.haveNetworkConnection(this@LotReportActivity)) {
                val baseRef = FirebaseDatabase.getInstance().getReference("users").child(
                    FirebaseAuth.getInstance().currentUser!!.uid
                )

                // delete the lot entity
                baseRef.child(Constants.LOTS).child(
                    mSelectedLotModel!!.lotCloudDatabaseId
                ).removeValue()

                // push archived lot to the cloud;
                baseRef.child(Constants.ARCHIVE_LOT).child(
                    archivedLotEntity.lotId!!
                ).setValue(archivedLotEntity)
            } else {
                val cacheLotEntity = CacheLotEntity(
                    mSelectedLotModel!!.lotPrimaryKey,
                    mSelectedLotModel!!.lotName,
                    mSelectedLotModel!!.lotCloudDatabaseId,
                    mSelectedLotModel!!.customerName,
                    mSelectedLotModel!!.notes,
                    mSelectedLotModel!!.date,
                    mSelectedLotModel!!.lotPenCloudDatabaseId,
                    Constants.DELETE
                )
                InsertHoldingLot(cacheLotEntity).execute(this@LotReportActivity)
                val cacheArchivedLotEntity = CacheArchivedLotEntity(
                    0,
                    archivedLotEntity.lotName,
                    archivedLotEntity.lotId,
                    archivedLotEntity.customerName,
                    archivedLotEntity.notes,
                    archivedLotEntity.dateStarted,
                    archivedLotEntity.dateEnded,
                    Constants.INSERT_UPDATE
                )
                InsertHoldingArchivedLot(cacheArchivedLotEntity).execute(this@LotReportActivity)
            }
            DeleteLotEntity(mSelectedLotModel!!.lotCloudDatabaseId).execute(this@LotReportActivity)
            InsertArchivedLotEntity(archivedLotEntity).execute(this@LotReportActivity)
            finish()
        }
        lotArchived.setNegativeButton("Cancel") { _, _ -> }
        lotArchived.show()
    }


    private fun lotEntityLoaded(lotModel: LotModel?) {
        if (lotModel != null) {
            title = lotModel.lotName
            mCustomerName.text = lotModel.customerName
            mDate.text = Utility.convertMillisToDate(lotModel.date)
            mNotes.text = lotModel.notes
        }
        if(lotModel != null) {
            mLotId = lotModel.lotCloudDatabaseId
            QueryFeedsByLotId(mLotId, this).execute(this)
        }
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

    companion object {
        private const val TAG = "LotReportActivity"
        private const val EDIT_PEN_CODE = 747
        private const val EDIT_LOAD_CODE = 472
    }
}