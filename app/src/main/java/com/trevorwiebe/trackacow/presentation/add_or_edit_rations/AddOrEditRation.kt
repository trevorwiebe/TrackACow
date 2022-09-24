package com.trevorwiebe.trackacow.presentation.add_or_edit_rations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrEditRation : AppCompatActivity() {

    private lateinit var addOrEditRationBtn: Button
    private lateinit var cancelRationBtn: Button
    private lateinit var addRationTV: TextInputEditText

    private var isEditOrDeleteActivity = false
    private var idToEdit: Int = -1

    private val addOrEditViewModel: AddOrEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_ration)

        val addOrEdit = intent.getIntExtra("add_or_edit", Constants.ADD_RATION)

        addOrEditRationBtn = findViewById(R.id.add_ration_add_btn)
        cancelRationBtn = findViewById(R.id.add_ration_cancel_btn)
        addRationTV = findViewById(R.id.add_ration_name_tv)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isEditOrDeleteActivity = addOrEdit == Constants.EDIT_RATION

        if(isEditOrDeleteActivity){
            supportActionBar?.title = getString(R.string.edit_ration)
            addOrEditRationBtn.text = getString(R.string.update)

            val rationName = intent.getStringExtra("ration_name")
            idToEdit = intent.getIntExtra("ration_id", -1)
            addRationTV.setText(rationName)

        }else{
            supportActionBar?.title = getString(R.string.add_ration)
            addOrEditRationBtn.text = getString(R.string.add)
        }
        addOrEditRationBtn.setOnClickListener{
            // sanitize the input
            val rationText = addRationTV.text
            if(rationText == null || addRationTV.text!!.isEmpty()){
                addRationTV.requestFocus()
                addRationTV.error = this.getString(R.string.please_fill_blank)
            }

            if(isEditOrDeleteActivity){
                // update Ration
                val rationModel = RationModel(idToEdit, "", rationText.toString())
                addOrEditViewModel.onEvent(AddOrEditRationsEvents.OnRationUpdated(rationModel))
                addRationTV.setText("")
            }else {
                // create ration object
                val rationModel = RationModel(0, "", rationText.toString())
                addOrEditViewModel.onEvent(AddOrEditRationsEvents.OnRationAdded(rationModel))
                addRationTV.setText("")
            }

            Snackbar.make(it, getString(R.string.ration_saved), Snackbar.LENGTH_LONG).show()

        }
        cancelRationBtn.setOnClickListener{ finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.add_or_edit_rations_menu, menu)
        val deleteBtn = menu?.findItem(R.id.action_delete_ration)
        deleteBtn?.isVisible = isEditOrDeleteActivity

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete_ration){
            if(idToEdit != -1) {
                addOrEditViewModel.onEvent(AddOrEditRationsEvents.OnRationDeleted(idToEdit))
                finish()
            }else{
                Toast.makeText(
                    this@AddOrEditRation,
                    getString(R.string.generic_error),
                    Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}