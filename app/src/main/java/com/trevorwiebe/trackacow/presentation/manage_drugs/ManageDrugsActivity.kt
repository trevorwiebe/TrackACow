package com.trevorwiebe.trackacow.presentation.manage_drugs

import androidx.appcompat.app.AppCompatActivity
import com.trevorwiebe.trackacow.domain.adapters.ManageDrugRecyclerViewAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.presentation.add_edit_drug.AddOrEditDrugActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageDrugsActivity : AppCompatActivity() {

    // TODO: fix bug where when offline, new drugs added don't show up until you navigate back and to

    private var mManageDrugRecyclerViewAdapter: ManageDrugRecyclerViewAdapter? = null
    private var mDrugList: List<DrugModel> = emptyList()

    private val mManageDrugsViewModel: ManageDrugsViewModel by viewModels()

    private lateinit var mProgressBar: LinearProgressIndicator
    private lateinit var mManageDrugRv: RecyclerView
    private lateinit var mDrugEmptyList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_drugs)

        mProgressBar = findViewById(R.id.manage_drugs_loading)
        mDrugEmptyList = findViewById(R.id.drug_list_empty)

        val addNewDrugFab = findViewById<FloatingActionButton>(R.id.add_new_drug)
        addNewDrugFab.setOnClickListener {
            val addNewDrugIntent =
                Intent(this@ManageDrugsActivity, AddOrEditDrugActivity::class.java)
            startActivity(addNewDrugIntent)
        }

        mManageDrugRv = findViewById(R.id.manage_drug_rv)
        mManageDrugRv.layoutManager = LinearLayoutManager(this)
        mManageDrugRecyclerViewAdapter = ManageDrugRecyclerViewAdapter(mDrugList, this)
        mManageDrugRv.adapter = mManageDrugRecyclerViewAdapter
        mManageDrugRv.addOnItemTouchListener(
            ItemClickListener(
                this,
                mManageDrugRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val editDrugIntent =
                            Intent(this@ManageDrugsActivity, AddOrEditDrugActivity::class.java)
                        editDrugIntent.putExtra("drugObject", mDrugList[position])
                        startActivity(editDrugIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mManageDrugsViewModel.uiState.collect {

                    if (it.isFetchingFromCloud && it.dataSource == DataSource.Local) {
                        mProgressBar.visibility = View.VISIBLE
                    } else {
                        mProgressBar.visibility = View.GONE
                    }

                    mDrugList = it.drugList

                    if (mDrugList.isEmpty()) {
                        // show drug list empty
                        mDrugEmptyList.visibility = View.VISIBLE
                    } else {
                        // hide drug list empty
                        mDrugEmptyList.visibility = View.INVISIBLE
                    }
                    mManageDrugRecyclerViewAdapter!!.swapData(mDrugList)
                }
            }
        }

    }
}