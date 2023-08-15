package com.trevorwiebe.trackacow.presentation.manage_rations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageRationsViewModel @Inject constructor(
    private val rationUseCases: RationUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(ManageRationsUiState())
    val uiState: StateFlow<ManageRationsUiState> = _uiState.asStateFlow()

    init {
        loadRations()
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadRations() {
        val ration = rationUseCases.readAllRationsUC()
        viewModelScope.launch {
            ration.dataFlow.collect { (listFromDB, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        rationsList = listFromDB as List<RationModel>,
                        dataSource = source,
                        isFetchingFromCloud = ration.isFetchingFromCloud
                    )
                }
            }
        }
    }
}

data class ManageRationsUiState(
    val rationsList: List<RationModel> = emptyList(),
    val isFetchingFromCloud: Boolean = false,
    val dataSource: DataSource = DataSource.Local
)