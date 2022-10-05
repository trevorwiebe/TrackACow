package com.trevorwiebe.trackacow.presentation.fragment_feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FeedContainerViewModel @Inject constructor(
    private val penUseCases: PenUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(FeedContainerUiState())
    val uiState: StateFlow<FeedContainerUiState> = _uiState.asStateFlow()

    init {
        getPenAndLotModels()
    }

    private fun getPenAndLotModels(){
        penUseCases.readPenAndLotModelUC()
            .map { penAndLotList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = penAndLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}