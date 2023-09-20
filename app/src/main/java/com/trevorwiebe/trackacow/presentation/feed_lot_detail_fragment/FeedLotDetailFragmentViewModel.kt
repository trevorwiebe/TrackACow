package com.trevorwiebe.trackacow.presentation.feed_lot_detail_fragment

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDayStartAndDayEnd
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val pen_and_lot_param = "pen_and_lot_param"
private const val pen_ui_date_param = "pen_ui_date_param"

class FeedLotDetailFragmentViewModel @AssistedInject constructor(
    private val callUseCases: CallUseCases,
    private val feedUseCases: FeedUseCases,
    private val rationUseCases: RationUseCases,
    private val lotUseCases: LotUseCases,
    calculateDayStartAndDayEnd: CalculateDayStartAndDayEnd,
    @Assisted defaultArgs: Bundle? = null
) : ViewModel() {

    @AssistedFactory
    interface FeedLotDetailFragmentViewModelFactory {
        fun create(defaultArgs: Bundle?): FeedLotDetailFragmentViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
                assistedFactory: FeedLotDetailFragmentViewModelFactory,
                defaultArgs: Bundle?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(defaultArgs) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(FeedLotDetailFragmentUiState())
    val uiState: StateFlow<FeedLotDetailFragmentUiState> = _uiState.asStateFlow()

    init {
        @Suppress("DEPRECATION")
        val penAndLotModel: PenAndLotModel? = if (Build.VERSION.SDK_INT >= 33) {
            defaultArgs?.getParcelable(pen_and_lot_param, PenAndLotModel::class.java)
        } else {
            defaultArgs?.getParcelable(pen_and_lot_param)
        }
        val penUidDate = defaultArgs?.getLong(pen_ui_date_param) ?: 0
        val lotId = penAndLotModel?.lotCloudDatabaseId ?: ""

        val dateList = calculateDayStartAndDayEnd.invoke(penUidDate)

        readCallByLotIdAndDate(lotId, dateList[0], dateList[1])
        readFeedsByLotIdAndDate(lotId, dateList[0], dateList[1])
        readRations()
    }

    fun onEvent(event: FeedLotDetailFragmentEvents) {
        when (event) {
            is FeedLotDetailFragmentEvents.OnSave -> {
                createOrUpdateCallandFeeds(
                    event.callModel,
                    event.feedModelList,
                    event.originalFeedModelList
                )
            }

            is FeedLotDetailFragmentEvents.OnRationsLoaded -> {
                readRations()
            }

            is FeedLotDetailFragmentEvents.OnUpdateRation -> {
                updateLotWithNewRation(event.lotModel)
            }
        }
    }

    private fun readCallByLotIdAndDate(lotId: String, dateStart: Long, dateEnd: Long) {
        val call = callUseCases.readCallsByLotIdAndDateUC(lotId, dateStart, dateEnd)
        viewModelScope.launch {
            call.dataFlow.collect { (receivedCallModel, source) ->
                if (receivedCallModel != null) {
                    _uiState.update { uiState ->
                        uiState.copy(
                            callModel = receivedCallModel as CallAndRationModel,
                            callDataSource = source,
                            callIsFetchingFromCloud = call.isFetchingFromCloud
                        )
                    }
                } else {
                    _uiState.update { uiState ->
                        uiState.copy(
                            callDataSource = source,
                            callIsFetchingFromCloud = call.isFetchingFromCloud
                        )
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readFeedsByLotIdAndDate(lotId: String, startDate: Long, endDate: Long) {
        val feed = feedUseCases.readFeedsByLotIdAndDate(lotId, startDate, endDate)
        viewModelScope.launch {
            feed.dataFlow.collect { (thisFeedList, source) ->
                _uiState.update {
                    it.copy(
                        feedList = thisFeedList as List<FeedModel>,
                        feedDataSource = source,
                        feedIsFetchingFromCloud = feed.isFetchingFromCloud
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readRations() {
        val ration = rationUseCases.readAllRationsUC()
        viewModelScope.launch {
            ration.dataFlow.collect { (feedRationList, source) ->
                _uiState.update {
                    it.copy(
                        rationList = feedRationList as List<RationModel>,
                        rationDataSource = source,
                        rationIsFetchingFromCloud = ration.isFetchingFromCloud
                    )
                }
            }
        }
    }

    private fun createOrUpdateCallandFeeds(
            callModel: CallModel,
            feedModelList: List<FeedModel>,
            originalFeedModelList: List<FeedModel>
    ) {
        viewModelScope.launch {
            if (callModel.callCloudDatabaseId.isNullOrEmpty()) {
                // TODO: remove hard-coded isConnected boolean
                callUseCases.createCallUC(callModel, true)
            } else {
                callUseCases.updateCallUC(callModel)
            }
            feedUseCases.createAndUpdateFeedList(originalFeedModelList, feedModelList)
        }
    }

    private fun updateLotWithNewRation(
        lotModel: LotModel
    ) {
        viewModelScope.launch {
            lotUseCases.updateLotWithNewRation(lotModel)
        }
    }
}

data class FeedLotDetailFragmentUiState(
    val callModel: CallAndRationModel? = null,
    val callIsFetchingFromCloud: Boolean = false,
    val callDataSource: DataSource = DataSource.Local,
    val feedList: List<FeedModel> = emptyList(),
    val feedIsFetchingFromCloud: Boolean = false,
    val feedDataSource: DataSource = DataSource.Local,
    val rationList: List<RationModel> = emptyList(),
    val rationIsFetchingFromCloud: Boolean = false,
    val rationDataSource: DataSource = DataSource.Local
)

sealed class FeedLotDetailFragmentEvents {
    data class OnSave(
        val callModel: CallModel,
        val feedModelList: List<FeedModel>,
        val originalFeedModelList: List<FeedModel>
    ) : FeedLotDetailFragmentEvents()

    object OnRationsLoaded : FeedLotDetailFragmentEvents()

    data class OnUpdateRation(
        val lotModel: LotModel
    ) : FeedLotDetailFragmentEvents()
}