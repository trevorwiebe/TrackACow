package com.trevorwiebe.trackacow.presentation.edit_medicated_cow

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.edit_drugs_given.EditDrugsGivenListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditMedicatedCowActivity : AppCompatActivity() {

    @Inject
    lateinit var editMedicatedCowsViewModelFactory: EditMedicatedCowViewModel.EditMedicatedCowsViewModelFactory

    private val editMedicatedCowViewModel: EditMedicatedCowViewModel by viewModels {
        EditMedicatedCowViewModel.providesFactory(
            assistedFactory = editMedicatedCowsViewModelFactory,
            cowId = intent.getStringExtra("cowId") ?: ""
        )
    }

    private var mSelectedCowId: String? = null
    private var mCowModel: CowModel? = null
    private var mStartDatePicker: OnDateSetListener? = null
    private val mCalendar = Calendar.getInstance()

    private lateinit var mCowIsDead: CardView
    private lateinit var mProgressIndicator: LinearProgressIndicator
    private lateinit var mEditTagNumber: TextInputEditText
    private lateinit var mEditDate: TextInputEditText
    private lateinit var mEditNotes: TextInputEditText
    private lateinit var mDrugLayout: LinearLayout
    private lateinit var mDrugsGiven: LinearLayout
    private lateinit var mEditDrugsGiven: Button
    private lateinit var mUpdateBtn: Button
    private lateinit var mDeleteBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_medicated_cow)
        mCowIsDead = findViewById(R.id.cow_is_dead_card)
        mProgressIndicator = findViewById(R.id.edit_cow_progress_indicator)
        mUpdateBtn = findViewById(R.id.update_medicated_cow)
        mDeleteBtn = findViewById(R.id.delete_medicated_cow)
        mEditDrugsGiven = findViewById(R.id.edit_drugs_given)
        mEditTagNumber = findViewById(R.id.edit_tag_number)
        mDrugLayout = findViewById(R.id.drugs_given_layout)
        mDrugsGiven = findViewById(R.id.drug_given_layout)
        mEditDate = findViewById(R.id.edit_date)
        mEditNotes = findViewById(R.id.edit_notes)
        mEditDate.setOnClickListener {
            DatePickerDialog(
                this@EditMedicatedCowActivity,
                mStartDatePicker,
                mCalendar[Calendar.YEAR],
                mCalendar[Calendar.MONTH],
                mCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
        mStartDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month
            mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            mEditDate.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        }
        mEditDrugsGiven.setOnClickListener {
            val editDrugsIntent =
                Intent(this@EditMedicatedCowActivity, EditDrugsGivenListActivity::class.java)
            editDrugsIntent.putExtra("cowModel", mCowModel)
            startActivity(editDrugsIntent)
        }
        mUpdateBtn.setOnClickListener {

            if (mCowModel != null) {
                mCowModel!!.tagNumber = mEditTagNumber.text.toString().toInt()
                mCowModel!!.date = mCalendar.timeInMillis
                mCowModel!!.notes = mEditNotes.text.toString()
                editMedicatedCowViewModel.onEvent(EditMedicatedCowUiEvent.OnCowUpdate(mCowModel!!))
            }

            finish()
        }

        mDeleteBtn.setOnClickListener {

            if (mCowModel != null && mSelectedCowId != null) {
                editMedicatedCowViewModel.onEvent(
                    EditMedicatedCowUiEvent.OnDrugsGivenByCowIdDeleted(
                        mSelectedCowId!!
                    )
                )
                editMedicatedCowViewModel.onEvent(EditMedicatedCowUiEvent.OnCowDeleted(mCowModel!!))
                finish()
            }
        }

        mSelectedCowId = intent.getStringExtra("cowId")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                editMedicatedCowViewModel.uiState.collect {

                    if ((it.cowIsFetchingFromCloud && it.cowDataSource == DataSource.Local && it.cowModel == null) ||
                            (it.drugIsFetchingFromCloud && it.drugDataSource == DataSource.Local && it.drugsGivenList.isEmpty())
                    ) {
                        mProgressIndicator.visibility = View.VISIBLE
                    } else {
                        mProgressIndicator.visibility = View.GONE
                    }

                    mCowModel = it.cowModel

                    val strTagNumber = mCowModel?.tagNumber.toString()
                    title = "Cow $strTagNumber"

                    mEditTagNumber.setText(strTagNumber)
                    mEditDate.setText(Utility.convertMillisToDate(mCowModel?.date ?: 0))
                    mEditNotes.setText(mCowModel?.notes)

                    mCalendar.timeInMillis = mCowModel?.date ?: 0

                    if (mCowModel?.alive == 1) {
                        mCowIsDead.visibility = View.GONE
                        mDrugLayout.visibility = View.VISIBLE
                    } else {
                        mCowIsDead.visibility = View.VISIBLE
                        mDrugLayout.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                editMedicatedCowViewModel.uiState.collect {
                    val drugsList = it.drugsGivenList
                    setDrugGivenLayout(drugsList)
                }
            }
        }
    }

    private fun setDrugGivenLayout(drugsGivenAndDrugModelList: List<DrugsGivenAndDrugModel>) {
        val scale = resources.displayMetrics.density
        val pixels16 = (16 * scale + 0.5f).toInt()
        val pixels8 = (8 * scale + 0.5f).toInt()
        mDrugsGiven.removeAllViews()
        if (drugsGivenAndDrugModelList.isEmpty()) {
            val textView = TextView(this@EditMedicatedCowActivity)
            val textViewParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textViewParams.setMargins(pixels16, pixels8, pixels16, 0)
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            textView.layoutParams = textViewParams
            textView.text = getString(R.string.no_drugs_given)
            mDrugsGiven.addView(textView)
        } else {
            for (t in drugsGivenAndDrugModelList.indices) {
                val drugAndDrugsGivenModel = drugsGivenAndDrugModelList[t]
                val drugName = drugAndDrugsGivenModel.drugName
                val amountGiven = drugAndDrugsGivenModel.drugsGivenAmountGiven
                val textToSet = resources.getQuantityString(
                    R.plurals.drug_unit,
                    amountGiven,
                    amountGiven,
                    drugName
                )
                val textView = TextView(this@EditMedicatedCowActivity)
                val textViewParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textViewParams.setMargins(pixels16, pixels8, pixels16, 0)
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                textView.layoutParams = textViewParams
                textView.text = textToSet
                mDrugsGiven.addView(textView)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}