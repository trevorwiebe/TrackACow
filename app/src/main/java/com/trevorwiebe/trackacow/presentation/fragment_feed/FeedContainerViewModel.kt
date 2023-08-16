package com.trevorwiebe.trackacow.presentation.fragment_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    // Decided not to put progress bar here
    @Suppress("UNCHECKED_CAST")
    private fun getPenAndLotModels() {
        val pen = penUseCases.readPenAndLotModelIncludeEmptyPens()
        viewModelScope.launch {
            pen.dataFlow.collect { (penAndLotList, _) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = penAndLotList as List<PenAndLotModel>
                    )
                }
            }
        }
    }
}

data class FeedContainerUiState(
    val penAndLotList: List<PenAndLotModel> = emptyList()
)