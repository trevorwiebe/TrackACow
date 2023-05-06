package com.trevorwiebe.trackacow.presentation.medicate_a_cow

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MedicateACowActivity : AppCompatActivity() {

    private lateinit var mMainScrollView: ScrollView
    private lateinit var mTagName: TextInputEditText
    private lateinit var mNotes: TextInputEditText
    private lateinit var mDrugLayout: LinearLayout
    private lateinit var mMoreDrugsGivenLayout: LinearLayout
    private lateinit var mCowFoundCardView: CardView
    private lateinit var mNoDrugs: TextView
    private lateinit var mSaveCow: Button
    private lateinit var mMedicateACowMessage: TextView
    private lateinit var mAddMemoBtn: Button
    private lateinit var mNotesLayout: TextInputLayout

    private var mDrugList: List<DrugModel> = emptyList()
    private var mCowModels: MutableList<CowModel> = mutableListOf()
    private var mPenAndLotModel: PenAndLotModel? = null

    @Inject
    lateinit var medicateACowViewModelFactory: MedicateACowViewModel.MedicateACowViewModelFactory

    @Suppress("DEPRECATION")
    private val medicateACowViewModel: MedicateACowViewModel by viewModels {
        MedicateACowViewModel.providesFactory(
            assistedFactory = medicateACowViewModelFactory,
            penAndLotModel = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
            } else {
                intent.getParcelableExtra("penAndLotModel")
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicate_a_cow)

        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mMainScrollView = findViewById(R.id.main_scroll_view)
        mTagName = findViewById(R.id.tag_number)
        mNotes = findViewById(R.id.notes)
        mNoDrugs = findViewById(R.id.no_drugs_added)
        mCowFoundCardView = findViewById(R.id.cow_found_card_view)
        mMoreDrugsGivenLayout = findViewById(R.id.more_drugs_given_layout)
        mDrugLayout = findViewById(R.id.drug_layout)
        mSaveCow = findViewById(R.id.save_medicated_cow)
        mMedicateACowMessage = findViewById(R.id.medicate_a_cow_message_center)
        mAddMemoBtn = findViewById(R.id.add_notes_btn)
        mNotesLayout = findViewById(R.id.notes_layout)

        val scale = resources.displayMetrics.density
        val pixels16 = (16 * scale + 0.5f).toInt()
        val pixels4 = (4 * scale + 0.5f).toInt()

        @Suppress("DEPRECATION")
        mPenAndLotModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
        } else {
            intent.getParcelableExtra("penAndLotModel")
        }

        mSaveCow.setOnClickListener { saveCow() }
        mAddMemoBtn.setOnClickListener {
            mAddMemoBtn.visibility = View.GONE
            mNotesLayout.visibility = View.VISIBLE
            mNotes.requestFocus()
        }

        mTagName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    val cowModels = mCowModels.filter {
                        it.tagNumber == Integer.parseInt(s.toString())
                    }
                    if (cowModels.isNotEmpty()) {
                        medicateACowViewModel.onEvent(MedicateACowEvent.OnCowFound(cowModels))
                    } else {
                        mCowFoundCardView.visibility = View.GONE
                        mMoreDrugsGivenLayout.removeAllViews()
                        medicateACowViewModel.onEvent(
                            MedicateACowEvent.OnCowFound(emptyList())
                        )
                    }
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                medicateACowViewModel.uiState.collect { medicatedACowState ->

                    mDrugList = medicatedACowState.drugList
                    clearDrugCheckBoxList(mDrugLayout)
                    for (x in mDrugList.indices) {
                        val drugModel = mDrugList[x]
                        addCheckBox(mDrugLayout, drugModel)
                    }
                    if (mDrugList.isEmpty()) {
                        mNoDrugs.visibility = View.VISIBLE
                    } else {
                        mNoDrugs.visibility = View.GONE
                    }

                }

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                medicateACowViewModel.uiState.collect {
                    mCowModels = it.medicatedCowList.toMutableList()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                medicateACowViewModel.uiState.collect { medicateACowUiState ->

                    val cowFoundList = medicateACowUiState.cowFoundList

                    val drugGivenAndDrugModelList =
                        medicateACowUiState.drugAndDrugGivenListForFoundCows

                    if (cowFoundList.isNotEmpty()) {
                        mCowFoundCardView.visibility = View.VISIBLE
                        val cowIsDead = cowFoundList.any { it.alive == 0 }
                        if (cowIsDead && cowFoundList.size > 1) {
                            mMedicateACowMessage.text =
                                getString(R.string.cow_is_dead_and_medicated)
                            mCowFoundCardView.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    this@MedicateACowActivity,
                                    R.color.colorPrimaryDark
                                )
                            )
                        } else {
                            if (cowIsDead) {
                                mMedicateACowMessage.text = getString(R.string.this_cow_is_dead)
                                mCowFoundCardView.setCardBackgroundColor(
                                    ContextCompat.getColor(
                                        this@MedicateACowActivity,
                                        R.color.redText
                                    )
                                )
                            } else {
                                mMedicateACowMessage.text =
                                    getString(R.string.this_cow_has_been_medicated)
                                mCowFoundCardView.setCardBackgroundColor(
                                    ContextCompat.getColor(
                                        this@MedicateACowActivity,
                                        R.color.greenText
                                    )
                                )
                            }
                        }

                        if (drugGivenAndDrugModelList.isNotEmpty()) {
                            for (r in drugGivenAndDrugModelList.indices) {

                                val drugGivenAndDrugModel = drugGivenAndDrugModelList[r]
                                val drugName = drugGivenAndDrugModel.drugName
                                val amountGivenStr =
                                    drugGivenAndDrugModel.drugsGivenAmountGiven.toString()
                                val date =
                                    Utility.convertMillisToDate(drugGivenAndDrugModel.drugsGivenDate)
                                val textToSet = "$amountGivenStr units of $drugName on $date"

                                val textView = TextView(this@MedicateACowActivity)
                                val params = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                params.setMargins(pixels16, 0, pixels16, pixels4)
                                textView.layoutParams = params
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        this@MedicateACowActivity,
                                        android.R.color.white
                                    )
                                )
                                textView.text = textToSet
                                mMoreDrugsGivenLayout.addView(textView)
                            }
                        }

                    } else {
                        mCowFoundCardView.visibility = View.GONE
                    }
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    private fun addCheckBox(
        linearLayout: LinearLayout,
        drugModel: DrugModel,
    ) {
        val drugName = drugModel.drugName
        val drugId = drugModel.drugCloudDatabaseId
        val defaultAmount = drugModel.defaultAmount
        val defaultAmountStr = defaultAmount.toString()
        val scale = resources.displayMetrics.density
        val pixels24 = (24 * scale + 0.5f).toInt()
        val pixels8 = (8 * scale + 0.5f).toInt()

        val cardView = CardView(this)
        val cardViewParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardViewParams.setMargins(pixels8, pixels8, pixels8, pixels8)
        cardView.layoutParams = cardViewParams
        cardView.tag = "$drugId&cardView"
        cardView.radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            this.resources.displayMetrics
        )
        cardView.setOnClickListener(cardViewClickListener)

        val containerLayout = LinearLayout(this)
        val containerParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        containerLayout.tag = "$drugId&linearLayout"
        containerLayout.orientation = LinearLayout.HORIZONTAL
        containerLayout.layoutParams = containerParams
        cardView.addView(containerLayout)
        val checkBox = CheckBox(this)
        checkBox.text = drugName
        checkBox.tag = drugId
        val checkBoxParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        checkBoxParams.setMargins(pixels24, pixels8, pixels24, pixels8)
        checkBox.tag = "$drugId&checkBox"
        checkBox.setOnCheckedChangeListener(checkedChangeListener)
        checkBox.layoutParams = checkBoxParams
        val view = View(this)
        val viewParams = LinearLayout.LayoutParams(
            0,
            0,
            1f
        )
        view.layoutParams = viewParams
        val editText = EditText(this)
        val editTextParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        editTextParams.setMargins(pixels24, pixels8, pixels24, pixels8)
        editText.setEms(4)
        editText.gravity = Gravity.CENTER
        editText.tag = "$drugId&editText"
        editText.setText(defaultAmountStr)
        editText.setSelectAllOnFocus(true)
        editText.setOnEditorActionListener(doneListener)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.layoutParams = editTextParams
        containerLayout.addView(checkBox)
        containerLayout.addView(view)
        containerLayout.addView(editText)
        linearLayout.addView(cardView)
    }

    private fun clearDrugCheckBoxList(linearLayout: LinearLayout) {
        linearLayout.removeAllViews()
    }

    private var cardViewClickListener = View.OnClickListener { v ->
        val cardViewTag = v.tag.toString()
        val drugId = cardViewTag.split("&".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0]
        val linearLayout = v.findViewWithTag<LinearLayout>("$drugId&linearLayout")
        val checkBox = linearLayout.findViewWithTag<CheckBox>("$drugId&checkBox")
        checkBox.isChecked = !checkBox.isChecked
    }

    private var checkedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val linearLayout = buttonView.parent as LinearLayout
            val cardView = linearLayout.parent as CardView
            if (isChecked) {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorAccentVeryLight)
                )
            } else {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(this, android.R.color.white)
                )
            }
        }
    private var doneListener = OnEditorActionListener { v, actionId, event ->
        if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
            saveCow()
        }
        false
    }

    private fun saveCow() {
        if (mDrugList.isEmpty()) {
            Snackbar.make(
                mTagName,
                getString(R.string.add_drugs_first),
                Snackbar.LENGTH_LONG
            ).show()
        } else if (mTagName.length() == 0) {
            mMainScrollView.fullScroll(ScrollView.FOCUS_UP)
            mTagName.requestFocus()
            mTagName.error = getString(R.string.please_fill_blank)
        } else {

            val drugList: MutableList<DrugGivenModel> = mutableListOf()
            for (r in 0 until mDrugLayout.childCount) {
                val drugGivenModel = DrugGivenModel()

                val cardView = mDrugLayout.getChildAt(r)
                if (cardView is CardView) {
                    val linearLayout = cardView.getChildAt(0)
                    if (linearLayout is LinearLayout) {
                        val checkBoxView = linearLayout.getChildAt(0)
                        if (checkBoxView is CheckBox) {
                            val drugId = checkBoxView.tag.toString().split("&".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()[0]
                            drugGivenModel.drugsGivenDrugId = drugId
                            if (checkBoxView.isChecked) {
                                val editText = linearLayout.getChildAt(2)
                                if (editText is EditText) {
                                    val amountGiven = editText.text.toString().toInt()
                                    drugGivenModel.drugsGivenAmountGiven = amountGiven
                                    drugGivenModel.drugsGivenLotId =
                                        mPenAndLotModel?.lotCloudDatabaseId ?: "null"
                                    drugGivenModel.drugsGivenDate = System.currentTimeMillis()
                                    drugList.add(drugGivenModel)
                                }
                            }
                        }
                    }
                }
            }
            val tagNumber = mTagName.text.toString().toInt()
            val notes = mNotes.text.toString()

            val cowModel = CowModel(
                0,
                1,
                "",
                tagNumber,
                System.currentTimeMillis(),
                notes,
                mPenAndLotModel?.lotCloudDatabaseId ?: "null"
            )

            medicateACowViewModel.onEvent(
                MedicateACowEvent.OnCowAndDrugListCreated(
                    cowModel,
                    drugList
                )
            )

            mCowModels.add(cowModel)

            mTagName.setText("")
            mNotes.setText("")
            mCowFoundCardView.visibility = View.GONE
            for (r in 0 until mDrugLayout.childCount) {
                val cardView = mDrugLayout.getChildAt(r)
                if (cardView is CardView) {
                    val linearLayout = cardView.getChildAt(0)
                    if (linearLayout is LinearLayout) {
                        val checkBoxView = linearLayout.getChildAt(0)
                        if (checkBoxView is CheckBox) {
                            checkBoxView.isChecked = false
                        }
                    }
                }
            }
            mMainScrollView.fullScroll(ScrollView.FOCUS_UP)
            mTagName.requestFocus()
            mNotesLayout.visibility = View.GONE
            mAddMemoBtn.visibility = View.VISIBLE
        }
    }
}