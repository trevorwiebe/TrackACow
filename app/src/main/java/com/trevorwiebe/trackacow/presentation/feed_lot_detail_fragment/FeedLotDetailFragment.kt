package com.trevorwiebe.trackacow.presentation.feed_lot_detail_fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.add_or_edit_rations.AddOrEditRation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import javax.inject.Inject


private const val penAndLotParam = "pen_and_lot_param"
private const val rationListParam = "ration_list_param"
private const val penUiDateParam = "pen_ui_date_param"

@AndroidEntryPoint
class FeedLotDetailFragment : Fragment() {

    private var mRationModelList: List<RationModel> = emptyList()
    private var mPenAndLotModel: PenAndLotModel? = null
    private var mCallAndRationModel: CallAndRationModel? = null
    private var mPenUiDate: Long = -1
    private var mOriginalFeedModelList: List<FeedModel> = emptyList()
    private lateinit var mProgressBar: LinearProgressIndicator
    private lateinit var mRationSpinner: Spinner
    private var mSelectedRation: RationModel? = null
    private lateinit var mCallET: TextInputEditText
    private lateinit var mFeedFirst: TextInputEditText
    private lateinit var mFeedAgainLayout: LinearLayout
    private lateinit var mTotalFed: TextView
    private lateinit var mLeftToFeed: TextView
    private lateinit var mSaveBtn: Button
    private lateinit var mAddRationBtn: Button
    private lateinit var mAddRationTxt: TextView

