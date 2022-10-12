package com.trevorwiebe.trackacow.presentation.feedlot

import androidx.appcompat.app.AppCompatActivity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotByLotId.OnLotByLotIdLoaded
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.QueryFeedByLotIdAndDate.OnFeedByLotIdAndDateLoaded
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.DeleteFeedEntitiesByDateAndLotId.OnFeedEntitiesByDateAndLotIdDeleted
import com.google.android.material.textfield.TextInputEditText
import android.widget.LinearLayout
import android.widget.TextView
import android.text.TextWatcher
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.text.Editable
import android.text.InputType
import android.util.Log
import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingFeed.InsertHoldingFeedEntity
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.InsertFeedEntities
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.DeleteFeedEntitiesById
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.DeleteFeedEntitiesByDateAndLotId
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.QueryFeedByLotIdAndDate
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotByLotId
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FeedLotActivity : AppCompatActivity(),
    OnLotByLotIdLoaded,
    OnFeedByLotIdAndDateLoaded,
    OnFeedEntitiesByDateAndLotIdDeleted {

    private lateinit var mCallET: TextInputEditText
    private lateinit var mFeedAgainLayout: LinearLayout
    private lateinit var mTotalFed: TextView
    private lateinit var mLeftToFeed: TextView
    private lateinit var mSave: Button
    private lateinit var mRationSpinner: Spinner

    private var mSelectedCall: CallModel? = null
    private var mFeedAgainNumber = 0
    private var isLoadingMore = false
    private var mShouldAddNewFeedEditText = false
    private lateinit var mFeedTextWatcher: TextWatcher
    private val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
    private var mFeedEntities = ArrayList<FeedEntity>()
    private var mDate: Long = 0L
    private var mLotId: String = ""

    @Inject  lateinit var feedLotViewModelFactory: FeedLotViewModel.FeedLotViewModelFactory

    private val feedLotViewModel: FeedLotViewModel by viewModels {
        FeedLotViewModel.providesFactory(
            assistedFactory = feedLotViewModelFactory,
            defaultArgs = intent.extras
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_lot)

        mDate = intent.getLongExtra("feed_ui_model_date", 0L)
        mLotId = intent.getStringExtra("lot_id")?:""

        mRationSpinner = findViewById(R.id.select_ration)
        mCallET = findViewById(R.id.feed_lot_call_et)
        mFeedAgainLayout = findViewById(R.id.feed_again_layout)
        mTotalFed = findViewById(R.id.pen_total_fed)
        mLeftToFeed = findViewById(R.id.pen_left_to_feed)
        mSave = findViewById(R.id.save_feed_lot_btn)

        mCallET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateReports()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        mFeedTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    mShouldAddNewFeedEditText = shouldAddAnotherEditText()
                    if (mShouldAddNewFeedEditText) {
                        addNewFeedEditText(null)
                    }
                }
                updateReports()
            }
            override fun afterTextChanged(s: Editable) {}
        }

        mSave.setOnClickListener {
            if (mCallET.length() == 0) {
                mCallET.requestFocus()
                mCallET.error = "Please fill this blank"
            } else {

                // code for updating the call entity
                val callAmount = mCallET.text.toString().toInt()

                val c = Calendar.getInstance()
                c.timeInMillis = mDate
                c[Calendar.HOUR_OF_DAY] = 0
                c[Calendar.MINUTE] = 0
                c[Calendar.SECOND] = 0
                c[Calendar.MILLISECOND] = 0
                val dateStarted = c.timeInMillis

                val callModel: CallModel
                if(mSelectedCall == null){
                    // create the call Model
                    callModel = CallModel(0, callAmount, dateStarted, mLotId, 0,"")
                }else{
                    // assign callModel to mSelectedCallModel
                    callModel = mSelectedCall as CallModel
                    // update call amount
                    callModel.callAmount = callAmount
                }

                // send event to the view model
                feedLotViewModel.onEvent(FeedLotEvents.OnSave(callModel, mSelectedCall != null))

                // first we delete the all the local old feed entities; See onFeedEntitiesByDateAndLotIdDeleted to view the adding of feed entities
                DeleteFeedEntitiesByDateAndLotId(
                    mDate,
                    mLotId,
                    this@FeedLotActivity
                ).execute(this@FeedLotActivity)
            }
        }

        lifecycleScope.launch {
            feedLotViewModel.uiState.collect{ feedLotUiState ->
                mSelectedCall = feedLotUiState.callModel

                if (mSelectedCall == null) {
                    mSave.text = getString(R.string.save)
                    val call = 0
                    val callStr = call.toString()
                    mCallET.setText(callStr)
                } else {
                    mSave.text = getString(R.string.update)
                    val call = mSelectedCall!!.callAmount
                    val callStr = call.toString()
                    mCallET.setText(callStr)
                }

                val rationsList: List<String> = feedLotUiState.rationList.map { it.rationName }

                mRationSpinner.adapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    rationsList)
            }
        }

        if(mDate != 0L && mLotId != "") {

            QueryFeedByLotIdAndDate(mDate, mLotId, this).execute(this)
            QueryLotByLotId(mLotId, this).execute(this)
        }
    }

    override fun onFeedByLotIdAndDateLoaded(feedEntities: ArrayList<FeedEntity>) {
        mFeedEntities = feedEntities
        isLoadingMore = true
        for (o in mFeedEntities.indices) {
            val feedEntity = mFeedEntities[o]
            val amountFed = feedEntity.feed
            val amountFedStr = numberFormatter.format(amountFed.toLong())
            addNewFeedEditText(amountFedStr)
        }
        addNewFeedEditText(null)
        isLoadingMore = false
    }

    override fun onLotByLotIdLoaded(lotEntity: LotEntity) {
        val lotName = lotEntity.lotName
        val friendlyDate = Utility.convertMillisToFriendlyDate(mDate)
        title = "$lotName: $friendlyDate"
    }

    override fun onFeedEntitiesByDateAndLotIdDeleted() {
        // code for updating the feed entities
        val feedsIntList = feedsFromLayout
        val newFeedEntityList = ArrayList<FeedEntity>()

        // iterate through the new feed amounts
        for (n in feedsIntList.indices) {

            // get feed reference
            val feedRef = Constants.BASE_REFERENCE.child(Constants.FEEDS)
            val feedEntityId = feedRef.push().key ?: ""

            // initialize feed entity
            var feedEntity: FeedEntity
            val amountFed = feedsIntList[n]
            if (mFeedEntities.size > n) {
                feedEntity = mFeedEntities[n]
                feedEntity.feed = amountFed
            } else {
                feedEntity = FeedEntity(0, amountFed, mDate, feedEntityId, mLotId)
            }
            if (Utility.haveNetworkConnection(this@FeedLotActivity)) {
                // update cloud
                feedRef.child(feedEntity.id).setValue(feedEntity)
            } else {
                // if cloud not available, add to holding feed entity to upload later
                Utility.setNewDataToUpload(this@FeedLotActivity, true)
                val cacheFeedEntity =
                    CacheFeedEntity(
                        feedEntity.primaryKey,
                        feedEntity.feed,
                        feedEntity.date,
                        feedEntity.id,
                        feedEntity.lotId,
                        Constants.INSERT_UPDATE
                    )
                InsertHoldingFeedEntity(cacheFeedEntity).execute(this@FeedLotActivity)
            }

            // add feedEntity to array to push to local database
            newFeedEntityList.add(feedEntity)
        }

        // insert new feed entities into local database
        InsertFeedEntities(newFeedEntityList).execute(this@FeedLotActivity)

        // check to see if any feedEntities have been deleted
        if (mFeedEntities.size > newFeedEntityList.size) {

            // yes, some have been deleted from the UI, let's go ahead and delete them from the database

            // get database reference
            val feedRef = Constants.BASE_REFERENCE.child(Constants.FEEDS)
            for (g in mFeedEntities.indices) {
                val feedEntityToDelete = mFeedEntities[g]
                if (!newFeedEntityList.contains(feedEntityToDelete)) {
                    val feedEntityIdToDelete = feedEntityToDelete.id
                    if (Utility.haveNetworkConnection(this@FeedLotActivity)) {
                        // delete from cloud if available
                        feedRef.child(feedEntityIdToDelete).removeValue()
                    } else {
                        // add to holding database to delete when cloud is available
                        val cacheFeedEntity =
                            CacheFeedEntity(
                                feedEntityToDelete.primaryKey,
                                feedEntityToDelete.feed,
                                feedEntityToDelete.date,
                                feedEntityToDelete.id,
                                feedEntityToDelete.lotId,
                                Constants.DELETE
                            )
                        InsertHoldingFeedEntity(cacheFeedEntity).execute(this@FeedLotActivity)
                    }

                    // delete locally
                    DeleteFeedEntitiesById(feedEntityIdToDelete).execute(this@FeedLotActivity)
                }
            }
        }

        // close activity after all work is done
        finish()
    }

    private fun addNewFeedEditText(text: String?) {
        mFeedAgainNumber += 1
        val scale = resources.displayMetrics.density
        val pixels24 = (24 * scale + 0.5f).toInt()
        val pixels16 = (16 * scale + 0.5f).toInt()
        val pixels8 = (8 * scale + 0.5f).toInt()
        val linearLayout = LinearLayout(this@FeedLotActivity)
        val linearLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.id = mFeedAgainNumber
        linearLayout.layoutParams = linearLayoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val textInputLayout = TextInputLayout(this@FeedLotActivity)
        val textInputLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textInputLayout.layoutParams = textInputLayoutParams
        val textInputEditText = TextInputEditText(this@FeedLotActivity)
        val textInputEditTextParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textInputEditTextParams.setMargins(pixels16, 0, pixels16, pixels8)
        textInputEditText.layoutParams = textInputEditTextParams
        if (text != null) {
            textInputEditText.setText(text)
        }
        textInputEditText.setEms(6)
        textInputEditText.addTextChangedListener(mFeedTextWatcher)
        textInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        textInputEditText.textSize = 16f
        textInputEditText.hint = "Feed"
        textInputLayout.addView(textInputEditText)
        val deleteButton = ImageButton(this@FeedLotActivity)
        val deleteBtnParams = LinearLayout.LayoutParams(
            pixels24,
            pixels24
        )
        deleteButton.setPadding(pixels8, pixels8, pixels8, pixels8)
        deleteBtnParams.setMargins(0, pixels24, pixels16, pixels8)
        deleteButton.background = resources.getDrawable(R.drawable.ic_delete_black_24dp)
        deleteButton.id = mFeedAgainNumber
        deleteButton.setOnClickListener { view ->
            val viewToDelete = view.id
            var i = 0
            while (i < mFeedAgainLayout.childCount) {
                val v = mFeedAgainLayout.getChildAt(i)
                val tagToCompare = v.id
                if (tagToCompare == viewToDelete) {
                    i = i - 1
                    mFeedAgainNumber -= 1
                    mFeedAgainLayout.removeView(v)
                }
                i++
            }
            updateReports()
        }
        deleteButton.layoutParams = deleteBtnParams
        linearLayout.addView(textInputLayout)
        linearLayout.addView(deleteButton)
        mFeedAgainLayout.addView(linearLayout)
    }

    private fun shouldAddAnotherEditText(): Boolean {
        if (isLoadingMore) return false
        for (i in 0 until mFeedAgainLayout.childCount) {
            val v = mFeedAgainLayout.getChildAt(i)
            val linearLayout = v as LinearLayout
            val textLayout = linearLayout.getChildAt(0)
            val textInputLayout = textLayout as TextInputLayout
            val text = textInputLayout.editText!!.text.toString()
            if (text.isEmpty()) return false
        }
        return true
    }

    private val feedsFromLayout: ArrayList<Int>
        private get() {
            val feedIntList = ArrayList<Int>()
            for (i in 0 until mFeedAgainLayout.childCount) {
                val v = mFeedAgainLayout.getChildAt(i)
                val linearLayout = v as LinearLayout
                val textLayout = linearLayout.getChildAt(0)
                val textInputLayout = textLayout as TextInputLayout
                val text = textInputLayout.editText!!.text.toString()
                if (text.isNotEmpty()) {
                    try {
                        val amountFed = numberFormatter.parse(text).toInt()
                        feedIntList.add(amountFed)
                    } catch (e: ParseException) {
                        Log.e(TAG, "getFeedsFromLayout: ", e)
                    }
                }
            }
            return feedIntList
        }

    private fun updateReports() {
        val feedList = feedsFromLayout
        var sum = 0
        for (i in feedList) sum += i
        val totalFedStr = numberFormatter.format(sum.toLong())
        mTotalFed.text = totalFedStr
        val callStr: String
        callStr = if (mCallET.length() == 0) {
            "0"
        } else {
            mCallET.text.toString()
        }
        val call: Int
        call = try {
            numberFormatter.parse(callStr).toInt()
        } catch (e: ParseException) {
            Log.e(TAG, "updateReports: ", e)
            0
        }
        val leftToFeed = call - sum
        val leftToFeedStr = numberFormatter.format(leftToFeed.toLong())
        mLeftToFeed.text = leftToFeedStr
    }

    companion object {
        private const val TAG = "FeedLotActivity"
    }
}