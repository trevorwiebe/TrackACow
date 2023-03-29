package com.trevorwiebe.trackacow.presentation.feed_lot_view_pager_container

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedLotViewPagerContainer : AppCompatActivity() {

    private val mFeedLotViewPagerContainerViewModel: FeedLotViewPagerContainerViewModel by viewModels()

    var penAndLotModelList: List<PenAndLotModel> = emptyList()
    var mFeedPenUiModelDate: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_lot_container)

        val feedViewPager: ViewPager2 = findViewById(R.id.feed_lot_detail_view_pager)
        val feedLotDetailViewPagerAdapter = FeedLotDetailViewPagerAdapter(this)
        feedViewPager.adapter = feedLotDetailViewPagerAdapter

        feedViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val selectedPenAndLotModel = penAndLotModelList[position]
                title = "${selectedPenAndLotModel.penName} - " +
                        "${selectedPenAndLotModel.lotName} - " +
                        Utility.convertMillisToFriendlyDate(mFeedPenUiModelDate)
                super.onPageSelected(position)
            }
        })

        mFeedPenUiModelDate = intent.getLongExtra("feed_ui_model_date", -1)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mFeedLotViewPagerContainerViewModel.uiState.collect {

                    penAndLotModelList = it.penAndLotList
                    feedLotDetailViewPagerAdapter.setLotData(
                        penAndLotModelList,
                        it.rationList,
                        it.lastUsedRation,
                        mFeedPenUiModelDate
                    )
                }
            }
        }
    }
}