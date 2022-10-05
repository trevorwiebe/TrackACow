package com.trevorwiebe.trackacow.presentation.fragment_move

import android.content.Context
import com.trevorwiebe.trackacow.domain.dataLoaders.main.pen.QueryAllPens.OnPensLoaded
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLots.OnLotsLoaded
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.trevorwiebe.trackacow.data.entities.PenEntity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.DragHelper
import androidx.recyclerview.widget.ItemTouchHelper
import com.trevorwiebe.trackacow.domain.dataLoaders.main.pen.QueryAllPens
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLots
import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.ShuffleObject
import java.util.ArrayList

class MoveFragment : Fragment(), OnPensLoaded, OnLotsLoaded {

    private lateinit var mMoveRv: RecyclerView
    private lateinit var mEmptyMoveList: TextView

    private var mPenEntities = ArrayList<PenEntity>()
    private var mShuffleAdapter: ShufflePenAndLotsAdapter? = null

    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_move, container, false)
        mMoveRv = rootView.findViewById(R.id.shuffle_rv)
        mEmptyMoveList = rootView.findViewById(R.id.empty_move_tv)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        mMoveRv.layoutManager = LinearLayoutManager(mContext)

        mShuffleAdapter =
            ShufflePenAndLotsAdapter()

        val dragHelper = DragHelper(mShuffleAdapter!!)
        val itemTouchHelper = ItemTouchHelper(dragHelper)
        mShuffleAdapter!!.setTouchHelper(itemTouchHelper)

        mMoveRv.adapter = mShuffleAdapter

        itemTouchHelper.attachToRecyclerView(mMoveRv)

        QueryAllPens(this@MoveFragment).execute(mContext)
    }

    override fun onPensLoaded(penEntitiesList: ArrayList<PenEntity>) {
        mPenEntities = penEntitiesList
        QueryLots(this@MoveFragment).execute(mContext)
    }

    override fun onLotsLoaded(lotEntities: ArrayList<LotEntity>) {
        val shuffleObjects = ArrayList<ShuffleObject>()
        for (r in mPenEntities.indices) {
            val (_, penId, penName) = mPenEntities[r]
            val penShuffleObject =
                ShuffleObject(
                    ShufflePenAndLotsAdapter.PEN_NAME,
                    penName,
                    penId
                )
            shuffleObjects.add(penShuffleObject)
            val selectedLotEntities = getLotEntities(penId, lotEntities)
            if (selectedLotEntities.size != 0) {
                for (p in selectedLotEntities.indices) {
                    val lotEntity = selectedLotEntities[p]
                    val lotName = lotEntity.lotName
                    val lotId = lotEntity.lotId
                    val lotShuffleObject =
                        ShuffleObject(
                            ShufflePenAndLotsAdapter.LOT_NAME,
                            lotName,
                            lotId
                        )
                    shuffleObjects.add(lotShuffleObject)
                }
            }
        }
        if (shuffleObjects.size == 0) {
            mEmptyMoveList.visibility = View.VISIBLE
        } else {
            mEmptyMoveList.visibility = View.INVISIBLE
        }
        mShuffleAdapter!!.setAdapterVariables(shuffleObjects, mContext)
    }

    private fun getLotEntities(
        penId: String,
        lotEntities: ArrayList<LotEntity>
    ): ArrayList<LotEntity> {
        val selectedLotEntities = ArrayList<LotEntity>()
        for (o in lotEntities.indices) {
            val lotEntity = lotEntities[o]
            if (lotEntity.lotPenId == penId) {
                selectedLotEntities.add(lotEntity)
            }
        }
        return selectedLotEntities
    }
}