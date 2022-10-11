package com.trevorwiebe.trackacow.presentation.fragment_medicate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var getPenAndLotJobs: Job? = null

    private fun getPensAndLots(){
        _uiState.update { it.copy(isLoading = true) }
        getPenAndLotJobs?.cancel()
        getPenAndLotJobs = penUseCases.readPenAndLotModelUC()
            .map { thisPenAndLotList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotModelList = thisPenAndLotList,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

}

data class MedicateListUiState(
    val penAndLotModelList: List<PenAndLotModel> = emptyList(),
    val isLoading: Boolean = false
)