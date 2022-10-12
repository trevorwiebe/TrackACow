package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.ui_model.FeedPenListUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.concurrent.TimeUnit

class FeedPenListViewModel @AssistedInject constructor(
    private val callUseCases: CallUseCases,
    private val feedUseCases: FeedUseCases,
    @Assisted("lotModel") lotModel: LotModel
): ViewModel() {

    @AssistedFactory
    interface FeedPenListViewModelFactory {
        fun create(@Assisted("lotModel") lotId: LotModel
        ): FeedPenListViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object{
        fun providesFactory(
            assistedFactory: FeedPenListViewModelFactory,
            lotModel: LotModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(lotModel) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(FeedPenListUiState())
    val uiState: StateFlow<FeedPenListUiState> = _uiState.asStateFlow()

    private lateinit var mCallList: List<CallModel>

    private var readCallsJob: Job? = null

    init {
        readCallsByLotId(lotModel.date, lotModel.lotCloudDatabaseId)
    }

    private fun readCallsByLotId(lotDate: Long, lotId: String?){
        _uiState.update { it.copy(isLoading = true) }
        readCallsJob?.cancel()
        readCallsJob = callUseCases.readCallsByLotId(lotId ?: "")
            .map { callList ->
                mCallList = callList
                readFeedsByLotId(lotDate, lotId?:"")
            }
            .launchIn(viewModelScope)
    }

    private fun readFeedsByLotId(lotDate: Long, lotId: String){
        feedUseCases.readFeedsByLotId(lotId)
            .map { feedList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        feedPenUiList = buildFeedList(lotDate, mCallList, feedList),
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun buildFeedList(lotStartDate: Long, callList: List<CallModel>, feedList: List<FeedModel>): MutableList<FeedPenListUiModel> {

        val feedPenUiModelList: MutableList<FeedPenListUiModel> = mutableListOf()

        // if the callAndLotList is empty we will return, as there is no use processing
        if(lotStartDate == 0L) return feedPenUiModelList

        val c = Calendar.getInstance()
        c.timeInMillis = lotStartDate
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0
        var dateStarted = c.timeInMillis

        val oneDay = TimeUnit.DAYS.toMillis(1)
        val currentTime = System.currentTimeMillis()

        val initialCallModel = callList.find { it.date == dateStarted }
            ?: CallModel(0, 0, 0,"", 0,"")

        val initialFeedList: List<FeedModel> = feedList.filter { it.date == dateStarted }

        val initialFeedPenListUiModel = FeedPenListUiModel(
            date = dateStarted,
            callModel = initialCallModel,
            feedList = initialFeedList
        )

        feedPenUiModelList.add(initialFeedPenListUiModel)

        while ((dateStarted + oneDay) < currentTime) {
            dateStarted += oneDay

            val callModel = callList.find { it.date == dateStarted }
                ?: CallModel(0, 0, 0,"", 0,"")

            val feedPenListUiModel = FeedPenListUiModel(
                date = dateStarted,
                callModel = callModel,
                feedList = feedList.filter { it.date == dateStarted }
            )

            feedPenUiModelList.add(feedPenListUiModel)
        }
        feedPenUiModelList.reverse()
        return feedPenUiModelList
    }
}