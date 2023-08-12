package com.trevorwiebe.trackacow.domain.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.FeedPenListFragment.Companion.newInstance
import androidx.fragment.app.FragmentStatePagerAdapter
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toLotModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel

class FeedPenViewPagerAdapter(
    fragmentManager: FragmentManager,
    private var penAndLotList: List<PenAndLotModel>
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(i: Int): Fragment {
        val penId = penAndLotList[i].penCloudDatabaseId
        val lotModel = penAndLotList[i].toLotModel()
        return newInstance(penId, lotModel)
    }

    override fun getCount(): Int {
        return penAndLotList.size
    }

    fun updatePenList(newPenAndLotList: List<PenAndLotModel>){
        penAndLotList = newPenAndLotList
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence {
        val penAndLotModel = penAndLotList[position]
        val penName = penAndLotModel.penName
        val lotName = penAndLotModel.lotName
        return if(lotName.isNullOrEmpty())
            "Pen: $penName"
        else
            "Pen: $penName - Lot: $lotName"
    }
}