package com.trevorwiebe.trackacow.presentation.feed_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.FeedAndRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

class FeedReportsViewModel @AssistedInject constructor(
    private val feedUseCases: FeedUseCases,
    private val rationUseCases: RationUseCases,
    @Assisted("lotId") private val lotId: String
) : ViewModel() {

    @AssistedFactory
    interface FeedReportsViewModelFactory {
        fun create(
            @Assisted("lotId") lotId: String
        ): FeedReportsViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: FeedReportsViewModelFactory,
            lotId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(lotId) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(FeedReportsUiState())
    val uiState: StateFlow<FeedReportsUiState> = _uiState.asStateFlow()

    init {
        val monthInMillis = TimeUnit.DAYS.toMillis(30)
        val currentTime = System.currentTimeMillis()
        val monthAgo = currentTime - monthInMillis

        getRationAndFeedListByDateAndLotId(lotId, monthAgo, currentTime)

        getRations()
    }

    fun onEvent(event: FeedReportsUiEvent) {
        when (event) {
            is FeedReportsUiEvent.OnDateSelected -> {
                getRationAndFeedListByDateAndLotId(event.lotId, event.startDate, event.endDate)
            }
        }
    }

    // TODO: add progress bar
    @Suppress("UNCHECKED_CAST")
    private fun getRationAndFeedListByDateAndLotId(lotId: String, startDate: Long, endDate: Long) {
        feedUseCases.readFeedsAndRationsTotalsByLotIdAndDate(lotId, startDate, endDate).dataFlow
                .map { (thisFeedList, source) ->
                    _uiState.update { uiState ->
                        uiState.copy(
                                feedList = thisFeedList as List<FeedAndRationModel>
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    // TODO: add progress bar
    @Suppress("UNCHECKED_CAST")
    private fun getRations() {
        rationUseCases.readAllRationsUC().dataFlow
            .map { (thisRationList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        rationList = thisRationList as List<RationModel>
                    )
                }
            }
            .launchIn(viewModelScope)
    }

}

sealed class FeedReportsUiEvent {
    data class OnDateSelected(val lotId: String, val startDate: Long, val endDate: Long) :
        FeedReportsUiEvent()
}

data class FeedReportsUiState(
        val feedList: List<FeedAndRationModel> = emptyList(),
        val rationList: List<RationModel> = emptyList()
)