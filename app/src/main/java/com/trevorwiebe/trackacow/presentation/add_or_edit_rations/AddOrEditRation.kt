package com.trevorwiebe.trackacow.presentation.add_or_edit_rations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrEditRation : AppCompatActivity() {

    private val TAG = "AddOrEditRation"

    private lateinit var addRationBtn: Button
    private lateinit var cancelRationBtn: Button
    private lateinit var addRationTV: TextInputEditText

    private val addOrEditViewModel: AddOrEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_ration)

        val addOrEdit = intent.getIntExtra("add_or_edit", Constants.ADD_RATION)

        addRationBtn = findViewById(R.id.add_ration_add_btn)
        cancelRationBtn = findViewById(R.id.add_ration_cancel_btn)
        addRationTV = findViewById(R.id.add_ration_name_tv)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(addOrEdit == Constants.ADD_RATION) {
            supportActionBar?.title = getString(R.string.add_ration)
            addRationBtn.text = getString(R.string.add_ration)
        }else{
            supportActionBar?.title = getString(R.string.edit_ration)
            addRationBtn.text = getString(R.string.update_ration)
        }

        addRationBtn.setOnClickListener{
            // sanitize the input
            val rationText = addRationTV.text
            if(rationText == null || addRationTV.text!!.isEmpty()){
                addRationTV.requestFocus()
                addRationTV.error = this.getString(R.string.please_fill_blank)
            }
            // create ration object
            val rationModel = RationModel(0, "", rationText.toString())
            addOrEditViewModel.onEvent(AddOrEditRationsEvents.OnRationAdded(rationModel))

            addRationTV.setText("")

            Snackbar.make(it, getString(R.string.ration_saved), Snackbar.LENGTH_LONG).show()

        }

        cancelRationBtn.setOnClickListener{ finish() }
    }
}