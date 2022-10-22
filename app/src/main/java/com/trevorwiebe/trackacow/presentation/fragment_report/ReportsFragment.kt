package com.trevorwiebe.trackacow.presentation.fragment_report

import com.trevorwiebe.trackacow.domain.adapters.ReportsRecyclerViewAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.presentation.lot_reports.LotReportActivity
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportsFragment : Fragment() {

    private var mReportsRvAdapter: ReportsRecyclerViewAdapter? = null
    private var mLotList: List<LotModel> = emptyList()

    private lateinit var mNoReportsTv: TextView

    private val reportsFragmentViewModel: ReportsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.fragment_reports, container, false)
        mNoReportsTv = rootView.findViewById(R.id.empty_reports_tv)
        val reportsRv = rootView.findViewById<RecyclerView>(R.id.reports_rv)
        reportsRv.layoutManager = LinearLayoutManager(context)
        mReportsRvAdapter = ReportsRecyclerViewAdapter()
        reportsRv.adapter = mReportsRvAdapter
        reportsRv.addOnItemTouchListener(
            ItemClickListener(
                context,
                reportsRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val reportsIntent = Intent(context, LotReportActivity::class.java)
                        val lotId = mLotList[position].lotPrimaryKey
                        Log.d("TAG", "onItemClick: $lotId")
                        reportsIntent.putExtra("lotId", lotId)
                        reportsIntent.putExtra("reportType", Constants.LOT)
                        startActivity(reportsIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                reportsFragmentViewModel.uiState.collect{
                    mLotList = it.lotList
                    if (mLotList.isEmpty()) {
                        mNoReportsTv.visibility = View.VISIBLE
                    } else {
                        mNoReportsTv.visibility = View.INVISIBLE
                    }
                    mReportsRvAdapter!!.swapLotData(mLotList)
                }
            }
        }

        return rootView
    }
}