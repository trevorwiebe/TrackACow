package com.trevorwiebe.trackacow.presentation.add_lot_and_load

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class AddLotAndLoad : AppCompatActivity() {

    private val addLotAndLoadViewModel: AddLotAndLoadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lot_and_load)

        val penId = intent.getStringExtra("pen_id")

        val lotName = findViewById<TextInputEditText>(R.id.add_lot_lot_name)
        val customerName = findViewById<TextInputEditText>(R.id.add_lot_customer_name)
        val lotDate = findViewById<TextInputEditText>(R.id.add_lot_lot_date)
        val lotMemo = findViewById<TextInputEditText>(R.id.add_lot_lot_memo)
        val headCount = findViewById<TextInputEditText>(R.id.add_load_first_load_head_count)
        val loadDate = findViewById<TextInputEditText>(R.id.add_load_first_load_date)
        val loadMemo = findViewById<TextInputEditText>(R.id.add_load_first_load_memo)
        val addBtn = findViewById<Button>(R.id.mark_as_active)

        val lotCalendar = Calendar.getInstance()
        lotDate.setText(Utility.convertMillisToDate(lotCalendar.timeInMillis))
        val lotDatePicker = OnDateSetListener { _, year, month, dayOfMonth ->
            lotCalendar[Calendar.YEAR] = year
            lotCalendar[Calendar.MONTH] = month
            lotCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            lotDate.setText(Utility.convertMillisToDate(lotCalendar.timeInMillis))
        }
        lotDate.setOnClickListener {
            DatePickerDialog(
                this@AddLotAndLoad,
                lotDatePicker,
                lotCalendar[Calendar.YEAR],
                lotCalendar[Calendar.MONTH],
                lotCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        val loadCalendar = Calendar.getInstance()
        loadDate.setText(Utility.convertMillisToDate(loadCalendar.timeInMillis))
        val loadDatePicker = OnDateSetListener { _, year, month, dayOfMonth ->
            loadCalendar[Calendar.YEAR] = year
            loadCalendar[Calendar.MONTH] = month
            loadCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            loadDate.setText(Utility.convertMillisToDate(loadCalendar.timeInMillis))
        }
        loadDate.setOnClickListener {
            DatePickerDialog(
                this@AddLotAndLoad,
                loadDatePicker,
                loadCalendar[Calendar.YEAR],
                loadCalendar[Calendar.MONTH],
                loadCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        addBtn.setOnClickListener {
            var shouldSave = true
            if (lotName.length() == 0) {
                lotName.error = getString(R.string.please_fill_blank)
                shouldSave = false
            }
            if (customerName.length() == 0) {
                customerName.error = getString(R.string.please_fill_blank)
                shouldSave = false
            }
            if (headCount.length() == 0) {
                headCount.error = getString(R.string.please_fill_blank)
                shouldSave = false
            }
            if (penId.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
                shouldSave = false
            }

            if (shouldSave) {
                val lotNameTxt = lotName.text.toString()
                val customerNameTxt = customerName.text.toString()
                val lotDateInt = lotCalendar.timeInMillis
                val lotMemoTxt = lotMemo.text.toString()
                val headCountInt = headCount.text.toString().toInt()
                val loadDateInt = loadCalendar.timeInMillis
                val loadMemoTxt = loadMemo.text.toString()

                val lotModel = LotModel(
                    0,
                    lotNameTxt,
                    "",
                    customerNameTxt,
                    lotMemoTxt,
                    lotDateInt,
                    0,
                    0,
                    penId!!
                )
                val loadModel = LoadModel(0, headCountInt, loadDateInt, loadMemoTxt, "", "")

                addLotAndLoadViewModel.onEvent(AddLotAndLoadEvents.OnPenFilled(lotModel, loadModel))

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                addLotAndLoadViewModel.uiState.collect {
                    if (it.isPenFilled) {
                        finish()
                    }
                }
            }
        }

    }
}