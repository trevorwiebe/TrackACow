package com.trevorwiebe.trackacow.presentation.manage_rations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.data.db.entities.RationEntity
import com.trevorwiebe.trackacow.domain.adapters.ManageRationsRvAdapter
import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.presentation.add_or_edit_rations.AddOrEditRation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageRationsActivity : AppCompatActivity() {

    private var rationsList: List<RationModel> = listOf()

    private val manageRationsViewModel: ManageRationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_rations)

        val manageRationsRv: RecyclerView = findViewById(R.id.ration_rv)
        manageRationsRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val manageRationsAdapter = ManageRationsRvAdapter()
        manageRationsRv.adapter = manageRationsAdapter

        findViewById<FloatingActionButton>(R.id.add_new_ration)
            .setOnClickListener{
                val intent = Intent(this, AddOrEditRation::class.java)
                intent.putExtra("add_or_edit", Constants.ADD_RATION)
                startActivity(intent)
            }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                manageRationsViewModel.uiState.collect{
                    rationsList = it.rationsList
                    manageRationsAdapter.setRationsList(rationsList)
                    findViewById<TextView>(R.id.manage_ration_empty_list_tv).isVisible = rationsList.isEmpty()
                }
            }
        }
    }
}