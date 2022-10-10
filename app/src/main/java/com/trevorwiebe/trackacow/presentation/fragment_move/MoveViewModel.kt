package com.trevorwiebe.trackacow.presentation.fragment_move

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        when(event){
            is MoveUiEvents.OnItemShuffled -> {
                updateLotWithNewPenId(event.lotModel)
            }
            is MoveUiEvents.OnDragStart -> {
                readPenAndLotsJob?.cancel()
                Log.d("TAG", "onEvent: here1")
            }
            is MoveUiEvents.OnDragStop -> {
                readPensAndLots()
                Log.d("TAG", "onEvent: here2")
            }
        }
    }

    private fun readPensAndLots(){
        readPenAndLotsJob?.cancel()
        readPenAndLotsJob = penUseCases.readPenAndLotModelUC()
            .cancellable()
            .map { thisPenAndLotList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = thisPenAndLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateLotWithNewPenId(lotModel: LotModel){
        viewModelScope.launch{
            lotUseCases.updateLotWithNewPenIdUC(lotModel)
        }
    }
}

sealed class MoveUiEvents{
    data class OnItemShuffled(val lotModel: LotModel): MoveUiEvents()
    object OnDragStart: MoveUiEvents()
    object OnDragStop: MoveUiEvents()
}