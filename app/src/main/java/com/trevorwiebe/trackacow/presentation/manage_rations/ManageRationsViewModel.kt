package com.trevorwiebe.trackacow.presentation.manage_rations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ManageRationsViewModel @Inject constructor(
    private val rationUseCases: RationUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(ManageRationsUiState())
    val uiState: StateFlow<ManageRationsUiState> = _uiState.asStateFlow()

    private var loadRationsJob: Job? = null

    init {
        loadRations()
    }

    private fun loadRations(){
        loadRationsJob?.cancel()
        loadRationsJob = rationUseCases.readAllRationsUC()
            .map { listFromDB ->
                _uiState.update { uiState ->
                    uiState.copy(rationsList = listFromDB)
                }
            }
            .launchIn(viewModelScope)
    }
}