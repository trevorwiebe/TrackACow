package com.trevorwiebe.trackacow.presentation.fragment_move

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
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
                updateLotWithNewPenId(event.lotModel)
            }

            is MoveUiEvents.OnLotsMerged -> {
                mergeLots(event.lotIdList)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readPensAndLots() {
        readPenAndLotsJob?.cancel()
        readPenAndLotsJob = penUseCases.readPenAndLotModelIncludeEmptyPens().dataFlow
            .map { (thisPenAndLotList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = thisPenAndLotList as List<PenAndLotModel>
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateLotWithNewPenId(lotModel: LotModel) {
        viewModelScope.launch {
            lotUseCases.updateLot(lotModel)
        }
    }

    private fun mergeLots(lotModelIdList: List<String>) {
        viewModelScope.launch {
            lotUseCases.mergeLots(lotModelIdList)
        }
    }
}

sealed class MoveUiEvents{
    data class OnItemShuffled(val lotModel: LotModel) : MoveUiEvents()
    data class OnLotsMerged(val lotIdList: List<String>) : MoveUiEvents()
}