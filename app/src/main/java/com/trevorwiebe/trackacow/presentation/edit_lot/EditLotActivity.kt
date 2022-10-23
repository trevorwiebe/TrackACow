package com.trevorwiebe.trackacow.presentation.edit_lot

import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog.OnDateSetListener
import com.google.android.material.textfield.TextInputEditText
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.app.DatePickerDialog
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.os.Build.VERSION
import android.widget.Button
import androidx.activity.viewModels
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EditLotActivity : AppCompatActivity() {

    private val mCalendar = Calendar.getInstance()
    private lateinit var mDatePicker: OnDateSetListener

    private val editLotViewModel: EditLotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_lot)

        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        val lotModel: LotModel? = if(VERSION.SDK_INT <= 33 ){
            intent.getParcelableExtra("lotModel", LotModel::class.java)
        }else{
            intent.getParcelableExtra("lotModel")
        }

        val lotDescriptionTV = findViewById<TextInputEditText>(R.id.edit_lot_description)
        val customerNameTV = findViewById<TextInputEditText>(R.id.edit_customer_name)
        val dateTV = findViewById<TextInputEditText>(R.id.edit_lot_date)
        val notesTv = findViewById<TextInputEditText>(R.id.edit_notes)
        val updateBtn = findViewById<Button>(R.id.update_pen_btn)

        val cancel = findViewById<Button>(R.id.cancel_update_pen_btn)
        cancel.setOnClickListener { finish() }
        dateTV.setOnClickListener {
            DatePickerDialog(
                this@EditLotActivity,
                mDatePicker,
                mCalendar[Calendar.YEAR],
                mCalendar[Calendar.MONTH],
                mCalendar[Calendar.DAY_OF_MONTH]
            )
                .show()
        }

        mDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month
            mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            dateTV.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        }

        updateBtn.setOnClickListener { v ->
            if (customerNameTV.length() == 0 || lotModel == null || lotDescriptionTV.length() == 0) {
                Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show()
            } else {
                lotModel.lotName = lotDescriptionTV.text.toString()
                lotModel.customerName = customerNameTV.text.toString()
                lotModel.date = mCalendar.timeInMillis
                lotModel.notes = notesTv.text.toString()

                editLotViewModel.onEvent(EditLotUiEvent.OnLotUpdated(lotModel))

                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        if(lotModel != null) {
            val longDate = lotModel.date
            mCalendar.timeInMillis = longDate
            val date = Utility.convertMillisToDate(longDate)
            lotDescriptionTV.setText(lotModel.lotName)
            customerNameTV.setText(lotModel.customerName)
            dateTV.setText(date)
            notesTv.setText(lotModel.notes)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}