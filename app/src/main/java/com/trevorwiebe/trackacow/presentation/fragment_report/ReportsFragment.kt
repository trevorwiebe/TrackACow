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
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.presentation.lot_reports.LotReportActivity
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportsFragment : Fragment() {

    private var mReportsRvAdapter: ReportsRecyclerViewAdapter? = null
    private var mLotList: List<LotModel> = emptyList()
    private lateinit var mProgressBar: LinearProgressIndicator
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
        mProgressBar = rootView.findViewById(R.id.reports_progress_bar)
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
                        val lotCloudDatabaseId = mLotList[position].lotCloudDatabaseId
                        reportsIntent.putExtra("lotId", lotId)
                        reportsIntent.putExtra("reportType", Constants.LOT)
                        reportsIntent.putExtra("lotCloudDatabaseId", lotCloudDatabaseId)
                        startActivity(reportsIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportsFragmentViewModel.uiState.collect {

                    if (it.isFetchingFromCloud && it.dataSource == DataSource.Local && it.lotList.isEmpty()) {
                        mProgressBar.visibility = View.VISIBLE
                    } else {
                        mProgressBar.visibility = View.GONE
                    }

                    mLotList = it.lotList
                    if (mLotList.isEmpty()) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                if (mLotList.isEmpty())
                                    mNoReportsTv.visibility = View.VISIBLE
                            }, 100 // value in milliseconds
                        )
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