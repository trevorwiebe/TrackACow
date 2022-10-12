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
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageDrugsActivity : AppCompatActivity() {

    private var mManageDrugRecyclerViewAdapter: ManageDrugRecyclerViewAdapter? = null
    private var mDrugList: List<DrugModel> = emptyList()

    private val mManageDrugsViewModel: ManageDrugsViewModel by viewModels()

    private lateinit var mManageDrugRv: RecyclerView
    private lateinit var mDrugEmptyList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_drugs)
        mDrugEmptyList = findViewById(R.id.drug_list_empty)

        val addNewDrugFab = findViewById<FloatingActionButton>(R.id.add_new_drug)
        addNewDrugFab.setOnClickListener {
            val addNewDrugIntent = Intent(this@ManageDrugsActivity, AddOrEditDrugActivity::class.java)
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
                        val editDrugIntent = Intent(this@ManageDrugsActivity, AddOrEditDrugActivity::class.java)
                        editDrugIntent.putExtra("drugObject", mDrugList[position])
                        startActivity(editDrugIntent)
                    }
                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mManageDrugsViewModel.uiState.collect{
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