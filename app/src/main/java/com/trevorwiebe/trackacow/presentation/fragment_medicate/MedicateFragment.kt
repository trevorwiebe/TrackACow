package com.trevorwiebe.trackacow.presentation.fragment_medicate

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.presentation.medicated_cows.MedicatedCowsActivity
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.manage_pens.ManagePensActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicateFragment : Fragment() {

    private var mPenRecyclerViewAdapter: PenRecyclerViewAdapter? = null
    private val mMedicateListViewModel: MedicateListViewModel by viewModels()

    private var mPenAndLotModelList = emptyList<PenAndLotModel>()

    private lateinit var mNoPensLl: LinearLayout
    private lateinit var mPenRv: RecyclerView

    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_medicate, container, false)

        rootView.findViewById<Button>(R.id.fragment_medicate_add_pen_btn)
            .setOnClickListener{
                val addPenIntent = Intent(mContext, ManagePensActivity::class.java)
                startActivity(addPenIntent)
            }

        mNoPensLl = rootView.findViewById(R.id.fragment_medicate_no_pens_layout)

        mPenRv = rootView.findViewById(R.id.main_rv)
        mPenRv.layoutManager = LinearLayoutManager(mContext)
        mPenRecyclerViewAdapter = PenRecyclerViewAdapter(mPenAndLotModelList, true, mContext)
        mPenRv.adapter = mPenRecyclerViewAdapter
        mPenRv.addOnItemTouchListener(
            ItemClickListener(
                mContext,
                mPenRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val trackCowIntent = Intent(mContext, MedicatedCowsActivity::class.java)
                        trackCowIntent.putExtra("penAndLotModel", mPenAndLotModelList[position])
                        startActivity(trackCowIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                mMedicateListViewModel.uiState.collect{
                    mPenAndLotModelList = it.penAndLotModelList
                    if(it.penAndLotModelList.isEmpty()){
                        mPenRv.visibility = View.GONE
                        if(!it.isLoading){
                            mNoPensLl.visibility = View.VISIBLE
                        }
                    }else {
                        mNoPensLl.visibility = View.GONE
                        mPenRv.visibility = View.VISIBLE
                        mPenRecyclerViewAdapter!!.swapData(mPenAndLotModelList)
                    }
                }
            }
        }

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}