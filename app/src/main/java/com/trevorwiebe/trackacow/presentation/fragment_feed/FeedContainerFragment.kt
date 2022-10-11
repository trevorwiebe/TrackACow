package com.trevorwiebe.trackacow.presentation.fragment_feed

import android.content.Context
import android.content.Intent
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.R
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.trevorwiebe.trackacow.domain.adapters.FeedPenViewPagerAdapter
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.manage_pens.ManagePensActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        val noPensLayout = view.findViewById<LinearLayout>(R.id.no_pens_layout)
        view.findViewById<Button>(R.id.fragment_feed_add_pen_btn)
            .setOnClickListener {
                val addPenIntent = Intent(mContext, ManagePensActivity::class.java)
                startActivity(addPenIntent)
            }

        feedPenViewPagerAdapter = fragmentManager?.let { FeedPenViewPagerAdapter(it, emptyList()) }

        feedPenViewPager.adapter = feedPenViewPagerAdapter
        feedPenViewPager.viewTreeObserver.addOnGlobalLayoutListener {
            feedPenViewPager.setCurrentItem(Utility.getLastFeedPen(mContext), false)
        }

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                feedContainerViewModel.uiState.collect{
                    if(it.penAndLotList.isEmpty()){
                        tabs.visibility = View.GONE
                        if(!it.isLoading){
                            noPensLayout.visibility = View.VISIBLE
                        }
                    }else {
                        feedPenViewPagerAdapter?.updatePenList(it.penAndLotList)
                        tabs.visibility = View.VISIBLE
                        noPensLayout.visibility = View.GONE
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