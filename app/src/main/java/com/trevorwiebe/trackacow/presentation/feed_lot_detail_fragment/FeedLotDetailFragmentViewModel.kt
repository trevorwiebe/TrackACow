package com.trevorwiebe.trackacow.presentation.feed_lot_detail_fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDayStartAndDayEnd
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val pen_and_lot_param = "pen_and_lot_param"
private const val pen_ui_date_param = "pen_ui_date_param"

class FeedLotDetailFragmentViewModel @AssistedInject constructor(
    private val callUseCases: CallUseCases,
    private val feedUseCases: FeedUseCases,
    private val calculateDayStartAndDayEnd: CalculateDayStartAndDayEnd,
    @Assisted defaultArgs: Bundle? = null
) : ViewModel() {

    private var readCallByLotIdAndDateJob: Job? = null
    private var readFeedsByLotIdAndDateJob: Job? = null

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
        }
    }

    private fun readCallByLotIdAndDate(lotId: String, dateStart: Long, dateEnd: Long) {
        readCallByLotIdAndDateJob?.cancel()
        readCallByLotIdAndDateJob =
            callUseCases.readCallsByLotIdAndDateUC(lotId, dateStart, dateEnd)
                .map { receivedCallModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            callModel = receivedCallModel
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    private fun readFeedsByLotIdAndDate(lotId: String, startDate: Long, endDate: Long) {
        readFeedsByLotIdAndDateJob?.cancel()
        readFeedsByLotIdAndDateJob = feedUseCases.readFeedsByLotIdAndDate(lotId, startDate, endDate)
            .map { thisFeedList ->
                _uiState.update {
                    it.copy(
                        feedList = thisFeedList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun createOrUpdateCallandFeeds(
        callModel: CallModel,
        feedModelList: List<FeedModel>,
        originalFeedModelList: List<FeedModel>
    ) {
        Log.d("TAG", "createOrUpdateCallandFeeds: $callModel")
        Log.d("TAG", "createOrUpdateCallandFeeds: $feedModelList")
        Log.d("TAG", "createOrUpdateCallandFeeds: $originalFeedModelList")
        viewModelScope.launch {
            if (callModel.callCloudDatabaseId.isNullOrEmpty()) {
                callUseCases.createCallUC(callModel)
            } else {
                callUseCases.updateCallUC(callModel)
            }
//            feedUseCases.createAndUpdateFeedList(feedModelList)
        }
    }
}

data class FeedLotDetailFragmentUiState(
    val callModel: CallAndRationModel? = null,
    val feedList: List<FeedModel> = emptyList()
)

sealed class FeedLotDetailFragmentEvents {
    data class OnSave(
        val callModel: CallModel,
        val feedModelList: List<FeedModel>,
        val originalFeedModelList: List<FeedModel>
    ) : FeedLotDetailFragmentEvents()
}