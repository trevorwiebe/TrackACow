package com.trevorwiebe.trackacow.presentation.edit_drugs_given

import android.content.Intent
import android.os.Build.VERSION
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
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.presentation.add_drugs_given_to_specific_cow.AddDrugsGivenToSpecificCowActivity
import com.trevorwiebe.trackacow.presentation.edit_drugs_given_to_specific.EditDrugsGivenToSpecificCowActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditDrugsGivenListActivity : AppCompatActivity() {

    private lateinit var mDrugsGivenRv: RecyclerView
    private lateinit var mNoDrugsGiven: TextView
    private lateinit var mAddNewDrugGiven: FloatingActionButton

    private lateinit var drugsGivenRecyclerViewAdapter: DrugsGivenRecyclerViewAdapter
    private var mCowModel: CowModel? = null
    private var mDrugsGivenList = emptyList<DrugsGivenAndDrugModel>()

    @Inject
    lateinit var editDrugsGivenListViewModelFactory: EditDrugsGivenListViewModel.EditDrugsGivenListViewModelFactory

    @Suppress("DEPRECATION")
    private val editDrugsGivenListViewModel: EditDrugsGivenListViewModel by viewModels {
        EditDrugsGivenListViewModel.providesFactory(
                assistedFactory = editDrugsGivenListViewModelFactory,
                cowId = if (VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra("cowModel", CowModel::class.java)
                } else {
                    intent.getParcelableExtra("cowModel")
                }?.cowId ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drugs_given)

        @Suppress("DEPRECATION")
        mCowModel = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("cowModel", CowModel::class.java)
        } else {
            intent.getParcelableExtra("cowModel")
        }

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
                        val drugsGivenAndDrugModel = mDrugsGivenList[position]
                        val editDrugIntent = Intent(
                                this@EditDrugsGivenListActivity,
                                EditDrugsGivenToSpecificCowActivity::class.java
                        )
                        editDrugIntent.putExtra("cowModel", mCowModel)
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
            addNewDrugIntent.putExtra("cowModel", mCowModel)
            startActivity(addNewDrugIntent)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                editDrugsGivenListViewModel.uiState.collect {

                    mDrugsGivenList = it.drugsGivenList

                    if (mDrugsGivenList.isEmpty()) {
                        mNoDrugsGiven.visibility = View.VISIBLE
                        mDrugsGivenRv.visibility = View.INVISIBLE
                    } else {
                        mNoDrugsGiven.visibility = View.INVISIBLE
                        mDrugsGivenRv.visibility = View.VISIBLE
                        drugsGivenRecyclerViewAdapter.swapData(mDrugsGivenList)
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