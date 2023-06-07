package com.trevorwiebe.trackacow.presentation.fragment_move

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
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
import com.trevorwiebe.trackacow.presentation.fragment_move.utils.ShuffleObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoveFragment : Fragment() {

    private lateinit var mMoveRv: RecyclerView
    private lateinit var mEmptyMoveList: TextView

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

        mMoveRv.layoutManager = LinearLayoutManager(mContext)

        mShuffleAdapter = ShufflePenAndLotsAdapter()

        mShuffleAdapter.onItemShuffled { lotId, penId ->
            moveViewModel.onEvent(MoveUiEvents.OnItemShuffled(lotId, penId))
        }

        val dragHelper = DragHelper(mShuffleAdapter)
        val itemTouchHelper = ItemTouchHelper(dragHelper)
        mShuffleAdapter.setTouchHelper(itemTouchHelper)

        mMoveRv.adapter = mShuffleAdapter

        itemTouchHelper.attachToRecyclerView(mMoveRv)

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                moveViewModel.uiState.collect{

                    val penAndLotList = it.penAndLotList

                    val shuffleObjectsList: MutableList<ShuffleObject> = mutableListOf()
                    
                    for (r in penAndLotList.indices){

                        val penAndLotModel = penAndLotList[r]

                        val penShuffleObject = ShuffleObject(
                            ShufflePenAndLotsAdapter.PEN_NAME,
                            penAndLotModel.penName,
                            penAndLotModel.penCloudDatabaseId?:""
                        )

                        shuffleObjectsList.add(penShuffleObject)

                        if(!penAndLotModel.lotName.isNullOrEmpty()){
                            val lotShuffleObject = ShuffleObject(
                                ShufflePenAndLotsAdapter.LOT_NAME,
                                penAndLotModel.lotName!!,
                                penAndLotModel.lotCloudDatabaseId!!
                            )
                            shuffleObjectsList.add(lotShuffleObject)
                        }
                    }

                    if (shuffleObjectsList.size == 0) {
                        mEmptyMoveList.visibility = View.VISIBLE
                    } else {
                        mEmptyMoveList.visibility = View.INVISIBLE
                    }
                    mShuffleAdapter.setShuffleObjectList(shuffleObjectsList, mContext)
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