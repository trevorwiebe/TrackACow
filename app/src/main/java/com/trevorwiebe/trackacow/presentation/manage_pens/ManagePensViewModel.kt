package com.trevorwiebe.trackacow.presentation.manage_pens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
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

    fun onEvent(events: ManagePenEvents) {
        when (events) {
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

    private fun updatePen(penModel: PenModel) {
        viewModelScope.launch {
            penUseCases.updatePenUC(penModel)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readPensAndLots() {
        val pens = penUseCases.readPenAndLotModelIncludeEmptyPens()
        viewModelScope.launch {
            pens.dataFlow.collect { (thisPenList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penList = thisPenList as List<PenAndLotModel>,
                        dataSource = source,
                        isFetchingFromCloud = pens.isFetchingFromCloud
                    )
                }
            }
        }
    }
}

sealed class ManagePenEvents{
    data class OnPenSaved(val penModel: PenModel): ManagePenEvents()
    data class OnPenDeleted(val penModel: PenModel): ManagePenEvents()
    data class OnPenUpdated(val penModel: PenModel): ManagePenEvents()
}

data class ManagePensUiState(
    val penList: List<PenAndLotModel> = emptyList(),
    val dataSource: DataSource = DataSource.Local,
    val isFetchingFromCloud: Boolean = false
)