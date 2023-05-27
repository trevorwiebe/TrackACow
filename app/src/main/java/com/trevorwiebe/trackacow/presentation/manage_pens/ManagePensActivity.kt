package com.trevorwiebe.trackacow.presentation.manage_pens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.trevorwiebe.trackacow.presentation.fragment_medicate.PenRecyclerViewAdapter
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import com.google.android.material.textfield.TextInputEditText
import android.view.View
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.presentation.add_edit_pen.AddEditPensActivity
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
                .setTitle(resources.getString(R.string.add_pen))
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
                            penName.error = resources.getString(R.string.name_already_used)
                        }
                    } else {
                        penName.requestFocus()
                        penName.error = resources.getString(R.string.please_fill_blank)
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
                        val add_edit_pen_intent =
                            Intent(this@ManagePensActivity, AddEditPensActivity::class.java)
                        startActivity(add_edit_pen_intent)
//                        val selectedPenAndLotModel = mPenAndLotList[position]
//
//                        val dialogView = LayoutInflater.from(this@ManagePensActivity)
//                            .inflate(R.layout.dialog_add_new_pen, null)
//                        val editPenEditText =
//                            dialogView.findViewById<EditText>(R.id.dialog_add_new_pen_edit_text)
//                        editPenEditText.setText(selectedPenAndLotModel.penName)
//                        editPenEditText.setSelectAllOnFocus(true)
//                        editPenEditText.setSelection(selectedPenAndLotModel.penName.length)
//                        val editPen = AlertDialog.Builder(this@ManagePensActivity)
//                            .setTitle("Edit pen")
//                            .setView(dialogView)
//                            .setPositiveButton(resources.getString(R.string.update), null)
//                            .setNegativeButton(resources.getString(R.string.cancel), null)
//                            .setNeutralButton(resources.getString(R.string.delete), null)
//                            .create()
//
//                        editPen.setOnShowListener { dialog ->
//
//                            // Update PenModel
//                            val posBtn = editPen.getButton(AlertDialog.BUTTON_POSITIVE)
//                            posBtn.setOnClickListener {
//                                if (editPenEditText.length() == 0) {
//                                    editPenEditText.requestFocus()
//                                    editPenEditText.error =
//                                        resources.getString(R.string.please_fill_blank)
//                                } else {
//                                    val updatedText = editPenEditText.text.toString()
//                                    if (isPenNameAvailable(updatedText, mPenAndLotList)) {
//                                        selectedPenAndLotModel.penName = updatedText
//
//                                        managePensViewModel.onEvent(ManagePenEvents.OnPenUpdated(selectedPenAndLotModel.toPenModel()))
//
//                                        editPen.dismiss()
//                                    } else {
//                                        editPenEditText.requestFocus()
//                                        editPenEditText.error =
//                                            resources.getString(R.string.name_already_used)
//                                    }
//                                }
//                            }
//
//                            // Dismiss dialog
//                            val negButton = editPen.getButton(AlertDialog.BUTTON_NEGATIVE)
//                            negButton.setOnClickListener { dialog.dismiss() }
//
//                            // Delete PenModel
//                            val neuButton = editPen.getButton(AlertDialog.BUTTON_NEUTRAL)
//                            neuButton.setOnClickListener {
//
//                                if (selectedPenAndLotModel.lotCloudDatabaseId == null) {
//                                    managePensViewModel.onEvent(ManagePenEvents.OnPenDeleted(selectedPenAndLotModel.toPenModel()))
//                                    editPen.dismiss()
//                                } else {
//
//                                    val lotOfCowsInThisPen =
//                                        AlertDialog.Builder(this@ManagePensActivity)
//                                    lotOfCowsInThisPen.setTitle(
//                                        resources.getString(R.string.still_cows_title)
//                                    )
//                                    lotOfCowsInThisPen.setMessage(
//                                        resources.getString(R.string.still_cows_body)
//                                    )
//                                    lotOfCowsInThisPen.setPositiveButton(resources.getString(R.string.ok)) { dialog, which -> editPen.dismiss() }
//                                    lotOfCowsInThisPen.show()
//
//                                }
//                            }
//                        }
//                        editPen.show()
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        // TODO: fix issue where pen list does not update when pen is edited
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                managePensViewModel.uiState.collect {

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
    }

    private fun isPenNameAvailable(penName: String, penList: List<PenAndLotModel>): Boolean {
        for (r in penList.indices) {
            val (_, _, penName1) = penList[r]
            if (penName1 == penName) return false
        }
        return true
    }
}