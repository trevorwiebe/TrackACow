package com.trevorwiebe.trackacow.presentation.medicated_cows

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.getbase.floatingactionbutton.FloatingActionsMenu
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import android.content.Intent
import android.os.Build.VERSION
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.addLotModel
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toLotModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.presentation.add_load_of_cattle.AddLoadOfCattleActivity
import com.trevorwiebe.trackacow.presentation.edit_lot.EditLotActivity
import com.trevorwiebe.trackacow.presentation.edit_medicated_cow.EditMedicatedCowActivity
import com.trevorwiebe.trackacow.presentation.mark_a_cow_dead.MarkACowDeadActivity
import com.trevorwiebe.trackacow.presentation.medicate_a_cow.MedicateACowActivity
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MedicatedCowsActivity : AppCompatActivity() {

    private var mMedicatedCowsRecyclerViewAdapter: MedicatedCowsRecyclerViewAdapter? = null
    private var mPenAndLotModel: PenAndLotModel? = null
    private var shouldShowCouldntFindTag = false
    private lateinit var mCowUiModelList: List<CowUiModel>
    private lateinit var mFilteredCowUiModelList: List<CowUiModel>

    private lateinit var mProgressIndicator: LinearProgressIndicator
    private lateinit var mNoMedicatedCows: TextView
    private lateinit var mSearchView: SearchView
    private lateinit var mMedicatedCows: RecyclerView
    private lateinit var mResultsNotFound: CardView
    private lateinit var mMedicateACowFabMenu: FloatingActionsMenu

    @Inject lateinit var medicatedCowsViewModelFactory: MedicatedCowsViewModel.MedicatedCowsViewModelFactory

    @Suppress("DEPRECATION")
    private val medicatedCowsViewModel: MedicatedCowsViewModel by viewModels{
        MedicatedCowsViewModel.providesFactory(
            assistedFactory = medicatedCowsViewModelFactory,
            lotId = if (VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
            } else {
                intent.getParcelableExtra("penAndLotModel")
            }
                ?.lotCloudDatabaseId ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicated_cows)

        mProgressIndicator = findViewById(R.id.medicated_cow_progress_indicator)
        mMedicateACowFabMenu = findViewById(R.id.floating_action_btn_menu)
        mNoMedicatedCows = findViewById(R.id.no_medicated_cows_tv)
        mResultsNotFound = findViewById(R.id.result_not_found)
        mMedicatedCows = findViewById(R.id.track_cow_rv)
        mMedicatedCows.layoutManager = LinearLayoutManager(this)
        mMedicatedCowsRecyclerViewAdapter = MedicatedCowsRecyclerViewAdapter(emptyList(), this)
        mMedicatedCows.adapter = mMedicatedCowsRecyclerViewAdapter
        mMedicatedCows.addOnItemTouchListener(
            ItemClickListener(
                this,
                mMedicatedCows,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val cowUiModel = mFilteredCowUiModelList[position]
                        val editCowIntent = Intent(
                            this@MedicatedCowsActivity,
                            EditMedicatedCowActivity::class.java
                        )
                        editCowIntent.putExtra("cowId", cowUiModel.cowModel.cowId)
                        startActivity(editCowIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        @Suppress("DEPRECATION")
        mPenAndLotModel = if (VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("penAndLotModel", PenAndLotModel::class.java)
        } else {
            intent.getParcelableExtra("penAndLotModel")
        }

        title = "Pen: ${mPenAndLotModel?.penName}"
        supportActionBar?.subtitle = mPenAndLotModel?.lotName

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                medicatedCowsViewModel.uiState.collect {

                    if (it.isFetchingCowFromCloud && it.cowDataSource == DataSource.Local) {
                        mProgressIndicator.visibility = View.VISIBLE
                    } else {
                        mProgressIndicator.visibility = View.GONE
                    }

                    mCowUiModelList =
                        if (it.cowUiModelList.isNullOrEmpty()) emptyList() else it.cowUiModelList
                    if (mCowUiModelList.isEmpty()) {
                        mNoMedicatedCows.visibility = View.VISIBLE
                    } else {
                        mNoMedicatedCows.visibility = View.INVISIBLE
                    }

                    mFilteredCowUiModelList = mCowUiModelList

                    mMedicatedCowsRecyclerViewAdapter!!.swapData(mCowUiModelList)

                    if (it.selectedLot != null) {
                        mPenAndLotModel = mPenAndLotModel?.addLotModel(it.selectedLot)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSearchView.setQuery("", false)
        mSearchView.isIconified = true
        mResultsNotFound.visibility = View.INVISIBLE
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.medicated_cows_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        mSearchView = (searchItem.actionView as SearchView?)!!
        mSearchView.inputType = InputType.TYPE_CLASS_NUMBER

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.isNotEmpty()) {
                    mFilteredCowUiModelList = findTags(s)
                    if (mFilteredCowUiModelList.isEmpty() && shouldShowCouldntFindTag) {
                        mResultsNotFound.visibility = View.VISIBLE
                        mMedicateACowFabMenu.visibility = View.INVISIBLE
                    } else {
                        mMedicateACowFabMenu.visibility = View.VISIBLE
                        mResultsNotFound.visibility = View.INVISIBLE
                    }
                    shouldShowCouldntFindTag = true
                    mMedicatedCowsRecyclerViewAdapter!!.swapData(mFilteredCowUiModelList)
                } else {
                    if (shouldShowCouldntFindTag) {
                        mMedicateACowFabMenu.visibility = View.VISIBLE
                    }
                    mResultsNotFound.visibility = View.INVISIBLE
                    mFilteredCowUiModelList = mCowUiModelList
                    mMedicatedCowsRecyclerViewAdapter!!.swapData(mCowUiModelList)
                }
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_edit_lot_medicated_cows) {
            val editLotIntent = Intent(
                this@MedicatedCowsActivity,
                EditLotActivity::class.java
            )
            editLotIntent.putExtra("lotModel", mPenAndLotModel?.toLotModel())
            startActivity(editLotIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private var addCattleResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(mMedicatedCows, "Cattle added successfully", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    /* private methods */
    @Suppress("UNUSED_PARAMETER")
    fun medicateCow(view: View?) {
        mMedicateACowFabMenu.collapse()
        val medicateCowIntent = Intent(this@MedicatedCowsActivity, MedicateACowActivity::class.java)
        medicateCowIntent.putExtra("penAndLotModel", mPenAndLotModel)
        startActivity(medicateCowIntent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun markACowDead(view: View?) {
        mMedicateACowFabMenu.collapse()
        val markCowDeadIntent = Intent(this@MedicatedCowsActivity, MarkACowDeadActivity::class.java)
        markCowDeadIntent.putExtra("penAndLotModel", mPenAndLotModel)
        startActivity(markCowDeadIntent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun addCattle(view: View?) {
        mMedicateACowFabMenu.collapse()
        val addLoadOfCattle =
            Intent(this@MedicatedCowsActivity, AddLoadOfCattleActivity::class.java)
        addLoadOfCattle.putExtra("penAndLotModel", mPenAndLotModel)
        addCattleResultLauncher.launch(addLoadOfCattle)
    }

    private fun findTags(inputString: String): List<CowUiModel> {
        val listToReturn: MutableList<CowUiModel> = mutableListOf()
        for (e in mCowUiModelList.indices) {
            val cowUiModel = mCowUiModelList[e]
            val tag = cowUiModel.cowModel.tagNumber.toString()
            if (tag.startsWith(inputString)) {
                listToReturn.add(cowUiModel)
            }
        }
        return listToReturn
    }
}