package com.trevorwiebe.trackacow.presentation.feed_lot_view_pager_container

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.presentation.feed_lot_detail_fragment.FeedLotDetailFragment

class FeedLotDetailViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private var penAndLotList: List<PenAndLotModel> = emptyList()
    private var rationList: ArrayList<RationModel> = ArrayList()
    private var lastUsedRationId: Int = -1
    private var penUiDate: Long = -1

    override fun getItemCount(): Int {
        return penAndLotList.size
    }

    override fun createFragment(position: Int) =
        FeedLotDetailFragment.newInstance(
            penAndLotList[position],
            rationList,
            lastUsedRationId,
            penUiDate
        )

    fun setLotData(
        newPenAndLotModelList: List<PenAndLotModel>,
        newRationList: List<RationModel>,
        newLastUsedRationId: Int,
        newPenUiDate: Long
    ) {
        this.penAndLotList = newPenAndLotModelList
        this.rationList = ArrayList(newRationList)
        this.lastUsedRationId = newLastUsedRationId
        this.penUiDate = newPenUiDate
        notifyDataSetChanged()
    }
}