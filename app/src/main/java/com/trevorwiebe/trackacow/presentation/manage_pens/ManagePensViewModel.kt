package com.trevorwiebe.trackacow.presentation.manage_pens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagePensViewModel @Inject constructor(
    private val penUseCases: PenUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(ManagePensUiState())
    val uiState: StateFlow<ManagePensUiState> = _uiState.asStateFlow()

    init {
        readPensAndLots()
    }

    fun onEvent(events: ManagePenEvents){
        when(events){
            is ManagePenEvents.OnPenSaved -> {
                createPen(events.penModel)
            }
            is ManagePenEvents.OnPenDeleted -> {
                deletePen(events.penModel)
            }
            is ManagePenEvents.OnPenUpdated -> {
                updatePen(events.penModel)
            }
        }
    }

    private fun createPen(penModel: PenModel){
        viewModelScope.launch {
            penUseCases.createPenUC(penModel)
        }
    }

    private fun deletePen(penModel: PenModel){
        viewModelScope.launch {
            penUseCases.deletePenUC(penModel)
        }
    }

    private fun updatePen(penModel: PenModel){
        viewModelScope.launch {
            penUseCases.updatePenUC(penModel)
        }
    }

    private fun readPensAndLots(){
        penUseCases.readPenAndLotModelIncludeEmptyPens()
            .map { thisPenList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penList = thisPenList
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

sealed class ManagePenEvents{
    data class OnPenSaved(val penModel: PenModel): ManagePenEvents()
    data class OnPenDeleted(val penModel: PenModel): ManagePenEvents()
    data class OnPenUpdated(val penModel: PenModel): ManagePenEvents()
}

data class ManagePensUiState(
    val penList: List<PenAndLotModel> = emptyList()
)