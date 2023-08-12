package com.trevorwiebe.trackacow.presentation.add_drugs_given_to_specific_cow

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddDrugsGivenToSpecificCowActivity : AppCompatActivity() {

    private val mAddDrugsGivenToSpecificCowViewModel: AddDrugsGivenToSpecificCowViewModel by viewModels()

    private var mDrugList: MutableList<DrugModel> = mutableListOf()
    private var mCowModel: CowModel? = null

    private lateinit var mDrugLayout: LinearLayout
    private lateinit var mNoDrugs: TextView
    private lateinit var mCancelButton: Button
    private lateinit var mSaveButton: Button
    private lateinit var mProgressIndicator: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drugs_given_to_specific_cow)

        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        mCowModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("cowModel", CowModel::class.java)
        } else {
            intent.getParcelableExtra("cowModel")
        }

        val tagNumber = mCowModel?.tagNumber.toString()
        title = "Medicate cow $tagNumber"

        mDrugLayout = findViewById(R.id.drug_layout)
        mNoDrugs = findViewById(R.id.no_drugs_added_to_specific_cow)
        mCancelButton = findViewById(R.id.add_drug_cancel_button)
        mSaveButton = findViewById(R.id.add_drug_save_button)
        mProgressIndicator = findViewById(R.id.add_drug_specific_cow_progress_indicator)
        mCancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        mSaveButton.setOnClickListener(View.OnClickListener { v ->
            val drugList: MutableList<DrugGivenModel> = mutableListOf()
            for (r in 0 until mDrugLayout.childCount) {
                val drugGivenModel = DrugGivenModel()
                drugGivenModel.drugsGivenCowId = mCowModel?.cowId
                val cardView = mDrugLayout.getChildAt(r)
                if (cardView is CardView) {
                    val linearLayout = cardView.getChildAt(0)
                    if (linearLayout is LinearLayout) {
                        val checkBoxView = linearLayout.getChildAt(0)
                        if (checkBoxView is CheckBox) {
                            val drugId = checkBoxView.tag.toString().split("#".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()[0]
                            drugGivenModel.drugsGivenDrugId = drugId
                            if (checkBoxView.isChecked) {
                                val editText = linearLayout.getChildAt(2)
                                if (editText is EditText) {
                                    val amountGiven = editText.text.toString().toInt()
                                    drugGivenModel.drugsGivenAmountGiven = amountGiven
                                    drugGivenModel.drugsGivenLotId = mCowModel?.lotId ?: ""
                                    drugGivenModel.drugsGivenDate = mCowModel?.date ?: 0
                                    drugList.add(drugGivenModel)
                                }
                            }
                        }
                    }
                }
            }

            if (drugList.size == 0) {
                Snackbar
                    .make(v, "You must select a drug before saving", Snackbar.LENGTH_LONG)
                    .show()
                return@OnClickListener
            }

            mAddDrugsGivenToSpecificCowViewModel.onEvent(
                AddDrugsGivenToSpecificCowEvent.OnDrugListCreated(
                    drugList
                )
            )

            setResult(RESULT_OK)
            finish()
        })


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mAddDrugsGivenToSpecificCowViewModel.uiState.collect {

                    if (it.isFetchingFromCloud && it.dataSource == DataSource.Local) {
                        mProgressIndicator.visibility = View.VISIBLE
                    } else {
                        mProgressIndicator.visibility = View.GONE
                    }

                    mDrugLayout.removeAllViews()

                    mDrugList = it.drugsList.toMutableList()
                    for (x in mDrugList.indices) {
                        val drugEntity = mDrugList[x]
                        addCheckBox(mDrugLayout, drugEntity)
                    }
                    if (mDrugList.size == 0) {
                        mNoDrugs.visibility = View.VISIBLE
                    } else {
                        mNoDrugs.visibility = View.GONE
                    }

                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    private fun addCheckBox(linearLayout: LinearLayout, drugModel: DrugModel) {
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
        cardView.tag = "$drugId#cardView"
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
        containerLayout.tag = "$drugId#linearLayout"
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
        checkBox.tag = "$drugId#checkBox"
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
        editText.tag = "$drugId#editText"
        editText.setText(defaultAmountStr)
        editText.setSelectAllOnFocus(true)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.layoutParams = editTextParams
        containerLayout.addView(checkBox)
        containerLayout.addView(view)
        containerLayout.addView(editText)
        linearLayout.addView(cardView)
    }

    private var cardViewClickListener = View.OnClickListener { v ->
        val cardViewTag = v.tag.toString()
        val drugId = cardViewTag.split("#".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0]
        val linearLayout = v.findViewWithTag<LinearLayout>("$drugId#linearLayout")
        val checkBox = linearLayout.findViewWithTag<CheckBox>("$drugId#checkBox")
        checkBox.isChecked = !checkBox.isChecked
    }
    private var checkedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val linearLayout = buttonView.parent as LinearLayout
            val cardView = linearLayout.parent as CardView
            if (isChecked) {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccentVeryLight
                    )
                )
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            }
        }
}