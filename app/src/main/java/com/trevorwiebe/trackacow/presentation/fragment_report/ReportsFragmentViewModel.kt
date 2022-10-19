package com.trevorwiebe.trackacow.presentation.fragment_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ReportsFragmentViewModel @Inject constructor(
    private val lotUseCases: LotUseCases
): ViewModel() {

    private var getLotsJob: Job? = null

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        getLots()
    }

    private fun getLots(){
        getLotsJob?.cancel()
        getLotsJob = lotUseCases.readLots()
            .map { thisLotList ->
                _uiState.update {
                    it.copy(
                        lotList = thisLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

}