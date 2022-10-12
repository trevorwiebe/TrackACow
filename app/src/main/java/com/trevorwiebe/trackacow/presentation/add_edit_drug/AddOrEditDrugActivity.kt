package com.trevorwiebe.trackacow.presentation.add_edit_drug

import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrEditDrugActivity : AppCompatActivity() {

    private val mAddOrEditDrugViewModel: AddOrEditDrugViewModel by viewModels()

    private lateinit var mDrugName: TextInputEditText
    private lateinit var mDefaultAmount: TextInputEditText

    private var mDrugModel: DrugModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_drug)

        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        mDrugModel = if(VERSION.SDK_INT >= 33 ){
            intent.getParcelableExtra("drugObject", DrugModel::class.java)
        }else {
            intent.getParcelableExtra("drugObject")
        }

        mDrugName = findViewById(R.id.add_drug_name)
        mDefaultAmount = findViewById(R.id.default_amount_given)

        val saveDrug = findViewById<Button>(R.id.save_drug)
        if(mDrugModel == null){
            saveDrug.text = getString(R.string.save)
        }else{
            saveDrug.text = getString(R.string.update)
            mDrugName.setText(mDrugModel?.drugName)
            mDefaultAmount.setText(mDrugModel?.defaultAmount.toString())
        }

        val cancel = findViewById<Button>(R.id.add_new_drug_cancel)

        saveDrug.setOnClickListener {
            if (mDrugName.length() == 0) {
                mDrugName.error = "Please fill the blank"
                mDrugName.requestFocus()
            } else if (mDefaultAmount.length() == 0) {
                mDefaultAmount.error = "Please fill the blank"
                mDefaultAmount.requestFocus()
            } else {

                val drugName = mDrugName.text.toString()
                val defaultGiven = mDefaultAmount.text.toString().toInt()

                val drugModel: DrugModel
                if(mDrugModel != null){
                    drugModel = mDrugModel as DrugModel
                    drugModel.drugName = drugName
                    drugModel.defaultAmount = defaultGiven
                    mAddOrEditDrugViewModel.onEvent(AddOrEditDrugUiEvent.OnDrugUpdated(drugModel))
                }else {
                    drugModel = DrugModel(0, defaultGiven, "", drugName)
                    mAddOrEditDrugViewModel.onEvent(AddOrEditDrugUiEvent.OnDrugAdded(drugModel))
                }

                mDrugName.setText("")
                mDefaultAmount.setText("")
                mDrugName.requestFocus()
            }
        }
        cancel.setOnClickListener { finish() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}