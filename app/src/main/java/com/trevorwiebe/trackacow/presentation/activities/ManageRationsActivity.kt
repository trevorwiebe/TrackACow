package com.trevorwiebe.trackacow.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.data.db.entities.RationEntity
import com.trevorwiebe.trackacow.domain.adapters.ManageRationsRvAdapter
import com.trevorwiebe.trackacow.domain.utils.Constants

class ManageRationsActivity : AppCompatActivity() {

    private val rationsList: List<RationEntity> = emptyList()
    private lateinit var addRationFAB: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_rations)

        val manageRationsRv: RecyclerView = findViewById(R.id.ration_rv)
        manageRationsRv.adapter = ManageRationsRvAdapter(rationsList)

        findViewById<FloatingActionButton>(R.id.add_new_ration)
            .setOnClickListener{
                val intent = Intent(this, AddOrEditRation::class.java)
                intent.putExtra("add_or_edit", Constants.ADD_RATION)
                startActivity(intent)
            }

        addRationFAB = findViewById(R.id.add_new_ration)
        addRationFAB.isVisible = rationsList.isEmpty()

    }
}