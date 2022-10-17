package com.trevorwiebe.trackacow.presentation.add_or_edit_rations

import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private var mRation: RationModel? = null

    private val addOrEditViewModel: AddOrEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_ration)

        @Suppress("DEPRECATION")
        mRation = if(VERSION.SDK_INT >= 33){
            intent.getParcelableExtra("ration_model", RationModel::class.java)
        }else{
            intent.getParcelableExtra("ration_model")
        }

        addOrEditRationBtn = findViewById(R.id.add_ration_add_btn)
        cancelRationBtn = findViewById(R.id.add_ration_cancel_btn)
        addRationTV = findViewById(R.id.add_ration_name_tv)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(mRation != null){
            supportActionBar?.title = getString(R.string.edit_ration)
            addOrEditRationBtn.text = getString(R.string.update)
            addRationTV.setText(mRation?.rationName)
        }else{
            supportActionBar?.title = getString(R.string.add_ration)
            addOrEditRationBtn.text = getString(R.string.add)
        }
        addOrEditRationBtn.setOnClickListener{

            var shouldSave = true

            // sanitize the input
            val rationText = addRationTV.text
            if(rationText == null || addRationTV.text!!.isEmpty()){
                addRationTV.requestFocus()
                addRationTV.error = this.getString(R.string.please_fill_blank)
                shouldSave = false
            }

            if(shouldSave) {
                if (mRation != null) {
                    // update Ration
                    mRation?.rationName = rationText.toString()
                    addOrEditViewModel.onEvent(AddOrEditRationsEvents.OnRationUpdated(mRation!!))
                    addRationTV.setText("")
                } else {
                    // create ration object
                    val rationModel = RationModel(0, "", rationText.toString())
                    addOrEditViewModel.onEvent(AddOrEditRationsEvents.OnRationAdded(rationModel))
                    addRationTV.setText("")
                }
            }
        }
        cancelRationBtn.setOnClickListener{ finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.add_or_edit_rations_menu, menu)
        val deleteBtn = menu?.findItem(R.id.action_delete_ration)
        deleteBtn?.isVisible = mRation != null

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete_ration){
            addOrEditViewModel.onEvent(AddOrEditRationsEvents.OnRationDeleted(mRation!!))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}