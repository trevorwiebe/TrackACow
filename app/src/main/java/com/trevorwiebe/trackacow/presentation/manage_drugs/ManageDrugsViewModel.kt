package com.trevorwiebe.trackacow.presentation.manage_drugs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ManageDrugsViewModel @Inject constructor(
    private val drugUseCases: DrugUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(ManageDrugsUiState())
    val uiState: StateFlow<ManageDrugsUiState> = _uiState.asStateFlow()

    private var getDrugList: Job? = null

    init {
        getDrugList()
    }

    private fun getDrugList() {
        getDrugList?.cancel()
        getDrugList = drugUseCases.readDrugsUC()
            .map { thisDrugList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        drugList = thisDrugList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

}

data class ManageDrugsUiState(
    val drugList: List<DrugModel> = emptyList()
)