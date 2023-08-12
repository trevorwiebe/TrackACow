package com.trevorwiebe.trackacow.presentation.add_edit_pen

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toPenModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEditPensActivity : AppCompatActivity() {

    private val mAddEditPenViewModel: AddEditPensViewModel by viewModels()

    private var mPenAndLotModel: PenAndLotModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_pens)

        val addPenTv = findViewById<TextInputEditText>(R.id.add_pen_name_tv)
        val cancelBtn = findViewById<Button>(R.id.add_new_pen_cancel)
        val saveBtn = findViewById<Button>(R.id.add_new_pen_save)

        @Suppress("Deprecation")
        mPenAndLotModel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("pen_and_lot", PenAndLotModel::class.java)
        } else {
            intent.getParcelableExtra("pen_and_lot")
        }

        if (mPenAndLotModel == null) {
            saveBtn.text = getString(R.string.save)
        } else {
            saveBtn.text = getString(R.string.update)
            addPenTv.setText(mPenAndLotModel?.penName)
        }

        saveBtn.setOnClickListener {
            val penName = addPenTv.text.toString()
            if (penName.isNotEmpty()) {
                if (mPenAndLotModel != null) {
                    mPenAndLotModel?.penName = penName
                    mAddEditPenViewModel.onEvent(
                        AddEditPensEvent.OnPenUpdated(mPenAndLotModel!!.toPenModel())
                    )
                    finish()
                } else {
                    val penModel = PenModel(
                        0,
                        "",
                        penName
                    )
                    mAddEditPenViewModel.onEvent(
                        AddEditPensEvent.OnPenAdded(penModel)
                    )
                }
                addPenTv.setText("")
            }
        }

        cancelBtn.setOnClickListener { finish() }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_delete_menu, menu)
        val deleteBtn = menu?.findItem(R.id.action_delete)
        deleteBtn?.isVisible = mPenAndLotModel != null
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            if (mPenAndLotModel?.lotCloudDatabaseId == null) {
                mAddEditPenViewModel.onEvent(
                    AddEditPensEvent.OnPenDeleted(mPenAndLotModel!!.toPenModel())
                )
                finish()
            } else {
                val cowsPresentBuilder = MaterialAlertDialogBuilder(this@AddEditPensActivity)
                cowsPresentBuilder.setMessage("There is a lot of cows in this pen, move or archive to continue with deletion.")
                cowsPresentBuilder.setCancelable(true)
                cowsPresentBuilder.setPositiveButton("Ok")
                { dialog, id -> dialog.cancel() }
                val dialog: AlertDialog = cowsPresentBuilder.create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}