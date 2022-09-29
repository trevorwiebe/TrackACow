package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class FeedPenListViewModel @AssistedInject constructor(
    private val callUseCases: CallUseCases,
    @Assisted lotId: String
): ViewModel() {

    @AssistedFactory
    interface FeedPenListViewModelFactory {
        fun create(lotId: String): FeedPenListViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object{
        fun providesFactory(
            assistedFactory: FeedPenListViewModelFactory,
            lotId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(lotId) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(FeedPenListUiState())
    val uiState: StateFlow<FeedPenListUiState> = _uiState.asStateFlow()

    private var readCallsJob: Job? = null

    init {
        readCallsByLotId(lotId)
    }

    private fun readCallsByLotId(lotId: String){
        readCallsJob?.cancel()
        readCallsJob = callUseCases.readCallsByLotId(lotId)
            .map { listFromDB ->
                _uiState.update { uiState ->
                    uiState.copy(callList = listFromDB)
                }
            }
            .launchIn(viewModelScope)
    }
}