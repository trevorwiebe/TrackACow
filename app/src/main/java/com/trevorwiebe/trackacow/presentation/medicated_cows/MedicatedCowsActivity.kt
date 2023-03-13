package com.trevorwiebe.trackacow.presentation.medicated_cows

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import android.app.DatePickerDialog.OnDateSetListener
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.textfield.TextInputEditText
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot.InsertHoldingLot
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLoad.InsertHoldingLoad
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.InsertLotEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.load.InsertLoadEntity
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import android.content.Intent
import android.app.DatePickerDialog
import android.os.Build.VERSION
import android.text.InputType
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.activities.AddLoadOfCattleActivity
import com.trevorwiebe.trackacow.presentation.edit_medicated_cow.EditMedicatedCowActivity
import com.trevorwiebe.trackacow.presentation.activities.MarkACowDeadActivity
import com.trevorwiebe.trackacow.presentation.medicate_a_cow.MedicateACowActivity
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MedicatedCowsActivity : AppCompatActivity() {

    var mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(
        FirebaseAuth.getInstance().currentUser!!.uid)

    private var mMedicatedCowsRecyclerViewAdapter: MedicatedCowsRecyclerViewAdapter? = null
    private var mPenAndLotModel: PenAndLotModel? = null
    private var mIsActive = false
    private var shouldShowCouldntFindTag = false
    private val mCalendar = Calendar.getInstance()
    private val mLoadCalender = Calendar.getInstance()
    private lateinit var mCowUiModelList: List<CowUiModel>

    private lateinit var mDatePicker: OnDateSetListener
    private lateinit var mLoadDatePicker: OnDateSetListener
    private lateinit var mNoMedicatedCows: TextView
    private lateinit var mSearchView: SearchView
    private lateinit var mMedicatedCows: RecyclerView
    private lateinit var mResultsNotFound: CardView
    private lateinit var mMedicateACowFabMenu: FloatingActionsMenu
    private lateinit var mMarkAsActive: Button
    private lateinit var mPenIdleLayout: ScrollView
    private lateinit var mLotName: TextInputEditText
    private lateinit var mCustomerName: TextInputEditText
    private lateinit var mDate: TextInputEditText
    private lateinit var mNotes: TextInputEditText
    private lateinit var mTotalCount: TextInputEditText
    private lateinit var mLoadDate: TextInputEditText
    private lateinit var mLoadMemo: TextInputEditText

    @Inject lateinit var medicatedCowsViewModelFactory: MedicatedCowsViewModel.MedicatedCowsViewModelFactory

    @Suppress("DEPRECATION")
    private val medicatedCowsViewModel: MedicatedCowsViewModel by viewModels{
        MedicatedCowsViewModel.providesFactory(
            assistedFactory = medicatedCowsViewModelFactory,
            lotId = if (VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
            } else {
                intent.getParcelableExtra("penAndLotModel")
            }
                ?.lotCloudDatabaseId ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicated_cows)

        // TODO: fix issue where when adding, editing or deleting a cow, screen does not refresh

        mMedicateACowFabMenu = findViewById(R.id.floating_action_btn_menu)
        mNoMedicatedCows = findViewById(R.id.no_medicated_cows_tv)
        mResultsNotFound = findViewById(R.id.result_not_found)
        mMarkAsActive = findViewById(R.id.mark_as_active)
        mPenIdleLayout = findViewById(R.id.pen_idle)
        mLotName = findViewById(R.id.lot_name)
        mCustomerName = findViewById(R.id.customer_name)
        mDate = findViewById(R.id.lot_date)
        mNotes = findViewById(R.id.lot_memo)
        mTotalCount = findViewById(R.id.first_load_head_count)
        mLoadDate = findViewById(R.id.first_load_date)
        mLoadMemo = findViewById(R.id.first_load_memo)
        mMedicatedCows = findViewById(R.id.track_cow_rv)
        mMedicatedCows.layoutManager = LinearLayoutManager(this)
        mMedicatedCowsRecyclerViewAdapter = MedicatedCowsRecyclerViewAdapter(emptyList(), this)
        mMedicatedCows.adapter = mMedicatedCowsRecyclerViewAdapter
        mMedicatedCows.addOnItemTouchListener(
            ItemClickListener(
                this,
                mMedicatedCows,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val cowUiModel = mCowUiModelList[position]
                        val editCowIntent = Intent(
                            this@MedicatedCowsActivity,
                            EditMedicatedCowActivity::class.java
                        )
                        editCowIntent.putExtra("cowUiModel", cowUiModel)
                        startActivity(editCowIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        @Suppress("DEPRECATION")
        mPenAndLotModel = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
        } else {
            intent.getParcelableExtra("penAndLotModel")
        }

        if (mPenAndLotModel != null) {
            title = "Pen: " + mPenAndLotModel?.penName
            if (mPenAndLotModel?.lotName.isNullOrEmpty()) {
                setInActive()
            } else {
                supportActionBar?.subtitle = mPenAndLotModel?.lotName
                setActive()
            }
        }

        val todayDate = Utility.convertMillisToDate(System.currentTimeMillis())
        mDate.setText(todayDate)
        mLoadDate.setText(todayDate)

        mMarkAsActive.setOnClickListener(View.OnClickListener {
            var shouldSave = true
            if (mLotName.length() == 0) {
                mLotName.error = getString(R.string.please_fill_blank)
                shouldSave = false
            }
            if (mCustomerName.length() == 0) {
                mCustomerName.error = getString(R.string.please_fill_blank)
                shouldSave = false
            }
            if (mTotalCount.length() == 0) {
                mTotalCount.error = getString(R.string.please_fill_blank)
                shouldSave = false
            }
            if (!shouldSave) return@OnClickListener
            val lotName = mLotName.text.toString()
            val customerName = mCustomerName.text.toString()
            val totalHead = mTotalCount.text.toString().toInt()
            val notes = mNotes.text.toString()
            val date = mCalendar.timeInMillis
            val lotPushRef = mBaseRef.child(Constants.LOTS).push()
            val id = lotPushRef.key
            val lotEntity = LotEntity(
                0,
                lotName,
                id!!,
                customerName,
                notes,
                date,
                mPenAndLotModel!!.penCloudDatabaseId!!
            )
            val loadDescription = mLoadMemo.text.toString()
            val loadPushRef = mBaseRef.child(Constants.LOAD).push()
            val loadId = loadPushRef.key
            val loadEntity = LoadEntity(0, totalHead, date, loadDescription, id, loadId)
            if (Utility.haveNetworkConnection(this@MedicatedCowsActivity)) {
                lotPushRef.setValue(lotEntity)
                loadPushRef.setValue(loadEntity)
            } else {
                Utility.setNewDataToUpload(this@MedicatedCowsActivity, true)
                val cacheLotEntity = CacheLotEntity(
                    lotEntity.lotPrimaryKey,
                    lotEntity.lotName,
                    lotEntity.lotCloudDatabaseId,
                    lotEntity.customerName,
                    lotEntity.notes,
                    lotEntity.date,
                    lotEntity.lotPenCloudDatabaseId,
                    Constants.INSERT_UPDATE
                )
                InsertHoldingLot(cacheLotEntity).execute(this@MedicatedCowsActivity)
                val cacheLoadEntity = CacheLoadEntity(
                    0,
                    loadEntity.numberOfHead,
                    loadEntity.date,
                    loadEntity.description,
                    loadEntity.lotId,
                    loadEntity.loadId,
                    Constants.INSERT_UPDATE
                )
                InsertHoldingLoad(cacheLoadEntity).execute(this@MedicatedCowsActivity)
            }
            InsertLotEntity(lotEntity).execute(this@MedicatedCowsActivity)
            InsertLoadEntity(loadEntity).execute(this@MedicatedCowsActivity)
            mNoMedicatedCows.visibility = View.VISIBLE
            setActive()
            val ab = supportActionBar
            ab?.subtitle = lotName
            mLotName.setText("")
            mCustomerName.setText("")
            mTotalCount.setText("")
            mNotes.setText("")
        })

        mDate.setOnClickListener {
            DatePickerDialog(
                this@MedicatedCowsActivity,
                mDatePicker,
                mCalendar[Calendar.YEAR],
                mCalendar[Calendar.MONTH],
                mCalendar[Calendar.DAY_OF_MONTH]
            )
                .show()
        }
        mLoadDate.setOnClickListener {
            DatePickerDialog(
                this@MedicatedCowsActivity,
                mLoadDatePicker,
                mLoadCalender[Calendar.YEAR],
                mLoadCalender[Calendar.MONTH],
                mLoadCalender[Calendar.DAY_OF_MONTH]
            )
                .show()
        }
        mDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month
            mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            mDate.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        }
        mLoadDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mLoadCalender[Calendar.YEAR] = year
            mLoadCalender[Calendar.MONTH] = month
            mLoadCalender[Calendar.DAY_OF_MONTH] = dayOfMonth
            mLoadDate.setText(Utility.convertMillisToDate(mLoadCalender.timeInMillis))
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                medicatedCowsViewModel.uiState.collect {

                    mCowUiModelList = if(it.cowUiModelList.isNullOrEmpty()) emptyList() else it.cowUiModelList
                    if (mCowUiModelList.isEmpty() && mIsActive) {
                        mNoMedicatedCows.visibility = View.VISIBLE
                    } else {
                        mNoMedicatedCows.visibility = View.INVISIBLE
                    }

                    mMedicatedCowsRecyclerViewAdapter!!.swapData(mCowUiModelList)

                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSearchView.setQuery("", false)
        mSearchView.isIconified = true
        mResultsNotFound.visibility = View.INVISIBLE
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.track_cow_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        mSearchView = (searchItem.actionView as SearchView?)!!
        mSearchView.inputType = InputType.TYPE_CLASS_NUMBER
        searchItem.isVisible = mIsActive

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.isNotEmpty()) {
                    val cowUiModelList = findTags(s)
                    if (cowUiModelList.isEmpty() && shouldShowCouldntFindTag) {
                        mResultsNotFound.visibility = View.VISIBLE
                        mMedicateACowFabMenu.visibility = View.INVISIBLE
                    } else {
                        mMedicateACowFabMenu.visibility = View.VISIBLE
                        mResultsNotFound.visibility = View.INVISIBLE
                    }
                    shouldShowCouldntFindTag = true
                    mMedicatedCowsRecyclerViewAdapter!!.swapData(cowUiModelList)
                } else {
                    if (shouldShowCouldntFindTag) {
                        mMedicateACowFabMenu.visibility = View.VISIBLE
                    }
                    mResultsNotFound.visibility = View.INVISIBLE
                    mMedicatedCowsRecyclerViewAdapter!!.swapData(mCowUiModelList)
                }
                return false
            }
        })
        return true
    }

    private var addCattleResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(mMedicatedCows, "Cattle added successfully", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    /* private methods */
    fun medicateCow(view: View?) {
        mMedicateACowFabMenu.collapse()
        val medicateCowIntent = Intent(this@MedicatedCowsActivity, MedicateACowActivity::class.java)
        medicateCowIntent.putExtra("penAndLotModel", mPenAndLotModel)
        startActivity(medicateCowIntent)
    }

    fun markACowDead(view: View?) {
        mMedicateACowFabMenu.collapse()
        val markCowDeadIntent = Intent(this@MedicatedCowsActivity, MarkACowDeadActivity::class.java)
        markCowDeadIntent.putExtra("penId", mPenAndLotModel!!.penCloudDatabaseId)
        startActivity(markCowDeadIntent)
    }

    fun addCattle(view: View?) {
        mMedicateACowFabMenu.collapse()
        val addLoadOfCattle =
            Intent(this@MedicatedCowsActivity, AddLoadOfCattleActivity::class.java)
        addLoadOfCattle.putExtra("lotId", mPenAndLotModel!!.lotCloudDatabaseId)
        addCattleResultLauncher.launch(addLoadOfCattle)
    }

    private fun findTags(inputString: String): List<CowUiModel> {
        val listToReturn: MutableList<CowUiModel> = mutableListOf()
        for (e in mCowUiModelList.indices) {
            val cowUiModel = mCowUiModelList[e]
            val tag = cowUiModel.cowModel.tagNumber.toString()
            if (tag.startsWith(inputString)) {
                listToReturn.add(cowUiModel)
            }
        }
        return listToReturn
    }

    private fun setActive() {
        mMedicateACowFabMenu.visibility = View.VISIBLE
        mPenIdleLayout.visibility = View.GONE
        mMedicatedCows.visibility = View.VISIBLE
        shouldShowCouldntFindTag = true
        mIsActive = true
        invalidateOptionsMenu()
    }

    private fun setInActive() {
        mIsActive = false
        mNoMedicatedCows.visibility = View.GONE
        mMedicateACowFabMenu.visibility = View.GONE
        shouldShowCouldntFindTag = false
        mMedicatedCows.visibility = View.GONE
        mPenIdleLayout.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }
}