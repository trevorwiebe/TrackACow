package com.trevorwiebe.trackacow.presentation.fragment_move

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
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
        val pen = penUseCases.readPenAndLotModelIncludeEmptyPens()
        viewModelScope.launch {
            pen.dataFlow.collect { (thisPenAndLotList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = thisPenAndLotList as List<PenAndLotModel>,
                        isFetchingFromCloud = pen.isFetchingFromCloud,
                        dataSource = source
                    )
                }
            }
        }
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

sealed class MoveUiEvents {
    data class OnItemShuffled(val lotModel: LotModel) : MoveUiEvents()
    data class OnLotsMerged(val lotIdList: List<String>) : MoveUiEvents()
}

data class MoveUiState(
    val penAndLotList: List<PenAndLotModel> = emptyList(),
    val isFetchingFromCloud: Boolean = false,
    val dataSource: DataSource = DataSource.Local
)