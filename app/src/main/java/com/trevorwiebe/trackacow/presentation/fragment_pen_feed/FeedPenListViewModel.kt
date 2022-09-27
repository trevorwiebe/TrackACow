package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FeedPenListViewModel @Inject constructor(
    private val callUseCases: CallUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(FeedPenListUiState())
    val uiState: StateFlow<FeedPenListUiState> = _uiState.asStateFlow()

    private var readCallsJob: Job? = null

    init {
        readCallsByLotId("-MsDcfplki27AZwFlBGl")
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