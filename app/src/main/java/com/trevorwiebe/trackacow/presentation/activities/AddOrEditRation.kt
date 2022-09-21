package com.trevorwiebe.trackacow.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.utils.Constants

class AddOrEditRation : AppCompatActivity() {

    private val TAG = "AddOrEditRation"

    private lateinit var addRationBtn: Button
    private lateinit var cancelRationBtn: Button
    private lateinit var addRationTV: TextInputEditText

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


            val rationModel: RationModel = RationModel(0, "", rationText.toString())



        }

        cancelRationBtn.setOnClickListener{ finish() }
    }
}