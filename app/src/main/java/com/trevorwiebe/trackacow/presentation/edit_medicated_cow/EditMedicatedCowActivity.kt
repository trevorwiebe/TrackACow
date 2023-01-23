package com.trevorwiebe.trackacow.presentation.edit_medicated_cow

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.activities.EditDrugsGivenActivity
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EditMedicatedCowActivity : AppCompatActivity() {

    private val editMedicatedCowViewModel: EditMedicatedCowViewModel by viewModels()

    private var mCowUiModel: CowUiModel? = null
    private var mStartDatePicker: OnDateSetListener? = null
    private val mCalendar = Calendar.getInstance()

    private lateinit var mCowIsDead: CardView
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
            )
                .show()
        }
        mStartDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month
            mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            mEditDate.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        }
        mEditDrugsGiven.setOnClickListener {
            val editDrugsIntent =
                Intent(this@EditMedicatedCowActivity, EditDrugsGivenActivity::class.java)
            editDrugsIntent.putExtra("cowId", mCowUiModel!!.cowModel.cowId)
            startActivity(editDrugsIntent)
        }
        mUpdateBtn.setOnClickListener {
            val cowModel = mCowUiModel!!.cowModel

            cowModel.tagNumber = mEditTagNumber.text.toString().toInt()
            cowModel.date = mCalendar.timeInMillis
            cowModel.notes = mEditNotes.text.toString()

            editMedicatedCowViewModel.onEvent(EditMedicatedCowUiEvent.OnCowUpdate(cowModel))

            finish()
        }

        mDeleteBtn.setOnClickListener {
            val cowModel = mCowUiModel!!.cowModel

            editMedicatedCowViewModel.onEvent(EditMedicatedCowUiEvent.OnCowDeleted(cowModel))
            editMedicatedCowViewModel.onEvent(
                EditMedicatedCowUiEvent.OnDrugsGivenByCowIdDeleted(
                    cowModel.cowId
                )
            )

            finish()
        }

        @Suppress("DEPRECATION")
        mCowUiModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("cowUiModel", CowUiModel::class.java)
        } else {
            intent.getParcelableExtra("cowUiModel")
        }

        val cowModel = mCowUiModel!!.cowModel

        val strTagNumber = cowModel.tagNumber.toString()
        title = "Cow $strTagNumber"

        mCalendar.timeInMillis = cowModel.date
        mDrugsGiven.removeAllViews()

        if (cowModel.isAlive == 1) {
            mCowIsDead.visibility = View.GONE
        } else {
            mCowIsDead.visibility = View.VISIBLE
            mDrugLayout.visibility = View.GONE
        }

        mEditTagNumber.setText(strTagNumber)
        mEditDate.setText(Utility.convertMillisToDate(cowModel.date))
        mEditNotes.setText(cowModel.notes)

        setDrugGivenLayout(mCowUiModel!!.drugsGivenAndDrugModelList)
    }

    private fun setDrugGivenLayout(drugsGivenAndDrugModelList: List<DrugsGivenAndDrugModel>) {
        val scale = resources.displayMetrics.density
        val pixels16 = (16 * scale + 0.5f).toInt()
        val pixels8 = (8 * scale + 0.5f).toInt()
        if (drugsGivenAndDrugModelList.isEmpty()) {
            val textView = TextView(this@EditMedicatedCowActivity)
            val textViewParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textViewParams.setMargins(pixels16, pixels8, pixels16, 0)
            textView.setTextColor(resources.getColor(android.R.color.black))
            textView.layoutParams = textViewParams
            // TODO: put this string in resources
            textView.text = "No drugs given"
            mDrugsGiven.addView(textView)
        } else {
            for (t in drugsGivenAndDrugModelList.indices) {
                val drugAndDrugsGivenModel = drugsGivenAndDrugModelList[t]
                val drugName = drugAndDrugsGivenModel.drugName
                val amountGiven = drugAndDrugsGivenModel.drugsGivenAmountGiven
                val textToSet = "$amountGiven units of $drugName"
                val textView = TextView(this@EditMedicatedCowActivity)
                val textViewParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textViewParams.setMargins(pixels16, pixels8, pixels16, 0)
                textView.setTextColor(resources.getColor(android.R.color.black))
                textView.layoutParams = textViewParams
                textView.text = textToSet
                mDrugsGiven.addView(textView)
            }
        }
    }
}