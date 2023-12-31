package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import android.content.Intent
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.presentation.add_lot_and_load.AddLotAndLoad
import com.trevorwiebe.trackacow.presentation.feed_lot_view_pager_container.FeedLotViewPagerContainer
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.ui_model.FeedPenListUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FeedPenListFragment : Fragment() {
    private lateinit var mEmptyPen: LinearLayout
    private lateinit var mAddLotBtn: Button
    private lateinit var mFeedPenRv: RecyclerView
    private lateinit var mLotModel: LotModel
    private lateinit var mProgressBar: LinearProgressIndicator
    private lateinit var feedPenRecyclerViewAdapter: FeedPenRecyclerViewAdapter
    private var feedPenListUiModelList: List<FeedPenListUiModel> = emptyList()

    @Inject
    lateinit var feedPenListViewModelFactory: FeedPenListViewModel.FeedPenListViewModelFactory

    private val feedPenListViewModel: FeedPenListViewModel by viewModels {
        FeedPenListViewModel.providesFactory(
            assistedFactory = feedPenListViewModelFactory,
            lotModel = getLotModel(arguments)
        )
    }

    // TODO: fix issue where recyclerview still shows even when lot is deleted

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_pen_feed, container, false)

        mLotModel = getLotModel(arguments)

        mEmptyPen = rootView.findViewById(R.id.feed_pen_empty_layout)
        mAddLotBtn = rootView.findViewById(R.id.feed_add_lot_btn)
        mFeedPenRv = rootView.findViewById(R.id.feed_pen_rv)
        mProgressBar = rootView.findViewById(R.id.pen_feed_progress_bar)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        mFeedPenRv.layoutManager = linearLayoutManager

        feedPenRecyclerViewAdapter = FeedPenRecyclerViewAdapter()
        mFeedPenRv.adapter = feedPenRecyclerViewAdapter

        mAddLotBtn.setOnClickListener {
            val intent = Intent(context, AddLotAndLoad::class.java)
            intent.putExtra("pen_id", mLotModel.lotPenCloudDatabaseId)
            startActivity(intent)
        }

        mFeedPenRv.addOnItemTouchListener(
            ItemClickListener(
                context,
                mFeedPenRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val feedLotIntent = Intent(activity, FeedLotViewPagerContainer::class.java)
                        feedLotIntent.putExtra(
                            "feed_ui_model_date",
                            feedPenListUiModelList[position].date
                        )
                        feedLotIntent.putExtra("feed_lot_model_id", mLotModel.lotCloudDatabaseId)
                        startActivity(feedLotIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                feedPenListViewModel.uiState.collect {

                    if ((it.feedIsFetchingFromCloud && it.feedDataSource == DataSource.Local && it.feedPenUiList.isEmpty()) ||
                            (it.callIsFetchingFromCloud && it.callDataSource == DataSource.Local && it.feedPenUiList.isEmpty())
                    ) {
                        mProgressBar.visibility = View.VISIBLE
                    } else {
                        mProgressBar.visibility = View.GONE
                    }

                    feedPenListUiModelList = it.feedPenUiList

                    // update ui with lot list

                    //if the feedPenListUiModelList is empty and loading is false, the list is empty
                    // and that is shown
                    if (feedPenListUiModelList.isEmpty()) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                if (feedPenListUiModelList.isEmpty())
                                    mEmptyPen.visibility = View.VISIBLE
                            }, 100
                        )
                    } else {
                        mEmptyPen.visibility = View.INVISIBLE
                        feedPenRecyclerViewAdapter.setFeedPenList(feedPenListUiModelList)
                    }
                }
            }
        }

        return rootView
    }

    private fun getLotModel(bundle: Bundle?): LotModel{
        return if(VERSION.SDK_INT >= 33){
            bundle?.getParcelable("fragment_lot", LotModel::class.java) ?: LotModel(
                0, "", "", "", "", "", 0L, 0, 0, ""
            )
        }else{
            @Suppress("DEPRECATION")
            bundle?.getParcelable("fragment_lot") ?: LotModel(
                0, "", "", "", "", "", 0L, 0, 0, ""
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(penId: String?, lotModel: LotModel): FeedPenListFragment {
            val args = Bundle()
            args.putParcelable("fragment_lot", lotModel)
            args.putString("fragment_pen_id", penId)
            val fragment = FeedPenListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}