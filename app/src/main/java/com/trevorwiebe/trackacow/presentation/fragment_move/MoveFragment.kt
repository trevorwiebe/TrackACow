package com.trevorwiebe.trackacow.presentation.fragment_move

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevorwiebe.trackacow.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.DragHelper
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toLotModel
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toPenModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoveFragment : Fragment() {

    private lateinit var mMoveRv: RecyclerView
    private lateinit var mEmptyMoveList: TextView
    private lateinit var mProgressBar: LinearProgressIndicator
    private var mObjectList: MutableList<Any> = mutableListOf()
    private lateinit var mShuffleAdapter: ShufflePenAndLotsAdapter

    private val moveViewModel: MoveViewModel by viewModels()

    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_move, container, false)
        mMoveRv = rootView.findViewById(R.id.shuffle_rv)
        mEmptyMoveList = rootView.findViewById(R.id.empty_move_tv)
        mProgressBar = rootView.findViewById(R.id.move_progress_indicator)
        mMoveRv.layoutManager = LinearLayoutManager(mContext)

        mShuffleAdapter = ShufflePenAndLotsAdapter()

        mShuffleAdapter.onItemShuffled { lotModel ->
            moveViewModel.onEvent(MoveUiEvents.OnItemShuffled(lotModel))
        }

        mShuffleAdapter.onLotsMerged { lotModelList ->
            moveViewModel.onEvent(MoveUiEvents.OnLotsMerged(lotModelList))
        }

        val dragHelper = DragHelper(mShuffleAdapter)
        val itemTouchHelper = ItemTouchHelper(dragHelper)
        mShuffleAdapter.setTouchHelper(itemTouchHelper)

        mMoveRv.adapter = mShuffleAdapter

        itemTouchHelper.attachToRecyclerView(mMoveRv)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                moveViewModel.uiState.collect {

                    mObjectList.clear()

                    if (it.isFetchingFromCloud && it.dataSource == DataSource.Local && it.penAndLotList.isEmpty()) {
                        mProgressBar.visibility = View.VISIBLE
                    } else {
                        mProgressBar.visibility = View.INVISIBLE
                    }

                    val penAndLotList = it.penAndLotList

                    for (r in penAndLotList.indices) {

                        val penAndLotModel = penAndLotList[r]

                        val penModel = penAndLotModel.toPenModel()
                        mObjectList.add(penModel)

                        if (!penAndLotModel.lotName.isNullOrEmpty()) {
                            val lotModel = penAndLotModel.toLotModel()
                            mObjectList.add(lotModel)
                        }
                    }

                    if (mObjectList.size == 0) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                if (mObjectList.size == 0) {
                                    mEmptyMoveList.visibility = View.VISIBLE
                                }
                            }, 100 // value in milliseconds
                        )
                    } else {
                        mEmptyMoveList.visibility = View.GONE
                    }
                    mShuffleAdapter.setShuffleObjectList(mObjectList, mContext)
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