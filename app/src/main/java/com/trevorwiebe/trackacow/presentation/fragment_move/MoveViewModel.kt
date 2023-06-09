package com.trevorwiebe.trackacow.presentation.fragment_move

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoveViewModel @Inject constructor(
    private val penUseCases: PenUseCases,
    private val lotUseCases: LotUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(MoveUiState())
    val uiState: StateFlow<MoveUiState> = _uiState.asStateFlow()

    var readPenAndLotsJob: Job? = null

    init {
        readPensAndLots()
    }

    fun onEvent(event: MoveUiEvents){
        when (event) {
            is MoveUiEvents.OnItemShuffled -> {
                updateLotWithNewPenId(event.lotId, event.penId)
            }

            is MoveUiEvents.OnLotsMerged -> {
                mergeLots(event.lotIdList)
            }
        }
    }

    private fun readPensAndLots(){
        readPenAndLotsJob?.cancel()
        readPenAndLotsJob = penUseCases.readPenAndLotModelIncludeEmptyPens()
            .map { thisPenAndLotList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = thisPenAndLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateLotWithNewPenId(lotId: String, penId: String) {
        viewModelScope.launch {
            lotUseCases.updateLotWithNewPenIdUC(lotId, penId)
        }
    }

    private fun mergeLots(lotModelIdList: List<String>) {
        viewModelScope.launch {
            lotUseCases.mergeLots(lotModelIdList)
        }
    }
}

sealed class MoveUiEvents{
    data class OnItemShuffled(val lotId: String, val penId: String) : MoveUiEvents()
    data class OnLotsMerged(val lotIdList: List<String>) : MoveUiEvents()
}