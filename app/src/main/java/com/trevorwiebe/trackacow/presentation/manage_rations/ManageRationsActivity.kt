package com.trevorwiebe.trackacow.presentation.manage_rations

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.adapters.ManageRationsRvAdapter
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener.OnItemClickListener
import com.trevorwiebe.trackacow.presentation.add_or_edit_rations.AddOrEditRation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageRationsActivity : AppCompatActivity() {

    private var globalRationsList: List<RationModel> = listOf()

    private val manageRationsViewModel: ManageRationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_rations)

        val manageRationsRv: RecyclerView = findViewById(R.id.ration_rv)
        manageRationsRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val manageRationsAdapter = ManageRationsRvAdapter()
        manageRationsRv.adapter = manageRationsAdapter

        manageRationsRv.addOnItemTouchListener(
            ItemClickListener(this, manageRationsRv, object : OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val selectedRation: RationModel = globalRationsList[position]
                        val editRationIntent = Intent(
                            this@ManageRationsActivity,
                            AddOrEditRation::class.java)
                        editRationIntent.putExtra("add_or_edit", Constants.EDIT_RATION)
                        editRationIntent.putExtra("ration_name", selectedRation.rationName)
                        editRationIntent.putExtra("ration_id", selectedRation.rationPrimaryKey)
                        startActivity(editRationIntent)
                    }
                    override fun onLongItemClick(view: View, position: Int) {}
                }
            )
        )

        findViewById<FloatingActionButton>(R.id.add_new_ration)
            .setOnClickListener{
                val intent = Intent(this, AddOrEditRation::class.java)
                intent.putExtra("add_or_edit", Constants.ADD_RATION)
                startActivity(intent)
            }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                manageRationsViewModel.uiState.collect{
                    globalRationsList = it.rationsList
                    manageRationsAdapter.setRationsList(globalRationsList)
                    findViewById<TextView>(R.id.manage_ration_empty_list_tv).isVisible = globalRationsList.isEmpty()
                }
            }
        }
    }
}