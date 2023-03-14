package com.trevorwiebe.trackacow.presentation.mark_a_cow_dead

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MarkACowDeadActivity : AppCompatActivity() {

    private lateinit var mDeadCowCard: CardView
    private lateinit var mTagNumber: TextInputEditText
    private lateinit var mDate: TextInputEditText
    private lateinit var mNotes: TextInputEditText
    private lateinit var mAddNotesLayout: TextInputLayout
    private lateinit var mAddNotes: Button
    private lateinit var mStartDatePicker: OnDateSetListener

    private var mCalendar = Calendar.getInstance()
    private var mDeadCowList: MutableList<CowModel> = mutableListOf()
    private var mPenAndLotModel: PenAndLotModel? = null

    @Inject
    lateinit var markACowDeadViewModelFactory: MarkACowDeadViewModel.MarkACowDeadViewModelFactory

    @Suppress("DEPRECATION")
    private val markACowDeadViewModel: MarkACowDeadViewModel by viewModels {
        MarkACowDeadViewModel.providesFactory(
            assistedFactory = markACowDeadViewModelFactory,
            lotId = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
            } else {
                intent.getParcelableExtra("penAndLotModel")
            }
                ?.lotCloudDatabaseId ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_a_cow_dead)

        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        mPenAndLotModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
        } else {
            intent.getParcelableExtra("penAndLotModel")
        }

        mDeadCowCard = findViewById(R.id.cow_is_dead_already_card)
        mTagNumber = findViewById(R.id.deads_tag_number)
        mDate = findViewById(R.id.deads_date)
        mNotes = findViewById(R.id.deads_notes)
        mAddNotesLayout = findViewById(R.id.dead_notes_layout)
        mAddNotes = findViewById(R.id.dead_add_notes_btn)
        mTagNumber.setOnEditorActionListener(doneListener)
        mNotes.setOnEditorActionListener(doneListener)
        mDate.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        mDate.setOnClickListener {
            DatePickerDialog(
                this@MarkACowDeadActivity,
                mStartDatePicker,
                mCalendar[Calendar.YEAR],
                mCalendar[Calendar.MONTH],
                mCalendar[Calendar.DAY_OF_MONTH]
            )
                .show()
        }

        mStartDatePicker = OnDateSetListener { _, year, month, dayOfMonth ->
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month
            mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            mDate.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        }

        mTagNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {


                    if (mDeadCowList.any { it.tagNumber == s.toString().toInt() }) {
                        mDeadCowCard.visibility = View.VISIBLE
                    } else {
                        mDeadCowCard.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mAddNotes.setOnClickListener {
            mAddNotesLayout.visibility = View.VISIBLE
            mAddNotes.visibility = View.GONE
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                markACowDeadViewModel.uiState.collect {
                    mDeadCowList = it.deadCowList.toMutableList()
                }
            }
        }

    }

    private var doneListener = OnEditorActionListener { v, actionId, event ->
        if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
            markAsDead(null)
        }
        false
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    fun markAsDead(view: View?) {
        if (mTagNumber.length() == 0 || mDate.length() == 0) {
            mTagNumber.requestFocus()
            mTagNumber.error = "Please fill the blank"
            return
        }
        val tagNumber = mTagNumber.text.toString().toInt()
        val notes = mNotes.text.toString()

        val cowModel = CowModel(
            0,
            0,
            "",
            tagNumber,
            mCalendar.timeInMillis,
            notes,
            mPenAndLotModel?.lotCloudDatabaseId ?: ""
        )

        markACowDeadViewModel.onEvent(MarkACowDeadEvent.OnDeadCowCreated(cowModel))

        mDeadCowList.add(cowModel)

        mAddNotes.visibility = View.VISIBLE

        mDeadCowCard.visibility = View.GONE

        mAddNotesLayout.visibility = View.GONE
        mTagNumber.setText("")
        mNotes.setText("")
        mCalendar = Calendar.getInstance()
        mDate.setText(Utility.convertMillisToDate(mCalendar.timeInMillis))
        mTagNumber.requestFocus()
    }
}