package com.trevorwiebe.trackacow.presentation.feed_lot_detail_fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val pen_and_lot_param = "pen_and_lot_param"
private const val ration_list_param = "ration_list_param"
private const val last_used_ration_param = "last_used_ration_param"
private const val pen_ui_date_param = "pen_ui_date_param"

@AndroidEntryPoint
class FeedLotDetailFragment : Fragment() {

    private lateinit var mRationModelList: List<RationModel>
    private var mPenAndLotModel: PenAndLotModel? = null
    private var mCallAndRationModel: CallAndRationModel? = null
    private var mPenUiDate: Long = -1
    private var mOriginalFeedModelList: List<FeedModel> = emptyList()
    private lateinit var mRationSpinner: Spinner
    private lateinit var mRationSpinnerAdapter: ArrayAdapter<String>
    private lateinit var mSelectedRation: RationModel
    private lateinit var mCallET: TextInputEditText
    private lateinit var mFeedFirst: TextInputEditText
    private lateinit var mFeedAgainLayout: LinearLayout
    private lateinit var mTotalFed: TextView
    private lateinit var mLeftToFeed: TextView
    private lateinit var mSaveBtn: Button

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
            val rationList = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelableArrayList(ration_list_param, RationModel::class.java)
            } else {
                it.getParcelableArrayList(ration_list_param)
            }
            mRationModelList = rationList?.toList() ?: emptyList()
            @Suppress("DEPRECATION")
            mPenAndLotModel = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelable(pen_and_lot_param, PenAndLotModel::class.java)
            } else {
                it.getParcelable(pen_and_lot_param)
            }
            mPenUiDate = it.getLong(pen_ui_date_param)
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

        mRationSpinner = view.findViewById(R.id.select_ration)
        mCallET = view.findViewById(R.id.feed_lot_call_et)
        mFeedFirst = view.findViewById(R.id.feed_lot_feed_first)
        mSaveBtn = view.findViewById(R.id.save_feed_lot_btn)
        mFeedAgainLayout = view.findViewById(R.id.feed_again_layout)
        mTotalFed = view.findViewById(R.id.pen_total_fed)
        mLeftToFeed = view.findViewById(R.id.pen_left_to_feed)

        setSaveButtonStatus(false, "Saved")

        mSaveBtn.setOnClickListener {
            if (mCallET.length() == 0 || mCallET.text.toString() == "0") {
                mCallET.requestFocus()
                mCallET.error = resources.getString(R.string.please_fill_blank)
            } else {

                // code for updating the call entity
                val callAmount = mCallET.text.toString().toInt()

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
                    mSelectedRation.rationPrimaryKey,
                    mCallET.tag?.toString()
                )

                feedLotDetailFragmentViewModel.onEvent(
                    FeedLotDetailFragmentEvents.OnSave(
                        callModel,
                        feedModelsFromLayout,
                        mOriginalFeedModelList
                    )
                )

                setSaveButtonStatus(false, "Saved")
                mTotalFed.requestFocus()

            }
        }

        val rationsList: List<String> = mRationModelList.map { it.rationName }
        mRationSpinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_dropdown_item,
            rationsList
        )
        mRationSpinner.adapter = mRationSpinnerAdapter
        var check = 0
        mRationSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                mSelectedRation = mRationModelList[position]
                if (++check > 1) {
                    Utility.saveLastUsedRation(mContext, mSelectedRation.rationPrimaryKey)
                    setSaveButtonStatus(true, "Save")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        mCallET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text_entered: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateReports()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        mFeedTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(
                text_entered: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (text_entered.isNotEmpty()) {
                    mShouldAddNewFeedEditText = shouldAddAnotherEditText()
                    if (mShouldAddNewFeedEditText) {
                        addNewFeedEditText(null, mContext)
                    }
                    setSaveButtonStatus(true, "Save")
                }
                updateReports()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        mFeedFirst.addTextChangedListener(mFeedTextWatcher)


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedLotDetailFragmentViewModel.uiState.collect {
                    mCallAndRationModel = it.callModel
                    if (mCallAndRationModel == null) {
                        mCallET.setText("0")
                        mCallET.tag = ""
                        mRationSpinner.setSelection(mRationModelList
                            .indexOfFirst { rationModel ->
                                rationModel.rationPrimaryKey == Utility.getLastUsedRation(
                                    mContext
                                )
                            })
                    } else {
                        mCallET.setText(mCallAndRationModel?.callAmount.toString())
                        mCallET.tag = mCallAndRationModel?.callCloudDatabaseId ?: ""
                        mRationSpinner.setSelection(mRationModelList
                            .indexOfFirst { rationModel ->
                                rationModel.rationPrimaryKey == (
                                        mCallAndRationModel?.rationPrimaryKey ?: 0
                                        )
                            })
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedLotDetailFragmentViewModel.uiState.collect {

                    isLoadingMore = true
                    mOriginalFeedModelList = it.feedList
                    for (o in mOriginalFeedModelList.indices) {
                        val feedModel = mOriginalFeedModelList[o]
                        if (o == 0) {
                            mFeedFirst.setText(numberFormatter.format(feedModel.feed))
                            mFeedFirst.tag = feedModel.id
                        } else {
                            addNewFeedEditText(feedModel, mContext)
                        }
                    }
                    if (mOriginalFeedModelList.isNotEmpty()) {
                        addNewFeedEditText(null, mContext)
                    }
                    isLoadingMore = false

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
            lastUsedRationId: Int,
            penUiDate: Long
        ) = FeedLotDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(pen_and_lot_param, newPenAndLotModel)
                putParcelableArrayList(ration_list_param, rationList)
                putInt(last_used_ration_param, lastUsedRationId)
                putLong(pen_ui_date_param, penUiDate)
            }
        }
    }

    private fun addNewFeedEditText(feedModel: FeedModel?, viewContext: Context) {
        mFeedAgainNumber += 1
        val scale = resources.displayMetrics.density
        val pixels24 = (24 * scale + 0.5f).toInt()
        val pixels16 = (16 * scale + 0.5f).toInt()
        val pixels8 = (8 * scale + 0.5f).toInt()
        val linearLayout = LinearLayout(context)
        val linearLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.id = mFeedAgainNumber
        linearLayout.layoutParams = linearLayoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val textInputLayout = TextInputLayout(viewContext)
        val textInputLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textInputLayout.layoutParams = textInputLayoutParams
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
                    setSaveButtonStatus(true, "Save")
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
            val amountFeedText = mFeedFirst.text
            if (!amountFeedText.isNullOrEmpty()) {
                firstFeedModel.feed = numberFormatter.parse(amountFeedText.toString())?.toInt() ?: 0
                firstFeedModel.id = mFeedFirst.tag?.toString() ?: ""
                firstFeedModel.date = mPenUiDate
                firstFeedModel.lotId = mPenAndLotModel?.lotCloudDatabaseId ?: ""
                feedModelList.add(firstFeedModel)
            }
            for (i in 0 until mFeedAgainLayout.childCount) {

                val rowView = mFeedAgainLayout.getChildAt(i) as LinearLayout
                val textLayout = rowView.getChildAt(0) as TextInputLayout
                val feedId = textLayout.editText?.tag?.toString() ?: ""
                val amountTextView = textLayout.editText?.text
                if (!amountTextView.isNullOrEmpty()) {
                    val feedModel = FeedModel()
                    feedModel.feed = numberFormatter.parse(amountTextView.toString())?.toInt() ?: 0
                    feedModel.date = mPenUiDate
                    feedModel.lotId = mPenAndLotModel?.lotCloudDatabaseId ?: ""
                    feedModel.id = feedId
                    feedModelList.add(feedModel)
                }

            }
            return feedModelList
        }

    private val feedsFromLayout: ArrayList<Int>
        get() {
            val feedIntList = ArrayList<Int>()
            var firstText = mFeedFirst.text.toString()
            if (firstText.isEmpty()) {
                firstText = "0"
            }
            feedIntList.add(numberFormatter.parse(firstText)?.toInt() ?: 0)
            for (i in 0 until mFeedAgainLayout.childCount) {
                val v = mFeedAgainLayout.getChildAt(i)
                val linearLayout = v as LinearLayout
                val textLayout = linearLayout.getChildAt(0)
                val textInputLayout = textLayout as TextInputLayout
                val text = textInputLayout.editText!!.text.toString()
                if (text.isNotEmpty()) {
                    try {
                        val amountFed = numberFormatter.parse(text)?.toInt() ?: 0
                        feedIntList.add(amountFed)
                    } catch (e: ParseException) {
                        Log.e("TAG", "getFeedsFromLayout: ", e)
                    }
                }
            }
            return feedIntList
        }

    private fun setSaveButtonStatus(active: Boolean, buttonText: String) {
        mSaveBtn.isClickable = active
        if (active) {
            mSaveBtn.text = buttonText
            mSaveBtn.background = ContextCompat.getDrawable(mContext, R.drawable.accent_sign_in_btn)
        } else {
            mSaveBtn.text = buttonText
            mSaveBtn.background =
                ContextCompat.getDrawable(mContext, R.drawable.accent_sign_in_btn_deactivated)
        }
    }

    private fun updateReports() {
        var sum = 0
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