package com.trevorwiebe.trackacow.presentation.manage_pens

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.trevorwiebe.trackacow.presentation.fragment_medicate.PenRecyclerViewAdapter
import android.os.Bundle
import android.util.Log
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import com.google.android.material.textfield.TextInputEditText
import android.view.View
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toPenModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManagePensActivity : AppCompatActivity(){

    private val managePensViewModel: ManagePensViewModel by viewModels()

    private var mPenAndLotList: List<PenAndLotModel> = emptyList()

    private lateinit var mPensRv: RecyclerView
    private lateinit var mEmptyTv: TextView

    private var mPenRecyclerViewAdapter: PenRecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_pens)

        mEmptyTv = findViewById(R.id.empty_pen_rv)

        mPensRv = findViewById(R.id.manage_pens_rv)
        mPensRv.layoutManager = LinearLayoutManager(this)
        mPenRecyclerViewAdapter = PenRecyclerViewAdapter(mPenAndLotList, false, this)
        mPensRv.adapter = mPenRecyclerViewAdapter

        val managePensFab = findViewById<FloatingActionButton>(R.id.manage_pens_fab)
        managePensFab.setOnClickListener {
            val editTextView = LayoutInflater.from(this@ManagePensActivity)
                .inflate(R.layout.dialog_add_new_pen, null)
            val penName =
                editTextView.findViewById<TextInputEditText>(R.id.dialog_add_new_pen_edit_text)
            val addNewPenDialog = AlertDialog.Builder(this@ManagePensActivity)
                .setTitle("Add new pen")
                .setView(editTextView)
                .setPositiveButton(getString(R.string.save), null)
                .create()

            addNewPenDialog.setOnShowListener {
                val button = addNewPenDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.setOnClickListener {
                    if (penName.length() != 0) {
                        val penNameText = penName.text.toString()
                        if (isPenNameAvailable(penNameText, mPenAndLotList)) {

                            val penModel = PenModel(0, "", penNameText)

                            managePensViewModel.onEvent(ManagePenEvents.OnPenSaved(penModel))

                            addNewPenDialog.dismiss()
                        } else {
                            penName.requestFocus()
                            penName.error = "Name used already"
                        }
                    } else {
                        penName.requestFocus()
                        penName.error = "Please fill the blank"
                    }
                }
            }

            addNewPenDialog.show()
        }

        mPensRv.addOnItemTouchListener(
            ItemClickListener(
                this,
                mPensRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val selectedPenAndLotModel = mPenAndLotList[position]

                        Log.d("TAG", "onItemClick: $selectedPenAndLotModel")

                        val dialogView = LayoutInflater.from(this@ManagePensActivity)
                            .inflate(R.layout.dialog_add_new_pen, null)
                        val editPenEditText =
                            dialogView.findViewById<EditText>(R.id.dialog_add_new_pen_edit_text)
                        editPenEditText.setText(selectedPenAndLotModel.penName)
                        editPenEditText.setSelectAllOnFocus(true)
                        editPenEditText.setSelection(selectedPenAndLotModel.penName.length)
                        val editPen = AlertDialog.Builder(this@ManagePensActivity)
                            .setTitle("Edit pen")
                            .setView(dialogView)
                            .setPositiveButton("Update", null)
                            .setNegativeButton("Cancel", null)
                            .setNeutralButton("Delete", null)
                            .create()

                        editPen.setOnShowListener { dialog ->

                            // Update PenModel
                            val posBtn = editPen.getButton(AlertDialog.BUTTON_POSITIVE)
                            posBtn.setOnClickListener {
                                if (editPenEditText.length() == 0) {
                                    editPenEditText.requestFocus()
                                    editPenEditText.error = "Please fill the blank"
                                } else {
                                    val updatedText = editPenEditText.text.toString()
                                    if (isPenNameAvailable(updatedText, mPenAndLotList)) {
                                        selectedPenAndLotModel.penName = updatedText

                                        managePensViewModel.onEvent(ManagePenEvents.OnPenUpdated(selectedPenAndLotModel.toPenModel()))

                                        editPen.dismiss()
                                    } else {
                                        editPenEditText.requestFocus()
                                        editPenEditText.error = "Name already taken"
                                    }
                                }
                            }

                            // Dismiss dialog
                            val negButton = editPen.getButton(AlertDialog.BUTTON_NEGATIVE)
                            negButton.setOnClickListener { dialog.dismiss() }

                            // Delete PenModel
                            val neuButton = editPen.getButton(AlertDialog.BUTTON_NEUTRAL)
                            neuButton.setOnClickListener {

                                if (selectedPenAndLotModel.lotCloudDatabaseId == null) {
                                    managePensViewModel.onEvent(ManagePenEvents.OnPenDeleted(selectedPenAndLotModel.toPenModel()))
                                    editPen.dismiss()
                                } else {

                                    val lotOfCowsInThisPen =
                                        AlertDialog.Builder(this@ManagePensActivity)
                                    lotOfCowsInThisPen.setTitle("There are cows in this pen")
                                    lotOfCowsInThisPen.setMessage("You can't delete this pen when there are still cows in it.  Move or archive the lot of cows to continue with deletion.")
                                    lotOfCowsInThisPen.setPositiveButton("Ok") { dialog, which -> editPen.dismiss() }
                                    lotOfCowsInThisPen.show()

                                }
                            }
                        }
                        editPen.show()
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch{
            managePensViewModel.uiState.collect{
                mPenAndLotList = it.penList
                mPenRecyclerViewAdapter!!.swapData(mPenAndLotList)

                if (mPenAndLotList.isNotEmpty()) {
                    mEmptyTv.visibility = View.INVISIBLE
                } else {
                    mEmptyTv.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isPenNameAvailable(penName: String, penList: List<PenAndLotModel>): Boolean {
        for (r in penList.indices) {
            val (_, _, penName1) = penList[r]
            if (penName1 == penName) return false
        }
        return true
    }
}