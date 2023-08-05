package com.trevorwiebe.trackacow.presentation.manage_drugs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageDrugsViewModel @Inject constructor(
    private val drugUseCases: DrugUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(ManageDrugsUiState())
    val uiState: StateFlow<ManageDrugsUiState> = _uiState.asStateFlow()

    init {
        getDrugList()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getDrugList() {

        val readDrugsUC = drugUseCases.readDrugsUC()

        viewModelScope.launch {
            readDrugsUC.dataFlow.collect { (drugList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        drugList = drugList as List<DrugModel>,
                        dataSource = source,
                        isFetchingFromCloud = readDrugsUC.isFetchingFromCloud
                    )
                }
            }
        }
    }

}

data class ManageDrugsUiState(
    val drugList: List<DrugModel> = emptyList(),
    val dataSource: DataSource = DataSource.Local,
    val isFetchingFromCloud: Boolean = false
)