    private var mFeedAgainNumber = 0
    private var isLoadingMore = false
    private var mShouldAddNewFeedEditText = false
    private lateinit var mFeedTextWatcher: TextWatcher
    private lateinit var mContext: Context
    private val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())

    @Inject
    lateinit var feedLotDetailFragmentViewModelFactory: FeedLotDetailFragmentViewModel.FeedLotDetailFragmentViewModelFactory

    private val feedLotDetailFragmentViewModel: FeedLotDetailFragmentViewModel by viewModels {
        FeedLotDetailFragmentViewModel.providesFactory(
            assistedFactory = feedLotDetailFragmentViewModelFactory,
            defaultArgs = arguments,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            mPenAndLotModel = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelable(penAndLotParam, PenAndLotModel::class.java)
            } else {
                it.getParcelable(penAndLotParam)
            }
            mPenUiDate = it.getLong(penUiDateParam)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_feed_lot_detail, container, false)
        mProgressBar = view.findViewById(R.id.feed_lot_detail_progress_bar)
        mRationSpinner = view.findViewById(R.id.select_ration)
        mCallET = view.findViewById(R.id.feed_lot_call_et)
        mFeedFirst = view.findViewById(R.id.feed_lot_feed_first)
        mSaveBtn = view.findViewById(R.id.save_feed_lot_btn)
        mFeedAgainLayout = view.findViewById(R.id.feed_again_layout)
        mTotalFed = view.findViewById(R.id.pen_total_fed)
        mLeftToFeed = view.findViewById(R.id.pen_left_to_feed)
        mAddRationBtn = view.findViewById(R.id.feed_add_ration_btn)
        mAddRationTxt = view.findViewById(R.id.feed_add_ration_txt)

        // code to allow spinner to gain focus
        mRationSpinner.isFocusable = true
        mRationSpinner.isFocusableInTouchMode = true
        mRationSpinner.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                this@FeedLotDetailFragment.mRationSpinner.performClick()
        }

        mSaveBtn.setOnClickListener {
            if (mCallET.length() == 0 || mCallET.text.toString() == "0") {
                mCallET.requestFocus()
                mCallET.error = resources.getString(R.string.please_fill_blank)
            } else {

                // code for updating the call entity
                val callAmount = numberFormatter.parse(mCallET.text.toString())?.toInt() ?: 0

                val c = Calendar.getInstance()
                c.timeInMillis = mPenUiDate
                c[Calendar.HOUR_OF_DAY] = 0
                c[Calendar.MINUTE] = 0
                c[Calendar.SECOND] = 0
                c[Calendar.MILLISECOND] = 0

                val callModel = CallModel(
                        0,
                        callAmount,
                        c.timeInMillis,
                        mPenAndLotModel?.lotCloudDatabaseId ?: "",
                        mSelectedRation?.rationPrimaryKey ?: 0,
                        mCallET.tag?.toString()
                )

                feedLotDetailFragmentViewModel.onEvent(
                    FeedLotDetailFragmentEvents.OnSave(
                        callModel,
                        feedModelsFromLayout,
                        mOriginalFeedModelList
                    )
                )
                mTotalFed.requestFocus()

                setSaveButtonStatus(false)

                val imm =
                    requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

            }
        }

        mAddRationBtn.setOnClickListener {
            val addRationIntent = Intent(mContext, AddOrEditRation::class.java)
            startActivity(addRationIntent)
        }

        mRationSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                mSelectedRation = mRationModelList[position]
                if (mRationSpinner.hasFocus()) {
                    setSaveButtonStatus(true)
                    Utility.saveLastUsedRation(mContext, mSelectedRation!!.rationPrimaryKey)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        mCallET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(textEntered: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateReports()
                if (mCallET.hasFocus()) setSaveButtonStatus(true)
            }

            override fun afterTextChanged(textEntered: Editable?) {}
        })

        mFeedTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(
                textEntered: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (textEntered.isNotEmpty()) {
                    mShouldAddNewFeedEditText = shouldAddAnotherEditText()
                    if (mShouldAddNewFeedEditText) {
                        addFeedEditText(null, mContext)
                    }
                }
                updateReports()
                feedViewsFromLayout.forEach {
                    if (it.hasFocus()) {
                        setSaveButtonStatus(true)
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        }

        mFeedFirst.addTextChangedListener(mFeedTextWatcher)


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedLotDetailFragmentViewModel.uiState.collect {
                    mCallAndRationModel = it.callModel

                    if (mCallAndRationModel == null) {
                        mCallET.tag = ""
                    } else {
                        mCallET.setText(numberFormatter.format(mCallAndRationModel?.callAmount))
                        mCallET.tag = mCallAndRationModel?.callCloudDatabaseId ?: ""
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedLotDetailFragmentViewModel.uiState.collect {

                    if ((it.callIsFetchingFromCloud && it.callDataSource == DataSource.Local && it.callModel == null) ||
                            (it.feedIsFetchingFromCloud && it.feedDataSource == DataSource.Local && it.feedList.isEmpty()) ||
                            (it.rationIsFetchingFromCloud && it.rationDataSource == DataSource.Local && it.rationList.isEmpty())
                    ) {
                        mProgressBar.visibility = View.VISIBLE
                    } else {
                        mProgressBar.visibility = View.INVISIBLE
                    }

                    mFeedAgainLayout.removeAllViews()

                    isLoadingMore = true
                    mOriginalFeedModelList = it.feedList
                    for (o in mOriginalFeedModelList.indices) {
                        val feedModel = mOriginalFeedModelList[o]
                        if (o == 0) {
                            mFeedFirst.setText(numberFormatter.format(feedModel.feed))
                            mFeedFirst.tag = feedModel.id
                            mTotalFed.tag = feedModel.primaryKey
                        } else {
                            addFeedEditText(feedModel, mContext)
                        }
                    }
                    if (mOriginalFeedModelList.isNotEmpty()) {
                        addFeedEditText(null, mContext)
                    }
                    isLoadingMore = false

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedLotDetailFragmentViewModel.uiState.collect { uiState ->

                    mRationModelList = uiState.rationList

                    if (mRationModelList.isNotEmpty()) {

                        var rationSelection = mRationModelList.indexOfFirst { rationModel ->
                            rationModel.rationPrimaryKey == (mCallAndRationModel?.rationPrimaryKey)
                        }
                        if (rationSelection == -1)
                            rationSelection = mRationModelList.indexOfFirst { rationModel ->
                                rationModel.rationPrimaryKey == Utility.getLastUsedRation(mContext)
                            }
                        if (rationSelection == -1) rationSelection = 0

                        val rationNameList = mRationModelList.map { it.rationName }

                        val newRationSpinnerAdapter = ArrayAdapter(
                                view.context,
                                android.R.layout.simple_spinner_dropdown_item,
                                rationNameList
                        )

                        mRationSpinner.adapter = newRationSpinnerAdapter
                        mRationSpinner.setSelection(rationSelection)

                        mAddRationTxt.visibility = View.GONE
                        mAddRationBtn.visibility = View.GONE
                    } else {
                        mAddRationTxt.visibility = View.VISIBLE
                        mAddRationBtn.visibility = View.VISIBLE
                    }
                }
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(
                newPenAndLotModel: PenAndLotModel,
                rationList: ArrayList<RationModel>,
                penUiDate: Long
        ) = FeedLotDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(penAndLotParam, newPenAndLotModel)
                putParcelableArrayList(rationListParam, rationList)
                putLong(penUiDateParam, penUiDate)
            }
        }
    }

    private fun addFeedEditText(feedModel: FeedModel?, viewContext: Context) {
        mFeedAgainNumber += 1

        val scale = resources.displayMetrics.density
        val pixels24 = (24 * scale + 0.5f).toInt()
        val pixels16 = (16 * scale + 0.5f).toInt()
        val pixels8 = (8 * scale + 0.5f).toInt()

        // create horizontal linear layout
        val linearLayout = LinearLayout(context)
        val linearLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.id = mFeedAgainNumber
        linearLayout.layoutParams = linearLayoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL

        // create textInputLayout
        val textInputLayout = TextInputLayout(viewContext)
        val textInputLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textInputLayout.layoutParams = textInputLayoutParams
        textInputLayout.tag = feedModel?.primaryKey

        // create textInputEditText
        val textInputEditText = TextInputEditText(viewContext)
        val textInputEditTextParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textInputEditTextParams.setMargins(pixels16, 0, pixels16, pixels8)
        textInputEditText.layoutParams = textInputEditTextParams
        textInputEditText.setEms(6)
        var feedAmount = feedModel?.feed.toString()
        if (feedAmount == "null") {
            feedAmount = ""
        }
        textInputEditText.setText(feedAmount)
        textInputEditText.tag = feedModel?.id
        textInputEditText.addTextChangedListener(mFeedTextWatcher)
        textInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        textInputEditText.textSize = 16f
        textInputEditText.hint = getString(R.string.feed)
        textInputLayout.addView(textInputEditText)

        // create deleteButton
        val deleteButton = ImageButton(context)
        val deleteBtnParams = LinearLayout.LayoutParams(
            pixels24,
            pixels24
        )
        deleteButton.setPadding(pixels8, pixels8, pixels8, pixels8)
        deleteBtnParams.setMargins(0, pixels24, pixels16, pixels8)
        deleteButton.background =
            AppCompatResources.getDrawable(viewContext, R.drawable.ic_delete_black_24dp)
        deleteButton.id = mFeedAgainNumber
        deleteButton.setOnClickListener { view ->
            val viewToDelete = view.id
            var i = 0
            while (i < mFeedAgainLayout.childCount) {
                val v = mFeedAgainLayout.getChildAt(i) as LinearLayout
                val textLayout = v.getChildAt(0) as TextInputLayout
                val textView = textLayout.editText
                val tagToCompare = v.id
                if (
                    tagToCompare == viewToDelete &&
                    (!textView?.text.isNullOrEmpty() || mFeedFirst.text.isNullOrEmpty())
                ) {
                    i -= 1
                    mFeedAgainLayout.removeView(v)
                    setSaveButtonStatus(true)
                }
                i++
            }
            updateReports()
        }
        deleteButton.layoutParams = deleteBtnParams

        // add views to layouts
        linearLayout.addView(textInputLayout)
        linearLayout.addView(deleteButton)
        mFeedAgainLayout.addView(linearLayout)

    }

    private fun shouldAddAnotherEditText(): Boolean {
        if (isLoadingMore) return false
        for (i in 0 until mFeedAgainLayout.childCount) {
            val linearLayout = mFeedAgainLayout.getChildAt(i) as LinearLayout
            val textInputLayout = linearLayout.getChildAt(0) as TextInputLayout
            val text = textInputLayout.editText!!.text.toString()
            if (text.isEmpty()) return false
        }
        return true
    }

    private val feedModelsFromLayout: List<FeedModel>
        get() {
            val feedModelList: MutableList<FeedModel> = mutableListOf()
            val firstFeedModel = FeedModel()
            val firstFeedKey = mTotalFed.tag ?: 0
            val amountFeedText = mFeedFirst.text
            if (!amountFeedText.isNullOrEmpty()) {
                firstFeedModel.feed = numberFormatter.parse(amountFeedText.toString())?.toInt() ?: 0
                firstFeedModel.id = mFeedFirst.tag?.toString() ?: ""
                firstFeedModel.date = mPenUiDate
                firstFeedModel.primaryKey = firstFeedKey as Int
                firstFeedModel.rationCloudId = mSelectedRation?.rationCloudDatabaseId
                firstFeedModel.lotId = mPenAndLotModel?.lotCloudDatabaseId ?: ""
                feedModelList.add(firstFeedModel)
            }
            for (i in 0 until mFeedAgainLayout.childCount) {

                val rowView = mFeedAgainLayout.getChildAt(i) as LinearLayout
                val textLayout = rowView.getChildAt(0) as TextInputLayout
                val feedKey = textLayout.tag ?: 0
                val feedId = textLayout.editText?.tag?.toString() ?: ""
                val amountTextView = textLayout.editText?.text
                if (!amountTextView.isNullOrEmpty()) {
                    val feedModel = FeedModel()
                    feedModel.primaryKey = feedKey as Int
                    feedModel.feed = numberFormatter.parse(amountTextView.toString())?.toInt() ?: 0
                    feedModel.date = mPenUiDate
                    feedModel.rationCloudId = mSelectedRation?.rationCloudDatabaseId
                    feedModel.lotId = mPenAndLotModel?.lotCloudDatabaseId ?: ""
                    feedModel.id = feedId
                    feedModelList.add(feedModel)
                }

            }
            return feedModelList
        }

    private val feedViewsFromLayout: ArrayList<TextInputEditText>
        get() {
            val feedList = ArrayList<TextInputEditText>()
            feedList.add(mFeedFirst)
            for (i in 0 until mFeedAgainLayout.childCount) {
                val v = mFeedAgainLayout.getChildAt(i)
                val linearLayout = v as LinearLayout
                val textLayout = linearLayout.getChildAt(0)
                val textInputLayout = textLayout as TextInputLayout
                val text = textInputLayout.editText as TextInputEditText
                feedList.add(text)
            }
            return feedList
        }

    private fun setSaveButtonStatus(active: Boolean) {
        mSaveBtn.isClickable = active
        if (active) {
            mSaveBtn.background = ContextCompat.getDrawable(mContext, R.drawable.accent_sign_in_btn)
        } else {
            mSaveBtn.background =
                ContextCompat.getDrawable(mContext, R.drawable.accent_sign_in_btn_deactivated)
        }
    }

    private fun updateReports() {
        var sum = 0
        val feedsFromLayout = feedViewsFromLayout.map {
            var text: String = it.text.toString()
            if (text.isEmpty()) text = "0"
            numberFormatter.parse(text)?.toInt() ?: 0
        }
        for (i in feedsFromLayout) sum += i
        val totalFedStr = numberFormatter.format(sum.toLong())
        mTotalFed.text = totalFedStr
        val callStr: String = if (mCallET.length() == 0) {
            "0"
        } else {
            mCallET.text.toString()
        }
        val call: Int = try {
            numberFormatter.parse(callStr)?.toInt() ?: 0
        } catch (e: ParseException) {
            Log.e("TAG", "updateReports: ", e)
            0
        }
        val leftToFeed = call - sum
        val leftToFeedStr = numberFormatter.format(leftToFeed.toLong())
        mLeftToFeed.text = leftToFeedStr
    }
}