package com.trevorwiebe.trackacow.presentation.add_load_of_cattle

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddLoadOfCattleActivity : AppCompatActivity() {

    private val mAddLoadOfCattleViewModel: AddLoadOfCattleViewModel by viewModels()

    private lateinit var mHeadCount: TextInputEditText
    private lateinit var mDate: TextInputEditText
    private lateinit var mMemo: TextInputEditText
    private lateinit var mSaveButton: Button

    private val mCalendar = Calendar.getInstance()
    private var mDatePicker: OnDateSetListener? = null
    private var mPenAndLotModel: PenAndLotModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_load_of_cattle)

        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        mPenAndLotModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
        } else {
            intent.getParcelableExtra("penAndLotModel")
        }

        mHeadCount = findViewById(R.id.load_head_count)
        mDate = findViewById(R.id.load_date)
        mMemo = findViewById(R.id.load_memo)
        mSaveButton = findViewById(R.id.save_load_of_cattle)
        val date = Utility.convertMillisToDate(System.currentTimeMillis())
        mDate.setText(date)
        mDate.setOnClickListener {
            DatePickerDialog(
                this@AddLoadOfCattleActivity,
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
            mDate.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        }
        mSaveButton.setOnClickListener { v ->
            if (mHeadCount.length() == 0 || mDate.length() == 0) {
                Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show()
            } else {
                val totalHead = mHeadCount.text.toString().toInt()
                mCalendar[Calendar.HOUR_OF_DAY] = 0
                mCalendar[Calendar.MINUTE] = 0
                mCalendar[Calendar.SECOND] = 0
                mCalendar[Calendar.MILLISECOND] = 0
                val longDate = mCalendar.timeInMillis
                val loadDescription = mMemo.text.toString()
                val lotId = mPenAndLotModel?.lotCloudDatabaseId

                val loadModel = LoadModel(0, totalHead, longDate, loadDescription, lotId, "")
                mAddLoadOfCattleViewModel.onEvent(AddLoadOfCattleEvents.OnLoadCreated(loadModel))

                setResult(RESULT_OK)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(RESULT_CANCELED)
        finish()
        return false
    }
}