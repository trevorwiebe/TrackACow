package com.trevorwiebe.trackacow.presentation.fragment_feed

import android.content.Context
import com.trevorwiebe.trackacow.domain.dataLoaders.main.pen.QueryAllPens.OnPensLoaded
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.trevorwiebe.trackacow.domain.dataLoaders.main.pen.QueryAllPens
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.domain.adapters.FeedPenViewPagerAdapter
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.FeedPenListFragment
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.FeedPenListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.list_pen.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class FeedContainerFragment : Fragment() {

    private lateinit var feedPenViewPager: ViewPager
    private var feedPenViewPagerAdapter: FeedPenViewPagerAdapter? = null
    private lateinit var mContext: Context

    private val feedContainerViewModel: FeedContainerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        feedPenViewPager = view.findViewById(R.id.feed_pen_view_pager)
        feedPenViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                Utility.saveLastFeedPen(mContext, i)
            }
            override fun onPageScrollStateChanged(i: Int) {}
        })
        val tabs = view.findViewById<TabLayout>(R.id.feed_pen_tab_layout)

        tabs.setupWithViewPager(feedPenViewPager)

        feedPenViewPagerAdapter = fragmentManager?.let { FeedPenViewPagerAdapter(it, emptyList()) }

        feedPenViewPager.adapter = feedPenViewPagerAdapter

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                feedContainerViewModel.uiState.collect{
                    feedPenViewPagerAdapter?.updatePenList(it.penAndLotList)
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