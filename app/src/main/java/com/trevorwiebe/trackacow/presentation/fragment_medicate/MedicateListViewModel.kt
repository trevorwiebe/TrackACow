package com.trevorwiebe.trackacow.presentation.fragment_medicate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MedicateListViewModel @Inject constructor(
    private val penUseCases: PenUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(MedicateListUiState())
    val uiState: StateFlow<MedicateListUiState> = _uiState.asStateFlow()

    init {
        getPensAndLots()
    }

    private fun getPensAndLots(){
        penUseCases.readPenAndLotModelUC()
            .map { thisPenAndLotList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotModelList = thisPenAndLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

}

data class MedicateListUiState(
    val penAndLotModelList: List<PenAndLotModel> = emptyList()
)