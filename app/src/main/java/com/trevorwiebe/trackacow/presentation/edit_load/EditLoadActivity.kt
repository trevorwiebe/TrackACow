package com.trevorwiebe.trackacow.presentation.edit_load

import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog.OnDateSetListener
import com.google.android.material.textfield.TextInputEditText
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.app.DatePickerDialog
import com.google.android.material.snackbar.Snackbar
import android.app.AlertDialog
import android.os.Build.VERSION
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

@AndroidEntryPoint
class EditLoadActivity : AppCompatActivity() {

    private val mCalendar = Calendar.getInstance()
    private lateinit var mDatePicker: OnDateSetListener
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    private var mSelectedLoad: LoadModel? = null

    private val editLoadViewModel: EditLoadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_load)

        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val headCountTv = findViewById<TextInputEditText>(R.id.edit_load_head_count)
        val dateTv = findViewById<TextInputEditText>(R.id.edit_load_date)
        val memoTv = findViewById<TextInputEditText>(R.id.edit_load_memo)
        val updateBtn = findViewById<Button>(R.id.edit_load_of_cattle)
        val deleteBtn = findViewById<Button>(R.id.delete_load_of_cattle)

        @Suppress("DEPRECATION")
        mSelectedLoad = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("loadModel", LoadModel::class.java)
        } else {
            intent.getParcelableExtra("loadModel")
        }

        if(mSelectedLoad != null) {
            headCountTv.setText(mSelectedLoad!!.numberOfHead.toString())
            dateTv.setText(Utility.convertMillisToDate(mSelectedLoad!!.date))
            memoTv.setText(mSelectedLoad!!.description)
            mCalendar.timeInMillis = mSelectedLoad!!.date
        }

        dateTv.setOnClickListener {
            DatePickerDialog(
                this@EditLoadActivity,
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
            dateTv.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        }

        updateBtn.setOnClickListener { v ->
            if (headCountTv.length() == 0 || dateTv.length() == 0) {
                Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show()
            } else {
                var headCount = 0
                try {
                    headCount = numberFormat.parse(headCountTv.text.toString())?.toInt() ?: 0
                } catch (e: ParseException) {
                    Log.e(TAG, "onClick: ", e)
                }

                if(mSelectedLoad != null) {

                    mSelectedLoad!!.numberOfHead = headCount
                    mSelectedLoad!!.date = mCalendar.timeInMillis
                    mSelectedLoad!!.description = memoTv.text.toString()

                    editLoadViewModel.onEvent(EditLoadUiEvent.OnLoadUpdated(mSelectedLoad!!))
                }

                setResult(RESULT_OK)
                finish()
            }
        }

        deleteBtn.setOnClickListener {
            val deleteCattle = AlertDialog.Builder(this@EditLoadActivity)
            deleteCattle.setTitle("Are you sure?")
            deleteCattle.setMessage("You are about to delete a group of cattle.  You can not undo this action.")
            deleteCattle.setPositiveButton("Delete") { dialog, which ->

                if(mSelectedLoad != null) {
                    editLoadViewModel.onEvent(EditLoadUiEvent.OnLoadDeleted(mSelectedLoad!!))
                }

                setResult(RESULT_OK)
                finish()
            }
            deleteCattle.setNegativeButton("Cancel") { dialog, which -> }
            deleteCattle.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(RESULT_CANCELED)
        finish()
        return false
    }

    companion object {
        private const val TAG = "EditLoadActivity"
    }
}