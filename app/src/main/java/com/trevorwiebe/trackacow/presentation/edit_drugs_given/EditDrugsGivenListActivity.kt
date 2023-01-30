package com.trevorwiebe.trackacow.presentation.edit_drugs_given

import android.content.Intent
import android.os.Build.VERSION
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.presentation.activities.AddDrugsGivenToSpecificCowActivity
import com.trevorwiebe.trackacow.presentation.edit_drugs_given_to_specific.EditDrugsGivenToSpecificCowActivity
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDrugsGivenListActivity : AppCompatActivity() {

    private lateinit var mDrugsGivenRv: RecyclerView
    private lateinit var mNoDrugsGiven: TextView
    private lateinit var mAddNewDrugGiven: FloatingActionButton

    private lateinit var drugsGivenRecyclerViewAdapter: DrugsGivenRecyclerViewAdapter
    private var mCowUiModel: CowUiModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drugs_given)

        @Suppress("DEPRECATION")
        mCowUiModel = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("cowModel", CowUiModel::class.java)
        } else {
            intent.getParcelableExtra("cowModel")
        }

        val drugsGivenAndDrugModelList = mCowUiModel?.drugsGivenAndDrugModelList ?: emptyList()
        val cowModel = mCowUiModel?.cowModel

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
                        val drugsGivenAndDrugModel = drugsGivenAndDrugModelList[position]
                        val editDrugIntent = Intent(
                            this@EditDrugsGivenListActivity,
                            EditDrugsGivenToSpecificCowActivity::class.java
                        )
                        editDrugIntent.putExtra("cowModel", cowModel)
                        editDrugIntent.putExtra("drugGivenAndDrugModel", drugsGivenAndDrugModel)
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
            addNewDrugIntent.putExtra("cowModel", cowModel)
            startActivity(addNewDrugIntent)
        }

        if (drugsGivenAndDrugModelList.isEmpty()) {
            mNoDrugsGiven.visibility = View.VISIBLE
            mDrugsGivenRv.visibility = View.INVISIBLE
        } else {
            mNoDrugsGiven.visibility = View.INVISIBLE
            mDrugsGivenRv.visibility = View.VISIBLE
            drugsGivenRecyclerViewAdapter.swapData(drugsGivenAndDrugModelList)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}