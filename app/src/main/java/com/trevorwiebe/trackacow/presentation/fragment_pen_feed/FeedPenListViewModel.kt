package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDayStartAndDayEnd
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
    private val calculateDayStartAndDayEnd: CalculateDayStartAndDayEnd,
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

    private lateinit var mCallList: List<CallAndRationModel>

    private var readCallsJob: Job? = null

    init {
        readCallsAndRationsByLotId(lotModel.date, lotModel.lotCloudDatabaseId)
    }

    private fun readCallsAndRationsByLotId(lotDate: Long, lotId: String?){
        _uiState.update { it.copy(isLoading = true) }
        readCallsJob?.cancel()
        readCallsJob = callUseCases.readCallsAndRationsByLotId(lotId ?: "")
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

    private fun buildFeedList(lotStartDate: Long, callList: List<CallAndRationModel>, feedList: List<FeedModel>): MutableList<FeedPenListUiModel> {

        val feedPenUiModelList: MutableList<FeedPenListUiModel> = mutableListOf()

        // if the start date is 0 we will return, as there is no use processing
        if(lotStartDate == 0L) return feedPenUiModelList

        // calculate the beginning of the day on lot create date
        var dateToChange = calculateDayStartAndDayEnd(lotStartDate)[0]

        val oneDay = TimeUnit.DAYS.toMillis(1)
        val endTime = calculateDayStartAndDayEnd(System.currentTimeMillis())[1]

        while (dateToChange < endTime) {

            val currentDateList = calculateDayStartAndDayEnd(dateToChange)
            val dayStart = currentDateList[0]
            val dayEnd = currentDateList[1]

            val callAndRationModel = callList.find {
                it.date >= dayStart && it.date <= dayEnd
            } ?: CallAndRationModel(0, 0, 0, "", 0, "", 0, "", "")
            val feedPenListUiModel = FeedPenListUiModel(
                date = dateToChange,
                callAndRationModel = callAndRationModel,
                feedList = feedList.filter { it.date >= dayStart && it.date <= dayEnd }
            )

            feedPenUiModelList.add(feedPenListUiModel)

            dateToChange += oneDay

        }

        feedPenUiModelList.reverse()
        return feedPenUiModelList
    }
}