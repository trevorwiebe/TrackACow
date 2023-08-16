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
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.ui_model.FeedPenListUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private var mCallList: List<CallAndRationModel> = emptyList()
    private var mFeedList: List<FeedModel> = emptyList()

    init {
        readCallsAndRationsByLotId(lotModel.date, lotModel.lotCloudDatabaseId)
        readFeedsByLotId(lotModel.date, lotModel.lotCloudDatabaseId)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readCallsAndRationsByLotId(lotDate: Long, lotId: String?) {
        val call = callUseCases.readCallsAndRationsByLotId(lotId ?: "")
        viewModelScope.launch {
            call.dataFlow.collect { (callList, source) ->
                mCallList = callList as List<CallAndRationModel>
                _uiState.update {
                    it.copy(
                        callDataSource = source,
                        callIsFetchingFromCloud = call.isFetchingFromCloud,
                        feedPenUiList = buildFeedList(lotDate, mCallList, mFeedList)
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readFeedsByLotId(lotDate: Long, lotId: String) {
        val feed = feedUseCases.readFeedsByLotId(lotId)
        viewModelScope.launch {
            feed.dataFlow.collect { (feedList, source) ->
                mFeedList = feedList as List<FeedModel>
                _uiState.update { uiState ->
                    uiState.copy(
                        feedDataSource = source,
                        feedIsFetchingFromCloud = feed.isFetchingFromCloud,
                        feedPenUiList = buildFeedList(lotDate, mCallList, mFeedList),
                    )
                }
            }
        }
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
                it.date in dayStart..dayEnd
            } ?: CallAndRationModel(0, 0, 0, "", 0, "", 0, "", "")
            val feedPenListUiModel = FeedPenListUiModel(
                date = dateToChange,
                callAndRationModel = callAndRationModel,
                feedList = feedList.filter { it.date in dayStart..dayEnd }
            )

            feedPenUiModelList.add(feedPenListUiModel)

            dateToChange += oneDay

        }

        feedPenUiModelList.reverse()
        return feedPenUiModelList
    }
}

data class FeedPenListUiState(
    val callDataSource: DataSource = DataSource.Local,
    val feedDataSource: DataSource = DataSource.Local,
    val callIsFetchingFromCloud: Boolean = false,
    val feedIsFetchingFromCloud: Boolean = false,
    val feedPenUiList: List<FeedPenListUiModel> = emptyList(),
    val lotList: List<LotModel> = emptyList(),
)