package com.trevorwiebe.trackacow.presentation.edit_drugs_given_to_specific

import android.os.Build.VERSION
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDrugsGivenToSpecificCowActivity : AppCompatActivity() {

    private lateinit var mCancelButton: Button
    private lateinit var mSaveButton: Button
    private lateinit var mDrugName: TextView
    private lateinit var mAmountGiven: TextInputEditText

    private var mDrugGivenAndDrug: DrugsGivenAndDrugModel? = null

    private val editDrugsGivenToSpecificCowViewModel: EditDrugsGivenToSpecificCowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drugs_given_to_specific_cow)
        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        val cowModel = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("cowModel", CowModel::class.java)
        } else {
            intent.getParcelableExtra("cowModel")
        }

        @Suppress("DEPRECATION")
        mDrugGivenAndDrug = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("drugGivenAndDrugModel", DrugsGivenAndDrugModel::class.java)
        } else {
            intent.getParcelableExtra("drugGivenAndDrugModel")
        }

        mCancelButton = findViewById(R.id.edit_drug_cancel_button)
        mSaveButton = findViewById(R.id.edit_drug_save_button)
        mDrugName = findViewById(R.id.name_of_drug_given)
        mAmountGiven = findViewById(R.id.edit_drug_given_amount_given)

        mCancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        mSaveButton.setOnClickListener { v ->
            if (mAmountGiven.length() == 0) {
                Snackbar.make(
                    v,
                    resources.getString(R.string.please_fill_blank),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                val amountGiven = mAmountGiven.text.toString().toInt()
                mDrugGivenAndDrug?.drugsGivenAmountGiven = amountGiven

                editDrugsGivenToSpecificCowViewModel.onEvent(
                    EditDrugsGivenToSpecificCowEvents.OnDrugGivenEdited(
                        mDrugGivenAndDrug?.toDrugGivenModel()
                    )
                )
            }
            setResult(RESULT_OK)
            finish()
        }

        val tagNumber = cowModel?.tagNumber.toString()
        title = resources.getString(R.string.edit_cow_title, tagNumber)

        val amountGiven = mDrugGivenAndDrug?.drugsGivenAmountGiven
        val amountGivenStr = amountGiven.toString()
        val length = amountGivenStr.length
        mAmountGiven.setText(amountGivenStr)
        mAmountGiven.setSelection(0, length)
        mAmountGiven.requestFocus()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_drugs_given_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_delete_drug_given) {

            editDrugsGivenToSpecificCowViewModel.onEvent(
                EditDrugsGivenToSpecificCowEvents
                    .OnDrugGivenDelete(mDrugGivenAndDrug?.toDrugGivenModel())
            )

            setResult(RESULT_OK)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}