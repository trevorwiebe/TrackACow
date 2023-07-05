package com.trevorwiebe.trackacow.presentation.manage_pens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.trevorwiebe.trackacow.presentation.fragment_medicate.PenRecyclerViewAdapter
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.presentation.add_edit_pen.AddEditPensActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManagePensActivity : AppCompatActivity(){

    // TODO: fix bug where when offline pens do not show up, until going back and to
    
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
            val addEditPenIntent =
                Intent(this@ManagePensActivity, AddEditPensActivity::class.java)
            startActivity(addEditPenIntent)
        }

        mPensRv.addOnItemTouchListener(
            ItemClickListener(
                this,
                mPensRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val addEditPenIntent =
                            Intent(this@ManagePensActivity, AddEditPensActivity::class.java)
                        addEditPenIntent.putExtra("pen_and_lot", mPenAndLotList[position])
                        startActivity(addEditPenIntent)
                    }
                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
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
}