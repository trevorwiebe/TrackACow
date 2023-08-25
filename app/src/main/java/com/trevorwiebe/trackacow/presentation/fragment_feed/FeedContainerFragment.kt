package com.trevorwiebe.trackacow.presentation.fragment_feed

import android.content.Context
import android.content.Intent
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.R
import com.google.android.material.tabs.TabLayout
import com.trevorwiebe.trackacow.domain.adapters.FeedPenViewPagerAdapter
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.presentation.manage_pens.ManagePensActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedContainerFragment : Fragment() {

    private lateinit var feedPenViewPager: ViewPager
    private var feedPenViewPagerAdapter: FeedPenViewPagerAdapter? = null
    private lateinit var mContext: Context
    private val feedContainerViewModel: FeedContainerViewModel by viewModels()
    private var mPenAndLotList: List<PenAndLotModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        feedPenViewPager = view.findViewById(R.id.feed_pen_view_pager)
        val tabs = view.findViewById<TabLayout>(R.id.feed_pen_tab_layout)
        tabs.setupWithViewPager(feedPenViewPager)
        val noPensLayout = view.findViewById<LinearLayout>(R.id.no_pens_layout)
        view.findViewById<Button>(R.id.fragment_feed_add_pen_btn)
            .setOnClickListener {
                val addPenIntent = Intent(mContext, ManagePensActivity::class.java)
                startActivity(addPenIntent)
            }

        feedPenViewPagerAdapter = FeedPenViewPagerAdapter(childFragmentManager, emptyList())

        feedPenViewPager.adapter = feedPenViewPagerAdapter

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedContainerViewModel.uiState.collect {

                    mPenAndLotList = it.penAndLotList

                    if (mPenAndLotList.isEmpty()) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                if (mPenAndLotList.isEmpty()) {
                                    noPensLayout.visibility = View.VISIBLE
                                    tabs.visibility = View.GONE
                                }
                            }, 100 // value in milliseconds
                        )
                    } else {
                        feedPenViewPagerAdapter?.updatePenList(mPenAndLotList)
                        noPensLayout.visibility = View.GONE
                        tabs.visibility = View.VISIBLE
                    }
                }
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}