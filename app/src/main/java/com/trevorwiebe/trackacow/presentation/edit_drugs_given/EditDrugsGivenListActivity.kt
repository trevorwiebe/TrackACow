package com.trevorwiebe.trackacow.presentation.edit_drugs_given

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.presentation.activities.AddDrugsGivenToSpecificCowActivity
import com.trevorwiebe.trackacow.presentation.activities.EditDrugsGivenToSpecificCowActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditDrugsGivenListActivity : AppCompatActivity() {

    private lateinit var mDrugsGivenRv: RecyclerView
    private lateinit var mNoDrugsGiven: TextView
    private lateinit var mAddNewDrugGiven: FloatingActionButton

    private lateinit var drugsGivenRecyclerViewAdapter: DrugsGivenRecyclerViewAdapter
    private var mDrugsAndDrugsGivenList: List<DrugsGivenAndDrugModel> = emptyList()
    private var cowId: String? = null

    @Inject
    lateinit var drugsGivenListViewModelFactory: EditDrugsGivenListViewModel.EditDrugsGivenViewModelFactory

    private val editDrugsGivenViewModel: EditDrugsGivenListViewModel by viewModels {
        EditDrugsGivenListViewModel.providesFactory(
            assistedFactory = drugsGivenListViewModelFactory,
            cowId = intent.getStringExtra("cowId") ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drugs_given)

        cowId = intent.getStringExtra("cowId")
        mNoDrugsGiven = findViewById(R.id.edit_drugs_no_drugs_given)
        mAddNewDrugGiven = findViewById(R.id.edit_drugs_add_new_drug)

        mDrugsGivenRv = findViewById(R.id.drugs_given_rv)
        mDrugsGivenRv.layoutManager = LinearLayoutManager(this)
        drugsGivenRecyclerViewAdapter = DrugsGivenRecyclerViewAdapter()
        mDrugsGivenRv.adapter = drugsGivenRecyclerViewAdapter
        mDrugsGivenRv.addOnItemTouchListener(
            ItemClickListener(
                this,
                mDrugsGivenRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val drugsGivenId = mDrugsAndDrugsGivenList[position].drugsGivenId
                        val editDrugIntent = Intent(
                            this@EditDrugsGivenListActivity,
                            EditDrugsGivenToSpecificCowActivity::class.java
                        )
                        editDrugIntent.putExtra("cowId", cowId)
                        editDrugIntent.putExtra("drugGivenId", drugsGivenId)
                        startActivity(editDrugIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )
        mAddNewDrugGiven.setOnClickListener {
            val addNewDrugIntent =
                Intent(
                    this@EditDrugsGivenListActivity,
                    AddDrugsGivenToSpecificCowActivity::class.java
                )
            addNewDrugIntent.putExtra("cowId", cowId)
            startActivity(addNewDrugIntent)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                editDrugsGivenViewModel.uiState.collect { uiState ->

                    mDrugsAndDrugsGivenList = uiState.drugsGivenAndDrugsList

                    if (mDrugsAndDrugsGivenList.isEmpty()) {
                        mNoDrugsGiven.visibility = View.VISIBLE
                        mDrugsGivenRv.visibility = View.INVISIBLE
                    } else {
                        mNoDrugsGiven.visibility = View.INVISIBLE
                        mDrugsGivenRv.visibility = View.VISIBLE
                        drugsGivenRecyclerViewAdapter.swapData(mDrugsAndDrugsGivenList)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}